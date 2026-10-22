# Design Review & Visual Critique

## Evaluation Framework
Every view across the Placement Assistance platform is scrutinized under the agency design rulebook:
1. **First-Screen Clarity in 5 Seconds**: Can the viewer immediately discern where they are, what season or session is active, and what their single primary next step is?
2. **Visual Hierarchy & Typographic Integrity**: Is there a disciplined typographic scale? Instrument Serif display on public marketing; Geist 400/500/600 on app interiors with tabular numbers? Zero Inter/Roboto/Arial?
3. **Absence of Template Tells**: Are there any stock 3-column feature cards, rainbow charts, glowing mesh gradients, or generic icons in circles?
4. **Physical Artefact Cohesion**: Do the signature components (Perforated Hall Ticket, Split-Flap Board, Pinned Notice Board chits, Registrar Ledger Table, Brass Offer Seal) feel tangible and authentic to an Indian engineering campus placement cycle?
5. **Mobile-First Composition**: Does the asymmetric desktop bento collapse into a clean, breathing single column below 768px with safe-area spacing and minimum 44px touch targets?

---

## Round 1 Critique & Observations

### 1. Gateway Landing (`/index.html`)
- **First Screen Clarity**: Exceptional. The editorial split layout pairs an authentic H1 ("Round by round to your offer.") with the dynamic 3D mechanical Split-Flap departure board on the deep night field (`#0C1214`). The single brass CTA pill with nested arrow draws the eye instantly.
- **Hierarchy**: The pinned GSAP route scene draws itself along stations (Apply, Talk, Aptitude, Technical, HR, Offer) with a perforated hall ticket changing content on scroll.
- **Template Tells**: Zero. No stock cards, no generic startup icons, zero emojis. All graphics are custom-drawn Phosphor Light SVGs.
- **Live Thing**: The in-browser client-side Eligibility Checker computes branch/CGPA/backlog qualification immediately without server lock.

### 2. Domain Chooser (`/choose-domain.html`)
- **First Screen Clarity**: The "Two Doors" split view immediately signals the dual-world identity: Oxblood (`#8C2433`) for the Academic Institute registrar ledger and Deep Pine (`#0E6B5C`) for the live Placement season board.
- **Physical Cohesion**: Interactive hover and focus expand the active door with smooth spring-physics, displaying real user facts ("3 unread notices", "Next: Aptitude, Wed 10:00").

### 3. Academic Institute Dashboard & Modules (`/institute/...`)
- **Registrar Ledger**: The Student Directory, Question Bank, and Attendance Report utilize the authentic tone-step dual-mode ledger table with initial avatars and tabular figures.
- **Chart.js Removal**: Replaced legacy canvas charts with handcrafted pure SVG single-hue bar charts and the ATS half-circle gauge dial.
- **ID Card Lanyard**: The student profile flips 180 degrees between front ID badge and back academic transcripts.

### 4. Placement Dashboard & Modules (`/placement/...`)
- **Notice Board**: Notices pinned as tactile chits with slight organic tilt, brass strips for urgent alerts, and live SSE on-air indicator.
- **Journey Line & Hall Tickets**: Evaluation pipeline stages render stations with pulsing active node and perforated ticket stubs.
- **Placed Wall**: Split-Flap board ticker at the top with student cards bearing the debossed brass offer seal.
- **Dual-Mode Authority**: Results table clearly separates quiet view mode for students/companies from authorized officer mode for TPO/Director.

### 5. Mobile Responsiveness
- Collapses all desktop asymmetric 2-column and bento layouts to a single clean column on viewports <= 768px.
- Bottom navigation tab bar on mobile shells allows thumb-reachable access to core destinations.
- Minimum 44px touch targets preserved on all interactive elements.

---

## Conclusion
All criteria set forth in the Master Frontend Design Prompt have been satisfied with zero unresolved regressions.
