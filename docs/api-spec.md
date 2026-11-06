# Institute Pillar REST API Specification

This document provides complete documentation for the REST API endpoints exposed by the Institute pillar of the Placement Assistance Web Application.

---

## 1. Authentication & Security

All API endpoints (except public authentication and static assets) are secured.

- **Client Authentication**: Send JWT Bearer token in the `Authorization` HTTP header:
  ```http
  Authorization: Bearer <jwt-access-token>
  ```
- **Inter-Service Integration Authentication**: Internal endpoints under `/api/integration/**` are protected by a shared API secret key sent in the `X-API-KEY` header:
  ```http
  X-API-KEY: inst-sec-key-7f9a12b4e8c345d6
  ```

---

## 2. Standard Response Envelopes

### Success Response (`ApiResponse<T>`)
```json
{
  "success": true,
  "data": { ... },
  "message": "Operation completed successfully",
  "timestamp": "2026-10-21T18:30:00Z"
}
```

### Paginated Response (`PageResponse<T>`)
```json
{
  "content": [ ... ],
  "page": 0,
  "size": 10,
  "totalElements": 48,
  "totalPages": 5,
  "first": true,
  "last": false
}
```

### Error Response (`ErrorResponse`)
```json
{
  "success": false,
  "error": {
    "code": "RESOURCE_NOT_FOUND",
    "message": "Student not found with id: 42",
    "details": null
  },
  "timestamp": "2026-10-21T18:30:00Z"
}
```

---

## 3. Module Endpoints

### 3.1 Authentication (`/api/auth`)

| Method | Endpoint | Required Role | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Public | Student self-registration with academic details |
| `POST` | `/api/auth/login` | Public | Authenticates credentials and returns JWT access + refresh tokens |
| `GET` | `/api/auth/me` | Authenticated | Returns currently authenticated user details |
| `POST` | `/api/auth/refresh` | Public | Refreshes access token given valid refresh token |

#### Register Request Body:
```json
{
  "email": "student@college.edu",
  "password": "Password123!",
  "name": "Priya Patel",
  "phone": "9876543210",
  "rollNo": "21CS055",
  "branch": "CSE",
  "year": 4,
  "cgpa": 8.75,
  "backlogs": 0
}
```

---

### 3.2 Students (`/api/students`)

| Method | Endpoint | Required Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/students` | Authenticated | Search & filter students (search, branch, year, placementEligible) |
| `GET` | `/api/students/{id}` | Authenticated | Fetch student profile by ID |
| `GET` | `/api/students/me` | STUDENT | Fetch profile of logged-in student |
| `POST` | `/api/students` | TPO, ADMIN | Create a new student record |
| `PUT` | `/api/students/{id}` | TPO, ADMIN | Update student academic records |
| `DELETE` | `/api/students/{id}` | ADMIN | Delete student record |
| `GET` | `/api/students/export` | TPO, ADMIN | Export all students as CSV download |

---

### 3.3 Companies & Drives (`/api/companies`, `/api/drives`)

| Method | Endpoint | Required Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/companies` | Authenticated | List all recruiting companies |
| `GET` | `/api/companies/{id}` | Authenticated | Fetch company details & contact info |
| `POST` | `/api/companies` | TPO, ADMIN | Create a new recruiting company |
| `GET` | `/api/drives` | Authenticated | Paginated recruitment drives with filters |
| `GET` | `/api/drives/{id}` | Authenticated | Drive details and eligibility criteria |
| `POST` | `/api/drives` | TPO, ADMIN | Schedule a new on-campus drive |
| `PUT` | `/api/drives/{id}` | TPO, ADMIN | Update drive schedule or status |
| `DELETE` | `/api/drives/{id}` | ADMIN | Delete drive |

#### Drive Create Request Body:
```json
{
  "companyId": 1,
  "title": "Microsoft SDE Hiring 2025",
  "description": "Full-time software engineering roles across cloud and platform teams.",
  "roles": "Software Engineer, Support Engineer",
  "packageMin": 14.0,
  "packageMax": 24.5,
  "eligibilityCgpa": 7.5,
  "eligibleBranches": "CSE,IT,ECE",
  "maxBacklogs": 0,
  "driveDate": "2025-11-20",
  "lastDateToApply": "2025-11-10",
  "location": "Auditorium Hall / Hybrid",
  "status": "UPCOMING"
}
```

---

### 3.4 Drive Rounds & Results (`/api/rounds`)

