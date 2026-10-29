#!/usr/bin/env bash
# ==============================================================================
# Graceful Shutdown Script: Placement Assistance Web Application
# Stops services listening on ports 8080 (Portal), 8081 (Institute), 8082 (Placement)
# ==============================================================================

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

echo "=============================================================================="
echo "          PLACEMENT ASSISTANCE WEB APPLICATION - STOPPING ALL SERVICES"
echo "=============================================================================="
echo ""

# Stop via PID files first if present
for pidfile in .portal.pid .institute.pid .placement.pid; do
    if [ -f "$pidfile" ]; then
        PID=$(cat "$pidfile")
        if kill -0 "$PID" 2>/dev/null; then
            echo "Stopping PID $PID from $pidfile..."
            kill "$PID" 2>/dev/null || kill -9 "$PID" 2>/dev/null
        fi
        rm -f "$pidfile"
    fi
done

# Fallback: check ports
for port in 8080 8081 8082; do
    PID=$(lsof -ti :$port 2>/dev/null || true)
    if [ -n "$PID" ]; then
        echo "Stopping lingering process on port $port (PID: $PID)..."
        kill -9 "$PID" 2>/dev/null || true
    fi
done

echo ""
echo "All pillar services and gateway have been stopped."
echo "=============================================================================="
