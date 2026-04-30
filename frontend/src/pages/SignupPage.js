import { useState } from "react";
import { useNavigate } from "react-router-dom";

function SignupPage() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const navigate = useNavigate();

  const handleSignup = async () => {
    try {
      const res = await fetch("http://localhost:8080/api/customer/signup", {
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
        <h2>☕ Signup</h2>

        <input placeholder="Name" onChange={e => setName(e.target.value)} />
        <input placeholder="Email" onChange={e => setEmail(e.target.value)} />
        <input type="password" placeholder="Password" onChange={e => setPassword(e.target.value)} />

        <button onClick={handleSignup}>Sign Up</button>
      </div>
    </div>
  );
}

export default SignupPage;