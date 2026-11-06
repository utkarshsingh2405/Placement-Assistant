# Design Direction: Placement Assistance Platform

**Date:** October 2026  
**Document:** Direction & Strategic Blueprint (`docs/design/00-direction.md`)  
**Scope:** Whole Platform (Portal Gateway, Institute Pillar, Placement Pillar)

---

## 1. Subject Inventory

Unlike generic corporate SaaS products, a collegiate campus placement cycle possesses a physical and institutional vernacular. Every screen on this platform draws from real artefacts:

1. **The Notice Board outside the Placement Cell:** Wooden frame, pinned paper chits, handwritten updates, official rubber stamps, urgent brass flags, strike-through venue changes.
2. **The Hall Ticket / Admit Card:** Perforated edges, tear-off registration stubs, QR verification code, reporting gate times, dress codes, allowed stationery checklist, "Qualified" ink stamps.
3. **The Departure / Split-Flap Board:** The mechanical rhythm of a central train terminal or airport departure lounge; alphabet tiles flipping with weight as rounds conclude and company offers land.
4. **The Institute Ledger & Registrar's Registry:** Heavy paper surfaces, oxblood heraldic college crests, tabular rows, roll numbers, academic semesters, backlogs, percentage rings, defaulter lists.
5. **The Offer Letter with Debossed Seal:** Parchment-toned cardstock, circular brass debossed seal, CTC package in LPA, formal appointment declarations, joining dates.
6. **The Stage-by-Stage Journey Line:** Physical pilgrimage from Pre-Placement Talk (PPT) to Online Aptitude Test, Group Discussion / Technical Round, Managerial Interview, and HR Final.
7. **The Lanyard & Student ID Card:** Vertical laminated credential card with lanyard slot, high-contrast roll number, barcode, emergency contact, flip side for academic credentials and verified skill chips.
8. **Seminar Hall Doorway QR Scanner:** Live rotating verification QR code with circular validity countdown timer, door bouncer scan viewfinder, real-time participant counter.
9. **The Practice Flashcard Stack:** Physical 3x5 index cards in a brass wire box, front question prompt, tactile spacebar flip to solution with key formula highlights.
10. **The Indian Engineering Campus Context:** Tier-1/Tier-2 engineering colleges, NIRF metrics, branch designations (CSE, IT, ECE, EEE, MECH, CIVIL), CTC ranges quoted in Lakhs Per Annum (LPA), bilingual official notices.
11. **The Company Job Description Dossier:** Physical ring-bound dossier with stipend breakdown, bond clauses, eligibility cutoffs (CGPA >= 7.5, max backlogs = 0), interview timeline.

---

## 2. The Argument

1. **Round by round, you always know what is next.** (Visualized via the animated Journey Line with station traveller dot).
2. **Every stage tells you the venue and the test before it happens.** (Rendered as a perforated Hall Ticket with tear-off reporting stub).
3. **Two desks, one login.** (The two-door domain architecture: oxblood Institute ledger + pine Placement season board).
4. **This season, in real numbers.** (Live stats band fed by live backend APIs without vanity figures).
5. **Check your eligibility before you apply.** (Interactive browser-side Eligibility Checker).
6. **Notices land here first.** (Masonry Notice Board with dropped-in chits and SSE live indicators).
7. **Internships, hackathons, and off-campus opportunities in one ledger.** (Listing stubs with live countdown chips).
8. **Engineered for the people who run the season.** (Real operational UI fragments for TPOs, Directors, Recruiters, and Students).

---

## 3. The Idea Sentence

> **On this platform, the physical printed artefacts of a collegiate placement season—the perforated hall ticket, the pinned notice board chits, the mechanical split-flap departure board, the registrar's ledger, and the debossed brass offer seal—become the interface itself.**

---

## 4. The Live Thing & Signature Move

- **The Live Thing (Public Landing):** **The Interactive Eligibility Checker.** Visitors and students set their branch, CGPA slider, and active backlogs. It instantly filters live upcoming drives from `/placement/api/public/upcoming-drives` entirely in client-side memory, highlighting eligible positions and specifying exact unmet criteria for the rest.
- **The Signature Move:** **The 3D Split-Flap Placement Board.** Mechanical flipping letterboard driven by live highlights from `/placement/api/public/highlights`. Characters rotate on a 3D horizontal axis with physical spring easing, showing company name, designation, and package.

---

## 5. Dials & Variance

