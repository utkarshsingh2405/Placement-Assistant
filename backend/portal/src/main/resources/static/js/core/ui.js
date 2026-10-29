/**
 * Placement Assistance Platform Design System
 * core/ui.js: Tactile UI Component Engine (Toasts, Modals, Loaders, Empty States, Layout Rails)
 */

import auth from './auth.js';
import theme from './theme.js';
import domainSwitcher from '../shell/domain-switcher.js';

class UIManager {
  initLayout(activePageKey = '') {
    const user = auth.getUser();
    const role = user?.role || 'STUDENT';

    // Ensure SVG Sprite is present in DOM for reliable icon rendering
    this.injectSprite();

    // Inject Sidebar / App Rail
    const sidebarContainer = document.getElementById('sidebar-container');
    if (sidebarContainer) {
      sidebarContainer.innerHTML = this.renderRail(role, activePageKey);
    }

    // Inject Topbar
    const topbarContainer = document.getElementById('topbar-container');
    if (topbarContainer) {
      topbarContainer.innerHTML = this.renderTopbar(user);
      domainSwitcher.init();
      theme.updateToggleIcons(theme.getTheme());
    }

    // Bind common interactions
    document.getElementById('logout-btn')?.addEventListener('click', (e) => {
      e.preventDefault();
      auth.logout();
    });

    document.getElementById('theme-toggle-btn')?.addEventListener('click', () => {
      theme.toggleTheme();
    });

    document.getElementById('sidebar-toggle')?.addEventListener('click', () => {
      document.querySelector('.app-rail')?.classList.toggle('show');
    });

    document.getElementById('nav-back-btn')?.addEventListener('click', (e) => {
      e.preventDefault();
      if (window.history.length > 1) {
        window.history.back();
      } else {
        const isPlacement = domainSwitcher.getCurrentDomain() === 'placement';
        window.location.href = isPlacement ? '/placement/dashboard.html' : '/institute/dashboard.html';
      }
    });
  }

  async injectSprite() {
    if (document.getElementById('__svg_sprite_container__')) return;
    const candidates = [
      '/icons/sprite.svg',
      '/institute/icons/sprite.svg',
      '/placement/icons/sprite.svg',
      '../icons/sprite.svg',
      'icons/sprite.svg'
    ];
    for (const url of candidates) {
      try {
        const resp = await fetch(url);
        if (resp.ok) {
          const svgText = await resp.text();
          if (svgText.includes('<svg') && svgText.includes('symbol')) {
            const div = document.createElement('div');
            div.id = '__svg_sprite_container__';
            div.style.position = 'absolute';
            div.style.width = '0';
            div.style.height = '0';
            div.style.overflow = 'hidden';
            div.style.pointerEvents = 'none';
            div.innerHTML = svgText;
            document.body.insertBefore(div, document.body.firstChild);
            return;
          }
        }
      } catch (err) {
        // try next candidate
      }
    }
  }

  escapeHtml(str) {
    if (str === null || str === undefined) return '';
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }

  renderTopbar(user) {
    const role = user?.role || 'STUDENT';
    const isPlacement = domainSwitcher.getCurrentDomain() === 'placement';
    const initial = (user?.name || user?.email || 'U')[0].toUpperCase();

    return `
      <header class="app-topbar">
        <div style="display: flex; align-items: center; gap: var(--space-3);">
          <button id="sidebar-toggle" class="btn btn-sm btn-outline hide-on-desktop" type="button" aria-label="Toggle Navigation">
            <svg width="18" height="18"><use href="#ph-arrow-right"/></svg>
          </button>

          <!-- Explicit Back Button for Universal Navigation -->
          <button id="nav-back-btn" class="btn btn-sm btn-outline" title="Return to previous screen" type="button" style="display: flex; align-items: center; gap: 6px; padding: 6px 12px; font-weight: 500;">
            <svg width="14" height="14"><use href="#ph-arrow-left"/></svg>
            <span>Back</span>
          </button>

          <!-- Domain Gateway Chooser Button -->
          <a href="/choose-domain.html" class="btn btn-sm btn-outline" title="Return to Domain Gateway Chooser" style="display: flex; align-items: center; gap: 6px;">
            <svg width="14" height="14"><use href="#ph-layers"/></svg>
            <span>Gateway</span>
          </a>

          <!-- Domain Switcher Pill -->
          ${domainSwitcher.render()}

          <!-- Direct Cross-Pillar Quick Link Button -->
          ${isPlacement ? `
            <a href="/institute/dashboard.html" class="btn btn-sm btn-outline" style="border-color: var(--institute); color: var(--institute); font-weight: 600; padding: 6px 12px;" title="Switch to Academic Institute Pillar">
              <svg width="14" height="14"><use href="#ph-building"/></svg>
              <span>Institute Desk</span>
              <svg width="12" height="12"><use href="#ph-arrow-right"/></svg>
            </a>
          ` : `
            <a href="/placement/dashboard.html" class="btn btn-sm btn-outline" style="border-color: var(--placement); color: var(--placement); font-weight: 600; padding: 6px 12px;" title="Switch to Live Placement Recruitment Hub">
              <svg width="14" height="14"><use href="#ph-briefcase"/></svg>
              <span>Placement Season</span>
              <svg width="12" height="12"><use href="#ph-arrow-right"/></svg>
            </a>
          `}
        </div>

        <div style="display: flex; align-items: center; gap: var(--space-3);">
          <!-- Notification Bell -->
          <a href="/placement/pages/11-notifications/notifications.html" class="btn btn-sm btn-outline" title="Notifications" id="topbar-bell" style="position: relative; padding: 8px;">
            <svg width="16" height="16"><use href="#ph-bell"/></svg>
            <span id="unread-notification-count" class="chip chip-danger" style="position: absolute; top: -4px; right: -4px; padding: 2px 5px; font-size: 10px; display: none;">0</span>
          </a>

          <!-- Theme Toggle Button -->
          <button class="btn btn-sm btn-outline theme-toggle-btn" id="theme-toggle-btn" title="Toggle Light/Dark Theme" aria-label="Toggle Light/Dark Theme" style="padding: 8px;">
            <span id="theme-icon" class="theme-icon" style="display: flex; align-items: center; justify-content: center;">
              <svg width="16" height="16"><use href="#ph-moon"/></svg>
            </span>
          </button>

          <!-- User Chip & Logout -->
          <div style="display: flex; align-items: center; gap: var(--space-2); margin-left: var(--space-2);">
            <div style="width: 32px; height: 32px; border-radius: 50%; background: var(--accent); color: #FFF; display: flex; align-items: center; justify-content: center; font-weight: 600; font-size: var(--text-xs);">
              ${initial}
            </div>
            <div class="hide-on-mobile" style="text-align: left; line-height: 1.2;">
              <div style="font-size: var(--text-xs); font-weight: 600;">${this.escapeHtml(user?.name || user?.email || 'User')}</div>
              <div style="font-size: 11px; color: var(--muted);">${role}</div>
            </div>
            <button id="logout-btn" class="btn btn-sm btn-outline" title="Sign Out" style="padding: 8px; margin-left: var(--space-1);">
              <svg width="14" height="14"><use href="#ph-sign-out"/></svg>
            </button>
          </div>
        </div>
      </header>
    `;
  }

  renderRail(role, activePageKey) {
    const domain = domainSwitcher.getCurrentDomain();
    if (domain === 'placement') {
      return this.renderPlacementRail(role, activePageKey);
    }
    return this.renderInstituteRail(role, activePageKey);
  }

