import theme from './core/theme.js';

if (typeof window !== 'undefined') {
  window.ThemeManager = theme;
  window.toggleTheme = () => theme.toggleTheme();
}

export default theme;
