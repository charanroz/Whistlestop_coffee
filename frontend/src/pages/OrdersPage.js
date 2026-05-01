import { useEffect, useState } from "react";

function OrdersPage() {
  const [orders, setOrders] = useState([]);

  useEffect(() => {
    const fetchOrders = () => {
      fetch("http://localhost:8080/orders")
        .then(res => res.json())
        .then(data => setOrders(data))
        .catch(err => console.error(err));
    };

    fetchOrders();
    const interval = setInterval(fetchOrders, 3000);
    return () => clearInterval(interval);
  }, []);

  const getStatusStyle = (status) => {
    switch (status) {
      case "Pending": return { background: "#ffe0b2", color: "#e65100" };
      case "Accepted": return { background: "#bbdefb", color: "#0d47a1" };
      case "In Progress": return { background: "#e1bee7", color: "#4a148c" };
      case "Ready for Collection": return { background: "#c8e6c9", color: "#1b5e20" };
      case "Collected": return { background: "#eeeeee", color: "#424242" };
      case "Cancelled": return { background: "#ffcdd2", color: "#b71c1c" };
      default: return {};
    }
  };

  const updateStatus = async (orderId, status) => {
    await fetch(`http://localhost:8080/orders/${orderId}/status?status=${status}`, {
      method: "PUT"
    });
  };

  const cancelOrder = async (orderId) => {
    await fetch(`http://localhost:8080/orders/${orderId}/staff-cancel?reason=OUT_OF_STOCK`, {
      method: "PUT"
    });
  };

  const buttonStyle = {
    padding: "6px 10px",
    borderRadius: "10px",
    border: "none",
    cursor: "pointer",
    fontSize: "12px"
  };

  return (
    <div style={{
      padding: "20px",
      background: "#f5f1eb",
      minHeight: "100vh"
    }}>
      <h1 style={{ textAlign: "center" }}>☕ Staff Dashboard</h1>

      {orders.length === 0 && (
        <p style={{ textAlign: "center" }}>No orders yet</p>
      )}

      <div style={{
        display: "grid",
        gridTemplateColumns: "repeat(auto-fit, minmax(320px, 1fr))",
        gap: "20px"
      }}>

        {orders.map(order => (
          <div key={order.id} style={{
            background: "white",
            borderRadius: "20px",
            padding: "20px",
            boxShadow: "0 6px 20px rgba(0,0,0,0.1)",

            //highlight READY orders
            border:
              order.status === "Ready for Collection"
                ? "3px solid green"
                : "none"
          }}>

            <h3>Order #{order.id}</h3>

            <span style={{
              ...getStatusStyle(order.status),
              padding: "6px 12px",
              borderRadius: "20px",
              fontWeight: "bold",
              display: "inline-block",
              marginBottom: "10px"
            }}>
              {order.status}
            </span>

            <p><strong>Pickup:</strong> {order.pickupTime}</p>
            {order.trainId && (
              <p style={{ color: "#e65100", fontWeight: "bold" }}>
                🚆 Train: {order.trainId}
                <br />
                ⏳ Est. Arrival: {order.estimatedArrivalTime}
              </p>
            )}

            <ul>
              {order.items?.map((item, i) => (
                <li key={i}>
                  {item.menuItem?.name} ({item.size}) x{item.quantity}
                  - £{item.price}
                </li>
              ))}
            </ul>

            <div style={{ marginTop: "10px", display: "flex", flexWrap: "wrap", gap: "5px" }}>

              <button
                style={buttonStyle}
                disabled={order.status !== "Pending"}
                onClick={() => updateStatus(order.id, "Accepted")}
              >
                Accept
              </button>

              <button
                style={buttonStyle}
                disabled={order.status !== "Accepted"}
                onClick={() => updateStatus(order.id, "In Progress")}
              >
                In Progress
              </button>

              <button
                style={buttonStyle}
                disabled={order.status !== "In Progress"}
                onClick={() => updateStatus(order.id, "Ready for Collection")}
              >
                Ready
              </button>

              <p><strong>Total: £{order.totalPrice?.toFixed(2)}</strong></p>

              <button
                style={buttonStyle}
                disabled={order.status !== "Ready for Collection"}
                onClick={() => updateStatus(order.id, "Collected")}
              >
                Collected
              </button>

              <button
                style={{
                  ...buttonStyle,
                  background: "#e74c3c",
                  color: "white"
                }}
                disabled={order.status === "Collected" || order.status === "Cancelled"}
                onClick={() => cancelOrder(order.id)}
              >
                Cancel
              </button>

            </div>

          </div>
        ))}
      </div>
    </div>
  );
}

export default OrdersPage;