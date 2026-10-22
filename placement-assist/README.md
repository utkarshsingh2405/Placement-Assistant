# Placement Assistance Web Application

A full-stack, enterprise-grade **Campus Placement Assistance Platform** built with **Java (Spring Boot 3.3.4)**, **SQLite 3** for single-file zero-config persistent storage, and **modern HTML5, Vanilla CSS3, ES6 JavaScript, and Bootstrap 5** for the user interface.

The application brings together **two standalone pillars** and a **unified gateway**:
1. **Portal Gateway & SSO** (`portal/`, Port `8080`, Context `/`): Unified entry point, Spring Cloud Gateway reverse proxy, single sign-on, and domain chooser.
2. **Institute Pillar** (`institute/`, Port `8081`, Context `/institute`): Master student academic records, company drives, question banks, CRT & classroom attendance with 75% shortage alerts, and automated PDF resume ATS scoring.
3. **Placement Pillar** (`placement/`, Port `8082`, Context `/placement`): Live recruitment engine, multi-stage candidate evaluations, pre-placement talks with live Q&A, QR code attendance, Server-Sent Events (SSE) live announcements, strict TPO/Director authorized selected lists, and placed student wall of fame.

---

## Architecture Overview

```
                          Visitor / Student / Recruiter
                                        │
                         http://localhost:8080 (Single Port)
                                        ▼
                         ┌─────────────────────────────┐
                         │    Spring Cloud Gateway     │
                         │   & Single Sign-On (SSO)    │
                         │         PORT 8080           │
                         │      data/portal.db         │
                         └──────────────┬──────────────┘
                                        │
        ┌───────────────────────────────┴───────────────────────────────┐
        │ /institute/**                                                 │ /placement/**
        ▼                                                               ▼
┌───────────────────────────────┐               ┌───────────────────────────────┐
│       Institute Pillar        │               │       Placement Pillar        │
│          PORT 8081            │◄──────────────┤          PORT 8082            │
│       data/institute.db       │  REST API Key │       data/placement.db       │
│      Context: /institute      │  (No DB Share)│      Context: /placement      │
└───────────────────────────────┘               └───────────────────────────────┘
```

### Strict Architectural Boundaries
- **Zero Cross-Pillar DB Sharing**: Each pillar operates solely on its dedicated SQLite database file.
- **Zero Cross-Pillar Java Imports**: Inter-pillar communication occurs exclusively over authenticated HTTP REST (`/institute/api/integration/**`).
- **Resilient Fallback**: If the Institute service is unreachable, Placement automatically switches to stub data with zero downtime.

---

## Key Features by Pillar

### 1. Portal Gateway & SSO (Port 8080)
- **Home Landing Page**: Modern Hero section, recruitment highlights, dynamic live ticker.
- **Single Sign-On (SSO)**: One login grants access across all pillars via JWT tokens and gateway forwarded headers.
- **Domain Chooser**: Seamless domain picker (`/choose-domain.html`) and in-app switcher (`Alt+D`).
- **Pillar Status & Audit**: Dynamic health monitoring and administrative audit trails.

### 2. Institute Pillar (Port 8081, Context `/institute`)
- **Student Master Database**: CGPA, branch, active backlogs, placement eligibility verification, CSV export.
- **Master Drives**: On-campus company scheduling, criteria, package brackets, and registration deadlines.
- **Question Bank & Timed Practice**: Categorized aptitude, coding, and HR questions with practice mode.
- **CRT & Classroom Attendance**: Multi-roster tracking with automated 75% attendance shortage alerts.
- **Automated Resume ATS Scorer**: 100-point algorithmic PDF resume evaluator using Apache PDFBox.
- **Opportunities Feed**: Curated listings for hackathons, paid internships, and off-campus jobs.

