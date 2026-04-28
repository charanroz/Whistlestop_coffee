import { useEffect, useState } from "react";

function MenuPage() {
  const [menu, setMenu] = useState([]);
  const [selectedSizes, setSelectedSizes] = useState({});
  const [cart, setCart] = useState([]);
  const [message, setMessage] = useState("");

  // Load menu
  useEffect(() => {
    fetch("http://localhost:8080/menu")
      .then(res => res.json())
      .then(data => setMenu(data))
      .catch(err => console.error(err));
  }, []);

  // Load cart from localStorage
  useEffect(() => {
    const saved = localStorage.getItem("cart");
    if (saved) setCart(JSON.parse(saved));
  }, []);

  // Save cart to localStorage
  useEffect(() => {
    localStorage.setItem("cart", JSON.stringify(cart));
  }, [cart]);

  const handleSizeChange = (itemId, size) => {
    setSelectedSizes(prev => ({
      ...prev,
      [itemId]: size
    }));
  };

  // Add to cart
  const addToCart = (item) => {
    const selectedSize = item.hasSize
      ? (selectedSizes[item.id] || "Regular")
      : "Regular";

    const price =
      selectedSize === "Large"
        ? item.priceLarge
        : item.priceRegular;

    setCart(prev => {
      const existing = prev.find(
        i => i.menuItemId === item.id && i.size === selectedSize
      );


      if (existing) {
        return prev.map(i =>
          i.menuItemId === item.id && i.size === selectedSize
            ? { ...i, quantity: i.quantity + 1 }
            : i
        );
      }

      return [
        ...prev,
        {
          menuItemId: item.id,
          name: item.name,
          size: selectedSize,
          quantity: 1,
          price
        }
      ];
    });

    setMessage("Added to cart ✅");

          setTimeout(() => {
            setMessage("");
          }, 2000);
  };


  // Change quantity
  const changeQty = (index, change) => {
    setCart(prev =>
      prev.map((item, i) =>
        i === index
          ? { ...item, quantity: Math.max(1, item.quantity + change) }
          : item
      )
    );
  };

  // Remove item
  const removeItem = (index) => {
    setCart(prev => prev.filter((_, i) => i !== index));
  };

  // Total
  const cartTotal = cart.reduce((total, item) => {
    return total + item.price * item.quantity;
  }, 0);

  // Place order
  const placeOrder = async () => {
    if (cart.length === 0) {
      alert("Cart is empty");
      return;
    }

    const order = {
      customer: { id: 1 },
      pickupTime: "10:00",
      items: cart.map(item => ({
        menuItemId: item.menuItemId,
        size: item.size,
        quantity: item.quantity
      }))
    };

    try {
      const res = await fetch("http://localhost:8080/orders", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(order)
      });

      if (!res.ok) {
        alert("Order failed");
        return;
      }

      alert("Order placed!");
      setCart([]);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div style={{ padding: "20px" }}>
      <h1>☕ Menu</h1>

      {message && (
        <p style={{
          background: "#d4edda",
          color: "#155724",
          padding: "10px",
          borderRadius: "5px",
          marginBottom: "10px"
        }}>
          {message}
        </p>
      )}

      {menu.map(item => (
        <div
          key={item.id}
          style={{
            border: "1px solid #ccc",
            padding: "15px",
            margin: "10px 0",
            borderRadius: "8px"
          }}
        >
          <h3>{item.name}</h3>

          <p>Regular: £{item.priceRegular}</p>

          {item.hasSize && (
            <p>Large: £{item.priceLarge}</p>
          )}

          {item.hasSize && (
            <select
              value={selectedSizes[item.id] || "Regular"}
              onChange={(e) =>
                handleSizeChange(item.id, e.target.value)
              }
            >
              <option value="Regular">Regular</option>
              <option value="Large">Large</option>
            </select>
          )}

          <br />

          <button onClick={() => addToCart(item)}>
            Add to Cart
          </button>
        </div>
      ))}

      <h2>🛒 Cart</h2>

      {cart.length === 0 && <p>Cart is empty</p>}

      <ul>
        {cart.map((item, index) => (
          <li key={index}>
            {item.name} ({item.size}) x{item.quantity} - £{item.price}

            <button onClick={() => changeQty(index, -1)}>➖</button>
            <button onClick={() => changeQty(index, 1)}>➕</button>
            <button onClick={() => removeItem(index)}>❌</button>
          </li>
        ))}
      </ul>

      <p><strong>Total: £{cartTotal.toFixed(2)}</strong></p>

      <button onClick={placeOrder}>
        Place Order
      </button>

      <button onClick={() => setCart([])}>
        Clear Cart
      </button>
    </div>
  );
}

export default MenuPage;