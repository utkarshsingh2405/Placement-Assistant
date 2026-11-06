/**
 * Placement Assistance Platform Design System
 * components/modal.js: Accessible Modal Dialog Component
 */

export class ModalComponent {
  /**
   * Opens an accessible dialog modal
   * @param {Object} options - { title, content, confirmText, cancelText, onConfirm }
   */
  static open({ title, content, confirmText = 'Confirm', cancelText = 'Cancel', onConfirm }) {
    const backdrop = document.createElement('div');
    backdrop.className = 'modal-backdrop';
    backdrop.style.cssText = `
      position: fixed;
      inset: 0;
      background: rgba(0, 0, 0, 0.5);
      backdrop-filter: blur(4px);
      z-index: 9999;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 16px;
      opacity: 0;
      transition: opacity 0.2s ease;
    `;

    const dialog = document.createElement('div');
    dialog.className = 'modal-dialog card';
    dialog.setAttribute('role', 'dialog');
    dialog.setAttribute('aria-modal', 'true');
    dialog.style.cssText = `
      background: var(--surface-primary, #ffffff);
      color: var(--text-primary, #0f172a);
      border-radius: 12px;
      max-width: 520px;
      width: 100%;
      padding: 24px;
      box-shadow: 0 20px 25px -5px rgba(0,0,0,0.1), 0 8px 10px -6px rgba(0,0,0,0.1);
      transform: scale(0.95);
      transition: transform 0.2s ease;
    `;

    dialog.innerHTML = `
      <div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:16px;">
        <h3 style="margin:0;font-size:18px;font-weight:600;">${title}</h3>
        <button type="button" class="btn-close" aria-label="Close" style="background:none;border:none;cursor:pointer;opacity:0.6;">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 6L6 18M6 6l12 12"/></svg>
        </button>
      </div>
      <div class="modal-body" style="font-size:14px;line-height:1.6;margin-bottom:24px;">
        ${content}
      </div>
      <div style="display:flex;align-items:center;justify-content:flex-end;gap:12px;">
        <button type="button" class="btn btn-secondary btn-cancel">${cancelText}</button>
        <button type="button" class="btn btn-primary btn-confirm">${confirmText}</button>
      </div>
    `;

    backdrop.appendChild(dialog);
    document.body.appendChild(backdrop);

    const close = () => {
      backdrop.style.opacity = '0';
      dialog.style.transform = 'scale(0.95)';
      setTimeout(() => backdrop.remove(), 200);
    };

    dialog.querySelector('.btn-close').addEventListener('click', close);
    dialog.querySelector('.btn-cancel').addEventListener('click', close);
    dialog.querySelector('.btn-confirm').addEventListener('click', async () => {
      if (typeof onConfirm === 'function') {
        const btn = dialog.querySelector('.btn-confirm');
        btn.disabled = true;
        btn.textContent = 'Processing...';
        try {
          await onConfirm();
          close();
        } catch (e) {
          btn.disabled = false;
          btn.textContent = confirmText;
        }
      } else {
        close();
      }
    });

    backdrop.addEventListener('click', (e) => {
      if (e.target === backdrop) close();
    });

    const keyListener = (e) => {
      if (e.key === 'Escape') {
        close();
        document.removeEventListener('keydown', keyListener);
      }
    };
    document.addEventListener('keydown', keyListener);

    requestAnimationFrame(() => {
      backdrop.style.opacity = '1';
      dialog.style.transform = 'scale(1)';
    });
  }
}

export default ModalComponent;

