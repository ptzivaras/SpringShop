import { BrowserRouter, Routes, Route, Link } from 'react-router-dom'
import { useGetProductsQuery } from '../api/productsApi.js'

function Nav() {
  return (
    <nav style={{display: 'flex', gap: 12, padding: 12, borderBottom: '1px solid #ddd'}}>
      <Link to="/">Home</Link>
      <Link to="/product/101">Product</Link>
      <Link to="/cart">Cart</Link>
      <Link to="/checkout">Checkout</Link>
      <Link to="/admin">Admin</Link>
    </nav>
  )
}

function HomePage() {
  const { data, isLoading, isError } = useGetProductsQuery()

  if (isLoading) return <p style={{padding:20}}>Loading products...</p>
  if (isError)   return <p style={{padding:20}}>Error loading products</p>

  return (
    <div style={{ padding: 20 }}>
      <h1>Home Page</h1>
      <ul>
        {data?.map(p => (
          <li key={p.id}>
            {p.name} — ${p.price}
          </li>
        ))}
      </ul>
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
        <Route path="/" element={<h1>Home Page</h1>} />
        <Route path="/product/:id" element={<h1>Product Page</h1>} />
        <Route path="/cart" element={<h1>Cart Page</h1>} />
        <Route path="/checkout" element={<h1>Checkout Page</h1>} />
        <Route path="/admin" element={<h1>Admin Page</h1>} />
        <Route path="*" element={<h1 style={{padding:20}}>Not Found</h1>} />
      </Routes>
    </BrowserRouter>
  )
}
