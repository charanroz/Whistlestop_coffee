import { useEffect, useState, useMemo } from "react";
import { useNavigate } from "react-router-dom";

function MenuPage() {
  const [menu, setMenu] = useState([]);
  const [cart, setCart] = useState([]);
  const [pickupTime, setPickupTime] = useState("");
  const [station, setStation] = useState(null);
  const [hours, setHours] = useState([]);
  const [selectedSizes, setSelectedSizes] = useState({});

  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem("user"));

  // redirect if not logged in
  useEffect(() => {
    if (!user) navigate("/");
  }, [user, navigate]);

  // fetch data
  useEffect(() => {
    fetch("http://localhost:8080/menu")
      .then(res => res.json())
      .then(setMenu)
      .catch(() => console.log("Menu fetch failed"));

    fetch("http://localhost:8080/station-setting")
      .then(res => res.json())
      .then(setStation)
      .catch(() => console.log("Station fetch failed"));

    fetch("http://localhost:8080/business-hours")
      .then(res => res.json())
      .then(setHours)
      .catch(() => console.log("Hours fetch failed"));
  }, []);

  // live time
  const [currentTime, setCurrentTime] = useState(
    new Date().toTimeString().slice(0, 5)
  );

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentTime(new Date().toTimeString().slice(0, 5));
    }, 60000);
    return () => clearInterval(interval);
  }, []);

  // business logic
  const today = new Date().toLocaleDateString("en-US", {
    weekday: "long"
  });

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
      const h = String(hour).padStart(2, "0");
      const m = String(minute).padStart(2, "0");
      const time = `${h}:${m}`;

      if (!isOpenNow || time >= currentTime) {
        slots.push(time);
      }

      minute += 15;
      if (minute >= 60) {
        minute = 0;
        hour++;
      }
    }

    return slots;
  }, [todayHours, currentTime, isOpenNow]);

  useEffect(() => {
    if (timeSlots.length > 0) {
      setPickupTime(prev => prev || timeSlots[0]);
    }
  }, [timeSlots]);

  const formatTime = (time) => {
    const [h, m] = time.split(":");
    const date = new Date();
    date.setHours(h, m);
    return date.toLocaleTimeString([], {
      hour: "2-digit",
      minute: "2-digit"
    });
  };

  // image map
  const imageMap = {
    "Americano": "https://images.unsplash.com/photo-1580661869408-55ab23f2ca6e?w=500",
    "Americano with milk": "https://images.unsplash.com/photo-1565434007235-d3a18c2e0954",
    "Latte": "https://images.unsplash.com/photo-1593443320739-77f74939d0da?w=500",
    "Cappuccino": "https://images.unsplash.com/photo-1512568400610-62da28bc8a13?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTB8fGNhcHB1Y2Npbm98ZW58MHx8MHx8fDA%3D",
    "Hot Chocolate": "https://images.unsplash.com/photo-1608651057580-4a50b2fc2281?w=500",
    "Mocha": "https://images.unsplash.com/photo-1618576230663-9714aecfb99a?w=500",
    "Mineral Water": "https://plus.unsplash.com/premium_photo-1681236320994-3395e842ed81?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTN8fG1pbmVyYWwlMjB3YXRlciUyMGJvdHRsZXxlbnwwfHwwfHx8MA%3D%3D"
  };

  // size selection
  const handleSizeChange = (itemId, size) => {
    setSelectedSizes(prev => ({
      ...prev,
      [itemId]: size
    }));
  };

  // cart
  const addToCart = (item) => {
    const size = selectedSizes[item.id] || "Regular";

    const price =
      size === "Large"
        ? item.priceLarge
        : item.priceRegular;

    setCart(prev => {
      const existing = prev.find(
        i => i.id === item.id && i.size === size
      );

      if (existing) {
        return prev.map(i =>
          i.id === item.id && i.size === size
            ? { ...i, quantity: i.quantity + 1 }
            : i
        );
      }

      return [
        ...prev,
        {
          ...item,
          size,
          selectedPrice: price,
          quantity: 1
        }
      ];
    });
  };

  const increaseQty = (index) => {
    setCart(prev =>
      prev.map((item, i) =>
        i === index ? { ...item, quantity: item.quantity + 1 } : item
      )
    );
  };

  const decreaseQty = (index) => {
    setCart(prev =>
      prev.flatMap((item, i) => {
        if (i !== index) return item;
        if (item.quantity === 1) return [];
        return { ...item, quantity: item.quantity - 1 };
      })
    );
  };

  const cartTotal = cart.reduce(
    (t, i) => t + i.selectedPrice * i.quantity,
    0
  );

  // place order
  const placeOrder = async () => {
    if (cart.length === 0) {
      alert("Cart is empty");
      return;
    }

    const order = {
      customer: { id: user.id },
      pickupTime,
      items: cart.map(i => ({
        menuItemId: i.id,
        size: i.size,
        quantity: i.quantity
      }))
    };

    try {
      const res = await fetch("http://localhost:8080/orders", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(order)
      });

      const text = await res.text();   // 👈 get real response

      if (!res.ok) {
        console.error("Backend error:", text);  // 🔥 SEE ERROR
        alert(text);
        return;
      }

      const data = JSON.parse(text);

      console.log("SUCCESS:", data);

      setCart([]);
      navigate(`/checkout/${data.id}`);

    } catch (err) {
      console.error("Network error:", err);
      alert("Failed to connect to server");
    }
  };

  return (
    <div className="bg-[#f5f1eb] min-h-screen p-6 font-sans">
      {/* HEADER */}
      <div className="flex justify-between items-center mb-8 max-w-6xl mx-auto">
        <div>
          <h1 className="text-3xl font-bold tracking-wide">
            ☕ {station?.kioskName || "Loading..."}
          </h1>

          {todayHours && (
            <p className={`text-sm mt-1 ${isOpenNow ? "text-green-600" : "text-orange-500"}`}>
              {todayHours.closed
                ? "Closed today (pre-orders available)"
                : isOpenNow
                ? `Open now (${todayHours.openTime} - ${todayHours.closeTime})`
                : `Closed now — pre-order for ${todayHours.openTime}`}
            </p>
          )}
        </div>

        <button
          onClick={() => {
            localStorage.removeItem("user");
            navigate("/");
          }}
          className="bg-red-500 text-white px-4 py-2 rounded-lg"
        >
          Logout
        </button>
      </div>

      {/* MAIN */}
      <div className="flex gap-6 max-w-6xl mx-auto">

        {/* MENU */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 flex-1">
          {menu.map(item => {
            const size = selectedSizes[item.id] || "Regular";
            const price = size === "Large" ? item.priceLarge : item.priceRegular;

            return (
              <div key={item.id} className="bg-white rounded-2xl shadow hover:shadow-xl transition">
                <img
                  src={imageMap[item.name] || "https://images.unsplash.com/photo-1509042239860-f550ce710b93"}
                  alt={item.name}
                  className="w-full h-40 object-cover rounded-t-2xl"
                />

                <div className="p-4">
                  <h3 className="font-semibold text-lg">{item.name}</h3>

                  {item.hasSize && (
                    <div className="flex gap-2 mt-2">
                      {["Regular", "Large"].map(s => (
                        <button
                          key={s}
                          onClick={() => handleSizeChange(item.id, s)}
                          className={`px-3 py-1 rounded ${
                            size === s ? "bg-[#6f4e37] text-white" : "bg-gray-200"
                          }`}
                        >
                          {s}
                        </button>
                      ))}
                    </div>
                  )}

                  <p className="font-bold mt-2 text-gray-700">
                    £{price.toFixed(2)}
                  </p>

                  <button
                    onClick={() => addToCart(item)}
                    className="mt-3 w-full py-2 rounded-lg text-white bg-[#6f4e37] hover:bg-[#5a3d2b]"
                  >
                    Add to Cart
                  </button>
                </div>
              </div>
            );
          })}
        </div>

        {/* CART */}
        <div className="w-80 bg-white rounded-2xl shadow-lg p-5">
          <h2 className="text-xl font-bold mb-4">🛒 Cart</h2>

          {cart.length === 0 && <p className="text-gray-500">Empty</p>}

          {cart.map((item, i) => (
            <div key={i} className="text-sm border-b py-2">
              <div>{item.name} ({item.size})</div>

              <div className="flex items-center gap-2 mt-1">
                <button
                  onClick={() => decreaseQty(i)}
                  className="px-2 bg-gray-200 rounded"
                >
                  -
                </button>

                <span>{item.quantity}</span>

                <button
                  onClick={() => increaseQty(i)}
                  className="px-2 bg-gray-200 rounded"
                >
                  +
                </button>

                <span className="ml-auto font-medium">
                  £{(item.selectedPrice * item.quantity).toFixed(2)}
                </span>
              </div>
            </div>
          ))}

          <div className="mt-3 font-bold">
            Total: £{cartTotal.toFixed(2)}
          </div>

          {/* PICKUP */}
          <select
            value={pickupTime}
            onChange={e => setPickupTime(e.target.value)}
            className="w-full border rounded-lg p-2 mt-3"
          >
            {timeSlots.map(time => (
              <option key={time} value={time}>
                {formatTime(time)}
              </option>
            ))}
          </select>

          <button
            onClick={placeOrder}
            disabled={cart.length === 0}
            className="w-full mt-4 py-2 rounded-lg text-white bg-green-500 hover:bg-green-600"
          >
            Checkout (£{cartTotal.toFixed(2)})
          </button>
        </div>
      </div>
    </div>
  );
}

export default MenuPage;