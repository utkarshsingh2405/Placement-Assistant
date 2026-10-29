/**
 * Placement Assistance Platform Design System
 * shell/command-palette.js: Quick Command & Search Palette (Ctrl/Cmd + K)
 */

import theme from '../core/theme.js';
import domainSwitcher from './domain-switcher.js';

class CommandPalette {
  constructor() {
    this.dialog = null;
    this.isOpen = false;
    this.commands = [
      { id: 'home', title: 'Go to Domain Gateway', category: 'Navigation', icon: 'ph-building', action: () => window.location.href = '/choose-domain.html' },
      { id: 'inst-dash', title: 'Open Institute Dashboard', category: 'Navigation', icon: 'ph-building', action: () => window.location.href = '/institute/dashboard.html' },
      { id: 'plac-dash', title: 'Open Placement Dashboard', category: 'Navigation', icon: 'ph-briefcase', action: () => window.location.href = '/placement/dashboard.html' },
      { id: 'students', title: 'Search Student Directory', category: 'Navigation', icon: 'ph-users', action: () => window.location.href = '/institute/pages/1-students/student-list.html' },
      { id: 'drives', title: 'Browse Recruitment Drives', category: 'Navigation', icon: 'ph-briefcase', action: () => window.location.href = '/placement/pages/1-drives/drive-list.html' },
      { id: 'notices', title: 'Check Placement Notices', category: 'Navigation', icon: 'ph-broadcast', action: () => window.location.href = '/placement/pages/5-live-updates/live-feed.html' },
      { id: 'theme', title: 'Toggle Light / Dark Mode', category: 'Preferences', icon: 'ph-moon', action: () => theme.toggleTheme() },
      { id: 'switch', title: 'Switch Domain (Alt+D)', category: 'Preferences', icon: 'ph-arrow-right', action: () => domainSwitcher.switchDomain(domainSwitcher.getCurrentDomain() === 'placement' ? 'institute' : 'placement') }
    ];
    this.init();
  }

  init() {
    window.addEventListener('keydown', (e) => {
      if ((e.ctrlKey || e.metaKey) && (e.key === 'k' || e.key === 'K')) {
        e.preventDefault();
        this.toggle();
      }
    });
  }

  toggle() {
    if (this.isOpen) {
      this.close();
    } else {
      this.open();
    }
  }

  open() {
    if (!this.dialog) {
      this.dialog = document.createElement('dialog');
      this.dialog.className = 'command-palette-dialog';
      this.dialog.innerHTML = `
        <div class="palette-input-wrap">
          <svg width="20" height="20" style="color: var(--muted);"><use href="#ph-magnifying-glass"/></svg>
          <input type="text" class="palette-input" id="palette-search" placeholder="Type a command or destination..." autocomplete="off">
          <kbd style="font-size: 11px; background: var(--surface-2); padding: 2px 6px; border-radius: 4px; color: var(--muted);">ESC</kbd>
        </div>
        <div class="palette-results" id="palette-results"></div>
      `;
      document.body.appendChild(this.dialog);

      const input = this.dialog.querySelector('#palette-search');
      input.addEventListener('input', () => this.renderResults(input.value.trim()));
      
      this.dialog.addEventListener('cancel', () => this.close());
      this.dialog.addEventListener('click', (e) => {
        if (e.target === this.dialog) this.close();
      });
    }

    this.dialog.showModal();
    this.isOpen = true;
    this.renderResults('');
    this.dialog.querySelector('#palette-search').focus();
  }

  close() {
    if (this.dialog && this.isOpen) {
      this.dialog.close();
      this.isOpen = false;
    }
  }

  renderResults(query) {
    const resultsContainer = this.dialog.querySelector('#palette-results');
    const filtered = this.commands.filter(cmd => 
      !query || cmd.title.toLowerCase().includes(query.toLowerCase()) || cmd.category.toLowerCase().includes(query.toLowerCase())
    );

    if (filtered.length === 0) {
      resultsContainer.innerHTML = `
        <div style="padding: var(--space-6); text-align: center; color: var(--muted); font-size: var(--text-sm);">
          No matching commands found.
        </div>
      `;
      return;
    }

    resultsContainer.innerHTML = filtered.map((cmd, idx) => `
      <div class="palette-item ${idx === 0 ? 'active' : ''}" data-cmd-id="${cmd.id}">
        <svg width="18" height="18" style="color: var(--accent);"><use href="#${cmd.icon}"/></svg>
        <div style="flex: 1;">
          <div style="font-weight: 500;">${cmd.title}</div>
          <div style="font-size: 11px; color: var(--muted);">${cmd.category}</div>
        </div>
        <kbd style="font-size: 10px; color: var(--muted);">↵</kbd>
      </div>
    `).join('');

    resultsContainer.querySelectorAll('.palette-item').forEach(item => {
      item.addEventListener('click', () => {
        const cmd = this.commands.find(c => c.id === item.dataset.cmdId);
        if (cmd) {
          this.close();
          cmd.action();
        }
      });
    });
  }
}

const commandPalette = new CommandPalette();
export default commandPalette;
