import sqlite3

def fix_timestamps():
    conn = sqlite3.connect('./data/placement.db')
    cur = conn.cursor()

    # Format applied_at, created_at, updated_at with .000
    cur.execute("""
        UPDATE placement_applications
        SET applied_at = strftime('%Y-%m-%d %H:%M:%S.000', 'now', '-10 days'),
            created_at = strftime('%Y-%m-%d %H:%M:%S.000', 'now', '-10 days'),
            updated_at = strftime('%Y-%m-%d %H:%M:%S.000', 'now')
    """)

    cur.execute("""
        UPDATE placement_placed_students
        SET offered_at = strftime('%Y-%m-%d 10:00:00.000', 'now', '-5 days'),
            created_at = strftime('%Y-%m-%d 10:00:00.000', 'now', '-5 days')
    """)

    cur.execute("""
        UPDATE placement_stages
        SET created_at = strftime('%Y-%m-%d %H:%M:%S.000', 'now')
    """)

    cur.execute("""
        UPDATE placement_attendance
        SET check_in_time = strftime('%Y-%m-%d 09:30:00.000', 'now', '-1 day'),
            created_at = strftime('%Y-%m-%d %H:%M:%S.000', 'now', '-1 day')
    """)

    cur.execute("""
        UPDATE placement_results
        SET marked_at = strftime('%Y-%m-%d %H:%M:%S.000', 'now', '-1 day'),
            created_at = strftime('%Y-%m-%d %H:%M:%S.000', 'now', '-1 day')
    """)

    cur.execute("""
        UPDATE placement_talks
        SET scheduled_at = strftime('%Y-%m-%d 15:00:00.000', 'now', '+2 days'),
            created_at = strftime('%Y-%m-%d %H:%M:%S.000', 'now')
    """)

    cur.execute("""
        UPDATE placement_talk_questions
        SET created_at = strftime('%Y-%m-%d %H:%M:%S.000', 'now')
    """)

    cur.execute("""
        UPDATE placement_live_updates
        SET created_at = strftime('%Y-%m-%d %H:%M:%S.000', 'now')
    """)

    cur.execute("""
        UPDATE placement_feedback
        SET created_at = strftime('%Y-%m-%d %H:%M:%S.000', 'now')
    """)

    cur.execute("""
        UPDATE placement_drives
        SET status = 'ONGOING'
        WHERE status = 'ACTIVE'
    """)

    conn.commit()
    conn.close()
    print("Placement timestamps and ONGOING drives successfully updated!")

if __name__ == '__main__':
    fix_timestamps()
