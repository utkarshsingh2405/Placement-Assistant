/**
 * Placement Assistance Platform Design System
 * core/state.js: Centralized Reactive Application State Management
 * 
 * Provides unified, reactive state across all 3 pillars (Portal, Institute, Placement).
 * Supports state slices, pub/sub change listeners, persistence, and cross-tab sync.
 */

class StateManager {
  constructor() {
    this._state = {
      auth: {
        user: null,
        token: null,
        role: null,
        isAuthenticated: false
      },
      domain: {
        active: this._detectInitialDomain(),
        available: ['portal', 'institute', 'placement']
      },
      theme: {
        current: this._detectInitialTheme()
      },
      ui: {
        loading: false,
        loadingMessage: '',
        activeModal: null,
        sidebarExpanded: true
      },
      cache: {
        students: [],
        drives: [],
        stats: {}
      }
    };

    this._listeners = new Map();
    this._initStorageSync();
  }

  _detectInitialDomain() {
    if (typeof window === 'undefined') return 'portal';
    const path = window.location.pathname;
    if (path.startsWith('/institute')) return 'institute';
    if (path.startsWith('/placement')) return 'placement';
    const docDomain = document.documentElement.getAttribute('data-domain');
    if (docDomain) return docDomain;
    return 'portal';
  }

  _detectInitialTheme() {
    if (typeof window === 'undefined') return 'light';
    try {
      const stored = localStorage.getItem('pa_theme_preference');
      if (stored === 'dark' || stored === 'light') return stored;
    } catch {}
    return 'light';
  }

  _initStorageSync() {
    if (typeof window === 'undefined') return;
    window.addEventListener('storage', (e) => {
      if (e.key === 'pa_theme_preference') {
        const newTheme = e.newValue || 'light';
        this.set('theme.current', newTheme);
      }
      if (e.key === 'current_user' || e.key === 'access_token') {
        this._syncAuthFromStorage();
      }
    });
    this._syncAuthFromStorage();
  }

  _syncAuthFromStorage() {
    if (typeof window === 'undefined') return;
    try {
      const token = localStorage.getItem('access_token');
      const userStr = localStorage.getItem('current_user');
      const user = userStr ? JSON.parse(userStr) : null;
      this.set('auth', {
        user: user,
        token: token,
        role: user ? user.role : null,
        isAuthenticated: !!token
      });
    } catch {
      // Ignored on parse error
    }
  }

  /**
   * Retrieves state by key path (e.g. 'auth.user' or 'domain')
   */
  get(path) {
    if (!path) return this._state;
    const parts = path.split('.');
    let current = this._state;
    for (const part of parts) {
      if (current === null || current === undefined) return undefined;
      current = current[part];
    }
    return current;
  }

  /**
   * Updates state at key path and notifies subscribers
   */
  set(path, value) {
    const parts = path.split('.');
    let current = this._state;

    for (let i = 0; i < parts.length - 1; i++) {
      const part = parts[i];
      if (!(part in current) || typeof current[part] !== 'object') {
        current[part] = {};
      }
      current = current[part];
    }

    const lastKey = parts[parts.length - 1];
    const oldValue = current[lastKey];
    current[lastKey] = value;

    this._notify(path, value, oldValue);
    this._notify('*', this._state, null);
  }

  /**
   * Shallow merge an object into a state slice
   */
  patch(path, partial) {
    const existing = this.get(path) || {};
    if (typeof existing === 'object' && typeof partial === 'object') {
      this.set(path, { ...existing, ...partial });
    } else {
      this.set(path, partial);
    }
  }

  /**
   * Subscribe to state changes at key path (or '*' for all state changes)
   * Returns an unsubscribe function.
   */
  subscribe(path, callback) {
    if (!this._listeners.has(path)) {
      this._listeners.set(path, new Set());
    }
    this._listeners.get(path).add(callback);

    // Initial trigger with current state
    try {
      callback(this.get(path), undefined);
    } catch (e) {
      console.warn(`[StateManager] Initial subscriber callback error for '${path}':`, e);
    }

    return () => {
      const set = this._listeners.get(path);
      if (set) {
        set.delete(callback);
        if (set.size === 0) {
          this._listeners.delete(path);
        }
      }
    };
  }

  _notify(path, newValue, oldValue) {
    // Notify exact listeners
    if (this._listeners.has(path)) {
      this._listeners.get(path).forEach(cb => {
        try {
          cb(newValue, oldValue);
        } catch (e) {
          console.error(`[StateManager] Subscriber error on '${path}':`, e);
        }
      });
    }

    // Notify parent paths if nested
    const parts = path.split('.');
    for (let i = parts.length - 1; i > 0; i--) {
      const parentPath = parts.slice(0, i).join('.');
      if (this._listeners.has(parentPath)) {
        const parentVal = this.get(parentPath);
        this._listeners.get(parentPath).forEach(cb => {
          try {
            cb(parentVal, null);
          } catch (e) {
            console.error(`[StateManager] Parent subscriber error on '${parentPath}':`, e);
          }
        });
      }
    }
  }

  /**
   * Shorthand helper to set active loading spinner state
   */
  setLoading(isLoading, message = '') {
    this.patch('ui', {
      loading: isLoading,
      loadingMessage: message
    });
  }
}

const state = new StateManager();

if (typeof window !== 'undefined') {
  window.__PA_STATE__ = state;
}

export default state;
