<#
.SYNOPSIS
    Placement Assistance Platform - Unified Single-JVM Stop Script (PowerShell)
.DESCRIPTION
    Safely identifies and stops the running Placement Assistant single-JVM instance.
    Does not disrupt non-Java system services or the IDE's Java Language Server.
#>

[CmdletBinding()]
param ()

$ErrorActionPreference = "SilentlyContinue"

Write-Host "==============================================================================" -ForegroundColor Cyan
Write-Host "       PLACEMENT ASSISTANCE PLATFORM - STOPPING UNIFIED SERVICE" -ForegroundColor Cyan
Write-Host "==============================================================================" -ForegroundColor Cyan
Write-Host ""

$stopped = 0
$targetPids = @()

# 1. Target Java processes listening on application ports 8080 or 8088
$ports = @(8080, 8088)
foreach ($port in $ports) {
    $conns = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
    if ($conns) {
        $targetPids += ($conns | Select-Object -ExpandProperty OwningProcess -Unique)
    }
}

# 2. Check Java processes running portal.jar specifically
$javaProcs = Get-Process -Name java -ErrorAction SilentlyContinue
foreach ($jp in $javaProcs) {
    try {
        $procWmi = Get-CimInstance Win32_Process -Filter "ProcessId = $($jp.Id)" -ErrorAction SilentlyContinue
        if ($procWmi -and $procWmi.CommandLine -like "*portal.jar*") {
            $targetPids += $jp.Id
        }
    } catch {}
}

$uniquePids = $targetPids | Select-Object -Unique
foreach ($id in $uniquePids) {
    if ($id -gt 0) {
        $p = Get-Process -Id $id -ErrorAction SilentlyContinue
        if ($p -and $p.ProcessName -eq 'java') {
            Write-Host "[OK] Stopping Placement Assistant process (PID: $id)..." -ForegroundColor Green
            Stop-Process -Id $id -Force
            $stopped++
        }
    }
}

if ($stopped -eq 0) {
    Write-Host "[INFO] No running Placement Assistant instances found." -ForegroundColor Yellow
} else {
    Write-Host "[SUCCESS] Successfully stopped $stopped application process(es)." -ForegroundColor Green
}

Write-Host ""
Write-Host "==============================================================================" -ForegroundColor Cyan
Write-Host "[OK] Stop operation completed." -ForegroundColor Cyan
Write-Host "==============================================================================" -ForegroundColor Cyan
