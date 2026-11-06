/**
 * Placement Assistance Platform Design System
 * services/drive.service.js: Campus Recruitment Drive Operations & Logic
 * 
 * Centralizes drive querying, round transitions, and eligibility checks.
 */

import api from '../core/api.js';
import state from '../core/state.js';
import studentService from './student.service.js';

export class DriveService {
  /**
   * Fetches recruitment drives with optional status filter
   */
  async listDrives(params = {}) {
    const query = new URLSearchParams(params).toString();
    const endpoint = query ? `/api/drives?${query}` : '/api/drives';
    const response = await api.get(endpoint);
    const list = Array.isArray(response) ? response : (response.content || response.data || []);
    state.patch('cache', { drives: list });
    return list;
  }

  /**
   * Fetches single drive by ID
   */
  async getDrive(id) {
    const drive = await api.get(`/api/drives/${id}`);
    return drive;
  }

  /**
   * Filters drives that are currently active or upcoming
   */
  filterActiveDrives(drives = []) {
    const now = new Date();
    return drives.filter(d => {
      const status = (d.status || '').toUpperCase();
      if (status === 'COMPLETED' || status === 'CANCELLED') return false;
      return true;
    });
  }

  /**
   * Verifies if a student can register for a specific drive
   */
  canStudentApply(student, drive) {
    if (!drive) return { canApply: false, reason: 'Drive details missing.' };
    
    // Check drive registration status
    const status = (drive.status || '').toUpperCase();
    if (status !== 'ACTIVE' && status !== 'REGISTRATION_OPEN' && status !== 'UPCOMING') {
      return { canApply: false, reason: `Drive registration is closed (status: ${status}).` };
    }

    // Evaluate academic criteria
    const criteria = {
      minCgpa: drive.minCgpa || 6.0,
      maxBacklogs: drive.maxBacklogs || 0,
      eligibleBranches: drive.eligibleBranches || drive.eligibleBranchesList || []
    };

    const evalResult = studentService.evaluateEligibility(student, criteria);
    if (!evalResult.isEligible) {
      return { canApply: false, reason: evalResult.reasons.join(' ') };
    }

    return { canApply: true, reason: 'Eligible to apply.' };
  }
}

const driveService = new DriveService();
export default driveService;
