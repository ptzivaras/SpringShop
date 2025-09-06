import { baseApi } from './baseApi.js'

export const ordersApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    createOrder: builder.mutation({
      query: (order) => ({
        url: 'orders',
        method: 'POST',
        body: order
      })
    }),
    getOrders: builder.query({
      query: () => 'orders'
    })
  })
})

export const { useCreateOrderMutation, useGetOrdersQuery } = ordersApi
