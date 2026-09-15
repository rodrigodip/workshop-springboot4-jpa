document.addEventListener('DOMContentLoaded', () => {
  const tabs = ['client', 'admin'];
  const initial = document.body.dataset.initialTab || 'client';
  const btns = Object.fromEntries(tabs.map(t => [t, document.getElementById('tab-btn-' + t)]));
  const panels = Object.fromEntries(tabs.map(t => [t, document.getElementById('panel-' + t)]));

  function show(tab) {
    tabs.forEach(t => {
      const active = t === tab;
      panels[t].hidden = !active;
      btns[t].setAttribute('aria-selected', active ? 'true' : 'false');
      btns[t].classList.toggle('tab-active', active);
    });
    history.replaceState(null, '', '?tab=' + tab);
  }

  tabs.forEach(t => btns[t].addEventListener('click', () => {
    show(t);
    if (t === 'admin' && window.htmx) {
      const panel = document.getElementById('admin-panel');
      if (panel && panel.dataset.loaded !== '1') {
        panel.dataset.loaded = '1';
        htmx.ajax('GET', '/fragments/admin/dashboard', { target: '#admin-panel', swap: 'innerHTML' });
      }
    }
  }));

  show(tabs.includes(initial) ? initial : 'client');

  document.body.addEventListener('htmx:responseError', (e) => {
    const target = e.detail.target;
    if (target) target.innerHTML = '<div class="err">Request failed (' + e.detail.xhr.status + '). Check the JSON API is up.</div>';
  });
});
