import { baseApi } from './baseApi.js'

export const productsApi = baseApi.injectEndpoints({
  endpoints: (builder) => ({
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
      },
      providesTags: ['Products']
    }),

    getProductById: builder.query({
      query: (id) => `products/${id}`
    }),

    createProduct: builder.mutation({
      query: (product) => ({
        url: 'products',
        method: 'POST',
        body: product
      }),
      invalidatesTags: ['Products']
    }),

    updateProduct: builder.mutation({
      query: ({ id, patch }) => ({
        url: `products/${id}`,
        method: 'PATCH', // με json-server δουλεύει το PATCH
        body: patch
      }),
      invalidatesTags: ['Products']
    }),

    deleteProduct: builder.mutation({
      query: (id) => ({
        url: `products/${id}`,
        method: 'DELETE'
      }),
      invalidatesTags: ['Products']
    })
  })
})

export const {
  useGetProductsQuery,
  useGetProductByIdQuery,
  useCreateProductMutation,
  useUpdateProductMutation,
  useDeleteProductMutation
} = productsApi
