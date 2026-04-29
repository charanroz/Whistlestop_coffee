import React, { useState } from 'react';
import { useParams } from 'react-router-dom';

function CheckoutPage() {
    // get order ID from URL
    const { orderId } = useParams();
    const [paymentResult, setPaymentResult] = useState(null);
    const [errorMessage, setErrorMessage] = useState('');

    const handlePayment = async () => {
        try {
            // PaymentController API
            const response = await fetch(`http://localhost:8080/payments/process?orderId=${orderId}`, {
                method: 'POST',
            });

            if (response.ok) {
                // if successful, the backend will return a JSON object named "Payment".
                const paymentData = await response.json();
                setPaymentResult(paymentData);
                setErrorMessage('');
            } else {
                const errorText = await response.text();
                setErrorMessage(errorText);
                setPaymentResult(null);
            }
        } catch (error) {
            setErrorMessage("connection failed");
        }
    };

    return (
        <div style={{ padding: '20px', maxWidth: '400px', margin: '0 auto' }}>
            <h2>🛒 Order List</h2>
            <p>Order ID: {orderId}</p>

            <button
                onClick={handlePayment}
                style={{ padding: '10px 20px', backgroundColor: '#4CAF50', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}
            >
                Use HorsePay payment
            </button>

            {/* error message */}
            {errorMessage && <p style={{ color: 'red' }}>{errorMessage}</p>}

            {/* payment detail */}
            {paymentResult && (
                <div style={{ marginTop: '20px', padding: '15px', border: '1px solid #ddd', borderRadius: '8px', backgroundColor: '#f9f9f9' }}>
                    <h3 style={{ color: '#2ecc71' }}>Payment successful !</h3>
                    <p><strong>payment ID：</strong> {paymentResult.paymentId}</p>
                    <p><strong>Order ID：</strong> {paymentResult.orderId}</p>
                    <p><strong>Time：</strong> {new Date(paymentResult.confirmedTime).toLocaleString()}</p>
                </div>
            )}
        </div>
    );
}

export default CheckoutPage;