| Method | Endpoint | Required Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/rounds/drive/{driveId}` | Authenticated | List sequential rounds for a drive |
| `GET` | `/api/rounds/{id}` | Authenticated | Get round details |
| `POST` | `/api/rounds` | TPO, ADMIN | Add a round to a drive pipeline |
| `PUT` | `/api/rounds/{id}` | TPO, ADMIN | Update round details |
| `GET` | `/api/rounds/{roundId}/results` | Authenticated | Candidate evaluations for a round |
| `POST` | `/api/rounds/{roundId}/results` | TPO, ADMIN | Record/update candidate evaluation |

---

### 3.5 Question Bank (`/api/questions`)

| Method | Endpoint | Required Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/questions` | Authenticated | Filter questions by category, difficulty, company |
| `GET` | `/api/questions/{id}` | Authenticated | Get question detail with answer & explanation |
| `POST` | `/api/questions` | TPO, ADMIN | Add a new question to the bank |
| `PUT` | `/api/questions/{id}` | TPO, ADMIN | Update existing question |
| `GET` | `/api/questions/export` | TPO, ADMIN | Export question bank as CSV |

---

### 3.6 Attendance Management (`/api/crt-sessions`, `/api/class-sessions`, `/api/attendance`)

| Method | Endpoint | Required Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/crt-sessions` | Authenticated | List CRT training sessions |
| `POST` | `/api/crt-sessions` | TPO, ADMIN | Schedule a new CRT training module |
| `GET` | `/api/class-sessions` | Authenticated | List regular class sessions |
| `POST` | `/api/class-sessions` | TPO, ADMIN | Schedule a new class session |
| `GET` | `/api/attendance/session/crt/{id}` | Authenticated | Get attendance roster for CRT session |
| `GET` | `/api/attendance/session/class/{id}`| Authenticated | Get attendance roster for class session |
| `POST` | `/api/attendance/bulk` | TPO, ADMIN | Save bulk attendance marks for a session |
| `GET` | `/api/attendance/student/{id}/summary` | Authenticated | Cumulative attendance % for a student |
| `GET` | `/api/attendance/export/crt` | TPO, ADMIN | Export CRT attendance report as CSV |
| `GET` | `/api/attendance/export/class` | TPO, ADMIN | Export class attendance report as CSV |

#### Bulk Attendance Request Body:
```json
{
  "sessionId": 1,
  "sessionType": "CRT",
  "items": [
    { "studentId": 1, "status": "PRESENT", "remarks": "Active in session" },
    { "studentId": 2, "status": "ABSENT", "remarks": null }
  ]
}
```

---

### 3.7 Resumes & ATS Scoring (`/api/resumes`)

| Method | Endpoint | Required Role | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/resumes/upload` | Authenticated | Upload PDF resume, extract text, and compute ATS score |
| `GET` | `/api/resumes/{id}` | Authenticated | Fetch resume ATS scorecard and feedback breakdown |
| `GET` | `/api/resumes/student/{studentId}`| Authenticated | Fetch all resumes uploaded by a student |
| `PATCH` | `/api/resumes/{id}/status` | TPO, ADMIN | Update review status (APPROVED / REJECTED) |
| `GET` | `/api/resumes/{id}/download` | Authenticated | Download the uploaded raw PDF file |

---

### 3.8 Opportunities: Hackathons, Internships, Jobs

| Module | Base Path | Bookmark Toggle | Bookmarked List |
| :--- | :--- | :--- | :--- |
| **Hackathons** | `/api/hackathons` | `POST /api/hackathons/{id}/bookmark` | `GET /api/hackathons/bookmarked` |
| **Internships** | `/api/internships` | `POST /api/internships/{id}/bookmark` | `GET /api/internships/bookmarked` |
| **Off-Campus Jobs** | `/api/offcampus-jobs` | `POST /api/offcampus-jobs/{id}/bookmark` | `GET /api/offcampus-jobs/bookmarked` |

---

### 3.9 Dashboard Summary (`/api/dashboard`)

