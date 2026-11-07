/**
 * Placement Assistance Platform Design System
 * components/stat-card.js: Reusable KPI Metric Card Component
 */

export class StatCardComponent {
  /**
   * Generates HTML markup for a KPI metric card
   * @param {Object} props - { id, title, value, subtext, trend, icon, accentColor }
   */
  static render({ id, title, value, subtext = '', trend = null, icon = 'chart', accentColor = 'blue' }) {
    let trendHtml = '';
    if (trend) {
      const isPositive = trend > 0;
      const trendColor = isPositive ? 'var(--emerald-600, #059669)' : 'var(--red-600, #dc2626)';
      const arrow = isPositive ? '↑' : '↓';
      trendHtml = `<span style="font-size:12px;font-weight:600;color:${trendColor};margin-left:8px;">${arrow} ${Math.abs(trend)}%</span>`;
    }

    return `
      <div class="kpi-card card card-hover" ${id ? `id="${id}"` : ''} style="display:flex;flex-direction:column;gap:8px;padding:20px;border-radius:12px;position:relative;overflow:hidden;">
        <div style="display:flex;align-items:center;justify-content:space-between;">
          <span style="font-size:13px;font-weight:600;color:var(--text-secondary,#64748b);text-transform:uppercase;letter-spacing:0.05em;">${title}</span>
          <span class="icon-wrap icon-wrap-${accentColor}" style="width:36px;height:36px;border-radius:8px;display:flex;align-items:center;justify-content:center;background:var(--accent-subtle, rgba(37,99,235,0.1));color:var(--accent-primary, #2563eb);">
            <svg width="20" height="20"><use href="#icon-${icon}" /></svg>
          </span>
        </div>
        <div style="display:flex;align-items:baseline;">
          <span class="kpi-value" style="font-size:28px;font-weight:700;letter-spacing:-0.02em;color:var(--text-primary,#0f172a);">${value}</span>
          ${trendHtml}
        </div>
        ${subtext ? `<span style="font-size:12px;color:var(--text-muted,#94a3b8);">${subtext}</span>` : ''}
      </div>
    `;
  }
}

export default StatCardComponent;

