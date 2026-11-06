# Placement Assistance Platform — System Architecture, Workflow & Technical Guide

---

## 1. Executive Summary & Platform Overview

The **Placement Assistance Platform** is an enterprise-grade, multi-pillar ecosystem designed for engineering colleges and universities to manage the end-to-end campus recruitment lifecycle. It bridges academic performance tracking (classroom attendance, student dossiers, question banks, and resume ATS diagnostics) with high-velocity placement operations (campus drives, eligibility filtering, stage progression, hall ticket issuance, and offer analytics).

The platform consolidates three autonomous pillars into a **single, unified gateway**:
1. **Portal Gateway & SSO (`Port 8080 / 8088`)**: The primary reverse-proxy entrypoint, authentication identity broker (JWT), security audit system, and dual-domain chooser.
2. **Academic Institute Pillar (`Port 8081`, Context `/institute`)**: Manages student academic records, 75% attendance compliance, technical question banks, resume ATS analysis, and hackathons/internships.
3. **Corporate Placement Pillar (`Port 8082`, Context `/placement`)**: Oversees company recruitment drives, stage-by-stage pipelines, candidate shortlisting, QR-coded hall tickets, and verified placement offers.

---

## 2. High-Level System Architecture

The platform uses a **Supervised Multi-Service Architecture** that allows all three pillars to run either as isolated microservices or embedded inside a single unified container orchestrated by the **`PillarSupervisor`**.

```
                           [ CLIENT BROWSER ]
                   Desktop / Tablet / Mobile Responsive
                                    │
                                    │ HTTP / HTTPS (Port 8080 or 8088)
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                   UNIFIED PORTAL GATEWAY CONTAINER                     │
│                                                                        │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                    REVERSE PROXY & GATEWAY                       │  │
│  │  - Single Sign-On (JWT Auth Filter)                              │  │
│  │  - Dynamic Port Resolver (8080 fallback to 8088)                 │  │
│  │  - Route Proxying (/institute/** -> 8081, /placement/** -> 8082) │  │
│  │  - PillarSupervisor (Embedded Child Process Orchestration)       │  │
│  └───────────────┬──────────────────────────────────┬───────────────┘  │
│                  │                                  │                  │
│       Forward to Localhost:8081          Forward to Localhost:8082     │
│                  ▼                                  ▼                  │
│  ┌──────────────────────────────┐    ┌──────────────────────────────┐  │
│  │       INSTITUTE PILLAR       │    │       PLACEMENT PILLAR       │  │
│  │  - Academic Dossiers         │    │  - Master Campus Drives      │  │
│  │  - 75% Attendance Compliance │    │  - Stage Pipeline (OA -> HR) │  │
│  │  - Question Bank & Practice  │    │  - Admit Passes & QR Tickets │  │
│  │  - ATS Resume Scorer         │    │  - Verified Offer Slates     │  │
│  └──────────────┬───────────────┘    └──────────────┬───────────────┘  │
│                 │                                   │                  │
└─────────────────┼───────────────────────────────────┼──────────────────┘
                  ▼                                   ▼
         ┌─────────────────┐                 ┌─────────────────┐
         │  institute.db   │                 │  placement.db   │
         │  (SQLite/MySQL) │                 │  (SQLite/MySQL) │
         └─────────────────┘                 └─────────────────┘
```

---

## 3. Technology Stack

### Backend Technologies
- **Runtime Environment:** Java 17 LTS (Eclipse Temurin / OpenJDK 17+)
- **Framework:** Spring Boot 3.3.4
- **Security:** Spring Security with stateless JWT (`jjwt 0.12.5`), BCrypt password hashing, and role-based access control (`ROLE_STUDENT`, `ROLE_TPO`, `ROLE_DIRECTOR`, `ROLE_ADMIN`, `ROLE_RECRUITER`)
- **Persistence & ORM:** Spring Data JPA / Hibernate 6.x
- **Database Engine (Dual-Mode):**
  - *Local Development / Zero-Config:* SQLite 3 via `sqlite-jdbc`
  - *Production / Enterprise:* MySQL 8.x Connector/J with connection pooling (HikariCP)
- **Embedded Proxying:** Apache HttpClient / Spring `RestClient` with dynamic timeout resilience

