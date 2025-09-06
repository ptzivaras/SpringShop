export function loadCartItems() {
  try {
    const raw = localStorage.getItem('cart')
    if (!raw) return []
    const parsed = JSON.parse(raw)
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

export function saveCartItems(items) {
  try {
    localStorage.setItem('cart', JSON.stringify(items))
  } catch {
    // ignore write errors (π.χ. quota)
  }
}
