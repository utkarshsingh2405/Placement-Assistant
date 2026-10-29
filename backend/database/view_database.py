import sqlite3
import os
import sys

# Ensure UTF-8 output on Windows consoles
if hasattr(sys.stdout, 'reconfigure'):
    try:
        sys.stdout.reconfigure(encoding='utf-8')
    except Exception:
        pass

def inspect():
    db_dir = os.path.dirname(os.path.abspath(__file__))
    databases = ["portal.db", "institute.db", "placement.db"]
    
    print("=" * 75)
    print("       PLACEMENT ASSISTANT PLATFORM -- REAL-TIME DATABASE INSPECTOR")
    print(f"       Folder: {db_dir}")
    print("=" * 75)

    for db_name in databases:
        db_path = os.path.join(db_dir, db_name)
        if not os.path.exists(db_path):
            print(f"\n[MISSING] {db_name} (File not found at {db_path})")
            continue

        size_kb = os.path.getsize(db_path) / 1024.0
        print(f"\n[DB] DATABASE: {db_name}  ({size_kb:.1f} KB)")
        print("-" * 75)
        
        try:
            conn = sqlite3.connect(db_path)
            cur = conn.cursor()
            cur.execute("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' AND name NOT LIKE 'flyway_%' ORDER BY name;")
            tables = [r[0] for r in cur.fetchall()]
            
            if not tables:
                print("   (No user tables found)")
            else:
                for t in tables:
                    try:
                        cur.execute(f'SELECT count(*) FROM "{t}"')
                        count = cur.fetchone()[0]
                        print(f"   * {t:<28} : {count:>5} rows")
                    except Exception as err:
                        print(f"   * {t:<28} : [Error: {err}]")
            conn.close()
        except Exception as e:
            print(f"   [Error opening database: {e}]")

    print("\n" + "=" * 75)
    print("All writes made through the UI and REST APIs immediately update these files.")
    print("=" * 75)

if __name__ == "__main__":
    inspect()
