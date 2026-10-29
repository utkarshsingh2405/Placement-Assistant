-- ==============================================================================
-- Portal Database Migration (SQLite)
-- Schema V1: Identity Provider, User Management, and Security Audit Logs
-- ==============================================================================

CREATE TABLE IF NOT EXISTS portal_users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    role TEXT NOT NULL, -- STUDENT, TPO, DIRECTOR, COMPANY, ADMIN
    name TEXT NOT NULL,
    roll_no TEXT,
    company_id INTEGER,
    active INTEGER NOT NULL DEFAULT 1,
    reset_token TEXT,
    reset_token_expiry TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE INDEX IF NOT EXISTS idx_portal_users_email ON portal_users(email);
CREATE INDEX IF NOT EXISTS idx_portal_users_roll_no ON portal_users(roll_no);
CREATE INDEX IF NOT EXISTS idx_portal_users_role ON portal_users(role);

CREATE TABLE IF NOT EXISTS audit_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    actor TEXT NOT NULL,
    action TEXT NOT NULL,
    target_type TEXT NOT NULL,
    target_id TEXT,
    before_state TEXT,
    after_state TEXT,
    ip_address TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE INDEX IF NOT EXISTS idx_audit_logs_actor ON audit_logs(actor);
CREATE INDEX IF NOT EXISTS idx_audit_logs_created ON audit_logs(created_at);

-- Default Super Administrator (Password: Admin@123)
INSERT OR IGNORE INTO portal_users (id, email, password_hash, role, name, roll_no, active)
VALUES (1, 'admin@college.edu', '$2a$10$oIVmtQl56m5WIFYGhKAGnOfFi/fYpRZG6myhQzndnnlUX1K67AyM6', 'ADMIN', 'System Administrator', NULL, 1);

-- Default TPO Account (Password: Tpo@123)
INSERT OR IGNORE INTO portal_users (id, email, password_hash, role, name, roll_no, active)
VALUES (2, 'tpo@college.edu', '$2a$10$LgLLbcK33DEOdrRqRxf0T.MKip.6YFh/g.5/tydsK7uiGXW/uooe.', 'TPO', 'Prof. Rajesh Sharma', NULL, 1);

-- Default Director Account (Password: Director@123)
INSERT OR IGNORE INTO portal_users (id, email, password_hash, role, name, roll_no, active)
VALUES (3, 'director@college.edu', '$2a$10$avb8nhg4fzPCSsdNjSaR4OwaMcparwTqAh1uL/yatWq72n2gA1sAa', 'DIRECTOR', 'Dr. V. K. Rao', NULL, 1);

-- Default Recruiter Account (Password: Company@123)
INSERT OR IGNORE INTO portal_users (id, email, password_hash, role, name, roll_no, company_id, active)
VALUES (4, 'hr@google.com', '$2a$10$uEs.rQDqEYyVsO2clnB.1uafZHFgA.ceUv6Fbu0XEyjcLotTYzp..', 'COMPANY', 'Priya Menon (Google HR)', NULL, 1, 1);

-- Default Student Account (Password: Student@123)
INSERT OR IGNORE INTO portal_users (id, email, password_hash, role, name, roll_no, active)
VALUES (5, 'aarav@college.edu', '$2a$10$P6oomM8lmwWfvq9L5nEJQ.f950gtEjBqFchNrNOeV3MoH2mknkaEC', 'STUDENT', 'Aarav Sharma', '21CS001', 1);
