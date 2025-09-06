import { baseApi } from './baseApi.js'

export const reviewsApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getReviewsByProduct: builder.query({
      query: (productId) => `reviews?productId=${productId}`
    })
  })
})

export const { useGetReviewsByProductQuery } = reviewsApi
