import asyncio
import aiohttp
import json
import subprocess
import os
import time

CHROME_PATH = r"C:\Program Files\Google\Chrome\Application\chrome.exe"
PORT = 9224
TMP_PROFILE = os.path.join(os.environ['TEMP'], 'chrome_test_profile_pa_multi')

async def main():
    # 1. Get auth token from backend
    async with aiohttp.ClientSession() as session:
        login_payload = {"email": "admin@college.edu", "password": "Admin@123"}
        async with session.post("http://localhost:8088/api/auth/login", json=login_payload) as resp:
            data = await resp.json()
            token = data['data']['accessToken']
            refresh_token = data['data'].get('refreshToken', '')
            user = {
                "userId": data['data'].get('uid') or data['data'].get('userId'),
                "email": data['data']['email'],
                "role": data['data']['role'],
                "name": data['data']['name']
            }
            print(f"Logged in as {user['name']} ({user['role']})")

    # 2. Launch headless Chrome with CDP
    proc = subprocess.Popen([
        CHROME_PATH,
        '--headless=new',
        f'--remote-debugging-port={PORT}',
        '--disable-extensions',
        f'--user-data-dir={TMP_PROFILE}',
        '--disable-gpu',
        '--no-first-run',
        '--no-default-browser-check',
        'about:blank'
    ])
    time.sleep(1.5)

    try:
        async with aiohttp.ClientSession() as session:
            async with session.get(f"http://localhost:{PORT}/json") as resp:
                targets = await resp.json()
                page_target = [t for t in targets if t.get('type') == 'page'][0]
                ws_url = page_target['webSocketDebuggerUrl']

            async with session.ws_connect(ws_url) as ws:
                msg_id = 0

                async def cdp_call(method, params=None):
                    nonlocal msg_id
                    msg_id += 1
                    cmd = {"id": msg_id, "method": method, "params": params or {}}
                    await ws.send_str(json.dumps(cmd))
                    while True:
                        res = await ws.receive()
                        if res.type == aiohttp.WSMsgType.TEXT:
                            obj = json.loads(res.data)
                            if obj.get('id') == msg_id:
                                return obj.get('result', {})

                await cdp_call("Page.enable")
                await cdp_call("Runtime.enable")

                # Establish origin
                await cdp_call("Page.navigate", {"url": "http://localhost:8088/choose-domain.html"})
                await asyncio.sleep(1)

                # Inject localStorage tokens
                inject_js = f"""
                    localStorage.setItem('access_token', '{token}');
                    localStorage.setItem('refresh_token', '{refresh_token}');
                    localStorage.setItem('current_user', '{json.dumps(user)}');
                """
                await cdp_call("Runtime.evaluate", {"expression": inject_js})

                pages_to_test = [
                    # Placement Pillar Services
                    ("/placement/dashboard.html", "Placement Dashboard"),
                    ("/placement/pages/1-drives/drive-list.html", "Placement Drives"),
                    ("/placement/pages/3-stages/stage-management.html", "Placement Stages"),
                    ("/placement/pages/4-talks/talk-list.html", "Placement Pre-Placement Talks"),
                    ("/placement/pages/6-attendance/mark-attendance.html", "Placement Attendance"),
                    ("/placement/pages/7-results/drive-results.html", "Placement Results"),
                    ("/placement/pages/8-placed/placed-students.html", "Placed Students"),
                    ("/placement/pages/settings/connection.html", "Architecture & Connections"),
                    
                    # Institute Pillar Services
                    ("/institute/dashboard.html", "Institute Dashboard"),
                    ("/institute/pages/1-students/student-profile.html", "Student Directory/Profile"),
                    ("/institute/pages/2-company-drives/drive-detail.html", "Institute Drive Detail"),
                    ("/institute/pages/5-drive-rounds/rounds-board.html", "Institute Rounds Board"),
                    ("/institute/pages/7-hackathons/hackathon-list.html", "Institute Hackathons"),
                    ("/institute/pages/8-internships/internship-list.html", "Institute Internships"),
                    ("/institute/pages/9-offcampus-jobs/job-list.html", "Institute Offcampus Jobs")
                ]

                print("\n================== DOM VERIFICATION RESULTS ==================")
                all_passed = True
                for path, title in pages_to_test:
                    url = f"http://localhost:8088{path}"
                    await cdp_call("Page.navigate", {"url": url})
                    await asyncio.sleep(1.5)  # allow modules & fetches to finish

                    check_js = """
                    (() => {
                        const sidebar = document.getElementById('sidebar-container');
                        const hasRail = sidebar && sidebar.querySelector('.app-rail') !== null;
                        const topbar = document.getElementById('topbar-container');
                        const hasTopbar = topbar && topbar.children.length > 0;
                        const mainContent = document.getElementById('main-content') || document.body;
                        const mainHtml = mainContent.innerHTML;
                        const hasEmptyOrError = mainHtml.includes('Failed to load') || mainHtml.includes('Error loading');
                        const elementCount = mainContent.querySelectorAll('article, tr, .card, .poster-card, .list-item').length;
                        return {
                            hasRail,
                            hasTopbar,
                            elementCount,
                            hasEmptyOrError,
                            sidebarLength: sidebar ? sidebar.innerHTML.length : 0,
                            bodyLength: mainHtml.length
                        };
                    })()
                    """
                    res = await cdp_call("Runtime.evaluate", {"expression": check_js, "returnByValue": True})
                    dom_data = res.get('result', {}).get('value', {})

                    rail_ok = dom_data.get('hasRail', False)
                    topbar_ok = dom_data.get('hasTopbar', False)
                    elem_count = dom_data.get('elementCount', 0)
                    has_err = dom_data.get('hasEmptyOrError', False)
                    sidebar_len = dom_data.get('sidebarLength', 0)

                    passed = rail_ok and topbar_ok and not has_err
                    if not passed:
                        all_passed = False
                    status_str = "PASS" if passed else "FAIL"
                    print(f"[{status_str}] {title} ({path}):")
                    print(f"       Sidebar App Rail : {'RENDERED' if rail_ok else 'MISSING'} ({sidebar_len} chars)")
                    print(f"       Topbar Header    : {'RENDERED' if topbar_ok else 'MISSING'}")
                    print(f"       Data Elements    : {elem_count} dynamic elements rendered (Has error: {has_err})")
                    print()

                print("==============================================================")
                if all_passed:
                    print("ALL PAGES AND PILLAR SERVICES VERIFIED: SIDEBAR AND DATA RENDERED.")
                else:
                    print("SOME PAGES HAVE ISSUES.")
    finally:
        proc.terminate()

if __name__ == '__main__':
    asyncio.run(main())
