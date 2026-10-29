/**
 * Placed Badge Component.
 * Inquires the placement public API for student placement status by roll number.
 * Displays a gold "Placed at {Company}" badge on the student's profile page.
 * Silently fails and hides itself if the student is not placed or if placement is offline.
 */

export async function renderPlacedBadge(rollNo, containerId = 'placed-badge-container') {
  const container = typeof containerId === 'string' ? document.getElementById(containerId) : containerId;
  if (!container || !rollNo) return;

  try {
    const res = await fetch(`/placement/api/public/placements/by-roll/${encodeURIComponent(rollNo)}`);
    if (!res.ok) {
      container.innerHTML = '';
      return;
    }

    const body = await res.json();
    const data = body.data || body;

    if (data && data.isPlaced) {
      const company = data.companyName || 'Campus Recruiter';
      const pkg = data.packageLpa ? ` • ${data.packageLpa} LPA` : '';
      container.innerHTML = `
        <div class="d-inline-flex align-items-center gap-2 px-3 py-1 rounded-pill" style="background-color: var(--gold); color: #0F172A; font-weight: 700; font-size: 0.85rem; box-shadow: 0 2px 8px rgba(245, 158, 11, 0.35);">
          <svg width="16" height="16" fill="currentColor" viewBox="0 0 24 24">
            <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
          </svg>
          <span>Placed at ${escapeHtml(company)}${pkg}</span>
        </div>
      `;
    } else {
      container.innerHTML = '';
    }
  } catch (err) {
    // Silently ignore when placement pillar is unreachable
    container.innerHTML = '';
  }
}

function escapeHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

export default { renderPlacedBadge };
