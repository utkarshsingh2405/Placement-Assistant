#!/bin/sh
set -e

# Target DB path from environment or default
TARGET_DB="${SQLITE_DB_PATH:-${DB_PATH:-/data/app.db}}"
TARGET_DIR=$(dirname "$TARGET_DB")

# Ensure target directory exists
mkdir -p "$TARGET_DIR"

# Pre-seeded database location inside image
SEED_DB="/app/database/placement_assist.db"

# Copy pre-seeded DB if target does not exist and seed DB is available
if [ ! -f "$TARGET_DB" ]; then
    if [ -f "$SEED_DB" ]; then
        echo "[entrypoint] Initializing database: copying seed $SEED_DB -> $TARGET_DB"
        cp "$SEED_DB" "$TARGET_DB"
    else
        echo "[entrypoint] No seed DB found at $SEED_DB. A new database will be initialized at $TARGET_DB"
    fi
else
    echo "[entrypoint] Existing database found at '$TARGET_DB'. Preserving existing data."
fi

# Ensure /tmp exists and is writable
mkdir -p /tmp
chmod 1777 /tmp

exec "$@"
