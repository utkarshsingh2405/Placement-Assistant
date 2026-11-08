/**
 * Placement Assistance Platform Design System
 * core/auth.js: Multi-Pillar Authentication and Role Authorization
 */

import api from './api.js';

class AuthManager {
  constructor() {
    this.currentUser = this.loadStoredUser();
    this.bindStorageListener();
  }

  loadStoredUser() {
    try {
      const stored = localStorage.getItem('current_user');
      if (stored) return JSON.parse(stored);

      const token = api.getToken();
      if (token) {
        return this.parseToken(token);
      }
      return null;
    } catch {
      return null;
    }
  }

  parseToken(token) {
    try {
      const parts = token.split('.');
      if (parts.length !== 3) return null;
      const payload = JSON.parse(atob(parts[1].replace(/-/g, '+').replace(/_/g, '/')));
      return {
        userId: payload.uid || payload.userId,
        email: payload.sub,
        role: payload.role,
        name: payload.name || payload.sub,
        rollNo: payload.rollNo || null,
        companyId: payload.companyId || null
      };
    } catch {
      return null;
    }
  }

  bindStorageListener() {
    window.addEventListener('storage', (e) => {
      if (e.key === 'access_token' || e.key === 'current_user') {
        this.currentUser = this.loadStoredUser();
      }
    });
  }

  isAuthenticated() {
    return !!api.getToken();
  }

  getToken() {
    return api.getToken();
  }

  getUser() {
    return this.currentUser;
  }

  getRole() {
    return this.currentUser?.role || null;
  }

  hasRole(...roles) {
    const role = this.getRole();
    if (!role) return false;
    if (role === 'DIRECTOR' || role === 'ADMIN') return true;
    return roles.includes(role);
  }

  canManage() {
    const role = this.getRole();
    return role === 'DIRECTOR' || role === 'TPO' || role === 'ADMIN';
  }

  isAdmin() {
    return this.hasRole('ADMIN');
  }

  isTpo() {
    return this.canManage();
  }

  isTpoOrDirector() {
    return this.canManage();
  }

  isDirector() {
    const role = this.getRole();
    return role === 'DIRECTOR' || role === 'ADMIN';
  }

  isStudent() {
    return this.getRole() === 'STUDENT';
  }

  isCompany() {
    return this.getRole() === 'COMPANY';
  }

  async login(email, password) {
    const res = await api.post('/api/auth/login', { email, password });
    const data = res.data || res;
    api.setTokens(data.accessToken, data.refreshToken);
    this.currentUser = {
      userId: data.userId || data.uid,
      email: data.email,
      role: data.role,
      name: data.name,
      rollNo: data.rollNo || null,
      companyId: data.companyId || null
    };
    localStorage.setItem('current_user', JSON.stringify(this.currentUser));
    return this.currentUser;
  }

  async register(registrationData) {
    const res = await api.post('/api/auth/register', registrationData);
    const data = res.data || res;
    api.setTokens(data.accessToken, data.refreshToken);
    this.currentUser = {
      userId: data.userId || data.uid,
      email: data.email,
      role: data.role,
      name: data.name,
      rollNo: data.rollNo || null,
      companyId: null
    };
    localStorage.setItem('current_user', JSON.stringify(this.currentUser));
    return this.currentUser;
  }

  logout() {
    api.clearTokens();
    this.currentUser = null;
    window.location.href = '/login.html';
  }

  requireAuth(allowedRoles = []) {
    if (!this.isAuthenticated()) {
      const currentPath = encodeURIComponent(window.location.pathname + window.location.search);
      window.location.href = `/login.html?next=${currentPath}`;
      return false;
    }

    if (allowedRoles.length > 0 && !this.hasRole(...allowedRoles)) {
      window.location.href = '/choose-domain.html';
      return false;
    }

    return true;
  }
}

const auth = new AuthManager();
export default auth;
