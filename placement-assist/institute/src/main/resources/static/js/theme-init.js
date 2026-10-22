/**
 * Placement Assistance Platform Design System
 * theme-init.js: Zero-Flicker Early Theme Initializer
 * Executed synchronously in <head> to eliminate Flash of Unstyled Content (FOUC).
 */
(function() {
  try {
    var stored = localStorage.getItem('pa_theme_preference');
    var isDark = stored === 'dark' || (!stored && window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches);
    var active = isDark ? 'dark' : 'light';
    document.documentElement.setAttribute('data-theme', active);
    document.documentElement.style.colorScheme = active;
  } catch (e) {}
})();
