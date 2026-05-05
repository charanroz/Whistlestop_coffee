import { useState } from "react";
import { useNavigate } from "react-router-dom";
const API = "https://whistlestop-coffee.onrender.com";

function SignupPage() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const navigate = useNavigate();

  const handleSignup = async () => {
    try {
      const res = await fetch(`${API}/api/customer/signup`,{
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({ name, email, password })
      });

      const data = await res.json();

      if (!data.success) {
        alert(data.message);
        return;
      }

      localStorage.setItem("user", JSON.stringify({
        id: data.customer.id,
        name: data.customer.name,
        role: "CUSTOMER"
      }));

      navigate("/menu");

    } catch (err) {
      alert("Signup failed");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-[#f5f1eb]">

      <div className="bg-white p-8 rounded-2xl shadow-xl w-[360px]">

        <h1 className="text-2xl font-bold text-center mb-6">
          Create Account
        </h1>

        <input
          placeholder="Full Name"
          value={name}
          onChange={e => setName(e.target.value)}
          className="w-full p-3 border rounded-lg mb-3 focus:outline-none focus:ring-2 focus:ring-[#6f4e37]"
        />

        <input
          placeholder="Email Address"
          value={email}
          onChange={e => setEmail(e.target.value)}
          className="w-full p-3 border rounded-lg mb-3 focus:outline-none focus:ring-2 focus:ring-[#6f4e37]"
        />

        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={e => setPassword(e.target.value)}
          className="w-full p-3 border rounded-lg mb-4 focus:outline-none focus:ring-2 focus:ring-[#6f4e37]"
        />

        <button
          onClick={handleSignup}
          className="w-full py-3 bg-[#6f4e37] text-white rounded-lg hover:bg-[#5a3d2b] transition"
        >
          Sign Up
        </button>

        <p className="text-center mt-4 text-sm text-gray-600">
          Already have an account?{" "}
          <span
            onClick={() => navigate("/")}
            className="text-blue-500 cursor-pointer hover:underline"
          >
            Login
          </span>
        </p>

      </div>
    </div>
  );
}

export default SignupPage;