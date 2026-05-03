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
        <nav className="flex flex-wrap items-center justify-between p-4 bg-[#f5f5f5] mb-5 gap-y-3">

         <div className="flex flex-wrap gap-4">
          {/* CUSTOMER NAV */}
          {user?.role === "CUSTOMER" && (
           <>
            <Link to="/menu" className="font-medium text-gray-700 hover:text-[#6f4e37] no-underline">Menu</Link>
            <Link to="/my-orders" className="font-medium text-gray-700 hover:text-[#6f4e37] no-underline">My Orders</Link>
           </>
          )}

          {/* STAFF NAV */}
          {user?.role === "STAFF" && (
            <>
              <Link to="/orders" className="font-medium text-gray-700 hover:text-[#6f4e37] no-underline">Dashboard</Link>
              <Link to="/cancel" className="font-medium text-gray-700 hover:text-[#6f4e37] no-underline">Cancel Order</Link>
            </>
          )}
          </div>

          {/* LOGOUT */}
          {user && (
            <button
              onClick={() => {
                localStorage.removeItem("user");
                window.location.href = "/login";
              }}
              className="bg-red-500 text-white px-4 py-2 rounded-lg text-sm font-medium border-none cursor-pointer hover:bg-red-600 transition"
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