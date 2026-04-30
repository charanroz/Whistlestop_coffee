import React from 'react';
import { BrowserRouter, Routes, Route, Link, useLocation } from "react-router-dom";

import MenuPage from "./pages/MenuPage";
import OrdersPage from "./pages/OrdersPage";
import CancelPage from "./pages/CancelPage";
import CheckoutPage from "./pages/CheckoutPage";
import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/SignupPage";
import MyOrdersPage from "./pages/MyOrdersPage";
import ProtectedRoute from "./components/ProtectedRoute";


function Layout() {
  const location = useLocation();
  const user = JSON.parse(localStorage.getItem("user") || "null");

  const hideNavbar =
    location.pathname === "/" ||
    location.pathname === "/login" ||
    location.pathname === "/signup";

  return (
    <>
      {!hideNavbar && (
        <nav style={{
          padding: "15px",
          background: "#f5f5f5",
          marginBottom: "20px"
        }}>

          {/* CUSTOMER NAV */}
          {user?.role === "CUSTOMER" && (
            <>
              <Link to="/menu" style={{ marginRight: "15px" }}>Menu</Link>
              <Link to="/my-orders" style={{ marginRight: "15px" }}>My Orders</Link>
            </>
          )}

          {/* STAFF NAV */}
          {user?.role === "STAFF" && (
            <>
              <Link to="/orders" style={{ marginRight: "15px" }}>Dashboard</Link>
              <Link to="/cancel" style={{ marginRight: "15px" }}>Cancel Order</Link>
            </>
          )}

          {/* LOGOUT */}
          {user && (
            <button
              onClick={() => {
                localStorage.removeItem("user");
                window.location.href = "/login";
              }}
              style={{ marginLeft: "20px" }}
            >
              Logout
            </button>
          )}
        </nav>
      )}

      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />

        {/* CUSTOMER */}
        <Route
          path="/menu"
          element={
            <ProtectedRoute role="CUSTOMER">
              <MenuPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/my-orders"
          element={
            <ProtectedRoute role="CUSTOMER">
              <MyOrdersPage />
            </ProtectedRoute>
          }
        />

        {/* STAFF */}
        <Route
          path="/orders"
          element={
            <ProtectedRoute role="STAFF">
              <OrdersPage />
            </ProtectedRoute>
          }
        />

        {/* SHARED */}
        <Route path="/checkout/:orderId" element={<CheckoutPage />} />
        <Route path="/cancel" element={<CancelPage />} />
      </Routes>
    </>
  );
}

function App() {
  return (
    <BrowserRouter>
      <Layout />
    </BrowserRouter>
  );
}

export default App;