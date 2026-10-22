#!/usr/bin/env bash
# ==============================================================================
# Unified Launcher (Bash): Placement Assistance Platform (All 3 Pillars)
# Starts:
#   1. Portal Gateway & SSO (Port 8080)
#   2. Institute Pillar (Port 8081, Context /institute)
#   3. Placement Pillar (Port 8082, Context /placement)
# ==============================================================================

set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

echo "=============================================================================="
echo "          PLACEMENT ASSISTANCE WEB APPLICATION - UNIFIED LAUNCHER"
echo "=============================================================================="
echo ""

# Ensure data and upload directories exist
mkdir -p data/portal/uploads
mkdir -p data/institute/uploads/resumes
mkdir -p data/placement/uploads/jd
mkdir -p data/placement/uploads/results
mkdir -p data/placement/uploads/offers
mkdir -p data/placement/uploads/logos

mkdir -p logs

echo "[1/3] Starting Institute Pillar (Port 8081, Context /institute)..."
mvn spring-boot:run -pl institute > logs/institute.log 2>&1 &
echo $! > .institute.pid

echo "[2/3] Starting Placement Pillar (Port 8082, Context /placement)..."
mvn spring-boot:run -pl placement > logs/placement.log 2>&1 &
echo $! > .placement.pid

echo "[3/3] Starting Portal Gateway & SSO (Port 8080)..."
mvn spring-boot:run -pl portal > logs/portal.log 2>&1 &
echo $! > .portal.pid

echo ""
echo "Waiting for all three services to pass health checks..."

check_health() {
    local url=$1
    curl -s -f "$url" > /dev/null 2>&1
}

TIMEOUT=60
COUNT=0

while [ $COUNT -lt $TIMEOUT ]; do
    COUNT=$((COUNT + 1))
    sleep 3

    PORTAL_OK=0
    INST_OK=0
    PLACE_OK=0

    if check_health "http://localhost:8080/actuator/health"; then PORTAL_OK=1; fi
    if check_health "http://localhost:8081/institute/actuator/health"; then INST_OK=1; fi
    if check_health "http://localhost:8082/placement/actuator/health"; then PLACE_OK=1; fi

    if [ $PORTAL_OK -eq 1 ] && [ $INST_OK -eq 1 ] && [ $PLACE_OK -eq 1 ]; then
        echo ""
        echo "=============================================================================="
        echo "[SUCCESS] ALL THREE SERVICES ARE ONLINE AND HEALTHY!"
        echo "=============================================================================="
        break
    fi

    echo "[Waiting...] Services initializing ($COUNT/$TIMEOUT)..."
done

echo ""
echo "------------------------------------------------------------------------------"
echo "ACCESS URLS:"
echo "  - Unified Portal Entry : http://localhost:8080"
echo "  - Choose Domain        : http://localhost:8080/choose-domain.html"
echo "  - Institute Pillar     : http://localhost:8080/institute/dashboard.html"
echo "  - Placement Pillar     : http://localhost:8080/placement/dashboard.html"
echo "------------------------------------------------------------------------------"
echo "DEMO CREDENTIALS (All passwords: Password@123)"
echo "  - Student  : student@college.edu   (Roll: 21CS001, Aarav Sharma)"
echo "  - TPO      : tpo@college.edu       (Training & Placement Officer)"
echo "  - Director : director@college.edu  (Campus Director)"
echo "  - Company  : company@google.com    (Recruiter, Google Campus)"
echo "  - Admin    : admin@college.edu     (System Administrator)"
echo "------------------------------------------------------------------------------"
echo "Logs are available in logs/ (institute.log, placement.log, portal.log)"
echo "To stop services gracefully, run: ./stop-all.sh"
echo "=============================================================================="
