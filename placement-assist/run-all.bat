@echo off
REM ==============================================================================
REM Unified Launcher: Placement Assistance Platform (All 3 Pillars)
REM Starts:
REM   1. Portal Gateway & SSO (Port 8080 or 8088 if 8080 is occupied)
REM   2. Institute Pillar (Port 8081, Context /institute)
REM   3. Placement Pillar (Port 8082, Context /placement)
REM ==============================================================================

cd /d "%~dp0"
setlocal EnableDelayedExpansion

echo ==============================================================================
echo          PLACEMENT ASSISTANCE WEB APPLICATION - UNIFIED LAUNCHER
echo ==============================================================================
echo.

REM 1. Check for Java
where java >nul 2>&1
if %ERRORLEVEL% neq 0 (
    if exist "C:\Program Files\Java\jdk-25.0.4\bin\java.exe" (
        set "PATH=C:\Program Files\Java\jdk-25.0.4\bin;!PATH!"
    ) else if exist "C:\Program Files\Java\jdk-17\bin\java.exe" (
        set "PATH=C:\Program Files\Java\jdk-17\bin;!PATH!"
    ) else (
        echo [ERROR] Java was not found on your system PATH.
        echo Please ensure Java 17 or higher is installed.
        echo.
        pause
        exit /b 1
    )
)

echo [OK] Java environment detected.

REM 2. Check if pre-packaged JARs exist; if not, check Maven to compile
set "PORTAL_JAR=portal\target\portal-1.0.0-SNAPSHOT.jar"
set "INSTITUTE_JAR=institute\target\institute-1.0.0-SNAPSHOT.jar"
set "PLACEMENT_JAR=placement\target\placement-1.0.0-SNAPSHOT.jar"

if not exist "%PORTAL_JAR%" goto NEED_BUILD
if not exist "%INSTITUTE_JAR%" goto NEED_BUILD
if not exist "%PLACEMENT_JAR%" goto NEED_BUILD

echo [OK] Pre-packaged executable JARs found for all pillars.
goto CHECK_PORTS

:NEED_BUILD
echo [INFO] Application JARs not found. Searching for Maven to build...
set "MVN_CMD="
where mvn >nul 2>&1
if %ERRORLEVEL% equ 0 (
    set "MVN_CMD=mvn"
) else if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.3\plugins\maven-plugin\lib\maven3\bin\mvn.cmd" (
    set "MVN_CMD=C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.3\plugins\maven-plugin\lib\maven3\bin\mvn.cmd"
) else if exist "%USERPROFILE%\.vscode\extensions\oracle.oracle-java-27.0.0\nbcode\java\maven\bin\mvn.cmd" (
    set "MVN_CMD=%USERPROFILE%\.vscode\extensions\oracle.oracle-java-27.0.0\nbcode\java\maven\bin\mvn.cmd"
) else if exist "%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.12\59fe215c0ad6947fea90184bf7add084544567b927287592651fda3782e0e798\bin\mvn.cmd" (
    set "MVN_CMD=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.12\59fe215c0ad6947fea90184bf7add084544567b927287592651fda3782e0e798\bin\mvn.cmd"
)

if "%MVN_CMD%"=="" (
    echo [ERROR] Maven was not found on PATH or in standard paths.
    echo Please install Maven or add it to your PATH to build the project.
    echo.
    pause
    exit /b 1
)

echo [INFO] Compiling and packaging modules with Maven...
call "%MVN_CMD%" clean package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Maven build failed. Please inspect the output above.
    echo.
    pause
    exit /b 1
)

:CHECK_PORTS
REM 3. Ensure data and upload directories exist
if not exist "data\portal\uploads" mkdir "data\portal\uploads"
if not exist "data\institute\uploads\resumes" mkdir "data\institute\uploads\resumes"
if not exist "data\placement\uploads\jd" mkdir "data\placement\uploads\jd"
if not exist "data\placement\uploads\results" mkdir "data\placement\uploads\results"
if not exist "data\placement\uploads\offers" mkdir "data\placement\uploads\offers"
if not exist "data\placement\uploads\logos" mkdir "data\placement\uploads\logos"

REM 4. Clean up any stale processes on ports 8081, 8082, 8088
powershell -NoProfile -Command "$ports = @(8081, 8082, 8088); foreach ($p in $ports) { $conns = Get-NetTCPConnection -LocalPort $p -ErrorAction SilentlyContinue; if ($conns) { $pids = $conns | Select-Object -ExpandProperty OwningProcess -Unique; foreach ($id in $pids) { if ($id -gt 0) { Stop-Process -Id $id -Force -ErrorAction SilentlyContinue; } } } }" >nul 2>&1

REM 5. Detect port 8080 availability
set "PORTAL_PORT=8080"
powershell -NoProfile -Command "if (Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue) { exit 1 } else { exit 0 }" >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [NOTE] Port 8080 is occupied by an existing system service.
    echo [NOTE] Assigning Portal Gateway to Port 8088.
    set "PORTAL_PORT=8088"
)

