# Placement Assistant — Engineering Team Roles & Architecture Ownership

> **A Comprehensive Technical Specification of Team Roles, Architectural Ownership, and Deliverables**  
> **Project:** Placement Assistance Platform (Multi-Module Enterprise Campus Placement Suite)  
> **Repository:** `utkarshsingh2405/Placement-Assistant`

---

## Executive Summary

The **Placement Assistant** platform is an enterprise-grade, distributed campus placement and academic readiness ecosystem built with **Java (Spring Boot 3.3.4)**, **Spring Cloud Gateway**, **SQLite 3 (WAL mode)**, and a **modern, tactile web presentation layer** (HTML5, Vanilla CSS3, ES6 JavaScript, Bootstrap 5).

The entire system was architected, engineered, and validated by a core team of **5 software engineers**, each maintaining end-to-end technical ownership over a mission-critical pillar of the system:

```
                                  PLACEMENT ASSISTANT PLATFORM
                                 ══════════════════════════════
                                               │
             ┌─────────────────────────────────┼─────────────────────────────────┐
             │                                 │                                 │
     PORTAL GATEWAY & SSO              INSTITUTE PILLAR                  PLACEMENT PILLAR
   [ Bhavya Bhardwaj ]                [ Utkarsh Singh ]                  [ Isha Bajpai ]
   • Unified Reverse Proxy            • Academic Records Ledger         • Live Recruitment Engine
   • Centralized SSO & JWT            • Master Drives & Criteria        • Multi-Stage Stepper
   • Gateway Header Mutation          • 100-Pt ATS Resume Scorer        • Dynamic QR Check-in & PIN
   • Domain Switcher (Alt+D)          • CRT & Class 75% Shortage        • Live SSE Broadcast Ticker
   • Health & Audit System            • Question Bank Practice          • Strictly Guarded Results
             │                                 │                                 │
             └─────────────────────────────────┼─────────────────────────────────┘
                                               │
               ┌───────────────────────────────┴───────────────────────────────┐
               │                                                               │
     FRONTEND & UI/UX DESIGN                                         DATABASE & TESTING
        [ Ankit Kumar ]                                                 [ Mahak Joshi ]
   • Tactile Double-Bezel UI System                               • Isolated SQLite 3 DB Architecture
   • Split-Flap Live Announcement Board                           • Write-Ahead Logging (WAL) Tuning
   • Perforated Hall Tickets & Cards                              • HikariCP Write Serialization
   • Responsive Steppers & Dashboards                             • Automated Test Suites (37/37 Passing)
   • SSE Real-Time Client Receivers                               • Strict Security Slice Assertions
```

---

## Detailed Team Profiles & Technical Responsibilities

---

### 1. Utkarsh Singh — Backend Lead & Institute Pillar Architect
* **Primary Domain:** Institute Pillar (`institute/`, Port `8081`, Context Path `/institute`, Database `data/institute.db`)
* **Core Technology Stack:** Java 17/25, Spring Boot 3.3.4, Spring Data JPA, Hibernate Community Dialects, SQLite JDBC, Apache PDFBox, OpenCSV, Apache POI

#### Architectural Scope & Leadership
Utkarsh designed and developed the foundational **Institute Pillar**, which acts as the official Registrar’s academic repository and readiness engine for the campus. He enforced strict data integrity and domain encapsulation, ensuring that student eligibility rules, academic histories, and preparation modules run independently without coupling to recruitment round workflows.

#### Key Engineering Deliverables
1. **Master Student Academic Records & Eligibility Engine:**
   - Designed schema and service logic for student profiles, CGPA records, active backlogs, department branches, and placement clearance statuses.
   - Built the real-time eligibility validator that evaluates students against company drive criteria (e.g., minimum CGPA cutoff, maximum allowed backlogs, eligible disciplines).
   - Implemented high-throughput CSV bulk import and export capabilities for academic administrative officers.
2. **On-Campus Master Drives & Round Scheduling:**
   - Created company drive registry, package brackets (Normal, Dream > 10 LPA, Super Dream > 20 LPA), eligibility rules, registration deadlines, and interview round sequences.
