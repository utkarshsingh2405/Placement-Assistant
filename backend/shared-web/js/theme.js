import theme from './core/theme.js';

if (typeof window !== 'undefined') {
  window.ThemeManager = theme;
  window.toggleTheme = (e) => theme.toggleTheme(e);
}

export default theme;
