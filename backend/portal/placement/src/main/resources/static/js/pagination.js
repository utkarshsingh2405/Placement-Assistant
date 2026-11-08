/**
 * Reusable pagination component.
 */
export class Pagination {
  /**
   * @param {Object} options
   * @param {string} options.containerId
   * @param {number} options.currentPage - 0-indexed
   * @param {number} options.totalPages
   * @param {number} options.totalElements
   * @param {number} options.pageSize
   * @param {Function} options.onPageChange - callback(pageNumber)
   */
  static render({ containerId, currentPage = 0, totalPages = 1, totalElements = 0, pageSize = 10, onPageChange }) {
    const container = document.getElementById(containerId);
    if (!container) return;

    if (totalPages <= 1 && totalElements === 0) {
      container.innerHTML = '';
      return;
    }

    const startItem = totalElements === 0 ? 0 : currentPage * pageSize + 1;
    const endItem = Math.min((currentPage + 1) * pageSize, totalElements);

    let pageItems = '';
    const maxVisible = 5;
    let startPage = Math.max(0, currentPage - Math.floor(maxVisible / 2));
    let endPage = Math.min(totalPages - 1, startPage + maxVisible - 1);

    if (endPage - startPage + 1 < maxVisible) {
      startPage = Math.max(0, endPage - maxVisible + 1);
    }

    for (let p = startPage; p <= endPage; p++) {
      pageItems += `
        <li class="page-item ${p === currentPage ? 'active' : ''}">
          <button class="page-link" data-page="${p}">${p + 1}</button>
        </li>
      `;
    }

    container.innerHTML = `
      <div class="d-flex flex-column flex-sm-row justify-content-between align-items-center gap-2 p-3 border-top">
        <div class="text-muted small">
          Showing <strong>${startItem}</strong> to <strong>${endItem}</strong> of <strong>${totalElements}</strong> entries
        </div>
        <nav aria-label="Page navigation">
          <ul class="pagination pagination-sm mb-0">
            <li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
              <button class="page-link" data-page="${currentPage - 1}" aria-label="Previous">&laquo;</button>
            </li>
            ${pageItems}
            <li class="page-item ${currentPage >= totalPages - 1 ? 'disabled' : ''}">
              <button class="page-link" data-page="${currentPage + 1}" aria-label="Next">&raquo;</button>
            </li>
          </ul>
        </nav>
      </div>
    `;

    container.querySelectorAll('button[data-page]').forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.preventDefault();
        const targetPage = parseInt(btn.getAttribute('data-page'), 10);
        if (!isNaN(targetPage) && targetPage >= 0 && targetPage < totalPages && targetPage !== currentPage) {
          if (typeof onPageChange === 'function') {
            onPageChange(targetPage);
          }
        }
      });
    });
  }
}

export default Pagination;
