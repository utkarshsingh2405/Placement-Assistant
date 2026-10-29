# Placement Assistance Frontend QA Harness & Audit Suite

This test harness evaluates the entire Placement Assistance platform across the Portal, Academic Institute, and Placement domains against the agency-level visual design and accessibility standards.

## Prerequisites
- Node.js v20+ or v22
- Playwright and axe-core:
  ```bash
  npm i -D playwright @axe-core/playwright
  npx playwright install chromium
  ```

## Running the Harness
1. Ensure the backend services are running:
   - Portal Gateway: `http://localhost:8088`
   - Academic Institute Pillar: `http://localhost:8081`
   - Placement Pillar: `http://localhost:8082`

2. **Capture Full-Viewport Screenshots across Roles & Viewports**:
   ```bash
   node tools/qa/capture.mjs
   ```
   Screenshots are captured at:
   - Desktop: `1440x900`
   - Tablet: `768x1024`
   - Mobile: `390x844`
   Saved into `qa-output/`.

3. **Run Automated Visual & Accessibility Audit**:
   ```bash
   node tools/qa/audit.mjs
   ```
   Checks for:
   - Zero horizontal overflow
   - Font compliance (only Instrument Serif and Geist loaded)
   - Headings that fit within 2 lines
   - Minimum 8 moving elements on landing page
   - Zero axe-core accessibility violations
   - Zero console errors or unhandled rejections
   - Visible focus indicators
   - Absence of unapproved shadows or gradients on public pages at rest
   - Mobile touch targets ≥ 44px
   - Dual-mode ledger isolation (edit controls never rendered for unauthorized roles)
