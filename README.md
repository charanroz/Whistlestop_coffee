# Whistlestop Coffee ☕🚆

A real-time coffee ordering kiosk for **Cramlington Railway Station**, built with Spring Boot + React.

Customers choose their train, and their coffee is ready exactly when they arrive — automatically adjusted for delays using live Network Rail data.

---

## Features

- 🚆 **Live train arrivals** — real-time data from Network Rail via Confluent Kafka
- ⏱️ **Auto delay adjustment** — Kafka Train Movements update order pickup times automatically
- ☕ **Menu ordering** — size selection, cart, checkout
- 📅 **Daily schedule** — full timetable from Rail Data Marketplace (NWR Schedule feed)
- 🗺️ **CORPUS lookup** — TIPLOC codes resolved to readable station names
- 🟡 **Mock fallback** — works without API credentials for local testing

---

## Staff Access

To access the staff dashboard, use the following credentials:
- **Username:** `admin@coffe.com`
- **Password:** `1234`

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3, Spring Kafka, H2 Database |
| Frontend | React 18 |
| Real-time data | Confluent Cloud Kafka (NWR Train Movements) |
| Schedule data | Rail Data Marketplace (NWR Schedule CIF JSON) |
| Reference data | NWR CORPUS (bundled in project) |

---

## Quick Start

> **Note:** `application.properties` is **not** committed to Git (it contains credentials).
> You must create your own copy from the template before running.

### Prerequisites

- Java 21+
- Maven 3.8+
- Node.js 18+

### 1. Create your config file (required first step)

```bash
# From the project root:
cp backend/src/main/resources/application.properties.example \
   backend/src/main/resources/application.properties
```
> On Windows (Command Prompt): `copy backend\src\main\resources\application.properties.example backend\src\main\resources\application.properties`

The default config already has Kafka **disabled**, so the app runs immediately with **mock train data** — no NWR account needed.

### 2. Run the backend (IntelliJ IDEA)

1. Open IntelliJ IDEA
2. **File → Open** → select the `backend` folder (contains `pom.xml`)
3. Wait for Maven to download dependencies (progress bar at bottom-right)
4. Open `src/main/java/.../WhistlestopCoffeeApplication.java`
5. Click the green ▶ **Run** button
6. Backend starts on **http://18.130.223.148:8080**

On first startup you will see in the console:
```
✅ CORPUS loaded: XXXX station entries
⚠️  RDM credentials not configured — using mock fallback
```
*(This is normal — mock train data is served automatically.)*

### 3. Run the frontend

Open a **new terminal** (IntelliJ: View → Tool Windows → Terminal):

```bash
cd frontend
npm install
npm start
```

Frontend opens at **http://localhost:3000**

---

## Run with Real Network Rail Data (Optional)

To connect to live train data:

### Kafka (Train Movements — real-time delays)

1. Register at [Rail Data Marketplace](https://raildata.org.uk)
2. Subscribe to **NWR Train Movements**
3. Fill in `application.properties`:

```properties
spring.kafka.bootstrap-servers=YOUR_CONFLUENT_BOOTSTRAP
spring.kafka.properties.sasl.jaas.config=...username="YOUR_KEY" password="YOUR_SECRET";
```

### Schedule (Daily timetable)

Fill in your RDM login credentials in `application.properties`:

```properties
rdm.username=YOUR_RDM_USERNAME
rdm.password=YOUR_RDM_PASSWORD
```

The system will automatically:
1. Log in to RDM OAuth2
2. Download today's CIF schedule (127 MB)
3. Filter Cramlington arrivals (TIPLOC: `CRMLNGT`)
4. Refresh at 06:00 daily

---

## Project Structure

```
Whistlestop_coffee/
├── backend/
│   ├── src/main/java/.../
│   │   ├── controller/          REST endpoints
│   │   ├── model/               JPA entities (Order, Train, Customer...)
│   │   ├── service/
│   │   │   ├── TrainCache.java           In-memory train store
│   │   │   ├── TrainMovementConsumer.java Kafka listener (STANOX 12136)
│   │   │   ├── ScheduleFetchService.java  Daily schedule downloader
│   │   │   ├── CorpusService.java         TIPLOC → station name
│   │   │   ├── RealTrainService.java      Live data + mock fallback
│   │   │   └── TrainStatusScheduler.java  Updates orders on delay
│   │   └── repository/          Spring Data JPA repos
│   └── src/main/resources/
│       ├── application.properties         ← NOT in Git (add your credentials)
│       ├── application.properties.example ← Template (committed to Git)
│       └── CORPUSExtract.json             ← NWR reference data (bundled)
└── frontend/
    └── src/pages/
        └── MenuPage.js                    Train card UI + cart
```

---

## Cramlington Station Reference

| Code | Value |
|---|---|
| TIPLOC | `CRMLNGT` |
| STANOX | `12136` |
| CRS | `CRM` |

*Confirmed from NWR CORPUS (bundled in project).*

---

## Licence

University coursework project — Newcastle University.
