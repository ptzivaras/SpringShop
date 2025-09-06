import { BrowserRouter, Routes, Route } from 'react-router-dom'

export default function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<h1>Home Page</h1>} />
        <Route path="/product/:id" element={<h1>Product Page</h1>} />
        <Route path="/cart" element={<h1>Cart Page</h1>} />
        <Route path="/checkout" element={<h1>Checkout Page</h1>} />
        <Route path="/admin" element={<h1>Admin Page</h1>} />
      </Routes>
    </BrowserRouter>
  )
}
