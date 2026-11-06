package com.placementassist.portal.supervisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * PillarSupervisor: Embeds and orchestrates Institute Pillar (8081) and Placement Pillar (8082)
 * directly inside the Portal Gateway lifecycle.
 *
 * When Portal Gateway starts up:
 * 1. Checks if Institute and Placement are already running.
 * 2. If not, automatically launches their executable JARs as background child processes.
 * 3. Streams their logs into Portal's console with [INSTITUTE] and [PLACEMENT] prefixes.
 * 4. Ensures all uploads and database folders exist.
 * 5. On Portal shutdown (or Ctrl+C / SIGTERM), gracefully terminates child processes.
 */
@Component
public class PillarSupervisor implements ApplicationListener<ApplicationReadyEvent>, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(PillarSupervisor.class);

    @Value("${pillar.supervisor.enabled:true}")
    private boolean enabled;

    @Value("${pillar.institute.url:http://localhost:8081}")
    private String instituteUrl;

    @Value("${pillar.placement.url:http://localhost:8082}")
    private String placementUrl;

    @Value("${pillar.institute.jar:}")
    private String instituteJarProp;

    @Value("${pillar.placement.jar:}")
    private String placementJarProp;

    @Value("${spring.profiles.active:sqlite}")
    private String activeProfile;

    @Value("${DB_INSTITUTE_PATH:}")
    private String dbInstitutePathProp;

    @Value("${DB_PLACEMENT_PATH:}")
    private String dbPlacementPathProp;

    private final Environment environment;

    private Process instituteProcess;
    private Process placementProcess;
    private volatile boolean isShuttingDown = false;

    public PillarSupervisor(Environment environment) {
        this.environment = environment;
        // Register JVM shutdown hook to guarantee child processes terminate cleanly
        Runtime.getRuntime().addShutdownHook(new Thread(this::destroy, "PillarSupervisor-ShutdownHook"));
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!enabled) {
            log.info("[PillarSupervisor] Automatic pillar supervision is disabled (pillar.supervisor.enabled=false).");
            return;
        }

        CompletableFuture.runAsync(this::supervisePillars);
    }

    private void supervisePillars() {
        try {
            ensureDirectoriesExist();

            File rootDir = findProjectRoot();
            log.info("[PillarSupervisor] Project root directory identified: {}", rootDir.getAbsolutePath());

            // 1. Supervise Institute Pillar
            int institutePort = extractPort(instituteUrl, 8081);
            if (isPillarResponding(instituteUrl, institutePort)) {
                log.info("[PillarSupervisor] Institute Pillar is already active on port {}.", institutePort);
            } else {
                File instituteJar = resolveJar(instituteJarProp, "institute", rootDir);
                if (instituteJar != null && instituteJar.exists()) {
                    String dbPath = resolveDatabasePath(dbInstitutePathProp, "institute.db", rootDir);
                    log.info("[PillarSupervisor] Launching Institute Pillar (Port {}, JAR: {})...", institutePort, instituteJar.getName());
                    instituteProcess = startPillarProcess("INSTITUTE", instituteJar, institutePort, dbPath, rootDir);
                } else {
                    log.warn("[PillarSupervisor] Could not locate Institute JAR file. If running in dev mode, build with 'mvn package -DskipTests'.");
                }
            }

            // 2. Supervise Placement Pillar
            int placementPort = extractPort(placementUrl, 8082);
            if (isPillarResponding(placementUrl, placementPort)) {
                log.info("[PillarSupervisor] Placement Pillar is already active on port {}.", placementPort);
            } else {
                File placementJar = resolveJar(placementJarProp, "placement", rootDir);
                if (placementJar != null && placementJar.exists()) {
                    String dbPath = resolveDatabasePath(dbPlacementPathProp, "placement.db", rootDir);
                    log.info("[PillarSupervisor] Launching Placement Pillar (Port {}, JAR: {})...", placementPort, placementJar.getName());
                    placementProcess = startPillarProcess("PLACEMENT", placementJar, placementPort, dbPath, rootDir);
                } else {
                    log.warn("[PillarSupervisor] Could not locate Placement JAR file. If running in dev mode, build with 'mvn package -DskipTests'.");
                }
            }

            // 3. Monitor pillar readiness
            waitForPillarsReady(institutePort, placementPort);

        } catch (Exception e) {
            log.error("[PillarSupervisor] Error while initializing pillars: {}", e.getMessage(), e);
        }
    }

    private void waitForPillarsReady(int instPort, int placePort) {
        int maxAttempts = 30;
        int attempt = 0;
        boolean instReady = false;
        boolean placeReady = false;

        while (attempt++ < maxAttempts && (!instReady || !placeReady) && !isShuttingDown) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                return;
            }

            if (!instReady) {
                instReady = isPillarResponding(instituteUrl, instPort);
            }
            if (!placeReady) {
                placeReady = isPillarResponding(placementUrl, placePort);
            }
        }

        String serverPort = environment.getProperty("server.port", "8080");
        String launchUrl = "http://localhost:" + serverPort;

        if (instReady && placeReady) {
            System.out.println();
            System.out.println("==============================================================================");
            System.out.println("   >>> ALL THREE PILLARS ARE ONLINE AND INTEGRATED IN PORTAL GATEWAY <<<");
            System.out.println("==============================================================================");
            System.out.println("  * Portal Gateway & Entrypoint : " + launchUrl);
            System.out.println("  * Choose Domain Hub           : " + launchUrl + "/choose-domain.html");
            System.out.println("  * Institute Pillar Dashboard  : " + launchUrl + "/institute/dashboard.html");
            System.out.println("  * Placement Pillar Dashboard  : " + launchUrl + "/placement/dashboard.html");
            System.out.println("==============================================================================");
            System.out.println();
            launchBrowser(launchUrl);
        } else if (!isShuttingDown) {
            log.warn("[PillarSupervisor] Child pillars took longer than expected (Institute: {}, Placement: {}). Opening portal gateway...",
                    instReady ? "ONLINE" : "PENDING", placeReady ? "ONLINE" : "PENDING");
            launchBrowser(launchUrl);
        }
    }

    private void launchBrowser(String url) {
        String railwayEnv = System.getenv("RAILWAY_ENVIRONMENT");
        String ci = System.getenv("CI");
        String headless = System.getProperty("java.awt.headless");
        String noBrowser = System.getenv("DISABLE_BROWSER_AUTO_LAUNCH");
        if (railwayEnv != null || ci != null || "true".equalsIgnoreCase(noBrowser) || "true".equalsIgnoreCase(headless)) {
            log.info("[PillarSupervisor] Headless / Cloud / CI environment detected; skipping browser auto-launch.");
            return;
        }

        try {
            String os = System.getProperty("os.name", "").toLowerCase();
            log.info("[PillarSupervisor] Automatically opening default browser to {}", url);
            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "start", "", url).start();
            } else if (os.contains("mac")) {
                new ProcessBuilder("open", url).start();
            } else {
                new ProcessBuilder("xdg-open", url).start();
            }
        } catch (Exception ex) {
            log.warn("[PillarSupervisor] Could not auto-launch browser: {}", ex.getMessage());
        }
    }

    private Process startPillarProcess(String name, File jar, int port, String dbPath, File workingDir) throws IOException {
        String javaBin = resolveJavaBinary();
        List<String> command = new ArrayList<>();
        command.add(javaBin);
        command.add("-Dserver.port=" + port);
        command.add("-Dspring.profiles.active=" + activeProfile);

        if ("sqlite".equalsIgnoreCase(activeProfile)) {
            command.add("-DDB_PATH=" + dbPath);
            command.add("-Dspring.datasource.url=jdbc:sqlite:" + dbPath);
        }

        // JVM options for JDK 17-25 modern class loading & reflection compatibility
        command.add("--enable-native-access=ALL-UNNAMED");
        command.add("-XX:+EnableDynamicAgentLoading");

        // Execute JAR
        command.add("-jar");
        command.add(jar.getAbsolutePath());

        ProcessBuilder pb = new ProcessBuilder(command);
        if (workingDir != null && workingDir.exists()) {
            pb.directory(workingDir);
        }

        // Inherit environment variables (e.g. JWT_SECRET, PORTAL_SSO_ENABLED, etc.)
        pb.environment().putAll(System.getenv());
        pb.environment().put("SERVER_PORT", String.valueOf(port));
        pb.environment().put("SPRING_PROFILES_ACTIVE", activeProfile);

        Process process = pb.start();

        // Stream logs live into Portal's standard output and error
        streamProcessOutput(process.getInputStream(), "[" + name + "]");
        streamProcessOutput(process.getErrorStream(), "[" + name + "-ERR]");

        // Watch for unexpected exit
        process.onExit().thenAccept(p -> {
            if (!isShuttingDown) {
                log.warn("[PillarSupervisor] {} process exited unexpectedly with exit code {}", name, p.exitValue());
            }
        });

        return process;
    }

    private void streamProcessOutput(InputStream inputStream, String prefix) {
        Thread t = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(prefix + " " + line);
                }
            } catch (IOException ignored) {
            }
        }, prefix + "-LogThread");
        t.setDaemon(true);
        t.start();
    }

    private boolean isPillarResponding(String urlStr, int fallbackPort) {
        try {
            URI uri = URI.create(urlStr);
            String host = (uri.getHost() != null && !uri.getHost().isBlank()) ? uri.getHost() : "localhost";
            int port = uri.getPort() > 0 ? uri.getPort() : fallbackPort;

            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), 600);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private int extractPort(String urlStr, int defaultPort) {
        try {
            URI uri = URI.create(urlStr);
            return uri.getPort() > 0 ? uri.getPort() : defaultPort;
        } catch (Exception e) {
            return defaultPort;
        }
    }

    private File resolveJar(String configuredProp, String pillarName, File rootDir) {
        if (configuredProp != null && !configuredProp.isBlank()) {
            File f = new File(configuredProp);
            if (f.exists() && f.isFile()) return f;
        }

        String envVar = System.getenv("PILLAR_" + pillarName.toUpperCase() + "_JAR");
        if (envVar != null && !envVar.isBlank()) {
            File f = new File(envVar);
            if (f.exists() && f.isFile()) return f;
        }

        List<File> candidates = new ArrayList<>();
        // 1. Check relative to rootDir
        candidates.add(new File(rootDir, "backend/" + pillarName + "/target/" + pillarName + "-1.0.0-SNAPSHOT.jar"));
        candidates.add(new File(rootDir, pillarName + "/target/" + pillarName + "-1.0.0-SNAPSHOT.jar"));
        // 2. Check current working directory
        candidates.add(new File("backend/" + pillarName + "/target/" + pillarName + "-1.0.0-SNAPSHOT.jar"));
        candidates.add(new File(pillarName + "/target/" + pillarName + "-1.0.0-SNAPSHOT.jar"));
        candidates.add(new File("../" + pillarName + "/target/" + pillarName + "-1.0.0-SNAPSHOT.jar"));
        // 3. Check Docker / Railway standard container directory
        candidates.add(new File("/app/" + pillarName + ".jar"));
        candidates.add(new File("/app/" + pillarName + "-1.0.0-SNAPSHOT.jar"));
        candidates.add(new File(pillarName + ".jar"));

        // 4. Check relative to current running JAR location
        try {
            File codeSource = new File(PillarSupervisor.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File parent = codeSource.isDirectory() ? codeSource : codeSource.getParentFile();
            if (parent != null) {
                candidates.add(new File(parent, pillarName + ".jar"));
                candidates.add(new File(parent, pillarName + "-1.0.0-SNAPSHOT.jar"));
                File backendParent = parent.getParentFile();
                if (backendParent != null) {
                    candidates.add(new File(backendParent, pillarName + "/target/" + pillarName + "-1.0.0-SNAPSHOT.jar"));
                }
            }
        } catch (Exception ignored) {
        }

        for (File candidate : candidates) {
            if (candidate != null && candidate.exists() && candidate.isFile()) {
                return candidate;
            }
        }

        return null;
    }

    private String resolveDatabasePath(String configuredPath, String dbFilename, File rootDir) {
        if (configuredPath != null && !configuredPath.isBlank()) {
            return new File(configuredPath).getAbsolutePath().replace('\\', '/');
        }

        List<File> candidates = List.of(
                new File(rootDir, "database/" + dbFilename),
                new File(rootDir, "backend/database/" + dbFilename),
                new File("database/" + dbFilename),
                new File("../database/" + dbFilename),
                new File("backend/database/" + dbFilename),
                new File("/app/database/" + dbFilename)
        );

        for (File f : candidates) {
            if (f.exists() && f.isFile()) {
                return f.getAbsolutePath().replace('\\', '/');
            }
        }

        // Fallback default
        File defaultFile = new File(rootDir, "database/" + dbFilename);
        return defaultFile.getAbsolutePath().replace('\\', '/');
    }

    private File findProjectRoot() {
        File current = new File(".").getAbsoluteFile();
        while (current != null) {
            if (new File(current, "database").isDirectory() &&
                    (new File(current, "backend").isDirectory() || new File(current, "portal").isDirectory())) {
                return current;
            }
            current = current.getParentFile();
        }
        return new File(".").getAbsoluteFile();
    }

    private void ensureDirectoriesExist() {
        try {
            File root = findProjectRoot();
            List<String> dirs = List.of(
                    "database",
                    "data/portal/uploads",
                    "data/institute/uploads/resumes",
                    "data/placement/uploads/jd",
                    "data/placement/uploads/results",
                    "data/placement/uploads/offers",
                    "data/placement/uploads/logos",
                    "uploads/resumes"
            );
            for (String d : dirs) {
                Path p = Paths.get(root.getAbsolutePath(), d);
                if (!Files.exists(p)) {
                    Files.createDirectories(p);
                }
            }
        } catch (Exception e) {
            log.warn("[PillarSupervisor] Could not pre-create directory structure: {}", e.getMessage());
        }
    }

    private String resolveJavaBinary() {
        try {
            Optional<String> cmd = ProcessHandle.current().info().command();
            if (cmd.isPresent() && new File(cmd.get()).exists()) {
                return cmd.get();
            }
        } catch (Exception ignored) {
        }

        String javaHome = System.getProperty("java.home");
        if (javaHome != null && !javaHome.isBlank()) {
            boolean isWin = System.getProperty("os.name", "").toLowerCase().contains("win");
            File exe = new File(javaHome, "bin/java" + (isWin ? ".exe" : ""));
            if (exe.exists()) {
                return exe.getAbsolutePath();
            }
        }
        return "java";
    }

    @Override
    public void destroy() {
        if (isShuttingDown) return;
        isShuttingDown = true;

        log.info("[PillarSupervisor] Shutting down managed pillars...");
        terminateProcess(instituteProcess, "Institute");
        terminateProcess(placementProcess, "Placement");
    }

    private void terminateProcess(Process process, String name) {
        if (process != null && process.isAlive()) {
            try {
                log.info("[PillarSupervisor] Stopping {} Pillar process...", name);
                process.destroy();
                if (!process.waitFor(4, TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                }
                log.info("[PillarSupervisor] {} Pillar process terminated.", name);
            } catch (Exception e) {
                try {
                    process.destroyForcibly();
                } catch (Exception ignored) {
                }
            }
        }
    }
}