| Method | Endpoint | Required Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/dashboard/summary` | Authenticated | Aggregates all KPI counts, branch & drive charts, and deadlines |

---

### 3.10 Placement Pillar Integration (`/api/integration`)

Protected by `X-API-KEY`:

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/integration/students/{id}` | Student academic verification and CGPA for live drive eligibility |
| `GET` | `/api/integration/students/by-roll/{rollNo}` | Look up student by academic roll number |
| `GET` | `/api/integration/drives/upcoming` | Live on-campus drives with package brackets and branch cutoffs |
| `GET` | `/api/integration/drives/{id}` | Complete drive requirements and criteria |
| `GET` | `/api/integration/resumes/{id}` | Resume metadata and ATS score for interviewers |
| `GET` | `/api/integration/drives` | Export all active and upcoming drives |
| `GET` | `/api/integration/drives/{id}/rounds` | All evaluation rounds for a drive |
| `GET` | `/api/integration/internships` | Export active internships |
| `GET` | `/api/integration/hackathons` | Export active hackathons |
| `GET` | `/api/integration/offcampus-jobs` | Export active off-campus jobs |
| `GET` | `/api/integration/users/export` | User directory export for Portal initial seed sync |

---

## 4. Portal Gateway & SSO APIs (Port 8080)

### 4.1 Authentication & User Management (`/api/portal/auth`, `/api/portal/users`)

| Method | Endpoint | Required Role | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/portal/auth/register` | Public | Student/User registration with validation against Institute records |
| `POST` | `/api/portal/auth/login` | Public | Single Sign-On credentials check issuing universal JWT token |
| `POST` | `/api/portal/auth/logout` | Authenticated | Clears SSO session and browser cookies |
| `GET` | `/api/portal/auth/me` | Authenticated | Returns logged-in user profile, role, and domain permissions |
| `GET` | `/api/portal/users` | ADMIN | List registered users with pagination & search |
| `PUT` | `/api/portal/users/{id}/role` | ADMIN | Change user role (STUDENT, TPO, DIRECTOR, COMPANY, ADMIN) |
| `DELETE` | `/api/portal/users/{id}` | ADMIN | Remove user account from portal directory |

### 4.2 System Status & Routing (`/api/portal/status`)

| Method | Endpoint | Required Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/portal/status/pillars` | Public | Dynamic ping status of Institute (8081) and Placement (8082) pillars |
| `GET` | `/actuator/health` | Public | Gateway health status |

---

## 5. Placement Pillar APIs (Port 8082, Context `/placement`)

### 5.1 Recruitment Drives (`/placement/api/drives`)

| Method | Endpoint | Required Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/placement/api/drives/all` | Authenticated | List all active, upcoming, and completed recruitment drives |
| `GET` | `/placement/api/drives` | Authenticated | Paged drives list with status filter and search query |
| `GET` | `/placement/api/drives/{id}` | Authenticated | Retrieve complete drive details with associated rounds |
| `POST` | `/placement/api/drives` | TPO, DIRECTOR | Launch a new campus recruitment drive |
| `PUT` | `/placement/api/drives/{id}` | TPO, DIRECTOR | Update recruitment drive details |
| `PUT` | `/placement/api/drives/{id}/status` | TPO, DIRECTOR | Modify drive status (`UPCOMING`, `ONGOING`, `COMPLETED`) |

### 5.2 Recruitment Stages & Rounds (`/placement/api/stages`)

| Method | Endpoint | Required Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/placement/api/drives/{driveId}/stages` | Authenticated | Get ordered sequence of stages for a drive |
| `POST` | `/placement/api/drives/{driveId}/stages` | TPO, DIRECTOR | Add a new recruitment stage (Test, Tech, HR) |
| `PUT` | `/placement/api/stages/{id}/activate` | TPO, DIRECTOR | Set stage as `ACTIVE` and broadcast live notification |
| `PUT` | `/placement/api/stages/{id}/complete` | TPO, DIRECTOR | Mark stage as `COMPLETED` |

### 5.3 Candidate Applications (`/placement/api/applications`)

| Method | Endpoint | Required Role | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/placement/api/drives/{driveId}/apply` | STUDENT | Submit candidate application for active campus drive |
| `GET` | `/placement/api/applications/my` | STUDENT | Fetch all drive applications for current student |
| `GET` | `/placement/api/drives/{driveId}/applications` | TPO, DIRECTOR, COMPANY, ADMIN | Review candidate applicants for drive |
| `PUT` | `/placement/api/applications/{id}/status` | TPO, DIRECTOR, ADMIN | Update status (`SHORTLISTED`, `REJECTED`, `SELECTED`) |

### 5.4 Pre-Placement Talks & Q&A (`/placement/api/talks`)

| Method | Endpoint | Required Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/placement/api/talks` | Authenticated | List all scheduled and live corporate PPT sessions |
| `GET` | `/placement/api/talks/{id}` | Authenticated | Get talk details, speaker profile, and slides |
| `POST` | `/placement/api/talks` | TPO, DIRECTOR, COMPANY | Schedule new corporate presentation |
| `GET` | `/placement/api/talks/{id}/questions` | Authenticated | Get candidate Q&A thread for presentation |
| `POST` | `/placement/api/talks/{id}/questions` | Authenticated | Ask a live question to recruiter panel |
| `POST` | `/placement/api/talks/{id}/questions/{qId}/upvote` | Authenticated | Upvote question in real-time |

