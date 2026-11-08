#!/usr/bin/env bash
# scripts/merge-dbs.sh
# Merges portal.db, institute.db, and placement.db into a single unified SQLite database.
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DB_DIR="${DIR}/backend/database"
TARGET_DB="${1:-${DB_DIR}/placement_assist.db}"

echo "=== Merging SQLite databases into ${TARGET_DB} ==="

mkdir -p "$(dirname "${TARGET_DB}")"

# If target already exists, back it up
if [ -f "${TARGET_DB}" ]; then
    echo "Backing up existing ${TARGET_DB} to ${TARGET_DB}.bak"
    cp "${TARGET_DB}" "${TARGET_DB}.bak"
    rm -f "${TARGET_DB}"
fi

sqlite3 "${TARGET_DB}" <<EOF
PRAGMA journal_mode=WAL;
PRAGMA busy_timeout=5000;

-- Attach source databases
ATTACH '${DB_DIR}/portal.db' AS db_portal;
ATTACH '${DB_DIR}/institute.db' AS db_institute;
ATTACH '${DB_DIR}/placement.db' AS db_placement;

-- Copy portal tables
CREATE TABLE audit_logs AS SELECT * FROM db_portal.audit_logs;
CREATE TABLE portal_users AS SELECT * FROM db_portal.portal_users;

-- Copy institute tables
CREATE TABLE attendances AS SELECT * FROM db_institute.attendances;
CREATE TABLE bookmarks AS SELECT * FROM db_institute.bookmarks;
CREATE TABLE class_sessions AS SELECT * FROM db_institute.class_sessions;
CREATE TABLE companies AS SELECT * FROM db_institute.companies;
CREATE TABLE crt_sessions AS SELECT * FROM db_institute.crt_sessions;
CREATE TABLE drive_rounds AS SELECT * FROM db_institute.drive_rounds;
CREATE TABLE drives AS SELECT * FROM db_institute.drives;
CREATE TABLE hackathons AS SELECT * FROM db_institute.hackathons;
CREATE TABLE internships AS SELECT * FROM db_institute.internships;
CREATE TABLE offcampus_jobs AS SELECT * FROM db_institute.offcampus_jobs;
CREATE TABLE questions AS SELECT * FROM db_institute.questions;
CREATE TABLE resumes AS SELECT * FROM db_institute.resumes;
CREATE TABLE round_results AS SELECT * FROM db_institute.round_results;
CREATE TABLE student_skills AS SELECT * FROM db_institute.student_skills;
CREATE TABLE students AS SELECT * FROM db_institute.students;
CREATE TABLE users AS SELECT * FROM db_institute.users;

-- Copy placement tables
CREATE TABLE placement_applications AS SELECT * FROM db_placement.placement_applications;
CREATE TABLE placement_attendance AS SELECT * FROM db_placement.placement_attendance;
CREATE TABLE placement_companies AS SELECT * FROM db_placement.placement_companies;
CREATE TABLE placement_drives AS SELECT * FROM db_placement.placement_drives;
CREATE TABLE placement_feedback AS SELECT * FROM db_placement.placement_feedback;
CREATE TABLE placement_live_updates AS SELECT * FROM db_placement.placement_live_updates;
CREATE TABLE placement_placed_students AS SELECT * FROM db_placement.placement_placed_students;
CREATE TABLE placement_results AS SELECT * FROM db_placement.placement_results;
CREATE TABLE placement_stages AS SELECT * FROM db_placement.placement_stages;
CREATE TABLE placement_subscriptions AS SELECT * FROM db_placement.placement_subscriptions;
CREATE TABLE placement_talk_questions AS SELECT * FROM db_placement.placement_talk_questions;
CREATE TABLE placement_talks AS SELECT * FROM db_placement.placement_talks;

DETACH db_portal;
DETACH db_institute;
DETACH db_placement;
EOF

echo "=== Merge completed successfully! Unified database created at: ${TARGET_DB} ==="
