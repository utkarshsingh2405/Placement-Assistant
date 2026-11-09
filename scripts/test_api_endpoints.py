import urllib.request
import urllib.error
import json

BASE_URL = "http://localhost:8088"

# Login as admin
login_data = json.dumps({"email": "admin@college.edu", "password": "Admin@123"}).encode('utf-8')
req = urllib.request.Request(f"{BASE_URL}/api/auth/login", data=login_data, headers={"Content-Type": "application/json"})
with urllib.request.urlopen(req) as resp:
    res = json.loads(resp.read().decode('utf-8'))
    token = res['data']['accessToken']

endpoints = [
    # Placement
    "/placement/api/dashboard/stats",
    "/placement/api/applications/my",
    "/placement/api/drives/all",
    "/placement/api/drives",
    "/placement/api/talks",
    "/placement/api/live-updates/recent",
    "/placement/api/placed/highlights",
    "/placement/api/placed",
    "/placement/api/feedback/my",
    # Institute
    "/institute/api/students?size=200",
    "/institute/api/drives",
    "/institute/api/drives?size=50",
    "/institute/api/hackathons",
    "/institute/api/hackathons/bookmarked",
    "/institute/api/internships",
    "/institute/api/internships/bookmarked",
    "/institute/api/offcampus-jobs",
    "/institute/api/offcampus-jobs/bookmarked"
]

for ep in endpoints:
    url = f"{BASE_URL}{ep}"
    try:
        r = urllib.request.Request(url, headers={"Authorization": f"Bearer {token}"})
        with urllib.request.urlopen(r) as resp:
            data = resp.read()
            print(f"[OK 200] {ep} -> {len(data)} bytes")
    except urllib.error.HTTPError as e:
        body = e.read().decode('utf-8', errors='ignore')
        print(f"[FAIL {e.code}] {ep} -> {body[:150]}")
    except Exception as e:
        print(f"[ERROR] {ep} -> {e}")

