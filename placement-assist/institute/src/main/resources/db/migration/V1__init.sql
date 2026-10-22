-- ==============================================================================
-- Flyway Migration V1: Initial Schema for Placement Assistance - Institute Pillar
-- SQLite 3 Syntax with Foreign Keys, Constraints, and Indexes
-- ==============================================================================

-- 1. USERS
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role TEXT NOT NULL,
    created_at TEXT NOT NULL,
    updated_at TEXT
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- 2. STUDENTS
CREATE TABLE IF NOT EXISTS students (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    name TEXT NOT NULL,
    roll_no TEXT UNIQUE NOT NULL,
    email TEXT UNIQUE NOT NULL,
    phone TEXT,
    branch TEXT NOT NULL,
    year INTEGER NOT NULL,
    cgpa REAL NOT NULL,
    backlogs INTEGER NOT NULL,
    linkedin TEXT,
    github TEXT,
    photo_url TEXT,
    placement_eligible INTEGER NOT NULL DEFAULT 0,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_students_roll_no ON students(roll_no);
CREATE INDEX IF NOT EXISTS idx_students_email ON students(email);
CREATE INDEX IF NOT EXISTS idx_students_user_id ON students(user_id);
CREATE INDEX IF NOT EXISTS idx_students_branch_year ON students(branch, year);

-- 3. STUDENT SKILLS (Element Collection)
CREATE TABLE IF NOT EXISTS student_skills (
    student_id INTEGER NOT NULL,
    skill TEXT NOT NULL,
    PRIMARY KEY (student_id, skill),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- 4. COMPANIES
CREATE TABLE IF NOT EXISTS companies (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    website TEXT,
    about TEXT,
    industry TEXT,
    headquarters TEXT,
    logo_url TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT
);

CREATE INDEX IF NOT EXISTS idx_companies_name ON companies(name);

-- 5. DRIVES
CREATE TABLE IF NOT EXISTS drives (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    company_id INTEGER NOT NULL,
    title TEXT NOT NULL,
    drive_date TEXT NOT NULL,
    last_date_to_apply TEXT NOT NULL,
    eligibility_cgpa REAL NOT NULL DEFAULT 6.0,
    eligible_branches TEXT NOT NULL,
    max_backlogs INTEGER NOT NULL DEFAULT 0,
    roles TEXT NOT NULL,
    package_min REAL,
    package_max REAL,
    location TEXT,
    status TEXT NOT NULL DEFAULT 'UPCOMING',
    description TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_drives_company_id ON drives(company_id);
CREATE INDEX IF NOT EXISTS idx_drives_deadline ON drives(last_date_to_apply);
CREATE INDEX IF NOT EXISTS idx_drives_status ON drives(status);

-- 6. DRIVE ROUNDS
CREATE TABLE IF NOT EXISTS drive_rounds (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    drive_id INTEGER NOT NULL,
    round_type TEXT NOT NULL,
    order_no INTEGER NOT NULL,
    round_date TEXT,
    venue TEXT,
    duration_mins INTEGER,
    cutoff REAL,
    instructions TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (drive_id) REFERENCES drives(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_drive_rounds_drive_id ON drive_rounds(drive_id);

-- 7. ROUND RESULTS
CREATE TABLE IF NOT EXISTS round_results (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    round_id INTEGER NOT NULL,
    student_id INTEGER NOT NULL,
    score REAL,
    result TEXT NOT NULL DEFAULT 'PENDING',
    remarks TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    UNIQUE(round_id, student_id),
    FOREIGN KEY (round_id) REFERENCES drive_rounds(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_round_results_round_id ON round_results(round_id);
CREATE INDEX IF NOT EXISTS idx_round_results_student_id ON round_results(student_id);

-- 8. QUESTION BANK
CREATE TABLE IF NOT EXISTS questions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    drive_id INTEGER,
    role TEXT,
    round_type TEXT,
    difficulty TEXT NOT NULL DEFAULT 'MEDIUM',
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    tags TEXT,
    asked_year INTEGER,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (drive_id) REFERENCES drives(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_questions_drive_id ON questions(drive_id);
CREATE INDEX IF NOT EXISTS idx_questions_difficulty ON questions(difficulty);

-- 9. CRT SESSIONS
CREATE TABLE IF NOT EXISTS crt_sessions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    topic TEXT NOT NULL,
    trainer TEXT NOT NULL,
    session_date TEXT NOT NULL,
    start_time TEXT NOT NULL,
    end_time TEXT NOT NULL,
    venue TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT
);

CREATE INDEX IF NOT EXISTS idx_crt_sessions_date ON crt_sessions(session_date);

-- 10. CLASS SESSIONS
CREATE TABLE IF NOT EXISTS class_sessions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    subject TEXT NOT NULL,
    faculty TEXT NOT NULL,
    session_date TEXT NOT NULL,
    start_time TEXT NOT NULL,
    end_time TEXT NOT NULL,
    branch TEXT NOT NULL,
    year INTEGER NOT NULL,
    section TEXT NOT NULL,
    created_at TEXT NOT NULL,
    updated_at TEXT
);

CREATE INDEX IF NOT EXISTS idx_class_sessions_branch_year ON class_sessions(branch, year, session_date);

-- 11. ATTENDANCES
CREATE TABLE IF NOT EXISTS attendances (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    student_id INTEGER NOT NULL,
    session_type TEXT NOT NULL,
    crt_session_id INTEGER,
    class_session_id INTEGER,
    status TEXT NOT NULL,
    marked_by TEXT NOT NULL,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (crt_session_id) REFERENCES crt_sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (class_session_id) REFERENCES class_sessions(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_attendances_student_id ON attendances(student_id);
CREATE INDEX IF NOT EXISTS idx_attendances_crt_session ON attendances(crt_session_id);
CREATE INDEX IF NOT EXISTS idx_attendances_class_session ON attendances(class_session_id);

-- 12. RESUMES
CREATE TABLE IF NOT EXISTS resumes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    student_id INTEGER NOT NULL,
    file_url TEXT NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    status TEXT NOT NULL DEFAULT 'PENDING',
    ats_score INTEGER,
    ats_breakdown TEXT,
    feedback TEXT,
    reviewed_by TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_resumes_student_id ON resumes(student_id);
CREATE INDEX IF NOT EXISTS idx_resumes_status ON resumes(status);

-- 13. HACKATHONS
CREATE TABLE IF NOT EXISTS hackathons (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    organizer TEXT NOT NULL,
    mode TEXT NOT NULL,
    start_date TEXT NOT NULL,
    end_date TEXT NOT NULL,
    registration_deadline TEXT NOT NULL,
    prize TEXT,
    team_size INTEGER,
    location TEXT,
    link TEXT,
    description TEXT,
    how_to_apply TEXT,
    status TEXT NOT NULL DEFAULT 'OPEN',
    created_at TEXT NOT NULL,
    updated_at TEXT
);

CREATE INDEX IF NOT EXISTS idx_hackathons_deadline ON hackathons(registration_deadline);
CREATE INDEX IF NOT EXISTS idx_hackathons_status ON hackathons(status);

-- 14. INTERNSHIPS
CREATE TABLE IF NOT EXISTS internships (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    company TEXT NOT NULL,
    role TEXT NOT NULL,
    stipend REAL,
    duration_months INTEGER,
    location TEXT,
    mode TEXT NOT NULL,
    eligibility TEXT,
    deadline TEXT NOT NULL,
    apply_url TEXT,
    how_to_apply TEXT,
    skills_required TEXT,
    status TEXT NOT NULL DEFAULT 'OPEN',
    created_at TEXT NOT NULL,
    updated_at TEXT
);

CREATE INDEX IF NOT EXISTS idx_internships_deadline ON internships(deadline);
CREATE INDEX IF NOT EXISTS idx_internships_status ON internships(status);

-- 15. OFF-CAMPUS JOBS
CREATE TABLE IF NOT EXISTS offcampus_jobs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    company TEXT NOT NULL,
    role TEXT NOT NULL,
    package_lpa REAL,
    location TEXT,
    batch_eligible TEXT,
    experience TEXT,
    skills_required TEXT,
    deadline TEXT NOT NULL,
    apply_url TEXT,
    how_to_apply TEXT,
    status TEXT NOT NULL DEFAULT 'OPEN',
    created_at TEXT NOT NULL,
    updated_at TEXT
);

CREATE INDEX IF NOT EXISTS idx_jobs_deadline ON offcampus_jobs(deadline);
CREATE INDEX IF NOT EXISTS idx_jobs_status ON offcampus_jobs(status);

-- 16. BOOKMARKS
CREATE TABLE IF NOT EXISTS bookmarks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    student_id INTEGER NOT NULL,
    listing_type TEXT NOT NULL,
    listing_id INTEGER NOT NULL,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    UNIQUE(student_id, listing_type, listing_id),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_bookmarks_student ON bookmarks(student_id, listing_type);

-- ==============================================================================
-- INITIAL SEED DATA FOR INSTITUTE PILLAR
-- ==============================================================================

-- 1. USERS
INSERT OR IGNORE INTO users (id, email, password_hash, role, created_at, updated_at) VALUES
(1, 'admin@college.edu', '$2a$10$oIVmtQl56m5WIFYGhKAGnOfFi/fYpRZG6myhQzndnnlUX1K67AyM6', 'ADMIN', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(2, 'tpo@college.edu', '$2a$10$LgLLbcK33DEOdrRqRxf0T.MKip.6YFh/g.5/tydsK7uiGXW/uooe.', 'TPO', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(3, 'director@college.edu', '$2a$10$avb8nhg4fzPCSsdNjSaR4OwaMcparwTqAh1uL/yatWq72n2gA1sAa', 'DIRECTOR', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(4, 'aarav@college.edu', '$2a$10$P6oomM8lmwWfvq9L5nEJQ.f950gtEjBqFchNrNOeV3MoH2mknkaEC', 'STUDENT', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(5, 'bhavya@college.edu', '$2a$10$P6oomM8lmwWfvq9L5nEJQ.f950gtEjBqFchNrNOeV3MoH2mknkaEC', 'STUDENT', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(6, 'chirag@college.edu', '$2a$10$P6oomM8lmwWfvq9L5nEJQ.f950gtEjBqFchNrNOeV3MoH2mknkaEC', 'STUDENT', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(7, 'divya@college.edu', '$2a$10$P6oomM8lmwWfvq9L5nEJQ.f950gtEjBqFchNrNOeV3MoH2mknkaEC', 'STUDENT', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(8, 'eshaan@college.edu', '$2a$10$P6oomM8lmwWfvq9L5nEJQ.f950gtEjBqFchNrNOeV3MoH2mknkaEC', 'STUDENT', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(9, 'farhan@college.edu', '$2a$10$P6oomM8lmwWfvq9L5nEJQ.f950gtEjBqFchNrNOeV3MoH2mknkaEC', 'STUDENT', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(10, 'gayatri@college.edu', '$2a$10$P6oomM8lmwWfvq9L5nEJQ.f950gtEjBqFchNrNOeV3MoH2mknkaEC', 'STUDENT', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(11, 'harshita@college.edu', '$2a$10$P6oomM8lmwWfvq9L5nEJQ.f950gtEjBqFchNrNOeV3MoH2mknkaEC', 'STUDENT', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(12, 'ishaan@college.edu', '$2a$10$P6oomM8lmwWfvq9L5nEJQ.f950gtEjBqFchNrNOeV3MoH2mknkaEC', 'STUDENT', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(13, 'karan@college.edu', '$2a$10$P6oomM8lmwWfvq9L5nEJQ.f950gtEjBqFchNrNOeV3MoH2mknkaEC', 'STUDENT', '2026-01-01 00:00:00', '2026-01-01 00:00:00');

-- 2. STUDENTS
INSERT OR IGNORE INTO students (id, user_id, name, roll_no, email, phone, branch, year, cgpa, backlogs, linkedin, github, photo_url, placement_eligible, created_at, updated_at) VALUES
(1, 4, 'Aarav Sharma', '21CS001', 'aarav@college.edu', '+91 98765 43210', 'CSE', 4, 8.85, 0, 'https://linkedin.com/in/aarav-sharma', 'https://github.com/aarav-sharma', 'https://images.unsplash.com/photo-1539571696357-5a69c17a67c6', 1, '2026-01-05 10:00:00', '2026-01-05 10:00:00'),
(2, 5, 'Bhavya Patel', '21CS002', 'bhavya@college.edu', '+91 98765 43211', 'CSE', 4, 8.42, 0, 'https://linkedin.com/in/bhavya-patel', 'https://github.com/bhavya-patel', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb', 1, '2026-01-05 10:00:00', '2026-01-05 10:00:00'),
(3, 6, 'Chirag Rao', '21CS003', 'chirag@college.edu', '+91 98765 43212', 'CSE', 4, 5.80, 2, 'https://linkedin.com/in/chirag-rao', 'https://github.com/chirag-rao', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d', 0, '2026-01-05 10:00:00', '2026-01-05 10:00:00'),
(4, 7, 'Divya Nair', '21IT001', 'divya@college.edu', '+91 98765 43213', 'IT', 4, 9.15, 0, 'https://linkedin.com/in/divya-nair', 'https://github.com/divya-nair', 'https://images.unsplash.com/photo-1517841905240-472988babdf9', 1, '2026-01-05 10:00:00', '2026-01-05 10:00:00'),
(5, 8, 'Eshaan Gupta', '21CS010', 'eshaan@college.edu', '+91 98765 43214', 'CSE', 4, 9.40, 0, 'https://linkedin.com/in/eshaan-gupta', 'https://github.com/eshaan-gupta', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e', 1, '2026-01-05 10:00:00', '2026-01-05 10:00:00'),
(6, 9, 'Farhan Ali', '21CS015', 'farhan@college.edu', '+91 98765 43215', 'CSE', 4, 8.10, 0, 'https://linkedin.com/in/farhan-ali', 'https://github.com/farhan-ali', 'https://images.unsplash.com/photo-1492562080023-ab3db95bfbce', 1, '2026-01-05 10:00:00', '2026-01-05 10:00:00'),
(7, 10, 'Gayatri Joshi', '21IT005', 'gayatri@college.edu', '+91 98765 43216', 'IT', 4, 8.65, 0, 'https://linkedin.com/in/gayatri-joshi', 'https://github.com/gayatri-joshi', 'https://images.unsplash.com/photo-1524504388940-b1c1722653e1', 1, '2026-01-05 10:00:00', '2026-01-05 10:00:00'),
(8, 11, 'Harshita Roy', '22CS040', 'harshita@college.edu', '+91 98765 43217', 'CSE', 3, 7.90, 0, 'https://linkedin.com/in/harshita-roy', 'https://github.com/harshita-roy', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2', 1, '2026-01-05 10:00:00', '2026-01-05 10:00:00'),
(9, 12, 'Ishaan Malhotra', '22ECE012', 'ishaan@college.edu', '+91 98765 43218', 'ECE', 3, 7.20, 0, 'https://linkedin.com/in/ishaan-malhotra', 'https://github.com/ishaan-malhotra', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d', 1, '2026-01-05 10:00:00', '2026-01-05 10:00:00'),
(10, 13, 'Karan Verma', '21MECH003', 'karan@college.edu', '+91 98765 43219', 'MECH', 4, 6.85, 0, 'https://linkedin.com/in/karan-verma', 'https://github.com/karan-verma', 'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7', 1, '2026-01-05 10:00:00', '2026-01-05 10:00:00');

-- 3. STUDENT SKILLS
INSERT OR IGNORE INTO student_skills (student_id, skill) VALUES
(1, 'Java'), (1, 'Spring Boot'), (1, 'Data Structures'), (1, 'Algorithms'), (1, 'SQL'), (1, 'Docker'),
(2, 'Python'), (2, 'React.js'), (2, 'JavaScript'), (2, 'HTML/CSS'), (2, 'Django'),
(3, 'C++'), (3, 'Basic Java'),
(4, 'Java'), (4, 'Spring Boot'), (4, 'Microservices'), (4, 'AWS'), (4, 'Kubernetes'), (4, 'System Design'),
(5, 'C++'), (5, 'Advanced Algorithms'), (5, 'Competitive Programming'), (5, 'Distributed Systems'),
(6, 'TypeScript'), (6, 'Node.js'), (6, 'React'), (6, 'GraphQL'), (6, 'PostgreSQL'),
(7, 'Python'), (7, 'Machine Learning'), (7, 'Data Analytics'), (7, 'TensorFlow'), (7, 'SQL'),
(8, 'Java'), (8, 'Data Structures'), (8, 'Web Development'),
(9, 'Embedded C'), (9, 'IoT'), (9, 'MATLAB'), (9, 'Python'),
(10, 'AutoCAD'), (10, 'SolidWorks'), (10, 'Python Basics');

-- 4. COMPANIES
INSERT OR IGNORE INTO companies (id, name, website, about, industry, headquarters, logo_url, created_at, updated_at) VALUES
(1, 'Google', 'https://careers.google.com', 'Global technology leader specializing in search, cloud, AI, and advertising.', 'Technology / Internet', 'Mountain View, CA', 'https://logo.clearbit.com/google.com', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(2, 'Microsoft', 'https://careers.microsoft.com', 'Pioneering platform and cloud solutions for enterprise productivity and AI.', 'Enterprise Software & Cloud', 'Redmond, WA', 'https://logo.clearbit.com/microsoft.com', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(3, 'Amazon', 'https://amazon.jobs', 'E-commerce, digital streaming, and cloud computing platform provider.', 'Cloud & E-Commerce', 'Seattle, WA', 'https://logo.clearbit.com/amazon.com', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(4, 'Atlassian', 'https://atlassian.com/company/careers', 'Collaboration software for agile development, team management, and IT operations.', 'Product Software', 'Sydney, Australia', 'https://logo.clearbit.com/atlassian.com', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(5, 'Goldman Sachs', 'https://goldmansachs.com/careers', 'Global financial services institution with quantitative engineering and tech teams.', 'FinTech & Investment Banking', 'New York, NY', 'https://logo.clearbit.com/goldmansachs.com', '2026-01-01 00:00:00', '2026-01-01 00:00:00');

-- 5. DRIVES
INSERT OR IGNORE INTO drives (id, company_id, title, drive_date, last_date_to_apply, eligibility_cgpa, eligible_branches, max_backlogs, roles, package_min, package_max, location, status, description, created_at, updated_at) VALUES
(1, 1, 'Google Campus Recruitment 2026', '2026-11-15', '2026-11-05', 8.0, 'CSE,IT', 0, 'Software Engineer (L3), Site Reliability Engineer', 38.5, 54.5, 'Bangalore / Hyderabad', 'ONGOING', 'Flagship engineering hiring drive for fresh graduates across Google Search, Cloud, and Systems infrastructure.', '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(2, 2, 'Microsoft SDE FTE & Intern Drive', '2026-11-20', '2026-11-10', 7.5, 'CSE,IT,ECE', 0, 'Software Development Engineer', 28.0, 42.0, 'Bangalore / Hyderabad / Noida', 'ONGOING', 'Hiring for core Azure cloud, Windows kernel, and Developer Division engineering teams.', '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(3, 3, 'Amazon University Graduate Hiring', '2026-10-15', '2026-10-01', 7.0, 'CSE,IT,ECE,EEE', 0, 'Software Development Engineer I', 44.0, 44.0, 'Bangalore / Chennai / Hyderabad', 'COMPLETED', 'Campus hiring concluded for AWS Platform, Prime Video, and Supply Chain engineering.', '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(4, 4, 'Atlassian Engineering Campus Drive', '2026-12-05', '2026-11-25', 7.5, 'CSE,IT', 0, 'Graduate Software Developer', 36.0, 36.0, 'Bangalore (R&D Center)', 'UPCOMING', 'Join Atlassian Bangalore R&D to craft Jira, Confluence, and next-generation collaboration services.', '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(5, 5, 'Goldman Sachs Engineering Campus Drive', '2026-12-15', '2026-12-01', 8.0, 'CSE,IT,ECE,EEE', 0, 'Analyst - Engineering & Quantitative Tech', 32.5, 32.5, 'Bangalore / Hyderabad', 'UPCOMING', 'Quantitative finance, low-latency execution engines, and risk management systems.', '2026-01-10 10:00:00', '2026-01-10 10:00:00');

-- 6. DRIVE ROUNDS
INSERT OR IGNORE INTO drive_rounds (id, drive_id, round_type, order_no, round_date, venue, duration_mins, cutoff, instructions, created_at, updated_at) VALUES
(1, 1, 'ONLINE_TEST', 1, '2026-11-10', 'Online HackerEarth Assessment', 90, 75.0, '3 Data structures & algorithmic coding problems. Camera and microphone proctoring mandatory.', '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(2, 1, 'TECHNICAL_INTERVIEW_1', 2, '2026-11-15', 'Google Meet (Rooms 1-10)', 45, 70.0, 'Live coding in Google Docs with Senior Software Engineer. Focus on graphs and dynamic programming.', '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(3, 1, 'TECHNICAL_INTERVIEW_2', 3, '2026-11-15', 'Google Meet', 45, 70.0, 'System design fundamentals, caching, concurrency, and DB scalability.', '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(4, 1, 'HR', 4, '2026-11-16', 'Google Meet', 30, 60.0, 'Googliness, leadership principles, conflict resolution, and teamwork fit.', '2026-01-10 10:00:00', '2026-01-10 10:00:00');

-- 7. ROUND RESULTS
INSERT OR IGNORE INTO round_results (id, round_id, student_id, score, result, remarks, created_at, updated_at) VALUES
(1, 1, 1, 95.0, 'QUALIFIED', 'All test cases passed with optimal time and space complexity.', '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(2, 1, 2, 88.0, 'QUALIFIED', 'Clean DP implementation with correct boundary handling.', '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(3, 1, 4, 98.0, 'QUALIFIED', 'Outstanding performance, completed full assessment in 52 mins.', '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(4, 1, 5, 100.0, 'QUALIFIED', 'Flawless execution, highest score in college batch.', '2026-01-10 10:00:00', '2026-01-10 10:00:00');

-- 8. QUESTION BANK
INSERT OR IGNORE INTO questions (id, drive_id, role, round_type, difficulty, question, answer, tags, asked_year, created_at, updated_at) VALUES
(1, 1, 'Software Engineer', 'TECHNICAL_INTERVIEW_1', 'HARD', 'How would you design a distributed Least Recently Used (LRU) Cache that handles 100,000 requests/sec with concurrent evictions?', 'Combine a doubly linked list with a concurrent hash map. Use fine-grained bucket locking or striped lock segments to prevent global bottleneck during eviction. For distributed multi-node clusters, partition keys using consistent hashing and store metadata in Redis or Memcached clusters.', 'Data Structures, LRU, Caching, Concurrency', 2025, '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(2, 1, 'Software Engineer', 'ONLINE_TEST', 'MEDIUM', 'Given a directed acyclic graph (DAG), explain Kahn Algorithm for Topological Sort and write its time and space complexity.', 'Kahn Algorithm uses in-degree counting. Compute in-degree for all vertices, enqueue all nodes with in-degree 0. While queue is non-empty, dequeue vertex u, append to result, and decrement in-degree for all neighbors v. If v becomes 0, enqueue it. Time Complexity: O(V + E), Space Complexity: O(V).', 'Graphs, Topological Sort, BFS, Algorithms', 2025, '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(3, 2, 'Software Development Engineer', 'TECHNICAL_INTERVIEW_1', 'MEDIUM', 'Explain the differences between B-Tree and B+ Tree indexing in Relational Databases and why B+ Trees are preferred for disk storage.', 'In B-Trees, data pointers are stored in all internal and leaf nodes. In B+ Trees, data records/pointers are stored exclusively in leaf nodes, while internal nodes store only index keys. Leaf nodes in B+ Trees are linked sequentially, allowing high-throughput range scans with sequential disk I/O. Internal nodes fit more keys per disk block, resulting in higher fan-out and shallower tree depth.', 'Database, Indexing, B+ Tree, SQL', 2025, '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(4, 3, 'SDE I', 'ONLINE_TEST', 'EASY', 'What is the Two Sum problem and how can you achieve O(N) time complexity using extra space?', 'Use a hash map. For each number x at index i, compute complement = target - x. Check if complement exists in hash map; if found, return the pair of indices. Otherwise, insert x with its index i into the map. Time: O(N), Space: O(N).', 'Hash Map, Array, Algorithms', 2024, '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(5, 1, 'All Roles', 'HR', 'EASY', 'Describe a challenging project where you had a disagreement with a teammate and how you resolved it.', 'Use the STAR method: Situation (describe technical design choice dispute), Task (needed unified solution before sprint deadline), Action (scheduled 30-min design session, compared metrics and benchmarked prototypes objectively), Result (adopted optimal hybrid solution, completed project ahead of schedule).', 'Behavioral, STAR Method, HR, Teamwork', 2025, '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(6, 4, 'Software Developer', 'TECHNICAL_INTERVIEW_1', 'MEDIUM', 'Explain how Spring Boot handles Dependency Injection (DI) and how the ApplicationContext lifecycle operates.', 'Spring IoC Container initializes beans defined by @Component, @Service, @Repository, and @Configuration. In constructor injection, the container resolves dependencies from the BeanFactory registry, instantiates beans in topological dependency order, applies BeanPostProcessors, and injects singletons.', 'Spring Boot, Java, IoC, Dependency Injection', 2025, '2026-01-10 10:00:00', '2026-01-10 10:00:00');

-- 9. CRT SESSIONS
INSERT OR IGNORE INTO crt_sessions (id, title, topic, trainer, session_date, start_time, end_time, venue, created_at, updated_at) VALUES
(1, 'Quantitative Aptitude Masterclass', 'Permutations, Probability, Time & Work', 'Prof. Arvind Saxena', '2026-10-25', '09:00', '12:00', 'Auditorium Hall A', '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(2, 'Dynamic Programming & Graph Theory', 'DP State Transitions, Dijkstra & Floyd-Warshall', 'Dr. Radhika Iyer', '2026-10-28', '14:00', '17:00', 'Computer Lab 3', '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(3, 'Corporate Behavioral & STAR Interviews', 'Mock HR Interviews, Non-verbal Communication', 'Ms. Sunita Kapoor', '2026-11-02', '10:00', '13:00', 'Seminar Hall 2', '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(4, 'Distributed Systems & System Design', 'Load Balancing, Microservices, CAP Theorem', 'Er. Naveen Reddy', '2026-11-06', '14:00', '17:30', 'Auditorium Hall B', '2026-01-10 10:00:00', '2026-01-10 10:00:00');

-- 10. CLASS SESSIONS
INSERT OR IGNORE INTO class_sessions (id, subject, faculty, session_date, start_time, end_time, branch, year, section, created_at, updated_at) VALUES
(1, 'Distributed Cloud Systems', 'Dr. M. S. Swaminathan', '2026-10-24', '09:30', '11:00', 'CSE', 4, 'A', '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(2, 'Advanced Machine Learning & AI', 'Dr. Anita Desai', '2026-10-24', '11:15', '12:45', 'CSE', 4, 'A', '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(3, 'Enterprise Database Engineering', 'Prof. R. K. Bhatt', '2026-10-24', '14:00', '15:30', 'IT', 4, 'A', '2026-01-10 10:00:00', '2026-01-10 10:00:00');

-- 11. ATTENDANCES
INSERT OR IGNORE INTO attendances (id, student_id, session_type, crt_session_id, class_session_id, status, marked_by, created_at, updated_at) VALUES
(1, 1, 'CRT', 1, NULL, 'PRESENT', 'tpo@college.edu', '2026-10-25 12:05:00', '2026-10-25 12:05:00'),
(2, 2, 'CRT', 1, NULL, 'PRESENT', 'tpo@college.edu', '2026-10-25 12:05:00', '2026-10-25 12:05:00'),
(3, 3, 'CRT', 1, NULL, 'ABSENT', 'tpo@college.edu', '2026-10-25 12:05:00', '2026-10-25 12:05:00'),
(4, 4, 'CRT', 1, NULL, 'PRESENT', 'tpo@college.edu', '2026-10-25 12:05:00', '2026-10-25 12:05:00'),
(5, 1, 'CLASS', NULL, 1, 'PRESENT', 'swaminathan@college.edu', '2026-10-24 11:05:00', '2026-10-24 11:05:00'),
(6, 2, 'CLASS', NULL, 1, 'PRESENT', 'swaminathan@college.edu', '2026-10-24 11:05:00', '2026-10-24 11:05:00'),
(7, 3, 'CLASS', NULL, 1, 'ABSENT', 'swaminathan@college.edu', '2026-10-24 11:05:00', '2026-10-24 11:05:00');

-- 12. RESUMES
INSERT OR IGNORE INTO resumes (id, student_id, file_url, version, status, ats_score, ats_breakdown, feedback, reviewed_by, created_at, updated_at) VALUES
(1, 1, '/uploads/resumes/21CS001_Aarav_Sharma_Resume.pdf', 2, 'APPROVED', 88, '{"contact": 100, "skills": 95, "projects": 90, "education": 100, "formatting": 85}', 'Strong action verbs used in project descriptions. Quantitative impact metrics included. Approved for Tier 1 campus drives.', 'tpo@college.edu', '2026-01-15 10:00:00', '2026-01-15 10:00:00'),
(2, 2, '/uploads/resumes/21CS002_Bhavya_Patel_Resume.pdf', 1, 'APPROVED', 84, '{"contact": 100, "skills": 90, "projects": 80, "education": 100, "formatting": 80}', 'Clean single-page layout. Add GitHub links to showcase live demo deployments.', 'tpo@college.edu', '2026-01-15 10:00:00', '2026-01-15 10:00:00'),
(3, 4, '/uploads/resumes/21IT001_Divya_Nair_Resume.pdf', 1, 'APPROVED', 92, '{"contact": 100, "skills": 95, "projects": 95, "education": 100, "formatting": 90}', 'Exemplary ATS score with strong keywords matching Google and Microsoft job profiles.', 'tpo@college.edu', '2026-01-15 10:00:00', '2026-01-15 10:00:00');

-- 13. HACKATHONS
INSERT OR IGNORE INTO hackathons (id, name, organizer, mode, start_date, end_date, registration_deadline, prize, team_size, location, link, description, how_to_apply, status, created_at, updated_at) VALUES
(1, 'Smart India Hackathon 2026', 'Ministry of Education & AICTE', 'HYBRID', '2026-11-20', '2026-11-22', '2026-11-10', '₹1,00,000 per problem statement', 6, 'New Delhi / Nodal Centers', 'https://sih.gov.in', 'Nationwide hackathon focused on problem statements from central ministries, state governments, and premier industries.', 'Register via college SPOC endorsement and submit problem statement PPT.', 'OPEN', '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(2, 'Google Solution Challenge 2026', 'Google Developer Student Clubs (GDSC)', 'ONLINE', '2026-12-01', '2026-12-15', '2026-11-28', '$10,000 USD + Google Mentorship', 4, 'Global Online', 'https://developers.google.com/community/gdsc-solution-challenge', 'Build a technical solution to one or more of the United Nations 17 Sustainable Development Goals using Google Cloud, Android, or Flutter.', 'Submit 2-minute demo video and public GitHub repository link.', 'OPEN', '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(3, 'HackNITR 6.0', 'NIT Rourkela & Major League Hacking (MLH)', 'OFFLINE', '2026-12-18', '2026-12-20', '2026-12-05', '₹2,50,000 Cash Pool + Swags', 4, 'NIT Rourkela Campus', 'https://hacknitr.com', 'Eastern India largest student-run hackathon with Web3, AI/ML, Cloud, and Open Innovation tracks.', 'Direct application on Devfolio with portfolio evaluation.', 'OPEN', '2026-01-10 10:00:00', '2026-01-10 10:00:00');

-- 14. INTERNSHIPS
INSERT OR IGNORE INTO internships (id, company, role, stipend, duration_months, location, mode, eligibility, deadline, apply_url, how_to_apply, skills_required, status, created_at, updated_at) VALUES
(1, 'Microsoft India', 'Summer Research & Engineering Intern 2026', 125000.0, 3, 'Bangalore', 'HYBRID', 'Pre-final Year (2027 batch) B.Tech CSE/IT with CGPA >= 8.0', '2026-11-20', 'https://careers.microsoft.com/students', 'Apply through careers portal or get a referral from an FTE employee.', 'C++, C#, Data Structures, Operating Systems, Azure basics', 'OPEN', '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(2, 'Google India', 'Student Training in Engineering Program (STEP) Intern', 110000.0, 3, 'Hyderabad / Bangalore', 'HYBRID', '2nd and 3rd year engineering students with passion for computer science', '2026-11-15', 'https://careers.google.com/jobs/results/?q=STEP', 'Submit resume and transcript on Google Careers.', 'Python, Java, C++, Basic Data Structures & Algorithms', 'OPEN', '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(3, 'Amazon India', 'Software Development Engineer Intern (Summer 2026)', 80000.0, 6, 'Bangalore / Chennai', 'HYBRID', '3rd and 4th Year B.Tech CSE/IT/ECE', '2026-11-30', 'https://amazon.jobs/en/jobs/university', 'Direct application on Amazon Jobs followed by Online Assessment (OA).', 'Java, Object-Oriented Design, Algorithms, Problem Solving', 'OPEN', '2026-01-10 10:00:00', '2026-01-10 10:00:00');

-- 15. OFF-CAMPUS JOBS
INSERT OR IGNORE INTO offcampus_jobs (id, company, role, package_lpa, location, batch_eligible, experience, skills_required, deadline, apply_url, how_to_apply, status, created_at, updated_at) VALUES
(1, 'Oracle Cloud Infrastructure (OCI)', 'Member of Technical Staff - Cloud Native', 18.0, 'Bangalore', '2026 Batch', 'Fresh Graduate (0-1 yrs)', 'Java, Kubernetes, Go, Cloud Architecture, Linux', '2026-12-10', 'https://oracle.com/careers', 'Apply on Oracle Taleo system with resume matching keywords.', 'OPEN', '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(2, 'Cisco Systems', 'Software Engineer - Networking & Distributed Systems', 19.5, 'Bangalore', '2026 Batch', 'Fresh Graduate (0-1 yrs)', 'Python, C/C++, Networking Protocols, TCP/IP, Linux Kernel', '2026-12-05', 'https://jobs.cisco.com', 'Online application followed by CodeSignal test.', 'OPEN', '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(3, 'Adobe Systems', 'Software Engineer - Creative Cloud Platform', 22.0, 'Noida / Bangalore', '2026 Batch', 'Fresh Graduate (0-1 yrs)', 'C++, JavaScript, WebGL, High-performance graphics, Multi-threading', '2026-12-15', 'https://adobe.com/careers', 'Direct application on Adobe University talent portal.', 'OPEN', '2026-01-10 10:00:00', '2026-01-10 10:00:00');

