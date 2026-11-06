# Inter-Pillar Integration & Connection Guide

This document defines the architectural connection between the **Portal Gateway (8080)**, **Institute Pillar (8081)**, and **Placement Pillar (8082)**.

---

## 1. Top-Level Network Architecture

```
                    Visitor / Browser
                           │
             http://localhost:8080 (Single Port)
                           ▼
             ┌─────────────────────────────┐
             │    Spring Cloud Gateway     │
             │   & Single Sign-On (SSO)    │
             │         PORT 8080           │
             └─────────────┬───────────────┘
                           │
        ┌──────────────────┴──────────────────┐
        │ /institute/**                       │ /placement/**
        ▼                                     ▼
┌───────────────────────────┐         ┌───────────────────────────┐
│     Institute Pillar      │         │     Placement Pillar      │
│         PORT 8081         │◄────────┤         PORT 8082         │
│     data/institute.db     │ REST    │     data/placement.db     │
│   Context: /institute     │ X-API-KEY│   Context: /placement     │
└───────────────────────────┘         └───────────────────────────┘
```

---

## 2. Ports, Context Paths & Databases

| Pillar / Service | Port | Context Path | Primary Database | Key Capabilities |
| :--- | :--- | :--- | :--- | :--- |
| **Portal Gateway** | `8080` | `/` | `data/portal.db` | Landing page, Gateway reverse proxy, SSO login, Domain Chooser, Ticker |
| **Institute Pillar** | `8081` | `/institute` | `data/institute.db` | Student academic records, drives master, question bank, CRT & class attendance, ATS scorer |
| **Placement Pillar** | `8082` | `/placement` | `data/placement.db` | Live recruitment engine, stages stepper, PPTs, SSE live feed, QR attendance, selected lists |

---

## 3. Strict Boundary Rules

1. **Zero Database Sharing**:
   - The Institute pillar touches **ONLY** `data/institute.db`.
   - The Placement pillar touches **ONLY** `data/placement.db`.
   - The Portal touches **ONLY** `data/portal.db`.
2. **Zero Direct Java Class Imports**:
   - No module imports classes from another pillar module. Reusable common classes reside strictly in `shared-lib`.
3. **Inter-Service Communication**:
   - The Placement pillar fetches master student and drive information from Institute via `RestInstituteClient` over HTTP REST:
     - Header: `X-API-KEY: inst-sec-key-7f9a12b4e8c345d6`
     - Endpoint: `http://localhost:8081/institute/api/integration/**`
   - If Institute is unavailable, Placement gracefully falls back to `StubInstituteClient` with zero runtime crash.

---

## 4. Single Sign-On (SSO) & Context Forwarding

1. When a user logs in via `http://localhost:8080/login.html`:
   - A signed JWT token is issued and stored in `localStorage` under `pa_token` and in a browser cookie.
2. The Spring Cloud Gateway forwards the user identity in downstream request headers:
   - `X-User-Email`: user's login email
   - `X-User-Role`: user role (`STUDENT`, `TPO`, `DIRECTOR`, `COMPANY`, `ADMIN`)
   - `X-User-Id`: unique numeric ID
3. Downstream filters (`JwtAuthFilter` and `PortalJwtFilter`) authenticate requests transparently based on either the Bearer token or Gateway forwarded headers.

---

## 5. Domain Switcher (`Alt+D`)

- Implemented in `shared-web/js/domain-switcher.js` and styled via `shared-web/css/domain-switcher.css`.
- The top header pill shows current domain (`Portal`, `Institute`, or `Placement`).
- Clicking or pressing `Alt+D` reveals a dropdown enabling instantaneous switching without re-logging in.
- Shows unread notification indicator dots across domains.

---

## 6. Strict Role Permissions Matrix

| Action / Endpoint | STUDENT | COMPANY | TPO | DIRECTOR | ADMIN |
| :--- | :---: | :---: | :---: | :---: | :---: |
| View Active Drives & Stages | Yes | Yes | Yes | Yes | Yes |
| Apply for Drive | Yes | No | No | No | No |
| Join PPT & Live Q&A | Yes | Yes | Yes | Yes | Yes |
| QR Attendance Check-in | Yes | No | Yes | Yes | Yes |
| **Mark Candidate Result (SELECTED/NOT_SELECTED)** | **403 Forbidden** | **403 Forbidden** | **Allowed** | **Allowed** | 403 / Audit |
| Launch New Drive | No | No | Allowed | Allowed | Allowed |
| Activate Recruitment Stage | No | No | Allowed | Allowed | Allowed |
| Modify Master Student Profile | Self | No | Allowed | Allowed | Allowed |
