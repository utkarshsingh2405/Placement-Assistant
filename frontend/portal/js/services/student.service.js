/**
 * Placement Assistance Platform Design System
 * services/student.service.js: Academic Student Intelligence & Eligibility Logic
 * 
 * Separates data operations and business rules (eligibility, CGPA thresholds, backlogs)
 * from HTML page rendering.
 */

import api from '../core/api.js';
import state from '../core/state.js';

export class StudentService {
  /**
   * Fetches the profile of the currently signed-in student
   */
  async getProfile() {
    try {
      const data = await api.get('/api/students/me');
      state.patch('cache', { currentStudent: data });
      return data;
    } catch (err) {
      console.warn('[StudentService] Failed to fetch current student profile:', err.message);
      return null;
    }
  }

  /**
   * Fetches paginated students list with optional filters
   */
  async listStudents(params = {}) {
    const query = new URLSearchParams(params).toString();
    const endpoint = query ? `/api/students?${query}` : '/api/students';
    const response = await api.get(endpoint);
    const list = Array.isArray(response) ? response : (response.content || response.data?.content || []);
    state.patch('cache', { students: list });
    return list;
  }

  /**
   * Evaluates student eligibility for campus drives based on institutional policy
   * @param {Object} student - Student dossier or record
   * @param {Object} criteria - Drive rules { minCgpa, maxBacklogs, eligibleBranches }
   * @returns {Object} { isEligible: boolean, reasons: string[] }
   */
  evaluateEligibility(student, criteria = {}) {
    if (!student) return { isEligible: false, reasons: ['Student profile not found.'] };

    const reasons = [];
    const minCgpa = criteria.minCgpa !== undefined ? criteria.minCgpa : 6.0;
    const maxBacklogs = criteria.maxBacklogs !== undefined ? criteria.maxBacklogs : 0;
    const studentCgpa = parseFloat(student.cgpa || 0);
    const studentBacklogs = parseInt(student.activeBacklogs || student.backlogs || 0, 10);

    if (studentCgpa < minCgpa) {
      reasons.push(`CGPA (${studentCgpa.toFixed(2)}) is below minimum required (${minCgpa.toFixed(2)}).`);
    }

    if (studentBacklogs > maxBacklogs) {
      reasons.push(`Active backlogs (${studentBacklogs}) exceed maximum allowed (${maxBacklogs}).`);
    }

    if (Array.isArray(criteria.eligibleBranches) && criteria.eligibleBranches.length > 0) {
      const branch = (student.branch || student.department || '').trim().toUpperCase();
      const allowed = criteria.eligibleBranches.map(b => b.trim().toUpperCase());
      if (branch && !allowed.includes(branch) && !allowed.includes('ALL')) {
        reasons.push(`Branch '${branch}' is not in eligible branches list: [${allowed.join(', ')}].`);
      }
    }

    return {
      isEligible: reasons.length === 0,
      reasons
    };
  }

  /**
   * Computes attendance compliance percentage
   */
  calculateAttendanceCompliance(attendanceRecords = []) {
    if (!attendanceRecords || attendanceRecords.length === 0) return { rate: 100, isDefaulter: false };
    const present = attendanceRecords.filter(r => r.status === 'PRESENT' || r.status === 'OD').length;
    const rate = Math.round((present / attendanceRecords.length) * 100);
    return {
      rate,
      isDefaulter: rate < 75
    };
  }
}

const studentService = new StudentService();
export default studentService;