| Surface Context | DESIGN_VARIANCE | MOTION | DENSITY | Typographic Hierarchy |
|---|---|---|---|---|
| **Public Portal (Landing, Auth, Chooser)** | `8` | `7` | `3` | Instrument Serif Display + Geist text |
| **App Interiors (Dashboards, Drives, Feed)** | `5` | `5` | `6` | Geist 600 Headings + Geist 400/500 text |
| **Data Ledgers (TPO & Director Tables, Audit)**| `4` | `3` | `7` | Geist with `tabular-nums`, high data density |

---

## 6. Typography & Color Palette

### Typography
- **Public Headlines / Editorial Display:** `Instrument Serif` (Google Fonts + local woff2), regular, `clamp()` fluid sizing.
- **All Body, UI Controls, Data, and App Interiors:** `Geist` (Google Fonts + local woff2: 400, 500, 600) with `font-variant-numeric: tabular-nums`.
- **Hindi / Vernacular Fallback:** `Noto Sans Devanagari` (400, 500).
- **Prohibited Fonts:** Inter, Roboto, Arial, system fonts, and decorative novelty typefaces.

### Color Tokens

```css
/* Ground & Surfaces */
--ground: #F3F4F0;         /* Chalk paper ground in light mode */
--surface: #FBFBF8;        /* Clean document surface */
--surface-2: #E9ECE6;      /* Tone step: shells, inputs, table stripes */
--surface-3: #DFE3DB;      /* Deeper tone step: active controls, borders */
--ink: #101820;            /* Rich dark ink (never pure black #000) */
--ink-2: #3B4752;          /* Secondary narrative text */
--muted: #5E6B75;          /* Metadata, hints, footnotes (WCAG AA compliant) */
--night: #0C1214;          /* Deep nocturnal ground (hero, stats band, dark mode) */

/* Domain Identities */
--institute: #8C2433;      /* Heraldic Oxblood (Academic Ledger) */
--institute-hover: #721C29;
--institute-tint: #F4E3E5;

--placement: #0E6B5C;      /* Deep Pine (Recruitment Season) */
--placement-hover: #0A5448;
--placement-tint: #DDF1EC;

--brass: #B8892B;          /* Polished Brass (Offer seal, primary CTA, badges) */
--brass-light: #E0B552;

/* Status Semantics */
--success: #2E7D4F;
--warning: #B45309;
--danger: #E5484D;
--info: #1E6F9F;
```

---

## 7. Surviving Element IDs and Data Hooks

The following existing element IDs, form names, and data attributes are wired to real backend endpoints and MUST survive completely in the new markup:

### Authentication & Portal
- `#login-form`, `#email`, `#password`, `#remember-me`, `#submit-btn`, `#btn-text`, `#btn-spinner`, `#alert-container`
- `#toggle-password`, `#password-eye`
- `.demo-chip[data-email][data-pass]`
- `#theme-btn`, `#theme-icon`
- `#user-display-name`, `#user-avatar`, `#user-dropdown-name`, `#user-dropdown-email`, `#user-dropdown-role`
- `#admin-link-li`, `#logout-btn`
- `#card-institute`, `#card-placement`

### Domain Switcher & Topbar
- `#domain-switcher-container`, `#domain-switcher-pill`, `#domain-switcher-dropdown`
- `.domain-item[data-target-domain="institute"]`, `.domain-item[data-target-domain="placement"]`, `.domain-item[data-target-domain="portal"]`
- `#domain-switch-account-btn`, `#domain-global-alert-dot`, `#placement-unread-dot`, `#institute-unread-dot`
- `#topbar-bell`, `#unread-notification-count`, `#sidebar-toggle`

### Institute Module
- `student-list.html`: `#search-input`, `#branch-filter`, `#year-filter`, `#eligibility-filter`, `#students-table-body`, `#students-count-badge`, `#export-btn`, `#add-student-btn`
- `student-form.html`: `#student-form`, `#name`, `#rollNo`, `#email`, `#phone`, `#branch`, `#year`, `#cgpa`, `#backlogs`, `#placementEligible`, `#save-btn`
- `2-company-drives`: `#drives-grid`, `#search-drive`, `#status-filter`
- `3-question-bank`: `#questions-container`, `#company-filter`, `#practice-mode-btn`
- `4-attendance`: `#crt-attendance-table`, `#class-attendance-table`, `#mark-attendance-btn`, `#defaulters-list`
- `6-resume-ats`: `#resume-dropzone`, `#ats-gauge`, `#ats-score-display`, `#ats-suggestions`

