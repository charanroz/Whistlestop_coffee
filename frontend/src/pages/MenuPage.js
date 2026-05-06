import { useEffect, useState, useMemo, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import API from "../api";

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
  // eslint-disable-next-line react-hooks/exhaustive-deps
  useEffect(() => {
    if (!user) navigate("/");
  }, [user, navigate]);

  // fetch menu, station, hours once
  useEffect(() => {
    fetch(`${API}/menu`)
      .then(res => res.json())
      .then(setMenu)
      .catch(() => {});

    fetch(`${API}/station-setting`)
      .then(res => res.json())
      .then(setStation)
      .catch(() => {});

    fetch(`${API}/business-hours`)
      .then(res => res.json())
      .then(setHours)
      .catch(() => {});
  }, []);

  // fetch trains initially and every 60 seconds
  const fetchTrains = () => {
    setTrainsLoading(true);
    fetch(`${API}/trains/arrivals?stationName=Cramlington`)
      .then(res => res.json())
      .then(data => {
        const trainList = Array.isArray(data) ? data : [];
        setTrains(trainList);
        setTrainsLoading(false);
        if (trainList.length > 0) {
          setSelectedTrain(prev => {
            if (!prev) return trainList[0];
            const refreshed = trainList.find(t => t.trainId === prev.trainId);
            return refreshed || trainList[0];
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

  // หาวันถัดไปที่เปิด
    const getNextOpenDay = useCallback(() => {
      const days = ["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"];
      let i = new Date().getDay();
      for (let x = 1; x <= 7; x++) {
        const next = days[(i + x) % 7];
        const found = hours.find(h => h.dayOfWeek === next && !h.closed);
        if (found) return { hours: found, daysAhead: x };
      }
      return null;
    }, [hours]);

  // หาวันที่ของ pickup (วันนี้หรือวันถัดไปที่เปิด)
  const getPickupDate = () => {
    const formatLocalDate = (date) =>
      `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;

    if (!todayHours || todayHours.closed) {
      const next = getNextOpenDay();
      if (next) {
        const d = new Date();
        d.setDate(d.getDate() + next.daysAhead);
        return formatLocalDate(d);
      }
    }
    return formatLocalDate(new Date());
  };

  const toDatedTime = (time) => {
    if (!time) return "";
    if (String(time).includes(" ")) return time;

    const today = new Date();
    const [hour, minute] = String(time).split(":").map(Number);
    const target = new Date(today);
    target.setHours(hour, minute || 0, 0, 0);

    if (target < today) {
      target.setDate(target.getDate() + 1);
    }

    const date = `${target.getFullYear()}-${String(target.getMonth() + 1).padStart(2, "0")}-${String(target.getDate()).padStart(2, "0")}`;
    return `${date} ${String(hour).padStart(2, "0")}:${String(minute || 0).padStart(2, "0")}`;
  };

  // ✅ FIXED: time slots — ถ้าวันนี้ปิด ใช้วันถัดไปที่เปิด
  // eslint-disable-next-line react-hooks/exhaustive-deps
  const timeSlots = useMemo(() => {
    const next = getNextOpenDay();
    const target = (!todayHours || todayHours.closed) ? next?.hours : todayHours;

    if (!target || !target.openTime) return [];

    const slots = [];
    let [hour, minute] = target.openTime.split(":").map(Number);
    const [closeHour, closeMinute] = target.closeTime.split(":").map(Number);

    while (hour < closeHour || (hour === closeHour && minute <= closeMinute)) {
      const time = `${String(hour).padStart(2,"0")}:${String(minute).padStart(2,"0")}`;

      if (todayHours && !todayHours.closed && isOpenNow) {
        if (time >= currentTime) {
          slots.push(time);
        }
      } else {
        slots.push(time);
      }

      minute += 5;
      if (minute >= 60) {
        minute = 0;
        hour++;
      }
    }

    return slots;
  }, [todayHours, currentTime, isOpenNow, getNextOpenDay]);

  useEffect(() => {
    if (timeSlots.length > 0) setPickupTime(timeSlots[0]);
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

    if (pickupType === "train") {
      const arrTime = selectedTrain.estimatedArrivalTime;
      if (!todayHours || todayHours.closed || arrTime < todayHours.openTime || arrTime > todayHours.closeTime) {
        alert("outofbusinesshours");
        return;
      }
    }

    const pickupDateTime = `${getPickupDate()} ${pickupTime}`;
    const trainDateTime = pickupType === "train"
      ? toDatedTime(selectedTrain.estimatedArrivalTime)
      : null;

    const order = {
      customer: { id: user.id },
      pickupTime: pickupType === "train" ? trainDateTime : pickupDateTime,
      trainId: pickupType === "train" ? selectedTrain.trainId : null,
      estimatedArrivalTime: pickupType === "train" ? trainDateTime : pickupDateTime,
      items: cart.map(i => ({ menuItemId: i.id, size: i.size, quantity: i.quantity }))
    };

    try {
      const res = await fetch(`${API}/orders`, {
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

  // label บอกว่า pre-order สำหรับวันไหน
  const preOrderLabel = () => {
    if (!todayHours || todayHours.closed) {
      const next = getNextOpenDay();
      if (next) return `Pre-order for ${next.hours.dayOfWeek} ${next.hours.openTime}–${next.hours.closeTime}`;
    }
    return null;
  };

  return (
    <div className="bg-[#f5f1eb] min-h-screen p-4 sm:p-6 pb-24 font-sans">
      {/* HEADER */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center max-w-[1200px] mx-auto mb-7 gap-4">
        <div>
          <h1 className="m-0 text-2xl sm:text-[26px] font-bold">
            ☕ {station?.kioskName || "Whistlestop Coffee"}
          </h1>
          {todayHours && (
            <p className={`m-0 mt-1 text-[13px] ${isOpenNow ? "text-green-600" : "text-orange-600"}`}>
              {todayHours.closed
                ? `Closed today — ${preOrderLabel() || "pre-orders available"}`
                : isOpenNow
                  ? `Open · ${todayHours.openTime}–${todayHours.closeTime}`
                  : `Closed · Pre-order for ${todayHours.openTime}`}
            </p>
          )}
        </div>
      </div>

      {/* BODY */}
      <div className="flex flex-col lg:flex-row gap-6 max-w-[1200px] mx-auto relative">

        {/* MENU GRID */}
        <div className="flex-1 grid grid-cols-2 sm:grid-cols-3 xl:grid-cols-4 gap-4 content-start">
          {menu.map(item => {
            const size = selectedSizes[item.id] || "Regular";
            const price = size === "Large" ? item.priceLarge : item.priceRegular;
            return (
              <div key={item.id} className="bg-white rounded-2xl overflow-hidden shadow-[0_2px_10px_rgba(0,0,0,0.07)] flex flex-col">
                <img src={imageMap[item.name] || "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=500"}
                  alt={item.name} className="w-full h-28 sm:h-32 object-cover" />
                <div className="p-3 flex flex-col flex-1">
                  <div className="font-semibold mb-2 text-[13px] sm:text-[14px] leading-tight flex-1">{item.name}</div>

                  {item.hasSize && (
                    <div className="flex gap-1 mb-2">
                      {["Regular", "Large"].map(s => (
                        <button key={s} onClick={() => setSelectedSizes(p => ({ ...p, [item.id]: s }))}
                          style={{ background: size === s ? "#6f4e37" : "#e5e7eb", color: size === s ? "#fff" : "#374151" }}
                          className="flex-1 py-1 border-none rounded-[5px] cursor-pointer text-[11px] transition-colors">
                          {s}
                        </button>
                      ))}
                    </div>
                  )}

                  <div className="flex justify-between items-center mt-auto pt-1">
                    <span className="font-bold text-[13px] sm:text-[14px]">£{price.toFixed(2)}</span>
                    <button onClick={() => addToCart(item)}
                      className="bg-[#6f4e37] text-white border-none rounded-lg px-2 sm:px-3 py-1 cursor-pointer text-[12px] sm:text-[13px] hover:bg-[#5a3f2d] transition-colors">
                      + Add
                    </button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>

        {/* CART PANEL */}
        <div className="w-full lg:w-[310px] shrink-0 bg-white rounded-2xl p-5 shadow-[0_4px_16px_rgba(0,0,0,0.09)] h-fit lg:sticky lg:top-6">
          <h2 className="m-0 mb-4 text-[17px]">🛒 Your Order</h2>

          {cart.length === 0
            ? <p className="text-gray-400 text-[13px]">Add items to get started</p>
            : cart.map((item, i) => (
              <div key={i} className="flex items-center gap-2 mb-3 text-[13px]">
                <span className="flex-1 leading-tight">{item.name} <span className="text-gray-400 text-[11px]">({item.size})</span></span>
                <button onClick={() => updateQty(i, -1)} className="w-6 h-6 rounded-full border-none bg-gray-100 hover:bg-gray-200 cursor-pointer flex items-center justify-center">−</button>
                <span className="w-4 text-center">{item.quantity}</span>
                <button onClick={() => updateQty(i, +1)} className="w-6 h-6 rounded-full border-none bg-gray-100 hover:bg-gray-200 cursor-pointer flex items-center justify-center">+</button>
                <span className="w-10 text-right font-medium">£{(item.selectedPrice * item.quantity).toFixed(2)}</span>
              </div>
            ))
          }

          {cart.length > 0 && (
            <div className="border-t border-gray-100 pt-3 font-bold mb-4 mt-2">
              Total: £{cartTotal.toFixed(2)}
            </div>
          )}

          {/* PICKUP TOGGLE */}
          <div className="flex gap-2 mb-3">
            {[["time", "⏰ Pick Time"], ["train", "🚆 My Train"]].map(([type, label]) => (
              <button key={type} onClick={() => setPickupType(type)}
                style={{ background: pickupType === type ? "#6f4e37" : "#f3f4f6", color: pickupType === type ? "#fff" : "#374151" }}
                className="flex-1 p-2 rounded-lg border-none cursor-pointer text-[12px] font-semibold transition-colors">
                {label}
              </button>
            ))}
          </div>

          {/* TIME PICKER */}
          {pickupType === "time" && (
            <div>
              {preOrderLabel() && (
                <p style={{ fontSize: "11px", color: "#ea580c", marginBottom: "6px" }}>
                  📅 {preOrderLabel()}
                </p>
              )}
              <select value={pickupTime} onChange={e => setPickupTime(e.target.value)}
                className="w-full p-2 rounded-lg border border-gray-300 mb-1 text-[13px] bg-white">
                {timeSlots.map(t => <option key={t} value={t}>{formatTime(t)}</option>)}
              </select>
            </div>
          )}

          {/* TRAIN CARD PICKER */}
          {pickupType === "train" && (
            <div>
              <div className="flex justify-between mb-2">
                <span className="text-[12px] text-gray-500">Trains to Cramlington</span>
                {trainsLoading && <span className="text-[11px] text-gray-400">↻</span>}
              </div>

              {trains.length === 0 && !trainsLoading && (
                <p className="text-[12px] text-gray-400 text-center py-3">No upcoming arrivals found</p>
              )}

              <div className="max-h-[300px] overflow-y-auto flex flex-col gap-2 pr-1">
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
                        background: isSelected ? "#fdf8f5" : isCancelled ? "#fef2f2" : "#fff",
                        opacity: isCancelled ? 0.65 : 1
                      }}
                      className={`rounded-lg p-2 cursor-${isCancelled ? "not-allowed" : "pointer"} transition-all`}>
                      <div className="flex justify-between items-start gap-2">
                        <div className="flex-1">
                          <div className="text-[12px] font-semibold text-gray-900 leading-tight">
                            {train.origin} → Cramlington
                          </div>
                          <div className="text-[11px] text-gray-500 mt-1">
                            Sched <strong>{train.scheduledArrivalTime}</strong>
                            {delay !== 0 && (
                              <span className={`ml-1 ${delay > 0 ? "text-orange-600" : "text-green-600"}`}>
                                · Est <strong>{train.estimatedArrivalTime}</strong>
                              </span>
                            )}
                          </div>
                        </div>
                        <span style={{ background: badge.bg }} className="text-white text-[10px] font-bold px-2 py-0.5 rounded-full whitespace-nowrap">
                          {badge.label}
                        </span>
                      </div>
                    </div>
                  );
                })}
              </div>

              {selectedTrain && (
                <div className="mt-3 bg-[#fdf8f5] rounded-lg p-3 text-[12px]">
                  <div className="text-gray-500 mb-1">☕ Coffee ready before</div>
                  <div className="text-[22px] font-extrabold text-[#6f4e37] leading-none">
                    {selectedTrain.estimatedArrivalTime}
                  </div>
                  {getDelayMinutes(selectedTrain) > 0 && (
                    <div className="text-orange-600 mt-1 font-medium">
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
            className={`w-full mt-4 p-3 rounded-xl border-none font-bold text-[14px] transition-colors
              ${cart.length === 0 ? "bg-gray-300 text-gray-100 cursor-not-allowed" : "bg-green-500 text-white cursor-pointer hover:bg-green-600"}`}>
            Checkout · £{cartTotal.toFixed(2)}
          </button>
        </div>
      </div>
    </div>
  );
}

export default MenuPage;
