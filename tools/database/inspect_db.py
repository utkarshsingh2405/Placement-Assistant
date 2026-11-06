import sqlite3
import glob
import os

db_paths = glob.glob('./data/*.db') + glob.glob('./*/src/main/resources/data/*.db')
db_paths = list(set([os.path.normpath(p) for p in db_paths]))

for db in db_paths:
    if not os.path.exists(db):
        continue
    print(f"\n=================== DB: {db} ===================")
    conn = sqlite3.connect(db)
    cur = conn.cursor()
    cur.execute("SELECT name FROM sqlite_master WHERE type='table';")
    tables = [row[0] for row in cur.fetchall() if not row[0].startswith('sqlite_')]
    for t in tables:
        try:
            cur.execute(f'SELECT count(*) FROM "{t}"')
            cnt = cur.fetchone()[0]
            print(f"  {t}: {cnt} rows")
        except Exception as e:
            print(f"  {t}: error {e}")
    conn.close()
