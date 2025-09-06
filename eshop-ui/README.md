# React + Vite

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Babel](https://babeljs.io/) for Fast Refresh
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/) for Fast Refresh

## Expanding the ESLint configuration

If you are developing a production application, we recommend using TypeScript with type-aware lint rules enabled. Check out the [TS template](https://github.com/vitejs/vite/tree/main/packages/create-vite/template-react-ts) for information on how to integrate TypeScript and [`typescript-eslint`](https://typescript-eslint.io) in your project.

RTK Query έχει ήδη:
-baseQuery (fetchBaseQuery → χτίζεται πάνω από fetch)
-caching
-loading/error states
-automatic re-fetching & invalidation

Folder Structure
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

