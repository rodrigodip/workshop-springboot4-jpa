(function () {
  let lastSection = null;

  function mark(btn) {
    const nav = btn.closest('nav');
    if (nav) nav.querySelectorAll('.subnav').forEach((b) => b.classList.remove('subnav-active'));
    btn.classList.add('subnav-active');
    lastSection = btn.textContent.trim();
  }

  document.body.addEventListener('click', (e) => {
    const btn = e.target.closest('#panel-admin .subnav');
    if (btn) mark(btn);
  });

  document.body.addEventListener('htmx:afterRequest', (e) => {
    if (e.target && e.target.id === 'admin-panel' && !lastSection) {
      const dash = document.querySelector('#panel-admin .subnav');
      if (dash) mark(dash);
    }
  });
})();
