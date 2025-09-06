import { BrowserRouter, Routes, Route, Link } from 'react-router-dom'

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