3. **100-Point Algorithmic Resume ATS Scorer:**
   - Developed an automated PDF resume parser using **Apache PDFBox 3.0.3**.
   - Implemented an algorithmic ATS evaluation model grading resumes across 5 weighted categories:
     - Contact & Social Profiles (15 pts)
     - Technical & Soft Skills (25 pts)
     - Education & Academic Credentials (20 pts)
     - Work Experience & Projects (25 pts)
     - Layout, Typography & Formatting (15 pts)
   - Generates instant keyword match analysis, missing critical competencies, and remediation suggestions for candidates.
4. **Question Bank & Timed Practice Simulation:**
   - Engineered categorized question repositories spanning Quantitative Aptitude, Logical Reasoning, Technical/Coding, and HR questions.
   - Built timed self-practice modules with score computation, instant explanations, and performance history.
5. **CRT & Classroom Attendance System (75% Rule):**
   - Engineered multi-roster tracking for Campus Recruitment Training (CRT) and regular classroom lectures.
   - Implemented automated triggers calculating attendance percentages and flagging students with < 75% attendance for placement disqualification alerts.
6. **Curated Opportunities Feed:**
   - Built management and publishing modules for external Hackathons, Paid Internships, and Off-Campus Job drives with bookmarking functionality.
7. **Inter-Pillar Integration API (`/institute/api/integration/**`):**
   - Exposed secure, RESTful endpoints authenticated via `X-API-KEY` (`inst-sec-key-7f9a12b4e8c345d6`) allowing the Placement Pillar to query student validation data without direct database sharing.

---

### 2. Bhavya Bhardwaj — Portal Gateway & Security / SSO Architect
* **Primary Domain:** Portal Gateway & Security (`portal/`, Port `8080`, Context Path `/`, Database `data/portal.db`)
* **Core Technology Stack:** Spring Boot 3.3.4, Spring Cloud Gateway 2023.0.3, Spring Security 6, JJWT (io.jsonwebtoken 0.12.6), Reactive WebFlux

#### Architectural Scope & Leadership
Bhavya architected the single-entry gateway and security boundary of the platform. By introducing **Spring Cloud Gateway** as the reverse proxy on port 8080, Bhavya eliminated the complexity of multi-port access for users while establishing a secure, unified **Single Sign-On (SSO)** perimeter.

#### Key Engineering Deliverables
1. **Unified Reverse Proxy Routing:**
   - Configured high-performance Spring Cloud Gateway route definitions directing traffic cleanly:
     - Route `/institute/**` &rarr; forwarded to Institute Pillar (`http://localhost:8081`)
     - Route `/placement/**` &rarr; forwarded to Placement Pillar (`http://localhost:8082`)
     - Route `/**` &rarr; served by Portal Gateway (`http://localhost:8080`)
2. **Centralized Single Sign-On (SSO) & JWT Engine:**
   - Engineered the centralized authentication service issuing signed HMAC-SHA256 JWT tokens upon login (`/api/auth/login`).
   - Implemented dual-token handling supporting both HTTP-only session cookies and `Authorization: Bearer <token>` in `localStorage`.
   - Designed the JWT token refresh and expiration cycle (24-hour access token, 7-day refresh token).
3. **Gateway Header Mutation & Context Forwarding:**
   - Built custom Gateway Pre-Filters (`PortalJwtFilter` and `JwtAuthFilter`) that inspect the incoming JWT, validate claims, and mutate downstream request headers:
     - `X-User-Email`: Authenticated user's email address
     - `X-User-Role`: User's authorized role (`STUDENT`, `COMPANY`, `TPO`, `DIRECTOR`, `ADMIN`)
     - `X-User-Id`: Unique database user ID
   - Enabled downstream pillars to operate with zero-overhead stateless authentication without re-authenticating against credentials.
4. **Domain Chooser & Keyboard Navigation (`Alt+D`):**
   - Built the centralized Domain Chooser gateway (`/choose-domain.html`) and coordinated the seamless domain switching mechanism.
5. **Security Perimeter & Audit Logging:**
   - Enforced CORS policies, CSRF protections, and route-level authorization guards.
   - Built the centralized audit logging service capturing authentication events, failed logins, credential modifications, and administrative activities into `data/portal.db`.
6. **Gateway Health & Domain Status Monitoring:**
   - Implemented diagnostic health check aggregators reporting real-time status of all backend pillars.

---

