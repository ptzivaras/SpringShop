import { baseApi } from './baseApi.js'

export const categoriesApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getCategories: builder.query({
      query: () => 'categories'
    })
  })
})

export const { useGetCategoriesQuery } = categoriesApi
