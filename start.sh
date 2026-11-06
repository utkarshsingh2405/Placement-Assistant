#!/usr/bin/env bash
# ==============================================================================
# Unified Single-Console Launcher: Placement Assistance Platform
# Starts Portal Gateway which embeds & supervises:
#   1. Portal Gateway & SSO (Port 8080)
#   2. Institute Pillar (Port 8081, Context /institute)
#   3. Placement Pillar (Port 8082, Context /placement)
# ==============================================================================

set -e
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
cd "$DIR"

echo "=============================================================================="
echo "         PLACEMENT ASSISTANCE WEB PLATFORM - UNIFIED LAUNCHER"
echo "=============================================================================="
echo ""

PORTAL_JAR="$DIR/backend/portal/target/portal-1.0.0-SNAPSHOT.jar"
INSTITUTE_JAR="$DIR/backend/institute/target/institute-1.0.0-SNAPSHOT.jar"
PLACEMENT_JAR="$DIR/backend/placement/target/placement-1.0.0-SNAPSHOT.jar"

if [ ! -f "$PORTAL_JAR" ] || [ ! -f "$INSTITUTE_JAR" ] || [ ! -f "$PLACEMENT_JAR" ]; then
    echo "[INFO] Application JARs not found. Building with Maven Wrapper..."
    if [ -f "$DIR/mvnw" ]; then
        chmod +x "$DIR/mvnw"
        "$DIR/mvnw" clean package -DskipTests
    else
        mvn clean package -DskipTests
    fi
fi

mkdir -p "$DIR/database" \
         "$DIR/data/portal/uploads" \
         "$DIR/data/institute/uploads/resumes" \
         "$DIR/data/placement/uploads/jd" \
         "$DIR/data/placement/uploads/results" \
         "$DIR/data/placement/uploads/offers" \
         "$DIR/data/placement/uploads/logos" \
         "$DIR/uploads/resumes"

if [ -z "$PORT" ]; then
    PORTAL_PORT="8080"
    if command -v nc >/dev/null 2>&1 && nc -z localhost 8080 2>/dev/null; then
        echo "[NOTE] Port 8080 is occupied. Assigning Portal Gateway to Port 8088."
        PORTAL_PORT="8088"
    elif command -v lsof >/dev/null 2>&1 && lsof -i :8080 >/dev/null 2>&1; then
        echo "[NOTE] Port 8080 is occupied. Assigning Portal Gateway to Port 8088."
        PORTAL_PORT="8088"
    fi
else
    PORTAL_PORT="$PORT"
fi
DB_PROFILE="sqlite"

export DB_PORTAL="$DIR/database/portal.db"
export DB_INSTITUTE="$DIR/database/institute.db"
export DB_PLACEMENT="$DIR/database/placement.db"

echo ""
echo "Launching unified platform in this console:"
echo "  - Unified Portal Entry : http://localhost:$PORTAL_PORT"
echo "  - Choose Domain Hub    : http://localhost:$PORTAL_PORT/choose-domain.html"
echo "  - Institute Pillar     : http://localhost:$PORTAL_PORT/institute/dashboard.html"
echo "  - Placement Pillar     : http://localhost:$PORTAL_PORT/placement/dashboard.html"
echo ""
echo "(Press Ctrl+C at any time to gracefully stop all pillars)"
echo ""

exec java \
    -Dserver.port="$PORTAL_PORT" \
    -Dspring.profiles.active="$DB_PROFILE" \
    -DDB_PATH="$DB_PORTAL" \
    -DDB_INSTITUTE_PATH="$DB_INSTITUTE" \
    -DDB_PLACEMENT_PATH="$DB_PLACEMENT" \
    -Dpillar.institute.jar="$INSTITUTE_JAR" \
    -Dpillar.placement.jar="$PLACEMENT_JAR" \
    --enable-native-access=ALL-UNNAMED \
    -XX:+EnableDynamicAgentLoading \
    -jar "$PORTAL_JAR"

