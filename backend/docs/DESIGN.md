# Placement Assistance Platform: Design System Specification

**Version:** 2.0 (Authoritative Design System)  
**Standard:** Google Stitch 7-Section Architecture (`docs/DESIGN.md`)  
**Scope:** Whole Platform (Portal, Institute, Placement)

---

## 1. Visual Theme and Atmosphere

The Placement Assistance platform rejects the homogenization of software templates. A university placement cycle is a rite of passage with physical gravitas: notice boards pinned with urgent circulars, perforated admit tickets with reporting slots, departure boards clattering as offers roll in, heraldic institute ledgers, and formal offer letters stamped with brass seals.

The atmosphere is tactile, disciplined, and typographic. Surfaces are rendered as distinct tone steps reminiscent of archival paper and night boards. There are no synthetic neon gradients, no purple backdrops, and no floating pill navbars with glassmorphism blur. 

Depth is constructed through **tonal double-bezels** (an outer casing in a subdued tone step with generous 32px radii, cradling a concentric 26px inner core), authentic paper micro-grain overlays, and purposeful motion physics. The platform houses two interconnected domains:
- **The Institute Pillar:** The Registrar's academic ledger. Characterized by oxblood heraldic accents (`#8C2433`), calm paper surfaces, high data-density ledger tables, and student credentials.
- **The Placement Pillar:** The Live Season board. Characterized by deep pine accents (`#0E6B5C`), real-time split-flap boards, perforated hall tickets, and countdown timers.
- **The Portal Gateway:** The central crossroads. Framed in nocturnal depth (`#0C1214`) with polished brass CTAs (`#B8892B`).

---

## 2. Color Palette and Roles

The system uses a calibrated neutral spectrum paired with singular, authoritative domain accents. Pure black (`#000000`) is prohibited. All color pairs are tested and verified for WCAG 2.2 AA contrast compliance.

| Token | Light Value | Dark Value | Semantic Role |
|---|---|---|---|
| `--ground` | `#F3F4F0` | `#0C1214` | Atmospheric page background (chalk paper / night canvas) |
| `--surface` | `#FBFBF8` | `#121C1F` | Primary panel, modal, and table background |
| `--surface-2` | `#E9ECE6` | `#182529` | Outer tone step for double-bezels, table striping, input fields |
| `--surface-3` | `#DFE3DB` | `#1F3036` | Deeper tone step, pressed controls, subtle structural borders |
| `--ink` | `#101820` | `#E8EEEC` | Primary editorial and body typography (never washed out) |
| `--ink-2` | `#3B4752` | `#B9C6C9` | Secondary descriptions and supporting sentences |
| `--muted` | `#5E6B75` | `#8FA0A5` | Metadata, captions, timestamps, column headers |
| `--night` | `#0C1214` | `#0C1214` | Permanent deep nocturnal tone (hero canvas, stats band, footer) |
| `--institute` | `#8C2433` | `#E0808C` | Institute Academic Accent (heraldic oxblood) |
| `--institute-hover` | `#721C29` | `#F09CA6` | Hover state for institute interactive controls |
| `--institute-tint` | `#F4E3E5` | `#2A1418` | Soft tint background for institute badges and highlights |
| `--placement` | `#0E6B5C` | `#4FD1B8` | Placement Recruitment Accent (deep pine) |
| `--placement-hover` | `#0A5448` | `#6EE7CF` | Hover state for placement interactive controls |
| `--placement-tint` | `#DDF1EC` | `#0B2A26` | Soft tint background for placement badges and highlights |
| `--brass` | `#B8892B` | `#E0B552` | Portal signature accent, Offer Seal, primary landing CTA |
| `--brass-tint` | `#FAF3E3` | `#2A2210` | Urgent notice highlight and seal background |
| `--success` | `#2E7D4F` | `#5CC389` | Verified statuses, passed rounds, selected candidates |
| `--warning` | `#B45309` | `#F0A04B` | Urgent notices, approaching deadlines (< 3 days) |
| `--danger` | `#E5484D` | `#FF7A7F` | Defaulters, rejected statuses, destructive actions |
| `--info` | `#1E6F9F` | `#6BB6E0` | Informational announcements, venue updates |

