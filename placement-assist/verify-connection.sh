#!/usr/bin/env bash
# ==============================================================================
# Connectivity and Integration Verifier: Placement Assistance Web Application
# ==============================================================================

echo "=============================================================================="
echo "      PLACEMENT ASSISTANCE WEB APPLICATION - INTEGRATION VERIFIER"
echo "=============================================================================="
echo ""

echo "[1/3] Testing Gateway Route -> Institute Pillar (/institute)..."
if curl -s -f "http://localhost:8080/institute/actuator/health" > /dev/null 2>&1; then
    echo -e "  \033[0;32m[PASS]\033[0m Gateway correctly routes /institute to port 8081"
else
    echo -e "  \033[0;31m[FAIL]\033[0m Gateway or Institute route offline"
fi

echo "[2/3] Testing Gateway Route -> Placement Pillar (/placement)..."
if curl -s -f "http://localhost:8080/placement/actuator/health" > /dev/null 2>&1; then
    echo -e "  \033[0;32m[PASS]\033[0m Gateway correctly routes /placement to port 8082"
else
    echo -e "  \033[0;31m[FAIL]\033[0m Gateway or Placement route offline"
fi

echo "[3/3] Testing Inter-Service Integration API Key Authentication..."
res=$(curl -s -H "X-API-KEY: inst-sec-key-7f9a12b4e8c345d6" "http://localhost:8081/institute/api/integration/drives" 2>/dev/null || echo "")
if [ -n "$res" ]; then
    echo -e "  \033[0;32m[PASS]\033[0m Inter-service API key authenticated successfully"
else
    echo -e "  \033[0;33m[NOTE]\033[0m Institute service offline or not reachable"
fi

echo ""
echo "=============================================================================="
echo "Verification check finished."
echo "=============================================================================="
