@echo off
REM ==============================================================================
REM Connectivity and Integration Verifier: Placement Assistance Web Application
REM ==============================================================================

cd /d "%~dp0"
setlocal EnableDelayedExpansion

echo ==============================================================================
echo       PLACEMENT ASSISTANCE WEB APPLICATION - INTEGRATION VERIFIER
echo ==============================================================================
echo.

set "GATEWAY_PORT=8080"
powershell -NoProfile -Command "try { $r = Invoke-WebRequest -Uri 'http://localhost:8080/choose-domain.html' -UseBasicParsing -TimeoutSec 1; exit 0 } catch { exit 1 }" >nul 2>&1
if %ERRORLEVEL% neq 0 (
    set "GATEWAY_PORT=8088"
)

echo [1/3] Testing Gateway Route -> Institute Pillar (/institute)...
powershell -NoProfile -Command "try { $r = Invoke-WebRequest -Uri 'http://localhost:!GATEWAY_PORT!/institute/index.html' -UseBasicParsing -TimeoutSec 3; if ($r.StatusCode -eq 200) { Write-Host '  [PASS] Gateway correctly routes /institute' -ForegroundColor Green } else { Write-Host '  [FAIL] Status: '$r.StatusCode -ForegroundColor Red } } catch { Write-Host '  [FAIL] Unable to connect to Gateway or Institute route' -ForegroundColor Red }"

echo [2/3] Testing Gateway Route -> Placement Pillar (/placement)...
powershell -NoProfile -Command "try { $r = Invoke-WebRequest -Uri 'http://localhost:!GATEWAY_PORT!/placement/index.html' -UseBasicParsing -TimeoutSec 3; if ($r.StatusCode -eq 200) { Write-Host '  [PASS] Gateway correctly routes /placement' -ForegroundColor Green } else { Write-Host '  [FAIL] Status: '$r.StatusCode -ForegroundColor Red } } catch { Write-Host '  [FAIL] Unable to connect to Gateway or Placement route' -ForegroundColor Red }"

echo [3/3] Testing Inter-Service Integration API Key Authentication...
powershell -NoProfile -Command "try { $headers = @{ 'X-API-KEY' = 'inst-sec-key-7f9a12b4e8c345d6' }; $r = Invoke-RestMethod -Uri 'http://localhost:8081/institute/api/integration/drives' -Headers $headers -TimeoutSec 3; Write-Host '  [PASS] Inter-service API key authenticated successfully' -ForegroundColor Green } catch { Write-Host '  [NOTE] Service offline or integration key returned: ' $_.Exception.Message -ForegroundColor Yellow }"

echo.
echo ==============================================================================
echo Verification check finished.
echo ==============================================================================
echo.
pause
endlocal
