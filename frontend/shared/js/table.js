import ui from './ui.js';

/**
 * Reusable data table renderer.
 */
export class DataTable {
  /**
   * @param {Object} options
   * @param {string} options.containerId - Container DOM element ID
   * @param {Array<Object>} options.columns - Column configs: { key, label, render(item, index) }
   * @param {Array<Object>} options.data - Array of row objects
   * @param {string} [options.emptyMessage] - Message when data is empty
   */
  static render({ containerId, columns, data, emptyMessage = 'No records found' }) {
    const container = document.getElementById(containerId);
    if (!container) return;

    if (!data || data.length === 0) {
      container.innerHTML = ui.renderEmpty(emptyMessage);
      return;
    }

    let thead = '<tr>';
    columns.forEach(col => {
      thead += `<th scope="col" class="${col.className || ''}">${ui.escapeHtml(col.label)}</th>`;
    });
    thead += '</tr>';

    let tbody = '';
    data.forEach((item, index) => {
      tbody += '<tr>';
      columns.forEach(col => {
        let cellContent = '';
        if (typeof col.render === 'function') {
          cellContent = col.render(item, index);
        } else {
          cellContent = ui.escapeHtml(item[col.key] !== undefined && item[col.key] !== null ? item[col.key] : '-');
        }
        tbody += `<td class="align-middle ${col.className || ''}">${cellContent}</td>`;
      });
      tbody += '</tr>';
    });

    container.innerHTML = `
      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
          <thead class="table-light">
            ${thead}
          </thead>
          <tbody>
            ${tbody}
          </tbody>
        </table>
      </div>
    `;
  }
}

export default DataTable;
