<#
.SYNOPSIS
    Placement Assistance Platform - Unified Single-JVM Launcher (PowerShell)
.DESCRIPTION
    Runs all 3 pillars (Portal, Institute, Placement) in ONE unified Spring Boot JVM.
    Fits within 512 MB RAM (Render free tier / local development).
#>

[CmdletBinding()]
param (
    [int]$Port = 0,
    [string]$Profile = "sqlite",
    [switch]$NoBrowser
)

$ErrorActionPreference = "Stop"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $ScriptDir

Write-Host "==============================================================================" -ForegroundColor Cyan
Write-Host "          PLACEMENT ASSISTANCE WEB PLATFORM - UNIFIED LAUNCHER" -ForegroundColor Cyan
Write-Host "==============================================================================" -ForegroundColor Cyan
Write-Host ""

# 1. Load .env if present
$envFile = Join-Path $ScriptDir ".env"
if (-not (Test-Path $envFile)) {
    $envFile = Join-Path $ScriptDir "backend\.env"
}
if (Test-Path $envFile) {
    Write-Host "[INFO] Loading configuration from $envFile..." -ForegroundColor Gray
    Get-Content $envFile | ForEach-Object {
        $line = $_.Trim()
        if ($line -and -not $line.StartsWith("#") -and $line.Contains("=")) {
            $parts = $line.Split("=", 2)
            $k = $parts[0].Trim()
            $v = $parts[1].Trim()
            if (-not [System.Environment]::GetEnvironmentVariable($k)) {
                [System.Environment]::SetEnvironmentVariable($k, $v)
            }
        }
    }
}

# Default secrets if not set
if (-not $env:JWT_SECRET) {
    $env:JWT_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
}
if (-not $env:INSTITUTE_API_KEY) {
    $env:INSTITUTE_API_KEY = "inst-sec-key-7f9a12b4e8c345d6"
}

# 2. Check Java Runtime (PowerShell 5.1 compatible)
$javaCmd = $null
$cmdObj = Get-Command java -ErrorAction SilentlyContinue
if ($cmdObj) {
    $javaCmd = $cmdObj.Source
} elseif ($env:JAVA_HOME) {
    $cand = Join-Path $env:JAVA_HOME "bin\java.exe"
    if (Test-Path $cand) { $javaCmd = $cand }
}

if (-not $javaCmd) {
    $foundJavas = @()
    if (Test-Path "C:\Program Files\Java") {
        $foundJavas += Get-ChildItem "C:\Program Files\Java\jdk*\bin\java.exe" -ErrorAction SilentlyContinue
    }
    if (Test-Path "C:\Program Files\Eclipse Adoptium") {
        $foundJavas += Get-ChildItem "C:\Program Files\Eclipse Adoptium\jdk*\bin\java.exe" -ErrorAction SilentlyContinue
    }
    if ($foundJavas.Count -gt 0) {
        $javaCmd = $foundJavas[0].FullName
    }
}

if (-not $javaCmd) {
    Write-Error "[ERROR] Java 17+ was not found on PATH or JAVA_HOME. Please install Java 17+."
    exit 1
}
Write-Host "[OK] Java runtime detected: $javaCmd" -ForegroundColor Green

# 3. Check Unified Runnable JAR
$portalJar = Join-Path $ScriptDir "backend\portal\target\portal.jar"
if (-not (Test-Path $portalJar)) {
    Write-Host "[INFO] Unified portal.jar not found. Building project with Maven..." -ForegroundColor Yellow
    $mvnw = Join-Path $ScriptDir "mvnw.cmd"
    if (Test-Path $mvnw) {
        & $mvnw clean package -DskipTests
    } else {
        mvn clean package -DskipTests
    }
    if ($LASTEXITCODE -ne 0 -or -not (Test-Path $portalJar)) {
        Write-Error "[ERROR] Maven build failed."
        exit 1
    }
    Write-Host "[OK] Build completed successfully." -ForegroundColor Green
} else {
    Write-Host "[OK] Unified portal.jar found." -ForegroundColor Green
}

# 4. Storage & Database Directories
$dirs = @(
    "database",
    "backend\database",
    "data\portal\uploads",
    "data\institute\uploads\resumes",
    "data\placement\uploads\jd",
    "data\placement\uploads\results",
    "data\placement\uploads\offers",
    "data\placement\uploads\logos",
    "uploads\resumes"
)
foreach ($d in $dirs) {
    $p = Join-Path $ScriptDir $d
    if (-not (Test-Path $p)) {
        New-Item -ItemType Directory -Path $p -Force | Out-Null
    }
}

