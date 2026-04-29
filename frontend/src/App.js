import React from 'react';
import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
import MenuPage from "./pages/MenuPage";
import OrdersPage from "./pages/OrdersPage";
import CancelPage from "./pages/CancelPage";
import CheckoutPage from "./pages/CheckoutPage";

function App() {
  return (
    <BrowserRouter>
      <nav style={{ padding: "10px" }}>
        <Link to="/">Menu</Link> |{" "}
        <Link to="/orders">Orders</Link>
        <Link to="/cancel">Cancel Order</Link>
      </nav>

      <Routes>
        <Route path="/" element={<MenuPage />} />
        <Route path="/orders" element={<OrdersPage />} />
        <Route path="/checkout/:orderId" element={<CheckoutPage/>} />
        <Route path="/cancel" element={<CancelPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;