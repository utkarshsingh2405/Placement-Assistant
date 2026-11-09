import os
import re
import urllib.request
import urllib.error
import json

BASE_URL = "http://localhost:8088"

# 1. Login to get token
login_data = json.dumps({"email": "admin@college.edu", "password": "Admin@123"}).encode('utf-8')
req = urllib.request.Request(f"{BASE_URL}/api/auth/login", data=login_data, headers={"Content-Type": "application/json"})
with urllib.request.urlopen(req) as resp:
    res = json.loads(resp.read().decode('utf-8'))
    token = res['data']['accessToken']
print("Auth token acquired successfully.")

# Find all HTML files
root_dirs = [
    ('institute', 'backend/portal/institute/src/main/resources/static/institute'),
    ('placement', 'backend/portal/placement/src/main/resources/static/placement')
]

pages_to_test = []
for domain, rdir in root_dirs:
    for root, dirs, files in os.walk(rdir):
        for f in files:
            if f.endswith('.html'):
                full_path = os.path.join(root, f)
                rel_path = os.path.relpath(full_path, rdir).replace('\\', '/')
                url_path = f"/{domain}/{rel_path}"
                pages_to_test.append((domain, full_path, url_path))

print(f"Testing {len(pages_to_test)} pages...")

failed_pages = []
asset_cache = {}

def check_asset(url):
    if url in asset_cache:
        return asset_cache[url]
    try:
        req = urllib.request.Request(url, headers={"Authorization": f"Bearer {token}"})
        with urllib.request.urlopen(req) as resp:
            status = resp.status
            content = resp.read()
            asset_cache[url] = (status, content)
            return status, content
    except urllib.error.HTTPError as e:
        asset_cache[url] = (e.code, b'')
        return e.code, b''
    except Exception as e:
        asset_cache[url] = (500, str(e).encode())
        return 500, str(e).encode()

for domain, local_path, url_path in sorted(pages_to_test):
    page_status, page_content = check_asset(f"{BASE_URL}{url_path}")
    if page_status != 200:
        print(f"[FAIL PAGE] {url_path} -> HTTP {page_status}")
        failed_pages.append((url_path, f"Page returned {page_status}"))
        continue

    text = page_content.decode('utf-8', errors='ignore')
    
    # Check CSS links
    css_links = re.findall(r'<link\s+[^>]*href=[\'"]([^\'"]+\.css[^\'"]*)[\'"]', text)
    missing_assets = []
    for css in css_links:
        if css.startswith('http'):
            continue
        c_url = f"{BASE_URL}{css}" if css.startswith('/') else f"{BASE_URL}/{domain}/{css}"
        st, _ = check_asset(c_url)
        if st != 200:
            missing_assets.append(f"CSS {css} -> {st}")

    # Check JS imports
    imports = re.findall(r'from\s+[\'"]([^\'"]+)[\'"]', text)
    for imp in imports:
        if imp.startswith('http'):
            continue
        # In browser, relative imports in /placement/pages/... resolve relative to page path
        if imp.startswith('/'):
            j_url = f"{BASE_URL}{imp}"
        elif imp.startswith('./') or imp.startswith('../'):
            dir_part = os.path.dirname(url_path)
            # resolve path
            j_url = f"{BASE_URL}" + os.path.normpath(os.path.join(dir_part, imp)).replace('\\', '/')
        else:
            j_url = f"{BASE_URL}/{imp}"

        st, j_content = check_asset(j_url)
        if st != 200:
            missing_assets.append(f"JS import {imp} ({j_url}) -> {st}")

    # Check script src tags
    script_srcs = re.findall(r'<script\s+[^>]*src=[\'"]([^\'"]+)[\'"]', text)
    for s in script_srcs:
        if s.startswith('http'):
            continue
        s_url = f"{BASE_URL}{s}" if s.startswith('/') else f"{BASE_URL}/{domain}/{s}"
        st, _ = check_asset(s_url)
        if st != 200:
            missing_assets.append(f"Script src {s} -> {st}")

    # Check API calls inside page
    api_calls = re.findall(r'api\.(?:get|post|put|delete)\s*\(\s*[\'"`]([^\'"`]+)[\'"`]', text)
    api_issues = []
    for ep in api_calls:
        # Resolve how api.buildUrl works
        if ep.startswith('http'):
            continue
        # If ep starts with /
        clean = ep if ep.startswith('/') else f"/{ep}"
        if clean.startswith('/institute') or clean.startswith('/placement'):
            ep_url = f"{BASE_URL}{clean}"
        elif clean.startswith('/api/auth') or clean.startswith('/auth') or clean.startswith('/api/status') or clean.startswith('/api/admin'):
            ep_url = f"{BASE_URL}{clean}"
        elif clean.startswith('/api/'):
            ep_url = f"{BASE_URL}/{domain}{clean}"
        else:
            ep_url = f"{BASE_URL}/{domain}/api{clean}"

        # Test GET calls only
        if 'api.get' in text:
            # only test if ep is in a get call
            m = re.search(r'api\.get\s*\(\s*[\'"`]' + re.escape(ep) + r'[\'"`]', text)
            if m and not '${' in ep and not '?' in ep:
                st, resp_body = check_asset(ep_url)
                if st not in (200, 201):
                    api_issues.append(f"API GET {ep} ({ep_url}) -> {st}")

    if missing_assets or api_issues:
        print(f"\n[PAGE ISSUE] {url_path}:")
        for ma in missing_assets:
            print(f"   Missing Asset: {ma}")
        for ai in api_issues:
            print(f"   API Issue: {ai}")
        failed_pages.append((url_path, missing_assets + api_issues))

print(f"\nAudit complete. {len(failed_pages)} pages had asset or API issues out of {len(pages_to_test)}.")

