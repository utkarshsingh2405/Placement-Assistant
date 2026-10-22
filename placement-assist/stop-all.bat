@echo off
cd /d "%~dp0"
echo ==============================================================================
echo          PLACEMENT ASSISTANCE WEB APPLICATION - STOPPING ALL SERVICES
echo ==============================================================================
echo.

powershell -NoProfile -Command "$ports = @(8081, 8082, 8088); foreach ($p in $ports) { $conns = Get-NetTCPConnection -LocalPort $p -ErrorAction SilentlyContinue; if ($conns) { $pids = $conns | Select-Object -ExpandProperty OwningProcess -Unique; foreach ($id in $pids) { if ($id -gt 0) { Write-Host ('Stopping process on port ' + $p + ' with PID ' + $id); Stop-Process -Id $id -Force -ErrorAction SilentlyContinue; } } } else { Write-Host ('Port ' + $p + ' is clear.'); } }"

echo.
echo All pillar services and gateway have been stopped.
echo ==============================================================================
echo.
