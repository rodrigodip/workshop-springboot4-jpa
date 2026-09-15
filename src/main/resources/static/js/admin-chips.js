// Category chips for admin product forms (create + edit modal).
(function () {
  function chipsBox(select) {
    const form = select.closest('form');
    return form ? form.querySelector('[data-cat-chips]') : null;
  }

  function refreshEmpty(box) {
    const empty = box ? box.querySelector('[data-cat-empty]') : null;
    if (empty) empty.style.display = box.querySelector('[data-chip]') ? 'none' : '';
  }

  document.body.addEventListener('change', (e) => {
    const select = e.target.closest('[data-cat-select]');
    if (!select || !select.value) return;
    const box = chipsBox(select);
    if (!box) return;
    if (box.querySelector('[data-chip="' + select.value + '"]')) {
      select.value = '';
      return;
    }
    const chip = document.createElement('span');
    chip.setAttribute('data-chip', select.value);
    chip.className = 'flex gap-1 items-center bg-slate-900 border border-slate-700 rounded px-2 py-1';
    const label = select.options[select.selectedIndex].text;
    chip.innerHTML = '<input type="hidden" name="categoryIds"/><button type="button" class="text-slate-400 hover:text-white" data-chip-remove>✕</button>';
    chip.querySelector('input').value = select.value;
    chip.insertBefore(document.createTextNode(label + ' '), chip.firstChild);
    box.appendChild(chip);
    select.value = '';
    refreshEmpty(box);
  });

  document.body.addEventListener('click', (e) => {
    const rm = e.target.closest('[data-chip-remove]');
    if (!rm) return;
    const chip = rm.closest('[data-chip]');
    const box = chip ? chip.parentElement : null;
    if (chip) chip.remove();
    if (box) refreshEmpty(box);
  });
})();
