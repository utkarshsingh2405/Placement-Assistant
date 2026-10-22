/**
 * Placement Assistance Platform Design System
 * core/theme.js: Master Light, Dark, System Theme Management Engine
 */

class ThemeManager {
  constructor() {
    this.STORAGE_KEY = 'pa_theme_preference';
    this._lastToggle = 0;

    // Self-contained, crisp SVG icons that do not depend on external SVG sprite loading latency
    this.SUN_SVG = `
      <svg width="16" height="16" viewBox="0 0 256 256" fill="currentColor" aria-hidden="true">
        <circle cx="128" cy="128" r="48"/>
        <line x1="128" y1="24" x2="128" y2="48" stroke="currentColor" stroke-width="24" stroke-linecap="round"/>
        <line x1="128" y1="208" x2="128" y2="232" stroke="currentColor" stroke-width="24" stroke-linecap="round"/>
        <line x1="24" y1="128" x2="48" y2="128" stroke="currentColor" stroke-width="24" stroke-linecap="round"/>
        <line x1="208" y1="128" x2="232" y2="128" stroke="currentColor" stroke-width="24" stroke-linecap="round"/>
        <line x1="54.5" y1="54.5" x2="71.5" y2="71.5" stroke="currentColor" stroke-width="24" stroke-linecap="round"/>
        <line x1="184.5" y1="184.5" x2="201.5" y2="201.5" stroke="currentColor" stroke-width="24" stroke-linecap="round"/>
        <line x1="54.5" y1="201.5" x2="71.5" y2="184.5" stroke="currentColor" stroke-width="24" stroke-linecap="round"/>
        <line x1="184.5" y1="71.5" x2="201.5" y2="54.5" stroke="currentColor" stroke-width="24" stroke-linecap="round"/>
      </svg>
    `;

    this.MOON_SVG = `
      <svg width="16" height="16" viewBox="0 0 256 256" fill="currentColor" aria-hidden="true">
        <path d="M216.7,152.6A96,96,0,1,1,103.4,39.3,80,80,0,0,0,216.7,152.6Z"/>
      </svg>
    `;

    this.init();
    this.setupListeners();
  }

  init() {
    let active = this.getStoredPreference();
    if (!active) {
      const docAttr = document.documentElement.getAttribute('data-theme');
      if (docAttr && ['light', 'dark'].includes(docAttr)) {
        active = docAttr;
      } else {
        active = this.getSystemPreference();
      }
    }
    this.setTheme(active, false);
  }

  getStoredPreference() {
    try {
      return localStorage.getItem(this.STORAGE_KEY);
    } catch (e) {
      return null;
    }
  }

  getSystemPreference() {
    if (typeof window !== 'undefined' && window.matchMedia) {
      return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    }
    return 'light';
  }

  getTheme() {
    return document.documentElement.getAttribute('data-theme') || this.getStoredPreference() || 'light';
  }

  setTheme(theme, persist = true) {
    const valid = ['light', 'dark', 'system'].includes(theme) ? theme : 'light';
    let target = valid;

    if (valid === 'system') {
      target = this.getSystemPreference();
    }

    document.documentElement.setAttribute('data-theme', target);
    try {
      document.documentElement.style.colorScheme = target;
    } catch (e) {}

    if (persist) {
      try {
        localStorage.setItem(this.STORAGE_KEY, valid);
      } catch (e) {}
    }

    this.updateToggleIcons(target);

    // Broadcast custom event for SVG charts, dashboards, or live canvases
    window.dispatchEvent(new CustomEvent('themechange', { detail: { theme: target } }));
  }

  toggleTheme() {
    const now = Date.now();
    // 120ms debounce lock to avoid double toggle when both delegated & inline listeners execute
    if (this._lastToggle && now - this._lastToggle < 120) {
      return this.getTheme();
    }
    this._lastToggle = now;

    const current = this.getTheme();
    const next = current === 'dark' ? 'light' : 'dark';
    this.setTheme(next, true);
    return next;
  }

  updateToggleIcons(theme) {
    const isDark = theme === 'dark';
    const svgContent = isDark ? this.SUN_SVG : this.MOON_SVG;
    const actionText = isDark ? 'Switch to Light Theme' : 'Switch to Dark Theme';

    // 1. Update all designated icon containers across the entire page
    const iconContainers = document.querySelectorAll('#theme-icon, .theme-icon, [data-theme-icon]');
    iconContainers.forEach(container => {
      container.innerHTML = svgContent;
    });

    // 2. Update all toggle buttons (attributes, accessible titles, and fallback content)
    const toggleButtons = document.querySelectorAll('#theme-btn, #theme-toggle-btn, [data-theme-toggle], .theme-toggle-btn');
    toggleButtons.forEach(btn => {
      btn.setAttribute('title', actionText);
      btn.setAttribute('aria-label', actionText);
      const hasIconContainer = btn.querySelector('#theme-icon, .theme-icon, [data-theme-icon]');
      if (!hasIconContainer) {
        const existingSvg = btn.querySelector('svg');
        if (existingSvg) {
          existingSvg.outerHTML = svgContent;
        }
      }
    });
  }

  setupListeners() {
    if (typeof window === 'undefined') return;

    // 1. Universal Delegated Click Listener: Works on any theme toggle button anywhere, anytime
    document.addEventListener('click', (e) => {
      const btn = e.target.closest('#theme-btn, #theme-toggle-btn, [data-theme-toggle], .theme-toggle-btn');
      if (btn) {
        e.preventDefault();
        this.toggleTheme();
      }
    });

    // 2. DOM Ready synchronization
    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', () => {
        this.updateToggleIcons(this.getTheme());
      });
    } else {
      setTimeout(() => this.updateToggleIcons(this.getTheme()), 0);
    }

    // 3. Real-Time Cross-Tab Synchronization
    window.addEventListener('storage', (e) => {
      if (e.key === this.STORAGE_KEY && e.newValue) {
        this.setTheme(e.newValue, false);
      }
    });

    // 4. OS / Browser System Theme Change Listener
    if (window.matchMedia) {
      window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
        if (!this.getStoredPreference()) {
          this.setTheme(e.matches ? 'dark' : 'light', false);
        }
      });
    }

    // 5. MutationObserver: Catch dynamically injected topbars, modals, or app shell layouts
    if (typeof MutationObserver !== 'undefined') {
      let scheduled = false;
      const observer = new MutationObserver((mutations) => {
        const needsUpdate = mutations.some(m =>
          Array.from(m.addedNodes).some(n =>
            n.nodeType === 1 && (
              n.id === 'theme-icon' ||
              n.id === 'theme-btn' ||
              n.id === 'theme-toggle-btn' ||
              n.querySelector?.('#theme-icon, #theme-btn, #theme-toggle-btn, [data-theme-toggle]')
            )
          )
        );
        if (needsUpdate && !scheduled) {
          scheduled = true;
          requestAnimationFrame(() => {
            this.updateToggleIcons(this.getTheme());
            scheduled = false;
          });
        }
      });

      const root = document.body || document.documentElement;
      if (root) {
        observer.observe(root, { childList: true, subtree: true });
      }
    }
  }
}

const theme = new ThemeManager();

if (typeof window !== 'undefined') {
  window.ThemeManager = theme;
  window.toggleTheme = () => theme.toggleTheme();
}

export default theme;
