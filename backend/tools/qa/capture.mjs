import { chromium } from 'playwright';
import fs from 'node:fs';
import path from 'node:path';

const OUTPUT_DIR = './qa-output';
if (!fs.existsSync(OUTPUT_DIR)) {
  fs.mkdirSync(OUTPUT_DIR, { recursive: true });
}

const VIEWPORTS = [
  { name: 'desktop', width: 1440, height: 900 },
  { name: 'tablet', width: 768, height: 1024 },
  { name: 'mobile', width: 390, height: 844 }
];

const PAGES = [
  { name: 'landing', url: 'http://localhost:8088/' },
  { name: 'login', url: 'http://localhost:8088/login.html' },
  { name: 'choose-domain', url: 'http://localhost:8088/choose-domain.html' },
  { name: 'institute-dashboard', url: 'http://localhost:8088/institute/dashboard.html' },
  { name: 'institute-students', url: 'http://localhost:8088/institute/pages/1-students/student-list.html' },
  { name: 'institute-drives', url: 'http://localhost:8088/institute/pages/2-company-drives/drive-list.html' },
  { name: 'institute-questions', url: 'http://localhost:8088/institute/pages/3-question-bank/question-list.html' },
  { name: 'institute-crt', url: 'http://localhost:8088/institute/pages/4-attendance/crt/crt-report.html' },
  { name: 'placement-dashboard', url: 'http://localhost:8088/placement/dashboard.html' },
  { name: 'placement-drives', url: 'http://localhost:8088/placement/pages/1-drives/drive-list.html' },
  { name: 'placement-stages', url: 'http://localhost:8088/placement/pages/3-stages/stage-management.html' },
  { name: 'placement-live-feed', url: 'http://localhost:8088/placement/pages/5-live-updates/live-feed.html' },
  { name: 'placement-attendance', url: 'http://localhost:8088/placement/pages/6-attendance/mark-attendance.html' },
  { name: 'placement-results', url: 'http://localhost:8088/placement/pages/7-results/drive-results.html' },
  { name: 'placement-placed', url: 'http://localhost:8088/placement/pages/8-placed/placed-students.html' }
];

async function run() {
  console.log('Launching browser to capture responsive screenshots...');
  const browser = await chromium.launch();

  for (const vp of VIEWPORTS) {
    console.log(`\nCapturing viewports at ${vp.name} (${vp.width}x${vp.height})...`);
    const context = await browser.newContext({
      viewport: { width: vp.width, height: vp.height },
      deviceScaleFactor: 1
    });
    const page = await context.newPage();

    for (const p of PAGES) {
      try {
        await page.goto(p.url, { waitUntil: 'networkidle', timeout: 8000 });
        const filePath = path.join(OUTPUT_DIR, `${p.name}_${vp.name}.png`);
        await page.screenshot({ path: filePath, fullPage: true });
        console.log(`  ✓ ${p.name} [${vp.name}] -> ${filePath}`);
      } catch (err) {
        console.log(`  ✗ ${p.name} [${vp.name}] failed: ${err.message}`);
      }
    }
    await context.close();
  }

  await browser.close();
  console.log('\nAll captures completed. Files stored in qa-output/');
}

run().catch(console.error);
