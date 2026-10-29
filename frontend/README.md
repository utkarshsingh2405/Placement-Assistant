# Placement Assistant — Frontend Suite

This directory contains the standalone frontend client code for the **Placement Assistant** platform, decoupled from the Spring Boot backend services.

## Directory Structure

```
frontend/
├── index.html              # Main navigation hub & API target switcher
├── portal/                 # Portal & SSO Landing, Login, Registration
│   ├── index.html          # Interactive GSAP-animated landing page
│   ├── login.html          # Centralized Single Sign-On login
│   ├── register.html       # Student self-registration
│   ├── forgot-password.html
│   └── reset-password.html
├── institute/              # Academic Institute Pillar (9 Modules)
│   ├── index.html          # Entry redirect to dashboard
│   ├── dashboard.html      # Comprehensive master dashboard & stats
│   └── pages/
│       ├── 1-students/     # Student roster, dossiers, editing
│       ├── 2-company-drives/ # Campus drives & recruiter profiles
│       ├── 3-question-bank/  # Categorized interview question bank
│       ├── 4-attendance/   # Class sessions & CRT compliance
│       ├── 5-drive-rounds/ # Live selection stages & interview rounds
│       ├── 6-resume-ats/   # Resume upload & ATS keyword scoring
│       ├── 7-hackathons/   # National competitions & hackathons
│       ├── 8-internships/  # Industry internship postings
│       └── 9-offcampus-jobs/ # Direct tech hiring & referrals
├── placement/              # Corporate Placement Pillar
│   ├── index.html          # Real-time placement operations board
│   └── ...
└── shared/                 # Core Design System & Shared Assets
    ├── css/                # 00-tokens.css through 07-domains.css
    ├── js/                 # api.js, auth.js, ui.js, charts.js, etc.
    ├── icons/              # sprite.svg SVG icon symbols
    └── textures/           # Noise textures and patterns
```

## Running the Frontend Standalone

To run or preview the frontend independently of the backend:

```bash
cd frontend
npm start
```

Or with Python:
```bash
python -m http.server 3000
```

Open `http://localhost:3000`. You can configure the backend target URL directly from the switcher on `index.html`!
