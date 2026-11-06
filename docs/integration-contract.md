# Institute Integration Contract

This document defines the formal, single-source-of-truth HTTP REST integration contract between the **Institute Pillar** (data provider) and the **Placement Pillar** (consumer).

---

## 1. Authentication & Security

- **Base Path**: `/institute/api/integration` (via Portal `:8080`) or `/api/integration` (direct to Institute `:8081`).
- **Authorization**:
  - Primary: Header `X-API-KEY: <institute.integration.api-key>` (configured via `INSTITUTE_API_KEY`).
  - Secondary: Standard Bearer JWT in `Authorization` header signed with the shared `JWT_SECRET`.
- **Envelopes**: Every response returns standard `ApiResponse<T>` or `ApiResponse<PageResponse<T>>`.
- **Errors**: Return `{ "success": false, "error": { "code": "...", "message": "..." } }`.
- **Timeouts & Reliability**:
  - Connect Timeout: 2 seconds
  - Read Timeout: 5 seconds
  - Retries: 2 retries with exponential backoff before fallback to stale `InstituteCache`.

---

## 2. Integration Endpoints

| Method | Path | Summary | Description | Return Type |
|---|---|---|---|---|
| `GET` | `/students/{id}` | Get Student by ID | Fetch snapshot profile for application matching | `ApiResponse<StudentSummary>` |
| `GET` | `/students?rollNo={rollNo}` | Find Student by Roll Number | Lookup student by unique collegiate roll number | `ApiResponse<StudentSummary>` |
| `GET` | `/drives/upcoming` | List Upcoming Drives | Fetch upcoming master placement drives | `ApiResponse<PageResponse<DriveSummary>>` or `List<DriveSummary>` |
| `GET` | `/drives/{id}` | Get Drive Details | Fetch drive requirements, CTC, branches, CGPA | `ApiResponse<DriveSummary>` |
| `GET` | `/drives/{id}/eligible-students` | Get Eligible Students | Candidate list satisfying CGPA, branch, backlogs | `ApiResponse<PageResponse<StudentSummary>>` |
| `GET` | `/students/{id}/resume` | Latest Approved Resume | Get file URL and verified ATS score (404 if none) | `ApiResponse<ResumeSummary>` |
| `GET` | `/drives/{id}/rounds` | Drive Evaluation Rounds | Master rounds configured in Institute drive | `ApiResponse<List<RoundSummary>>` |
| `GET` | `/internships` | Open Internships | Corporate & research listings (only OPEN) | `ApiResponse<PageResponse<ListingSummary>>` |
| `GET` | `/hackathons` | Open Hackathons | National hackathons (only OPEN) | `ApiResponse<PageResponse<ListingSummary>>` |
| `GET` | `/offcampus-jobs` | Open Off-Campus Jobs | Verified fresher job listings (only OPEN) | `ApiResponse<PageResponse<ListingSummary>>` |
| `GET` | `/users/export` | User Account Export | Export existing BCrypt user hashes for SSO (API Key only) | `ApiResponse<List<UserExport>>` |

---

## 3. Data Transfer Objects (JSON Schemas)

### StudentSummary
```json
{
  "id": 12,
  "name": "Ananya Sharma",
  "rollNo": "21CS045",
  "email": "ananya@college.edu",
  "phone": "9876543210",
  "branch": "CSE",
  "year": 4,
  "cgpa": 8.4,
  "backlogs": 0,
  "placementEligible": true
}
```

### DriveSummary
```json
{
  "id": 7,
  "title": "TCS Ninja 2026",
  "company": {
    "id": 3,
    "name": "TCS",
    "website": "https://www.tcs.com",
    "industry": "IT Services"
  },
  "roles": ["Software Engineer"],
  "packageMin": 3.36,
  "packageMax": 7.0,
  "location": "Pan India",
  "driveDate": "2026-11-15",
  "lastDateToApply": "2026-11-05",
  "eligibilityCgpa": 6.0,
  "eligibleBranches": ["CSE", "IT", "ECE"],
  "maxBacklogs": 0,
  "status": "UPCOMING"
}
```

### ResumeSummary
```json
{
  "studentId": 12,
  "fileUrl": "/institute/uploads/resumes/12_v3.pdf",
  "atsScore": 78,
  "status": "APPROVED"
}
```

### RoundSummary
```json
{
  "id": 21,
  "roundType": "APTITUDE",
  "orderNo": 1,
  "date": "2026-11-15",
  "venue": "Seminar Hall"
}
```

### ListingSummary
```json
{
  "id": 5,
  "type": "INTERNSHIP",
  "title": "Backend Engineering Intern",
  "organizer": "Microsoft India",
  "mode": "HYBRID",
  "location": "Hyderabad / Bangalore",
  "stipendOrPackage": "₹80,000 / month",
  "startDate": "2026-12-01",
  "deadline": "2026-11-15",
  "applyUrl": "https://careers.microsoft.com",
  "howToApply": ["Step 1: Submit online portal application", "Step 2: Complete Codility coding assessment"]
}
```

### UserExport
```json
{
  "email": "aarav@example.com",
  "passwordHash": "$2a$10$abcdef...",
  "role": "STUDENT",
  "name": "Aarav Sharma",
  "rollNo": "21CS001"
}
```
