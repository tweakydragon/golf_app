const fallbackPalette = [
  '#0d6efd',
  '#20c997',
  '#ffc107',
  '#dc3545',
  '#6610f2',
  '#6f42c1',
  '#fd7e14',
  '#198754',
  '#adb5bd'
];

const tokenPalette = [
  '--color-chart-1',
  '--color-chart-2',
  '--color-chart-3',
  '--color-chart-4',
  '--color-chart-5',
  '--color-chart-6',
  '--color-chart-7',
  '--color-chart-8',
  '--color-chart-9'
];

export const resolveToken = (token, fallback) => {
  if (typeof token !== 'string') {
    return fallback;
  }
  if (typeof window !== 'undefined' && window.getComputedStyle) {
    const styles = window.getComputedStyle(document.documentElement);
    const value = styles.getPropertyValue(token).trim();
    if (value) {
      return value;
    }
  }
  return fallback;
};

export const applyAlpha = (hex, alpha) => {
  if (typeof hex !== 'string') {
    return hex;
  }
  const normalized = hex.trim();
  const match = /^#?([a-fA-F0-9]{6})$/.exec(normalized);
  if (!match) {
    return normalized;
  }
  const intVal = parseInt(match[1], 16);
  const r = (intVal >> 16) & 255;
  const g = (intVal >> 8) & 255;
  const b = intVal & 255;
  return `rgba(${r}, ${g}, ${b}, ${alpha})`;
};

export const getChartPalette = (count) => {
  if (count <= 0) {
    return [];
  }

  let styles = null;
  if (typeof window !== 'undefined' && window.getComputedStyle) {
    styles = window.getComputedStyle(document.documentElement);
  }

  const resolved = [];
  for (let i = 0; i < count; i++) {
    const tokenIndex = i % tokenPalette.length;
    const token = tokenPalette[tokenIndex];
    let color = fallbackPalette[tokenIndex % fallbackPalette.length];

    if (styles) {
      const cssValue = styles.getPropertyValue(token).trim();
      if (cssValue) {
        color = cssValue;
      }
    }

    resolved.push(color);
  }

  return resolved;
};

export const getEmphasisColor = () => {
  return resolveToken('--color-primary', fallbackPalette[0]);
};
