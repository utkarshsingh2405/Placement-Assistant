# Placement Assistant — Enterprise Campus Placement Platform

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg?logo=springboot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17%20%7C%2025-orange.svg?logo=openjdk)](https://www.oracle.com/java/)
[![Spring Cloud Gateway](https://img.shields.io/badge/Spring%20Cloud-Gateway%202023.0.3-blue.svg)](https://spring.io/projects/spring-cloud-gateway)
[![Database](https://img.shields.io/badge/Database-SQLite%203%20(WAL%20Mode)-003B57.svg?logo=sqlite)](https://www.sqlite.org/)
[![UI](https://img.shields.io/badge/Frontend-HTML5%20%7C%20CSS3%20%7C%20ES6%20%7C%20Bootstrap%205-E34F26.svg)](https://getbootstrap.com/)
[![Tests](https://img.shields.io/badge/Automated%20Tests-37%2F37%20Passing-success.svg)](https://junit.org/)
[![Architecture](https://img.shields.io/badge/Architecture-Multi--Pillar%20Microservices-informational.svg)]()

> A full-stack, enterprise-grade **Campus Placement Assistance & Academic Readiness Platform** built with **Java (Spring Boot 3.3.4)**, **Spring Cloud Gateway**, isolated zero-config **SQLite 3 (WAL mode)** persistent databases, and a modern, tactile, editorial web presentation layer.

---

## The Engineering Team

This platform was architected, designed, engineered, and tested by a core team of **5 software engineers**:

| Contributor | Core Responsibility & Architectural Domain | Primary Modules & Tech Stack |
| :--- | :--- | :--- |
| **Utkarsh Singh** | **Backend Lead & Institute Pillar Architect** | Academic records ledger, master drives, 100-pt ATS resume scorer (Apache PDFBox), CRT & class 75% attendance alerts, question bank practice, inter-pillar REST APIs (`X-API-KEY`). |
| **Bhavya Bhardwaj** | **Portal Gateway & Security / SSO Architect** | Reverse proxy routing (Spring Cloud Gateway), Centralized SSO with signed JWTs, gateway header forwarding (`X-User-*`), domain chooser, system health, and audit logging. |
| **Ankit Kumar** | **Lead Frontend Engineer & UI/UX Designer** | Google Stitch 7-Section Design System, tactile double-bezel UI, split-flap announcement board, perforated hall tickets, multi-stage stepper UI, SSE live ticker clients, responsive dashboards. |
| **Isha Bajpai** | **Placement Pillar Lead & Recruitment Engine** | Live drive recruitment pipeline, candidate stage progression, dynamic QR attendance (ZXing), Pre-Placement Talks (PPTs) with live upvotable Q&A, SSE broadcast service, strictly guarded selection authority (TPO/Director). |
| **Mahak Joshi** | **Database Architect & Lead Test Engineer** | Multi-database SQLite architecture (WAL mode & HikariCP write serialization), schema design & migrations, 37/37 automated unit, integration, and security slice test suites. |

> 📖 **Detailed Technical Breakdown:** Read the full technical specifications, deliverables, and RACI matrix in [TEAM_ROLES.md](TEAM_ROLES.md) (or [placement-assist/docs/TEAM_ROLES.md](placement-assist/docs/TEAM_ROLES.md)).

---

## Architectural Overview

The platform is designed around **two independent domain pillars** unified behind a **single-entry reverse proxy gateway**:

```
                          Student / Recruiter / TPO / Director
                                            │
                           http://localhost:8080 (Single Port)
                                            ▼
                           ┌─────────────────────────────────┐
                           │      Spring Cloud Gateway       │
                           │     & Single Sign-On (SSO)      │
                           │           PORT 8080             │
                           │        data/portal.db           │
                           └────────────────┬────────────────┘
                                            │
            ┌───────────────────────────────┴───────────────────────────────┐
            │ /institute/**                                                 │ /placement/**
            ▼                                                               ▼
    ┌───────────────────────────────┐               ┌───────────────────────────────┐
    │       Institute Pillar        │               │       Placement Pillar        │
    │          PORT 8081            │◄──────────────┤          PORT 8082            │
    │       data/institute.db       │ REST API Key  │       data/placement.db       │
    │      Context: /institute      │(Zero DB Share)│      Context: /placement      │
    └───────────────────────────────┘               └───────────────────────────────┘
```

### Strict Architectural Boundaries
1. **Zero Database Sharing:**  
   Each pillar exclusively owns its database file (`data/portal.db`, `data/institute.db`, `data/placement.db`). Cross-pillar database queries are strictly prohibited.
2. **Zero Direct Java Class Imports:**  
   The `institute` and `placement` modules do not import each other's classes. Common DTOs and utilities reside strictly in `shared-lib`.
3. **Resilient Decoupled Fallback:**  
   Inter-pillar queries (`RestInstituteClient`) communicate over authenticated HTTP REST (`X-API-KEY: inst-sec-key-7f9a12b4e8c345d6`). If Institute is offline, Placement automatically fails over to `StubInstituteClient` with zero runtime crash.

---

## Key Features by Pillar

### 1. Portal Gateway & SSO (Port 8080, Context `/`)
* **Unified Single Port Entry:** Access all platform domains and services via `http://localhost:8080`.
* **Centralized Single Sign-On (SSO):** Authenticate once and traverse all pillars seamlessly via signed HMAC-SHA256 JWT tokens.
* **Gateway Context Forwarding:** Custom gateway filters inject authenticated user claims downstream via `X-User-Email`, `X-User-Role`, and `X-User-Id` headers.
* **Domain Chooser & Keyboard Navigation:** Visual domain switcher card grid (`/choose-domain.html`) and in-app navigation pill supporting `Alt+D` keyboard shortcuts.
* **System Diagnostics & Audit Trails:** Real-time health check aggregators and database-persisted administrative audit logs.

### 2. Institute Pillar (Port 8081, Context `/institute`)
* **Student Academic Records:** Centralized repository for CGPA, department branches, backlogs, and placement clearance statuses with bulk CSV import/export.
* **Master On-Campus Drives:** Drive setup, package brackets (Normal, Dream, Super Dream), eligibility criteria, and deadlines.
* **100-Point Algorithmic ATS Resume Scorer:** Automated PDF resume parsing via **Apache PDFBox 3.0.3** evaluating Contact, Skills, Education, Experience, and Formatting with instant suggestions.
* **Question Bank & Timed Practice:** Categorized aptitude, logical reasoning, coding, and HR interview questions with interactive practice mode.
* **CRT & Classroom Attendance:** Dual-roster tracking with automated threshold rules that flag students below 75% attendance for placement disqualification.
* **Curated Opportunities Feed:** Real-time listings for external Hackathons, Paid Internships, and Off-Campus Job drives.

### 3. Placement Pillar (Port 8082, Context `/placement`)
* **Live Recruitment Drive Engine:** Real-time campus drive workflows, one-click student applications, and branch eligibility filtering.
* **Multi-Stage Candidate Stepper:** 4-stage recruitment pipeline (`Aptitude` &rarr; `Technical 1` &rarr; `Technical 2` &rarr; `HR Interview`) with evaluation scoring.
* **Pre-Placement Talks (PPTs) & Live Q&A:** Company presentation manager, slide deck viewer, and real-time candidate Q&A with upvoting.
* **Dynamic QR Code Hall Attendance:** **ZXing** generated session QR codes for instant mobile camera check-in, with a fallback 6-character session PIN.
* **Server-Sent Events (SSE) Live Ticker:** Zero-latency browser broadcast ticker streaming round qualifications and urgent notifications.
* **Strictly Guarded Selection Lists:** **Strict role enforcement** &mdash; only **TPO** and **DIRECTOR** roles are authorized to mark candidate selections. Unauthorized attempts return **HTTP 403 Forbidden**.
* **Placed Students Wall of Fame:** Visual honor roll showcasing candidates securing Dream (> 10 LPA) and Super Dream (> 20 LPA) offers.
* **Interview Experience Repository:** Crowdsourced candidate interview feedback, round questions, and difficulty ratings.

---

## Role Permissions & Security Matrix

| Action / Capability | STUDENT | COMPANY | TPO | DIRECTOR | ADMIN |
| :--- | :---: | :---: | :---: | :---: | :---: |
| Apply to Campus Drive | **Yes** | No | No | No | No |
| Join Pre-Placement Talk & Live Q&A | **Yes** | **Yes** | **Yes** | **Yes** | **Yes** |
| QR Code Attendance Check-in | **Yes** | No | **Yes** | **Yes** | **Yes** |
| View Active Drives & Stages | **Yes** | **Yes** | **Yes** | **Yes** | **Yes** |
| **Mark Candidate Result (SELECTED/NOT_SELECTED)** | ❌ **403 Forbidden** | ❌ **403 Forbidden** | ✅ **Allowed** | ✅ **Allowed** | ❌ **403 Forbidden** |
| Launch New Drive / Stage | No | No | **Yes** | **Yes** | **Yes** |
| Edit Master Student Records | Self Only | No | **Yes** | **Yes** | **Yes** |
| Access System Audit Trails | No | No | No | **Yes** | **Yes** |

---

## Demo Credentials

All seed accounts are configured with the default password: **`Password@123`**

| Role | Email | Name / Department | Primary Access Capabilities |
| :--- | :--- | :--- | :--- |
| **Student** | `student@college.edu` | Aarav Sharma (`21CS001`) | Applications, ATS Scorer, QR Scan, PPT Q&A |
| **TPO** | `tpo@college.edu` | Placement Officer | Drives, Stepper Rounds, Selection Authority |
| **Director** | `director@college.edu` | Campus Director | Executive Analytics, Full Selection Authority |
| **Company** | `company@google.com` | Google Recruiter | Drive Details, Stages, Candidate Pipeline |
| **Admin** | `admin@college.edu` | System Administrator | User Management, Health & Audit Diagnostics |

---

## Technology Stack

```
┌────────────────────────────────────────────────────────────────────────┐
│                          TECHNOLOGY STACK                              │
├────────────────────────────────────────────────────────────────────────┤
│ Backend Services  │ Spring Boot 3.3.4, Java 17/25, Spring Data JPA     │
│ API Gateway       │ Spring Cloud Gateway 2023.0.3, WebFlux Reactive    │
│ Security & SSO    │ Spring Security 6, JJWT 0.12.6 (HMAC-SHA256)       │
│ Database Layer    │ SQLite 3.46.1, HikariCP, Hibernate SQLite Dialects │
│ Concurrency Mode  │ Write-Ahead Logging (WAL), Single-Writer Pool      │
│ Document Parsing  │ Apache PDFBox 3.0.3, OpenCSV 5.9, Apache POI 5.3.0 │
│ QR Generation     │ ZXing Core & JavaSE 3.5.3                          │
│ Real-Time Feed    │ Server-Sent Events (SSE), Web Push (VAPID)         │
│ Presentation UI   │ HTML5, Vanilla CSS3, ES6 JavaScript, Bootstrap 5   │
│ Design Standard   │ Google Stitch 7-Section Design System Spec         │
│ DevOps & QA       │ Docker, Docker Compose, JUnit 5, Mockito, Batch/Sh │
└────────────────────────────────────────────────────────────────────────┘
```

---

## Quick Start & Running Locally

### Prerequisites
- **Java JDK**: Version 17 or higher (tested on Java 17 and Java 25).
- **Apache Maven**: Version 3.8 or higher.
- **Web Browser**: Any modern browser (Chrome, Edge, Firefox, Safari).

---

### Method 1: One-Command Startup (Recommended)

#### On Windows:
Double-click `run.bat` in the root folder, or execute from PowerShell / Command Prompt:
```cmd
run.bat
```
*(Or navigate to `placement-assist` and run `run-all.bat`)*

#### On Linux / macOS:
```bash
cd placement-assist
chmod +x *.sh
./run-all.sh
```

The startup script will:
1. Verify Java 17+ and Maven installations.
2. Initialize and verify SQLite databases in `data/`.
3. Launch Institute (`8081`), Placement (`8082`), and Portal Gateway (`8080`).
4. Monitor health check endpoints until all 3 services are active.
5. Print a summary banner with quick URLs and credentials.

Access the platform at: **[http://localhost:8080](http://localhost:8080)**

---

### Method 2: Docker Compose

Build and run the entire 3-tier containerized stack with a single command:
```bash
cd placement-assist
docker compose up -d --build
```
To monitor running logs:
```bash
docker compose logs -f
```

---

### Method 3: Manual Startup (Separate Terminals)

If you prefer running services individually for development:

```bash
# 1. Build shared library and modules
mvn clean install -DskipTests

# 2. Terminal 1: Institute Pillar (Port 8081)
mvn spring-boot:run -pl institute

# 3. Terminal 2: Placement Pillar (Port 8082)
mvn spring-boot:run -pl placement

# 4. Terminal 3: Portal Gateway (Port 8080)
mvn spring-boot:run -pl portal
```

---

## Operational Control Scripts

The `placement-assist/` directory contains helper scripts for day-to-day operations:

| Script (Windows) | Script (Linux/Mac) | Purpose |
| :--- | :--- | :--- |
| `run-all.bat` | `./run-all.sh` | Launches all 3 microservices with health checks |
| `status.bat` | `./status.sh` | Queries actuator health endpoints and reports status |
| `verify-connection.bat` | `./verify-connection.sh` | Validates inter-pillar REST handshake and API keys |
| `stop-all.bat` | `./stop-all.sh` | Gracefully shuts down all running instances |

---

## Running Automated Tests

Run the full test suite across all modules:

```bash
# Run all unit, integration, and security slice tests
mvn test

# Run tests for a specific module
mvn test -pl institute -am
mvn test -pl placement -am
```

### Test Validation Highlights
* **Institute Module:** `30/30` passing tests (Student service, ATS resume parser, attendance alerts, integration controller).
* **Placement Module:** `7/7` passing tests (Drive pipeline, QR generation, and strict **HTTP 403 Forbidden** security assertion for student/recruiter result updates).
* **Overall Pass Rate:** **100% (37/37 Tests Passed)**.

---

## Project Repository Structure

```
Placement-Assistant/
├── README.md                     # Root project documentation & overview
├── TEAM_ROLES.md                 # Full technical role specification & RACI matrix
├── run.bat                       # One-click Windows launch entry point
├── stop.bat                      # One-click Windows stop entry point
└── placement-assist/
    ├── pom.xml                   # Root Maven multi-module parent POM
    ├── docker-compose.yml        # Multi-container orchestration definition
    ├── .env.example              # Sample environment configuration template
    ├── run-all.bat / .sh         # Multi-service launcher with health checks
    ├── status.bat / .sh          # Service diagnostic check script
    ├── stop-all.bat / .sh        # Multi-service graceful termination script
    ├── verify-connection.bat/.sh # Inter-service connectivity validator
    │
    ├── portal/                   # PORT 8080 - Portal Gateway & SSO (Bhavya Bhardwaj)
    │   ├── src/main/java/        # Spring Cloud Gateway, JWT Filter, Audit, Status
    │   └── src/main/resources/   # Landing page, SSO login, Domain Chooser
    │
    ├── institute/                # PORT 8081 - Institute Pillar (Utkarsh Singh)
    │   ├── src/main/java/        # Students, Master Drives, ATS Scorer, Attendance, Practice
    │   └── src/main/resources/   # Institute dashboard, Question bank, Resumes UI
    │
    ├── placement/                # PORT 8082 - Placement Pillar (Isha Bajpai)
    │   ├── src/main/java/        # Live Drives, Stepper Stages, QR Check-in, PPTs, Results
    │   └── src/main/resources/   # Placement dashboard, Live SSE ticker, Wall of Fame
    │
    ├── shared-lib/               # Common DTOs, Security filters, Exceptions, Utilities
    ├── shared-web/               # Design tokens, CSS components, Icons, Textures (Mahak Joshi)
    ├── data/                     # Persistent SQLite DB storage (portal.db, institute.db, placement.db)
    └── docs/                     # Technical specifications, API contracts, Architecture guides
        ├── TEAM_ROLES.md         # Team roles and responsibilities specification
        ├── DESIGN.md             # Authoritative Google Stitch 7-Section Design System
        ├── CONNECT.md            # Inter-pillar connection and network guide
        ├── api-spec.md           # OpenAPI / REST API endpoint documentation
        ├── db-diagram.md         # Database ER diagram & WAL concurrency architecture
        └── deployment.md         # Production systemd, Nginx, and backup guide
```

---

## Documentation Links

* [Engineering Team Roles Specification](TEAM_ROLES.md)
* [Design System Specification (Google Stitch Standard)](placement-assist/docs/DESIGN.md)
* [Inter-Pillar Integration & Network Guide](placement-assist/docs/CONNECT.md)
* [REST API Specification](placement-assist/docs/api-spec.md)
* [Database Schema & ER Diagrams](placement-assist/docs/db-diagram.md)
* [Production Deployment & Operations Guide](placement-assist/docs/deployment.md)

---

## License

This software is built for educational institutions to streamline and elevate the campus placement experience.  
**Developed by Utkarsh Singh, Bhavya Bhardwaj, Ankit Kumar, Isha Bajpai, and Mahak Joshi.**