### Placement Module
- `1-drives/drive-list.html`: `#drives-container`, `#search-input`, `#status-filter`, `#filter-all`, `#filter-active`
- `2-applications`: `#applications-table`, `#status-filter`, `#withdraw-btn`
- `3-stages`: `#stages-timeline`, `#hall-ticket-container`
- `5-live-updates`: `#notices-feed`, `#notice-type-filter`, `#on-air-indicator`, `#new-update-form`
- `6-attendance`: `#qr-code-canvas`, `#countdown-timer`, `#present-count`, `#scan-video`
- `7-results`: `#results-tabs`, `#selected-table`, `#not-selected-table`, `#upload-results-btn`, `#results-upload-stepper`
- `8-placed`: `#flap-board-container`, `#placed-grid`, `#salary-stat`, `#placed-count-stat`

---

## 8. Page-by-Page Migration Plan

| Module | Existing Page | Redesigned Experience |
|---|---|---|
| **Portal** | `index.html` | Editorial split hero with 3D Split-Flap board, pinned GSAP Journey route, two-door cards, live stats band, client-side Eligibility Checker, masonry notice board, kinetic opportunities marquee, asymmetric TPO/Director bento. |
| **Portal** | `login.html`, `register.html`, `forgot-password.html` | Split nocturnal/chalk layout. Left: flat illustrated notice board scene + quote. Right: clean form with large labels, password visibility toggle, accessible error alerts, quick demo role chips. |
| **Portal** | `choose-domain.html` | Two huge full-height tactile doors (oxblood Institute / pine Placement). Live user statistics per half. Animated door expansion on hover/selection with View Transitions. |
| **Portal** | `unavailable.html`, `offline.html` | Calm, non-generic illustrated offline / service unavailable pages with retry buttons. |
| **Institute** | `dashboard.html` | Asymmetric bento: active drives timeline, attendance dial, ATS distribution bar, upcoming round schedules. |
| **Institute** | `1-students/*` | Registration as high-focus multi-step questionnaire; directory as Ledger Table with initial avatars and inline status pills; student profile as an ID card on a lanyard (flippable front/back). |
| **Institute** | `2-company-drives/*` | Poster cards with large company typographic mark, salary badges, deadline countdowns, tabbed drive dossiers. |
| **Institute** | `3-question-bank/*` | Practice Mode as tactile index flashcard deck with spacebar reveal and keyboard navigation. |
| **Institute** | `4-attendance/*` | CRT and Class attendance register with tactile toggle cells, radial percentage indicators, brass-flagged defaulter warnings. |
| **Institute** | `5-drive-rounds/*` | Funnel-stage horizontal round board with candidate count flows. |
| **Institute** | `6-resume-ats/*` | Tactile paper dropzone, SVG animated half-circle ATS gauge dial, 4-bar diagnostic breakdown. |
| **Institute** | `7-9-opportunities/*`| Perforated listing stubs with mode tags (ONLINE, OFFLINE, HYBRID) and expandable instructions drawers. |
| **Placement**| `dashboard.html` | Role-specific commanding view: Next-Up Hall Ticket leading student view; Today Board leading TPO/Director view; Stage Pipeline leading recruiter view. |
| **Placement**| `1-drives/*` | Recruitment drive cards with eligibility chips and multi-tab detailed view (Overview, Criteria, JD, Process, Stages, Updates). |
| **Placement**| `3-stages/*` | Visual Journey Line station route + perforated Hall Ticket with tear-off reporting stub and "Qualified" stamp. |
| **Placement**| `5-live-updates/*` | Pinned chits Notice Board with live SSE drop-in animation, struck-through venue updates, and live composer. |
| **Placement**| `6-attendance/*` | High-precision rotating QR display with circular SVG timer ring, live check-in ticker, and camera viewfinder scanner. |
| **Placement**| `7-results/*` | Ledger table with dual-mode protection (view-only for students, 4-step upload stepper & inline editor for TPO/Director). |
| **Placement**| `8-placed/*` | Mechanical Split-Flap board, student card grid with brass debossed offer seal stamps. |
| **Placement**| `10-feedback/*` | Star-rating questionnaires and SVG response distribution analytics. |
| **Placement**| `11-notifications/*`| Centralized alerts registry with channel toggles and browser push test trigger. |
