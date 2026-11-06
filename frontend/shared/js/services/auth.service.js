/**
 * Placement Assistance Platform Design System
 * services/auth.service.js: Single Sign-On and Access Control Service
 * 
 * Manages JWT sessions, role verification, and login state synchronization.
 */

import api from '../core/api.js';
import authManager from '../core/auth.js';
import state from '../core/state.js';

export class AuthService {
  /**
   * Logs in a user with email and password
   */
  async login(email, password, domain = 'portal') {
    state.setLoading(true, 'Authenticating user...');
    try {
      const response = await api.post('/api/auth/login', { email, password });
      const authData = response.data || response;

      if (authData.accessToken) {
        api.setTokens(authData.accessToken, authData.refreshToken);
      }

      const user = authData.user || authManager.parseToken(authData.accessToken);
      if (user) {
        localStorage.setItem('current_user', JSON.stringify(user));
        state.set('auth', {
          user,
          token: authData.accessToken,
          role: user.role,
          isAuthenticated: true
        });
      }

      state.setLoading(false);
      return { success: true, user, redirect: this.getPostLoginRedirect(user, domain) };
    } catch (err) {
      state.setLoading(false);
      throw err;
    }
  }

  /**
   * Registers a new student account
   */
  async register(registrationData) {
    state.setLoading(true, 'Registering new account...');
    try {
      const response = await api.post('/api/auth/register', registrationData);
      state.setLoading(false);
      return response;
    } catch (err) {
      state.setLoading(false);
      throw err;
    }
  }

  /**
   * Clears tokens and signs out the user
   */
  logout() {
    api.clearTokens();
    state.set('auth', {
      user: null,
      token: null,
      role: null,
      isAuthenticated: false
    });
    window.location.href = '/login.html';
  }

  /**
   * Determines appropriate target URL after login
   */
  getPostLoginRedirect(user, preferredDomain) {
    if (preferredDomain === 'institute') return '/institute/dashboard.html';
    if (preferredDomain === 'placement') return '/placement/dashboard.html';
    return '/choose-domain.html';
  }

  /**
   * Reactive check if user has any of the specified roles
   */
  hasRole(...roles) {
    return authManager.hasRole(...roles);
  }

  /**
   * Gets current user object
   */
  getUser() {
    return state.get('auth.user') || authManager.getUser();
  }
}

const authService = new AuthService();
export default authService;
