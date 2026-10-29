# Institute Pillar Architecture & Code Audit

This audit document provides a complete technical baseline of the existing Institute Pillar codebase, establishing real identifiers, coordinates, security models, data contracts, and routing paths prior to portal and placement pillar integration.

---

## 1. Maven Coordinates & Dependencies

### Parent Project
- **Coordinates**: `com.placementassist:placement-assist-parent:1.0.0-SNAPSHOT`
- **Packaging**: `pom`
- **Spring Boot Version**: `3.3.4` (`spring-boot-starter-parent`)
- **Java Target**: `Java 17` (`<java.version>17</java.version>`)
- **Runtime Environment**: Java 25 (`25.0.4+7-LTS-189`) with Byte Buddy experimental agent options configured in Surefire.
- **Existing Submodules**:
  - `shared-lib`
  - `institute`

### Shared Library (`shared-lib`)
- **Coordinates**: `com.placementassist:shared-lib:1.0.0-SNAPSHOT`
- **Dependencies**: `spring-boot-starter-web`, `spring-boot-starter-security`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, `jjwt-api`, `jjwt-impl`, `jjwt-jackson` (v`0.12.6`), `opencsv` (v`5.9`), `springdoc-openapi-starter-webmvc-ui` (v`2.6.0`), `lombok` (v`1.18.38`).

### Institute Pillar (`institute`)
- **Coordinates**: `com.placementassist:institute:1.0.0-SNAPSHOT`
- **Dependencies**: `shared-lib`, `sqlite-jdbc` (v`3.46.1.0`), `hibernate-community-dialects` (v`6.5.3.Final`), `flyway-core` (v`10.10.0`), `pdfbox` (v`3.0.3`), `mapstruct` (v`1.5.5.Final`), `spring-boot-starter-test`, `spring-security-test`.
- **Packaging**: `spring-boot-maven-plugin` repackaged executable JAR with `copy-resources` maven plugin syncing `shared-web/css` and `shared-web/js` into `src/main/resources/static/`.

---

## 2. Base Package Names & Application Structure

- **Shared Library Package**: `com.placementassist.shared`
  - `api/`: `ApiResponse<T>`, `PageResponse<T>`, `ErrorResponse`
  - `exception/`: `ApiException`, `GlobalExceptionHandler`, `NotFoundException`, `ConflictException`, `UnauthorizedException`
  - `security/`: `JwtAuthFilter`, `JwtService`, `Role`, `SecurityUtils`
- **Institute Application Package**: `com.placementassist.institute`
  - Main Class: `InstituteApplication.java` (`@SpringBootApplication(scanBasePackages = {"com.placementassist.shared", "com.placementassist.institute"})`)
  - Modules:
    1. `auth/`: `User`, `UserRepository`, `AuthService`, `AuthController`, `dto/`
    2. `student/`: `Student`, `StudentRepository`, `StudentService`, `StudentController`, `StudentMapper`, `dto/`, `StudentSeedRunner`
    3. `companydrive/`: `Company`, `Drive`, repositories, `CompanyDriveService`, `CompanyController`, `DriveController`, `dto/`, `CompanyDriveSeedRunner`
    4. `questionbank/`: `Question`, `QuestionRepository`, `QuestionService`, `QuestionController`, `QuestionMapper`, `dto/`, `QuestionSeedRunner`
    5. `attendance/`: `CrtSession`, `ClassSession`, `AttendanceRecord`, repositories, `AttendanceService`, `AttendanceController`, `CrtSessionController`, `ClassSessionController`, `dto/`, `AttendanceSeedRunner`
    6. `driveround/`: `DriveRound`, `RoundResult`, repositories, `DriveRoundService`, `DriveRoundController`, `DriveRoundMapper`, `dto/`, `DriveRoundSeedRunner`
    7. `resume/`: `Resume`, `ResumeRepository`, `AtsScorer`, `ResumeService`, `ResumeController`, `dto/`, `ResumeSeedRunner`
    8. `hackathon/`: `Hackathon`, `HackathonRepository`, `HackathonService`, `HackathonController`, `dto/`, `HackathonSeedRunner`
    9. `internship/`: `Internship`, `InternshipRepository`, `InternshipService`, `InternshipController`, `dto/`, `InternshipSeedRunner`
    10. `offcampusjob/`: `OffCampusJob`, `OffCampusJobRepository`, `OffCampusJobService`, `OffCampusJobController`, `dto/`, `OffCampusJobSeedRunner`
    11. `dashboard/`: `DashboardService`, `DashboardController`, `dto/`
    12. `integration/`: `IntegrationController`, `dto/`
    13. `jobs/`: `ExpireListingsJob`, `RemindersJob`
    14. `config/`: `SecurityConfig`, `WebConfig`, `OpenApiConfig`, `SchedulingConfig`

---

