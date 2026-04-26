import { useEffect, useState } from "react";

function MenuPage() {
  const [menu, setMenu] = useState([]);
  const [selectedSizes, setSelectedSizes] = useState({});


  useEffect(() => {
    fetch("http://localhost:8080/menu")
      .then(res => res.json())
      .then(data => setMenu(data))
      .catch(err => console.error(err));
  }, []);

  const handleSizeChange = (itemId, size) => {

    setSelectedSizes(prev => ({
      ...prev,
      [itemId]: size
    }));
  };

const handleOrder = async (item) => {

  const selectedSize = selectedSizes[item.id] || "Regular";

  const order = {
    customer: { id: 1 },
    pickupTime: "10:00",
    items: [
      {
        menuItemId: item.id,
        size: selectedSize,
        quantity: 1
      }
    ]
  };

  console.log("FINAL ORDER:", JSON.stringify(order, null, 2));

  try {
    const res = await fetch("http://localhost:8080/orders", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(order)
    });

    const text = await res.text();
    console.log("RESPONSE:", text);

    if (!res.ok) {
      alert("Order failed");
      return;
    }

    alert("Order placed!");
  } catch (err) {
    console.error(err);
  }
};


  return (
    <div style={{ padding: "20px" }}>
      <h1>☕ Menu</h1>

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
          <p>Large: £{item.priceLarge}</p>

          <select
            value={selectedSizes[item.id] || "Regular"}
            onChange={(e) => handleSizeChange(item.id, e.target.value)}
          >
            <option value="Regular">Regular</option>
            <option value="Large">Large</option>
          </select>

          <br />

          <button onClick={() => handleOrder(item)}>
            Order
          </button>
        </div>
      ))}
    </div>
  );
}

export default MenuPage;