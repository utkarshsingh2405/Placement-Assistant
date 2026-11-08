@echo off
REM ==============================================================================
REM Placement Assistance Platform - Unified Single-JVM Stop Script (Windows)
REM Safely stops the Placement Assistant single-JVM process.
REM ==============================================================================

cd /d "%~dp0"
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0stop.ps1" %*
if %ERRORLEVEL% neq 0 (
    echo.
    pause
)
