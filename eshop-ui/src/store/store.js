import { configureStore } from "@reduxjs/toolkit";
import cartReducer from '../features/cart/cartSlice.js'
import { baseApi } from '../api/baseApi.js'

export const store = configureStore({
  reducer: {
    [baseApi.reducerPath]: baseApi.reducer,
    cart: cartReducer
  }, // εδω ολοι οι reducers απο τα slices
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(baseApi.middleware)
});