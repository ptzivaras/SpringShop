import { createSlice } from '@reduxjs/toolkit'

const initialState = {
  lastOrder: null,
  myOrders: []
}

const ordersSlice = createSlice({
  name: 'orders',
  initialState,
  reducers: {
    setLastOrder: (state, action) => {
      state.lastOrder = action.payload
    },
    setMyOrders: (state, action) => {
      state.myOrders = action.payload
    }
  }
})

export const { setLastOrder, setMyOrders } = ordersSlice.actions
export default ordersSlice.reducer