### 3. Ankit Kumar — Lead Frontend Engineer & UI/UX Designer
* **Primary Domain:** Presentation Layer & User Experience (`shared-web/`, `portal/.../static/`, `institute/.../static/`, `placement/.../static/`)
* **Core Technology Stack:** Modern HTML5, Semantic CSS3 (CSS Variables, Flexbox, Grid), Vanilla ES6+ JavaScript, Bootstrap 5, Phosphor Light Icon Sprite, Google Fonts (`Instrument Serif`, `Geist`)

#### Architectural Scope & Leadership
Ankit Kumar designed and implemented the entire user interface and design system across all three pillars. In strict compliance with the **Google Stitch 7-Section Design System Specification** (`docs/DESIGN.md`), Ankit rejected generic templates and corporate software clichés, engineering a tactile, editorial aesthetic inspired by physical university notice boards, arrival/departure boards, and registrar archives.

#### Key Engineering Deliverables
1. **Tactile Design System & Token Architecture:**
   - Created the core CSS design tokens (`--ground`, `--surface`, `--surface-2`, `--ink`, `--brass`, `--institute`, `--placement`).
   - Implemented **Tonal Double-Bezel** containers (32px outer shell with `--surface-2` cradling a 26px concentric `--surface` core) providing physical depth without synthetic drop shadows or glassmorphism.
   - Configured fluid typography scaling via CSS `clamp()` using `Instrument Serif` for headlines and `Geist` with tabular figures (`font-variant-numeric: tabular-nums`) for data tables.
   - Embedded SVG turbulence paper micro-grain texture across the entire platform.
2. **Public Landing Surfaces & Domain Chooser:**
   - Built the Portal landing page with an authoritative Hero section, dynamic live recruitment ticker, and high-impact metrics.
   - Developed the interactive Domain Chooser cards and the floating top navigation Domain Switcher pill with keyboard shortcut `Alt+D` support and unread notification badges.
3. **Multi-Stage Recruitment Pipeline Stepper:**
   - Created the visual 4-stage recruitment stepper (Aptitude &rarr; Tech 1 &rarr; Tech 2 &rarr; HR) allowing candidates to inspect round statuses, cutoffs, and schedules.
   - Built admit card style **Perforated Hall Tickets** with tear-off stubs, QR verification placeholders, and animated stamp states.
4. **Split-Flap Mechanical Live Announcement Board:**
   - Designed and built a physical departure-board mechanical display component with dual-leaf 3D rotating cards, synchronized to live event updates.
5. **Interactive Role-Based Dashboards:**
   - Engineered dedicated, responsive dashboards for **Students**, **TPOs**, **Company Recruiters**, and **Directors**.
   - Built dual-mode data ledger tables (View-Only mode with lock indicators vs. Editing mode for authorized officers).
6. **Real-Time Client-Side SSE Consumers:**
   - Programmed the JavaScript EventSource clients that listen to Server-Sent Events (SSE), dynamically updating live news banners, qualification notices, and ticker items without page reloads.
7. **Mobile-First Responsive Layouts & Accessibility:**
   - Designed fluid layouts collapsing gracefully across mobile (`< 480px`), tablet (`768px`), laptop (`1024px`), and desktop (`1440px`).
   - Ensured all color pairs satisfy **WCAG 2.2 AA** contrast standards with zero pure blacks (`#000000`).

---

### 4. Isha Bajpai — Placement Pillar Lead & Recruitment Engine Architect
* **Primary Domain:** Placement Pillar (`placement/`, Port `8082`, Context Path `/placement`, Database `data/placement.db`)
* **Core Technology Stack:** Java 17/25, Spring Boot 3.3.4, Spring WebMVC, Spring Security 6, ZXing (QR Code), Server-Sent Events (SSE), Web Push (VAPID)

#### Architectural Scope & Leadership
Isha led the development of the **Placement Pillar**, the high-velocity live operational engine of campus recruitment drives. She designed the candidate progression workflow, real-time interview coordination, hall check-in systems, and established strict security protocols governing candidate selection authority.

#### Key Engineering Deliverables
1. **Live Campus Recruitment Drive Engine:**
   - Engineered drive management modules allowing TPOs and Recruiters to configure live drives, branch restrictions, salary packages, and deadlines.
   - Built the one-click student drive application workflow with automated pre-requisite verification.
2. **Multi-Stage Candidate Evaluation Pipeline:**
   - Developed the sequential stage evaluation service supporting progressive rounds: Aptitude Test &rarr; Technical Round 1 &rarr; Technical Round 2 &rarr; HR Interview.
   - Implemented candidate progression controls, allowing evaluators to promote qualified candidates to subsequent rounds while recording scores and feedback.