## 3. Security, Roles & JWT Claims

### Role Enum (`com.placementassist.shared.security.Role`)
Current Enum constants:
- `STUDENT`
- `ADMIN`
- `TPO`

*(Will be extended additively with `DIRECTOR` and `COMPANY` in Phase 2)*

### JWT Configuration
- **Secret Property**: `${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}`
- **Access Expiration**: `${jwt.expiration-ms:86400000}` (24 hours default; aligns with `${JWT_ACCESS_MINUTES:1440}`)
- **Refresh Expiration**: `${jwt.refresh-expiration-ms:604800000}` (7 days; aligns with `${JWT_REFRESH_DAYS:7}`)
- **Existing Claims**:
  - `sub`: User email
  - `userId`: Numeric database primary key (`Long`)
  - `role`: Role string (`STUDENT`, `TPO`, `ADMIN`)
  - `type`: Token type (`REFRESH` on refresh tokens)
  - Extra claims support: `rollNo`, `name`, `uid`

### Security Configuration (`com.placementassist.institute.config.SecurityConfig`)
- Stateless session management (`SessionCreationPolicy.STATELESS`)
- CSRF disabled
- Public routes: `/`, `/index.html`, `/login.html`, `/register.html`, `/dashboard.html`, `/pages/**`, `/css/**`, `/js/**`, `/assets/**`, `/favicon.ico`, `/swagger-ui/**`, `/v3/api-docs/**`, `/api/auth/login`, `/api/auth/register`, `/api/auth/refresh`
- Secured integration routes: `/api/integration/**` (validated via `X-API-KEY: inst-sec-key-7f9a12b4e8c345d6` or Bearer JWT)
- Authenticated endpoints: All other `/api/**` routes require valid JWT.

---

## 4. Key Entities: User & Student

### User (`com.placementassist.institute.modules.auth.User`)
- Table: `users`
- Fields: `id` (Long, PK), `email` (unique String), `passwordHash` (BCrypt String), `role` (Role Enum STRING), `createdAt` (LocalDateTime), `updatedAt` (LocalDateTime).

### Student (`com.placementassist.institute.modules.student.Student`)
- Table: `students`
- Fields: `id` (Long, PK), `user` (`@OneToOne` JoinColumn `user_id`), `name` (String), `rollNo` (unique String), `email` (unique String), `phone` (String), `branch` (String), `year` (Integer), `cgpa` (Double), `backlogs` (Integer), `placementEligible` (Boolean), `skills` (`@ElementCollection` table `student_skills`), `createdAt`, `updatedAt`.

---

## 5. Web Client & Frontend Asset Structure

### URL Resolution in `shared-web/js/api.js`
- Currently: `const BASE_URL = window.location.origin;`
- Endpoint calls: `const url = endpoint.startsWith('http') ? endpoint : `${BASE_URL}${endpoint}`;`
- 401 Handling: Clears `access_token`, `refresh_token`, `current_user` from `localStorage` and redirects to `/login.html`.
- **Audit Finding**: When served behind context path `/institute` or portal gateway (`:8080/institute`), `endpoint` needs to resolve relative to the current context path.

### Asset Paths in Static HTML
- Pages reference CSS as:
  `<link href="/css/theme.css" rel="stylesheet">`
  `<link href="/css/components.css" rel="stylesheet">`
- Pages link to each other as:
  `<a href="/pages/2-company-drives/drive-list.html">`
- **Audit Finding**: Hard-coded root-absolute paths (`/css/...`, `/pages/...`) require adjustment to context-aware relative paths or dynamic prefixing so they function correctly under `/institute` when proxied by Spring Cloud Gateway on `:8080`.

### Navbar & Layout Rendering (`shared-web/js/ui.js`)
- `UIManager.initLayout(activePageKey)` dynamically renders:
  - `#sidebar-container` -> `renderSidebar(role, activeKey)`
  - `#topbar-container` -> `renderTopbar(user)`
- Sidebar includes branding, active state highlighting, and links to all 9 modules.
- Topbar includes mobile toggle, user email/name, role badge, and logout button.

---

## 6. Complete REST Endpoint Inventory

### Authentication (`/api/auth`)
- `POST /api/auth/login` - Authenticate and retrieve JWT tokens
- `POST /api/auth/register` - Self-registration for students
- `POST /api/auth/refresh` - Rotate access token using refresh token
- `GET /api/auth/me` - Current authenticated user details

### Students (`/api/students`)
- `POST /api/students` - Create student record (`TPO`, `ADMIN`)
- `GET /api/students` - Paginated students list with branch/year/cgpa/eligibility filters
- `GET /api/students/{id}` - Student detail by ID
- `PUT /api/students/{id}` - Update student record (`TPO`, `ADMIN`)
- `DELETE /api/students/{id}` - Remove student record (`ADMIN`)
- `GET /api/students/export` - Export filtered student records as CSV (`TPO`, `ADMIN`)

