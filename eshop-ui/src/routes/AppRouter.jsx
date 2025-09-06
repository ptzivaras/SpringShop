import { BrowserRouter, Routes, Route, Link, useParams, useNavigate } from 'react-router-dom'
import { useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useGetProductsQuery, useGetProductByIdQuery } from '../api/productsApi.js'
import { useGetCategoriesQuery } from '../api/categoriesApi.js'
import { useGetReviewsByProductQuery } from '../api/reviewsApi.js'
import { addToCart } from '../features/cart/cartSlice.js'
// ✅ extra imports for CartPage actions (ΔΕΝ σβήνω τα δικά σου σχόλια παρακάτω)
import {
  removeFromCart,
  increaseQuantity,
  decreaseQuantity,
  clearCart
} from '../features/cart/cartSlice.js'

// ✅ forms + validation + toasts
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'
import { Toaster, toast } from 'react-hot-toast'

function Nav() {
  const cartCount = useSelector(state =>
    state.cart.items.reduce((sum, i) => sum + i.quantity, 0)
  )
  return (
    <header className="sticky top-0 z-40 border-b bg-white/95 backdrop-blur">
      <nav className="mx-auto flex max-w-6xl items-center gap-4 px-4 py-3">
        <Link to="/" className="font-semibold text-lg">E-Shop</Link>
        <div className="ml-auto flex items-center gap-3">
          <Link className="rounded-md px-3 py-2 text-sm hover:bg-gray-100" to="/">Home</Link>
          <Link className="rounded-md px-3 py-2 text-sm hover:bg-gray-100" to="/product/101">Product</Link>
          <Link className="rounded-md px-3 py-2 text-sm hover:bg-gray-100" to="/cart">
            Cart <span className="ml-1 inline-flex min-w-5 items-center justify-center rounded-full bg-gray-900 px-1.5 text-[10px] font-bold text-white">{cartCount}</span>
          </Link>
          <Link className="rounded-md px-3 py-2 text-sm hover:bg-gray-100" to="/checkout">Checkout</Link>
          <Link className="rounded-md px-3 py-2 text-sm hover:bg-gray-100" to="/admin">Admin</Link>
        </div>
      </nav>
    </header>
  )
}

function CartPage() {
  const items = useSelector(state => state.cart.items)
  const dispatch = useDispatch()

  const total = items.reduce((sum, i) => sum + i.price * i.quantity, 0)

  return (
    <div className="mx-auto max-w-6xl px-4 py-6">
      <h1 className="mb-4 text-2xl font-semibold">Cart</h1>
      {items.length === 0 ? (
        <p className="text-gray-600">Cart is empty</p>
      ) : (
        <>
          <ul className="divide-y rounded-xl border bg-white shadow-sm">
            {items.map(i => (
              <li key={i.productId} className="flex items-center justify-between gap-4 p-4">
                <div>
                  <div className="font-medium">{i.name}</div>
                  <div className="text-sm text-gray-600">${i.price} x {i.quantity} = <span className="font-semibold text-gray-900">${i.price * i.quantity}</span></div>
                </div>
                <div className="flex items-center gap-2">
                  <button
                    onClick={() => dispatch(decreaseQuantity(i.productId))}
                    className="inline-flex h-8 w-8 items-center justify-center rounded-md border bg-white shadow-sm hover:bg-gray-50"
                    title="Decrease"
                  >−</button>
                  <button
                    onClick={() => dispatch(increaseQuantity(i.productId))}
                    className="inline-flex h-8 w-8 items-center justify-center rounded-md border bg-white shadow-sm hover:bg-gray-50"
                    title="Increase"
                  >+</button>
                  <button
                    onClick={() => dispatch(removeFromCart(i.productId))}
                    className="rounded-md border bg-white px-3 py-1.5 text-sm shadow-sm hover:bg-red-50 hover:text-red-700"
                  >
                    Remove
                  </button>
                </div>
              </li>
            ))}
          </ul>
          <div className="mt-4 flex items-center justify-between">
            <h2 className="text-xl font-semibold">Total: ${total}</h2>
            <button
              onClick={() => dispatch(clearCart())}
              className="rounded-md border bg-white px-4 py-2 text-sm shadow-sm hover:bg-gray-50"
            >
              Clear Cart
            </button>
          </div>
        </>
      )}
    </div>
  )
}

