import { createSlice } from '@reduxjs/toolkit'

const initialState = {
  items: [] // κάθε item: { productId, name, price, quantity }
}

const cartSlice = createSlice({
  name: 'cart',
  initialState,
  reducers: {
    addToCart: (state, action) => {
      const { productId, name, price } = action.payload
      const existing = state.items.find(i => i.productId === productId)
      if (existing) {
        existing.quantity += 1
      } else {
        state.items.push({ productId, name, price, quantity: 1 })
      }
    },
    removeFromCart: (state, action) => {
      state.items = state.items.filter(i => i.productId !== action.payload)
    },
    clearCart: (state) => {
      state.items = []
    }
  }
})

export const { addToCart, removeFromCart, clearCart } = cartSlice.actions
export default cartSlice.reducer
