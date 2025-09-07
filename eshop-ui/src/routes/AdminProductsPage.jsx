import { useState } from "react"
import { useGetProductsQuery, useCreateProductMutation, useDeleteProductMutation } from "../api/productsApi.js"
import { toast } from "react-hot-toast"

export default function AdminProductsPage() {
  const { data: products = [], isLoading } = useGetProductsQuery({})
  const [createProduct] = useCreateProductMutation()
  const [deleteProduct] = useDeleteProductMutation()

  const [form, setForm] = useState({
    name: "",
    description: "",
    price: "",
    stockQty: ""
  })

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      await createProduct({
        ...form,
        price: parseFloat(form.price),
        stockQty: parseInt(form.stockQty),
        createdAt: Date.now()
      }).unwrap()
      toast.success("Product created")
      setForm({ name: "", description: "", price: "", stockQty: "" })
    } catch (err) {
      toast.error("Error creating product")
    }
  }

  const handleDelete = async (id) => {
    try {
      await deleteProduct(id).unwrap()
      toast.success("Product deleted")
    } catch (err) {
      toast.error("Error deleting product")
    }
  }

  return (
    <div className="mx-auto max-w-6xl px-4 py-6">
      <h1 className="mb-4 text-2xl font-semibold">Admin – Manage Products</h1>

      {/* Create product form */}
      <form onSubmit={handleSubmit} className="mb-6 space-y-3 rounded-md border p-4">
        <h2 className="text-lg font-semibold">Add New Product</h2>
        <input
          name="name"
          value={form.name}
          onChange={handleChange}
          placeholder="Name"
          className="w-full rounded-md border px-3 py-2 text-sm"
        />
        <textarea
          name="description"
          value={form.description}
          onChange={handleChange}
          placeholder="Description"
          className="w-full rounded-md border px-3 py-2 text-sm"
        />
        <input
          name="price"
          value={form.price}
          onChange={handleChange}
          placeholder="Price"
          type="number"
          className="w-full rounded-md border px-3 py-2 text-sm"
        />
        <input
          name="stockQty"
          value={form.stockQty}
          onChange={handleChange}
          placeholder="Stock Quantity"
          type="number"
          className="w-full rounded-md border px-3 py-2 text-sm"
        />
        <button type="submit" className="rounded-md border px-4 py-2 text-sm hover:bg-gray-50">
          Create
        </button>
      </form>

      {/* Product list */}
      {isLoading ? (
        <p>Loading products…</p>
      ) : (
        <ul className="divide-y rounded-md border">
          {products.map((p) => (
            <li key={p.id} className="flex items-center justify-between p-3">
              <div>
                <div className="font-medium">{p.name}</div>
                <div className="text-sm text-gray-600">${p.price} – Stock: {p.stockQty}</div>
              </div>
              <button
                onClick={() => handleDelete(p.id)}
                className="rounded-md border px-3 py-1.5 text-sm hover:bg-red-50 hover:text-red-700"
              >
                Delete
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}
