import { useEffect, useState } from "react";

function OrdersPage() {
  const [orders, setOrders] = useState([]);

useEffect(() => {
  const fetchOrders = () => {
    fetch("http://localhost:8080/orders")
      .then(res => res.json())
      .then(data => {
        console.log("ORDERS RESPONSE:", data);

        if (Array.isArray(data)) {
          setOrders(data);
        } else if (data.orders) {
          setOrders(data.orders);
        } else {
          setOrders([]);
        }
      })
      .catch(err => console.error(err));
  };

  fetchOrders();

  const interval = setInterval(fetchOrders, 3000);

  return () => clearInterval(interval);
}, []);

  const getStatusColor = (status) => {
    if (status === "Pending") return "orange";
    if (status === "Cancelled") return "red";
    return "green";
  };


  return (
    <div style={{ padding: "20px" }}>
      <h1>Orders</h1>

      {orders.length === 0 && <p>No orders yet</p>}

      {orders.map(order => (
        <div
          key={order.id}
          style={{
            border: "1px solid #ccc",
            margin: "10px 0",
            padding: "10px",
            borderRadius: "8px"
          }}
        >
          <p><strong>ID:</strong> {order.id}</p>

          <p>
            <strong>Status:</strong>{" "}
            <span style={{ color: getStatusColor(order.status) }}>
              {order.status}
            </span>
          </p>

          <p><strong>Pickup:</strong> {order.pickupTime}</p>
          <p><strong>Items:</strong></p>

          <ul>
            {order.items && order.items.map((item, index) => (
             <li key={index}>
               {item.itemName} ({item.size}) x{item.quantity} - £{item.price}
             </li>
            ))}
          </ul>
          <p>
            <strong>Total:</strong> £{order.totalPrice.toFixed(2)}
          </p>
        </div>
      ))}
    </div>
  );
}

export default OrdersPage;