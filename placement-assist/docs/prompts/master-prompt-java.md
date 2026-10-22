<USER_REQUEST>
# MASTER BUILD PROMPT: Placement Assistance Web Application (Java + HTML)

Paste everything below this line into your AI coding tool (Claude Code, Cursor, etc.).

---

## ROLE

You are a senior Java full-stack engineer and software architect. You will build a complete, working **Placement Assistance Web Application** from scratch using **Java (Spring Boot) for the backend and plain HTML, CSS and JavaScript for the frontend**. Work in strict phases. Do not skip ahead, do not summarize instead of writing code, and do not leave placeholders such as "// TODO", "...", or "add logic here". Every file must be complete, compilable and runnable.

## PRODUCT OVERVIEW

The app helps a college manage campus placements. It has **two standalone pillars**, but **you are building ONLY the institute pillar**. The placement pillar is being built separately by a teammate.

1. **Institute pillar** (`institute/`), IN SCOPE: student data, company drives, question bank, attendance, drive rounds, resumes and ATS scores, hackathons, internships and off-campus jobs.
2. **Placement pillar** (`placement/`), OUT OF SCOPE: the live recruitment process (applications, shortlisting, interviews, offers, analytics). Do NOT create, scaffold or modify any `placement/` code.

The institute pillar is its **own Spring Boot application** with its own SQLite database file, `application.yml`, `pom.xml`, and its own HTML frontend served from `src/main/resources/static`. It must start and work entirely on its own. The pillars must NOT import classes from each other or share database tables. The only shared code lives in `shared-lib` (Java) and `shared-web` (CSS/JS), which the teammate's pillar may also use, so keep both free of institute-specific logic. The teammate's placement pillar will read institute data only through the institute's REST API, so the institute API must be stable, documented and consumable by another service (see Phase 5).

## TECH STACK (fixed, do not substitute)

- Language and build: Java 17, Maven (multi-module)
- Backend: Spring Boot 3.x, Spring Web, Spring Data JPA (Hibernate), Spring Security, Spring Validation (Jakarta), Spring Scheduling
- Database: SQLite 3 (one database file per pillar: `data/institute.db` and `data/placement.db`) using `org.xerial:sqlite-jdbc`, Hibernate with `hibernate-community-dialects` (`SQLiteDialect`), and Flyway for migrations. Tests use a temporary SQLite file or `jdbc:sqlite::memory:`. No MySQL, PostgreSQL or H2.
- Auth: JWT (access + refresh) using jjwt, BCrypt password hashing, roles `STUDENT`, `ADMIN`, `TPO`
- Utilities: Lombok, MapStruct (DTO mapping), Apache PDFBox (resume text extraction), OpenCSV (CSV import/export), springdoc-openapi (Swagger UI)
- Frontend: HTML5, CSS3, vanilla JavaScript (ES6 modules, `fetch` API), Bootstrap 5 and Chart.js via CDN. No React, no build step. Pages are static `.html` files.
- Tests: JUnit 5, Mockito, Spring MockMvc
- Infra: Docker, docker-compose

## GLOBAL RULES