### 3. Placement Pillar (Port 8082, Context `/placement`)
- **Live Recruitment Drives**: Active campus drives with package brackets, branch eligibility, and instant application.
- **Multi-Stage Stepper Pipeline**: Sequential rounds (Aptitude -> Tech 1 -> Tech 2 -> HR) with one-click candidate progression.
- **Pre-Placement Talks (PPTs)**: Live company presentations, slide viewer, and real-time upvotable candidate Q&A.
- **Server-Sent Events (SSE) Live Feed**: Zero-latency broadcast ticker for stage qualifications and urgent announcements.
- **Dynamic QR Code Attendance**: Instant hall check-in via camera scanner or manual 6-character session PIN.
- **Selected & Non-Selected Lists**: **Strictly guarded** by role — only **TPO** and **DIRECTOR** can mark candidates as Selected/Not Selected. Students and Recruiters have clean read-only access (unauthorized POST returns HTTP 403 Forbidden).
- **Placed Students Wall of Fame**: Honor roll celebrating candidates placed across Dream (> 10 LPA) and Super Dream (> 20 LPA) offers.
- **Interview Feedback**: Peer review system for candidate interview experiences, round summaries, and difficulty ratings.

---

## Role Permissions & Security Matrix

| Action / Capability | STUDENT | COMPANY | TPO | DIRECTOR | ADMIN |
| :--- | :---: | :---: | :---: | :---: | :---: |
| Apply to Campus Drive | **Yes** | No | No | No | No |
| Join PPT & Live Q&A | **Yes** | **Yes** | **Yes** | **Yes** | **Yes** |
| QR Attendance Check-in | **Yes** | No | **Yes** | **Yes** | **Yes** |
| **Mark Candidate Result (SELECTED/NOT_SELECTED)** | ❌ **403 Forbidden** | ❌ **403 Forbidden** | ✅ **Allowed** | ✅ **Allowed** | ❌ 403 Forbidden |
| Launch New Drive / Stage | No | No | **Yes** | **Yes** | **Yes** |
| Modify Master Student Profile | Self | No | **Yes** | **Yes** | **Yes** |

---

## Quick Start & Running Locally

### Prerequisites
- **Java JDK**: Version 17 or higher (tested on Java 17 and Java 25).
- **Maven**: Version 3.8+.
- **Web Browser**: Modern browser (Chrome, Edge, Firefox, Safari).

### 1. Build Entire Multi-Module Project
```bash
mvn clean install -DskipTests
```

### 2. Run All Services with One Command
- **On Windows**:
  ```cmd
  run-all.bat
  ```
- **On Linux / macOS**:
  ```bash
  chmod +x *.sh
  ./run-all.sh
  ```

The launcher starts all 3 services, monitors their health endpoints, and prints an active summary banner when ready.

### 3. Service Management Scripts
- **Check Health Status**:
  - Windows: `status.bat`
  - Linux/Mac: `./status.sh`
- **Verify Inter-Service Connectivity**:
  - Windows: `verify-connection.bat`
  - Linux/Mac: `./verify-connection.sh`
- **Stop All Services Gracefully**:
  - Windows: `stop-all.bat`
  - Linux/Mac: `./stop-all.sh`

---

## Demo Credentials

All test accounts share the same default password: **`Password@123`**

| Role | Email | Name / Details | Primary Access |
| :--- | :--- | :--- | :--- |
| **Student** | `student@college.edu` | Aarav Sharma (Roll: `21CS001`) | Applications, QR Scan, PPT Q&A, Feedback |
| **TPO** | `tpo@college.edu` | Placement Officer | Drives, Stages, Full Results Authority |
| **Director** | `director@college.edu` | Campus Director | Executive Oversight, Full Results Authority |
| **Company** | `company@google.com` | Google Recruiter | Drive Details, Stages, Candidate Pipeline |
| **Admin** | `admin@college.edu` | System Administrator | User Management, System Diagnostics |

---

## Running Individual Services (Manual Mode)

If you prefer to run services in separate terminals:
```bash
# Terminal 1: Institute Pillar (Port 8081)
mvn spring-boot:run -pl institute

# Terminal 2: Placement Pillar (Port 8082)
mvn spring-boot:run -pl placement

# Terminal 3: Portal Gateway (Port 8080)
mvn spring-boot:run -pl portal
```

---

## Running Automated Tests

Run the full automated test suite across all modules:
```bash
# Run all unit, integration, and security slice tests
mvn test

# Run tests for specific module
mvn test -pl placement -am
mvn test -pl institute -am
```
- **Institute Tests**: 30/30 tests pass.
- **Placement Tests**: 7/7 tests pass (including strict 403 Forbidden checks for candidate selection write endpoints).

---

## License

This software is built for educational institutions to streamline and elevate the campus placement experience. All rights reserved.
