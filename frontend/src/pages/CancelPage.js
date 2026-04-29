// src/pages/CancelPage.js
import React, { useState } from 'react';

function CancelPage() {
    const [orderId, setOrderId] = useState('');
    const [reason, setReason] = useState('NO_SHOW');
    const [message, setMessage] = useState('');

    const handleCancelOrder = async () => {
        if (!orderId) {
            setMessage('type order ID');
            return;
        }

        setMessage('loading...');

        try {
            const response = await fetch(`http://localhost:8080/orders/${orderId}/staff-cancel?reason=${reason}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if (response.ok) {
                const data = await response.text();
                setMessage(`successful：${data}`);
            } else {
                const errorData = await response.text();
                setMessage(`fail：${errorData}`);
            }
        } catch (error) {
            console.error("connection failed:", error);
            setMessage('connection failed, please check Spring Boot turning on。');
        }
    };

    return (
        <div style={{ padding: '40px', fontFamily: 'sans-serif', maxWidth: '400px', margin: '0 auto' }}>
            <h2>staff：cancel order</h2>

            <div style={{ marginBottom: '15px', textAlign: 'left' }}>
                <label style={{ display: 'block', marginBottom: '5px' }}>order ID：</label>
                <input
                    type="number"
                    value={orderId}
                    onChange={(e) => setOrderId(e.target.value)}
                    placeholder="ex：1"
                    style={{ width: '100%', padding: '8px' }}
                />
            </div>

            <div style={{ marginBottom: '20px', textAlign: 'left' }}>
                <label style={{ display: 'block', marginBottom: '5px' }}>cancellation reason：</label>
                <select
                    value={reason}
                    onChange={(e) => setReason(e.target.value)}
                    style={{ width: '100%', padding: '8px' }}
                >
                    <option value="NO_SHOW">customer did not take the order(over 15 minutes)</option>
                    <option value="OUT_OF_STOCK">the item is sold out</option>
                </select>
            </div>

            <button
                onClick={handleCancelOrder}
                style={{ width: '100%', padding: '10px', backgroundColor: '#e74c3c', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer', fontSize: '16px' }}
            >
                Cancel order.
            </button>

            {message && (
                <div style={{ marginTop: '20px', padding: '15px', backgroundColor: '#f8f9fa', border: '1px solid #ddd', borderRadius: '5px', textAlign: 'left' }}>
                    {message}
                </div>
            )}
        </div>
    );
}

export default CancelPage;