// Fake database
const DB = [
  { id: 1, name: "Keyboard", description: "Mechanical", price: 59.9, stock: 12 },
  { id: 2, name: "Mouse", description: "Wireless", price: 29.9, stock: 30 },
  { id: 3, name: "Monitor", description: "27\" IPS", price: 199.0, stock: 7 },
];

// helper for random failyre (to try error states)
const sometimesFail = () => Math.random() < 0.1; // 10%

export async function fetchProducts() {
  // stimulate latency 500–900ms
  const delay = 500 + Math.random() * 400;
  await new Promise(r => setTimeout(r, delay));

  if (sometimesFail()) {
    const err = new Error("Mock API error while fetching products");
    err.status = 500;
    throw err;
  }

  // deep clone not to corrupt state by mistake
  return JSON.parse(JSON.stringify(DB));
}
