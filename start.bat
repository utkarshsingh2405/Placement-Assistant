@echo off
REM ==============================================================================
REM Unified Single-Console Launcher: Placement Assistance Platform
REM Starts Portal Gateway which embeds & supervises:
REM   1. Portal Gateway & SSO (Port 8080 or 8088)
REM   2. Institute Pillar (Port 8081, Context /institute)
REM   3. Placement Pillar (Port 8082, Context /placement)
REM All 3 run in this SINGLE console window.
REM ==============================================================================

cd /d "%~dp0"
setlocal EnableDelayedExpansion

echo ==============================================================================
echo          PLACEMENT ASSISTANCE WEB PLATFORM - UNIFIED LAUNCHER
echo ==============================================================================
echo.

REM 1. Smart Java Detection
set "JAVA_CMD="
where java >nul 2>&1
if !ERRORLEVEL! equ 0 (
    set "JAVA_CMD=java"
)

if "%JAVA_CMD%"=="" if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
        set "PATH=%JAVA_HOME%\bin;!PATH!"
    )
)

if "%JAVA_CMD%"=="" (
    for /d %%D in ("C:\Program Files\Java\jdk*") do (
        if exist "%%D\bin\java.exe" (
            set "JAVA_CMD=%%D\bin\java.exe"
            set "PATH=%%D\bin;!PATH!"
        )
    )
)

if "%JAVA_CMD%"=="" (
    for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk*") do (
        if exist "%%D\bin\java.exe" (
            set "JAVA_CMD=%%D\bin\java.exe"
            set "PATH=%%D\bin;!PATH!"
        )
    )
)

if "%JAVA_CMD%"=="" (
    echo [ERROR] Java 17 or higher was not detected on your system.
    echo Please install Java 17 or higher from https://adoptium.net/ and ensure it is on PATH.
    echo.
    pause
    exit /b 1
)

echo [OK] Java runtime detected: !JAVA_CMD!

REM 2. Check if Pre-packaged JARs Exist
set "PORTAL_JAR=%~dp0backend\portal\target\portal-1.0.0-SNAPSHOT.jar"
set "INSTITUTE_JAR=%~dp0backend\institute\target\institute-1.0.0-SNAPSHOT.jar"
set "PLACEMENT_JAR=%~dp0backend\placement\target\placement-1.0.0-SNAPSHOT.jar"

if not exist "%PORTAL_JAR%" goto NEED_BUILD
if not exist "%INSTITUTE_JAR%" goto NEED_BUILD
if not exist "%PLACEMENT_JAR%" goto NEED_BUILD

echo [OK] Pre-packaged executable JARs found for all pillars.
goto CHECK_PORTS

:NEED_BUILD
echo.
echo [INFO] Application JARs not found. First-time setup detected.
echo [INFO] Building modules with Maven Wrapper...
echo.

set "MVN_RUN="
if exist "%~dp0mvnw.cmd" (
    set "MVN_RUN=%~dp0mvnw.cmd"
) else (
    where mvn >nul 2>&1
    if !ERRORLEVEL! equ 0 (
        set "MVN_RUN=mvn"
    )
)

if "%MVN_RUN%"=="" (
    echo [ERROR] Maven or Maven Wrapper (mvnw.cmd) was not found.
    echo Please ensure mvnw.cmd is present in the repository root.
    echo.
    pause
    exit /b 1
)

call "%MVN_RUN%" clean package -DskipTests
if !ERRORLEVEL! neq 0 (
    echo.
    echo [ERROR] Maven build failed. Please check the compiler errors above.
    echo.
    pause
    exit /b 1
)
echo [OK] Build completed successfully.

:CHECK_PORTS
REM 3. Ensure storage & database directories exist
if not exist "%~dp0database" mkdir "%~dp0database"
if not exist "%~dp0data\portal\uploads" mkdir "%~dp0data\portal\uploads"
if not exist "%~dp0data\institute\uploads\resumes" mkdir "%~dp0data\institute\uploads\resumes"
if not exist "%~dp0data\placement\uploads\jd" mkdir "%~dp0data\placement\uploads\jd"
if not exist "%~dp0data\placement\uploads\results" mkdir "%~dp0data\placement\uploads\results"
if not exist "%~dp0data\placement\uploads\offers" mkdir "%~dp0data\placement\uploads\offers"
if not exist "%~dp0data\placement\uploads\logos" mkdir "%~dp0data\placement\uploads\logos"
if not exist "%~dp0uploads\resumes" mkdir "%~dp0uploads\resumes"

