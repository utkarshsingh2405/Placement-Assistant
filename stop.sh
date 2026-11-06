#!/usr/bin/env bash
# ==============================================================================
# Stops all Placement Assistant services on ports 8080, 8081, 8082, 8088
# ==============================================================================

echo "=============================================================================="
echo "      PLACEMENT ASSISTANCE PLATFORM - STOPPING ALL SERVICES"
echo "=============================================================================="

PORTS=(8080 8081 8082 8088)

for PORT in "${PORTS[@]}"; do
    PID=$(lsof -ti tcp:"$PORT" 2>/dev/null || fuser "$PORT"/tcp 2>/dev/null || true)
    if [ -n "$PID" ]; then
        echo "Stopping process on port $PORT (PID: $PID)..."
        kill -9 $PID 2>/dev/null || true
    else
        echo "Port $PORT is clear."
    fi
done

echo ""
echo "[OK] All pillar services and portal gateway have been stopped."
echo "=============================================================================="