  renderInstituteRail(role, activePageKey) {
    const isStudent = role === 'STUDENT';
    const items = isStudent ? [
      { key: 'dashboard', label: 'My Academic Desk', icon: 'ph-building', href: '/institute/dashboard.html' },
      { key: 'profile', label: 'My Student Profile', icon: 'ph-users', href: '/institute/pages/1-students/student-profile.html' },
      { key: 'drives', label: 'Master Drives', icon: 'ph-briefcase', href: '/institute/pages/2-company-drives/drive-list.html' },
      { key: 'questions', label: 'Question Bank', icon: 'ph-clipboard-text', href: '/institute/pages/3-question-bank/question-list.html' },
      { key: 'resume-ats', label: 'Resume & ATS Diagnostic', icon: 'ph-file-text', href: '/institute/pages/6-resume-ats/ats-report.html' },
      { key: 'hackathons', label: 'National Hackathons', icon: 'ph-code', href: '/institute/pages/7-hackathons/hackathon-list.html' },
      { key: 'internships', label: 'Internship Opportunities', icon: 'ph-briefcase', href: '/institute/pages/8-internships/internship-list.html' },
      { key: 'jobs', label: 'Off-Campus Openings', icon: 'ph-buildings', href: '/institute/pages/9-offcampus-jobs/job-list.html' }
    ] : [
      { key: 'dashboard', label: 'Dashboard', icon: 'ph-building', href: '/institute/dashboard.html' },
      { key: 'students', label: 'Student Directory', icon: 'ph-users', href: '/institute/pages/1-students/student-list.html' },
      { key: 'drives', label: 'Master Drives', icon: 'ph-briefcase', href: '/institute/pages/2-company-drives/drive-list.html' },
      { key: 'rounds', label: 'Rounds Pipeline', icon: 'ph-flow-arrow', href: '/institute/pages/5-drive-rounds/rounds-board.html' },
      { key: 'questions', label: 'Question Bank', icon: 'ph-clipboard-text', href: '/institute/pages/3-question-bank/question-list.html' },
      { key: 'attendance-class', label: 'Class Attendance', icon: 'ph-calendar-check', href: '/institute/pages/4-attendance/class/class-sessions.html' },
      { key: 'attendance-crt', label: 'CRT Attendance', icon: 'ph-check-circle', href: '/institute/pages/4-attendance/crt/crt-sessions.html' },
      { key: 'resume-ats', label: 'Resume & ATS Diagnostic', icon: 'ph-file-text', href: '/institute/pages/6-resume-ats/ats-report.html' },
      { key: 'hackathons', label: 'National Hackathons', icon: 'ph-code', href: '/institute/pages/7-hackathons/hackathon-list.html' },
      { key: 'internships', label: 'Internship Opportunities', icon: 'ph-briefcase', href: '/institute/pages/8-internships/internship-list.html' },
      { key: 'jobs', label: 'Off-Campus Openings', icon: 'ph-buildings', href: '/institute/pages/9-offcampus-jobs/job-list.html' }
    ];

    return `
      <aside class="app-rail">
        <div style="padding: var(--space-5) var(--space-4); border-bottom: 1px solid var(--surface-3);">
          <div class="brand-wordmark">
            <span class="brand-dot"></span>
            <span>Institute Academic</span>
          </div>
        </div>
        <div style="flex: 1; overflow-y: auto; padding: var(--space-3) 0;">
          <div class="nav-rail-section">${isStudent ? 'Candidate Console' : 'Academic Registry'}</div>
          ${items.map(item => `
            <a href="${item.href}" class="nav-rail-item ${activePageKey === item.key ? 'active' : ''}">
              <svg width="18" height="18"><use href="#${item.icon}"/></svg>
              <span>${item.label}</span>
            </a>
          `).join('')}
        </div>
        <div style="padding: var(--space-3) var(--space-4); border-top: 1px solid var(--surface-3);">
          <a href="/placement/dashboard.html" class="btn btn-sm btn-outline w-100" style="display: flex; align-items: center; justify-content: center; gap: 8px; text-decoration: none; width: 100%;">
            <span>Switch to Placement Hub</span>
            <svg width="14" height="14"><use href="#ph-arrow-right"/></svg>
          </a>
        </div>
      </aside>
    `;
  }

