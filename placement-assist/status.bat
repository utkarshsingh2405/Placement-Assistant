@echo off
REM ==============================================================================
REM Health and Status Inspector: Placement Assistance Web Application
REM Checks health of:
REM   - Portal Gateway (8080 or 8088)
REM   - Institute Pillar (8081)
REM   - Placement Pillar (8082)
REM ==============================================================================

cd /d "%~dp0"
setlocal EnableDelayedExpansion

echo ==============================================================================
echo          PLACEMENT ASSISTANCE WEB APPLICATION - SERVICE STATUS
echo ==============================================================================
echo.

echo ------------------------------------------------------------------------------
echo SERVICE           PORT    ENDPOINT                                STATUS
echo ------------------------------------------------------------------------------

REM Portal Gateway (Check 8080 or 8088)
powershell -NoProfile -Command "try { $r = Invoke-RestMethod -Uri 'http://localhost:8080/actuator/health' -TimeoutSec 2; Write-Host 'Portal Gateway    8080    http://localhost:8080                  ' -NoNewline; Write-Host 'ONLINE' -ForegroundColor Green } catch { try { $r2 = Invoke-WebRequest -Uri 'http://localhost:8088/choose-domain.html' -UseBasicParsing -TimeoutSec 2; Write-Host 'Portal Gateway    8088    http://localhost:8088                  ' -NoNewline; Write-Host 'ONLINE' -ForegroundColor Green } catch { Write-Host 'Portal Gateway    8080/88 http://localhost:8088                  ' -NoNewline; Write-Host 'OFFLINE' -ForegroundColor Red } }"

REM Institute Pillar
powershell -NoProfile -Command "try { $r = Invoke-WebRequest -Uri 'http://localhost:8081/institute/index.html' -UseBasicParsing -TimeoutSec 2; if ($r.StatusCode -eq 200) { Write-Host 'Institute Pillar  8081    http://localhost:8081/institute        ' -NoNewline; Write-Host 'ONLINE' -ForegroundColor Green } else { Write-Host 'Institute Pillar  8081    http://localhost:8081/institute        ' -NoNewline; Write-Host 'OFFLINE' -ForegroundColor Red } } catch { Write-Host 'Institute Pillar  8081    http://localhost:8081/institute        ' -NoNewline; Write-Host 'OFFLINE' -ForegroundColor Red }"

REM Placement Pillar
powershell -NoProfile -Command "try { $r = Invoke-WebRequest -Uri 'http://localhost:8082/placement/index.html' -UseBasicParsing -TimeoutSec 2; if ($r.StatusCode -eq 200) { Write-Host 'Placement Pillar  8082    http://localhost:8082/placement        ' -NoNewline; Write-Host 'ONLINE' -ForegroundColor Green } else { Write-Host 'Placement Pillar  8082    http://localhost:8082/placement        ' -NoNewline; Write-Host 'OFFLINE' -ForegroundColor Red } } catch { Write-Host 'Placement Pillar  8082    http://localhost:8082/placement        ' -NoNewline; Write-Host 'OFFLINE' -ForegroundColor Red }"

echo ------------------------------------------------------------------------------
echo.
pause
endlocal
