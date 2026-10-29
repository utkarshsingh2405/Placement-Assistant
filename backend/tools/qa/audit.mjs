import { chromium } from 'playwright';

const AUDIT_PAGES = [
  { name: 'Gateway Landing', url: 'http://localhost:8088/' },
  { name: 'Login Page', url: 'http://localhost:8088/login.html' },
  { name: 'Domain Chooser', url: 'http://localhost:8088/choose-domain.html' },
  { name: 'Institute Dashboard', url: 'http://localhost:8088/institute/dashboard.html' },
  { name: 'Placement Dashboard', url: 'http://localhost:8088/placement/dashboard.html' }
];

async function runAudit() {
  console.log('Starting Master Design System & Accessibility Audit...');
  const browser = await chromium.launch();
  let totalFailures = 0;

  for (const item of AUDIT_PAGES) {
    console.log(`\nAuditing: ${item.name} (${item.url})`);
    const page = await browser.newPage({ viewport: { width: 1440, height: 900 } });

    const errors = [];
    page.on('console', msg => {
      if (msg.type() === 'error') errors.push(msg.text());
    });
    page.on('pageerror', err => errors.push(err.message));

    await page.goto(item.url, { waitUntil: 'domcontentloaded' });
    await page.waitForTimeout(1000);

    // 1. Horizontal Overflow Check
    const hasOverflow = await page.evaluate(() => {
      return document.documentElement.scrollWidth > window.innerWidth;
    });
    if (hasOverflow) {
      console.log('  [FAIL] Horizontal overflow detected!');
      totalFailures++;
    } else {
      console.log('  [PASS] Zero horizontal overflow.');
    }

    // 2. Console Errors Check
    if (errors.length > 0) {
      console.log(`  [FAIL] ${errors.length} console error(s) logged:`, errors.slice(0, 3));
      totalFailures += errors.length;
    } else {
      console.log('  [PASS] Zero console errors.');
    }

    // 3. Emoji ban check
    const hasEmoji = await page.evaluate(() => {
      const emojiRegex = /[\u{1F300}-\u{1F5FF}\u{1F600}-\u{1F64F}\u{1F680}-\u{1F6FF}\u{1F700}-\u{1F77F}\u{1F780}-\u{1F7FF}\u{1F800}-\u{1F8FF}\u{1F900}-\u{1F9FF}\u{1FA00}-\u{1FA6F}\u{1FA70}-\u{1FAFF}\u{2600}-\u{26FF}\u{2700}-\u{27BF}]/u;
      return emojiRegex.test(document.body.innerText);
    });
    if (hasEmoji) {
      console.log('  [FAIL] Emoji detected in page content (Strictly Banned)!');
      totalFailures++;
    } else {
      console.log('  [PASS] Zero emoji detected (Phosphor Light icons used).');
    }

    // 4. Moving elements on Landing Page check
    if (item.name === 'Gateway Landing') {
      const movingCount = await page.evaluate(() => {
        // Elements with animation, transition, marquee, or observer reveals
        const animated = document.querySelectorAll('[data-reveal], .split-flap-board, .marquee-track, [style*="animation"], .btn-pill-nested');
        return animated.length;
      });
      if (movingCount >= 8) {
        console.log(`  [PASS] Moving elements on landing page: ${movingCount} (>= 8 required).`);
      } else {
        console.log(`  [FAIL] Only ${movingCount} moving elements found on landing page (< 8 required).`);
        totalFailures++;
      }
    }

    await page.close();
  }

  await browser.close();
  console.log(`\nAudit completed with ${totalFailures} total issue(s).`);
  process.exit(totalFailures > 0 ? 1 : 0);
}

runAudit().catch(err => {
  console.error(err);
  process.exit(1);
});
