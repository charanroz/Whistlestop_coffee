import { useEffect, useState, useMemo } from "react";
import { useNavigate } from "react-router-dom";

function MenuPage() {
  const [menu, setMenu] = useState([]);
  const [cart, setCart] = useState([]);
  const [pickupTime, setPickupTime] = useState("");
  const [station, setStation] = useState(null);
  const [hours, setHours] = useState([]);
  const [selectedSizes, setSelectedSizes] = useState({});
  const [pickupType, setPickupType] = useState("time");
  const [trains, setTrains] = useState([]);
  const [selectedTrain, setSelectedTrain] = useState(null);
  const [trainsLoading, setTrainsLoading] = useState(false);

  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem("user"));

  // redirect if not logged in
  useEffect(() => {
    if (!user) navigate("/");
  }, []);

  // fetch menu, station, hours once
  useEffect(() => {
    fetch("http://localhost:8080/menu")
      .then(res => res.json())
      .then(setMenu)
      .catch(() => {});

    fetch("http://localhost:8080/station-setting")
      .then(res => res.json())
      .then(setStation)
      .catch(() => {});

    fetch("http://localhost:8080/business-hours")
      .then(res => res.json())
      .then(setHours)
      .catch(() => {});
  }, []);

  // fetch trains initially and every 60 seconds
  const fetchTrains = () => {
    setTrainsLoading(true);
    fetch("http://localhost:8080/trains/arrivals?stationName=Cramlington")
      .then(res => res.json())
      .then(data => {
        setTrains(data);
        setTrainsLoading(false);
        if (data.length > 0) {
          setSelectedTrain(prev => {
            if (!prev) return data[0];
            const refreshed = data.find(t => t.trainId === prev.trainId);
            return refreshed || data[0];
          });
        }
      })
      .catch(() => setTrainsLoading(false));
  };

  useEffect(() => {
    fetchTrains();
    const interval = setInterval(fetchTrains, 60000);
    return () => clearInterval(interval);
  }, []);

  // live clock
  const [currentTime, setCurrentTime] = useState(
    new Date().toTimeString().slice(0, 5)
  );
  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentTime(new Date().toTimeString().slice(0, 5));
    }, 60000);
    return () => clearInterval(interval);
  }, []);

  // business hours
  const today = new Date().toLocaleDateString("en-US", { weekday: "long" });
  const todayHours = hours.find(h => h.dayOfWeek === today);
  const isOpenNow =
    todayHours &&
    !todayHours.closed &&
    currentTime >= todayHours.openTime &&
    currentTime <= todayHours.closeTime;

  // time slots
  const timeSlots = useMemo(() => {
    if (!todayHours || todayHours.closed || !todayHours.openTime) return [];
    const slots = [];
    let [hour, minute] = todayHours.openTime.split(":").map(Number);
    const [closeHour, closeMinute] = todayHours.closeTime.split(":").map(Number);
    while (hour < closeHour || (hour === closeHour && minute <= closeMinute)) {
      const time = `${String(hour).padStart(2,"0")}:${String(minute).padStart(2,"0")}`;
      if (!isOpenNow || time >= currentTime) slots.push(time);
      minute += 5;
      if (minute >= 60) { minute = 0; hour++; }
    }
    return slots;
  }, [todayHours, currentTime, isOpenNow]);

  useEffect(() => {
    if (timeSlots.length > 0) setPickupTime(prev => prev || timeSlots[0]);
  }, [timeSlots]);

  const formatTime = (time) => {
    const [h, m] = time.split(":");
    const d = new Date();
    d.setHours(h, m);
    return d.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
  };

  const getDelayMinutes = (train) => {
    if (!train?.scheduledArrivalTime || !train?.estimatedArrivalTime) return 0;
    try {
      const [sh, sm] = train.scheduledArrivalTime.split(":").map(Number);
      const [eh, em] = train.estimatedArrivalTime.split(":").map(Number);
      return (eh * 60 + em) - (sh * 60 + sm);
    } catch { return 0; }
  };

  const getStatusBadge = (train) => {
    if (train.status === "Cancelled") return { bg: "#ef4444", label: "Cancelled" };
    const delay = getDelayMinutes(train);
    if (delay > 0) return { bg: "#f97316", label: `+${delay} min` };
    return { bg: "#22c55e", label: "On time" };
  };

  const imageMap = {
    "Americano": "https://images.unsplash.com/photo-1580661869408-55ab23f2ca6e?w=500",
    "Americano with milk": "https://images.unsplash.com/photo-1565434007235-d3a18c2e0954?w=500",
    "Latte": "https://images.unsplash.com/photo-1593443320739-77f74939d0da?w=500",
    "Cappuccino": "https://images.unsplash.com/photo-1512568400610-62da28bc8a13?w=500",
    "Hot Chocolate": "https://images.unsplash.com/photo-1608651057580-4a50b2fc2281?w=500",
    "Mocha": "https://images.unsplash.com/photo-1618576230663-9714aecfb99a?w=500",
    "Mineral Water": "https://plus.unsplash.com/premium_photo-1681236320994-3395e842ed81?w=500"
  };

  const addToCart = (item) => {
    const size = selectedSizes[item.id] || "Regular";
    const price = size === "Large" ? item.priceLarge : item.priceRegular;
    setCart(prev => {
      const ex = prev.find(i => i.id === item.id && i.size === size);
      if (ex) return prev.map(i => i.id === item.id && i.size === size ? { ...i, quantity: i.quantity + 1 } : i);
      return [...prev, { ...item, size, selectedPrice: price, quantity: 1 }];
    });
  };

  const updateQty = (index, delta) => {
    setCart(prev => prev.flatMap((item, i) => {
      if (i !== index) return [item];
      if (item.quantity + delta < 1) return [];
      return [{ ...item, quantity: item.quantity + delta }];
    }));
  };

  const cartTotal = cart.reduce((t, i) => t + i.selectedPrice * i.quantity, 0);

  const placeOrder = async () => {
    if (cart.length === 0) { alert("Cart is empty"); return; }
    if (pickupType === "train" && !selectedTrain) { alert("Please select a train"); return; }
    if (pickupType === "train" && selectedTrain?.status === "Cancelled") {
      alert("This train is cancelled. Please select another."); return;
    }

    const order = {
      customer: { id: user.id },
      pickupTime: pickupType === "train" ? selectedTrain.estimatedArrivalTime : pickupTime,
      trainId: pickupType === "train" ? selectedTrain.trainId : null,
      estimatedArrivalTime: pickupType === "train" ? selectedTrain.estimatedArrivalTime : pickupTime,
      items: cart.map(i => ({ menuItemId: i.id, size: i.size, quantity: i.quantity }))
    };

    try {
      const res = await fetch("http://localhost:8080/orders", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(order)
      });
      const text = await res.text();
      if (!res.ok) { alert(text); return; }
      setCart([]);
      navigate(`/checkout/${JSON.parse(text).id}`);
    } catch { alert("Failed to connect to server"); }
  };

  return (
    <div style={{ background: "#f5f1eb", minHeight: "100vh", padding: "24px 24px 40px", fontFamily: "'Segoe UI', sans-serif" }}>

      {/* HEADER */}
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", maxWidth: "1200px", margin: "0 auto 28px" }}>
        <div>
          <h1 style={{ margin: 0, fontSize: "26px", fontWeight: "700" }}>
            ☕ {station?.kioskName || "Whistlestop Coffee"}
          </h1>
          {todayHours && (
            <p style={{ margin: "4px 0 0", fontSize: "13px", color: isOpenNow ? "#16a34a" : "#ea580c" }}>
              {todayHours.closed ? "Closed today — pre-orders available"
                : isOpenNow ? `Open · ${todayHours.openTime}–${todayHours.closeTime}`
                : `Closed · Pre-order for ${todayHours.openTime}`}
            </p>
          )}
        </div>
        <button
          onClick={() => { localStorage.removeItem("user"); navigate("/"); }}
          style={{ background: "#ef4444", color: "#fff", padding: "8px 14px", borderRadius: "8px", border: "none", cursor: "pointer", fontSize: "13px" }}
        >Logout</button>
      </div>

      {/* BODY */}
      <div style={{ display: "flex", gap: "24px", maxWidth: "1200px", margin: "0 auto" }}>

        {/* MENU GRID */}
        <div style={{ flex: 1, display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(210px, 1fr))", gap: "16px", alignContent: "start" }}>
          {menu.map(item => {
            const size = selectedSizes[item.id] || "Regular";
            const price = size === "Large" ? item.priceLarge : item.priceRegular;
            return (
              <div key={item.id} style={{ background: "#fff", borderRadius: "14px", overflow: "hidden", boxShadow: "0 2px 10px rgba(0,0,0,0.07)" }}>
                <img src={imageMap[item.name] || "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=500"}
                  alt={item.name} style={{ width: "100%", height: "130px", objectFit: "cover" }} />
                <div style={{ padding: "12px" }}>
                  <div style={{ fontWeight: "600", marginBottom: "6px", fontSize: "14px" }}>{item.name}</div>
                  {item.hasSize && (
                    <div style={{ display: "flex", gap: "5px", marginBottom: "8px" }}>
                      {["Regular", "Large"].map(s => (
                        <button key={s} onClick={() => setSelectedSizes(p => ({ ...p, [item.id]: s }))}
                          style={{ flex: 1, padding: "3px 0", border: "none", borderRadius: "5px", cursor: "pointer", fontSize: "11px",
                            background: size === s ? "#6f4e37" : "#e5e7eb", color: size === s ? "#fff" : "#374151" }}>
                          {s}
                        </button>
                      ))}
                    </div>
                  )}
                  <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                    <span style={{ fontWeight: "700", fontSize: "14px" }}>£{price.toFixed(2)}</span>
                    <button onClick={() => addToCart(item)}
                      style={{ background: "#6f4e37", color: "#fff", border: "none", borderRadius: "7px", padding: "5px 12px", cursor: "pointer", fontSize: "13px" }}>
                      + Add
                    </button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>

        {/* CART PANEL */}
        <div style={{ width: "310px", flexShrink: 0, background: "#fff", borderRadius: "16px", padding: "20px", boxShadow: "0 4px 16px rgba(0,0,0,0.09)", height: "fit-content" }}>
          <h2 style={{ margin: "0 0 14px", fontSize: "17px" }}>🛒 Your Order</h2>

          {cart.length === 0
            ? <p style={{ color: "#9ca3af", fontSize: "13px" }}>Add items to get started</p>
            : cart.map((item, i) => (
              <div key={i} style={{ display: "flex", alignItems: "center", gap: "8px", marginBottom: "10px", fontSize: "13px" }}>
                <span style={{ flex: 1 }}>{item.name} <span style={{ color: "#9ca3af" }}>({item.size})</span></span>
                <button onClick={() => updateQty(i, -1)} style={{ width: "22px", height: "22px", borderRadius: "50%", border: "none", background: "#f3f4f6", cursor: "pointer" }}>−</button>
                <span style={{ width: "16px", textAlign: "center" }}>{item.quantity}</span>
                <button onClick={() => updateQty(i, +1)} style={{ width: "22px", height: "22px", borderRadius: "50%", border: "none", background: "#f3f4f6", cursor: "pointer" }}>+</button>
                <span style={{ width: "40px", textAlign: "right", fontWeight: "500" }}>£{(item.selectedPrice * item.quantity).toFixed(2)}</span>
              </div>
            ))
          }

          {cart.length > 0 && (
            <div style={{ borderTop: "1px solid #f3f4f6", paddingTop: "10px", fontWeight: "700", marginBottom: "16px" }}>
              Total: £{cartTotal.toFixed(2)}
            </div>
          )}

          {/* PICKUP TOGGLE */}
          <div style={{ display: "flex", gap: "6px", marginBottom: "12px" }}>
            {[["time", "⏰ Pick Time"], ["train", "🚆 My Train"]].map(([type, label]) => (
              <button key={type} onClick={() => setPickupType(type)}
                style={{ flex: 1, padding: "7px", borderRadius: "8px", border: "none", cursor: "pointer", fontSize: "12px", fontWeight: "600",
                  background: pickupType === type ? "#6f4e37" : "#f3f4f6",
                  color: pickupType === type ? "#fff" : "#374151" }}>
                {label}
              </button>
            ))}
          </div>

          {/* TIME PICKER */}
          {pickupType === "time" && (
            <select value={pickupTime} onChange={e => setPickupTime(e.target.value)}
              style={{ width: "100%", padding: "8px", borderRadius: "8px", border: "1px solid #d1d5db", marginBottom: "4px", fontSize: "13px" }}>
              {timeSlots.map(t => <option key={t} value={t}>{formatTime(t)}</option>)}
            </select>
          )}

          {/* TRAIN CARD PICKER */}
          {pickupType === "train" && (
            <div>
              <div style={{ display: "flex", justifyContent: "space-between", marginBottom: "8px" }}>
                <span style={{ fontSize: "12px", color: "#6b7280" }}>Trains to Cramlington</span>
                {trainsLoading && <span style={{ fontSize: "11px", color: "#9ca3af" }}>↻</span>}
              </div>

              {trains.length === 0 && !trainsLoading && (
                <p style={{ fontSize: "12px", color: "#9ca3af", textAlign: "center", padding: "12px 0" }}>
                  No upcoming arrivals found
                </p>
              )}

              <div style={{ maxHeight: "300px", overflowY: "auto", display: "flex", flexDirection: "column", gap: "6px" }}>
                {trains.map(train => {
                  const badge = getStatusBadge(train);
                  const delay = getDelayMinutes(train);
                  const isSelected = selectedTrain?.trainId === train.trainId;
                  const isCancelled = train.status === "Cancelled";

                  return (
                    <div key={train.trainId}
                      onClick={() => !isCancelled && setSelectedTrain(train)}
                      style={{
                        border: `${isSelected ? "2px" : "1px"} solid ${isSelected ? "#6f4e37" : "#e5e7eb"}`,
                        borderRadius: "9px", padding: "9px 11px",
                        cursor: isCancelled ? "not-allowed" : "pointer",
                        background: isSelected ? "#fdf8f5" : isCancelled ? "#fef2f2" : "#fff",
                        opacity: isCancelled ? 0.65 : 1,
                        transition: "border-color 0.1s, background 0.1s"
                      }}>
                      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", gap: "6px" }}>
                        <div style={{ flex: 1 }}>
                          <div style={{ fontSize: "12px", fontWeight: "600", color: "#111827" }}>
                            {train.origin} → Cramlington
                          </div>
                          <div style={{ fontSize: "11px", color: "#6b7280", marginTop: "2px" }}>
                            Sched <strong>{train.scheduledArrivalTime}</strong>
                            {delay !== 0 && (
                              <span style={{ color: delay > 0 ? "#ea580c" : "#16a34a", marginLeft: "5px" }}>
                                · Est <strong>{train.estimatedArrivalTime}</strong>
                              </span>
                            )}
                          </div>
                        </div>
                        <span style={{
                          background: badge.bg, color: "#fff",
                          fontSize: "10px", fontWeight: "700",
                          padding: "2px 7px", borderRadius: "20px", whiteSpace: "nowrap"
                        }}>{badge.label}</span>
                      </div>
                    </div>
                  );
                })}
              </div>

              {selectedTrain && (
                <div style={{ marginTop: "10px", background: "#fdf8f5", borderRadius: "8px", padding: "10px", fontSize: "12px" }}>
                  <div style={{ color: "#6b7280" }}>☕ Coffee ready before</div>
                  <div style={{ fontSize: "22px", fontWeight: "800", color: "#6f4e37" }}>
                    {selectedTrain.estimatedArrivalTime}
                  </div>
                  {getDelayMinutes(selectedTrain) > 0 && (
                    <div style={{ color: "#ea580c", marginTop: "2px" }}>
                      ⚠️ Delayed +{getDelayMinutes(selectedTrain)} min · we'll adjust your order
                    </div>
                  )}
                </div>
              )}
            </div>
          )}

          <button
            onClick={placeOrder}
            disabled={cart.length === 0}
            style={{
              width: "100%", marginTop: "14px", padding: "12px",
              borderRadius: "10px", border: "none",
              background: cart.length === 0 ? "#d1d5db" : "#22c55e",
              color: "#fff", fontWeight: "700", fontSize: "14px",
              cursor: cart.length === 0 ? "not-allowed" : "pointer"
            }}>
            Checkout · £{cartTotal.toFixed(2)}
          </button>
        </div>
      </div>
    </div>
  );
}

export default MenuPage;