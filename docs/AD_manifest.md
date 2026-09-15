# Architecture Decisions — Recruiter-facing Web UI

> Living manifest. All web-UI decisions recorded here per project convention.
> JSON REST API is frozen as portfolio artifact; HTML is additive.

## AD-01 — Same-origin HTML, JSON untouched (CORS avoidance)

- Keep existing `@RestController`s (`/users`, `/products`, `/categories`, `/orders`) returning JSON exactly as-is.
- Add additive `com.rodrigodip.workshop_springboot4_jpa.web` `@Controller`s returning Thymeleaf pages/fragments:
  `GET /`, `GET /fragments/client/**`, `GET|POST|PUT|DELETE /fragments/admin/**`.
- Web controllers call `*Service` directly (same JVM), never HTTP to self.
- Rationale: single deployable jar, zero CORS/preflight, recruiters see live data.
- Scope existing `ControllerExceptionHandler` to `@RestController` so HTML errors render as fragments, not JSON.

## AD-02 — Server-rendered HTML: Thymeleaf + HTMX + vanilla JS

- Full pages + HTMX partials (`hx-get/hx-post/hx-put/hx-delete`, `hx-target`, `hx-swap`).
- Vanilla JS only for: virtual-tab switching (`tabs.js`), cart rows, toast, delete confirm. No SPA framework.
- HTMX via CDN (`https://unpkg.com/htmx.org@2`), `hx-boost` off for forms that must stay in-panel.
- Non-JS fallback: forms keep plain `action` so they still POST.

## AD-03 — Styling: Tailwind Play CDN (v1)

- `<script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>` in `index.html`.
- Custom CSS limited to `static/css/app.css` (placeholder, tab states, modal, toast).
- Revisit local Tailwind build later for CSP/offline hardening.

## AD-03a — Image placeholders

- `Product.imgUrl` seed is `""`; render the static SVG placeholder
  (`static/img/product-image-placeholder.svg`, 100×100 on a flat background).
  Never hotlink external images in v1.

## AD-04 — Recruiter-first IA: one page, two virtual tabs

- `GET /?tab=client|admin` → `index.html` shell: hero (stack badges, live API links), `role=tablist` (`Client | Admin`),
  two `<section role=tabpanel>` in the SAME browser tab (no `window.open`).
- Client panel lazy-loads on page load; admin lazy-loads on first tab click. `?tab=`/`#tab` only selects initial panel.

## AD-04a — Client gate: new client only (locked)

- Client virtual tab is gated. First paint = `Create your client` card only; catalog/cart/my-orders locked behind it.
- Valid client = `name` non-blank + `email` contains `@` + `phone` non-blank + `password` >= 4 chars
  (client-side `required`/`minlength`/`type=email`; server re-checks because Bean Validation does not exist yet — see README Phase 2).
- `POST /fragments/client/session-client` → `UserService.insert` → `HttpSession.currentClientId` → HTMX swaps panel to storefront.
- No "pick existing user" option in v1 (explicit user decision). `Reset` (`DELETE session-client`) clears session for demo replay.
- Password is `WRITE_ONLY` (never rendered).

## AD-04b — Admin: open demo, honest banner

- No auth (matches README Known Limitations). Header badge: `Demo — no auth · all endpoints public`.
- Sections: Dashboard counts, Products CRUD, Categories CRUD, Orders (dual filter + status update + delete), Users CRUD.

## AD-04c — Admin order filters: status + client (locked)

- `GET /fragments/admin/orders?status=&clientName=`; `status ∈ {ALL, WAITING_PAYMENT, PAID, SHIPPED, DELIVERED, CANCELED}`;
  `clientName` is a contains, case-insensitive search.
- Web-only `OrderService.findFilteredByClientName(status, clientName)`; no REST contract change.
- Client tab `My orders` is auto-scoped to session client (no filter UI).
- `Payment` is display-only until Phase-2 `POST /orders/{id}/payment` exists.

## AD-05 — Language: English only (v1)

- UI strings in English. PT-BR deferred; README stays bilingual reference.

## AD-06 — i18n / validation / pagination explicitly out of v1

