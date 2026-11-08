/**
 * Placement Assistance Platform Design System
 * charts.js: Hand-crafted SVG Chart Helpers (Bar, Line, Ring, Gauge, Sparkline, Funnel)
 * Strict Rule: One hue ramp per chart, never rainbow. Tabular numbers. Pure SVG.
 */

export function renderRingChart({ value = 0, max = 100, size = 120, strokeWidth = 10, color = 'var(--accent)', label = '' }) {
  const radius = (size - strokeWidth) / 2;
  const circumference = 2 * Math.PI * radius;
  const percentage = Math.min(Math.max(value / max, 0), 1);
  const strokeDashoffset = circumference - (percentage * circumference);

  return `
    <div style="display: flex; flex-direction: column; align-items: center; justify-content: center; position: relative; width: ${size}px; height: ${size}px;">
      <svg width="${size}" height="${size}" viewBox="0 0 ${size} ${size}">
        <!-- Background Track -->
        <circle cx="${size / 2}" cy="${size / 2}" r="${radius}" fill="none" stroke="var(--surface-3)" stroke-width="${strokeWidth}"/>
        <!-- Progress Ring -->
        <circle cx="${size / 2}" cy="${size / 2}" r="${radius}" fill="none" stroke="${color}" stroke-width="${strokeWidth}"
          stroke-dasharray="${circumference}" stroke-dashoffset="${strokeDashoffset}" stroke-linecap="round"
          transform="rotate(-90 ${size / 2} ${size / 2})" style="transition: stroke-dashoffset var(--dur-deliberate) var(--ease-out);"/>
      </svg>
      <div style="position: absolute; text-align: center; line-height: 1.1;">
        <div style="font-size: 1.25rem; font-weight: 700; font-variant-numeric: tabular-nums; color: var(--ink);">${Math.round(value)}%</div>
        ${label ? `<div style="font-size: 10px; color: var(--muted); text-transform: uppercase;">${label}</div>` : ''}
      </div>
    </div>
  `;
}

export function renderAtsGauge({ score = 0, size = 180 }) {
  const radius = 70;
  const strokeWidth = 12;
  const circumference = Math.PI * radius; // Half circle
  const percentage = Math.min(Math.max(score / 100, 0), 1);
  const strokeDashoffset = circumference - (percentage * circumference);
  const color = score >= 75 ? 'var(--success)' : (score >= 50 ? 'var(--warning)' : 'var(--danger)');

  return `
    <div class="ats-gauge-wrapper" style="width: ${size}px; height: ${size * 0.6}px;">
      <svg width="${size}" height="${size * 0.6}" viewBox="0 0 180 110">
        <!-- Background Arc -->
        <path d="M 20 95 A 70 70 0 0 1 160 95" fill="none" stroke="var(--surface-3)" stroke-width="${strokeWidth}" stroke-linecap="round"/>
        <!-- Value Arc -->
        <path d="M 20 95 A 70 70 0 0 1 160 95" fill="none" stroke="${color}" stroke-width="${strokeWidth}"
          stroke-dasharray="${circumference}" stroke-dashoffset="${strokeDashoffset}" stroke-linecap="round"
          style="transition: stroke-dashoffset 800ms var(--ease-settle);"/>
      </svg>
      <div class="ats-score-num">${score}</div>
    </div>
  `;
}

export function renderSparkline({ data = [], width = 120, height = 36, color = 'var(--accent)' }) {
  if (!data || data.length < 2) return '';
  const min = Math.min(...data);
  const max = Math.max(...data);
  const range = max - min || 1;

  const points = data.map((val, idx) => {
    const x = (idx / (data.length - 1)) * (width - 4) + 2;
    const y = height - 4 - ((val - min) / range) * (height - 8);
    return `${x.toFixed(1)},${y.toFixed(1)}`;
  }).join(' ');

  return `
    <svg width="${width}" height="${height}" viewBox="0 0 ${width} ${height}" style="overflow: visible;">
      <polyline points="${points}" fill="none" stroke="${color}" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
    </svg>
  `;
}

export function renderBarChart({ data = [], height = 140, barColor = 'var(--accent)' }) {
  if (!data || data.length === 0) return '';
  const max = Math.max(...data.map(d => d.value)) || 1;

  return `
    <div style="display: flex; align-items: flex-end; gap: var(--space-3); height: ${height}px; padding-top: var(--space-4);">
      ${data.map(item => {
        const h = Math.round((item.value / max) * (height - 30));
        return `
          <div style="flex: 1; display: flex; flex-direction: column; align-items: center; gap: 4px;">
            <div style="font-size: 11px; font-variant-numeric: tabular-nums; color: var(--muted);">${item.value}</div>
            <div style="width: 100%; max-width: 32px; height: ${Math.max(h, 4)}px; background-color: ${barColor}; border-radius: 4px 4px 0 0; transition: height 400ms var(--ease-out);"></div>
            <div style="font-size: 10px; color: var(--muted); text-align: center; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 48px;">${item.label}</div>
          </div>
        `;
      }).join('')}
    </div>
  `;
}