### Companies & Placement Drives (`/api/companies`, `/api/drives`)
- `POST /api/companies`, `GET /api/companies`, `GET /api/companies/{id}`, `PUT /api/companies/{id}`
- `POST /api/drives`, `GET /api/drives`, `GET /api/drives/{id}`, `PUT /api/drives/{id}`, `DELETE /api/drives/{id}`
- `GET /api/drives/{id}/eligible-students` - Retrieve eligible candidate list

### Question Bank (`/api/questions`)
- `POST /api/questions`, `GET /api/questions`, `GET /api/questions/{id}`, `PUT /api/questions/{id}`, `DELETE /api/questions/{id}`
- `GET /api/questions/practice` - Practice mode query by category and difficulty

### Attendance (`/api/attendance`)
- `POST /api/attendance/crt-sessions`, `GET /api/attendance/crt-sessions`, `GET /api/attendance/crt-sessions/{id}`
- `POST /api/attendance/class-sessions`, `GET /api/attendance/class-sessions`, `GET /api/attendance/class-sessions/{id}`
- `POST /api/attendance/crt/mark-bulk`, `POST /api/attendance/class/mark-bulk`
- `GET /api/attendance/student/{studentId}` - Detailed student stats & session history
- `GET /api/attendance/report` - Consolidated defaulters (< 75%) and cohort report
- `GET /api/attendance/report/export` - Defaulters CSV export

### Drive Rounds & Results (`/api/rounds`)
- `POST /api/rounds`, `GET /api/rounds/drive/{driveId}`, `GET /api/rounds/{id}`, `PUT /api/rounds/{id}`, `DELETE /api/rounds/{id}`
- `POST /api/rounds/{id}/results` - Submit round evaluation results
- `GET /api/rounds/{id}/results` - List candidate evaluations for a round

### Resumes & ATS Scoring (`/api/resumes`)
- `POST /api/resumes/upload` - PDF upload & deterministic ATS scoring
- `GET /api/resumes/student/{studentId}` - Resume revision history
- `GET /api/resumes/{id}` - Resume detail with ATS breakdown
- `POST /api/resumes/{id}/review` - Staff approve/reject resume with comments
- `GET /api/resumes/{id}/file` - Download/view uploaded resume PDF

### Hackathons (`/api/hackathons`)
- `POST /api/hackathons`, `GET /api/hackathons`, `GET /api/hackathons/{id}`, `PUT /api/hackathons/{id}`, `DELETE /api/hackathons/{id}`
- `POST /api/hackathons/{id}/bookmark` - Toggle student bookmark

### Internships (`/api/internships`)
- `POST /api/internships`, `GET /api/internships`, `GET /api/internships/{id}`, `PUT /api/internships/{id}`, `DELETE /api/internships/{id}`
- `POST /api/internships/{id}/bookmark` - Toggle student bookmark

### Off-Campus Jobs (`/api/jobs`)
- `POST /api/jobs`, `GET /api/jobs`, `GET /api/jobs/{id}`, `PUT /api/jobs/{id}`, `DELETE /api/jobs/{id}`
- `POST /api/jobs/{id}/bookmark` - Toggle student bookmark

### Dashboard (`/api/dashboard`)
- `GET /api/dashboard/stats` - Institute-level KPIs, branch distributions, upcoming deadlines

### Integration Endpoints (`/api/integration`)
Already implemented in `IntegrationController.java`:
- `GET /api/integration/students/{id}`
- `GET /api/integration/students?rollNo={rollNo}`
- `GET /api/integration/drives/upcoming`
- `GET /api/integration/drives/{id}`
- `GET /api/integration/drives/{id}/eligible-students`
- `GET /api/integration/students/{id}/resume`
*(Missing contract endpoints to add in Phase 4: `GET /api/integration/drives/{id}/rounds`, `GET /api/integration/internships`, `GET /api/integration/hackathons`, `GET /api/integration/offcampus-jobs`, and `GET /api/integration/users/export`)*

---

## 7. Current Infrastructure & Ports

- **Current Port**: `8081` (`server.port=${PORT:8081}`)
- **Database**: SQLite WAL mode at `./data/institute.db`
- **Upload Directory**: `./data/institute/uploads/resumes` (or `./uploads/resumes`)
- **Integration API Key**: `inst-sec-key-7f9a12b4e8c345d6`
- **Docker Compose**: Currently defines service `institute-service` on port `8081:8081`.

---

## 8. Test Status

- **Surefire Test Suite**: 11 Test Classes, **30 Tests Run**.
- **Results**: **30 Passed, 0 Failures, 0 Errors, 0 Skipped** (`BUILD SUCCESS`).
- All controllers, JPA models, security integration, and MockMvc endpoints verified.
