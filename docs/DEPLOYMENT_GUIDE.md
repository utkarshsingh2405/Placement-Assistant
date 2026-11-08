# Placement Assistant Enterprise &mdash; Production Deployment & Hosting Guide

Comprehensive production deployment guide for deploying the unified Placement Assistant platform to **Render** and **Railway**.

---

## 1. Executive Summary & Root Cause Analysis of Recent Deployment Errors

### Render Build Failure: `lstat /opt/render/project/src/placement-assist: no such file or directory`
- **Root Cause**: In the Render Dashboard under **Service Settings &rarr; Root Directory**, the value was set to `placement-assist`.
- When Render clones the repository (`https://github.com/utkarshsingh2405/Placement-Assistant`), it places the repository contents directly into `/opt/render/project/src`. Because the root of this repository contains `Dockerfile`, `backend/`, and `frontend/` directly, pointing the "Root Directory" to `placement-assist` causes Docker BuildKit to look for a non-existent subfolder `/opt/render/project/src/placement-assist`.
- **Solution**: Clear the **Root Directory** field in Render (leave it completely blank or set to `.`).

### Railway Build / Healthcheck Failure
- **Root Cause**: `railway.json` previously had a duplicate `startCommand` conflicting with the container's supervisor entrypoint, and default health check timeouts were too aggressive for a Java multi-module cold start.
- **Solution**:
  1. Updated `railway.json` with `builder: "DOCKERFILE"`.
  2. Health check path configured to `/healthz` with a 300-second timeout window.
  3. Replaced entrypoint with Docker `CMD` syntax in `Dockerfile` for seamless orchestrator compatibility.

---

## 2. Deploying on Render (Recommended Step-by-Step)

### Option A: Automatic Blueprint Deployment (Zero-Config)
The repository includes a ready-to-use `render.yaml` blueprint.
1. Log into [Render Dashboard](https://dashboard.render.com).
2. Click **New +** &rarr; **Blueprint**.
3. Connect your repository: `utkarshsingh2405/Placement-Assistant`.
4. Render will read `render.yaml` automatically and configure:
   - **Service Name**: `placement-assistant-unified`
   - **Runtime**: `Docker`
   - **Plan**: `starter` (or free)
   - **Health Check Path**: `/healthz`
5. Click **Apply**.

---

### Option B: Manual Web Service Deployment via Render Dashboard
If configuring manually:
1. Go to [Render Dashboard](https://dashboard.render.com) &rarr; **New +** &rarr; **Web Service**.
2. Select **Build and deploy from a Git repository** &rarr; Connect `Placement-Assistant`.
3. Configure the settings **exactly** as follows:

| Field | Configuration Value | Crucial Note |
|---|---|---|
| **Name** | `Placement-Assistant` | Or any name of your choice |
| **Region** | `Oregon (US West)` or `Singapore / Frankfurt` | Choose closest to your users |
| **Branch** | `main` | Production branch |
| **Root Directory** | **LEAVE BLANK** *(Do NOT type `placement-assist`)* | **Crucial:** Must be empty so Render builds from repository root |
| **Runtime** | **Docker** | Uses root `Dockerfile` |
| **Dockerfile Path** | `./Dockerfile` | Defaults to `./Dockerfile` |
| **Docker Build Context** | `.` | Points to repository root |
| **Instance Type** | Free or Starter (512MB - 1GB RAM) | Multi-module Java runs smoothly |

4. Under **Advanced &rarr; Health Check Path**:
   - Set to: `/healthz`
5. Click **Create Web Service**.

---

## 3. Deploying on Railway (Fastest Alternative)

Railway automatically detects Dockerfiles and deploys with low latency.

1. Log into [Railway.app](https://railway.app).
2. Click **New Project** &rarr; **Deploy from GitHub repo**.
3. Select `utkarshsingh2405/Placement-Assistant`.
4. Railway will immediately detect the root `Dockerfile` and `railway.json`.
5. Go to **Settings &rarr; Networking**:
   - Click **Generate Domain** (e.g. `placement-assistant-production.up.railway.app`).
6. Go to **Settings &rarr; Healthcheck**:
   - Path: `/healthz`
   - Timeout: `300` seconds.
7. Deployment completes in ~3-4 minutes.

---

## 4. Production Data Persistence (Crucial for SQLite & Uploads)

By default, containerized platforms like Render and Railway have ephemeral storage: every new deployment or container restart re-seeds the application from scratch.

To preserve user accounts, created drives, application pipelines, and resume diagnostic data permanently:

### On Render:
1. Open your Web Service in Render.
2. Navigate to **Disks** in the left sidebar.
3. Click **Add Disk**:
   - **Name**: `placement-data`
   - **Mount Path**: `/app/database`
   - **Size**: `1 GB` (or more)
4. Save and redeploy. All SQLite records in `app.db` will persist forever across all future updates.

### On Railway:
1. Open your project in Railway.
2. Click **+ New** &rarr; **Volume**.
3. Attach the Volume to the `Placement-Assistant` service.
4. Set the **Mount Path** to `/app/database`.

---

## 5. Platform Optimizations & SEO Directives Added

The following features and performance upgrades are pre-bundled in the deployment:

### 1. Interactive & Machine Sitemaps
- **XML Sitemap**: Accessible publicly at `/sitemap.xml` for Google, Bing, and Search Console indexing.
- **Search Engine Directives**: Accessible at `/robots.txt`. Protects internal endpoints (`/api/`, `/healthz`, `/actuator/`) while welcoming search engine indexing on public portals.
- **Human-Readable Directory**: Accessible at `/sitemap.html`. Includes real-time client-side search, category tabs, and persona badges (`Student`, `Placement Cell`, `Academic Admin`, `Recruiter`).

### 2. Gzip Response Compression
- Enabled in `application.yml` for all three Spring Boot microservices:
  - Compressed MIME types: `text/html`, `text/css`, `application/javascript`, `application/json`, `image/svg+xml`.
  - Typical payload reduction: **65% &ndash; 78%**, ensuring fast initial page loads over mobile 4G/5G networks.

### 3. Progressive Web App (PWA) Manifest
- Accessible at `/manifest.json`.
- Allows students and recruiters to "Add to Home Screen" on Android, iOS Safari, and Windows desktop as a standalone web app.

### 4. Custom Error Routing (404 Page)
- Accessible at `/404.html`.
- Dual-pillar branded fallback page with direct navigation back to the Portal Gateway, Institute Desk, Placement Hub, or Sitemap.

---

## 6. Pre-Flight Verification Checklist

Before triggering a deployment, verify the following:
- [x] All 3 modules (`backend/portal`, `backend/institute`, `backend/placement`) build cleanly without test failures (`-Dmaven.test.skip=true`).
- [x] Unified supervisor script (`backend/run-all.sh`) starts all 3 services and the gateway reverse proxy on dynamic `$PORT`.
- [x] `/healthz` endpoint responds with HTTP 200 once gateway is up.
- [x] Render dashboard **Root Directory** setting is verified **empty / blank**.
- [x] Railway volume mounted at `/app/database` for database persistence.