1. Every module is a package containing: `Entity`, `Repository`, `dto/` (request and response records), `Mapper`, `Service`, `Controller`. Controllers handle HTTP only; services hold the logic and are annotated `@Transactional` where needed.
2. Every list endpoint accepts `page`, `size`, `sort`, `search` plus relevant filters (using `Pageable` and JPA `Specification`), and returns `PageResponse<T> { content, page, size, totalElements, totalPages }`.
3. Every response is wrapped in `ApiResponse<T> { success, data, message }`. Errors return `{ success:false, error:{ code, message, details } }` through a `@RestControllerAdvice` global exception handler.
4. All request bodies use Jakarta validation annotations. Duplicate unique values return HTTP 409.
5. Role rules via `@PreAuthorize`: STUDENT can read listings and edit only their own records; TPO and ADMIN can create, edit and delete; only ADMIN can delete users and change roles.
6. Never store computed percentages. Compute attendance and similar statistics with JPQL or native queries.
7. Frontend rules: every feature has a list page (table with search, filters and pagination), a form page (create/edit), and a detail page, each with loading, empty and error states. Layout must be mobile-responsive using Bootstrap.
8. All configuration goes in `application.yml` with environment variable overrides (`${DB_URL:...}`). Provide `application-dev.yml` and `.env.example`.
9. Use Javadoc on every service method and constructor injection only (no field `@Autowired`).
10. **SQLite rules:** (a) set the datasource URL to `jdbc:sqlite:${DB_PATH:./data/institute.db}` and create the `data/` folder on startup if missing; (b) configure HikariCP with `maximum-pool-size: 1` because SQLite allows a single writer; (c) enable foreign keys with `?foreign_keys=on` or a connection-init SQL `PRAGMA foreign_keys = ON;` and enable WAL mode with `PRAGMA journal_mode=WAL;`; (d) write Flyway migrations in SQLite syntax (`INTEGER PRIMARY KEY AUTOINCREMENT`, `TEXT`, `INTEGER`, `REAL`, no `ALTER TABLE ... DROP COLUMN` tricks beyond what SQLite supports); (e) store dates as ISO-8601 `TEXT`, booleans as `INTEGER` 0/1, and JSON as `TEXT`; (f) set `spring.jpa.hibernate.ddl-auto=validate` so Flyway is the single source of schema; (g) write all queries so they run on SQLite (no `ILIKE`, use `LOWER(col) LIKE LOWER(...)`); (h) keep `@Transactional` boundaries short to avoid `SQLITE_BUSY` locks.
11. Add a Flyway migration and a seed script (`data-dev.sql` or a `CommandLineRunner` under the `dev` profile) with at least 15 realistic Indian-college sample rows per module.
12. Frontend JavaScript calls the backend only through the shared `api.js` helper, which attaches the JWT from `localStorage`, handles 401 by redirecting to `login.html`, and parses the `ApiResponse` wrapper.

---

## PHASE 1: CREATE THE FOLDER STRUCTURE

First output the full tree below, then give me one shell script (`scaffold.sh`) that creates every folder and empty file using `mkdir -p` and `touch`. Do not write any code in this phase. After the script, print the resulting tree and stop by saying: "PHASE 1 COMPLETE. Reply 'continue' for Phase 2."

```
placement-assist/
├── README.md
├── pom.xml                              # parent POM, modules: shared-lib, institute (teammate adds the placement module)
├── docker-compose.yml                   # institute service with a mounted ./data volume for its SQLite file (teammate adds placement)
├── .gitignore
├── .env.example
│
├── shared-lib/                          # Java module shared by both pillars (generic code only)
│   ├── pom.xml
│   └── src/main/java/com/placementassist/shared/
│       ├── security/    (JwtService.java, JwtAuthFilter.java, Role.java, SecurityUtils.java)
│       ├── api/         (ApiResponse.java, PageResponse.java, ErrorResponse.java)
│       ├── exception/   (ApiException.java, NotFoundException.java, ConflictException.java, GlobalExceptionHandler.java)
│       ├── util/        (CsvUtil.java, DateUtil.java, SpecificationBuilder.java)
│       └── config/      (OpenApiConfig.java, CorsConfig.java)
│
├── shared-web/                          # copied into each pillar's static/ at build time
│   ├── css/ (theme.css, components.css)
│   └── js/  (api.js, auth.js, ui.js, table.js, pagination.js, charts.js)
│
├── institute/                           # INSTITUTE PILLAR (your scope), Spring Boot app, port 8081
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
│       ├── main/java/com/placementassist/institute/
│       │   ├── InstituteApplication.java
│       │   ├── config/        (SecurityConfig.java, SchedulingConfig.java, FileStorageConfig.java)
│       │   ├── common/        (BaseEntity.java, enums/)
│       │   ├── jobs/          (ExpireListingsJob.java, RemindersJob.java)
│       │   └── modules/
│       │       ├── auth/               (User, AuthController, AuthService, dto/)
│       │       ├── student/            (module 1)
│       │       ├── companydrive/       (module 2)
│       │       ├── questionbank/       (module 3)
│       │       ├── attendance/
│       │       │   ├── crt/            (module 4a)
│       │       │   └── classroom/      (module 4b)
│       │       ├── driveround/         (module 5)
│       │       ├── resume/             (module 6, includes AtsScorer.java)
│       │       ├── hackathon/          (module 7)
│       │       ├── internship/         (module 8)
│       │       ├── offcampusjob/       (module 9)
│       │       ├── listing/            (generic ListingService shared by 7, 8, 9)
│       │       └── dashboard/
│       │     (each module package: Entity, Repository, Mapper, Service, Controller, dto/)
│       ├── main/resources/
│       │   ├── application.yml
│       │   ├── application-dev.yml
│       │   ├── db/migration/   (V1__init.sql ... one migration per module)
│       │   └── static/
│       │       ├── index.html
│       │       ├── login.html
│       │       ├── register.html
│       │       ├── dashboard.html
│       │       ├── css/ js/                         (filled from shared-web at build)
│       │       ├── assets/ (logo, icons)
│       │       └── pages/
│       │           ├── 1-students/        (student-list.html, student-form.html, student-profile.html)
│       │           ├── 2-company-drives/  (drive-list.html, drive-form.html, drive-detail.html, company-detail.html)
│       │           ├── 3-question-bank/   (question-list.html, question-form.html, practice-mode.html)
│       │           ├── 4-attendance/
│       │           │   ├── crt/           (crt-sessions.html, mark-crt.html, crt-report.html)
│       │           │   └── class/         (class-sessions.html, mark-class.html, class-report.html)
│       │           ├── 5-drive-rounds/    (rounds-board.html, round-form.html, round-results.html)
│       │           ├── 6-resume-ats/      (resume-upload.html, resume-review.html, ats-report.html)
│       │           ├── 7-hackathons/      (hackathon-list.html, hackathon-form.html, hackathon-detail.html)
│       │           ├── 8-internships/     (internship-list.html, internship-form.html, internship-detail.html)
│       │           └── 9-offcampus-jobs/  (job-list.html, job-form.html, job-detail.html)
│       └── test/java/com/placementassist/institute/   (mirrors main packages)
│
├── placement/                           # NOT YOURS: built by a teammate. Do not create or touch.
│
└── docs/
    ├── prompts/
    ├── api-spec.md
    ├── db-diagram.md
    └── deployment.md
```

