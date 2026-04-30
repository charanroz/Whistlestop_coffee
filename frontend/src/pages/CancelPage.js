import React, { useState } from "react";

function CancelPage() {
  const [orderId, setOrderId] = useState("");
  const [reason, setReason] = useState("no_show");
  const [message, setMessage] = useState("");

  const handleCancelOrder = async () => {
    if (!orderId) {
      setMessage("Please enter an Order ID");
      return;
    }

    setMessage("Processing...");

    try {
      const response = await fetch(
        `http://localhost:8080/orders/${orderId}/cancel?reason=${reason}`,
        {
          method: "PUT"
        }
      );

      if (response.ok) {
        setMessage(" Order cancelled successfully");
      } else {
        const errorText = await response.text();
        setMessage("❌ " + errorText);
      }
    } catch (error) {
      setMessage("❌ Server error. Check backend.");
    }
  };

  return (
    <div style={{ padding: "20px", maxWidth: "400px", margin: "auto" }}>
      <h2> Cancel Order</h2>

      <input
        type="number"
        placeholder="Enter Order ID"
        value={orderId}
        onChange={(e) => setOrderId(e.target.value)}
        style={{ width: "100%", padding: "10px", marginBottom: "10px" }}
      />

      <select
        value={reason}
        onChange={(e) => setReason(e.target.value)}
        style={{ width: "100%", padding: "10px", marginBottom: "10px" }}
      >
        <option value="no_show">No Show (15 min passed)</option>
        <option value="out_of_stock">Out of Stock</option>
      </select>

      <button
        onClick={handleCancelOrder}
        style={{
          width: "100%",
          padding: "10px",
          backgroundColor: "#e74c3c",
          color: "white",
          border: "none",
          borderRadius: "5px"
        }}
      >
        Cancel Order
      </button>

      {message && (
        <p style={{ marginTop: "15px" }}>{message}</p>
      )}
    </div>
  );
}

export default CancelPage;