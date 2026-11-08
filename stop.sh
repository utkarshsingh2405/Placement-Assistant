#!/usr/bin/env bash
# ==============================================================================
# Placement Assistance Platform - Unified Single-JVM Stop Script (Linux / macOS)
# Stops the running Placement Assistant process on ports 8080 / 8088 or by JAR name.
# ==============================================================================

echo "=============================================================================="
echo "      PLACEMENT ASSISTANCE PLATFORM - STOPPING UNIFIED SERVICE"
echo "=============================================================================="
echo ""

STOPPED=0

# 1. Stop by portal.jar match
PIDS=$(pgrep -f "portal.jar" 2>/dev/null || true)
if [ -n "$PIDS" ]; then
    for PID in $PIDS; do
        echo "[OK] Stopping process PID $PID (portal.jar)..."
        kill -15 "$PID" 2>/dev/null || kill -9 "$PID" 2>/dev/null || true
        STOPPED=$((STOPPED + 1))
    done
fi

# 2. Stop Java on port 8080 or 8088
for PORT in 8080 8088; do
    PORT_PIDS=$(lsof -ti tcp:"$PORT" 2>/dev/null || fuser "$PORT"/tcp 2>/dev/null || true)
    if [ -n "$PORT_PIDS" ]; then
        for PID in $PORT_PIDS; do
            CMD=$(ps -p "$PID" -o comm= 2>/dev/null || true)
            if [[ "$CMD" == *"java"* ]]; then
                echo "[OK] Stopping Java process on port $PORT (PID: $PID)..."
                kill -15 "$PID" 2>/dev/null || kill -9 "$PID" 2>/dev/null || true
                STOPPED=$((STOPPED + 1))
            fi
        done
    fi
done

echo ""
if [ "$STOPPED" -eq 0 ]; then
    echo "[INFO] No running Placement Assistant instances found."
else
    echo "[SUCCESS] Stopped $STOPPED application process(es)."
fi

echo "=============================================================================="
echo "[OK] Stop operation completed."
echo "=============================================================================="
