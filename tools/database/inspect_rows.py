import sqlite3

conn = sqlite3.connect('database/institute.db')
conn.row_factory = sqlite3.Row

print("=== HACKATHONS ===")
for r in conn.execute("SELECT * FROM hackathons LIMIT 2").fetchall():
    print(dict(r))

print("=== INTERNSHIPS ===")
for r in conn.execute("SELECT * FROM internships LIMIT 2").fetchall():
    print(dict(r))

print("=== OFFCAMPUS_JOBS ===")
for r in conn.execute("SELECT * FROM offcampus_jobs LIMIT 2").fetchall():
    print(dict(r))

conn.close()
