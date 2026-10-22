# Institute Integration & Multi-Pillar Architecture Plan

This plan documents every modification, addition, and configuration change required to integrate the Institute Pillar into the unified portal and placement architecture. All changes to the existing Institute codebase are strictly **additive** and preserve 100% backward compatibility.

---

## 1. Principles of Integration

1. **Autonomous Operation**: Each pillar retains its own standalone database (`data/institute.db`, `data/placement.db`, `data/portal.db`), Flyway migration lifecycle, and configuration.
2. **Zero Cross-Pillar Class Imports**: Institute and Placement modules never import each other's classes or entities. Communication occurs exclusively over HTTP via the Institute Integration REST API using `InstituteClient`.
3. **Single Origin & SSO Gateway**: The Portal on `http://localhost:8080` terminates user requests and proxies `/institute/**` to Institute (`:8081`) and `/placement/**` to Placement (`:8082`). All apps share one JWT secret and `localStorage` session.
4. **Additive Modifications**: No existing entity, table, or business logic in the Institute pillar is removed or altered destructively.

---

## 2. File-by-File Modification Inventory

### Root & Build Configuration
| File | Action | Purpose / Rationale |
|---|---|---|
| `pom.xml` | Modify | Add `<module>portal</module>` and `<module>placement</module>`. Add Spring Cloud BOM (`2023.0.3` compatible with Boot `3.3.4`), `nl.martijndwars:web-push` (VAPID), `com.google.zxing:core` & `javase` (QR generation), `org.apache.poi:poi-ooxml` (Excel result parsing). |
| `docker-compose.yml` | Modify | Define `portal` (8080), `institute` (8081), `placement` (8082) with shared `./data` volume and health checks. |
| `.env.example` | Modify | Add environment variable documentation for multi-pillar ports, URLs, integration keys, VAPID, and email modes. |
| `run-all.bat` / `run-all.sh` | Create | One-command launcher executing dependency checks, optional build, background startup, health verification, and connection testing. |
| `stop-all.bat` / `stop-all.sh` | Create | Graceful shutdown script for all 3 services using PID tracking. |
| `status.bat` / `status.sh` | Create | Reports runtime health, port, PID, and memory status across all 3 apps. |
| `verify-connection.bat` / `.sh` | Create | Automated curl/HTTP verification test validating all Institute integration contract endpoints with API key. |

### Shared Library (`shared-lib/`)
| File | Action | Purpose / Rationale |
|---|---|---|
| `com/placementassist/shared/security/Role.java` | Modify | Add `DIRECTOR` and `COMPANY` enum constants. Existing `STUDENT`, `ADMIN`, `TPO` remain unchanged. |
| `com/placementassist/shared/security/JwtService.java` | Modify | Standardize JWT claims across portal and pillars: `sub` (email), `userId`, `role`, `rollNo`, `name`, `companyId`. |
| `com/placementassist/shared/security/PortalJwtFilter.java` | Create | Reusable servlet and gateway filter to validate portal-issued JWTs across all services. |
| `com/placementassist/shared/security/JitUserProvisioner.java` | Create | Interface enabling pillars to dynamically provision or look up local users from portal JWT claims upon first arrival. |

### Shared Frontend (`shared-web/`)
| File | Action | Purpose / Rationale |
|---|---|---|
| `css/theme.css` | Create / Replace | Complete CSS token system with `--bg`, `--surface`, `--accent`, `--brand-navy`, `--gold`, etc., for light/dark themes and `data-domain="portal|institute|placement"`. |
| `css/components.css` | Modify | Card, stat-card, stepper, modal, toast, and badge styling bound directly to theme variables. |
| `css/domain-switcher.css` | Create | Styles for the top-bar domain switcher pill, dropdown, domain icons, and unread notification indicators. |
| `css/landing.css` | Create | Styles for the portal home page: hero gradient, statistics ticker, recruiter logo strip, and cards. |
| `js/theme.js` | Create | Theme manager (light, dark, system) with `localStorage` persistence and instant token switching. |
| `js/domain-switcher.js` | Create | Interactive domain switcher component supporting one-click transition, `Alt+D` shortcut, and domain remembering. |
| `js/api.js` | Modify | Dynamic context-path calculation so API calls resolve correctly under `/institute`, `/placement`, or portal gateway. |
| `js/auth.js` | Modify | Intercept 401s, auto-refresh tokens, support `?next=` redirects to portal login. |
| `js/ui.js` | Modify | Integrate domain switcher and theme toggle into `renderTopbar()`. Add skeleton loaders and confirm modals. |
| `js/sse.js` | Create | Resilient Server-Sent Events client for real-time live feeds and notifications. |
| `js/push.js` | Create | Web Push subscription helper interfacing service worker and VAPID public key. |
| `js/qr.js` | Create | Camera barcode detection and manual code check-in fallback. |
| `js/placed-badge.js` | Create | Dynamic "Placed at {Company}" badge query for student profile pages. |

### Institute Pillar (`institute/`)
| File | Action | Purpose / Rationale |
|---|---|---|
| `application.yml` | Modify | Set `server.servlet.context-path: ${CONTEXT_PATH:/institute}`. Add `portal.sso-enabled: ${PORTAL_SSO_ENABLED:true}` and `portal.url: ${PORTAL_URL:http://localhost:8080}`. |
| `config/SecurityConfig.java` | Modify | Integrate `PortalJwtFilter` and redirect unauthenticated login page hits to Portal when `portal.sso-enabled=true`. |
| `modules/integration/IntegrationController.java` | Modify | Add missing contract endpoints: `GET /drives/{id}/rounds`, `GET /internships`, `GET /hackathons`, `GET /offcampus-jobs`, and `GET /users/export`. |
| `modules/integration/dto/*` | Create | DTOs for rounds, listings, and user export (`UserExportDto`, `ListingSummaryDto`, `RoundSummaryDto`). |
| Static HTML files (`institute/src/main/resources/static/`) | Modify | Update asset links (`/css/...`, `/pages/...`) to context-relative paths so pages load correctly behind `:8080/institute`. |
| `pages/1-students/student-profile.html` | Modify | Include `placed-badge.js` to render live placement status if placed. |

### Portal Application (`portal/`) - NEW
- Gateway configuration routes `/institute/**` and `/placement/**`.
- Fallback route returning `unavailable.html` with retry logic when a pillar is offline.
- Identity Provider with `PortalUser` repository, BCrypt, registration validation via Institute API, and `InstituteUserImporter`.
- Home Landing Page (`index.html`) featuring live statistics ticker, recruiter strip, domain cards, and FAQ.
- Domain Chooser (`choose-domain.html`), User Admin (`admin/users.html`), and Service Worker (`sw.js`).

### Placement Pillar (`placement/`) - NEW
- Independent Spring Boot service on port `8082` with context path `/placement` and `data/placement.db`.
- Institute Access Layer (`placement/institute/`): `InstituteClient`, `RestInstituteClient`, `StubInstituteClient`, `InstituteCacheService`.
- 11 complete recruitment modules: Company, Drive & Stages, Applications, Stage Updates & Advancement, Pre-Placement Talks, Live Updates (SSE), Attendance (Rotating QR), Results (Selected/Non-Selected lists with strict TPO/Director edit permissions), Placed Students & Public Highlights, Opportunities, and Generic Feedback Engine.