### Accent Dynamic Resolution
The variable `--accent` automatically resolves based on the current context:
- `data-domain="portal"` -> resolves to `--brass`
- `data-domain="institute"` -> resolves to `--institute`
- `data-domain="placement"` -> resolves to `--placement`

---

## 3. Typography Rules

### Font Families
- **Display Typography (Public Surfaces Only):** `Instrument Serif`, regular weight only. Used for high-impact headlines on the landing page, auth splash views, and domain chooser titles. Never use italic accent words.
- **Editorial Body, Data, App Interiors, and Controls:** `Geist` (400 Regular, 500 Medium, 600 Semi-Bold). Used universally across app interiors, dashboards, cards, and tables.
- **Tabular Figures:** Numbers and numerical data must always use `font-variant-numeric: tabular-nums` to ensure perfect column alignment.
- **Vernacular Fallback:** `Noto Sans Devanagari` (400, 500) for bilingual campus notices.
- **Prohibited:** Inter, Roboto, Arial, System fonts, Comic Sans, decorative scripts.

### Fluid Scale with clamp()
Typography scales continuously without step-jump disorientation:
- `display-xl`: `clamp(3.25rem, 8.5vw, 8.5rem)` at line-height `0.92`, letter-spacing `-0.035em`
- `display-lg`: `clamp(2.5rem, 5.5vw, 5.25rem)` at line-height `0.96`, letter-spacing `-0.025em`
- `h1`: `clamp(2.0rem, 3.8vw, 3.5rem)` at line-height `1.05`, letter-spacing `-0.02em`
- `h2`: `clamp(1.5rem, 2.6vw, 2.25rem)` at line-height `1.15`, letter-spacing `-0.015em`
- `h3`: `clamp(1.2rem, 1.8vw, 1.5rem)` at line-height `1.25`
- `body`: `clamp(1rem, 1.1vw, 1.125rem)` at line-height `1.55` (45–75 character measure)
- `small`: `0.875rem` (14px) at line-height `1.4` (minimum allowed text size)

### Editorial Headline Constraints
- Every headline must fit within **two lines maximum** across all breakpoints (1440px, 768px, 390px).
- Banned: Eyebrow badges above public headlines, tracked-out micro-uppercase labels, and cliché corporate marketing jargon.

---

## 4. Component Stylings

### A. Tonal Double-Bezel Containers
Signature cards and structural dashboards use a double-bezel construction instead of hairline border outlines:
- **Outer Shell:** Background `--surface-2`, 6px internal padding, `border-radius: 32px`.
- **Inner Core:** Background `--surface`, `border-radius: 26px`, internal padding `24px` to `36px`.

### B. Nested "Button-in-Button" CTA
The single primary call-to-action per page uses a solid pill containing a circular inset button:
- Pill container in `--brass` with dark `--ink` text.
- Circular right-hand inset containing a Phosphor Light arrow (`<svg><use href="#ph-arrow-up-right"/></svg>`).
- On hover, the circular arrow translates `translate(4px, -1px)` with spring physics while the outer pill scales to `1.01`. On active click, scales to `0.98`.

### C. Perforated Hall Ticket (Admit Card)
Used for recruitment stages and student hall tickets:
- Border mask with semi-circular cut-outs along the perforation seam.
- Left ticket body: Company mark, stage name, reporting date & gate closure time, venue room number, test syllabus.
- Right tear-off stub: Student roll number, reporting QR code, seat allotment number.
- State Stamp: When qualified, a rubber "QUALIFIED" stamp slams down rotated `-12deg` with spring overshoot.

### D. The Split-Flap Placement Board
Departure-board mechanical display:
- Dual-leaf 3D rotating cards with horizontal seam and subtle shadow ridge.
- Character flips driven by real-time live highlight events.
- Pauses when browser tab is inactive (`document.hidden`); settles instantly under `prefers-reduced-motion`.

