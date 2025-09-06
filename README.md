# SpringShop
A massive eshop implemented with spring and react with many features

Τι έβαλες με RTK Query και πώς δουλεύει (σύντομα)
1. baseApi (src/api/baseApi.js)
-Φτιάξαμε ένα API “instance” με createApi και fetchBaseQuery('http://localhost:4000/').
-Αυτό είναι το transport layer (αντί για useEffect/fetch/axios).

2. productsApi (src/api/productsApi.js)
-“Ενέσαμε” endpoints πάνω στο baseApi:
--getProducts → GET /products
--getProductById → GET /products/:id
-Αυτό σου έδωσε React hooks: useGetProductsQuery, useGetProductByIdQuery.

3.store wiring (src/store/store.js)
-Προσθέσαμε baseApi.reducer στο Redux store.
-Προσθέσαμε baseApi.middleware για να δουλεύουν cache, re-fetching, invalidation.

4. Χρήση στο UI (Home)
-Καλείς const { data, isLoading, isError } = useGetProductsQuery()
-Δεν γράφεις useEffect/fetch. Το RTKQ αναλαμβάνει request, cache, loading flags.

5. Τι κερδίζεις
-Αυτόματο caching ανά query key.
-Status flags (loading/error/success).
-Refetch όταν αλλάζουν παράμετροι.
-Εύκολο CRUD με invalidatesTags/providesTags (θα τα βάλουμε όταν φτιάξουμε Admin CRUD).
