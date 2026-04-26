import { useEffect, useState } from "react";

function App() {
  const [menu, setMenu] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/menu")
      .then(res => res.json())
      .then(data => setMenu(data));
  }, []);

  return (
    <div style={{ padding: "20px" }}>
      <h1>☕ Menu</h1>

      {menu.map(item => (
        <div
          key={item.id}
          style={{
            border: "1px solid #ccc",
            padding: "10px",
            margin: "10px 0",
            borderRadius: "8px"
          }}
        >
          <h3>{item.name}</h3>
          <p>Price: £{item.price}</p>
          <button>Order</button>
        </div>
      ))}
    </div>
  );
}

export default App;