### E. Pinned Notice Board
Campus notice board with physical chits:
- Asymmetric columns of pinned notices with subtle random rotation (+/- 1.2deg on desktop, 0deg on mobile).
- Brass pin dot at top center.
- Struck-through text with highlighted correction when venue or timing changes.
- Live "ON AIR" pulsing indicator connected to Server-Sent Events (SSE).

### F. Dual-Mode Ledger Table
Institute student directories and placement results:
- Sticky header with tone-step contrast.
- Alternating subtle tone rows, no vertical column lines.
- Tabular figures on all numbers, percentages, and CGPAs.
- Two distinct modes:
  - **View-Only Mode:** Quiet lock icon, "View Only" indicator.
  - **Editing Mode (TPO / Director Only):** Prominent indicator bar, pending unsaved count, inline cell editor, publish confirmation dialog.

### G. Domain Switcher
Segmented pill control in top navigation:
- Sliding tactile thumb using FLIP transform calculations.
- Direct indicator of current pillar accent.
- Unread indicator dot when the other domain has pending updates.
- Keyboard accessible via `Alt+D`.
- Triggers circular door clip-path View Transition between domains.

---

## 5. Layout Principles

- **Breakpoints:** Mobile-first architecture: `480px`, `768px`, `1024px`, `1280px`, `1536px`.
- **One Dominant Element:** Every viewport must be anchored by one visually dominant component (e.g., the Split-Flap board, the Hall Ticket, or the Ledger Table). Never arrange equal-weight 3-card grids.
- **Asymmetric Grids:** Bento compositions using `grid-template-columns: 1.6fr 1fr` or `2fr 1fr 1fr`.
- **Mobile Collapse:** Below `768px`, all asymmetric grids collapse to a clean single column with `16px` outer gutters, full-bleed touch targets (min `44px`), and bottom sheets instead of modal windows.
- **Micro-Grain Texture:** A fixed, full-viewport SVG turbulence layer (`opacity: 0.035`, `pointer-events: none`) providing tactile paper depth without layout impact.

---

## 6. Motion and Interaction

- **Physical Easing Tokens:**
  - `--ease-out: cubic-bezier(0.16, 1, 0.3, 1)` (rapid entrance with smooth deceleration)
  - `--ease-settle: cubic-bezier(0.32, 0.72, 0, 1)` (tactile mechanical drop)
  - `--ease-spring: linear(...)` with physical overshoot
- **Durations:** Micro-interactions (160ms), standard transitions (280ms), entrances (520ms), mechanical sequences (800ms).
- **GPU-Safe Properties:** Only animate `transform` and `opacity`. Never animate `width`, `height`, `margin`, `top`, or `padding`.
- **View Transitions API:** Cross-document door transition between Portal, Institute, and Placement using `@view-transition { navigation: auto; }`. The incoming domain's color floods outward from the switcher coordinates.
- **Reduced Motion:** Fully honors `@media (prefers-reduced-motion: reduce)` by bypassing transforms and immediately snapping content into place.

---

## 7. Anti-Patterns & Prohibited Decisions

1. **NO Generic Fonts:** Strictly no Inter, Roboto, Arial, or browser serif defaults.
2. **NO Synthetic Gradients / Neon Glows:** No purple mesh backgrounds, no cyan laser lines.
3. **NO Stock Emoji:** Strictly no emoji in UI labels, cards, or status indicators. All iconography uses the unified Phosphor Light SVG sprite.
4. **NO Eyebrow Tags on Public Surfaces:** No tiny uppercase tracked tags sitting above titles on public pages.
5. **NO 3-Equal Cards Rows:** No generic "Features" grid with identical icon-in-circle cards.
6. **NO Faux Vanity Numbers:** No fabricated statistics ("99.9% uptime", "10,000+ happy students"). All metrics derive from live database records or are omitted.
7. **NO Floating Glass Navbars:** Navigation bars must be solid, grounded, and legible without heavy backdrop filters over scrolling text.
8. **NO Missing Fallbacks:** Every interactive module must degrade gracefully when JavaScript is disabled or offline.
