(function () {
  const cart = [];

  function money(n) {
    return (Math.round(n * 100) / 100).toFixed(2);
  }

  function cartTotal() {
    return cart.reduce((sum, it) => sum + it.price * it.qty, 0);
  }

  function render() {
    const list = document.getElementById('cart-items');
    const hidden = document.getElementById('cart-hidden-inputs');
    const totalEl = document.getElementById('cart-total');
    const placeBtn = document.getElementById('cart-place');
    if (!list || !hidden) return;
    list.innerHTML = '';
    hidden.innerHTML = '';
    if (cart.length === 0) {
      list.innerHTML = '<p class="text-sm text-slate-400" data-cart-empty>Cart is empty — add products from the catalog.</p>';
      if (totalEl) totalEl.hidden = true;
      if (placeBtn) placeBtn.disabled = true;
      return;
    }
    cart.forEach((it) => {
      const row = document.createElement('div');
      row.className = 'grid grid-cols-[1fr_auto] gap-2 items-center bg-slate-900 border border-slate-700 rounded px-2 py-1 text-sm';
      row.innerHTML = '<span></span><button type="button" class="btn btn-ghost" data-cart-remove>✕</button>';
      row.querySelector('span').textContent = it.name + ' · $' + money(it.price) + ' × ' + it.qty + ' = $' + money(it.price * it.qty);
      row.querySelector('[data-cart-remove]').dataset.cartRemove = it.id;
      list.appendChild(row);

      const pid = document.createElement('input');
      pid.type = 'hidden'; pid.name = 'productId'; pid.value = it.id;
      const qty = document.createElement('input');
      qty.type = 'hidden'; qty.name = 'quantity'; qty.value = it.qty;
      hidden.appendChild(pid); hidden.appendChild(qty);
    });
    if (totalEl) {
      totalEl.hidden = false;
      const span = totalEl.querySelector('[data-cart-total]');
      if (span) span.textContent = money(cartTotal());
    }
    if (placeBtn) placeBtn.disabled = false;
  }

  document.body.addEventListener('click', (e) => {
    const add = e.target.closest('[data-add-to-cart]');
    if (add) {
      const id = add.getAttribute('data-add-to-cart');
      const qtyInput = document.getElementById('qty-' + id);
      let qty = qtyInput ? parseInt(qtyInput.value, 10) : 1;
      if (!Number.isFinite(qty) || qty < 1) qty = 1;
      const found = cart.find((it) => String(it.id) === String(id));
      if (found) {
        found.qty += qty;
      } else {
        cart.push({
          id: id,
          name: add.getAttribute('data-product-name') || ('Product ' + id),
          price: parseFloat(add.getAttribute('data-product-price')) || 0,
          qty: qty,
        });
      }
      render();
      return;
    }
    const rm = e.target.closest('[data-cart-remove]');
    if (rm) {
      const id = rm.getAttribute('data-cart-remove');
      const idx = cart.findIndex((it) => String(it.id) === String(id));
      if (idx >= 0) cart.splice(idx, 1);
      render();
    }
  });

  let toastTimer = null;

  function dismissToast() {
    const mount = document.getElementById('order-result');
    if (mount) mount.innerHTML = '';
    if (toastTimer) {
      clearTimeout(toastTimer);
      toastTimer = null;
    }
  }

  document.body.addEventListener('click', (e) => {
    if (e.target.closest && e.target.closest('#order-result')) dismissToast();
  });

  document.body.addEventListener('htmx:afterSwap', (e) => {
    if (e.target && e.target.id === 'order-result' && e.target.innerHTML.trim() !== '') {
      if (toastTimer) clearTimeout(toastTimer);
      toastTimer = setTimeout(dismissToast, 5000);
    }
  });

  document.body.addEventListener('htmx:afterRequest', (e) => {
    if (e.target && e.target.id === 'cart-form' && e.detail.successful) {
      const ok = document.querySelector('#order-result .ok');
      if (ok) {
        cart.length = 0;
        render();
        if (window.htmx) {
          htmx.ajax('GET', '/fragments/client/orders', { target: '#my-orders', swap: 'innerHTML' });
        }
      }
    }
  });

  document.body.addEventListener('htmx:afterSwap', (e) => {
    if (e.target && (e.target.id === 'client-panel' || e.target.id === 'catalog')) render();
  });
})();
