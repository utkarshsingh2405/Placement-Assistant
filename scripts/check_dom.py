import subprocess

chrome = r'C:\Program Files\Google\Chrome\Application\chrome.exe'
cmd = [chrome, '--headless=new', '--dump-dom', 'http://localhost:8088/placement/pages/1-drives/drive-list.html']
out = subprocess.check_output(cmd).decode('utf-8', errors='ignore')

idx = out.find('id="sidebar-container"')
if idx != -1:
    print("sidebar-container found:")
    print(out[idx-10:idx+250])
else:
    print("sidebar-container NOT found in DOM!")

