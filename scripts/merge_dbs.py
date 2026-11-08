#!/usr/bin/env python3
"""
scripts/merge_dbs.py
Cross-platform SQLite database merger.
Merges portal.db, institute.db, and placement.db into a single unified database.
"""
import os
import shutil
import sqlite3
import sys

def main():
    root_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    db_dir = os.path.join(root_dir, "backend", "database")
    target_db = sys.argv[1] if len(sys.argv) > 1 else os.path.join(db_dir, "placement_assist.db")

    print(f"=== Merging SQLite databases into: {target_db} ===")

    portal_db = os.path.join(db_dir, "portal.db")
    institute_db = os.path.join(db_dir, "institute.db")
    placement_db = os.path.join(db_dir, "placement.db")

    for db_path, name in [(portal_db, "portal.db"), (institute_db, "institute.db"), (placement_db, "placement.db")]:
        if not os.path.exists(db_path):
            print(f"ERROR: Required source database '{name}' not found at {db_path}", file=sys.stderr)
            sys.exit(1)

    os.makedirs(os.path.dirname(target_db), exist_ok=True)
    if os.path.exists(target_db):
        backup_path = target_db + ".bak"
        print(f"Backing up existing database to {backup_path}")
        shutil.copy2(target_db, backup_path)
        os.remove(target_db)

    conn = sqlite3.connect(target_db)
    cur = conn.cursor()
    cur.execute("PRAGMA journal_mode=WAL;")
    cur.execute("PRAGMA busy_timeout=5000;")

    # Attach sources
    cur.execute(f"ATTACH ? AS db_portal;", (portal_db,))
    cur.execute(f"ATTACH ? AS db_institute;", (institute_db,))
    cur.execute(f"ATTACH ? AS db_placement;", (placement_db,))

    # Copy tables
    sources = [
        ("db_portal", ["audit_logs", "portal_users"]),
        ("db_institute", [
            "attendances", "bookmarks", "class_sessions", "companies",
            "crt_sessions", "drive_rounds", "drives", "hackathons",
            "internships", "offcampus_jobs", "questions", "resumes",
            "round_results", "student_skills", "students", "users"
        ]),
        ("db_placement", [
            "placement_applications", "placement_attendance", "placement_companies",
            "placement_drives", "placement_feedback", "placement_live_updates",
            "placement_placed_students", "placement_results", "placement_stages",
            "placement_subscriptions", "placement_talk_questions", "placement_talks"
        ])
    ]

    total_tables = 0
    total_rows = 0
    for alias, tables in sources:
        for table in tables:
            cur.execute(f'CREATE TABLE "{table}" AS SELECT * FROM {alias}."{table}";')
            cur.execute(f'SELECT COUNT(*) FROM "{table}";')
            row_count = cur.fetchone()[0]
            print(f"  + Merged table '{table}': {row_count} rows")
            total_tables += 1
            total_rows += row_count

    cur.execute("DETACH db_portal;")
    cur.execute("DETACH db_institute;")
    cur.execute("DETACH db_placement;")
    conn.commit()
    conn.close()

    print(f"\nMerge complete! Successfully merged {total_tables} tables and {total_rows} rows into {target_db}.")

if __name__ == "__main__":
    main()
