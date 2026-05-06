import { useState } from "react";
import { useNavigate } from "react-router-dom";
import API from "../api";

function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("CUSTOMER");

  const navigate = useNavigate();

  const handleLogin = async () => {
    try {
      const url =
        role === "STAFF"
          ? `${API}/api/staff/login`
          : `${API}/api/customer/login`;

      const res = await fetch(url, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          email,
          password
        })
      });

      // Handle non OK responses
      if (!res.ok) {
        throw new Error("Server error: " + res.status);
      }

      const data = await res.json();

      console.log("Login response:", data);

      if (!data.success) {
        alert(data.message);
        return;
      }

      const user = data.customer || data.staff;

      localStorage.setItem(
        "user",
        JSON.stringify({
          ...user,
          role
        })
      );

      if (role === "STAFF") {
        navigate("/orders");
      } else {
        navigate("/menu");
      }

    } catch (error) {
      console.error("Login error:", error);
      alert("Cannot connect to server. Make sure backend is running.");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-[#f5f1eb]">
      <div className="bg-white p-8 rounded-2xl shadow-xl w-[350px]">

        <h1 className="text-2xl font-bold text-center mb-6">
          Whistlestop
        </h1>

        <select
          value={role}
          onChange={e => setRole(e.target.value)}
          className="w-full p-3 border rounded-lg mb-3"
        >
          <option value="CUSTOMER">Customer</option>
          <option value="STAFF">Staff</option>
        </select>

        <input
          placeholder="Email"
          onChange={e => setEmail(e.target.value)}
          className="w-full p-3 border rounded-lg mb-3"
        />

        <input
          type="password"
          placeholder="Password"
          onChange={e => setPassword(e.target.value)}
          className="w-full p-3 border rounded-lg mb-4"
        />

        <button
          onClick={handleLogin}
          className="w-full py-3 bg-[#6f4e37] text-white rounded-lg hover:bg-[#5a3d2b]"
        >
          Login
        </button>

        <p className="text-center mt-4 text-sm">
          New user?{" "}
          <span
            onClick={() => navigate("/signup")}
            className="text-blue-500 cursor-pointer"
          >
            Signup
          </span>
        </p>
      </div>
    </div>
  );
}

export default LoginPage;