  renderPlacementRail(role, activePageKey) {
    const isStudent = role === 'STUDENT';
    const items = isStudent ? [
      { key: 'dashboard', label: 'My Placement Hub', icon: 'ph-briefcase', href: '/placement/dashboard.html' },
      { key: 'drives', label: 'Eligible Drives', icon: 'ph-briefcase', href: '/placement/pages/1-drives/drive-list.html' },
      { key: 'applications', label: 'My Applications', icon: 'ph-file-text', href: '/placement/pages/2-applications/my-applications.html' },
      { key: 'talks', label: 'Pre-Placement Talks', icon: 'ph-clock', href: '/placement/pages/4-talks/talk-list.html' },
      { key: 'live-feed', label: 'Live Notice Board', icon: 'ph-broadcast', href: '/placement/pages/5-live-updates/live-feed.html' },
      { key: 'attendance-qr', label: 'Scan Attendance QR', icon: 'ph-qr-code', href: '/placement/pages/6-attendance/scan.html' },
      { key: 'results', label: 'Results & Offers', icon: 'ph-seal-check', href: '/placement/pages/7-results/drive-results.html' },
      { key: 'placed-wall', label: 'Hall of Fame', icon: 'ph-users', href: '/placement/pages/8-placed/placed-students.html' },
      { key: 'feedback', label: 'Interview Experiences', icon: 'ph-chat-circle-dots', href: '/placement/pages/10-feedback/my-feedback.html' }
    ] : [
      { key: 'dashboard', label: 'Placement Control Center', icon: 'ph-briefcase', href: '/placement/dashboard.html' },
      { key: 'drives', label: 'Recruitment Drives', icon: 'ph-briefcase', href: '/placement/pages/1-drives/drive-list.html' },
      { key: 'applications', label: 'Applicant Pipeline', icon: 'ph-users', href: '/placement/pages/2-applications/application-board.html' },
      { key: 'stages', label: 'Stage Progression', icon: 'ph-calendar', href: '/placement/pages/3-stages/stage-management.html' },
      { key: 'talks', label: 'Pre-Placement Talks', icon: 'ph-clock', href: '/placement/pages/4-talks/talk-list.html' },
      { key: 'live-feed', label: 'Notice Broadcasts', icon: 'ph-broadcast', href: '/placement/pages/5-live-updates/live-feed.html' },
      { key: 'attendance-qr', label: 'Mark Attendance', icon: 'ph-check-circle', href: '/placement/pages/6-attendance/mark-attendance.html' },
      { key: 'results', label: 'Results & Offers', icon: 'ph-seal-check', href: '/placement/pages/7-results/drive-results.html' },
      { key: 'placed-wall', label: 'Placed Students Wall', icon: 'ph-users', href: '/placement/pages/8-placed/placed-students.html' },
      { key: 'feedback', label: 'Interview Experiences', icon: 'ph-chat-circle-dots', href: '/placement/pages/10-feedback/my-feedback.html' },
      { key: 'settings', label: 'Campus Connection', icon: 'ph-gear', href: '/placement/pages/settings/connection.html' }
    ];

    return `
      <aside class="app-rail">
        <div style="padding: var(--space-5) var(--space-4); border-bottom: 1px solid var(--surface-3);">
          <div class="brand-wordmark">
            <span class="brand-dot"></span>
            <span>Placement Season</span>
          </div>
        </div>
        <div style="flex: 1; overflow-y: auto; padding: var(--space-3) 0;">
          <div class="nav-rail-section">${isStudent ? 'Candidate Console' : 'Live Operations'}</div>
          ${items.map(item => `
            <a href="${item.href}" class="nav-rail-item ${activePageKey === item.key ? 'active' : ''}">
              <svg width="18" height="18"><use href="#${item.icon}"/></svg>
              <span>${item.label}</span>
            </a>
          `).join('')}
        </div>
        <div style="padding: var(--space-3) var(--space-4); border-top: 1px solid var(--surface-3);">
          <a href="/institute/dashboard.html" class="btn btn-sm btn-primary w-100" style="display: flex; align-items: center; justify-content: center; gap: 8px; text-decoration: none; width: 100%;">
            <span>Switch to Academic Institute</span>
            <svg width="14" height="14"><use href="#ph-arrow-right"/></svg>
          </a>
        </div>
      </aside>
    `;
  }

  renderLoader(message = 'Loading records...') {
    return `
      <div style="display: flex; flex-direction: column; align-items: center; justify-content: center; padding: var(--space-12) 0; color: var(--muted); text-align: center;">
        <div class="skeleton" style="width: 48px; height: 48px; border-radius: 50%; margin-bottom: var(--space-3);"></div>
        <div style="font-size: var(--text-sm); font-weight: 500;">${this.escapeHtml(message)}</div>
      </div>
    `;
  }

  renderEmpty(message = 'No records found', submessage = 'Try adjusting your search criteria or filters.', actionHtml = '') {
    return `
      <div style="display: flex; flex-direction: column; align-items: center; justify-content: center; padding: var(--space-16) var(--space-4); text-align: center; color: var(--muted);">
        <div style="width: 64px; height: 64px; border-radius: 50%; background: var(--surface-2); display: flex; align-items: center; justify-content: center; margin-bottom: var(--space-4); color: var(--muted);">
          <svg width="28" height="28"><use href="#ph-clipboard-text"/></svg>
        </div>
        <h3 style="color: var(--ink); margin-bottom: var(--space-2); font-size: 1.15rem;">${this.escapeHtml(message)}</h3>
        <p style="font-size: var(--text-sm); max-width: 45ch; margin-bottom: var(--space-4);">${this.escapeHtml(submessage)}</p>
        ${actionHtml}
      </div>
    `;
  }

