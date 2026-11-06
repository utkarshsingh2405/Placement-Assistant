@echo off
setlocal
echo ==============================================================================
echo       PLACEMENT ASSISTANCE PLATFORM - STOPPING ALL SERVICES
echo ==============================================================================
echo.

powershell -NoProfile -Command "$ports = @(8080, 8081, 8082, 8088); foreach ($p in $ports) { $conns = Get-NetTCPConnection -LocalPort $p -ErrorAction SilentlyContinue; if ($conns) { $pids = $conns | Select-Object -ExpandProperty OwningProcess -Unique; foreach ($id in $pids) { if ($id -gt 0) { Write-Host ('Stopping process on port ' + $p + ' (PID: ' + $id + ')...'); Stop-Process -Id $id -Force -ErrorAction SilentlyContinue; } } } else { Write-Host ('Port ' + $p + ' is clear.'); } }"

echo.
echo [OK] All pillar services and portal gateway have been stopped.
echo ==============================================================================
pause
endlocal
