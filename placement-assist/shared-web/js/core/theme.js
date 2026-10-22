/**
 * Placement Assistance Platform Design System
 * core/theme.js: Light, Dark, System Theme Management
 */

class ThemeManager {
  constructor() {
    this.STORAGE_KEY = 'pa_theme_preference';
    this.init();
  }

  init() {
    const saved = localStorage.getItem(this.STORAGE_KEY);
    if (saved) {
      this.setTheme(saved, false);
    } else {
      // Follow system preference
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      this.setTheme(prefersDark ? 'dark' : 'light', false);
    }

    // Listen for system changes if no explicit override
    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
      if (!localStorage.getItem(this.STORAGE_KEY)) {
        this.setTheme(e.matches ? 'dark' : 'light', false);
      }
    });
  }

  getTheme() {
    return document.documentElement.getAttribute('data-theme') || 'light';
  }

  setTheme(theme, persist = true) {
    const valid = ['light', 'dark', 'system'].includes(theme) ? theme : 'light';
    let target = valid;
    
    if (valid === 'system') {
      target = window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    }

    document.documentElement.setAttribute('data-theme', target);
    
    if (persist) {
      localStorage.setItem(this.STORAGE_KEY, valid);
    }

    this.updateToggleIcons(target);
    
    // Broadcast custom event for SVG charts or live canvases
    window.dispatchEvent(new CustomEvent('themechange', { detail: { theme: target } }));
  }

  toggleTheme() {
    const current = this.getTheme();
    const next = current === 'dark' ? 'light' : 'dark';
    this.setTheme(next, true);
    return next;
  }

  updateToggleIcons(theme) {
    const icon = document.getElementById('theme-icon');
    if (!icon) return;
    
    if (theme === 'dark') {
      icon.innerHTML = `
        <svg width="16" height="16" viewBox="0 0 256 256" fill="currentColor">
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
    } else {
      icon.innerHTML = `
        <svg width="16" height="16" viewBox="0 0 256 256" fill="currentColor">
          <path d="M216.7,152.6A96,96,0,1,1,103.4,39.3,80,80,0,0,0,216.7,152.6Z"/>
        </svg>
      `;
    }
  }
}

const theme = new ThemeManager();
export default theme;
