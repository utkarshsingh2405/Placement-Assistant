@echo off
cd /d "%~dp0"
echo ==============================================================================
echo          PLACEMENT ASSISTANCE WEB APPLICATION - STOPPING ALL SERVICES
echo ==============================================================================
echo.

powershell -NoProfile -Command "$ports = @(8081, 8082, 8088); foreach ($p in $ports) { $conns = Get-NetTCPConnection -LocalPort $p -ErrorAction SilentlyContinue; if ($conns) { $pids = $conns | Select-Object -ExpandProperty OwningProcess -Unique; foreach ($id in $pids) { if ($id -gt 0) { Write-Host ('Stopping process on port ' + $p + ' with PID ' + $id); Stop-Process -Id $id -Force -ErrorAction SilentlyContinue; } } } else { Write-Host ('Port ' + $p + ' is clear.'); } }; $c80 = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue; if ($c80) { foreach ($id in ($c80 | Select-Object -ExpandProperty OwningProcess -Unique)) { $p = Get-Process -Id $id -ErrorAction SilentlyContinue; if ($p -and $p.ProcessName -eq 'java') { Write-Host ('Stopping Java process on port 8080 with PID ' + $id); Stop-Process -Id $id -Force -ErrorAction SilentlyContinue; } } }"

echo.
echo All pillar services and gateway have been stopped.
echo ==============================================================================
echo.
