import os
import re

root_dirs = [
    'backend/portal/institute/src/main/resources/static/institute',
    'backend/portal/placement/src/main/resources/static/placement'
]

html_files = []
for rd in root_dirs:
    for root, dirs, files in os.walk(rd):
        for f in files:
            if f.endswith('.html'):
                html_files.append(os.path.join(root, f))

print(f"Total HTML files found: {len(html_files)}")

all_imports = set()
all_scripts = set()
all_css = set()
page_data = {}

for hf in sorted(html_files):
    with open(hf, 'r', encoding='utf-8', errors='ignore') as f:
        content = f.read()

    rel = hf.replace('\\', '/')
    imports = re.findall(r'from\s+[\'"]([^\'"]+)[\'"]', content)
    scripts = re.findall(r'<script\s+[^>]*src=[\'"]([^\'"]+)[\'"]', content)
    css = re.findall(r'<link\s+[^>]*href=[\'"]([^\'"]+\.css[^\'"]*)[\'"]', content)
    has_sidebar_container = 'sidebar-container' in content
    has_init_layout = 'initLayout' in content
    has_render_rail = 'renderRail' in content

    for imp in imports:
        all_imports.add(imp)
    for s in scripts:
        all_scripts.add(s)
    for c in css:
        all_css.add(c)

    page_data[rel] = {
        'imports': imports,
        'scripts': scripts,
        'css': css,
        'has_sidebar_container': has_sidebar_container,
        'has_init_layout': has_init_layout
    }

print("\n=== ALL JS IMPORTS ACROSS PAGES ===")
for imp in sorted(all_imports):
    print("  ", imp)

print("\n=== ALL SCRIPT SRCS ===")
for s in sorted(all_scripts):
    print("  ", s)

print("\n=== PAGES MISSING sidebar-container ===")
for p, data in page_data.items():
    if not data['has_sidebar_container']:
        print("  NO sidebar-container:", p)

print("\n=== PAGES WITH sidebar-container BUT MISSING initLayout ===")
for p, data in page_data.items():
    if data['has_sidebar_container'] and not data['has_init_layout']:
        print("  NO initLayout:", p)

