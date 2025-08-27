/* site.js v3 */
(function () {
  // Run when DOM is ready (defer should ensure this, but just in case)
  const ready = (fn) =>
    document.readyState !== 'loading'
      ? fn()
      : document.addEventListener('DOMContentLoaded', fn);

  // Utility: show toast
  function showToast(msg, type = 'ok', ms = 3200) {
    const host = document.getElementById('toastHost');
    if (!host || !msg) return;
    const el = document.createElement('div');
    el.className = `toast ${type}`;
    el.textContent = msg;
    host.appendChild(el);
    setTimeout(() => el.remove(), ms);
  }

  // Theme
  function initTheme() {
    const root = document.documentElement;
    const key = 'theme';
    const saved = localStorage.getItem(key);

    if (saved === 'dark') root.classList.add('dark');
    if (saved === 'light') root.classList.remove('dark');

    const btn = document.getElementById('themeToggle');
    if (btn) {
      btn.addEventListener('click', () => {
        const makeDark = !root.classList.contains('dark');
        root.classList.toggle('dark', makeDark);
        localStorage.setItem(key, makeDark ? 'dark' : 'light');
      });
    }
  }

  // Query toasts (?success=... & ?error=...)
  function initToastsFromQuery() {
    const p = new URLSearchParams(window.location.search);
    if (p.get('success')) showToast(p.get('success'), 'ok');
    if (p.get('error')) showToast(p.get('error'), 'err');
  }

  // Confirm forms with data-confirm="..."
  function initConfirmForms() {
    document.addEventListener('submit', (e) => {
      const form = e.target;
      const msg = form?.dataset?.confirm;
      if (msg && !confirm(msg)) e.preventDefault();
    });
  }

  // Autosize textareas
  function initAutosizeTextareas() {
    const grow = (t) => {
      t.style.height = 'auto';
      t.style.height = (t.scrollHeight + 2) + 'px';
    };
    document.querySelectorAll('textarea').forEach((t) => {
      grow(t);
      t.addEventListener('input', () => grow(t));
    });
  }

  // Grid auto-fit page size (avoid empty last row)
  function initGridAutoSize() {
    const grid = document.querySelector('.grid');
    if (!grid) return;
    const card = grid.querySelector('.card');
    if (!card) return;

    // Rows per page (default 3) can be set per page: <body data-rows="3">
    const rowsWanted = parseInt(document.body.dataset.rows || '3', 10);

    // Measure after layout
    requestAnimationFrame(() => {
      const style = getComputedStyle(grid);
      const gap = parseFloat(style.gap || style.columnGap || 0);
      const gridW = grid.clientWidth;
      const cardW = card.getBoundingClientRect().width;

      if (!cardW || !gridW) return;

      const cols = Math.max(1, Math.floor((gridW + gap) / (cardW + gap)));
      const desiredSize = Math.max(1, cols * rowsWanted);

      const url = new URL(window.location.href);
      const currentSize = parseInt(url.searchParams.get('size') || '12', 10);

      if (!Number.isNaN(desiredSize) && desiredSize > 0 && currentSize !== desiredSize) {
        url.searchParams.set('size', String(desiredSize));
        url.searchParams.set('page', '0'); // reset to first page
        console.log('[site.js] grid auto-size -> reloading with size =', desiredSize);
        window.location.replace(url.toString());
      }
    });
  }

  // Unread notifications badge
  function initUnreadBadge() {
    const badge = document.getElementById('notifCount');
    if (!badge) return;
    fetch('/api/me/notifications/unread-count', { credentials: 'same-origin' })
      .then((r) => (r.ok ? r.json() : null))
      .then((data) => {
        if (!data) return;
        const n = Number(data.count || 0);
        if (n > 0) {
          badge.textContent = n > 99 ? '99+' : String(n);
          badge.hidden = false;
        } else {
          badge.hidden = true;
        }
      })
      .catch(() => {
        // ignore network errors; keep badge hidden
      });
  }

  // Password eye toggle (auto-wrap all password fields)
  function initPasswordToggles() {
    const pwInputs = Array.from(document.querySelectorAll('input[type="password"]'))
      .filter((i) => !i.closest('.pw-wrap') && !i.dataset.noToggle); // opt-out with data-no-toggle

    pwInputs.forEach((input) => {
      const parent = input.parentNode;
      if (!parent) return;

      // Wrap
      const wrap = document.createElement('div');
      wrap.className = 'pw-wrap';
      parent.insertBefore(wrap, input);
      wrap.appendChild(input);

      // Button
      const btn = document.createElement('button');
      btn.type = 'button';
      btn.className = 'pw-toggle';
      btn.setAttribute('aria-label', 'Show password');
      btn.textContent = '👁️';

      btn.addEventListener('click', () => {
        const show = input.type === 'password';
        input.type = show ? 'text' : 'password';
        btn.textContent = show ? '🙈' : '👁️';
        btn.setAttribute('aria-label', show ? 'Hide password' : 'Show password');
        input.focus({ preventScroll: true });
      });

      wrap.appendChild(btn);
    });

    // Optional: global helper for manual markup with <button onclick="togglePw(this)">
    window.togglePw = function (btn) {
      const input = btn?.parentNode?.querySelector('input');
      if (!input) return;
      const show = input.type === 'password';
      input.type = show ? 'text' : 'password';
      btn.textContent = show ? '🙈' : '👁️';
      btn.setAttribute('aria-label', show ? 'Hide password' : 'Show password');
      input.focus({ preventScroll: true });
    };
  }

  // Boot
  ready(() => {
    console.log('site.js loaded');
    initTheme();
    initToastsFromQuery();
    initConfirmForms();
    initAutosizeTextareas();
    initGridAutoSize();
    initUnreadBadge();
    initPasswordToggles();
  });
})();