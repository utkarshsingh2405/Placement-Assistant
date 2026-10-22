-- ==============================================================================
-- Placement Database Migration (SQLite)
-- Schema V1: Live Recruitment Engine, Stages, PPT, Attendance, Results & Placed Wall
-- ==============================================================================

CREATE TABLE IF NOT EXISTS placement_companies (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    website TEXT,
    logo_url TEXT,
    industry TEXT,
    description TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS placement_drives (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    institute_drive_id INTEGER,
    company_id INTEGER NOT NULL REFERENCES placement_companies(id),
    title TEXT NOT NULL,
    description TEXT,
    job_roles TEXT,
    package_details TEXT,
    status TEXT NOT NULL DEFAULT 'UPCOMING', -- UPCOMING, ONGOING, COMPLETED, CANCELLED
    drive_date TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS placement_stages (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    drive_id INTEGER NOT NULL REFERENCES placement_drives(id),
    stage_name TEXT NOT NULL,
    stage_order INTEGER NOT NULL,
    stage_type TEXT NOT NULL, -- REGISTRATION, PPT, ONLINE_TEST, TECHNICAL_INTERVIEW_1, TECHNICAL_INTERVIEW_2, HR_INTERVIEW, OFFER
    status TEXT NOT NULL DEFAULT 'PENDING', -- PENDING, ACTIVE, COMPLETED
    venue_or_link TEXT,
    instructions TEXT,
    start_time TEXT,
    end_time TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS placement_applications (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    drive_id INTEGER NOT NULL REFERENCES placement_drives(id),
    student_id INTEGER,
    roll_no TEXT NOT NULL,
    student_name TEXT NOT NULL,
    email TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'APPLIED', -- APPLIED, ELIGIBLE, INELIGIBLE, SHORTLISTED, SELECTED, REJECTED, WITHDRAWN
    current_stage_id INTEGER REFERENCES placement_stages(id),
    applied_at TEXT NOT NULL DEFAULT (datetime('now')),
    resume_url TEXT,
    ats_score INTEGER,
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_apps_drive_roll ON placement_applications(drive_id, roll_no);

CREATE TABLE IF NOT EXISTS placement_talks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    drive_id INTEGER NOT NULL UNIQUE REFERENCES placement_drives(id),
    scheduled_at TEXT NOT NULL,
    venue_or_url TEXT,
    presentation_url TEXT,
    recording_url TEXT,
    overview TEXT,
    speaker_name TEXT,
    speaker_title TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS placement_talk_questions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    talk_id INTEGER NOT NULL REFERENCES placement_talks(id),
    student_id INTEGER,
    roll_no TEXT NOT NULL,
    student_name TEXT NOT NULL,
    question TEXT NOT NULL,
    answer TEXT,
    answered_by TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS placement_live_updates (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    drive_id INTEGER NOT NULL REFERENCES placement_drives(id),
    stage_id INTEGER REFERENCES placement_stages(id),
    title TEXT NOT NULL,
    message TEXT NOT NULL,
    category TEXT NOT NULL DEFAULT 'ANNOUNCEMENT', -- ANNOUNCEMENT, SHORTLIST, SCHEDULE_CHANGE, STAGE_STARTED, OFFERS_RELEASED
    priority TEXT NOT NULL DEFAULT 'INFO', -- INFO, HIGH, URGENT
    created_at TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS placement_attendance (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    drive_id INTEGER NOT NULL REFERENCES placement_drives(id),
    stage_id INTEGER NOT NULL REFERENCES placement_stages(id),
    student_id INTEGER,
    roll_no TEXT NOT NULL,
    student_name TEXT NOT NULL,
    check_in_time TEXT NOT NULL DEFAULT (datetime('now')),
    check_in_mode TEXT NOT NULL DEFAULT 'QR_SCAN', -- QR_SCAN, MANUAL_TPO
    marked_by TEXT,
    status TEXT NOT NULL DEFAULT 'PRESENT', -- PRESENT, ABSENT
    created_at TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_att_stage_roll ON placement_attendance(stage_id, roll_no);

CREATE TABLE IF NOT EXISTS placement_results (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    drive_id INTEGER NOT NULL REFERENCES placement_drives(id),
    stage_id INTEGER NOT NULL REFERENCES placement_stages(id),
    student_id INTEGER,
    roll_no TEXT NOT NULL,
    student_name TEXT NOT NULL,
    result_status TEXT NOT NULL, -- SELECTED, NON_SELECTED, WAITLIST
    round_feedback TEXT,
    marked_by TEXT NOT NULL,
    marked_at TEXT NOT NULL DEFAULT (datetime('now')),
    created_at TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_res_stage_roll ON placement_results(stage_id, roll_no);

CREATE TABLE IF NOT EXISTS placement_placed_students (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    drive_id INTEGER REFERENCES placement_drives(id),
    student_id INTEGER,
    roll_no TEXT NOT NULL,
    student_name TEXT NOT NULL,
    company_name TEXT NOT NULL,
    role_offered TEXT NOT NULL,
    package_lpa REAL NOT NULL,
    offer_letter_url TEXT,
    offered_at TEXT NOT NULL DEFAULT (datetime('now')),
    created_at TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS placement_feedback (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    drive_id INTEGER NOT NULL REFERENCES placement_drives(id),
    student_id INTEGER,
    roll_no TEXT NOT NULL,
    rating INTEGER NOT NULL,
    difficulty TEXT NOT NULL, -- EASY, MEDIUM, HARD
    rounds_count INTEGER,
    experience_review TEXT NOT NULL,
    tips TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS placement_subscriptions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    roll_no TEXT,
    endpoint TEXT NOT NULL UNIQUE,
    p256dh TEXT NOT NULL,
    auth TEXT NOT NULL,
    created_at TEXT NOT NULL DEFAULT (datetime('now'))
);

-- ==============================================================================
-- INITIAL SEEDS
-- ==============================================================================

-- Companies
INSERT OR IGNORE INTO placement_companies (id, name, website, industry, description) VALUES
(1, 'Google', 'https://careers.google.com', 'Technology / Internet', 'Multinational technology company specializing in online services and AI products.'),
(2, 'Microsoft', 'https://careers.microsoft.com', 'Enterprise Software', 'Global technology corporation developing personal computing and enterprise cloud systems.'),
(3, 'Amazon', 'https://amazon.jobs', 'E-Commerce & Cloud Computing', 'World leader in e-commerce, cloud infrastructure (AWS), and digital streaming.'),
(4, 'Atlassian', 'https://atlassian.com/company/careers', 'Software Collaboration', 'Developer of Jira, Confluence, Trello, and Bitbucket tools.'),
(5, 'Goldman Sachs', 'https://goldmansachs.com/careers', 'Investment Banking & Quant', 'Leading global financial institution providing investment banking and quantitative securities services.');

-- Drives
INSERT OR IGNORE INTO placement_drives (id, institute_drive_id, company_id, title, description, job_roles, package_details, status, drive_date) VALUES
(1, 1, 1, 'Google Campus Recruitment 2026', 'Google India hiring for Software Engineer (L3) and Systems Infrastructure roles.', 'Software Engineer (L3), Site Reliability Engineer', '₹38.5 LPA Base + Stocks (CTC: ₹54.5 LPA)', 'ONGOING', '2026-11-15'),
(2, 2, 2, 'Microsoft SDE FTE & Intern Drive', 'Engineering roles across Azure Cloud and Developer Division.', 'Software Development Engineer', '₹28.0 LPA - ₹42.0 LPA CTC', 'ONGOING', '2026-11-20'),
(3, 3, 3, 'Amazon University Graduate Hiring', 'FTE positions in Prime Video, AWS Cloud Platform, and Retail Systems.', 'Software Development Engineer I', '₹44.0 LPA CTC', 'COMPLETED', '2026-10-15'),
(4, 4, 4, 'Atlassian Engineering Campus Drive', 'Graduate and Intern Software Engineering opportunities in Bangalore R&D center.', 'Graduate Software Developer', '₹36.0 LPA Base + Equity', 'UPCOMING', '2026-12-05');

-- Stages for Google Drive (#1)
INSERT OR IGNORE INTO placement_stages (id, drive_id, stage_name, stage_order, stage_type, status, venue_or_link, instructions) VALUES
(1, 1, 'Registration & Resume Screening', 1, 'REGISTRATION', 'COMPLETED', 'Online Portal', 'Students meeting minimum 8.0 CGPA criteria screened.'),
(2, 1, 'Pre-Placement Talk (PPT)', 2, 'PPT', 'COMPLETED', 'Main Auditorium & Google Meet', 'Live interactive presentation with Google Bangalore engineering leaders.'),
(3, 1, 'Online Technical Coding Assessment', 3, 'ONLINE_TEST', 'COMPLETED', 'Online Testing Portal (HackerEarth)', '90 mins, 3 algorithmic and data structures coding challenges.'),
(4, 1, 'Technical Interview Round 1 (DS & Algo)', 4, 'TECHNICAL_INTERVIEW_1', 'ACTIVE', 'Google Meet (Rooms 1-12)', '45 mins live coding in Google Docs / CoderPad with Staff Engineer.'),
(5, 1, 'Technical Interview Round 2 (System Design)', 5, 'TECHNICAL_INTERVIEW_2', 'PENDING', 'Google Meet', '45 mins distributed system concepts and concurrency.'),
(6, 1, 'Googliness & Leadership Fit', 6, 'HR_INTERVIEW', 'PENDING', 'Google Meet', 'Behavioral and team culture alignment round.');

-- Pre-Placement Talk for Google Drive (#1)
INSERT OR IGNORE INTO placement_talks (id, drive_id, scheduled_at, venue_or_url, presentation_url, speaker_name, speaker_title, overview) VALUES
(1, 1, '2026-11-10 10:00:00', 'College Main Auditorium & https://meet.google.com/abc-defg-hij', '/assets/docs/google_campus_talk_2026.pdf', 'Siddharth Varma', 'Director of Engineering, Google Cloud', 'An in-depth introduction to Google Cloud architecture, core values, engineering culture, and the recruitment process for new graduates.');

-- Talk Questions
INSERT OR IGNORE INTO placement_talk_questions (id, talk_id, student_id, roll_no, student_name, question, answer, answered_by) VALUES
(1, 1, 1, '21CS001', 'Aarav Sharma', 'What programming languages are permitted in the online coding round?', 'You may use Java, C++, Python 3, or Go. All standard libraries are allowed.', 'Siddharth Varma (Google)'),
(2, 1, 2, '21CS002', 'Bhavya Patel', 'Does Google offer relocation assistance for Bangalore or Hyderabad locations?', 'Yes, comprehensive relocation support including temporary stay and flight tickets is provided.', 'Priya Menon (Google HR)');

-- Applications for Google Drive
INSERT OR IGNORE INTO placement_applications (id, drive_id, student_id, roll_no, student_name, email, status, current_stage_id, ats_score) VALUES
(1, 1, 1, '21CS001', 'Aarav Sharma', 'aarav@college.edu', 'SHORTLISTED', 4, 88),
(2, 1, 2, '21CS002', 'Bhavya Patel', 'bhavya@college.edu', 'SHORTLISTED', 4, 84),
(3, 1, 3, '21CS003', 'Chirag Rao', 'chirag@college.edu', 'INELIGIBLE', 1, 48),
(4, 1, 4, '21IT001', 'Divya Nair', 'divya@college.edu', 'SHORTLISTED', 4, 91);

-- Attendance for Stage 4 (Tech Round 1)
INSERT OR IGNORE INTO placement_attendance (id, drive_id, stage_id, student_id, roll_no, student_name, check_in_time, check_in_mode, marked_by, status) VALUES
(1, 1, 4, 1, '21CS001', 'Aarav Sharma', '2026-11-15 09:12:00', 'QR_SCAN', 'tpo@college.edu', 'PRESENT'),
(2, 1, 4, 2, '21CS002', 'Bhavya Patel', '2026-11-15 09:14:00', 'QR_SCAN', 'tpo@college.edu', 'PRESENT'),
(3, 1, 4, 4, '21IT001', 'Divya Nair', '2026-11-15 09:18:00', 'MANUAL_TPO', 'tpo@college.edu', 'PRESENT');

-- Results marked by TPO
INSERT OR IGNORE INTO placement_results (id, drive_id, stage_id, student_id, roll_no, student_name, result_status, round_feedback, marked_by) VALUES
(1, 1, 3, 1, '21CS001', 'Aarav Sharma', 'SELECTED', 'Passed all test cases with optimal time complexity O(N log N).', 'tpo@college.edu'),
(2, 1, 3, 2, '21CS002', 'Bhavya Patel', 'SELECTED', 'Excellent problem solving on dynamic programming challenge.', 'tpo@college.edu'),
(3, 1, 3, 4, '21IT001', 'Divya Nair', 'SELECTED', 'Full score on coding challenges in record time.', 'tpo@college.edu');

-- Live Updates (SSE feed items)
INSERT OR IGNORE INTO placement_live_updates (id, drive_id, stage_id, title, message, category, priority) VALUES
(1, 1, 2, 'Pre-Placement Talk Concluded', 'Google PPT recording and presentation deck have been uploaded to the portal.', 'ANNOUNCEMENT', 'INFO'),
(2, 1, 3, 'Online Assessment Shortlist Released', 'Candidates who cleared the HackerEarth assessment have been notified. Please report to Meet rooms for Round 1.', 'SHORTLIST', 'HIGH'),
(3, 1, 4, 'Technical Round 1 Commenced', 'Panelists have joined Google Meet rooms. Please ensure your camera and microphone are functional.', 'STAGE_STARTED', 'URGENT');

-- Placed Wall Honorees
INSERT OR IGNORE INTO placement_placed_students (id, drive_id, student_id, roll_no, student_name, company_name, role_offered, package_lpa) VALUES
(1, 3, 1, '21CS001', 'Aarav Sharma', 'Amazon', 'Software Development Engineer I', 44.0),
(2, 3, 4, '21IT001', 'Divya Nair', 'Amazon', 'Software Development Engineer I', 44.0),
(3, NULL, 5, '21CS010', 'Eshaan Gupta', 'Google', 'Software Engineer (L3)', 54.5),
(4, NULL, 6, '21CS015', 'Farhan Ali', 'Microsoft', 'Software Development Engineer', 38.0),
(5, NULL, 7, '21IT005', 'Gayatri Joshi', 'Goldman Sachs', 'Analyst - Engineering', 32.5);

-- Student Feedback
INSERT OR IGNORE INTO placement_feedback (id, drive_id, student_id, roll_no, rating, difficulty, rounds_count, experience_review, tips) VALUES
(1, 3, 1, '21CS001', 5, 'HARD', 4, 'Amazon interview process was very structured. Heavy focus on Leadership Principles along with clean LeetCode medium/hard coding.', 'Prepare behavioral stories using the STAR method for every Amazon LP.'),
(2, 3, 4, '21IT001', 5, 'HARD', 4, 'The bar raiser round was challenging but interviewer was encouraging.', 'Make sure you discuss edge cases and space complexity before coding.');

