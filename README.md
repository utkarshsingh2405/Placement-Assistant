# Placement Assistant — Enterprise Campus Placement Platform

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg?logo=springboot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17%20%7C%2025-orange.svg?logo=openjdk)](https://www.oracle.com/java/)
[![Spring Cloud Gateway](https://img.shields.io/badge/Spring%20Cloud-Gateway%202023.0.3-blue.svg)](https://spring.io/projects/spring-cloud-gateway)
[![Database](https://img.shields.io/badge/Database-SQLite%203%20(WAL%20Mode)-003B57.svg?logo=sqlite)](https://www.sqlite.org/)
[![UI](https://img.shields.io/badge/Frontend-HTML5%20%7C%20CSS3%20%7C%20ES6-E34F26.svg)](https://developer.mozilla.org/)
[![Railway](https://img.shields.io/badge/Deploy-Railway%20%7C%20Docker-blueviolet.svg?logo=railway)](https://railway.app/)
[![Build](https://img.shields.io/badge/Build-Maven%20Wrapper%20(mvnw)-C71A36.svg?logo=apachemaven)](https://maven.apache.org/)

> A full-stack, enterprise-grade **Campus Placement Assistance & Academic Readiness Platform** built with **Java (Spring Boot 3.3.4)**, **Spring Cloud Gateway**, isolated zero-config **SQLite 3 (WAL mode)** persistent databases, and a modern, tactile, editorial web presentation layer.

---

## Architectural Overview

The platform unifies **Institute Academic Management** and **Corporate Placement Operations** behind a single-entry **Portal Gateway & SSO Reverse Proxy**:

```
                          Student / Recruiter / TPO / Director
                                            │
                            http://localhost:8080 (Single Port)
                                            ▼
                            ┌─────────────────────────────────┐
                            │      Portal Gateway & SSO       │
                            │      [PillarSupervisor]         │
                            │           PORT 8080             │
                            │       database/portal.db        │
                            └────────────────┬────────────────┘
                                             │
             ┌───────────────────────────────┴───────────────────────────────┐
             │ /institute/**                                                 │ /placement/**
             ▼                                                               ▼
     ┌───────────────────────────────┐               ┌───────────────────────────────┐
     │       Institute Pillar        │               │       Placement Pillar        │
     │          PORT 8081            │◄──────────────┤          PORT 8082            │
     │     database/institute.db     │ REST API Key  │     database/placement.db     │
     │      Context: /institute      │(Zero DB Share)│      Context: /placement      │
     └───────────────────────────────┘               └───────────────────────────────┘
```

### Key Architectural Highlights
1. **Unified Single-JVM Architecture**:  
   Portal, Institute, and Placement now run inside a single Spring Boot JVM container optimized for memory-constrained PaaS instances (Render 512 MB Free Tier, Railway, Fly.io). Memory footprint is managed via serial GC, tiered compilation level 1, and tight metaspace limits.
2. **Path Matching & Prefix Routing**:  
   Institute controllers are bound under `/institute/**`, Placement controllers under `/placement/**`, and Portal / SSO under root `/` and `/api/**`.
3. **Consolidated High-Performance SQLite**:  
   Runs on a unified `placement_assist.db` in WAL mode with connection pooling (Hikari 4) and `busy_timeout=5000` to eliminate database locking.
4. **Resilient Decoupled Fallback**:  
   Inter-pillar queries (`RestInstituteClient`) communicate over loopback REST (`X-API-KEY`). If Institute is offline or initializing, Placement automatically fails over to `StubInstituteClient` with zero runtime crash.

> [!WARNING]
> ### ⚠️ Security Notice: Production Secret Generation Required
> The previous default credentials and tokens originally committed in repository templates are compromised.
> 
> Before deploying to **Render**, **Railway**, or any public cloud, you **MUST** generate new secure random secrets and configure them in your hosting provider's dashboard environment variables:
> 
> 1. **`JWT_SECRET`**: Generate a strong 256-bit or 512-bit HMAC key (at least 64 hex characters or 32 random bytes):
>    ```bash
>    openssl rand -hex 32
>    ```
> 2. **`INSTITUTE_API_KEY`**: Generate a random secure token:
>    ```bash
>    openssl rand -hex 16
>    ```
> 
> The application enforces strict security and will **fail fast at startup** if `JWT_SECRET` is missing.


---

## Directory Structure

```
Placement Assistant 2/
├── pom.xml                 # Root Maven Reactor Aggregator
├── mvnw / mvnw.cmd         # Apache Maven Wrapper (Zero-setup Maven)
├── Dockerfile              # Production multi-stage Docker deployment image
├── railway.json / Procfile # 1-Click Railway & PaaS deployment specifications
├── start.bat / stop.bat    # Windows 1-click startup & shutdown scripts
├── start.sh / stop.sh      # Linux / macOS 1-click startup & shutdown scripts
├── .env.example            # Complete environment variable reference
├── database/               # Pre-seeded SQLite databases (WAL mode)
│   ├── portal.db
│   ├── institute.db
│   └── placement.db
├── backend/
│   ├── pom.xml             # Backend parent module POM
│   ├── portal/             # Portal Gateway & SSO (Spring Cloud Gateway, Port 8080)
│   ├── institute/          # Academic Management Pillar (Port 8081, Context /institute)
│   ├── placement/          # Placement Recruitment Pillar (Port 8082, Context /placement)
│   ├── shared-lib/         # Shared Java library (JWT, DTOs, Security, Exception Handlers)
│   └── shared-web/         # Canonical UI Design System assets (CSS, JS, Icons, Textures)
│       ├── css/            # 00-tokens.css through 07-domains.css
│       ├── js/
│       │   ├── core/       # api.js, auth.js, theme.js, ui.js, state.js
│       │   ├── services/   # student.service.js, drive.service.js, placement.service.js, auth.service.js
│       │   └── components/ # toast.js, modal.js, stat-card.js
│       └── icons/          # sprite.svg (SVG icon symbol library)
├── frontend/               # Standalone frontend suite (decoupled static client)
├── tools/                  # Database diagnostics, inspection, and test data seeding
└── docs/                   # Architectural blueprints, API specifications, and role guides
```

---

## Quick Start & Running Locally

### Prerequisites
- **Java JDK**: Version 17 or higher (tested on OpenJDK 17, 21, and 25).
- **Maven**: Optional (bundled Maven Wrapper `mvnw` / `mvnw.cmd` runs automatically).

---

### One-Command Startup (Recommended)

#### On Windows:
Double-click `start.bat` in the root folder, or run from Command Prompt / PowerShell:
```cmd
start.bat
```
*(You can also run `run.bat`)*

#### On Linux / macOS:
```bash
chmod +x *.sh mvnw
./start.sh
```

**What happens on startup:**
1. Checks and verifies Java 17+ on `PATH`, `%JAVA_HOME%`, or standard JDK paths.
2. Checks for pre-packaged executable JARs. If first-time run, compiles and packages modules automatically via Maven Wrapper.
3. Automatically frees up stale processes on ports 8080, 8081, 8082, and 8088.
4. Boots Portal Gateway on port 8080 (or 8088 if 8080 is occupied).
5. Portal's `PillarSupervisor` launches Institute (8081) and Placement (8082) internally.
6. Automatically opens your default browser to **`http://localhost:8080`**.

---

### Graceful Shutdown

#### On Windows:
Double-click `stop.bat` or run:
```cmd
stop.bat
```
*(Or press <kbd>Ctrl+C</kbd> in the console window)*

#### On Linux / macOS:
```bash
./stop.sh
```

---

## Railway Cloud Deployment

The repository is pre-configured for **1-click Railway deployment**:

1. Create a new project on [Railway.app](https://railway.app/).
2. Connect your GitHub repository.
3. Railway automatically detects `Dockerfile` and `railway.json`.
4. Railway builds all modules and starts the container on its assigned `$PORT`.
5. Access your public Railway URL:
   - Gateway Hub: `https://your-app.up.railway.app/choose-domain.html`
   - Institute: `https://your-app.up.railway.app/institute/dashboard.html`
   - Placement: `https://your-app.up.railway.app/placement/dashboard.html`

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

## Role Permissions & Security Matrix

| Action / Capability | STUDENT | COMPANY | TPO | DIRECTOR | ADMIN |
| :--- | :---: | :---: | :---: | :---: | :---: |
| Apply to Campus Drive | **Yes** | No | No | No | No |
| Join Pre-Placement Talk & Live Q&A | **Yes** | **Yes** | **Yes** | **Yes** | **Yes** |
| QR Code Attendance Check-in | **Yes** | No | **Yes** | **Yes** | **Yes** |
| View Active Drives & Stages | **Yes** | **Yes** | **Yes** | **Yes** | **Yes** |
| **Mark Candidate Selection Status** | ❌ **403 Forbidden** | ❌ **403 Forbidden** | ✅ **Allowed** | ✅ **Allowed** | ❌ **403 Forbidden** |
| Launch New Drive / Stage | No | No | **Yes** | **Yes** | **Yes** |
| Edit Master Student Records | Self Only | No | **Yes** | **Yes** | **Yes** |
| Access System Audit Trails | No | No | No | **Yes** | **Yes** |

---

## Reusable Frontend Architecture

The frontend follows a clean separation of concerns:
* **`core/api.js`**: Centralized HTTP client with automatic JWT token attachment, 401 token refresh, and network failure handling.
* **`core/state.js`**: Reactive application state manager supporting cross-tab persistence and state subscription listeners.
* **`core/auth.js`**: Role authorization, token decoding, and protected route gating.
* **`core/ui.js`**: Tactile double-bezel UI layout manager, rail navigation, dialogs, and SVG sprite injection.
* **`services/`**: Domain business logic (eligibility calculation, attendance compliance, drive filtering) cleanly decoupled from DOM rendering.
* **`components/`**: Reusable UI elements (`toast.js`, `modal.js`, `stat-card.js`).

---

## Automated Test Suites

Run the full automated test suite:

```bash
# Run all tests via Maven Wrapper
./mvnw test

# Or on Windows
.\mvnw.cmd test
```

### Test Validation Highlights
* **Institute Module:** `30/30` passing tests (Student service, ATS resume parser, attendance alerts, integration controller).
* **Placement Module:** `7/7` passing tests (Drive pipeline, QR generation, and strict **HTTP 403 Forbidden** security assertion for student/recruiter result updates).
* **Overall Pass Rate:** **100% (37/37 Tests Passed)**.
