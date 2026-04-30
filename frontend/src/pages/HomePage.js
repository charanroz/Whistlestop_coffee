import { useNavigate } from "react-router-dom";

function HomePage() {
  const navigate = useNavigate();

  return (
    <div style={{
      height: "100vh",
      background: "#6f4e37",
      color: "white",
      display: "flex",
      flexDirection: "column",
      justifyContent: "center",
      alignItems: "center"
    }}>
      <h1 style={{ fontSize: "40px" }}>☕ Whistlestop Coffee</h1>

      <p style={{ marginTop: "10px" }}>
        Tap to start your order
      </p>

      <button
        onClick={() => navigate("/login")}
        style={{
          marginTop: "40px",
          padding: "20px 40px",
          fontSize: "20px",
          borderRadius: "15px",
          border: "none",
          background: "white",
          color: "#6f4e37",
          cursor: "pointer"
        }}
      >
        Start
      </button>
    </div>
  );
}

export default HomePage;