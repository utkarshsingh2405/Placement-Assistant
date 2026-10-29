# 🗄️ Master Database Directory — Placement Assistant Platform

This folder contains the active **SQLite Databases** powering all 3 pillars of the platform:

| Database File | Module / Service | Context Path | Primary Tables & Data |
| :--- | :--- | :--- | :--- |
| **`portal.db`** | **Portal Gateway & SSO** | Port `8088` (`/`) | `portal_users`, `audit_logs` |
| **`institute.db`** | **Academic Institute Pillar** | Port `8081` (`/institute`) | `students`, `student_skills`, `users`, `companies`, `drives`, `resumes`, `attendances`, `crt_sessions`, `hackathons`, `internships`, `offcampus_jobs` |
| **`placement.db`** | **Live Recruitment Pillar** | Port `8082` (`/placement`) | `placement_applications`, `placement_drives`, `placement_stages`, `placement_placed_students`, `placement_results`, `placement_attendance`, `placement_companies` |

---

## ⚡ Instant Database Inspector

To view all tables, row counts, and recent records across all 3 databases in your console, double-click:
```cmd
view_database.bat
```
Or run in PowerShell:
```powershell
python view_database.py
```

---

## 🔍 How to Browse and Modify Data

You can open and inspect any `.db` file using:
1. **DB Browser for SQLite** (Free, open-source GUI): [https://sqlitebrowser.org/](https://sqlitebrowser.org/)
2. **VS Code Extension**: "SQLite Viewer" by Florian Klampfer (allows clicking `.db` files directly in VS Code).
3. **SQLite CLI**: `sqlite3 institute.db`

All backend services write to and read from these database files in real-time. Any changes made through the web portal or directly in the database reflect immediately.
