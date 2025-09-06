import { baseApi } from './baseApi.js'

export const productsApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getProducts: builder.query({
      query: () => 'products'
    }),
    getProductById: builder.query({
      query: (id) => `products/${id}`
    })
  })
})

export const { useGetProductsQuery, useGetProductByIdQuery } = productsApi
