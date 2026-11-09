import asyncio, aiohttp, subprocess, json, time, os

async def test():
    # Login
    async with aiohttp.ClientSession() as s:
        async with s.post('http://localhost:8088/api/auth/login', json={'email':'admin@college.edu','password':'Admin@123'}) as r:
            data = await r.json()
            token = data['data']['accessToken']
            user = {'userId': data['data']['uid'], 'email': data['data']['email'], 'role': data['data']['role'], 'name': data['data']['name']}
    
    tmp_prof = os.path.join(os.environ['TEMP'], 'chrome_debug_prof')
    proc = subprocess.Popen([
        r'C:\Program Files\Google\Chrome\Application\chrome.exe',
        '--headless=new',
        '--remote-debugging-port=9223',
        '--disable-extensions',
        f'--user-data-dir={tmp_prof}',
        'about:blank'
    ])
    time.sleep(1.5)
    try:
        async with aiohttp.ClientSession() as s:
            async with s.get('http://localhost:9223/json') as r:
                targets = await r.json()
                page_target = [t for t in targets if t.get('type') == 'page'][0]
                ws_url = page_target['webSocketDebuggerUrl']
            async with s.ws_connect(ws_url) as ws:
                msg_id = 0
                async def cdp(m, p=None):
                    nonlocal msg_id
                    msg_id += 1
                    await ws.send_str(json.dumps({'id': msg_id, 'method': m, 'params': p or {}}))
                    while True:
                        msg = await ws.receive()
                        data = json.loads(msg.data)
                        if data.get('id') == msg_id: return data.get('result', {})
                
                await cdp('Page.enable')
                await cdp('Runtime.enable')

                await cdp('Page.navigate', {'url': 'http://localhost:8088/choose-domain.html'})
                await asyncio.sleep(1)
                
                eval_script = f"""
                    localStorage.setItem('access_token', '{token}');
                    localStorage.setItem('current_user', '{json.dumps(user)}');
                """
                await cdp('Runtime.evaluate', {'expression': eval_script})

                await cdp('Page.navigate', {'url': 'http://localhost:8088/institute/pages/1-students/student-profile.html'})
                await asyncio.sleep(2)
                
                url_res = await cdp('Runtime.evaluate', {'expression': 'window.location.href', 'returnByValue': True})
                print('Current URL:', url_res.get('result', {}).get('value'))

                sb_res = await cdp('Runtime.evaluate', {
                    'expression': 'document.getElementById("sidebar-container") ? document.getElementById("sidebar-container").innerHTML : "NO_ELEMENT"',
                    'returnByValue': True
                })
                sb_html = sb_res.get('result', {}).get('value')
                print('Sidebar HTML length:', len(sb_html) if sb_html else 0)
                if sb_html and len(sb_html) > 50:
                    print('Sidebar start:', sb_html[:200])

                doc_res = await cdp('Runtime.evaluate', {
                    'expression': 'document.documentElement.outerHTML.slice(0, 500)',
                    'returnByValue': True
                })
                print('Document start:', doc_res.get('result', {}).get('value'))
    finally:
        proc.terminate()

asyncio.run(test())
