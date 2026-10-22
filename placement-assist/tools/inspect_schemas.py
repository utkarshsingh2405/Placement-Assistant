import sqlite3

def dump_schema(db_path):
    print(f"\n=================== {db_path} ===================")
    conn = sqlite3.connect(db_path)
    cur = conn.cursor()
    cur.execute("SELECT name FROM sqlite_master WHERE type='table';")
    tables = [row[0] for row in cur.fetchall() if not row[0].startswith('sqlite_') and not row[0].startswith('flyway')]
    for t in tables:
        print(f"\nTABLE: {t}")
        cur.execute(f"PRAGMA table_info({t});")
        for col in cur.fetchall():
            print(f"  {col[1]}: {col[2]}")
    conn.close()

dump_schema("./data/placement.db")
dump_schema("./data/institute.db")
