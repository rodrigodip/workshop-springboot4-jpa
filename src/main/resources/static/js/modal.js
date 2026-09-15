(function () {
  function clearMount(el) {
    if (el) el.innerHTML = '';
  }

  function mountOf(el) {
    return el ? el.closest('[data-modal-mount]') : null;
  }

  function closeAll() {
    document.querySelectorAll('[data-modal-mount]').forEach(clearMount);
  }

  document.body.addEventListener('click', (e) => {
    if (e.target.closest('[data-close-modal]')) {
      clearMount(mountOf(e.target));
      return;
    }
    const backdrop = e.target.closest('[data-modal-backdrop]');
    if (backdrop && e.target === backdrop) clearMount(mountOf(backdrop));
  });

  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') closeAll();
  });

  document.body.addEventListener('htmx:afterRequest', (e) => {
    if (e.target && e.target.closest && e.target.closest('[data-modal-form]') && e.detail.successful) {
      closeAll();
    }
  });
})();
