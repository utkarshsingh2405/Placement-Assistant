/**
 * Placement Assistance Platform Design System
 * motion/flap-board.js: 3D Split-Flap Departure Board Mechanical Display
 */

class FlapBoard {
  constructor(containerId, options = {}) {
    this.container = typeof containerId === 'string' ? document.getElementById(containerId) : containerId;
    this.rows = options.rows || 3;
    this.cols = options.cols || 24;
    this.currentText = Array(this.rows).fill('').map(() => Array(this.cols).fill(' '));
    this.isPaused = false;
    this.initVisibilityListener();
  }

  initVisibilityListener() {
    document.addEventListener('visibilitychange', () => {
      this.isPaused = document.hidden;
    });
  }

  render() {
    if (!this.container) return;
    this.container.innerHTML = `
      <div class="flap-board">
        ${Array(this.rows).fill(0).map((_, r) => `
          <div class="flap-row" data-row="${r}">
            ${Array(this.cols).fill(0).map((_, c) => `
              <div class="flap-cell" data-col="${c}"> </div>
            `).join('')}
          </div>
        `).join('')}
      </div>
    `;
  }

  setText(rowIdx, text) {
    if (!this.container || this.isPaused) return;
    const isReduced = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    const clean = text.toUpperCase().padEnd(this.cols, ' ').substring(0, this.cols);
    const rowEl = this.container.querySelector(`.flap-row[data-row="${rowIdx}"]`);
    if (!rowEl) return;

    const cells = rowEl.querySelectorAll('.flap-cell');
    clean.split('').forEach((char, c) => {
      if (this.currentText[rowIdx][c] !== char) {
        this.currentText[rowIdx][c] = char;
        const cell = cells[c];
        if (cell) {
          if (isReduced) {
            cell.textContent = char;
          } else {
            // Mechanical flip animation
            setTimeout(() => {
              cell.style.transform = 'rotateX(90deg)';
              cell.style.transition = 'transform 100ms ease-in';
              setTimeout(() => {
                cell.textContent = char;
                cell.style.transform = 'rotateX(0deg)';
                cell.style.transition = 'transform 140ms cubic-bezier(0.32, 0.72, 0, 1)';
              }, 110);
            }, c * 35);
          }
        }
      }
    });
  }

  cyclePlacements(records = []) {
    if (!records || records.length === 0) return;
    let idx = 0;
    
    const update = () => {
      if (!this.isPaused) {
        const item = records[idx % records.length];
        this.setText(0, item.company || 'GOOGLE INDIA');
        this.setText(1, (item.role || 'SWE L3') + ' • ' + (item.package ? item.package + ' LPA' : '32 LPA'));
        this.setText(2, (item.student || 'OFFER CONFIRMED') + ' ' + (item.branch || 'CSE'));
        idx++;
      }
    };

    update();
    setInterval(update, 4500);
  }
}

export function initFlapBoard(containerId, records = []) {
  const board = new FlapBoard(containerId, { rows: 3, cols: 24 });
  board.render();
  if (records && records.length > 0) {
    const formatted = records.map(r => ({
      company: r.col1 || r.company || 'GOOGLE INDIA',
      role: r.col2 || r.role || 'SWE L3',
      package: r.col3 || r.package || '34.0',
      student: r.status || r.student || 'PLACED',
      branch: r.branch || 'CSE'
    }));
    board.cyclePlacements(formatted);
  }
  return board;
}

export default FlapBoard;

