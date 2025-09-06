import { configureStore } from "@reduxjs/toolkit";
import cartReducer from '../features/cart/cartSlice.js'
import ordersReducer from '../features/orders/ordersSlice.js'
import { baseApi } from '../api/baseApi.js'
import { loadCartItems, saveCartItems } from './persist.js'

// preload state only for cart
const preloadedCart = { items: loadCartItems() }

export const store = configureStore({
  reducer: {
    [baseApi.reducerPath]: baseApi.reducer,
    cart: cartReducer,
    orders: ordersReducer,
  },// εδω ολοι οι reducers απο τα slices
  preloadedState: {
    cart: preloadedCart
  }, 
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(baseApi.middleware)
});

// persist cart to localStorage on every change
store.subscribe(() => {
  const items = store.getState().cart.items
  saveCartItems(items)
});