import sqlite3

conn = sqlite3.connect('database/institute.db')
print("Students:", conn.execute("SELECT id, email, roll_no, name FROM students LIMIT 5").fetchall())
print("Hackathons:", conn.execute("SELECT id, name, organizer, mode, status, registration_deadline FROM hackathons LIMIT 2").fetchall())
print("Bookmarks:", conn.execute("SELECT * FROM bookmarks").fetchall())
conn.close()