function HomePage() {
  const dispatch = useDispatch()
  const [page, setPage] = useState(1)
  const [limit] = useState(8)
  const [categoryId, setCategoryId] = useState('')
  const [search, setSearch] = useState('')
  const [sort, setSort] = useState('createdAt')
  const [order, setOrder] = useState('desc')

  // Fetch κατηγοριών για το dropdown
  const { data: categories = [], isLoading: catLoading } = useGetCategoriesQuery()

  // Fetch προϊόντων με βάση φίλτρα
  const { data: products = [], isLoading, isError } = useGetProductsQuery({
    page,
    limit,
    categoryId: categoryId || undefined,
    search: search || undefined,
    sort,
    order
  })

  // json-server δεν επιστρέφει total count headers by default εύκολα στο fetchBaseQuery,
  // οπότε για απλότητα δείχνουμε "Next" όσο επιστρέφονται πλήρεις σελίδες (length === limit).
  const canGoPrev = page > 1
  const canGoNext = products.length === limit

  // const { data, isLoading, isError } = useGetProductsQuery()
  //if (isLoading) return <p style={{padding:20}}>Loading products...</p>
  //if (isError)   return <p style={{padding:20}}>Error loading products</p>

  return (
    <div className="mx-auto max-w-6xl px-4 py-6">
      <h1 className="mb-4 text-2xl font-semibold">Products</h1>

      {/* Φίλτρα */}
      <div
        className="mb-4 grid gap-3 rounded-xl border bg-white p-3 shadow-sm sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-5"
        style={{ /* κρατάω το comment block σου */ }}
      >
        <input
          placeholder="Search…"
          value={search}
          onChange={(e) => { setPage(1); setSearch(e.target.value) }}
          className="w-full rounded-md border bg-white px-3 py-2 text-sm outline-none focus:border-gray-400 focus:ring-1 focus:ring-gray-300"
        />
        <select
          value={categoryId}
          onChange={(e) => { setPage(1); setCategoryId(e.target.value) }}
          className="w-full rounded-md border bg-white px-3 py-2 text-sm outline-none focus:border-gray-400 focus:ring-1 focus:ring-gray-300"
        >
          <option value="">All categories</option>
          {!catLoading && categories.map(c => (
            <option key={c.id} value={c.id}>{c.name}</option>
          ))}
        </select>
        <select
          value={sort}
          onChange={(e) => { setPage(1); setSort(e.target.value) }}
          className="w-full rounded-md border bg-white px-3 py-2 text-sm outline-none focus:border-gray-400 focus:ring-1 focus:ring-gray-300"
        >
          <option value="createdAt">Newest</option>
          <option value="price">Price</option>
          <option value="name">Name</option>
        </select>
        <select
          value={order}
          onChange={(e) => { setPage(1); setOrder(e.target.value) }}
          className="w-full rounded-md border bg-white px-3 py-2 text-sm outline-none focus:border-gray-400 focus:ring-1 focus:ring-gray-300"
        >
          <option value="desc">Desc</option>
          <option value="asc">Asc</option>
        </select>
        <div className="flex items-center justify-end gap-2">
          <button
            disabled={!canGoPrev}
            onClick={() => setPage(p => Math.max(1, p - 1))}
            className="rounded-md border bg-white px-3 py-2 text-sm shadow-sm hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50"
          >
            ← Prev
          </button>
          <span className="text-sm">Page {page}</span>
          <button
            disabled={!canGoNext}
            onClick={() => setPage(p => p + 1)}
            className="rounded-md border bg-white px-3 py-2 text-sm shadow-sm hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50"
          >
            Next →
          </button>
        </div>
      </div>

      {/* Λίστα προϊόντων */}
      {isLoading && <div className="text-gray-600">Loading products…</div>}
      {isError && <div className="text-red-600">Error loading products</div>}
      {!isLoading && !isError && (
        <ul className="grid grid-cols-1 gap-5 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
          {products.map(p => (
            <li key={p.id} className="rounded-xl border bg-white p-4 shadow-sm transition hover:shadow-md">
              {/* Link σε product page */}
              <div className="mb-1 font-semibold">
                <Link to={`/product/${p.id}`} className="hover:underline">{p.name}</Link>
              </div>
              <div className="min-h-[42px] text-sm text-gray-600">{p.description}</div>
              <div className="mt-2 font-semibold">${p.price}</div>
              <div className="text-xs text-gray-500">Stock: {p.stockQty}</div>
              {/* ✅ κουμπί προσθήκης στο καλάθι */}
              <button
                onClick={() => {
                  // απλό toast feedback
                  toast.success(`${p.name} added to cart`)
                  return dispatch(addToCart({ productId: p.id, name: p.name, price: p.price }))
                }}
                className="mt-3 w-full rounded-md border bg-white px-3 py-2 text-sm shadow-sm hover:bg-gray-50"
              >
                Add to Cart
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}

function ProductPage()  {
  const { id } = useParams()
  const navigate = useNavigate()
  const dispatch = useDispatch()

  const { data: product, isLoading, isError } = useGetProductByIdQuery(id)
  const { data: reviews = [], isLoading: rLoading } = useGetReviewsByProductQuery(id)

  if (isLoading) return <div className="mx-auto max-w-4xl px-4 py-6">Loading product…</div>
  if (isError || !product) return <div className="mx-auto max-w-4xl px-4 py-6 text-red-600">Product not found</div>

  return (
    <div className="mx-auto max-w-4xl px-4 py-6">
      <div className="rounded-xl border bg-white p-5 shadow-sm">
        <button onClick={() => navigate(-1)} className="mb-3 rounded-md border bg-white px-3 py-2 text-sm shadow-sm hover:bg-gray-50">← Back</button>
        <h1 className="mb-1 text-2xl font-semibold">{product.name}</h1>
        <div className="mb-2 text-gray-600">{product.description}</div>
        <div className="mb-2 font-semibold">${product.price}</div>
        <div className="mb-4 text-xs text-gray-500">Stock: {product.stockQty}</div>
        <button
          onClick={() => {
            toast.success(`${product.name} added to cart`)
            return dispatch(addToCart({ productId: product.id, name: product.name, price: product.price }))
          }}
          className="mb-4 rounded-md border bg-white px-3 py-2 text-sm shadow-sm hover:bg-gray-50"
        >
          Add to Cart
        </button>

        <h2 className="mb-2 text-xl font-semibold">Reviews</h2>
        {rLoading && <div>Loading reviews…</div>}
        {!rLoading && reviews.length === 0 && <div className="text-gray-600">No reviews yet.</div>}
        {!rLoading && reviews.length > 0 && (
          <ul className="divide-y rounded-xl border bg-white shadow-sm">
            {reviews.map(rv => (
              <li key={rv.id} className="p-3">
                <div className="font-medium">{rv.userName} ★ {rv.rating}/5</div>
                <div className="text-gray-700">{rv.comment}</div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  )
}

// function CartPage()     { return <h1 style={{padding:20}}>Cart Page</h1> }

// ✅ Checkout με react-hook-form + zod
const checkoutSchema = z.object({
  fullName: z.string().min(2, 'Enter your full name'),
  email: z.string().email('Invalid email'),
  address: z.string().min(5, 'Enter a valid address'),
  city: z.string().min(2, 'Enter city'),
  zip: z.string().min(4, 'Enter ZIP/postal code'),
  payment: z.enum(['cod', 'card'])
})

function CheckoutPage() {
  const items = useSelector(state => state.cart.items)
  const total = items.reduce((s, i) => s + i.price * i.quantity, 0)
  const dispatch = useDispatch()

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    reset
  } = useForm({
    resolver: zodResolver(checkoutSchema),
    defaultValues: {
      fullName: '',
      email: '',
      address: '',
      city: '',
      zip: '',
      payment: 'cod'
    }
  })

  const onSubmit = async (data) => {
    // mock “order submit”
    // εδώ αργότερα μπορούμε να POST στο /orders & /orderItems
    toast.success('Order placed successfully!')
    dispatch(clearCart())
    reset()
  }

  return (
    <div className="mx-auto grid max-w-6xl grid-cols-1 gap-6 px-4 py-6 md:grid-cols-2">
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
        <h1 className="text-2xl font-semibold">Checkout</h1>

        <label className="block text-sm">
          Full name
          <input {...register('fullName')} className="mt-1 w-full rounded-md border bg-white px-3 py-2 outline-none focus:border-gray-400 focus:ring-1 focus:ring-gray-300"/>
          {errors.fullName && <div className="text-sm text-red-600">{errors.fullName.message}</div>}
        </label>

        <label className="block text-sm">
          Email
          <input {...register('email')} className="mt-1 w-full rounded-md border bg-white px-3 py-2 outline-none focus:border-gray-400 focus:ring-1 focus:ring-gray-300"/>
          {errors.email && <div className="text-sm text-red-600">{errors.email.message}</div>}
        </label>

        <label className="block text-sm">
          Address
          <input {...register('address')} className="mt-1 w-full rounded-md border bg-white px-3 py-2 outline-none focus:border-gray-400 focus:ring-1 focus:ring-gray-300"/>
          {errors.address && <div className="text-sm text-red-600">{errors.address.message}</div>}
        </label>

        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
          <label className="block text-sm">
            City
            <input {...register('city')} className="mt-1 w-full rounded-md border bg-white px-3 py-2 outline-none focus:border-gray-400 focus:ring-1 focus:ring-gray-300"/>
            {errors.city && <div className="text-sm text-red-600">{errors.city.message}</div>}
          </label>
          <label className="block text-sm">
            ZIP
            <input {...register('zip')} className="mt-1 w-full rounded-md border bg-white px-3 py-2 outline-none focus:border-gray-400 focus:ring-1 focus:ring-gray-300"/>
            {errors.zip && <div className="text-sm text-red-600">{errors.zip.message}</div>}
          </label>
        </div>

        <fieldset className="rounded-xl border bg-white p-3 shadow-sm">
          <legend className="text-sm font-medium">Payment</legend>
          <label className="mt-2 flex items-center gap-2 text-sm">
            <input type="radio" value="cod" {...register('payment')} /> Cash on Delivery
          </label>
          <label className="mt-1 flex items-center gap-2 text-sm">
            <input type="radio" value="card" {...register('payment')} /> Card (mock)
          </label>
          {errors.payment && <div className="text-sm text-red-600">{errors.payment.message}</div>}
        </fieldset>

        <button
          type="submit"
          disabled={isSubmitting || items.length === 0}
          className="w-full rounded-md border bg-white px-4 py-2 text-sm shadow-sm hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50"
        >
          Place Order
        </button>

        {items.length === 0 && (
          <div className="text-xs text-gray-600">
            Cart is empty — add items from <Link to="/" className="underline">Home</Link>.
          </div>
        )}
      </form>

      {/* Order summary */}
      <div>
        <h2 className="mb-2 text-xl font-semibold">Order Summary</h2>
        {items.length === 0 ? (
          <div className="text-gray-600">No items.</div>
        ) : (
          <ul className="divide-y rounded-xl border bg-white shadow-sm">
            {items.map(i => (
              <li key={i.productId} className="flex items-center justify-between p-3">
                <div className="text-sm">{i.name} x {i.quantity}</div>
                <div className="text-sm font-medium">${i.price * i.quantity}</div>
              </li>
            ))}
          </ul>
        )}
        <h3 className="mt-3 text-lg font-semibold">Total: ${total}</h3>
      </div>
    </div>
  )
}

function NotFound()     { return <h1 className="mx-auto max-w-6xl px-4 py-6 text-red-600">Not Found</h1> }

export default function AppRouter() {
  return (
    <BrowserRouter>
      <Toaster position="top-right" /> {/* toasts UI */}
      <Nav /> 
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/product/:id" element={<ProductPage />} />
        {/* ✅ κάνουμε render το κανονικό CartPage component */}
        <Route path="/cart" element={<CartPage />} />
        <Route path="/checkout" element={<CheckoutPage />} />
        <Route path="/admin" element={<h1 className="mx-auto max-w-6xl px-4 py-6">Admin Page</h1>} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </BrowserRouter>
  )
}
