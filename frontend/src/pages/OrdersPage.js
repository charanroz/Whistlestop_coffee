import { useEffect, useState } from "react";

const API = "https://whistlestop-coffee.onrender.com";
//test API
//const API = "http://localhost:8080";
function OrdersPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [lastUpdateTime, setLastUpdateTime] = useState(0);

  //check Order_state for cancelling
  const [cancellingOrderId, setCancellingOrderId] = useState(null);
  //stable sort
  const sortOrders = (list) => {
    return [...list].sort((a, b) => a.id - b.id);
  };

  useEffect(() => {
    const fetchOrders = () => {
      if (Date.now() - lastUpdateTime < 2000) return;

      fetch(`${API}/orders`)
        .then(res => res.json())
        .then(data => {
          setOrders(sortOrders(data));
          setLoading(false);
        })
        .catch(err => {
          console.error(err);
          setLoading(false);
        });
    };

    fetchOrders();

    const interval = setInterval(fetchOrders, 3000);
    return () => clearInterval(interval);
  }, [lastUpdateTime]);

  //  hide archived orders
  const visibleOrders = orders.filter(
    o => o.status !== "Collected" && o.status !== "Cancelled"
  );

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
    await fetch(`${API}/orders/${orderId}/status?status=${status}`, {
      method: "PUT"
    });

    setOrders(prev =>
      sortOrders(
        prev.map(o =>
          o.id === orderId ? { ...o, status } : o
        )
      )
    );

    setLastUpdateTime(Date.now());
  };

  const submitCancel = async (orderId, reason) => {
    try{
    const response = await fetch(`${API}/orders/${orderId}/staff-cancel?reason=${reason}`, {
      method: "PUT"
    });
      // check the time from backend
      if (!response.ok) {
        alert("cancellation is failed !\n Please check customer is late more than 15 minutes.");
        return;
      }
    setOrders(prev =>
      sortOrders(
        prev.map(o =>
          o.id === orderId ? { ...o, status: "Cancelled" } : o
        )
      )
    );

    setLastUpdateTime(Date.now());
    setCancellingOrderId(null);

      } catch (error) {
        console.error("Cancel API error:", error);
        alert("connection is failed. \n Please try again.");
      }
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

      {loading ? (
        <p style={{ textAlign: "center" }}>Loading orders...</p>
      ) : visibleOrders.length === 0 ? (
        <p style={{ textAlign: "center" }}>No active orders</p>
      ) : null}

      <div style={{
        display: "grid",
        gridTemplateColumns: "repeat(auto-fit, minmax(320px, 1fr))",
        gap: "20px"
      }}>

        {visibleOrders.map(order => (
          <div key={order.id} style={{
            background: "white",
            borderRadius: "20px",
            padding: "20px",
            boxShadow: "0 6px 20px rgba(0,0,0,0.1)",
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
                  - £{item.unitPrice?.toFixed(2)}
                </li>
              ))}
            </ul>

            <div style={{
              marginTop: "10px",
              display: "flex",
              flexWrap: "wrap",
              gap: "5px"
            }}>
              {/* fix：click Cancel and open reason option */}
              {cancellingOrderId === order.id ? (
                  <div style={{ display: "flex", flexDirection: "column", gap: "8px", width: "100%" }}>
                    <p style={{ margin: "5px 0", fontWeight: "bold", color: "#e74c3c" }}>Which is the reason for cancellation?：</p>
                    <button
                        style={{ ...buttonStyle, background: "#f39c12", color: "white" }}
                        onClick={() => submitCancel(order.id, "OUT_OF_STOCK")}
                    >
                      1. Out of stock
                    </button>
                    <button
                        style={{ ...buttonStyle, background: "#f39c12", color: "white" }}
                        onClick={() => submitCancel(order.id, "CUSTOMER_LATE")}
                    >
                      2. Late 15+ minutes
                    </button>
                    <button
                        style={{ ...buttonStyle, background: "#95a5a6", color: "white" }}
                        onClick={() => setCancellingOrderId(null)} // return
                    >
                      Back
                    </button>
                  </div>
              ) : (
                  <>
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

              <p><strong>Total: £{order.total?.toFixed(2)}</strong></p>

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
                onClick={() => setCancellingOrderId(order.id)}
              >
                Cancel
              </button>
</>
)}
            </div>

          </div>
        ))}
      </div>
    </div>
  );
}

export default OrdersPage;