export function renderFunnel({ stages = [] }) {
  if (!stages || stages.length === 0) return '';
  const max = stages[0]?.count || 1;

  return `
    <div style="display: flex; flex-direction: column; gap: var(--space-2);">
      ${stages.map((stg) => {
        const pct = Math.max(Math.round((stg.count / max) * 100), 8);
        return `
          <div>
            <div style="display: flex; justify-content: space-between; font-size: var(--text-xs); margin-bottom: 2px;">
              <span style="font-weight: 500;">${stg.name}</span>
              <span style="font-variant-numeric: tabular-nums; color: var(--muted);">${stg.count} candidates</span>
            </div>
            <div style="width: 100%; height: 18px; background: var(--surface-2); border-radius: 4px; overflow: hidden;">
              <div style="width: ${pct}%; height: 100%; background: var(--accent); border-radius: 4px; transition: width 500ms var(--ease-out);"></div>
            </div>
          </div>
        `;
      }).join('')}
    </div>
  `;
}

export function renderPerformanceTrendChart({ semesters = [], width = 580, height = 200, color = 'var(--institute)' }) {
  if (!semesters || semesters.length === 0) return '';
  const padding = { top: 25, right: 30, bottom: 35, left: 45 };
  const chartW = width - padding.left - padding.right;
  const chartH = height - padding.top - padding.bottom;

  const minVal = 6.0;
  const maxVal = 10.0;
  const range = maxVal - minVal;

  const getX = (idx) => padding.left + (idx / Math.max(semesters.length - 1, 1)) * chartW;
  const getY = (val) => padding.top + chartH - ((val - minVal) / range) * chartH;

  const points = semesters.map((s, idx) => ({
    x: getX(idx),
    y: getY(s.sgpa),
    sem: s.sem,
    sgpa: s.sgpa
  }));

  const lineD = points.map((p, i) => `${i === 0 ? 'M' : 'L'} ${p.x.toFixed(1)} ${p.y.toFixed(1)}`).join(' ');
  const areaD = `${lineD} L ${points[points.length - 1].x.toFixed(1)} ${(padding.top + chartH).toFixed(1)} L ${points[0].x.toFixed(1)} ${(padding.top + chartH).toFixed(1)} Z`;

  const y8 = getY(8.0);
  const y7 = getY(7.0);

  return `
    <div style="position: relative; width: 100%; max-width: ${width}px; margin: 0 auto;">
      <svg width="100%" height="${height}" viewBox="0 0 ${width} ${height}" style="overflow: visible;">
        <defs>
          <linearGradient id="sgpaGrad" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stop-color="${color}" stop-opacity="0.35"/>
            <stop offset="100%" stop-color="${color}" stop-opacity="0.02"/>
          </linearGradient>
        </defs>

        <!-- Grid Lines & Y Axis Labels -->
        <g stroke="var(--surface-3)" stroke-dasharray="3,3" stroke-width="1">
          <line x1="${padding.left}" y1="${getY(10.0)}" x2="${padding.left + chartW}" y2="${getY(10.0)}"/>
          <line x1="${padding.left}" y1="${getY(8.0)}" x2="${padding.left + chartW}" y2="${getY(8.0)}" stroke="var(--success)" stroke-opacity="0.4"/>
          <line x1="${padding.left}" y1="${getY(7.0)}" x2="${padding.left + chartW}" y2="${getY(7.0)}" stroke="var(--warning)" stroke-opacity="0.4"/>
          <line x1="${padding.left}" y1="${getY(6.0)}" x2="${padding.left + chartW}" y2="${getY(6.0)}"/>
        </g>

        <!-- Y Axis Text -->
        <g font-size="10" fill="var(--muted)" font-family="var(--font-mono)" text-anchor="end">
          <text x="${padding.left - 8}" y="${getY(10.0) + 3}">10.0</text>
          <text x="${padding.left - 8}" y="${getY(8.0) + 3}" fill="var(--success)">8.0</text>
          <text x="${padding.left - 8}" y="${getY(7.0) + 3}" fill="var(--warning)">7.0</text>
          <text x="${padding.left - 8}" y="${getY(6.0) + 3}">6.0</text>
        </g>

        <!-- Benchmark Labels -->
        <text x="${padding.left + chartW - 5}" y="${y8 - 5}" font-size="9" fill="var(--success)" text-anchor="end" font-weight="600">8.0 Distinction Line</text>
        <text x="${padding.left + chartW - 5}" y="${y7 - 5}" font-size="9" fill="var(--warning)" text-anchor="end" font-weight="600">7.0 Placement Cutoff</text>

        <!-- Area Fill -->
        <path d="${areaD}" fill="url(#sgpaGrad)" />

        <!-- Line Path -->
        <path d="${lineD}" fill="none" stroke="${color}" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" />

        <!-- Data Points & Labels -->
        ${points.map(p => `
          <g>
            <circle cx="${p.x.toFixed(1)}" cy="${p.y.toFixed(1)}" r="5" fill="var(--surface)" stroke="${color}" stroke-width="2.5" />
            <text x="${p.x.toFixed(1)}" y="${(p.y - 10).toFixed(1)}" font-size="11" font-weight="700" fill="var(--ink)" text-anchor="middle" font-family="var(--font-mono)">
              ${Number(p.sgpa).toFixed(2)}
            </text>
            <text x="${p.x.toFixed(1)}" y="${height - 10}" font-size="11" fill="var(--muted)" text-anchor="middle" font-weight="500">
              Sem ${p.sem}
            </text>
          </g>
        `).join('')}
      </svg>
    </div>
  `;
}

