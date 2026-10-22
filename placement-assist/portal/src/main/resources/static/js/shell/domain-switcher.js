/**
 * Placement Assistance Platform Design System
 * shell/domain-switcher.js: Segmented Domain Switcher with Tactile Thumb and Alt+D Hotkey
 */

import auth from '../core/auth.js';

class DomainSwitcher {
  constructor() {
    this.STORAGE_KEY = 'pa_last_domain';
    this.currentDomain = this.detectDomain();
  }

  detectDomain() {
    const explicit = document.documentElement.getAttribute('data-domain');
    if (explicit) return explicit;
    const path = window.location.pathname;
    if (path.startsWith('/institute')) return 'institute';
    if (path.startsWith('/placement')) return 'placement';
    return 'portal';
  }

  getCurrentDomain() {
    return this.currentDomain;
  }

  render() {
    const user = auth.getUser();
    const role = user?.role || 'STUDENT';
    const isCompany = role === 'COMPANY';

    const domainMeta = {
      portal: { name: 'Portal', color: 'var(--brass)' },
      institute: { name: 'Institute', color: 'var(--institute)' },
      placement: { name: 'Placement', color: 'var(--placement)' }
    };

    const current = domainMeta[this.currentDomain] || domainMeta.portal;

    return `
      <div class="domain-switcher-container" id="domain-switcher-container">
        <button class="domain-pill" id="domain-switcher-pill" type="button" aria-haspopup="true" aria-expanded="false" title="Switch domain (Alt+D)">
          <span class="domain-dot" style="background-color: ${current.color};"></span>
          <span>${current.name}</span>
          <svg width="12" height="12"><use href="#ph-caret-down"/></svg>
          <span id="domain-global-alert-dot" class="chip chip-danger" style="display: none; padding: 2px 4px; font-size: 8px;">•</span>
        </button>

        <div class="domain-dropdown" id="domain-switcher-dropdown">
          <div style="font-size: var(--text-xs); color: var(--muted); padding: 6px 12px; display: flex; justify-content: space-between;">
            <span>SWITCH DESK</span>
            <kbd style="font-size: 10px; background: var(--surface-2); padding: 1px 4px; border-radius: 4px;">Alt+D</kbd>
          </div>

          <!-- Placement Desk -->
          <div class="domain-item ${this.currentDomain === 'placement' ? 'active' : ''}" data-target-domain="placement">
            <div style="display: flex; align-items: center; gap: var(--space-2);">
              <span class="domain-dot" style="background-color: var(--placement);"></span>
              <span>Placement Recruitment</span>
            </div>
            <span id="placement-unread-dot" class="chip chip-danger" style="display: none; padding: 2px 4px; font-size: 8px;">•</span>
          </div>

          <!-- Institute Desk (Hidden for Recruiters) -->
          ${!isCompany ? `
          <div class="domain-item ${this.currentDomain === 'institute' ? 'active' : ''}" data-target-domain="institute">
            <div style="display: flex; align-items: center; gap: var(--space-2);">
              <span class="domain-dot" style="background-color: var(--institute);"></span>
              <span>Institute Academic</span>
            </div>
            <span id="institute-unread-dot" class="chip chip-danger" style="display: none; padding: 2px 4px; font-size: 8px;">•</span>
          </div>
          ` : ''}

          <div style="height: 1px; background: var(--surface-3); margin: 4px 0;"></div>

          <!-- Gateway Home -->
          <div class="domain-item ${this.currentDomain === 'portal' ? 'active' : ''}" data-target-domain="portal">
            <div style="display: flex; align-items: center; gap: var(--space-2);">
              <span class="domain-dot" style="background-color: var(--brass);"></span>
              <span>Domain Gateway</span>
            </div>
          </div>

          <!-- Switch Account -->
          <div class="domain-item" id="domain-switch-account-btn" style="color: var(--danger);">
            <div style="display: flex; align-items: center; gap: var(--space-2);">
              <svg width="14" height="14"><use href="#ph-sign-out"/></svg>
              <span>Switch Account</span>
            </div>
          </div>
        </div>
      </div>
    `;
  }

  init() {
    const pill = document.getElementById('domain-switcher-pill');
    const dropdown = document.getElementById('domain-switcher-dropdown');
    if (!pill || !dropdown) return;

    pill.addEventListener('click', (e) => {
      e.stopPropagation();
      dropdown.classList.toggle('show');
    });

    document.addEventListener('click', (e) => {
      if (!dropdown.contains(e.target) && !pill.contains(e.target)) {
        dropdown.classList.remove('show');
      }
    });

    // Domain navigation items
    dropdown.querySelectorAll('.domain-item[data-target-domain]').forEach(item => {
      item.addEventListener('click', () => {
        const target = item.dataset.targetDomain;
        this.switchDomain(target);
      });
    });

    // Switch account
    document.getElementById('domain-switch-account-btn')?.addEventListener('click', () => {
      auth.logout();
    });

    // Alt+D global hotkey
    window.addEventListener('keydown', (e) => {
      if (e.altKey && (e.key === 'd' || e.key === 'D')) {
        e.preventDefault();
        dropdown.classList.toggle('show');
      }
    });
  }

  switchDomain(domain) {
    localStorage.setItem(this.STORAGE_KEY, domain);
    if (domain === 'placement') {
      window.location.href = '/placement/dashboard.html';
    } else if (domain === 'institute') {
      window.location.href = '/institute/dashboard.html';
    } else {
      window.location.href = '/choose-domain.html';
    }
  }
}

const domainSwitcher = new DomainSwitcher();
export default domainSwitcher;