- No Bean Validation dependency yet (manual checks in web layer); no pagination (seed-sized tables); no auth.

## AD-07 — Admin conventions (locked)

- Tab keeps the name `Admin` (industry-standard; `Client | Admin` reads instantly).
- Visitor = *user* (generic copy only); registered API record = *client* in all admin labels.
  Internal endpoint paths (`/fragments/admin/users`) unchanged.
- Active section button highlighted with `#10b981` (`.subnav-active`, `admin-nav.js`).
- Products: create form rows (name / price+category-dropdown / description); categories chosen
  via dropdown into removable chips (`admin-chips.js`, hidden `categoryIds` inputs).
- Edits via modal (`#admin-modal` mount, shared `modal.js` infra with client tab):
  product (incl. categories), category, client. Edit forms carry `data-modal-form` so the
  modal auto-closes when `#admin-panel` re-renders after save. Blank password on client
  edit means "keep current" (guarded in controller; `UserService` overwrites when non-null).
- Orders: client filter is a contains, case-insensitive name search (`clientName`,
  `OrderService.findFilteredByClientName`); date-only `Date` column (`dd MMM yyyy` UTC);
  per-row `Client's orders` button re-filters to that client preserving status;
  `Refresh` resubmits the current filter.

## AD-08 — Admin refinement (locked)

- Active section button keeps `#10b981` on hover (`.subnav:not(.subnav-active):hover`);
  only non-active buttons react.
- Product image URL stays a disabled placeholder (`managed by seed data`). Rationale: no XSS
  vector through escaped `<img th:src>`, but open demo + no auth means unmoderated remote
  content (defacement, IP leak, dead links, mixed content). Enable only with auth + https-only
  (+ allowlist/moderation).
- Category lists are id-ordered in the web layer only (`sortedById`); REST order untouched.
- Orders show `Clear filters` only when a filter is active (status or name); bare `GET`
  returns the unfiltered panel. Admin order detail shows full human timestamp
  (`dd MMM yyyy HH:mm` UTC); table keeps date-only column.

## AD-09 — Catalog pagination + client order cancel (locked)

- Catalog shows 6 products per page (`ProductService.CATALOG_PAGE_SIZE`, fixed server-side).
  `Page<Product> findByCategories_Id` derived query; out-of-range pages clamp to last page.
- Pager is Prev/Next + `Page X of Y` (hidden on a single page); pager links preserve the
  active `categoryId`, choosing a category resets to page 0. Cards fragment owns the
  2-column grid (`grid sm:grid-cols-2`).
- Test seed grown to 12 products so 6/page yields 2 real pages (seed-data growth, not a
  contract change).
- Clients may cancel own orders in `WAITING_PAYMENT`/`PAID` only
  (`PUT /fragments/client/orders/{id}/cancel`, ownership + status guards, `hx-confirm`);
  result is `CANCELED`, visible in the admin status filter.

## AD-10 — Toast feedback + friendly FK errors (locked)

- Order-placed feedback is a toast: `#order-result.toast` fixed bottom-right, slide-in,
  auto-dismiss 5s, click to dismiss, timer resets on repeat orders (`cart.js`).
  Cart clears only when the `.ok` success box actually rendered (error fragments also
  arrive as HTTP 200 and must not wipe a valid cart).
- FK violations are translated in `WebExceptionHandler` only: raw-SQL detection
  (constraint/violation/FK/SQLSTATE markers), per-section friendly messages, raw text kept
  in server logs. Friendly validation strings pass through untouched; REST JSON stays raw.
- Failed admin deletes also offer `← Back to <section>` (section derived from URI) so the
  panel never strands the visitor.

## AD-11 — Asset versioning + error modals (locked)

- Static assets carry a `?v=N` query (`index.html`); bump N on every CSS/JS change so
  browsers refetch. Stale cache was the root cause of the "toast doesn't work" report —
  code was correct, delivery wasn't.
- All `_error` fragments render as modals (backdrop + Close via shared `modal.js`).
  Admin errors set `HX-Retarget: #admin-modal` so the listing stays intact behind the
  modal; the AD-10 back-button retry logic is removed as obsolete.