echo.
echo Launching all 3 pillars in dedicated console windows:
echo   - Institute Pillar : http://localhost:8081/institute
echo   - Placement Pillar : http://localhost:8082/placement
echo   - Portal Gateway   : http://localhost:!PORTAL_PORT!
echo.

set "JAVA_OPTS=--enable-native-access=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED -XX:+EnableDynamicAgentLoading"

REM 5. Start the 3 services
echo [1/3] Starting Institute Pillar (Port 8081)...
start "Institute Pillar 8081" /D "%~dp0" cmd /k "title Institute Pillar 8081 && java %JAVA_OPTS% -Dserver.port=8081 -Dspring.profiles.active=dev -jar institute\target\institute-1.0.0-SNAPSHOT.jar"

echo [2/3] Starting Placement Pillar (Port 8082)...
start "Placement Pillar 8082" /D "%~dp0" cmd /k "title Placement Pillar 8082 && java %JAVA_OPTS% -Dserver.port=8082 -Dspring.profiles.active=dev -jar placement\target\placement-1.0.0-SNAPSHOT.jar"

echo [3/3] Starting Portal Gateway and SSO (Port !PORTAL_PORT!)...
start "Portal Gateway !PORTAL_PORT!" /D "%~dp0" cmd /k "title Portal Gateway !PORTAL_PORT! && java %JAVA_OPTS% -Dserver.port=!PORTAL_PORT! -Dspring.profiles.active=dev -jar portal\target\portal-1.0.0-SNAPSHOT.jar"

echo.
echo Waiting for services to initialize...
echo Live logs can be monitored in each opened window.
echo.

set TIMEOUT=40
set COUNT=0

:WAIT_LOOP
set /a COUNT+=1
powershell -NoProfile -Command "Start-Sleep -Seconds 3" >nul 2>&1

REM Check Portal Health
powershell -NoProfile -Command "try { $r = Invoke-WebRequest -Uri 'http://localhost:!PORTAL_PORT!/choose-domain.html' -UseBasicParsing -TimeoutSec 2; if ($r.StatusCode -eq 200) { exit 0 } else { exit 1 } } catch { exit 1 }" >nul 2>&1
set PORTAL_STATUS=!ERRORLEVEL!

REM Check Institute Health
powershell -NoProfile -Command "try { $r = Invoke-WebRequest -Uri 'http://localhost:8081/institute/index.html' -UseBasicParsing -TimeoutSec 2; if ($r.StatusCode -eq 200) { exit 0 } else { exit 1 } } catch { exit 1 }" >nul 2>&1
set INST_STATUS=!ERRORLEVEL!

REM Check Placement Health
powershell -NoProfile -Command "try { $r = Invoke-WebRequest -Uri 'http://localhost:8082/placement/index.html' -UseBasicParsing -TimeoutSec 2; if ($r.StatusCode -eq 200) { exit 0 } else { exit 1 } } catch { exit 1 }" >nul 2>&1
set PLACE_STATUS=!ERRORLEVEL!

if !PORTAL_STATUS! equ 0 if !INST_STATUS! equ 0 if !PLACE_STATUS! equ 0 goto ALL_HEALTHY

if !COUNT! geq !TIMEOUT! (
    echo [WARNING] Services are taking longer than normal to boot.
    echo Please inspect individual console windows for logs.
    goto SUMMARY
)

echo [Waiting...] Services booting up (Check !COUNT! of !TIMEOUT!)...
goto WAIT_LOOP

:ALL_HEALTHY
echo.
echo ==============================================================================
echo [SUCCESS] ALL THREE SERVICES ARE ONLINE AND HEALTHY!
echo ==============================================================================

start http://localhost:!PORTAL_PORT!

:SUMMARY
echo.
echo ------------------------------------------------------------------------------
echo ACCESS URLS:
echo   - Unified Portal Entry : http://localhost:!PORTAL_PORT!
echo   - Choose Domain        : http://localhost:!PORTAL_PORT!/choose-domain.html
echo   - Institute Pillar     : http://localhost:!PORTAL_PORT!/institute/dashboard.html
echo   - Placement Pillar     : http://localhost:!PORTAL_PORT!/placement/dashboard.html
echo ------------------------------------------------------------------------------
echo DEMO CREDENTIALS:
echo   - Student  : aarav@college.edu      (Password: Student@123, Roll: 21CS001)
echo   - TPO      : tpo@college.edu        (Password: Tpo@123, Training and Placement)
echo   - Director : director@college.edu   (Password: Director@123, Campus Director)
echo   - Company  : hr@google.com          (Password: Company@123, Google Recruiter)
echo   - Admin    : admin@college.edu      (Password: Admin@123, System Administrator)
echo ------------------------------------------------------------------------------
echo To stop all services gracefully, run: stop-all.bat
echo ==============================================================================
echo.
pause

endlocal
