import sqlite3

conn = sqlite3.connect('database/institute.db')
c = conn.cursor()

# Fix students updated_at
c.execute("UPDATE students SET updated_at = '2026-10-29 14:15:21' WHERE updated_at LIKE '179%'")
print('Updated students:', c.rowcount)

# Fix hackathons dates
for col in ['start_date', 'end_date', 'registration_deadline']:
    c.execute(f"UPDATE hackathons SET {col} = {col} || ' 00:00:00' WHERE length({col}) = 10")
    print(f'Updated hackathons {col}:', c.rowcount)

# Fix internships deadline
c.execute("UPDATE internships SET deadline = deadline || ' 00:00:00' WHERE length(deadline) = 10")
print('Updated internships deadline:', c.rowcount)

# Fix offcampus_jobs deadline
c.execute("UPDATE offcampus_jobs SET deadline = deadline || ' 00:00:00' WHERE length(deadline) = 10")
print('Updated offcampus_jobs deadline:', c.rowcount)

# Check all other date columns across all tables in institute.db
for tbl in ['drives', 'drive_rounds', 'crt_sessions', 'class_sessions']:
    info = c.execute(f"PRAGMA table_info({tbl})").fetchall()
    for col in info:
        name = col[1]
        if 'date' in name:
            c.execute(f"UPDATE {tbl} SET {name} = {name} || ' 00:00:00' WHERE length({name}) = 10")
            if c.rowcount > 0:
                print(f"Updated {tbl}.{name}: {c.rowcount}")

conn.commit()
conn.close()
print("Done fixing dates in institute.db")