$dbPath = Join-Path $ScriptDir "database\placement_assist.db"
if (-not (Test-Path $dbPath)) {
    $altDb = Join-Path $ScriptDir "backend\database\placement_assist.db"
    if (Test-Path $altDb) {
        Write-Host "[INFO] Copying consolidated database to database\placement_assist.db..." -ForegroundColor Gray
        Copy-Item -Path $altDb -Destination $dbPath -Force
    } else {
        Write-Host "[INFO] Running database merge script..." -ForegroundColor Yellow
        python (Join-Path $ScriptDir "scripts\merge_dbs.py")
    }
}
$dbPathNormalized = $dbPath.Replace('\', '/')

# 5. Smart Port Resolution & Stale Java Cleanup
$targetPort = $Port
if ($targetPort -eq 0 -and $env:PORT) {
    $targetPort = [int]$env:PORT
}
if ($targetPort -eq 0) {
    $targetPort = 8080
}

# Check if targetPort is already bound
$conn = Get-NetTCPConnection -LocalPort $targetPort -ErrorAction SilentlyContinue
if ($conn) {
    $pids = $conn | Select-Object -ExpandProperty OwningProcess -Unique
    $cleaned = $false
    foreach ($id in $pids) {
        $proc = Get-Process -Id $id -ErrorAction SilentlyContinue
        if ($proc -and $proc.ProcessName -eq 'java') {
            Write-Host "[INFO] Terminating stale Java process on port $targetPort (PID: $id)..." -ForegroundColor Yellow
            Stop-Process -Id $id -Force -ErrorAction SilentlyContinue
            $cleaned = $true
        }
    }
    if ($cleaned) {
        Start-Sleep -Seconds 1
    }
    $connAfter = Get-NetTCPConnection -LocalPort $targetPort -ErrorAction SilentlyContinue
    if ($connAfter) {
        Write-Host "[NOTE] Port $targetPort is occupied by a system service." -ForegroundColor Yellow
        if ($targetPort -eq 8080) {
            $targetPort = 8088
            Write-Host "[NOTE] Automatically falling back to Port $targetPort." -ForegroundColor Yellow
        }
    }
}

# Also ensure targetPort 8088 doesn't have a stale Java process
if ($targetPort -eq 8088) {
    $conn88 = Get-NetTCPConnection -LocalPort 8088 -ErrorAction SilentlyContinue
    if ($conn88) {
        foreach ($id in ($conn88 | Select-Object -ExpandProperty OwningProcess -Unique)) {
            $proc = Get-Process -Id $id -ErrorAction SilentlyContinue
            if ($proc -and $proc.ProcessName -eq 'java') {
                Write-Host "[INFO] Terminating stale Java process on port 8088 (PID: $id)..." -ForegroundColor Yellow
                Stop-Process -Id $id -Force -ErrorAction SilentlyContinue
            }
        }
    }
}

$chosenPort = $targetPort

Write-Host ""
Write-Host "==============================================================================" -ForegroundColor Green
Write-Host "[READY] Launching unified platform in a SINGLE JVM:" -ForegroundColor Green
Write-Host "  - Unified Portal Entry : http://localhost:$chosenPort/" -ForegroundColor White
Write-Host "  - Domain Hub (Choose)  : http://localhost:$chosenPort/choose-domain.html" -ForegroundColor White
Write-Host "  - Institute Pillar     : http://localhost:$chosenPort/institute/dashboard.html" -ForegroundColor White
Write-Host "  - Placement Pillar     : http://localhost:$chosenPort/placement/dashboard.html" -ForegroundColor White
Write-Host "  - Health Check         : http://localhost:$chosenPort/healthz" -ForegroundColor White
Write-Host "  - Database File        : $dbPathNormalized" -ForegroundColor White
Write-Host "  - Memory Optimization  : Enabled (Target: ~350 MB RAM)" -ForegroundColor White
Write-Host "==============================================================================" -ForegroundColor Green
Write-Host "[Press Ctrl+C at any time to gracefully stop the application]" -ForegroundColor Gray
Write-Host ""

# Background browser opener
if (-not $NoBrowser) {
    Start-Job -ScriptBlock {
        param($p)
        for ($i=0; $i -lt 30; $i++) {
            Start-Sleep -Seconds 1
            try {
                $r = Invoke-WebRequest -Uri "http://localhost:$p/healthz" -UseBasicParsing -TimeoutSec 2
                if ($r.StatusCode -eq 200) {
                    Start-Process "http://localhost:$p/"
                    break
                }
            } catch {}
        }
    } -ArgumentList $chosenPort | Out-Null
}

# Execute Unified Spring Boot JAR
$javaArgs = @(
    "-XX:MaxRAMPercentage=60",
    "-XX:+UseSerialGC",
    "-XX:TieredStopAtLevel=1",
    "-Xss512k",
    "-XX:MaxMetaspaceSize=128m",
    "-Dserver.port=$chosenPort",
    "-Dspring.profiles.active=$Profile",
    "-DDB_PATH=$dbPathNormalized",
    "-DJWT_SECRET=$($env:JWT_SECRET)",
    "-DINSTITUTE_API_KEY=$($env:INSTITUTE_API_KEY)",
    "--enable-native-access=ALL-UNNAMED",
    "-XX:+EnableDynamicAgentLoading",
    "-jar",
    $portalJar
)

& $javaCmd $javaArgs