REM 4. Clean up stale zombie processes on ports 8080, 8081, 8082, 8088
powershell -NoProfile -Command "$ports = @(8081, 8082, 8088); foreach ($p in $ports) { $conns = Get-NetTCPConnection -LocalPort $p -ErrorAction SilentlyContinue; if ($conns) { $pids = $conns | Select-Object -ExpandProperty OwningProcess -Unique; foreach ($id in $pids) { if ($id -gt 0) { Stop-Process -Id $id -Force -ErrorAction SilentlyContinue; } } } }; $c80 = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue; if ($c80) { foreach ($id in ($c80 | Select-Object -ExpandProperty OwningProcess -Unique)) { $p = Get-Process -Id $id -ErrorAction SilentlyContinue; if ($p -and $p.ProcessName -eq 'java') { Stop-Process -Id $id -Force -ErrorAction SilentlyContinue; } } }" >nul 2>&1

REM 5. Detect Port 8080 availability
set "PORTAL_PORT=8080"
powershell -NoProfile -Command "if (Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue) { exit 1 } else { exit 0 }" >nul 2>&1
if !ERRORLEVEL! neq 0 (
    echo [NOTE] Port 8080 is occupied by a system service.
    echo [NOTE] Assigning Portal Gateway to Port 8088.
    set "PORTAL_PORT=8088"
)

REM 6. Database Engine Detection (MySQL vs SQLite fallback)
set "DB_PROFILE=dev"
powershell -NoProfile -Command "try { $client = New-Object System.Net.Sockets.TcpClient('localhost', 3306); $client.Close(); exit 0 } catch { exit 1 }" >nul 2>&1
if !ERRORLEVEL! equ 0 (
    echo [OK] MySQL server detected on port 3306. Using MySQL database engine.
    set "DB_PROFILE=dev"
) else (
    echo [NOTE] Local MySQL instance not detected on port 3306.
    echo [NOTE] Using zero-config local SQLite database engine [pre-seeded].
    set "DB_PROFILE=sqlite"
)

set "DB_INSTITUTE=%~dp0database\institute.db"
set "DB_INSTITUTE=!DB_INSTITUTE:\=/!"
set "DB_PLACEMENT=%~dp0database\placement.db"
set "DB_PLACEMENT=!DB_PLACEMENT:\=/!"
set "DB_PORTAL=%~dp0database\portal.db"
set "DB_PORTAL=!DB_PORTAL:\=/!"

echo.
echo Launching unified platform in this console:
echo   - Unified Portal Entry : http://localhost:!PORTAL_PORT!
echo   - Choose Domain Hub    : http://localhost:!PORTAL_PORT!/choose-domain.html
echo   - Institute Pillar     : http://localhost:!PORTAL_PORT!/institute/dashboard.html
echo   - Placement Pillar     : http://localhost:!PORTAL_PORT!/placement/dashboard.html
echo   - Database Mode        : !DB_PROFILE!
echo.
echo [Press Ctrl+C at any time to gracefully stop all pillars]
echo.

REM 7. Run Portal Gateway (PillarSupervisor embeds & launches Institute and Placement, then auto-opens browser)
"%JAVA_CMD%" ^
    -Dserver.port=!PORTAL_PORT! ^
    -Dspring.profiles.active=!DB_PROFILE! ^
    -DDB_PATH="!DB_PORTAL!" ^
    -DDB_INSTITUTE_PATH="!DB_INSTITUTE!" ^
    -DDB_PLACEMENT_PATH="!DB_PLACEMENT!" ^
    -Dpillar.institute.jar="%INSTITUTE_JAR%" ^
    -Dpillar.placement.jar="%PLACEMENT_JAR%" ^
    --enable-native-access=ALL-UNNAMED ^
    -XX:+EnableDynamicAgentLoading ^
    -jar "%PORTAL_JAR%"

if !ERRORLEVEL! neq 0 (
    echo.
    echo [ERROR] Portal Gateway exited with code !ERRORLEVEL!.
    echo.
    pause
)

echo.
echo ==============================================================================
echo [INFO] Placement Assistance Platform has stopped.
echo ==============================================================================
pause
endlocal
