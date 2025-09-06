import { BrowserRouter, Routes, Route, Link } from 'react-router-dom'
import { useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useGetProductsQuery } from '../api/productsApi.js'
import { useGetCategoriesQuery } from '../api/categoriesApi.js'
import { addToCart } from '../features/cart/cartSlice.js'


function Nav() {
  const cartCount = useSelector(state =>
    state.cart.items.reduce((sum, i) => sum + i.quantity, 0)
  )
  return (
    <nav style={{display: 'flex', gap: 12, padding: 12, borderBottom: '1px solid #ddd'}}>
      <Link to="/">Home</Link>
      <Link to="/product/101">Product</Link>
      <Link to="/cart">Cart ({cartCount})</Link>
      <Link to="/checkout">Checkout</Link>
      <Link to="/admin">Admin</Link>
    </nav>
  )
}

function CartPage() {
  const items = useSelector(state => state.cart.items)
  const dispatch = useDispatch()

  const total = items.reduce((sum, i) => sum + i.price * i.quantity, 0)

  return (
    <div style={{ padding:20 }}>
      <h1>Cart</h1>
      {items.length === 0 ? (
        <p>Cart is empty</p>
      ) : (
        <>
          <ul style={{ listStyle:'none', padding:0 }}>
            {items.map(i => (
              <li key={i.productId} style={{ borderBottom:'1px solid #eee', padding:'10px 0' }}>
                <div style={{ fontWeight:600 }}>{i.name}</div>
                <div>${i.price} x {i.quantity} = ${i.price * i.quantity}</div>
                <div style={{ marginTop:6 }}>
                  <button
                    onClick={() => dispatch(decreaseQuantity(i.productId))}
                    style={{ marginRight:6 }}
                  >
                    -
                  </button>
                  <button
                    onClick={() => dispatch(increaseQuantity(i.productId))}
                    style={{ marginRight:6 }}
                  >
                    +
                  </button>
                  <button onClick={() => dispatch(removeFromCart(i.productId))}>
                    Remove
                  </button>
                </div>
              </li>
            ))}
          </ul>
          <h2 style={{ marginTop:20 }}>Total: ${total}</h2>
          <button onClick={() => dispatch(clearCart())} style={{ marginTop:10 }}>
            Clear Cart
          </button>
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
    <div style={{ padding: 16 }}>
      <h1 style={{ marginBottom: 12 }}>Products</h1>

      {/* Φίλτρα */}
      <div style={{
        display:'grid',
        gridTemplateColumns:'1fr 1fr 1fr 1fr 1fr',
        gap: 8,
        marginBottom: 12
      }}>
        <input
          placeholder="Search…"
          value={search}
          onChange={(e) => { setPage(1); setSearch(e.target.value) }}
          style={{ padding: 8, border:'1px solid #ddd', borderRadius:8 }}
        />
        <select
          value={categoryId}
          onChange={(e) => { setPage(1); setCategoryId(e.target.value) }}
          style={{ padding: 8, border:'1px solid #ddd', borderRadius:8 }}
        >
          <option value="">All categories</option>
          {!catLoading && categories.map(c => (
            <option key={c.id} value={c.id}>{c.name}</option>
          ))}
        </select>
        <select
          value={sort}
          onChange={(e) => { setPage(1); setSort(e.target.value) }}
          style={{ padding: 8, border:'1px solid #ddd', borderRadius:8 }}
        >
          <option value="createdAt">Newest</option>
          <option value="price">Price</option>
          <option value="name">Name</option>
        </select>
        <select
          value={order}
          onChange={(e) => { setPage(1); setOrder(e.target.value) }}
          style={{ padding: 8, border:'1px solid #ddd', borderRadius:8 }}
        >
          <option value="desc">Desc</option>
          <option value="asc">Asc</option>
        </select>
        <div style={{ display:'flex', alignItems:'center', justifyContent:'flex-end', gap:8 }}>
          <button
            disabled={!canGoPrev}
            onClick={() => setPage(p => Math.max(1, p - 1))}
            style={{ padding:'8px 10px', border:'1px solid #ddd', borderRadius:8, background:'#f5f5f5', cursor: canGoPrev ? 'pointer' : 'not-allowed' }}
          >
            ← Prev
          </button>
          <span>Page {page}</span>
          <button
            disabled={!canGoNext}
            onClick={() => setPage(p => p + 1)}
            style={{ padding:'8px 10px', border:'1px solid #ddd', borderRadius:8, background:'#f5f5f5', cursor: canGoNext ? 'pointer' : 'not-allowed' }}
          >
            Next →
          </button>
        </div>
      </div>

      {/* Λίστα προϊόντων */}
      {isLoading && <div>Loading products…</div>}
      {isError && <div>Error loading products</div>}
      {!isLoading && !isError && (
        <ul style={{
          display:'grid',
          gridTemplateColumns:'repeat(auto-fill, minmax(220px, 1fr))',
          gap: 12,
          listStyle:'none',
          padding: 0
        }}>
          {products.map(p => (
            <li key={p.id} style={{ border:'1px solid #eee', borderRadius:12, padding:12 }}>
              <div style={{ fontWeight: 600, marginBottom:4 }}>{p.name}</div>
              <div style={{ color:'#666', fontSize:14, minHeight:38 }}>{p.description}</div>
              <div style={{ marginTop:8, fontWeight:600 }}>${p.price}</div>
              <div style={{ fontSize:12, color:'#666' }}>Stock: {p.stockQty}</div>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}

function ProductPage()  { return <h1 style={{padding:20}}>Product Page</h1> }
function CartPage()     { return <h1 style={{padding:20}}>Cart Page</h1> }
function CheckoutPage() { return <h1 style={{padding:20}}>Checkout Page</h1> }
function AdminPage()    { return <h1 style={{padding:20}}>Admin Page</h1> }
function NotFound()     { return <h1 style={{padding:20}}>Not Found</h1> }

export default function AppRouter() {
  return (
    <BrowserRouter>
    <Nav /> 
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/product/:id" element={<h1>Product Page</h1>} />
        <Route path="/cart" element={<h1>Cart Page</h1>} />
        <Route path="/checkout" element={<h1>Checkout Page</h1>} />
        <Route path="/admin" element={<h1>Admin Page</h1>} />
        <Route path="*" element={<h1 style={{padding:20}}>Not Found</h1>} />
      </Routes>
    </BrowserRouter>
  )
}
