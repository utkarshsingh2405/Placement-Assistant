#!/usr/bin/env bash
# ==============================================================================
# Placement Assistance Platform - Unified Single-JVM Launcher (Linux / macOS)
# Runs all 3 pillars (Portal, Institute, Placement) in ONE unified Spring Boot JVM.
# Fits within 512 MB RAM (Render free tier / local development).
# ==============================================================================

set -e
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
cd "$DIR"

echo "=============================================================================="
echo "         PLACEMENT ASSISTANCE WEB PLATFORM - UNIFIED LAUNCHER"
echo "=============================================================================="
echo ""

# 1. Load .env if present
if [ -f "$DIR/.env" ]; then
    echo "[INFO] Loading configuration from .env..."
    set -a
    # shellcheck disable=SC1091
    source "$DIR/.env"
    set +a
elif [ -f "$DIR/backend/.env" ]; then
    echo "[INFO] Loading configuration from backend/.env..."
    set -a
    # shellcheck disable=SC1091
    source "$DIR/backend/.env"
    set +a
fi

# Set default dev secrets if not defined
export JWT_SECRET="${JWT_SECRET:-404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}"
export INSTITUTE_API_KEY="${INSTITUTE_API_KEY:-inst-sec-key-7f9a12b4e8c345d6}"

# 2. Check Java Runtime
if ! command -v java >/dev/null 2>&1; then
    echo "[ERROR] Java was not found on PATH. Please install Java 17 or higher."
    exit 1
fi
echo "[OK] Java runtime detected: $(command -v java)"

# 3. Check Unified Runnable JAR
PORTAL_JAR="$DIR/backend/portal/target/portal.jar"
if [ ! -f "$PORTAL_JAR" ]; then
    echo "[INFO] Unified portal.jar not found. Building project with Maven..."
    if [ -f "$DIR/mvnw" ]; then
        chmod +x "$DIR/mvnw"
        "$DIR/mvnw" clean package -DskipTests
    else
        mvn clean package -DskipTests
    fi
    if [ ! -f "$PORTAL_JAR" ]; then
        echo "[ERROR] Maven build failed."
        exit 1
    fi
    echo "[OK] Build completed successfully."
else
    echo "[OK] Unified portal.jar found."
fi

# 4. Storage & Database Directories
mkdir -p "$DIR/database" \
         "$DIR/backend/database" \
         "$DIR/data/portal/uploads" \
         "$DIR/data/institute/uploads/resumes" \
         "$DIR/data/placement/uploads/jd" \
         "$DIR/data/placement/uploads/results" \
         "$DIR/data/placement/uploads/offers" \
         "$DIR/data/placement/uploads/logos" \
         "$DIR/uploads/resumes"

DB_FILE="$DIR/database/placement_assist.db"
if [ ! -f "$DB_FILE" ]; then
    if [ -f "$DIR/backend/database/placement_assist.db" ]; then
        echo "[INFO] Copying consolidated database to database/placement_assist.db..."
        cp "$DIR/backend/database/placement_assist.db" "$DB_FILE"
    elif [ -f "$DIR/scripts/merge_dbs.py" ]; then
        echo "[INFO] Running database merge script..."
        python3 "$DIR/scripts/merge_dbs.py" 2>/dev/null || python "$DIR/scripts/merge_dbs.py"
    fi
fi

# 5. Port Resolution
if [ -n "$PORT" ]; then
    APP_PORT="$PORT"
else
    APP_PORT="8080"
    if command -v nc >/dev/null 2>&1 && nc -z localhost 8080 2>/dev/null; then
        echo "[NOTE] Port 8080 is occupied. Selecting Port 8088."
        APP_PORT="8088"
    elif command -v lsof >/dev/null 2>&1 && lsof -i :8080 >/dev/null 2>&1; then
        echo "[NOTE] Port 8080 is occupied. Selecting Port 8088."
        APP_PORT="8088"
    fi
fi

DB_PROFILE="${SPRING_PROFILES_ACTIVE:-sqlite}"

echo ""
echo "=============================================================================="
echo "[READY] Launching unified platform in a SINGLE JVM:"
echo "  - Unified Portal Entry : http://localhost:$APP_PORT/"
echo "  - Domain Hub (Choose)  : http://localhost:$APP_PORT/choose-domain.html"
echo "  - Institute Pillar     : http://localhost:$APP_PORT/institute/dashboard.html"
echo "  - Placement Pillar     : http://localhost:$APP_PORT/placement/dashboard.html"
echo "  - Health Check         : http://localhost:$APP_PORT/healthz"
echo "  - Database File        : $DB_FILE"
echo "  - Memory Optimization  : Enabled (Target: ~350 MB RAM)"
echo "=============================================================================="
echo "[Press Ctrl+C at any time to gracefully stop the application]"
echo ""

# 6. Execute Single JVM
exec java \
    -XX:MaxRAMPercentage=60 \
    -XX:+UseSerialGC \
    -XX:TieredStopAtLevel=1 \
    -Xss512k \
    -XX:MaxMetaspaceSize=128m \
    -Dserver.port="$APP_PORT" \
    -Dspring.profiles.active="$DB_PROFILE" \
    -DDB_PATH="$DB_FILE" \
    -DJWT_SECRET="$JWT_SECRET" \
    -DINSTITUTE_API_KEY="$INSTITUTE_API_KEY" \
    --enable-native-access=ALL-UNNAMED \
    -XX:+EnableDynamicAgentLoading \
    -jar "$PORTAL_JAR"
