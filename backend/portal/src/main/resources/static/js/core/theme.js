/**
 * Placement Assistance Platform Design System
 * core/theme.js: Master Light, Dark, System Theme Management Engine
 */

class ThemeManager {
  constructor() {
    this.STORAGE_KEY = 'pa_theme_preference';
    this._lastToggleTick = 0;

    // Self-contained, crisp SVG icons with pointer-events: none to guarantee reliable click targeting
    this.SUN_SVG = `
      <svg width="16" height="16" viewBox="0 0 256 256" fill="currentColor" aria-hidden="true" style="pointer-events: none; display: block;">
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
      <svg width="16" height="16" viewBox="0 0 256 256" fill="currentColor" aria-hidden="true" style="pointer-events: none; display: block;">
        <path d="M216.7,152.6A96,96,0,1,1,103.4,39.3,80,80,0,0,0,216.7,152.6Z"/>
      </svg>
    `;

    this.init();
    this.setupListeners();
  }

  init() {
    const active = this.getStoredPreference() || document.documentElement.getAttribute('data-theme') || 'light';
    this.setTheme(active, false);
  }

  getStoredPreference() {
    try {
      // 1. Check document.cookie first (seamless cross-port sharing on localhost)
      const m = document.cookie.match(new RegExp('(?:^|; )' + this.STORAGE_KEY + '=(dark|light)'));
      if (m && m[1]) return m[1];

      // 2. Check localStorage
      const ls = localStorage.getItem(this.STORAGE_KEY);
      if (ls === 'dark' || ls === 'light') return ls;

      // 3. Check sessionStorage
      const ss = sessionStorage.getItem(this.STORAGE_KEY);
      if (ss === 'dark' || ss === 'light') return ss;

      // 4. Check window.name (persists across reloads & directory navigations)
      if (window.name && window.name.indexOf('pa_theme:') !== -1) {
        const parts = window.name.split('pa_theme:')[1];
        if (parts) {
          const val = parts.split(' ')[0].trim();
          if (val === 'dark' || val === 'light') return val;
        }
      }
    } catch (e) {}
    return null;
  }

  getSystemPreference() {
    return 'light'; // Clean enterprise baseline
  }

  getTheme() {
    return document.documentElement.getAttribute('data-theme') || this.getStoredPreference() || 'light';
  }

  setTheme(theme, persist = true) {
    const valid = (theme === 'dark') ? 'dark' : 'light';

    document.documentElement.setAttribute('data-theme', valid);
    try {
      document.documentElement.style.colorScheme = valid;
    } catch (e) {}

    if (persist) {
      try {
        localStorage.setItem(this.STORAGE_KEY, valid);
      } catch (e) {}
      try {
        sessionStorage.setItem(this.STORAGE_KEY, valid);
      } catch (e) {}
      try {
        document.cookie = `${this.STORAGE_KEY}=${valid}; path=/; max-age=31536000; SameSite=Lax`;
      } catch (e) {}
      try {
        const cleanName = (window.name || '').replace(/pa_theme:(dark|light)/g, '').trim();
        window.name = `${cleanName} pa_theme:${valid}`.trim();
      } catch (e) {}
    }

    this.updateToggleIcons(valid);

    // Broadcast custom event for SVG charts, dashboards, or live canvases
    window.dispatchEvent(new CustomEvent('themechange', { detail: { theme: valid } }));
  }

  toggleTheme(e) {
    if (e) {
      if (e.__themeToggleHandled) return this.getTheme();
      e.__themeToggleHandled = true;
      if (typeof e.preventDefault === 'function') e.preventDefault();
      if (typeof e.stopPropagation === 'function') e.stopPropagation();
    }

    // Atomic tick guard: eliminates duplicate executions from the same user click/tick
    const currentTick = (typeof performance !== 'undefined' && performance.now) ? performance.now() : Date.now();
    if (this._lastToggleTick && (currentTick - this._lastToggleTick < 80)) {
      return this.getTheme();
    }
    this._lastToggleTick = currentTick;

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
      btn.setAttribute('data-current-theme', isDark ? 'dark' : 'light');
      const hasIconContainer = btn.querySelector('#theme-icon, .theme-icon, [data-theme-icon]');
      if (!hasIconContainer) {
        const existingSvg = btn.querySelector('svg');
        if (existingSvg) {
          existingSvg.outerHTML = svgContent;
        }
      }
    });
  }

  _findThemeButton(target) {
    if (!target) return null;
    const el = target.nodeType === 1 ? target : target.parentElement;
    if (!el || typeof el.closest !== 'function') return null;
    return el.closest('#theme-btn, #theme-toggle-btn, [data-theme-toggle], .theme-toggle-btn');
  }

  setupListeners() {
    if (typeof window === 'undefined') return;

    // 1. Capture-phase delegated click listener: reliably intercepts theme buttons before bubbling quirks
    document.addEventListener('click', (e) => {
      const btn = this._findThemeButton(e.target);
      if (btn) {
        this.toggleTheme(e);
      }
    }, { capture: true });

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

    // 4. MutationObserver: Catch dynamically injected topbars, modals, or app shell layouts
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
  window.toggleTheme = (e) => theme.toggleTheme(e);
}

export default theme;
