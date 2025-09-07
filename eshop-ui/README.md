# E-Shop UI (React + Vite + Redux Toolkit + RTK Query)

Μικρό e-commerce front-end με mock backend (json-server). Περιλαμβάνει:
- Προϊόντα με φίλτρα, ταξινόμηση, pagination (client-side λογική με json-server params)
- Product details + Reviews
- Καλάθι (Redux slice) με persist σε `localStorage`
- Checkout με `react-hook-form` + `zod` + toasts
- Orders (mock POST)
- Admin: Dashboard + ξεχωριστή σελίδα **Products CRUD** (Create, Read, Update, Delete)
- Styling με Tailwind (basic utility classes)

> Στόχος: να δεις πλήρη ροή **RTK Query** (queries + mutations + cache invalidation).

---

## Requirements

- Node 18+
- npm / pnpm / yarn
- (προαιρετικό) Git

---

## Εγκατάσταση

```bash
npm install

## EndPoints

GET /products?_page=1&_limit=8&_sort=createdAt&_order=desc

GET /products/101

POST /products

PATCH /products/:id

DELETE /products/:id

GET /categories

GET /reviews?productId=101

POST /orders

## Folder Structure
src/
  app/            # redux store, root providers
  api/            # RTK Query api slices
  features/
    cart/         # cart slice & components
    products/     # product list, product details
    categories/   # categories logic
    reviews/      # reviews per product
  components/     # shared UI components (Header, Footer, Button, etc.)
  pages/          # route-level pages (Home, Cart, Checkout, Admin)
  routes/         # React Router setup
  theme/          # dark/light ThemeContext
  lib/            # helpers (utils, formatters, storage)

