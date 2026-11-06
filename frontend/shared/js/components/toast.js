/**
 * Placement Assistance Platform Design System
 * components/toast.js: Reusable Toast Notification Component
 */

class ToastManager {
  constructor() {
    this.container = null;
    this._ensureContainer();
  }

  _ensureContainer() {
    if (typeof document === 'undefined') return;
    let el = document.getElementById('pa-toast-container');
    if (!el) {
      el = document.createElement('div');
      el.id = 'pa-toast-container';
      el.setAttribute('role', 'region');
      el.setAttribute('aria-label', 'System Notifications');
      el.style.cssText = `
        position: fixed;
        bottom: 24px;
        right: 24px;
        z-index: 99999;
        display: flex;
        flex-direction: column;
        gap: 12px;
        max-width: 380px;
        pointer-events: none;
      `;
      document.body.appendChild(el);
    }
    this.container = el;
  }

  /**
   * Shows a toast notification
   * @param {string} message - Notification text
   * @param {'info'|'success'|'warning'|'error'} type - Style type
   * @param {number} duration - Auto dismiss timeout in ms
   */
  show(message, type = 'info', duration = 4000) {
    this._ensureContainer();
    if (!this.container) return;

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.style.cssText = `
      pointer-events: auto;
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px 16px;
      border-radius: 8px;
      box-shadow: 0 10px 25px -5px rgba(0,0,0,0.15), 0 8px 10px -6px rgba(0,0,0,0.1);
      font-size: 14px;
      font-weight: 500;
      color: #ffffff;
      background: ${this._getBgColor(type)};
      transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
      opacity: 0;
      transform: translateY(12px);
    `;

    const iconSvg = this._getIcon(type);
    toast.innerHTML = `
      <span style="display:flex;align-items:center;flex-shrink:0;">${iconSvg}</span>
      <span style="flex:1;line-height:1.4;">${this._escapeHtml(message)}</span>
      <button type="button" aria-label="Close notification" style="background:none;border:none;color:currentColor;opacity:0.75;cursor:pointer;padding:0;display:flex;align-items:center;">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 6L6 18M6 6l12 12"/></svg>
      </button>
    `;

    const closeBtn = toast.querySelector('button');
    const dismiss = () => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateY(8px)';
      setTimeout(() => toast.remove(), 250);
    };

    closeBtn.addEventListener('click', dismiss);
    this.container.appendChild(toast);

    // Trigger enter animation
    requestAnimationFrame(() => {
      toast.style.opacity = '1';
      toast.style.transform = 'translateY(0)';
    });

    if (duration > 0) {
      setTimeout(dismiss, duration);
    }
  }

  success(msg, dur) { this.show(msg, 'success', dur); }
  error(msg, dur) { this.show(msg, 'error', dur || 5000); }
  warning(msg, dur) { this.show(msg, 'warning', dur); }
  info(msg, dur) { this.show(msg, 'info', dur); }

  _getBgColor(type) {
    switch (type) {
      case 'success': return '#059669'; // Emerald 600
      case 'error': return '#dc2626';   // Red 600
      case 'warning': return '#d97706'; // Amber 600
      default: return '#2563eb';        // Blue 600
    }
  }

  _getIcon(type) {
    switch (type) {
      case 'success':
        return `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><path d="M22 4L12 14.01l-3-3"/></svg>`;
      case 'error':
        return `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>`;
      case 'warning':
        return `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>`;
      default:
        return `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>`;
    }
  }

  _escapeHtml(str) {
    if (!str) return '';
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }
}

const toast = new ToastManager();
export default toast;
