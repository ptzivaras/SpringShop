import { createSlice } from '@reduxjs/toolkit'

const initialState = {
  items: [] // { productId, quantity }
}

const cartSlice = createSlice({
  name: 'cart',
  initialState,
  reducers: {
    addToCart: (state, action) => {
      const { productId, quantity } = action.payload
      const existing = state.items.find(i => i.productId === productId)
      if (existing) {
        existing.quantity += quantity
      } else {
        state.items.push({ productId, quantity })
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