3. **Strictly Role-Guarded Candidate Selection Authority:**
   - Enforced stringent role-based access control around final candidate results (`SELECTED` / `NOT_SELECTED`).
   - **Strict Security Rule:** Only **TPO** and **DIRECTOR** roles are authorized to mark final selections.
   - Student or Recruiter attempts to post to selection endpoints immediately trigger an **HTTP 403 Forbidden** exception, backed by automated integration tests.
4. **Pre-Placement Talks (PPTs) & Real-Time Q&A:**
   - Created the PPT presentation management module with slide deck viewers, schedule announcements, and meeting links.
   - Implemented an interactive live Q&A engine allowing candidates to submit questions and upvote peers' questions in real time.
5. **Dynamic QR Code Hall Attendance & PIN Verification:**
   - Integrated **ZXing 3.5.3** to generate dynamic session QR codes for on-site recruitment test venues.
   - Built dual-mode check-in supporting mobile camera scanning or manual entry of a 6-character session PIN to record hall attendance.
6. **Server-Sent Events (SSE) Live Broadcast Service:**
   - Engineered the `LiveUpdateController` and `SseEmitter` service streaming real-time recruitment updates, shortlisted student announcements, and urgent schedule changes directly to connected browsers with zero latency.
7. **Placed Students Wall of Fame:**
   - Developed the Wall of Fame registry categorizing student achievements into **Dream Offers** (> 10 LPA) and **Super Dream Offers** (> 20 LPA), displaying packages, company logos, and roles.
8. **Candidate Interview Experiences & Peer Review:**
   - Built the feedback repository allowing placed students to share round-by-round interview questions, preparation tips, and difficulty ratings.
9. **Resilient Offline Architecture:**
   - Created the `RestInstituteClient` with automated fallback to `StubInstituteClient`. If the Institute Pillar is undergoing maintenance or unreachable, Placement continues operating with stub data, preventing system outages.

---

### 5. Mahak Joshi — Database Architect & Lead Test Engineer
* **Primary Domain:** Database Architecture, Persistence Isolation & Automated Testing (`data/`, Schema Migrations, Unit/Integration/Security Test Suites)
* **Core Technology Stack:** SQLite 3 (WAL Mode), HikariCP, Spring Data JPA, Hibernate Community Dialects, Flyway, Maven Surefire, JUnit 5, Mockito, Spring Security Test

#### Architectural Scope & Leadership
Mahak had complete ownership over multi-database persistence isolation, high-concurrency database stability, and automated test verification across the platform. Mahak ensured that all three pillars maintain strict data autonomy without cross-database bleeding, while engineering comprehensive automated test suites to guarantee functional correctness, data integrity, and strict security compliance.

#### Key Engineering Deliverables
1. **Isolated Multi-Database Architecture:**
   - Enforced the architectural golden rule: **Zero Database Sharing**.
   - Architected separate, dedicated SQLite 3 database files for each pillar:
     - `data/portal.db` &mdash; Authentication, user identities, sessions, audit trails
     - `data/institute.db` &mdash; Academic records, student profiles, master drives, questions, attendance rosters
     - `data/placement.db` &mdash; Live drives, student applications, stages, QR codes, PPTs, evaluation results
2. **SQLite High-Concurrency Tuning & Connection Pooling:**
   - Solved SQLite single-writer concurrency limits by configuring **HikariCP** connection pools with `maximum-pool-size: 1` on write operations, serializing transactions to completely prevent `SQLITE_BUSY` errors.
   - Enabled **Write-Ahead Logging (WAL)** across all databases (`PRAGMA journal_mode=WAL;`), permitting non-blocking concurrent readers during active write transactions.
   - Enforced relational integrity and cascading constraints with `PRAGMA foreign_keys = ON;`.
   - Standardized timestamp and audit fields across all schemas using ISO-8601 formatting.
3. **Database Schema Design & Flyway Migrations:**
   - Designed normalized entity schemas for students, academic records, drives, stages, attendance sessions, and evaluation records.
   - Configured Flyway versioned SQL migrations (`V1__init.sql`) for reproducible database initialization and automated schema versioning across environments.
   - Established online WAL database snapshot and backup routines to ensure zero data loss.
