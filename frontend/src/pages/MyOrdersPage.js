import { useEffect, useState } from "react";

function MyOrdersPage() {
  const [orders, setOrders] = useState([]);

  //  store user in state
  const [user] = useState(() =>
    JSON.parse(localStorage.getItem("user") || "null")
  );

  useEffect(() => {
    if (!user) return;

    const fetchOrders = () => {
      fetch(`http://18.130.223.148:8080/orders/customer/${user.id}`)
        .then(res => res.json())
        .then(data => setOrders(data))
        .catch(err => console.error(err));
    };

    fetchOrders();

    const interval = setInterval(fetchOrders, 5000); // slower

    return () => clearInterval(interval);
  }, [user]);


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


  const getStatusMessage = (status) => {
    switch (status) {
      case "Pending": return "Your order has been placed ☕";
      case "Accepted": return "Barista accepted your order ";
      case "In Progress": return "Your coffee is being prepared ";
      case "Ready for Collection": return "Ready for pickup ";
      case "Collected": return "Enjoy your coffee ";
      case "Cancelled": return "Order cancelled ";
      default: return "";
    }
  };

  return (
    <div style={{
      padding: "20px",
      background: "#f5f1eb",
      minHeight: "100vh"
    }}>
      <h1 style={{ textAlign: "center" }}> My Orders</h1>

      {!user && (
        <p style={{ textAlign: "center", color: "red" }}>
          Please login to view your orders
        </p>
      )}

      {user && orders.length === 0 && (
        <p style={{ textAlign: "center" }}>No orders yet</p>
      )}

      {orders.map(order => (
        <div key={order.id} style={{
          background: "white",
          padding: "20px",
          margin: "20px auto",
          borderRadius: "20px",
          width: "350px",
          boxShadow: "0 6px 20px rgba(0,0,0,0.1)",
          border:
            order.status === "Ready for Collection"
              ? "3px solid green"
              : "none"
        }}>
          <h3>Order #{order.id}</h3>

          <div style={{
            ...getStatusStyle(order.status),
            padding: "6px 12px",
            borderRadius: "20px",
            display: "inline-block",
            fontWeight: "bold",
            marginBottom: "10px"
          }}>
            {order.status}
          </div>

          <p>{getStatusMessage(order.status)}</p>

          <p><strong>Pickup:</strong> {order.pickupTime}</p>
          {order.trainId && (
            <p style={{ color: "#e65100", fontWeight: "bold" }}>
              🚆 Train: {order.trainId}
              <br />
              ⏳ Est. Arrival: {order.estimatedArrivalTime}
            </p>
          )}
        </div>
      ))}
    </div>
  );
}

export default MyOrdersPage;