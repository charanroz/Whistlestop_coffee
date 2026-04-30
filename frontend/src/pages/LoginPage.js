import { useState } from "react";
import { useNavigate } from "react-router-dom";

function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("CUSTOMER");

  const navigate = useNavigate();

  const handleLogin = async () => {
    try {
      const url =
        role === "STAFF"
          ? "http://localhost:8080/api/staff/login"
          : "http://localhost:8080/api/customer/login";

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

      localStorage.setItem(
        "user",
        JSON.stringify({
          ...data.customer,
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
    <div style={{
      height: "100vh",
      display: "flex",
      justifyContent: "center",
      alignItems: "center",
      background: "#f5f1eb"
    }}>
      <div style={{
        background: "white",
        padding: "40px",
        borderRadius: "15px",
        width: "300px",
        textAlign: "center"
      }}>
        <h2>☕ Whistlestop</h2>

        {/* KEEP ONLY ONE ROLE SELECT */}
        <select
          value={role}
          onChange={e => setRole(e.target.value)}
          style={{ width: "100%", padding: "10px", marginBottom: "10px" }}
        >
          <option value="CUSTOMER">Customer</option>
          <option value="STAFF">Staff</option>
        </select>

        <input
          placeholder="Email"
          onChange={e => setEmail(e.target.value)}
          style={{ width: "100%", padding: "10px", marginBottom: "10px" }}
        />

        <input
          type="password"
          placeholder="Password"
          onChange={e => setPassword(e.target.value)}
          style={{ width: "100%", padding: "10px", marginBottom: "20px" }}
        />

        <button
          onClick={handleLogin}
          style={{
            width: "100%",
            padding: "12px",
            background: "#6f4e37",
            color: "white",
            borderRadius: "10px"
          }}
        >
          Login
        </button>

        <p style={{ marginTop: "10px" }}>
          New user?{" "}
          <span
            onClick={() => navigate("/signup")}
            style={{ color: "blue", cursor: "pointer" }}
          >
            Signup
          </span>
        </p>
      </div>
    </div>
  );
}

export default LoginPage;