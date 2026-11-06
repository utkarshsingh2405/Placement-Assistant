import sqlite3
import os
import json
from datetime import datetime, timedelta

def seed_databases():
    bcrypt_pwd = "$2a$10$P6oomM8lmwWfvq9L5nEJQ.f950gtEjBqFchNrNOeV3MoH2mknkaEC" # password123
    now = datetime.now()
    now_str = now.strftime("%Y-%m-%d %H:%M:%S")

    # 30 Indian engineering students
    students_data = [
        (1, "Aarav Sharma", "21CS001", "aarav@college.edu", "+91 98765 43210", "CSE", 4, 9.42, 0, "https://linkedin.com/in/aarav-sharma", "https://github.com/aaravsharma", 1),
        (2, "Bhavya Patel", "21CS002", "bhavya@college.edu", "+91 98765 43211", "CSE", 4, 9.15, 0, "https://linkedin.com/in/bhavyapatel", "https://github.com/bhavyapatel", 1),
        (3, "Chaitanya Reddy", "21CS003", "chaitanya@college.edu", "+91 98765 43212", "CSE", 4, 8.84, 0, "https://linkedin.com/in/chaitanya-r", "https://github.com/chaitanyareddy", 1),
        (4, "Divya Nair", "21CS004", "divya@college.edu", "+91 98765 43213", "CSE", 4, 8.92, 0, "https://linkedin.com/in/divyanair", "https://github.com/divyanair", 1),
        (5, "Eshaan Verma", "21CS005", "eshaan@college.edu", "+91 98765 43214", "CSE", 4, 7.85, 0, "https://linkedin.com/in/eshaanv", "https://github.com/eshaanverma", 1),
        (6, "Fatima Khan", "21CS006", "fatima@college.edu", "+91 98765 43215", "CSE", 4, 8.65, 0, "https://linkedin.com/in/fatimakhan", "https://github.com/fatimakhan", 1),
        (7, "Gaurav Gupta", "21CS007", "gaurav@college.edu", "+91 98765 43216", "CSE", 4, 7.42, 1, "https://linkedin.com/in/gauravg", "https://github.com/gauravgupta", 1),
        (8, "Harshita Joshi", "21CS008", "harshita@college.edu", "+91 98765 43217", "CSE", 4, 9.05, 0, "https://linkedin.com/in/harshitaj", "https://github.com/harshitajoshi", 1),
        (9, "Ishaan Malhotra", "21CS009", "ishaan@college.edu", "+91 98765 43218", "CSE", 4, 8.35, 0, "https://linkedin.com/in/ishaanm", "https://github.com/ishaanmalhotra", 1),
        (10, "Jaya Sen", "21CS010", "jaya@college.edu", "+91 98765 43219", "CSE", 4, 8.78, 0, "https://linkedin.com/in/jayasen", "https://github.com/jayasen", 1),
        (11, "Karthik Raja", "21IT001", "karthik@college.edu", "+91 98765 43220", "IT", 4, 9.25, 0, "https://linkedin.com/in/karthikraja", "https://github.com/karthikraja", 1),
        (12, "Lavanya Iyer", "21IT002", "lavanya@college.edu", "+91 98765 43221", "IT", 4, 8.95, 0, "https://linkedin.com/in/lavanyaiyer", "https://github.com/lavanyaiyer", 1),
        (13, "Manoj Kumar", "21IT003", "manoj@college.edu", "+91 98765 43222", "IT", 4, 7.60, 0, "https://linkedin.com/in/manojk", "https://github.com/manojkumar", 1),
        (14, "Neha Deshmukh", "21IT004", "neha@college.edu", "+91 98765 43223", "IT", 4, 8.80, 0, "https://linkedin.com/in/nehadeshmukh", "https://github.com/nehadeshmukh", 1),
        (15, "Omkar Bhosle", "21IT005", "omkar@college.edu", "+91 98765 43224", "IT", 4, 8.12, 0, "https://linkedin.com/in/omkarb", "https://github.com/omkarbhosle", 1),
        (16, "Pooja Hegde", "21EC001", "pooja@college.edu", "+91 98765 43225", "ECE", 4, 8.90, 0, "https://linkedin.com/in/poojahegde", "https://github.com/poojahegde", 1),
        (17, "Rahul Mehta", "21EC002", "rahul@college.edu", "+91 98765 43226", "ECE", 4, 7.75, 1, "https://linkedin.com/in/rahulm", "https://github.com/rahulmehta", 1),
        (18, "Sneha Pillai", "21EC003", "sneha@college.edu", "+91 98765 43227", "ECE", 4, 9.30, 0, "https://linkedin.com/in/snehapillai", "https://github.com/snehapillai", 1),
        (19, "Tanmay Bhatia", "21EC004", "tanmay@college.edu", "+91 98765 43228", "ECE", 4, 8.45, 0, "https://linkedin.com/in/tanmayb", "https://github.com/tanmaybhatia", 1),
        (20, "Urvashi Raut", "21EC005", "urvashi@college.edu", "+91 98765 43229", "ECE", 4, 8.60, 0, "https://linkedin.com/in/urvashir", "https://github.com/urvashiraut", 1),
        (21, "Varun Dhawan", "21EE001", "varun@college.edu", "+91 98765 43230", "EEE", 4, 7.90, 0, "https://linkedin.com/in/varund", "https://github.com/varundhawan", 1),
        (22, "Yashika Jain", "21EE002", "yashika@college.edu", "+91 98765 43231", "EEE", 4, 8.85, 0, "https://linkedin.com/in/yashikaj", "https://github.com/yashikajain", 1),
        (23, "Zainab Ali", "21ME001", "zainab@college.edu", "+91 98765 43232", "MECH", 4, 8.20, 0, "https://linkedin.com/in/zainaba", "https://github.com/zainabali", 1),
        (24, "Ananya Sharma", "21CS011", "ananya.sharma@college.edu", "+91 98765 43233", "CSE", 4, 9.55, 0, "https://linkedin.com/in/ananyasharma", "https://github.com/ananyasharma", 1),
        (25, "Rohan Sen", "21CS012", "rohan.sen@college.edu", "+91 98765 43234", "CSE", 4, 8.10, 0, "https://linkedin.com/in/rohans", "https://github.com/rohansen", 1),
        (26, "Aditi Rao", "21IT006", "aditi.rao@college.edu", "+91 98765 43235", "IT", 4, 9.10, 0, "https://linkedin.com/in/aditirao", "https://github.com/aditirao", 1),
        (27, "Vikram Singhania", "21EC006", "vikram.s@college.edu", "+91 98765 43236", "ECE", 4, 8.40, 0, "https://linkedin.com/in/vikrams", "https://github.com/vikramsingh", 1),
        (28, "Meera Nambiar", "21CS013", "meera.n@college.edu", "+91 98765 43237", "CSE", 4, 8.75, 0, "https://linkedin.com/in/meeran", "https://github.com/meeranambiar", 1),
        (29, "Siddharth Roy", "21IT007", "siddharth.r@college.edu", "+91 98765 43238", "IT", 4, 7.30, 2, "https://linkedin.com/in/siddharthr", "https://github.com/siddharthroy", 0),
        (30, "Pranav Anand", "21ME002", "pranav.a@college.edu", "+91 98765 43239", "MECH", 4, 8.15, 0, "https://linkedin.com/in/pranava", "https://github.com/pranavanand", 1)
    ]

    # ==========================================
    # 1. SEED portal.db
    # ==========================================
    print("Seeding portal.db...")
    pconn = sqlite3.connect("./data/portal.db")
    pcur = pconn.cursor()

    # Core staff users
    core_users = [
        (1, "admin@college.edu", bcrypt_pwd, "ADMIN", "System Administrator", None, None, 1),
        (2, "tpo@college.edu", bcrypt_pwd, "TPO", "Prof. Rajesh Sharma", None, None, 1),
        (3, "director@college.edu", bcrypt_pwd, "DIRECTOR", "Dr. V. K. Rao", None, None, 1),
        (4, "hr@google.com", bcrypt_pwd, "COMPANY", "Priya Menon (Google HR)", None, 1, 1),
        (5, "hr@microsoft.com", bcrypt_pwd, "COMPANY", "Rohan Roy (Microsoft IDC)", None, 2, 1),
        (6, "hr@amazon.com", bcrypt_pwd, "COMPANY", "Kavita Sethi (Amazon AWS)", None, 3, 1),
    ]

    for uid, email, pwd, role, name, roll, cid, act in core_users:
        pcur.execute("SELECT id FROM portal_users WHERE email = ?", (email,))
        row = pcur.fetchone()
        if row:
            pcur.execute("""
                UPDATE portal_users SET role=?, name=?, roll_no=?, company_id=?, active=1, updated_at=?
                WHERE email=?
            """, (role, name, roll, cid, now_str, email))
        else:
            pcur.execute("""
                INSERT INTO portal_users (email, password_hash, role, name, roll_no, company_id, active, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, (email, pwd, role, name, roll, cid, act, now_str, now_str))

    # Student portal users
    for s in students_data:
        pcur.execute("SELECT id FROM portal_users WHERE email = ?", (s[3],))
        row = pcur.fetchone()
        if row:
            pcur.execute("""
                UPDATE portal_users SET role='STUDENT', name=?, roll_no=?, active=1, updated_at=?
                WHERE email=?
            """, (s[1], s[2], now_str, s[3]))
        else:
            pcur.execute("""
                INSERT INTO portal_users (email, password_hash, role, name, roll_no, company_id, active, created_at, updated_at)
                VALUES (?, ?, 'STUDENT', ?, ?, NULL, 1, ?, ?)
            """, (s[3], bcrypt_pwd, s[1], s[2], now_str, now_str))

    pconn.commit()
    pconn.close()

    # ==========================================
    # 2. SEED institute.db
    # ==========================================
    print("Seeding institute.db...")
    iconn = sqlite3.connect("./data/institute.db")
    icur = iconn.cursor()

    # Insert staff users into institute users table
    for uid, email, pwd, role, name, roll, cid, act in core_users:
        icur.execute("SELECT id FROM users WHERE email = ?", (email,))
        row = icur.fetchone()
        if row:
            icur.execute("UPDATE users SET role=?, updated_at=? WHERE email=?", (role, now_str, email))
        else:
            icur.execute("""
                INSERT INTO users (email, password_hash, role, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?)
            """, (email, pwd, role, now_str, now_str))

    for s in students_data:
        icur.execute("SELECT id FROM users WHERE email = ?", (s[3],))
        row = icur.fetchone()
        if row:
            u_id = row[0]
            icur.execute("UPDATE users SET role='STUDENT', updated_at=? WHERE email=?", (now_str, s[3]))
        else:
            icur.execute("""
                INSERT INTO users (email, password_hash, role, created_at, updated_at)
                VALUES (?, ?, 'STUDENT', ?, ?)
            """, (s[3], bcrypt_pwd, now_str, now_str))
            u_id = icur.lastrowid

        # Insert/update student record
        icur.execute("""
            INSERT INTO students (id, user_id, name, roll_no, email, phone, branch, year, cgpa, backlogs, linkedin, github, placement_eligible, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET
                user_id=excluded.user_id, name=excluded.name, roll_no=excluded.roll_no, email=excluded.email,
                phone=excluded.phone, branch=excluded.branch, year=excluded.year,
                cgpa=excluded.cgpa, backlogs=excluded.backlogs, placement_eligible=excluded.placement_eligible
        """, (s[0], u_id, s[1], s[2], s[3], s[4], s[5], s[6], s[7], s[8], s[9], s[10], s[11], now_str, now_str))

    # Student skills
    skills_pool = [
        ["Java", "Spring Boot", "Microservices", "PostgreSQL", "Docker", "AWS", "DSA", "System Design"],
        ["Python", "Django", "FastAPI", "React", "TypeScript", "Redis", "Kafka", "Data Structures"],
        ["C++", "Competitive Programming", "DSA", "Algorithms", "Linux", "Git", "SQL"],
        ["React.js", "Next.js", "Node.js", "Express", "MongoDB", "TailwindCSS", "REST APIs"],
        ["Machine Learning", "PyTorch", "TensorFlow", "Pandas", "NumPy", "Scikit-Learn", "Computer Vision"],
        ["Embedded C", "IoT", "ARM Cortex", "Microcontrollers", "RTOS", "VLSI", "MATLAB"],
        ["DevOps", "Kubernetes", "CI/CD", "Terraform", "Ansible", "GCP", "Prometheus"]
    ]
    icur.execute("DELETE FROM student_skills")
    for s in students_data:
        selected_skills = skills_pool[(s[0] - 1) % len(skills_pool)]
        for sk in selected_skills:
            icur.execute("INSERT INTO student_skills (student_id, skill) VALUES (?, ?)", (s[0], sk))

    # Companies
    companies_data = [
        (1, "Google India", "https://google.com/careers", "Alphabet subsidiary, global leader in search, AI, cloud computing.", "Technology / AI", "Bengaluru, India", "/assets/companies/google.png"),
        (2, "Microsoft IDC", "https://careers.microsoft.com", "Empowering every person and organization on the planet to achieve more.", "Software & Cloud", "Hyderabad, India", "/assets/companies/microsoft.png"),
        (3, "Amazon AWS", "https://amazon.jobs", "Earth's most customer-centric company, leading e-commerce and cloud infrastructure.", "E-Commerce / Cloud", "Bengaluru & Hyderabad", "/assets/companies/amazon.png"),
        (4, "TCS Digital", "https://tcs.com/careers", "Tata Consultancy Services premium digital and R&D engineering tier.", "IT & Consulting", "Pan India", "/assets/companies/tcs.png"),
        (5, "Infosys Springboard", "https://infosys.com", "Specialist Programmer (SP) and Digital Specialist Engineer (DSE) flagship roles.", "IT Services", "Bengaluru / Pune", "/assets/companies/infosys.png"),
        (6, "Goldman Sachs", "https://goldmansachs.com/careers", "Leading global investment banking, securities and investment management firm.", "FinTech / Banking", "Bengaluru, India", "/assets/companies/goldman.png"),
        (7, "Cisco Systems", "https://cisco.com/careers", "Worldwide leader in networking, security, and cloud collaborative solutions.", "Networking / Systems", "Bengaluru, India", "/assets/companies/cisco.png"),
        (8, "Deloitte USI", "https://deloitte.com", "Technology consulting, risk advisory, enterprise cloud transformation.", "Consulting", "Hyderabad / Bengaluru", "/assets/companies/deloitte.png"),
        (9, "Oracle Cloud", "https://oracle.com/cloud", "Enterprise cloud applications and platform services for mission-critical apps.", "Cloud / Database", "Bengaluru, India", "/assets/companies/oracle.png"),
        (10, "Adobe Systems", "https://adobe.com/careers", "Creative Cloud, Document Cloud, and Experience Cloud digital experiences.", "Product / Creative AI", "Noida / Bengaluru", "/assets/companies/adobe.png")
    ]
    for c in companies_data:
        icur.execute("""
            INSERT INTO companies (id, name, website, about, industry, headquarters, logo_url, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET name=excluded.name, about=excluded.about, industry=excluded.industry
        """, (c[0], c[1], c[2], c[3], c[4], c[5], c[6], now_str, now_str))

    # Master Drives in Institute
    drives_data = [
        (1, 1, "Google Campus Recruitment 2026", "2026-11-15", "2026-11-10", 8.5, "CSE,IT,ECE", 0, "Software Engineer (SDE-1), Site Reliability Engineer", 32.0, 44.0, "Bengaluru / Hyderabad", "ACTIVE", "Elite campus hiring for core algorithmic problem solvers and systems developers."),
        (2, 2, "Microsoft IDC Engineering Hiring", "2026-11-20", "2026-11-12", 8.0, "CSE,IT,ECE,EEE", 0, "Software Development Engineer (SDE), Cloud Engineer", 28.0, 42.0, "Hyderabad / Noida", "ACTIVE", "Recruiting for Azure Core, Microsoft 365, and Developer Tools teams."),
        (3, 3, "Amazon AWS SDE Campus Drive", "2026-11-25", "2026-11-16", 7.5, "CSE,IT,ECE", 0, "Software Development Engineer 1 (AWS)", 24.0, 36.0, "Bengaluru / Chennai", "ACTIVE", "Hiring builders with obsession for customer experience and high-scale systems."),
        (4, 4, "TCS Digital National Qualifier Test", "2026-12-01", "2026-11-22", 6.5, "CSE,IT,ECE,EEE,MECH,CIVIL", 1, "Digital Software Engineer, Systems Engineer", 7.5, 11.5, "Pan India", "ACTIVE", "Digital tier offering cutting-edge project placement in AI, Cloud, and Automation."),
        (5, 5, "Infosys Specialist Programmer (SP)", "2026-12-05", "2026-11-25", 7.0, "CSE,IT,ECE", 0, "Specialist Programmer (SP), Digital Specialist Engineer (DSE)", 9.5, 15.0, "Bengaluru / Pune", "ACTIVE", "Elite competitive programming track with accelerated growth and fast-track promotions."),
        (6, 6, "Goldman Sachs Engineering Campus Drive", "2026-12-10", "2026-11-28", 8.0, "CSE,IT,ECE,EEE", 0, "New Analyst - Quantitative & Software Engineering", 22.0, 32.0, "Bengaluru", "UPCOMING", "FinTech algorithmic trading, risk analytics, and enterprise data platforms."),
        (7, 7, "Cisco Systems Network Software Drive", "2026-12-15", "2026-12-02", 7.5, "CSE,IT,ECE,EEE", 0, "Software Engineer - Networking & Cloud Security", 18.0, 26.0, "Bengaluru", "UPCOMING", "Core networking kernels, SDN, edge computing and cloud telemetry systems.")
    ]
    for d in drives_data:
        icur.execute("""
            INSERT INTO drives (id, company_id, title, drive_date, last_date_to_apply, eligibility_cgpa, eligible_branches, max_backlogs, roles, package_min, package_max, location, status, description, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET title=excluded.title, status=excluded.status, package_min=excluded.package_min, package_max=excluded.package_max
        """, (d[0], d[1], d[2], d[3], d[4], d[5], d[6], d[7], d[8], d[9], d[10], d[11], d[12], d[13], now_str, now_str))

    # Class Sessions
    class_sessions_data = [
        (1, "CS801 - Distributed Systems & Cloud", "Prof. Sneha Rao", (now - timedelta(days=1)).strftime("%Y-%m-%d"), "09:00:00", "10:00:00", "CSE", 4, "A"),
        (2, "CS802 - Deep Learning & Neural Nets", "Dr. Amit Roy", (now - timedelta(days=1)).strftime("%Y-%m-%d"), "10:15:00", "11:15:00", "CSE", 4, "A"),
        (3, "CS803 - Advanced Software Engineering", "Prof. Neeraj Patel", (now - timedelta(days=2)).strftime("%Y-%m-%d"), "11:30:00", "12:30:00", "CSE", 4, "A"),
        (4, "IT801 - Information Security & Cryptography", "Dr. Kavita Shenoy", (now - timedelta(days=1)).strftime("%Y-%m-%d"), "09:00:00", "10:00:00", "IT", 4, "A"),
        (5, "IT802 - Cloud Infrastructure & DevOps", "Prof. S. Nambiar", (now - timedelta(days=2)).strftime("%Y-%m-%d"), "14:00:00", "15:00:00", "IT", 4, "A"),
        (6, "EC801 - Embedded Systems & RTOS", "Dr. R. Sundaram", (now - timedelta(days=1)).strftime("%Y-%m-%d"), "10:00:00", "11:00:00", "ECE", 4, "A"),
        (7, "EC802 - Wireless Sensor Networks", "Prof. Priya Nair", (now - timedelta(days=3)).strftime("%Y-%m-%d"), "11:15:00", "12:15:00", "ECE", 4, "A"),
        (8, "CS804 - Big Data Analytics & Spark", "Dr. Amit Roy", now.strftime("%Y-%m-%d"), "09:00:00", "10:00:00", "CSE", 4, "A"),
        (9, "CS805 - Compiler Design & LLVM", "Prof. Neeraj Patel", now.strftime("%Y-%m-%d"), "11:30:00", "12:30:00", "CSE", 4, "B"),
        (10, "EE801 - Power Electronics & Drives", "Prof. V. Sharma", (now - timedelta(days=1)).strftime("%Y-%m-%d"), "14:00:00", "15:00:00", "EEE", 4, "A")
    ]
    for cs in class_sessions_data:
        icur.execute("""
            INSERT INTO class_sessions (id, subject, faculty, session_date, start_time, end_time, branch, year, section, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET subject=excluded.subject, faculty=excluded.faculty, session_date=excluded.session_date
        """, (cs[0], cs[1], cs[2], cs[3], cs[4], cs[5], cs[6], cs[7], cs[8], now_str, now_str))

    # CRT Sessions
    crt_sessions_data = [
        (1, "Dynamic Programming & Graph Theory", "Advanced Algorithms", "Dr. Rajesh Sharma", (now - timedelta(days=1)).strftime("%Y-%m-%d"), "14:00:00", "17:00:00", "Auditorium Hall 1"),
        (2, "System Design & Microservices Architecture", "High Scale Architecture", "Prof. Aniket Verma", (now - timedelta(days=2)).strftime("%Y-%m-%d"), "14:00:00", "16:30:00", "Computer Lab 4"),
        (3, "Quantitative Aptitude & Speed Math", "Aptitude Round Mastery", "Trainer Meenakshi Sundaram", (now - timedelta(days=3)).strftime("%Y-%m-%d"), "09:30:00", "12:30:00", "Auditorium Hall 2"),
        (4, "Verbal Ability & Technical Communication", "Corporate Communication", "Dr. Shalini Saxena", (now - timedelta(days=4)).strftime("%Y-%m-%d"), "10:00:00", "12:00:00", "Seminar Room A"),
        (5, "Mock Technical Interviews & DSA Drill", "Live Coding Practice", "Industry Panel Alumni", (now - timedelta(days=5)).strftime("%Y-%m-%d"), "13:30:00", "17:30:00", "Innovation Lab"),
        (6, "Company-Specific NQT & Online Assessment Drill", "TCS/Infosys Test Patterns", "Prof. R. Deshmukh", now.strftime("%Y-%m-%d"), "14:00:00", "16:00:00", "Central Computing Facility")
    ]
    for crt in crt_sessions_data:
        icur.execute("""
            INSERT INTO crt_sessions (id, title, topic, trainer, session_date, start_time, end_time, venue, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET title=excluded.title, topic=excluded.topic, trainer=excluded.trainer
        """, (crt[0], crt[1], crt[2], crt[3], crt[4], crt[5], crt[6], crt[7], now_str, now_str))

    # Attendance Records (Mark for each student across sessions)
    icur.execute("DELETE FROM attendances")
    att_id = 1
    # For class sessions
    for cs in class_sessions_data:
        target_branch = cs[6]
        matching_students = [s for s in students_data if s[5] == target_branch]
        for s in matching_students:
            # 90% present, 10% absent/late
            status = "PRESENT" if (s[0] + cs[0]) % 7 != 0 else "ABSENT"
            icur.execute("""
                INSERT INTO attendances (id, student_id, session_type, class_session_id, status, marked_by, created_at, updated_at)
                VALUES (?, ?, 'CLASS', ?, ?, 'Prof. Attendance Staff', ?, ?)
            """, (att_id, s[0], cs[0], status, now_str, now_str))
            att_id += 1

    # For CRT sessions (open to all students)
    for crt in crt_sessions_data:
        for s in students_data[:24]: # first 24 students attend CRT
            status = "PRESENT" if (s[0] + crt[0]) % 9 != 0 else "ABSENT"
            icur.execute("""
                INSERT INTO attendances (id, student_id, session_type, crt_session_id, status, marked_by, created_at, updated_at)
                VALUES (?, ?, 'CRT', ?, ?, 'CRT Lead Trainer', ?, ?)
            """, (att_id, s[0], crt[0], status, now_str, now_str))
            att_id += 1

    # Question Bank
    questions_data = [
        (1, 1, "Software Engineer", "CODING", "HARD", "Given an array of integers representing asteroid sizes, find the state of asteroids after all collisions.", "Use a stack to simulate collisions between right-moving and left-moving asteroids.", "Stack, Simulation, Array", 2025),
        (2, 1, "Software Engineer", "TECHNICAL_INTERVIEW", "HARD", "Design a Distributed Key-Value store with strong consistency and partition tolerance (Raft/Paxos).", "Explain Raft consensus leader election, log replication, commit index, and heartbeats.", "System Design, Distributed Systems, Raft", 2025),
        (3, 2, "Cloud Engineer", "CODING", "MEDIUM", "Find the median of two sorted arrays of different sizes in O(log(min(n, m))) time.", "Binary search partition on the smaller array ensuring left halves equal right halves.", "Binary Search, Array, Divide and Conquer", 2025),
        (4, 2, "Software Engineer", "TECHNICAL_INTERVIEW", "MEDIUM", "Explain how virtual memory and page tables work in modern 64-bit operating systems.", "Multi-level page tables, TLB cache, page faults, CR3 register, and page replacement algorithms.", "OS, Memory Management, Architecture", 2024),
        (5, 3, "Software Development Engineer", "CODING", "MEDIUM", "Implement an LRU (Least Recently Used) cache with O(1) get and put operations.", "Combine a doubly linked list with a Hash Map storing node pointers for O(1) access and removal.", "Design, Hash Table, Linked List", 2025),
        (6, 3, "Software Development Engineer", "HR", "EASY", "Tell me about a time you had a conflicting deadline between two critical deliverables and how you prioritized.", "Follow STAR framework: explain the high-stake context, ownership, data-driven prioritization, and final successful delivery.", "Amazon Leadership Principles, Ownership, Bias for Action", 2025),
        (7, 4, "Digital Engineer", "CODING", "EASY", "Find the lowest common ancestor (LCA) of two given nodes in a Binary Search Tree (BST).", "Traverse from root: if both p and q are less, go left; if both greater, go right; else current node is LCA.", "Tree, BST, Recursion", 2024),
        (8, 5, "Specialist Programmer", "CODING", "HARD", "Find the maximum sum of non-adjacent elements in a circular array (House Robber II).", "Run DP twice: first excluding the last house, second excluding the first house, return maximum.", "Dynamic Programming, Array", 2025)
    ]
    for q in questions_data:
        icur.execute("""
            INSERT INTO questions (id, drive_id, role, round_type, difficulty, question, answer, tags, asked_year, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET question=excluded.question, answer=excluded.answer
        """, (q[0], q[1], q[2], q[3], q[4], q[5], q[6], q[7], q[8], now_str, now_str))

    # Resumes & ATS
    for s in students_data[:15]:
        ats_val = 82 + (s[0] * 3) % 15
        breakdown = json.dumps({
            "keywords": 88,
            "format": 92,
            "experience": ats_val - 5,
            "education": 95,
            "skills": 90
        })
        icur.execute("""
            INSERT INTO resumes (id, student_id, file_url, version, status, ats_score, ats_breakdown, feedback, reviewed_by, created_at, updated_at)
            VALUES (?, ?, ?, 1, 'APPROVED', ?, ?, 'Strong action verbs and clear quantified project impact.', 'Prof. Rajesh Sharma (TPO)', ?, ?)
            ON CONFLICT(id) DO UPDATE SET ats_score=excluded.ats_score, ats_breakdown=excluded.ats_breakdown
        """, (s[0], s[0], f"/uploads/resumes/{s[2]}_resume_v1.pdf", ats_val, breakdown, now_str, now_str))

    # Hackathons
    hackathons_data = [
        (1, "Smart India Hackathon (SIH) 2026", "Ministry of Education & AICTE", "HYBRID", "2026-11-20", "2026-11-22", "2026-11-05", "INR 1,00,000 per Problem Statement", 6, "New Delhi & Nodal Centers", "https://sih.gov.in", "India's largest nationwide digital initiative to solve real governance challenges.", "Submit 3-page idea abstract via Institute SPOC portal.", "UPCOMING"),
        (2, "Google Solution Challenge 2026", "Google Developer Student Clubs", "ONLINE", "2026-12-01", "2026-12-15", "2026-11-25", "$10,000 USD + Mentorship from Google Engineers", 4, "Global Online", "https://developers.google.com/community/gdsc-solution-challenge", "Build a solution for one or more UN Sustainable Development Goals using Google tech.", "Submit demo video and open-source GitHub repository.", "ACTIVE"),
        (3, "Flipkart GRiD 6.0 Engineering Track", "Flipkart Campus Relations", "ONLINE", "2026-11-18", "2026-11-28", "2026-11-10", "INR 5,00,000 + Direct PPI for SDE-1", 3, "Unstop Platform", "https://unstop.com/flipkart-grid", "E-commerce scale algorithmic challenge covering Robotics, GenAI, and Supply Chain.", "Register as a 3-member team and clear Round 1 Online Quiz.", "ACTIVE")
    ]
    for h in hackathons_data:
        icur.execute("""
            INSERT INTO hackathons (id, name, organizer, mode, start_date, end_date, registration_deadline, prize, team_size, location, link, description, how_to_apply, status, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET name=excluded.name, prize=excluded.prize, status=excluded.status
        """, (h[0], h[1], h[2], h[3], h[4], h[5], h[6], h[7], h[8], h[9], h[10], h[11], h[12], h[13], now_str, now_str))

    # Internships
    internships_data = [
        (1, "Google India", "Summer Software Engineering Intern 2027", 115000.0, 3, "Bengaluru / Hyderabad", "HYBRID", "Pre-final & Final year B.Tech students with strong DSA", "2026-11-30", "https://google.com/careers/internships", "Apply via Careers site with campus referral token.", "C++, Java, Python, Algorithms, Data Structures", "ACTIVE"),
        (2, "Microsoft IDC", "Software Development Engineer Intern", 100000.0, 2, "Hyderabad", "IN_PERSON", "Minimum 8.0 CGPA with no active backlogs", "2026-11-25", "https://careers.microsoft.com", "Campus placement cell direct nomination.", "Data Structures, System Fundamentals, Azure, C#", "ACTIVE"),
        (3, "Amazon AWS", "Cloud Support Associate Intern", 80000.0, 6, "Bengaluru", "HYBRID", "B.Tech CSE/IT/ECE with networking fundamentals", "2026-12-05", "https://amazon.jobs", "Apply on Amazon Jobs portal with campus ID.", "Linux, Networking, Cloud Architecture, Python", "ACTIVE")
    ]
    for it in internships_data:
        icur.execute("""
            INSERT INTO internships (id, company, role, stipend, duration_months, location, mode, eligibility, deadline, apply_url, how_to_apply, skills_required, status, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET company=excluded.company, role=excluded.role, stipend=excluded.stipend
        """, (it[0], it[1], it[2], it[3], it[4], it[5], it[6], it[7], it[8], it[9], it[10], it[11], it[12], now_str, now_str))

    # Off-Campus Jobs
    jobs_data = [
        (1, "Adobe Systems", "Software Development Engineer 1 (Full-Time)", 24.5, "Noida / Bengaluru", "2026 Batch", "0-1 Years", "C++, WebGL, Computer Graphics, DSA", "2026-12-15", "https://adobe.wd5.myworkdayjobs.com", "Submit application on Adobe Workday portal with resume and project links.", "ACTIVE"),
        (2, "Atlassian", "Associate Software Engineer", 26.0, "Remote / Bengaluru", "2026 Batch", "Fresher", "Java, Kotlin, React, Microservices", "2026-12-20", "https://atlassian.com/company/careers", "Online coding assessment followed by 3 rounds of system design and pair programming.", "ACTIVE"),
        (3, "Razorpay", "Frontend Engineer 1 (Core Checkout)", 18.0, "Bengaluru", "2026 Batch", "Fresher", "React, TypeScript, Web Performance, CSS Architecture", "2026-11-28", "https://razorpay.com/jobs", "Direct application via Razorpay careers page.", "ACTIVE")
    ]
    for j in jobs_data:
        icur.execute("""
            INSERT INTO offcampus_jobs (id, company, role, package_lpa, location, batch_eligible, experience, skills_required, deadline, apply_url, how_to_apply, status, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET company=excluded.company, role=excluded.role, package_lpa=excluded.package_lpa
        """, (j[0], j[1], j[2], j[3], j[4], j[5], j[6], j[7], j[8], j[9], j[10], j[11], now_str, now_str))

    iconn.commit()
    iconn.close()

    # ==========================================
    # 3. SEED placement.db
    # ==========================================
    print("Seeding placement.db...")
    plconn = sqlite3.connect("./data/placement.db")
    plcur = plconn.cursor()

    # Placement Companies
    for c in companies_data:
        plcur.execute("""
            INSERT INTO placement_companies (id, name, website, logo_url, industry, description, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET name=excluded.name, industry=excluded.industry, description=excluded.description
        """, (c[0], c[1], c[2], c[6], c[4], c[3], now_str, now_str))

    # Placement Drives
    p_drives_data = [
        (1, 1, 1, "Google Campus Recruitment 2026", "Elite campus recruitment drive for Google India engineering labs.", "Software Engineer (SDE-1), Site Reliability Engineer", "CTC: 32.0 - 44.0 LPA (Base: 18L + RSUs + Joining Bonus)", "ACTIVE", "2026-11-15"),
        (2, 2, 2, "Microsoft IDC Engineering Hiring", "Full-time software engineering roles across Azure and Office 365.", "Software Development Engineer (SDE)", "CTC: 28.0 - 42.0 LPA", "ACTIVE", "2026-11-20"),
        (3, 3, 3, "Amazon AWS SDE Campus Drive", "Building planetary scale cloud infrastructure and retail services.", "Software Development Engineer 1 (AWS)", "CTC: 24.0 - 36.0 LPA", "ACTIVE", "2026-11-25"),
        (4, 4, 4, "TCS Digital National Qualifier Test", "Premium software engineering and R&D tier at TCS.", "Digital Software Engineer", "CTC: 7.5 - 11.5 LPA", "ACTIVE", "2026-12-01"),
        (5, 5, 5, "Infosys Specialist Programmer (SP)", "Flagship competitive programming track at Infosys.", "Specialist Programmer (SP)", "CTC: 9.5 - 15.0 LPA", "ACTIVE", "2026-12-05"),
        (6, 6, 6, "Goldman Sachs Engineering Drive", "Quantitative analytics and high-throughput trading platforms.", "New Analyst - Engineering", "CTC: 22.0 - 32.0 LPA", "UPCOMING", "2026-12-10")
    ]
    for pd in p_drives_data:
        plcur.execute("""
            INSERT INTO placement_drives (id, institute_drive_id, company_id, title, description, job_roles, package_details, status, drive_date, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET title=excluded.title, status=excluded.status, package_details=excluded.package_details
        """, (pd[0], pd[1], pd[2], pd[3], pd[4], pd[5], pd[6], pd[7], pd[8], now_str, now_str))

    # Placement Stages for each drive
    stages_data = [
        # Drive 1 (Google)
        (1, 1, "Online Coding Assessment", 1, "ONLINE_TEST", "COMPLETED", "HackerRank Platform (Invited via Email)", "90 minutes, 2 algorithmic problems. Strict camera proctoring.", "2026-11-15 10:00:00", "2026-11-15 11:30:00"),
        (2, 1, "Technical Interview Round 1", 2, "TECHNICAL", "IN_PROGRESS", "Google Meet Room A-04", "Data Structures & Algorithms problem solving. Live Google Docs coding.", "2026-11-16 10:00:00", "2026-11-16 11:00:00"),
        (3, 1, "Technical Interview Round 2 - Systems", 3, "TECHNICAL", "UPCOMING", "Google Meet Room B-02", "System Design and Low-Level Object Oriented Architecture.", "2026-11-16 14:00:00", "2026-11-16 15:00:00"),
        (4, 1, "Googliness & Leadership Fit", 4, "HR", "UPCOMING", "Google Meet Room C-01", "Values, collaboration, navigating ambiguity, and cultural alignment.", "2026-11-17 11:00:00", "2026-11-17 11:45:00"),
        (5, 1, "Final Offer Rollout", 5, "FINAL_OFFER", "UPCOMING", "Placement Cell Office", "Confirmation of offer letters and pre-onboarding documentation.", "2026-11-18 16:00:00", "2026-11-18 17:00:00"),

        # Drive 2 (Microsoft)
        (6, 2, "Online Coding Assessment", 1, "ONLINE_TEST", "COMPLETED", "Codility Test Link", "3 Coding tasks in 100 minutes. Memory and runtime limits strictly enforced.", "2026-11-20 09:00:00", "2026-11-20 10:40:00"),
        (7, 2, "Technical Round 1 - Problem Solving", 2, "TECHNICAL", "IN_PROGRESS", "Microsoft Teams Room 1", "Binary trees, dynamic programming, and complexity trade-offs.", "2026-11-21 10:00:00", "2026-11-21 11:00:00"),
        (8, 2, "Technical Round 2 - Cloud Systems", 3, "TECHNICAL", "UPCOMING", "Microsoft Teams Room 2", "Distributed systems fundamentals, OS concepts, and concurrency.", "2026-11-21 14:00:00", "2026-11-21 15:00:00"),
        (9, 2, "Director & HR Round", 4, "HR", "UPCOMING", "Microsoft Teams Room 3", "Behavioral assessment with Engineering Director.", "2026-11-22 11:00:00", "2026-11-22 12:00:00"),

        # Drive 3 (Amazon)
        (10, 3, "Online Assessment (OA1 + OA2)", 1, "ONLINE_TEST", "COMPLETED", "Amazon Assessment Portal", "2 Coding problems + Amazon Work Style Simulation (90 mins).", "2026-11-25 10:00:00", "2026-11-25 11:30:00"),
        (11, 3, "Technical Bar Raiser Round", 2, "TECHNICAL", "UPCOMING", "Amazon Chime Room AWS-1", "Coding algorithms and Amazon Leadership Principles deep dive.", "2026-11-26 10:00:00", "2026-11-26 11:00:00"),
        (12, 3, "Hiring Manager Round", 3, "HR", "UPCOMING", "Amazon Chime Room AWS-2", "Architecture discussion, trade-offs, and final alignment.", "2026-11-26 14:00:00", "2026-11-26 15:00:00"),

        # Drive 4 (TCS Digital)
        (13, 4, "TCS NQT Cognitive & Coding", 1, "ONLINE_TEST", "COMPLETED", "TCS iON Test Center Hall A", "Quantitative, Verbal, Advanced Coding (Java/Python/C++).", "2026-12-01 09:00:00", "2026-12-01 12:00:00"),
        (14, 4, "TR + MR + HR Combined Panel", 2, "TECHNICAL", "IN_PROGRESS", "College Seminar Hall 2", "Technical, managerial, and HR comprehensive panel interview.", "2026-12-02 10:00:00", "2026-12-02 11:30:00"),

        # Drive 5 (Infosys SP)
        (15, 5, "HackWithInfy Grand Finale Coding", 1, "ONLINE_TEST", "COMPLETED", "Infosys Assessment Console", "3 Hard Competitive Programming problems (3 hours).", "2026-12-05 10:00:00", "2026-12-05 13:00:00"),
        (16, 5, "Technical Interview (SP Tier)", 2, "TECHNICAL", "UPCOMING", "Infosys Webex Room SP-1", "System design, advanced data structures, and project walkthrough.", "2026-12-06 11:00:00", "2026-12-06 12:00:00")
    ]
    for st in stages_data:
        plcur.execute("""
            INSERT INTO placement_stages (id, drive_id, stage_name, stage_order, stage_type, status, venue_or_link, instructions, start_time, end_time, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET
                stage_name=excluded.stage_name, status=excluded.status, venue_or_link=excluded.venue_or_link
        """, (st[0], st[1], st[2], st[3], st[4], st[5], st[6], st[7], st[8], st[9], now_str))

    # Applications
    plcur.execute("DELETE FROM placement_applications")
    app_id = 1
    # Distribute 30 students across drives
    for s in students_data:
        # Everyone applies to TCS and Infosys
        # Top students (CGPA >= 8.5) apply to Google, Microsoft, Amazon
        drives_to_apply = [4, 5]
        if s[7] >= 8.5:
            drives_to_apply.extend([1, 2, 3])
        elif s[7] >= 7.5:
            drives_to_apply.extend([2, 3])

        for did in drives_to_apply:
            ats = 80 + (s[0] * 4 + did * 3) % 18
            if did == 1:
                cur_stage = 2 if s[0] <= 8 else (1 if s[0] <= 14 else 1)
                status = "SHORTLISTED" if s[0] <= 8 else ("IN_REVIEW" if s[0] <= 14 else "REJECTED")
            elif did == 2:
                cur_stage = 7 if s[0] <= 10 else 6
                status = "SHORTLISTED" if s[0] <= 10 else "IN_REVIEW"
            elif did == 4:
                cur_stage = 14 if s[0] <= 18 else 13
                status = "SHORTLISTED" if s[0] <= 18 else "IN_REVIEW"
            else:
                cur_stage = 10 if did == 3 else 15
                status = "APPLIED"

            plcur.execute("""
                INSERT INTO placement_applications (id, drive_id, student_id, roll_no, student_name, email, status, current_stage_id, applied_at, resume_url, ats_score, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, (app_id, did, s[0], s[2], s[1], s[3], status, cur_stage, (now - timedelta(days=10)).strftime("%Y-%m-%d"), f"/uploads/resumes/{s[2]}_resume_v1.pdf", ats, now_str, now_str))
            app_id += 1

    # Pre-Placement Talks
    talks_data = [
        (1, 1, (now + timedelta(days=2)).strftime("%Y-%m-%d 16:00:00"), "Google Meet / Auditorium Hall 1", "https://docs.google.com/presentation/d/google-campus-ppt-2026", "https://youtube.com/watch?v=sample-google-ppt", "Overview of Engineering Culture at Google India, AI first initiatives, team matching process, and compensation breakdown.", "Priya Menon", "Staff Engineering Director, Google India"),
        (2, 2, (now + timedelta(days=3)).strftime("%Y-%m-%d 15:00:00"), "Microsoft Teams Link", "https://onedrive.live.com/view/msft-campus-deck-2026", "https://youtube.com/watch?v=sample-msft-ppt", "Life at Microsoft IDC: Building for billions on Azure, GitHub, and Surface engineering teams.", "Rohan Roy", "Principal Partner Group Manager, Microsoft"),
        (3, 3, (now + timedelta(days=4)).strftime("%Y-%m-%d 17:00:00"), "Amazon Chime Link / Main Auditorium", "https://amazon.aws/ppt-presentation-deck", "https://youtube.com/watch?v=sample-amazon-ppt", "Pioneering the Future of Cloud: 16 Leadership Principles in action and Day 1 culture.", "Kavita Sethi", "Director of Software Development, AWS"),
        (4, 4, (now - timedelta(days=2)).strftime("%Y-%m-%d 11:00:00"), "Auditorium Hall 2", "https://tcs.com/ppt-digital-2026", "https://youtube.com/watch?v=sample-tcs-ppt", "TCS Digital Transformation journey, global client architectures, and research labs.", "Sunil Varma", "Vice President - Technology, TCS")
    ]
    for tk in talks_data:
        plcur.execute("""
            INSERT INTO placement_talks (id, drive_id, scheduled_at, venue_or_url, presentation_url, recording_url, overview, speaker_name, speaker_title, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET
                overview=excluded.overview, speaker_name=excluded.speaker_name, speaker_title=excluded.speaker_title
        """, (tk[0], tk[1], tk[2], tk[3], tk[4], tk[5], tk[6], tk[7], tk[8], now_str))

    # Talk Questions
    plcur.execute("DELETE FROM placement_talk_questions")
    questions = [
        (1, 1, 1, "21CS001", "Aarav Sharma", "What tech stacks are most prominent for new grad SDE-1 roles at Google Bengaluru?", "We look for algorithmic depth rather than a single framework; teams use C++, Java, Go, and Python extensively across core services.", "Priya Menon (Google)"),
        (2, 1, 2, "21CS002", "Bhavya Patel", "Does Google offer rotation programs across different product areas for fresh hires?", "Yes, through our Engineering Residency and internal team mobility after 12-18 months of tenure.", "Priya Menon (Google)"),
        (3, 2, 11, "21IT001", "Karthik Raja", "Can interns convert to full-time before the 8th semester starts?", "Absolutely! Performance reviews happen at week 8 of the internship, and PPOs are rolled out promptly.", "Rohan Roy (Microsoft)"),
        (4, 3, 4, "21CS004", "Divya Nair", "How heavily are the Amazon Leadership Principles weighted in the bar raiser interview?", "50% of the bar raiser interview evaluates customer obsession, bias for action, and ownership.", "Kavita Sethi (AWS)")
    ]
    for tq in questions:
        plcur.execute("""
            INSERT INTO placement_talk_questions (id, talk_id, student_id, roll_no, student_name, question, answer, answered_by, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, (tq[0], tq[1], tq[2], tq[3], tq[4], tq[5], tq[6], tq[7], now_str))

    # Live Updates
    plcur.execute("DELETE FROM placement_live_updates")
    updates = [
        (1, 1, 2, "Google Technical Round 1 Shortlist Released", "Shortlisted candidates for Technical Interview Round 1 have received calendar invites. Check-in starts at 09:30 AM in Google Meet.", "SHORTLIST", "HIGH"),
        (2, 1, 2, "Google Interview Room Link Updated", "Candidates assigned to Panel B please use Room A-04 backup link provided on your hall ticket.", "VENUE", "MEDIUM"),
        (3, 2, 7, "Microsoft Coding Test Results Published", "Top 35 candidates from yesterday's Codility assessment have qualified for Round 1 interviews.", "SHORTLIST", "HIGH"),
        (4, 3, 10, "Amazon AWS OA2 Link Dispatched", "Work Style Simulation and coding round links dispatched to eligible students. Complete before 11:59 PM today.", "SCHEDULE", "HIGH"),
        (5, 4, 14, "TCS Digital In-Person Panel Schedule", "TR/MR/HR combined interview slots are posted on the notice board. Dress in formal business attire.", "SCHEDULE", "MEDIUM"),
        (6, 1, 1, "Campus Placement Policy Reminder from Director", "All students holding Tier-1 dream company offers must confirm acceptance within 24 hours of result declaration.", "GENERAL", "HIGH")
    ]
    for lu in updates:
        plcur.execute("""
            INSERT INTO placement_live_updates (id, drive_id, stage_id, title, message, category, priority, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """, (lu[0], lu[1], lu[2], lu[3], lu[4], lu[5], lu[6], now_str))

    # Attendance
    plcur.execute("DELETE FROM placement_attendance")
    att_p_id = 1
    for s in students_data[:12]:
        plcur.execute("""
            INSERT INTO placement_attendance (id, drive_id, stage_id, student_id, roll_no, student_name, check_in_time, check_in_mode, marked_by, status, created_at)
            VALUES (?, 1, 1, ?, ?, ?, ?, 'QR_CODE', 'TPO_SCANNER', 'PRESENT', ?)
        """, (att_p_id, s[0], s[2], s[1], (now - timedelta(days=1)).strftime("%Y-%m-%d 09:45:00"), now_str))
        att_p_id += 1

    # Results
    plcur.execute("DELETE FROM placement_results")
    res_id = 1
    for s in students_data[:8]:
        status_eval = "QUALIFIED" if s[0] <= 5 else "ON_HOLD"
        plcur.execute("""
            INSERT INTO placement_results (id, drive_id, stage_id, student_id, roll_no, student_name, result_status, round_feedback, marked_by, marked_at, created_at)
            VALUES (?, 1, 1, ?, ?, ?, ?, 'Excellent algorithmic clarity and optimal time complexity.', 'Priya Menon (Google Lead)', ?, ?)
        """, (res_id, s[0], s[2], s[1], status_eval, now_str, now_str))
        res_id += 1

    # Placed Students (Hall of Fame)
    placed_data = [
        (1, 1, 1, "21CS001", "Aarav Sharma", "Google India", "Software Engineer (SDE-1)", 44.0, "/uploads/offers/21CS001_google.pdf", "2026-10-18"),
        (2, 2, 2, "21CS002", "Bhavya Patel", "Microsoft IDC", "Software Development Engineer", 42.0, "/uploads/offers/21CS002_microsoft.pdf", "2026-10-19"),
        (3, 3, 11, "21IT001", "Karthik Raja", "Amazon AWS", "SDE-1 (Cloud Infrastructure)", 36.0, "/uploads/offers/21IT001_amazon.pdf", "2026-10-17"),
        (4, 1, 4, "21CS004", "Divya Nair", "Google India", "Software Engineer - Core Search", 32.5, "/uploads/offers/21CS004_google.pdf", "2026-10-18"),
        (5, 2, 12, "21IT002", "Lavanya Iyer", "Microsoft IDC", "Software Engineer (Teams)", 38.0, "/uploads/offers/21IT002_microsoft.pdf", "2026-10-19"),
        (6, 6, 8, "21CS008", "Harshita Joshi", "Goldman Sachs", "New Analyst (Quant Engineering)", 32.0, "/uploads/offers/21CS008_goldman.pdf", "2026-10-15"),
        (7, 7, 18, "21EC003", "Sneha Pillai", "Cisco Systems", "Network Software Engineer", 24.5, "/uploads/offers/21EC003_cisco.pdf", "2026-10-14"),
        (8, 4, 13, "21IT003", "Manoj Kumar", "TCS Digital", "Digital Software Engineer", 11.5, "/uploads/offers/21IT003_tcs.pdf", "2026-10-12"),
        (9, 5, 14, "21IT004", "Neha Deshmukh", "Infosys Springboard", "Specialist Programmer (SP)", 15.0, "/uploads/offers/21IT004_infosys.pdf", "2026-10-13"),
        (10, 1, 24, "21CS011", "Ananya Sharma", "Google India", "Software Engineer (Google Cloud)", 34.0, "/uploads/offers/21CS011_google.pdf", "2026-10-18")
    ]
    for ps in placed_data:
        plcur.execute("""
            INSERT INTO placement_placed_students (id, drive_id, student_id, roll_no, student_name, company_name, role_offered, package_lpa, offer_letter_url, offered_at, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET
                company_name=excluded.company_name, role_offered=excluded.role_offered, package_lpa=excluded.package_lpa
        """, (ps[0], ps[1], ps[2], ps[3], ps[4], ps[5], ps[6], ps[7], ps[8], ps[9], now_str))

    # Feedback
    plcur.execute("DELETE FROM placement_feedback")
    feedbacks = [
        (1, 1, 1, "21CS001", 5, "HARD", 4, "Google interview was intense on Tree, Graph and Dynamic Programming edge cases. The interviewers were very collaborative and gave helpful hints when explaining my thought process.", "Focus heavily on LeetCode Medium/Hard and practice explaining your code verbally while typing.", now_str),
        (2, 2, 2, "21CS002", 5, "HARD", 4, "Microsoft focused on concurrency, object-oriented design patterns, and clean code. Make sure you write modular code with error handling.", "Brush up on OS threads, synchronization primitives, and design patterns like Factory and Observer.", now_str),
        (3, 3, 11, "21IT001", 4, "MEDIUM", 3, "Amazon bar raiser was purely situational based on Leadership Principles. Do not fake scenarios; they cross-question deeply.", "Prepare 2-3 stories for every Amazon leadership principle using the STAR framework.", now_str)
    ]
    for fb in feedbacks:
        plcur.execute("""
            INSERT INTO placement_feedback (id, drive_id, student_id, roll_no, rating, difficulty, rounds_count, experience_review, tips, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, (fb[0], fb[1], fb[2], fb[3], fb[4], fb[5], fb[6], fb[7], fb[8], fb[9]))

    plconn.commit()
    plconn.close()

    print("\nSUCCESS: All databases seeded with rich, realistic placeholder actual data!")

if __name__ == "__main__":
    seed_databases()