### Frontend & Design System
- **HTML5:** Semantic HTML5 templates with pre-baked inline theme bootstrapper to eliminate Theme Flash (FOUC).
- **CSS Architecture:** Zero-dependency, modular tokenized design system (`00-tokens.css` through `07-domains.css`):
  - Strict color variables with CSS `color-scheme: light dark` declarations.
  - Native dark reader locks (`<meta name="darkreader-lock">`).
  - High-specificity selectors guaranteeing true enterprise white (`#FFFFFF` / `#F8FAFC`) in light mode and nocturnal obsidian (`#0C1214` / `#121C1F`) in dark mode.
- **Typography:** Instrument Serif (Display Editorial Headings), Geist / Inter (Body & Data Grids), JetBrains Mono (Split-Flap Timetables).
- **JavaScript Core:** Vanilla ES6+ modules (`auth.js`, `api.js`, `ui.js`, `theme.js`, `domain-switcher.js`) without heavyweight JavaScript framework overhead.

### DevOps & Deployment
- **Packaging:** Multi-module Apache Maven (`mvnw.cmd` / `mvnw`)
- **Containerization:** Multi-stage Dockerfile (`eclipse-temurin:17-jre-jammy`)
- **Process Orchestration:** Single-console batch launcher (`start.bat` / `stop.bat`) and Spring `PillarSupervisor`

---

## 4. Directory Structure & Folder Flow

```
Placement Assistant 2/
│
├── start.bat                     # Windows single-click launcher (builds, starts all 3 pillars, opens browser)
├── stop.bat                      # Windows clean shutdown script (frees ports 8080, 8081, 8082, 8088)
├── Dockerfile                    # Production multi-stage Dockerfile for Railway / Render
├── docker-compose.yml            # Multi-container local orchestration (Portal, Institute, Placement, MySQL)
├── pom.xml                       # Root Maven reactor POM managing all submodules
│
├── database/                     # Active SQLite database storage (pre-seeded)
│   ├── portal.db                 # Auth credentials, user profiles, audit logs
│   ├── institute.db              # Academic records, attendance records, questions
│   └── placement.db              # Drives, job applications, round progression
│
├── data/                         # Uploaded binary assets
│   ├── portal/uploads/           # Branding assets & badges
│   ├── institute/uploads/resumes # Candidate uploaded PDF/DOCX resumes
│   └── placement/uploads/        # Corporate JDs, results, verified offer letters
│
├── backend/                      # Spring Boot backend source tree
│   ├── pom.xml                   # Backend parent POM
│   ├── shared-lib/               # Reusable Java domain models, DTOs, JWT utils, & Security filters
│   │   └── src/main/java/com/placementassist/shared/
│   │       ├── security/         # JwtTokenProvider, JwtAuthFilter, PasswordEncoder
│   │       └── dto/              # Standard ApiResponse, UserDto, AuthRequest
│   │
│   ├── shared-web/               # CANONICAL Web Assets (Single Source of Truth)
│   │   ├── css/                  # 00-tokens.css to 07-domains.css, landing.css
│   │   ├── js/                   # core/api.js, core/auth.js, core/theme.js, core/ui.js
│   │   └── textures/             # Paper grain SVG textures, icons
│   │
│   ├── portal/                   # Pillar 01: Gateway, Identity Broker & Proxy
│   │   ├── src/main/java/.../portal/
│   │   │   ├── auth/             # Login, Registration, Password Reset controllers
│   │   │   ├── proxy/            # Reverse proxy forwarding to 8081 & 8082
│   │   │   ├── status/           # PillarHealthService (live status monitor)
│   │   │   └── supervisor/       # PillarSupervisor (child process manager)
│   │   └── src/main/resources/static/ # Landing page (index.html), choose-domain.html, login.html
│   │
│   ├── institute/                # Pillar 02: Academic Institute Operations
│   │   ├── src/main/java/.../institute/modules/
│   │   │   ├── student/          # Student dossiers & directory management
│   │   │   ├── attendance/       # Class & CRT session attendance with 75% calculator
│   │   │   ├── questionbank/     # Technical problem repository & practice arena
│   │   │   ├── resume/           # ATS resume scoring & keyword diagnostics
│   │   │   └── hackathons/       # Extracurricular competitions & internships
│   │   └── src/main/resources/static/ # Institute UI pages & dashboards
│   │
│   └── placement/                # Pillar 03: Corporate Recruitment Engine
│       ├── src/main/java/.../placement/modules/
│       │   ├── drives/           # Master recruitment drives & corporate partner catalog
│       │   ├── applications/     # Candidate application lifecycle & eligibility gate
│       │   ├── stages/           # Multi-round progression board (OA -> Tech -> HR)
│       │   ├── attendance/       # QR code attendance verification for interview rounds
│       │   └── results/          # Offer verification, placement wall & analytics
│       └── src/main/resources/static/ # Placement UI pages & live recruitment boards
│
└── docs/                         # Architecture diagrams, specifications & design contracts
```