4. **Comprehensive Automated Test Framework (37/37 Tests Passed):**
   - Architected the test framework using **JUnit 5**, **Spring Boot Test**, and **Mockito**, achieving a 100% test pass rate across the platform.
   - **Institute Test Suite (30/30 Tests Passing):**
     - Unit and integration tests for student master records, CGPA threshold filtering, and backlog checks.
     - Verification tests for the 100-point algorithmic ATS resume scoring calculations.
     - Automated validation of attendance percentage algorithms and the 75% shortage alert triggers.
     - Integration controller tests for the authenticated `/institute/api/integration/**` endpoints.
   - **Placement Test Suite (7/7 Tests Passing):**
     - End-to-end recruitment pipeline progression and round qualification flow tests.
     - QR code generation and PIN check-in verification tests.
     - **Strict Security Test Assertions:** Automated tests verifying that unauthorized attempts by Students or Recruiters to modify candidate selection results immediately fail with **HTTP 403 Forbidden**, ensuring only TPOs and Directors have write authority.

---

## Technical RACI Matrix (Responsibility Assignment)

| Capability / Module | Utkarsh Singh | Bhavya Bhardwaj | Ankit Kumar | Isha Bajpai | Mahak Joshi |
| :--- | :---: | :---: | :---: | :---: | :---: |
| **Portal Gateway & Routing** | Consulted | **Accountable** | Informed | Consulted | Responsible |
| **SSO & JWT Authentication** | Consulted | **Accountable** | Informed | Consulted | Responsible |
| **Design System & UI/UX** | Informed | Informed | **Accountable** | Informed | Informed |
| **Student Academic Database** | **Accountable** | Informed | Responsible | Consulted | Responsible |
| **Resume ATS Scorer (PDFBox)**| **Accountable** | Informed | Responsible | Informed | Consulted |
| **Live Recruitment Engine** | Consulted | Informed | Responsible | **Accountable** | Responsible |
| **Stage Stepper & Selection Security** | Informed | Informed | Responsible | **Accountable** | Responsible |
| **Pre-Placement Talks (PPT) & Q&A** | Informed | Informed | Responsible | **Accountable** | Consulted |
| **QR Code Attendance System** | Informed | Informed | Responsible | **Accountable** | Consulted |
| **Server-Sent Events (SSE)** | Informed | Informed | Responsible | **Accountable** | Consulted |
| **Database Architecture (SQLite WAL)** | Consulted | Consulted | Informed | Consulted | **Accountable** |
| **Database Schema & Migrations** | Consulted | Consulted | Informed | Consulted | **Accountable** |
| **Automated Testing Suite (37/37)** | Responsible | Responsible | Informed | Responsible | **Accountable** |
| **Security & Access Slice Tests** | Responsible | Responsible | Informed | Responsible | **Accountable** |

*Legend: **Accountable** = Primary Lead & Owner | **Responsible** = Contributed Code / Implementation | **Consulted** = Architectural Reviewer | **Informed** = Upstream/Downstream Consumer*

---

## Architectural Integrity: The 3 Golden Rules

The engineering team maintained three non-negotiable architectural boundaries throughout development:

1. **Zero Database Sharing:**  
   Each pillar exclusively owns its SQLite database (`portal.db`, `institute.db`, `placement.db`). Direct connections across database files are strictly prohibited.
2. **Zero Cross-Pillar Java Class Imports:**  
   The `institute` and `placement` modules do not import each other's classes or entities. Common data transfer objects and utility classes reside strictly within `shared-lib`.
3. **Resilient Decoupled Fallback:**  
   All inter-pillar communication takes place via authenticated REST APIs (`X-API-KEY`). If the Institute Pillar is offline, the Placement Pillar automatically switches to stub data (`StubInstituteClient`) ensuring zero user-facing crashes.

---

## Summary of Completed Deliverables

- **3 Backend Microservices:** Portal Gateway (`8080`), Institute Pillar (`8081`), Placement Pillar (`8082`).
- **1 Shared Library:** Reusable security, DTOs, and exception models (`shared-lib`).
- **1 Presentation Suite:** Shared styles, textures, and component scripts (`shared-web`) feeding 3 frontend targets.
- **3 Isolated SQLite Databases:** Clean schema separation with WAL mode & HikariCP concurrency tuning.
- **37 Automated Tests:** 100% pass rate across security assertions, integration flows, and algorithmic test suites.
