# Institute Pillar Database Schema & Architecture

The Institute pillar uses **SQLite 3** as its database engine. SQLite was selected for zero-configuration, single-file portability, high query performance, and straightforward containerization.

---

## 1. Concurrency & Connection Pooling Architecture

SQLite supports multiple concurrent readers and a single concurrent writer. To ensure ACID compliance without lock contention:

1. **HikariCP Connection Pool**: Configured with `maximum-pool-size: 1` on write operations to serialize SQLite transactions and prevent `SQLITE_BUSY` errors.
2. **Write-Ahead Logging (WAL)**: Enabled on startup via `PRAGMA journal_mode=WAL;`, allowing simultaneous reads while a write transaction is executing.
3. **Foreign Key Enforcement**: Explicitly activated via `PRAGMA foreign_keys = ON;` upon connection establishment.
4. **Dates and Timestamps**: Stored consistently as ISO-8601 strings (`YYYY-MM-DDTHH:MM:SSZ`).

---

## 2. Entity-Relationship (ER) Diagram

```mermaid
erDiagram
    USERS ||--o| STUDENTS : "maps to"
    USERS ||--o{ BOOKMARKS : "saves"
    COMPANIES ||--o{ DRIVES : "hosts"
    DRIVES ||--o{ DRIVE_ROUNDS : "contains"
    DRIVE_ROUNDS ||--o{ ROUND_RESULTS : "evaluates"
    STUDENTS ||--o{ ROUND_RESULTS : "participates in"
    STUDENTS ||--o{ RESUMES : "submits"
    STUDENTS ||--o{ ATTENDANCES : "records"
    CRT_SESSIONS ||--o{ ATTENDANCES : "logs"
    CLASS_SESSIONS ||--o{ ATTENDANCES : "logs"

    USERS {
        INTEGER id PK
        TEXT email UK
        TEXT password_hash
        TEXT role
        TEXT name
        TEXT created_at
        TEXT updated_at
    }

    STUDENTS {
        INTEGER id PK
        INTEGER user_id FK
        TEXT roll_no UK
        TEXT name
        TEXT email UK
        TEXT phone
        TEXT branch
        INTEGER year
        REAL cgpa
        INTEGER backlogs
        BOOLEAN placement_eligible
        TEXT resume_url
        TEXT created_at
        TEXT updated_at
    }

    COMPANIES {
        INTEGER id PK
        TEXT name UK
        TEXT website
        TEXT industry
        TEXT contact_person
        TEXT contact_email
        TEXT created_at
        TEXT updated_at
    }

    DRIVES {
        INTEGER id PK
        INTEGER company_id FK
        TEXT title
        TEXT description
        TEXT roles
        REAL package_min
        REAL package_max
        REAL eligibility_cgpa
        TEXT eligible_branches
        INTEGER max_backlogs
        TEXT drive_date
        TEXT last_date_to_apply
        TEXT location
        TEXT status
        TEXT created_at
        TEXT updated_at
    }

    DRIVE_ROUNDS {
        INTEGER id PK
        INTEGER drive_id FK
        TEXT round_name
        INTEGER sequence
        TEXT round_type
        TEXT round_date
        REAL cutoff_score
        TEXT platform_venue
        TEXT instructions
        TEXT status
        TEXT created_at
        TEXT updated_at
    }

    ROUND_RESULTS {
        INTEGER id PK
        INTEGER round_id FK
        INTEGER student_id FK
        REAL score
        TEXT status
        TEXT feedback
        TEXT created_at
        TEXT updated_at
    }

    QUESTIONS {
        INTEGER id PK
        TEXT title
        TEXT category
        TEXT difficulty
        TEXT company
        TEXT tags
        TEXT question_text
        TEXT option_a
        TEXT option_b
        TEXT option_c
        TEXT option_d
        TEXT correct_answer
        TEXT explanation
        TEXT created_at
        TEXT updated_at
    }

    CRT_SESSIONS {
        INTEGER id PK
        TEXT topic
        TEXT trainer
        TEXT session_date
        INTEGER duration_hours
        TEXT batch
        TEXT notes
        TEXT created_at
        TEXT updated_at
    }

    CLASS_SESSIONS {
        INTEGER id PK
        TEXT subject_name
        TEXT branch
        INTEGER semester
        TEXT section
        TEXT session_date
        TEXT period
        TEXT faculty_name
        TEXT created_at
        TEXT updated_at
    }

    ATTENDANCES {
        INTEGER id PK
        INTEGER student_id FK
        TEXT session_type
        INTEGER session_id
        TEXT status
        TEXT remarks
        TEXT created_at
        TEXT updated_at
    }

    RESUMES {
        INTEGER id PK
        INTEGER student_id FK
        TEXT file_name
        TEXT file_path
        TEXT target_role
        INTEGER ats_score
        INTEGER contact_score
        INTEGER skills_score
        INTEGER education_score
        INTEGER experience_score
        INTEGER formatting_score
        TEXT extracted_text
        TEXT matched_keywords
        TEXT missing_keywords
        TEXT suggestions
        TEXT status
        TEXT created_at
        TEXT updated_at
    }

    HACKATHONS {
        INTEGER id PK
        TEXT title
        TEXT organizer
        TEXT description
        TEXT mode
        TEXT location
        TEXT prize_pool
        TEXT registration_deadline
        TEXT start_date
        TEXT end_date
        TEXT apply_url
        TEXT tags
        INTEGER min_team_size
        INTEGER max_team_size
        TEXT status
        TEXT created_at
        TEXT updated_at
    }

    INTERNSHIPS {
        INTEGER id PK
        TEXT title
        TEXT company
        TEXT description
        TEXT mode
        TEXT location
        TEXT stipend
        INTEGER duration_months
        TEXT registration_deadline
        TEXT start_date
        TEXT apply_url
        TEXT skills_required
        TEXT status
        TEXT created_at
        TEXT updated_at
    }

    OFFCAMPUS_JOBS {
        INTEGER id PK
        TEXT title
        TEXT company
        TEXT description
        TEXT mode
        TEXT location
        TEXT salary_ctc
        TEXT experience_required
        TEXT batch_eligible
        TEXT registration_deadline
        TEXT apply_url
        TEXT referral_code_notes
        TEXT skills_required
        TEXT status
        TEXT created_at
        TEXT updated_at
    }

    BOOKMARKS {
        INTEGER id PK
        INTEGER user_id FK
        TEXT item_type
        INTEGER item_id
        TEXT created_at
    }
```