---

## PHASE 2: FOUNDATION (write files one by one)

Write the following **one file per step**. For each file print its full path as a heading, then the complete code in a single code block, then one line: "Next file: <path>". Continue automatically unless I ask for step-by-step mode.

Order:
1. Parent `pom.xml`, `.gitignore`, `.env.example`, `docker-compose.yml`.
2. `shared-lib`: `pom.xml`, then every class under `api/`, `exception/`, `security/`, `util/`, `config/`.
3. `shared-web`: `theme.css`, `components.css`, `api.js`, `auth.js`, `ui.js`, `table.js`, `pagination.js`, `charts.js`. Configure `maven-resources-plugin` in the institute `pom.xml` to copy `shared-web` into `static/` at build.
4. `institute`: `pom.xml` (include `sqlite-jdbc`, `hibernate-community-dialects`, `flyway-core`), `Dockerfile`, `application.yml` (SQLite datasource, `SQLiteDialect`, pool size 1, Flyway enabled), `application-dev.yml`, `InstituteApplication.java`, `SecurityConfig.java`, `FileStorageConfig.java`, `SchedulingConfig.java`, `BaseEntity.java` and all enums.
5. `institute/db/migration/V1__init.sql` containing ALL tables from the model list in Phase 3, written in SQLite syntax with foreign keys, unique constraints and indexes on rollNo, email, driveId, studentId and deadline columns.
6. `modules/auth` (register, login, refresh, me, change-password) plus `login.html` and `register.html`.

## PHASE 3: INSTITUTE BACKEND MODULES (one module at a time, in this order)

For each module write, in this order: Entity, Repository, dto (request and response), Mapper, Service, Controller, Flyway migration (if not in V1), seed data, and a JUnit/MockMvc test class. Provide each as its own file. Then print the progress tracker.

