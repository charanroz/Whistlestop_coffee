import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import API from "../api";

function CheckoutPage() {
  const { orderId } = useParams();
  const navigate = useNavigate();

  const [paymentResult, setPaymentResult] = useState(null);
  const [errorMessage, setErrorMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const formatPaymentTime = (value) => {
    if (!value) return "Just now";
    return new Date(value).toLocaleString();
  };

  const handlePayment = async () => {
    setLoading(true);

    try {
      const response = await fetch(
        `${API}/payments/process?orderId=${orderId}`,
         { method: "POST" }
      );

      if (response.ok) {
        const paymentData = await response.json();
        setPaymentResult(paymentData);
        setErrorMessage("");

        // ✅ redirect after success
        setTimeout(() => {
          navigate("/orders");
        }, 2000);
      } else {
        const errorText = await response.text();
        setErrorMessage(errorText);
      }
    } catch (error) {
      setErrorMessage("Server not responding. Check backend.");
    }

    setLoading(false);
  };

  return (
    <div style={{ padding: "20px", maxWidth: "400px", margin: "auto" }}>
      <h2>💳 Checkout</h2>

      <p><strong>Order ID:</strong> {orderId}</p>

      <button
        onClick={handlePayment}
        disabled={loading || paymentResult}
        style={{
          padding: "10px",
          width: "100%",
          backgroundColor: "#4CAF50",
          color: "white",
          border: "none",
          borderRadius: "5px"
        }}
      >
        {loading ? "Processing..." : "Pay with HorsePay"}
      </button>

      {errorMessage && (
        <p style={{ color: "red", marginTop: "10px" }}>
          {errorMessage}
        </p>
      )}

      {paymentResult && (
        <div style={{
          marginTop: "20px",
          padding: "15px",
          border: "1px solid #ddd",
          borderRadius: "8px",
          backgroundColor: "#f9f9f9"
        }}>
          <h3 style={{ color: "green" }}> Payment Successful</h3>
          <p><strong>Payment ID:</strong> {paymentResult.paymentId}</p>
          <p><strong>Order ID:</strong> {paymentResult.orderId}</p>
          <p>
            <strong>Time:</strong>{" "}
            {formatPaymentTime(paymentResult.confirmedTime)}
          </p>
        </div>
      )}
    </div>
  );
}

export default CheckoutPage;
