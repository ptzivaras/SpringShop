import { baseApi } from './baseApi.js'

export const productsApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    // params: { page, limit, categoryId, search, sort, order }
    getProducts: builder.query({
      query: (params = {}) => {
        const {
          page = 1,
          limit = 8,
          categoryId,
          search,
          sort = 'createdAt',
          order = 'desc'
        } = params

        const qp = new URLSearchParams()
        qp.set('_page', String(page))
        qp.set('_limit', String(limit))
        qp.set('_sort', String(sort))
        qp.set('_order', String(order))
        if (categoryId) qp.set('categoryId', String(categoryId))
        if (search) qp.set('q', String(search))

        return `products?${qp.toString()}`
      } // <-- σωστό: κλείνουμε την arrow function με }, όχι })
    }),
    getProductById: builder.query({
      query: (id) => `products/${id}`
    })
  })
})

export const { useGetProductsQuery, useGetProductByIdQuery } = productsApi
