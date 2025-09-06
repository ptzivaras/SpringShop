import { configureStore } from "@reduxjs/toolkit";
import cartReducer from '../features/cart/cartSlice.js'
import { baseApi } from '../api/baseApi.js'
import { loadCartItems, saveCartItems } from './persist.js'

// preload state only for cart
const preloadedCart = { items: loadCartItems() }

export const store = configureStore({
  reducer: {
    [baseApi.reducerPath]: baseApi.reducer,
    cart: cartReducer
  },// εδω ολοι οι reducers απο τα slices
  preloadedState: {
    cart: preloadedCart
  }, 
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(baseApi.middleware)
});