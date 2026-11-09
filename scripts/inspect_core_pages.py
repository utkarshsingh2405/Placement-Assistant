import os
import re

pages = [
    # Institute
    "backend/portal/institute/src/main/resources/static/institute/dashboard.html",
    "backend/portal/institute/src/main/resources/static/institute/pages/1-students/student-list.html",
    "backend/portal/institute/src/main/resources/static/institute/pages/1-students/student-profile.html",
    "backend/portal/institute/src/main/resources/static/institute/pages/2-company-drives/drive-list.html",
    "backend/portal/institute/src/main/resources/static/institute/pages/3-question-bank/question-list.html",
    "backend/portal/institute/src/main/resources/static/institute/pages/4-attendance/class/class-sessions.html",
    "backend/portal/institute/src/main/resources/static/institute/pages/4-attendance/crt/crt-sessions.html",
    "backend/portal/institute/src/main/resources/static/institute/pages/5-drive-rounds/rounds-board.html",
    "backend/portal/institute/src/main/resources/static/institute/pages/6-resume-ats/ats-report.html",
    "backend/portal/institute/src/main/resources/static/institute/pages/7-hackathons/hackathon-list.html",
    "backend/portal/institute/src/main/resources/static/institute/pages/8-internships/internship-list.html",
    "backend/portal/institute/src/main/resources/static/institute/pages/9-offcampus-jobs/job-list.html",
    # Placement
    "backend/portal/placement/src/main/resources/static/placement/dashboard.html",
    "backend/portal/placement/src/main/resources/static/placement/pages/1-drives/drive-list.html",
    "backend/portal/placement/src/main/resources/static/placement/pages/2-applications/my-applications.html",
    "backend/portal/placement/src/main/resources/static/placement/pages/2-applications/application-board.html",
    "backend/portal/placement/src/main/resources/static/placement/pages/3-stages/stage-management.html",
    "backend/portal/placement/src/main/resources/static/placement/pages/4-talks/talk-list.html",
    "backend/portal/placement/src/main/resources/static/placement/pages/5-live-updates/live-feed.html",
    "backend/portal/placement/src/main/resources/static/placement/pages/6-attendance/scan.html",
    "backend/portal/placement/src/main/resources/static/placement/pages/6-attendance/mark-attendance.html",
    "backend/portal/placement/src/main/resources/static/placement/pages/7-results/drive-results.html",
    "backend/portal/placement/src/main/resources/static/placement/pages/8-placed/placed-students.html",
    "backend/portal/placement/src/main/resources/static/placement/pages/10-feedback/my-feedback.html",
    "backend/portal/placement/src/main/resources/static/placement/pages/settings/connection.html"
]

for p in pages:
    if not os.path.exists(p):
        print(f"[MISSING FILE] {p}")
        continue
    with open(p, 'r', encoding='utf-8', errors='ignore') as f:
        html = f.read()

    # Check IDs
    has_sidebar = 'id="sidebar-container"' in html or "id='sidebar-container'" in html
    has_topbar = 'id="topbar-container"' in html or "id='topbar-container'" in html
    
    # Check script
    script_match = re.search(r'<script\s+type=[\'"]module[\'"]>(.*?)</script>', html, re.DOTALL)
    if not script_match:
        print(f"[NO MODULE SCRIPT] {p}")
        continue

    script_content = script_match.group(1)
    has_init = 'ui.initLayout(' in script_content
    has_auth_guard = 'auth.requireAuth(' in script_content or 'auth.requireAuth()' in script_content

    # Check for syntax or common issues in script
    imports = re.findall(r'from\s+[\'"]([^\'"]+)[\'"]', script_content)
    api_calls = re.findall(r'api\.(get|post|put|delete)\s*\(\s*[\'"`]([^\'"`]+)[\'"`]', script_content)

    print(f"\nPAGE: {os.path.basename(p)} ({'/'.join(p.split('/')[-3:])})")
    print(f"  Sidebar Container: {has_sidebar} | Topbar: {has_topbar} | initLayout: {has_init} | requireAuth: {has_auth_guard}")
    print(f"  Imports: {imports}")
    print(f"  API Calls: {api_calls}")

