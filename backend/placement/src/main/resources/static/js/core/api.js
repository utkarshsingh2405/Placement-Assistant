/**
 * Placement Assistance Platform Design System
 * core/api.js: Centralized API Client with JWT Lifecycle & Context Path Resolution
 */

class ApiClient {
  constructor() {
    this.isRefreshing = false;
    this.refreshSubscribers = [];
  }

  getToken() {
    return localStorage.getItem('access_token');
  }

  getRefreshToken() {
    return localStorage.getItem('refresh_token');
  }

  setTokens(accessToken, refreshToken) {
    if (accessToken) localStorage.setItem('access_token', accessToken);
    if (refreshToken) localStorage.setItem('refresh_token', refreshToken);
  }

  clearTokens() {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('current_user');
  }

  getBaseUrl() {
    if (typeof window !== 'undefined') {
      if (window.__API_BASE__) return window.__API_BASE__.replace(/\/+$/, '');
      const stored = localStorage.getItem('pa_api_base');
      if (stored) return stored.replace(/\/+$/, '');
      const port = window.location.port;
      // When served on common standalone frontend dev ports (3000, 5000, 5173, 5500, 8000), default target is Gateway 8080
      if (port === '8080' || port === '8088') {
        try { localStorage.setItem('pa_gateway_port', port); } catch (e) {}
      }
      if (['3000', '5000', '5173', '5500', '8000'].includes(port)) {
        let gw = '8080';
        try { gw = localStorage.getItem('pa_gateway_port') || '8088'; } catch (e) {}
        return 'http://' + (window.location.hostname || 'localhost') + ':' + gw;
      }
      return window.location.origin;
    }
    return '';
  }

  getContextPrefix() {
    const path = window.location.pathname;
    if (path.startsWith('/institute')) return '/institute';
    if (path.startsWith('/placement')) return '/placement';
    const domain = document.documentElement.getAttribute('data-domain');
    if (domain === 'institute') return '/institute';
    if (domain === 'placement') return '/placement';
    return '';
  }

  buildUrl(endpoint) {
    if (endpoint.startsWith('http://') || endpoint.startsWith('https://')) {
      return endpoint;
    }

    const base = this.getBaseUrl();
    const prefix = this.getContextPrefix();
    let clean = endpoint.startsWith('/') ? endpoint : `/${endpoint}`;

    if (clean.startsWith('/institute') || clean.startsWith('/placement')) {
      return `${base}${clean}`;
    }

    // Portal Gateway Auth, Status, and Admin User endpoints must not be prefixed with /institute or /placement
    if (
      clean.startsWith('/api/auth') || 
      clean.startsWith('/auth') || 
      clean.startsWith('/api/status') || 
      clean.startsWith('/api/admin')
    ) {
      if (clean.startsWith('/auth')) clean = `/api${clean}`;
      if (typeof window !== 'undefined' && (window.location.port === '8081' || window.location.port === '8082')) {
        let gw = '8080'; try { gw = localStorage.getItem('pa_gateway_port') || '8088'; } catch (e) {}
        return 'http://' + window.location.hostname + ':' + gw + clean;
      }
      return `${base}${clean}`;
    }

    if (clean.startsWith('/actuator')) {
      return `${base}${prefix}${clean}`;
    }

    if (clean.startsWith('/api/') || clean === '/api') {
      return `${base}${prefix}${clean}`;
    }

    return `${base}${prefix}/api${clean}`;
  }

  async request(endpoint, options = {}) {
    const url = this.buildUrl(endpoint);
    const headers = options.headers || {};

    const token = this.getToken();
    if (token && !headers['Authorization']) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    if (!(options.body instanceof FormData) && !headers['Content-Type']) {
      headers['Content-Type'] = 'application/json';
    }

    const config = {
      ...options,
      headers
    };

    try {
      const response = await fetch(url, config);

      if (response.status === 401 || (response.status === 403 && this.getToken())) {
        const refreshed = await this.tryRefreshToken();
        if (refreshed) {
          headers['Authorization'] = `Bearer ${this.getToken()}`;
          return fetch(url, { ...options, headers }).then(res => this.handleResponse(res));
        } else {
          this.clearTokens();
          if (!window.location.pathname.includes('login.html') && !window.location.pathname.includes('register.html')) {
            const currentPath = encodeURIComponent(window.location.pathname + window.location.search);
            window.location.href = `/login.html?next=${currentPath}`;
          }
          throw new Error('Session expired. Please sign in again.');
        }
      }

      return await this.handleResponse(response);
    } catch (err) {
      if (err.message === 'Failed to fetch') {
        throw new Error('Network connection error. Service may be offline.');
      }
      throw err;
    }
  }

  async handleResponse(response) {
    const contentType = response.headers.get('content-type') || '';

    if (response.status === 204) {
      return null;
    }

    if (contentType.includes('application/json')) {
      const json = await response.json();
      if (!response.ok) {
        const msg = json.error?.message || json.message || `Request failed (${response.status})`;
        const err = new Error(msg);
        err.status = response.status;
        err.data = json;
        throw err;
      }
      return json.data !== undefined ? json.data : json;
    }

    if (contentType.includes('text/csv') || contentType.includes('application/octet-stream') || contentType.includes('application/pdf')) {
      if (!response.ok) {
        throw new Error(`Download failed with status ${response.status}`);
      }
      return await response.blob();
    }

    const text = await response.text();
    if (!response.ok) {
      throw new Error(text || `Request failed (${response.status})`);
    }
    return text;
  }

  async tryRefreshToken() {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) return false;

    if (this.isRefreshing) {
      return new Promise((resolve) => {
        this.refreshSubscribers.push(resolve);
      });
    }

    this.isRefreshing = true;

    try {
      const refreshUrl = this.buildUrl('/api/auth/refresh');
      const response = await fetch(refreshUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken })
      });

      if (!response.ok) {
        this.clearTokens();
        this.isRefreshing = false;
        this.refreshSubscribers.forEach(cb => cb(false));
        this.refreshSubscribers = [];
        return false;
      }

      const data = await response.json();
      const newTokens = data.data || data;
      this.setTokens(newTokens.accessToken, newTokens.refreshToken);

      this.isRefreshing = false;
      this.refreshSubscribers.forEach(cb => cb(true));
      this.refreshSubscribers = [];
      return true;
    } catch {
      this.isRefreshing = false;
      this.refreshSubscribers.forEach(cb => cb(false));
      this.refreshSubscribers = [];
      return false;
    }
  }

  get(endpoint, options = {}) {
    return this.request(endpoint, { ...options, method: 'GET' });
  }

  post(endpoint, body, options = {}) {
    const payload = body instanceof FormData ? body : JSON.stringify(body);
    return this.request(endpoint, { ...options, method: 'POST', body: payload });
  }

  put(endpoint, body, options = {}) {
    const payload = body instanceof FormData ? body : JSON.stringify(body);
    return this.request(endpoint, { ...options, method: 'PUT', body: payload });
  }

  patch(endpoint, body, options = {}) {
    const payload = body instanceof FormData ? body : JSON.stringify(body);
    return this.request(endpoint, { ...options, method: 'PATCH', body: payload });
  }

  delete(endpoint, options = {}) {
    return this.request(endpoint, { ...options, method: 'DELETE' });
  }
}

const api = new ApiClient();
export default api;