### JPA entities (institute)
- `User` (id, email unique, passwordHash, role, createdAt)
- `Student` (id, user, name, rollNo unique, email unique, phone, branch, year, cgpa, backlogs, skills as element collection, linkedin, github, photoUrl, placementEligible computed on save)
- `Company` (id, name, website, about, industry, headquarters, logoUrl)
- `Drive` (id, company, title, driveDate, lastDateToApply, eligibilityCgpa, eligibleBranches, maxBacklogs, roles, packageMin, packageMax, location, status [UPCOMING/ONGOING/COMPLETED/CANCELLED], description)
- `Question` (id, drive, role, roundType, difficulty [EASY/MEDIUM/HARD], question, answer, tags, askedYear)
- `CrtSession` (id, title, topic, trainer, date, startTime, endTime, venue)
- `ClassSession` (id, subject, faculty, date, startTime, endTime, branch, year, section)
- `Attendance` (id, student, sessionType [CRT/CLASS], crtSession nullable, classSession nullable, status [PRESENT/ABSENT/LATE/EXCUSED], markedBy; unique constraint per student and session)
- `DriveRound` (id, drive, roundType [APTITUDE/TECHNICAL/MANAGERIAL/SD/GD], orderNo, date, venue, durationMins, cutoff, instructions)
- `RoundResult` (id, round, student, score, result [PASSED/FAILED/ABSENT/PENDING], remarks)
- `Resume` (id, student, fileUrl, version, status [PENDING/REVIEWED/APPROVED/REJECTED], atsScore, atsBreakdown stored as a JSON string in a TEXT column, feedback, reviewedBy)
- `Hackathon` (id, name, organizer, mode [ONLINE/OFFLINE/HYBRID], startDate, endDate, registrationDeadline, prize, teamSize, location, link, description, status)
- `Internship` (id, company, role, stipend, durationMonths, location, mode, eligibility, deadline, applyUrl, howToApply, skillsRequired, status)
- `OffCampusJob` (id, company, role, packageLpa, location, batchEligible, experience, skillsRequired, deadline, applyUrl, howToApply, status)
- `Bookmark` (id, student, listingType [HACKATHON/INTERNSHIP/JOB], listingId)

### Module build order and required endpoints
1. **student**: CRUD, `GET /api/students?search&branch&year&minCgpa&eligible`, `GET /api/students/export` (CSV download), `GET /api/students/me`. Registration auto-creates the User and Student in one transaction. Duplicate rollNo/email returns 409. `placementEligible = cgpa >= 6.0 && backlogs == 0` with thresholds read from `application.yml`.
2. **companydrive**: CRUD for companies and drives, `GET /api/drives/upcoming`, `GET /api/drives/{id}` (includes company, rounds and eligible-student count), `GET /api/drives/{id}/eligible-students`.
3. **driveround** (module 5, build third because it depends on drives): CRUD for rounds per drive with ordering, bulk result upload via CSV, `GET /api/rounds/{id}/results`, `GET /api/drives/{id}/funnel` (students passing each round).
4. **questionbank**: CRUD, filter by drive, company, role, roundType and difficulty, `GET /api/questions/random?count&difficulty&role` for practice mode, bulk CSV import.
5. **attendance/crt** and **attendance/classroom**: session CRUD, `POST /api/{crt|class}/sessions/{id}/mark` (bulk marking for a list of students), `GET /api/attendance/student/{id}` with percentage computed by query, `GET /api/attendance/report?from&to&branch` with a defaulter list (below 75%).
6. **resume**: upload PDF (`MultipartFile`, PDF only, max 5 MB), extract text with PDFBox, compute the ATS score in `AtsScorer.java` using: keyword match against a target job description (40 points), section completeness for education, skills, projects, experience and contact (30), formatting and length checks (20), action-verb and quantified-achievement detection (10). Store the breakdown JSON and a list of suggestions. Endpoints: upload, list, TPO review (status and feedback), re-score with a new job description.
7. **hackathon**, 8. **internship**, 9. **offcampusjob**: build ONE generic `ListingService<T>` abstraction in the `listing` package and configure it three times. Include filters, `GET /upcoming`, `GET /{id}`, student bookmarking, and an ordered "how to apply" steps list.
10. **jobs**: `ExpireListingsJob` (`@Scheduled` nightly, marks past-deadline hackathons, internships and jobs as CLOSED) and `RemindersJob` (daily, flags items closing within 3 days).
11. **dashboard**: `GET /api/dashboard/summary` returning counts of students, eligible students, upcoming drives, average attendance, pending resumes and listings closing soon.

## PHASE 4: INSTITUTE FRONTEND (HTML, CSS, JS, one file at a time)