  renderError(message = 'Unable to fetch data', retryFnName = null) {
    return `
      <div style="display: flex; flex-direction: column; align-items: center; justify-content: center; padding: var(--space-12) var(--space-4); text-align: center;">
        <div style="width: 48px; height: 48px; border-radius: 50%; background: var(--danger-tint); color: var(--danger); display: flex; align-items: center; justify-content: center; margin-bottom: var(--space-3);">
          <svg width="24" height="24"><use href="#ph-warning-circle"/></svg>
        </div>
        <div style="font-weight: 600; color: var(--danger); margin-bottom: var(--space-1);">${this.escapeHtml(message)}</div>
        <div style="font-size: var(--text-xs); color: var(--muted); margin-bottom: var(--space-4);">Please verify connection and retry.</div>
        ${retryFnName ? `<button class="btn btn-sm btn-outline" onclick="${retryFnName}()">Retry</button>` : ''}
      </div>
    `;
  }

  toast(message, type = 'info') {
    let container = document.querySelector('.toast-container');
    if (!container) {
      container = document.createElement('div');
      container.className = 'toast-container';
      document.body.appendChild(container);
    }

    const card = document.createElement('div');
    card.className = `toast-card toast-${type}`;
    card.innerHTML = `
      <svg width="18" height="18"><use href="#${type === 'success' ? 'ph-check-circle' : (type === 'danger' ? 'ph-warning-circle' : 'ph-bell')}"/></svg>
      <span>${this.escapeHtml(message)}</span>
    `;

    container.appendChild(card);
    setTimeout(() => {
      card.style.opacity = '0';
      card.style.transform = 'translateY(12px)';
      card.style.transition = 'all 240ms ease-out';
      setTimeout(() => card.remove(), 250);
    }, 4000);
  }

  showToast(message, type = 'info') {
    return this.toast(message, type);
  }

  confirm(arg1, arg2 = '') {
    return new Promise((resolve) => {
      let title = 'Confirmation Required';
      let message = arg1;
      if (arg2) {
        if (arg1.length < arg2.length) {
          title = arg1;
          message = arg2;
        } else {
          message = arg1;
          title = arg2;
        }
      }

      const dialog = document.createElement('dialog');
      dialog.className = 'custom-dialog';
      dialog.innerHTML = `
        <div class="dialog-card" style="max-width: 480px;">
          <div class="dialog-header p-4 border-bottom d-flex justify-between align-center">
            <h3 class="text-base fw-bold mb-0 text-ink">${this.escapeHtml(title)}</h3>
            <button type="button" class="btn-icon" id="confirm-x" aria-label="Close">
              <svg class="icon icon-sm"><use href="/icons/sprite.svg#x"/></svg>
            </button>
          </div>
          <div class="dialog-body p-5">
            <p style="color: var(--ink-2); font-size: var(--text-sm); line-height: 1.5; margin-bottom: 0;">${this.escapeHtml(message)}</p>
          </div>
          <div class="dialog-footer p-4 border-top d-flex justify-end gap-2 surface-ground">
            <button class="btn btn-secondary btn-sm" id="confirm-cancel">Cancel</button>
            <button class="btn btn-danger btn-sm" id="confirm-ok">Confirm</button>
          </div>
        </div>
      `;
      document.body.appendChild(dialog);
      if (dialog.showModal) dialog.showModal();
      else dialog.setAttribute('open', 'true');

      const cleanup = (res) => {
        if (dialog.close) dialog.close();
        dialog.remove();
        resolve(res);
      };

      dialog.querySelector('#confirm-cancel')?.addEventListener('click', () => cleanup(false));
      dialog.querySelector('#confirm-x')?.addEventListener('click', () => cleanup(false));
      dialog.querySelector('#confirm-ok')?.addEventListener('click', () => cleanup(true));
    });
  }
}

const ui = new UIManager();
export default ui;
