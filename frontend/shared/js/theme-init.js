/**
 * Placement Assistance Platform Design System
 * theme-init.js: Zero-Flicker Early Theme Initializer
 * Executed synchronously in <head> to eliminate Flash of Unstyled Content (FOUC).
 */
(function() {
  function getTheme() {
    try {
      // 1. Check document.cookie first (seamless cross-port sharing on localhost)
      var m = document.cookie.match(/(?:^|; )pa_theme_preference=(dark|light)/);
      if (m && m[1]) return m[1];

      // 2. Check localStorage
      var ls = localStorage.getItem('pa_theme_preference');
      if (ls === 'dark' || ls === 'light') return ls;

      // 3. Check sessionStorage
      var ss = sessionStorage.getItem('pa_theme_preference');
      if (ss === 'dark' || ss === 'light') return ss;

      // 4. Check window.name (persists across all page reloads & file:// directory switches)
      if (window.name && window.name.indexOf('pa_theme:') !== -1) {
        var parts = window.name.split('pa_theme:')[1];
        if (parts) {
          var val = parts.split(' ')[0].trim();
          if (val === 'dark' || val === 'light') return val;
        }
      }
    } catch (e) {}

    // Default to 'light' for optimal, crisp enterprise white experience
    return 'light';
  }

  var active = getTheme();
  document.documentElement.setAttribute('data-theme', active);
  try {
    document.documentElement.style.colorScheme = active;
  } catch (e) {}
})();