Order: `index.html`, `login.html`, `register.html`, `dashboard.html`, then every page module by module (1 through 9). Requirements:
- Every page includes the shared navbar/sidebar (rendered by `ui.js`), checks the JWT on load via `auth.js`, and shows or hides menu items by role.
- Students: a 3-step registration form (Personal, Academic, Skills and Links) with client-side validation and a progress bar, plus a list with filters and a CSV export button.
- Drives: a card grid of upcoming drives with a countdown, and a detail page with the company profile and a round timeline.
- Question bank: filter chips for company, role and difficulty, and a Practice Mode that shows one question at a time with a reveal-answer button.
- Attendance: a checkbox grid for marking, a per-student percentage ring, and a defaulter table.
- Resume and ATS: drag-and-drop upload, a circular ATS score gauge, a Chart.js breakdown bar chart, and a suggestions list.
- Hackathons, internships and off-campus jobs: one reusable listing-card renderer and filter bar in `ui.js`, an "Apply Now" button that opens the external link in a new tab, and an expandable "How to apply" panel.
- Dashboard: stat cards plus Chart.js charts.
- No inline credentials, escape all user-provided text before inserting it into the DOM, and keep each page's script in its own `.js` file or a `<script type="module">` block.

## PHASE 5: INTEGRATION CONTRACT FOR THE TEAMMATE'S PLACEMENT PILLAR

Do NOT build any placement features. Instead, make the institute pillar easy for another Spring Boot service to consume:

1. Keep every endpoint under `/api/**` with stable paths and the `ApiResponse` / `PageResponse` wrappers, and never change a published path or field name without noting it in `docs/api-spec.md`.
2. Add a read-only **integration API** under `/api/integration/**` that the placement service can call, secured with the same JWT scheme (shared secret configured by `JWT_SECRET`) or a service API key header `X-API-KEY` from `institute.integration.api-key`:
   - `GET /api/integration/students/{id}` and `GET /api/integration/students?rollNo=` (name, rollNo, branch, year, cgpa, backlogs, placementEligible, email, phone)
   - `GET /api/integration/drives/upcoming` and `GET /api/integration/drives/{id}` (company, roles, package range, eligibility rules, status, last date to apply)
   - `GET /api/integration/drives/{id}/eligible-students`
   - `GET /api/integration/students/{id}/resume` (latest approved resume URL and ATS score)
3. Enable CORS configurable through `application.yml` so the teammate's frontend (another origin or port) can call the institute API in development.
4. Publish the full contract with springdoc-openapi at `/v3/api-docs` and in `docs/api-spec.md`, with a "Consumed by placement pillar" section listing the integration endpoints and example JSON.
5. Add MockMvc tests for every integration endpoint.

## PHASE 6: QUALITY, DOCS, DEPLOYMENT

1. JUnit and MockMvc tests (running against SQLite) for auth, student registration, attendance percentage, `AtsScorer` and the integration endpoints.
2. A complete `README.md` with prerequisites (JDK 17 and Maven only; SQLite needs no separate install), where each `.db` file is created, how to back up or reset it, setup for the institute pillar, `mvn clean install`, how to run it with `mvn spring-boot:run -pl institute`, how to run it with docker-compose, default seeded logins (admin, tpo, student), the folder map, and a short "Connecting the placement pillar" section pointing to the integration API.
3. `docs/api-spec.md` listing every endpoint with method, path, role, and request and response examples (also expose Swagger UI at `/swagger-ui.html`).
4. `docs/db-diagram.md` with a Mermaid ER diagram for the institute database.
5. `docs/deployment.md` covering environment variables, Flyway migrations, building the jar, and running behind Nginx.
6. Final checklist: build all modules, run tests, start the institute pillar, confirm it works with no other service running, and call each integration endpoint with curl to confirm the teammate can consume it.

## OUTPUT FORMAT RULES (apply to every response)

- Heading with the full file path, followed by a single code block with the complete file. No abbreviations.
- After each module, print a progress tracker like `[x] student  [x] companydrive  [ ] driveround ...`.
- If a response would exceed your output limit, stop at a clean file boundary and say "CONTINUE FROM: <next file path>". Do not truncate mid-file.
- Do not ask clarifying questions; where something is unspecified, choose a sensible default and note it in one line.
- Begin now with PHASE 1.
</USER_REQUEST>
<ADDITIONAL_METADATA>
The current local time is: 2026-10-21T15:43:57+05:30.

The user's current state is as follows:
Active Document: c:\Users\91978\OneDrive\Desktop\33\scaffold.sh (LANGUAGE_SHELL)
Cursor is on line: 1
Other open documents:
- c:\Users\91978\OneDrive\Desktop\33\scaffold.sh (LANGUAGE_SHELL)
</ADDITIONAL_METADATA>