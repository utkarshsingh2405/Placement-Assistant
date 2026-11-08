@echo off
REM ==============================================================================
REM Placement Assistance Platform - Unified Single-JVM Launcher (Windows)
REM Runs all three pillars (Portal, Institute, Placement) in ONE Spring Boot JVM.
REM Fits within 512 MB RAM (Render free tier / local development).
REM ==============================================================================

cd /d "%~dp0"
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0start.ps1" %*
if %ERRORLEVEL% neq 0 (
    echo.
    pause
)