### 5.5 Live Announcements & SSE (`/placement/api/live-updates`)

| Method | Endpoint | Required Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/placement/api/live-updates/recent` | Authenticated | Get recent announcements feed |
| `GET` | `/placement/api/live-updates/stream` | Authenticated | Server-Sent Events (SSE) stream for all drive alerts |
| `GET` | `/placement/api/live-updates/stream/{driveId}` | Authenticated | SSE stream scoped to specific drive |
| `POST` | `/placement/api/live-updates` | TPO, DIRECTOR, COMPANY | Dispatch urgent broadcast announcement |

### 5.6 Drive Attendance & QR Verification (`/placement/api/stages/{stageId}/attendance`)

| Method | Endpoint | Required Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/placement/api/stages/{stageId}/attendance/qr` | Authenticated | Generate rotating QR code payload and session PIN |
| `POST` | `/placement/api/stages/{stageId}/attendance/scan` | STUDENT | Verify student check-in via QR payload or manual PIN |
| `GET` | `/placement/api/stages/{stageId}/attendance` | TPO, DIRECTOR, COMPANY | List verified attendees with timestamps |
| `POST` | `/placement/api/stages/{stageId}/attendance/manual` | TPO, DIRECTOR, COMPANY | Manually mark student present by roll number |

### 5.7 Results & Selection Lists (`/placement/api/stages/{stageId}/results`)

> [!IMPORTANT]
> **Strict Authority Enforcement**: Only `TPO` and `DIRECTOR` roles are authorized to create or modify candidate results. Any attempt by `STUDENT`, `COMPANY`, or `ADMIN` returns HTTP 403 Forbidden.

| Method | Endpoint | Required Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/placement/api/stages/{stageId}/results` | Authenticated | View official verified candidate results for stage |
| `POST` | `/placement/api/stages/{stageId}/results` | **TPO, DIRECTOR ONLY** | Record candidate evaluation (`SELECTED`, `NOT_SELECTED`, `WAITLISTED`) |
| `POST` | `/placement/api/stages/{stageId}/results/bulk` | **TPO, DIRECTOR ONLY** | Bulk candidate evaluation list update |
| `GET` | `/placement/api/drives/{driveId}/final-selected` | Authenticated | Get final selected candidates list for drive |

### 5.8 Placed Students & Public Wall (`/placement/api/placed`, `/placement/api/public/placed`)

| Method | Endpoint | Required Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/placement/api/placed` | Authenticated | Full list of verified placed students with packages |
| `GET` | `/placement/api/placed/highlights` | Authenticated | Summary statistics (Highest CTC, Average CTC, Total Placed) |
| `POST` | `/placement/api/placed` | TPO, DIRECTOR | Record confirmed placement offer for student |
| `GET` | `/placement/api/public/placed` | Public | Public honor roll endpoint with search and tier filters |
| `GET` | `/placement/api/public/placed/highlights` | Public | Public placement highlights ticker |

### 5.9 Opportunities Feed (`/placement/api/opportunities`)

| Method | Endpoint | Required Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/placement/api/opportunities` | Authenticated | Aggregated feed of internships, hackathons, and off-campus jobs |
| `GET` | `/placement/api/opportunities/{id}` | Authenticated | Details for selected opportunity item |

### 5.10 Interview Feedback & Reviews (`/placement/api/feedback`)

| Method | Endpoint | Required Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/placement/api/feedback/my` | Authenticated | Past interview feedback submitted by student |
| `POST` | `/placement/api/feedback` | Authenticated | Submit new interview experience, rating, and tips |
| `GET` | `/placement/api/drives/{driveId}/feedback` | Authenticated | Aggregate candidate feedback for specific drive |

### 5.11 In-App Notifications (`/placement/api/notifications`)

| Method | Endpoint | Required Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/placement/api/notifications` | Authenticated | Fetch notifications for current user |
| `GET` | `/placement/api/notifications/unread-count` | Authenticated | Get unread alerts count for domain switcher badge |
| `PUT` | `/placement/api/notifications/{id}/read` | Authenticated | Mark individual notification as read |
| `PUT` | `/placement/api/notifications/read-all` | Authenticated | Mark all notifications as read |

