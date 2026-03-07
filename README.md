# Whistlestop Coffee System – Development Flow

## 1. Project Goal

The goal of this project is to build a system that allows customers to **order coffee in advance from a kiosk at Cramlington Station**. Customers will place orders using a mobile/web interface, and staff will manage the orders through a staff dashboard.

---

# 2. Development Stages

## Stage 1 – Core Java Logic (Current Stage)

At this stage we are building the **core backend logic using Java classes only**. No APIs or frameworks yet.

### Package Structure

model
Contains classes representing system data.

Examples:

* Staff
* Customer
* Order
* MenuItem
* Payment

service
Contains logic that manages the models.

Examples:

* LoginManager
* OrderManager
* MenuManager
* PaymentManager

### Goal of This Stage

* Design system classes
* Define attributes and methods
* Simulate how the system works using a main class
* Ensure all components interact correctly

---

# 3. System Components

## Staff

Represents employees working at the coffee kiosk.

Responsibilities:

* Log into the system
* View incoming orders
* Update order status
* Cancel orders
* Manage completed orders

---

## Customer

Represents a user ordering coffee.

Responsibilities:

* Browse menu
* Place orders
* Select pickup time
* Pay for orders
* View order status

---

## Menu

Stores available coffee items.

Example information:

* Coffee name
* Description
* Price
* Availability

Customers will browse the menu before placing an order.

---

## Order

Represents a coffee order placed by a customer.

Order includes:

* Order ID
* Coffee item(s)
* Quantity
* Pickup time
* Order status

Order status can be:

* Pending
* Accepted
* In Progress
* Ready for Collection
* Collected
* Cancelled

---

## Payment

Handles payment processing for orders.

Responsibilities:

* Store payment details
* Confirm payment
* Mark order as paid

---

# 4. System Workflow

## Customer Flow

1. Customer opens the application.
2. Customer browses the available coffee menu.
3. Customer selects coffee and quantity.
4. Customer chooses a pickup time.
5. Customer confirms the order.
6. Customer completes payment.
7. Order is saved in the system.
8. Customer can view order status updates.

---

## Staff Flow

1. Staff logs into the system.
2. Staff views incoming orders on a dashboard.
3. Staff accepts the order.
4. Staff prepares the order.
5. Staff marks the order as ready.
6. Customer collects the order.
7. Staff marks the order as collected.
8. Completed orders move to archive.

---

# 5. Frontend Plan

The frontend will be built using:

* JavaScript
* React

The frontend will communicate with the backend using **REST APIs**.

### Customer Interface

Pages may include:

* Home page
* Menu page
* Order page
* Payment page
* Order status page

### Staff Interface

Pages may include:

* Staff login
* Order dashboard
* Order status update
* Completed orders archive

---

# 6. Backend Plan (Spring Boot Phase)

After finishing core Java classes, we will move to **Spring Boot**.

The backend structure will include:

controller
Handles API requests from the frontend.

service
Contains business logic.

model
Represents system data.

repository
Handles database communication.

Example structure:

controller

* AuthController
* OrderController
* MenuController

service

* LoginService
* OrderService
* MenuService

model

* Staff
* Customer
* Order
* MenuItem
* Payment

repository

* StaffRepository
* OrderRepository

---

# 7. Frontend ↔ Backend Communication

React frontend will call backend APIs such as:

POST /login
Staff login

GET /menu
Retrieve menu items

POST /orders
Create a new order

GET /orders
View orders

PUT /orders/{id}/status
Update order status

---

# 8. Database

The database will store:

Staff
Customer
Orders
Menu items
Payments

The backend will communicate with the database using **Spring Data JPA**.

---

# 9. Final System Flow

Customer (React App)
↓
Calls Backend API (Spring Boot)
↓
Backend Processes Logic (Services)
↓
Data Stored / Retrieved from Database
↓
Response sent back to frontend
↓
Frontend updates UI

---

# 10. Next Steps for the Team

1. Complete Java model and service classes.
2. Test system logic locally.
3. Learn Spring Boot basics.
4. Convert services into REST APIs.
5. Learn React and build frontend UI.
6. Connect frontend with backend APIs.
7. Add database integration.