---

## 5. End-to-End Workflow & Data Lifecycle

### 5.1 Authentication & Domain Routing
1. **User Landing:** User accesses `http://localhost:8080/`. The high-contrast editorial landing page presents live platform statistics.
2. **SSO Sign-In:** User navigates to `/login.html` and inputs credentials.
3. **JWT Issuance:** Portal Gateway authenticates against `portal.db` using BCrypt. Upon success, a signed JWT containing `userId`, `email`, `role`, and `rollNo` is returned.
4. **Client Storage:** `auth.js` stores the JWT in `localStorage` under `pa_auth_token` and saves user metadata in `pa_user`.
5. **Gateway Routing:** User visits `/choose-domain.html` and selects either **Academic Institute** or **Placement Season**.

### 5.2 Academic Eligibility & Compliance Workflow
1. **Attendance Tracking:** Faculty log daily attendance via `/institute/pages/4-attendance/class/mark-class.html`.
2. **75% Rule Engine:** The `AttendanceService` computes real-time attendance percentage for every registered student:
   $$\text{Attendance \%} = \left(\frac{\text{Sessions Attended}}{\text{Total Conducted Sessions}}\right) \times 100$$
3. If attendance drops below 75%, an academic compliance warning flag is appended to the student's profile.

### 5.3 Recruitment Drive & Application Progression
1. **Drive Publication:** Placement Officer registers a new drive (e.g., *Google India — SDE-1, 32 LPA*) specifying cutoffs (e.g., CGPA $\ge 8.0$, 0 backlogs, allowed branches: CSE, IT).
2. **Student Application:** Student browses `/placement/pages/1-drives/live-drives.html`.
3. **Automated Eligibility Check:** Before an application is accepted, the backend verifies:
   - Does student's CGPA meet or exceed the drive cutoff?
   - Is attendance compliance satisfied?
   - Does student have any active backlogs?
4. **Admit Pass Generation:** If qualified, the status changes to `APPLIED`, and the student receives a digital **Hall Ticket** with a QR token.
5. **Round-by-Round Advancement:**
   $$\text{Applied} \longrightarrow \text{Online Assessment} \longrightarrow \text{Technical Interview} \longrightarrow \text{HR Discussion} \longrightarrow \text{Offered}$$
6. **Offer Confirmation:** TPO confirms selected candidates, automatically updating the **Placed Students Wall** and dashboard metrics.

---

## 6. Shared Utilities & Architectural Invariants

### 6.1 ThemeManager (`theme.js` & `theme-init.js`)
- Enforces instant theme resolution before DOM parsing to prevent flickering.
- Shares the theme preference across ports via `document.cookie` (`SameSite=Lax`), `localStorage`, and `window.name`.
- Toggles between `:root` (light mode) and `html[data-theme="dark"]` with crisp SVG icons.

### 6.2 UI Manager (`ui.js`)
- Standardizes toasts, confirmation modals, loading skeletons, and empty states.
- Automatically injects the universal topbar, domain switcher pill, notification bell, and responsive app rail across all screens.

### 6.3 PillarSupervisor (`PillarSupervisor.java`)
- Embeds inside Portal Gateway.
- Detects whether Institute and Placement JARs are running. If not, spawns them as managed child processes.
- Pipes child process stdout/stderr into the unified console with `[INSTITUTE]` and `[PLACEMENT]` prefixes.
- Shuts down all child processes gracefully when Portal terminates.

---

## 7. Security & Failure-Mode Defenses

1. **Role-Based Endpoint Protection:** Every REST controller validates JWT claims against authorized authorities (`hasAnyRole('TPO', 'DIRECTOR')`).
2. **Port Conflict Resilience:** If port `8080` is occupied by another local service (e.g., Oracle or Jenkins), the launcher automatically rebinds Portal Gateway to port `8088`.
3. **Database Fallback:** If a remote or local MySQL instance is unavailable, the application gracefully initializes SQLite without throwing connection pool abort errors.
4. **Dark Reader & Inversion Locks:** Browser auto-dark modes are explicitly locked via `<meta name="darkreader-lock">` and `color-scheme` metadata to preserve intentional styling.

