# Whistlestop Coffee Ordering System – Team Development Guide

## 1. Project Goal

The goal of this project is to build a system that allows customers to **order coffee in advance** from the Whistlestop kiosk at Cramlington Station. Customers place orders through a web/mobile interface and staff manage the orders through a staff dashboard.

---

# 2. Development Stages

## Stage 1 – Core Java Logic (Current Stage)

We are currently building the **core system logic using plain Java classes** before introducing frameworks.

At this stage we are:

* Designing system classes
* Defining attributes and methods
* Organizing packages
* Simulating system behaviour

No APIs, frameworks, or databases are used yet.

### Package Structure

model
Contains classes representing system data.

Examples:

* Staff
* Customer
* MenuItem
* Order
* OrderItem
* Payment

service
Contains classes implementing system logic.

Examples:

* LoginManager
* OrderManager
* MenuManager
* PaymentManager

Service classes will use the model classes.

---

# 3. Core System Models

## Staff

Represents employees working at the coffee kiosk.

Responsibilities:

* Log into the system
* View incoming orders
* Accept or reject orders
* Update order status
* Mark orders as ready or collected
* Cancel orders when necessary

---

## Customer

Represents a user placing an order.

Responsibilities:

* Browse the coffee menu
* Select items
* Place orders
* Choose pickup time
* Pay for the order
* Check order status

---

## MenuItem

Represents a drink available on the menu.

Information includes:

* Item name
* Description
* Price
* Availability

Customers select menu items when placing orders.

---

## Order

Represents a full order placed by a customer.

An order contains:

* Order ID
* Customer
* Pickup time
* Order status
* List of ordered items (OrderItem)

Order statuses may include:

* Pending
* Accepted
* In Progress
* Ready for Collection
* Collected
* Cancelled

---

## OrderItem

Represents a single item within an order.

An order can contain multiple items.
Each OrderItem includes:

* Menu item
* Quantity

Example:

Order #102

* Latte ×2
* Cappuccino ×1

Each drink entry is an OrderItem.

This design keeps the system flexible and allows customers to order multiple drinks in a single order.

---

## Payment

Handles payment information for an order.

Responsibilities:

* Store payment details
* Confirm payment
* Link payment to the order

---

# 4. System Workflow

## Customer Flow

1. Customer opens the application.
2. Customer browses the coffee menu.
3. Customer selects one or more menu items.
4. Items are added to an order.
5. Customer chooses a pickup time.
6. Customer confirms the order.
7. Customer completes payment.
8. Order is saved in the system.
9. Customer can view order status updates.

---

## Staff Flow

1. Staff logs into the system.
2. Staff views incoming orders.
3. Staff accepts the order.
4. Staff prepares the drinks.
5. Staff marks the order as **ready for collection**.
6. Customer collects the order.
7. Staff marks the order as **collected**.
8. Completed orders move to an archive.

---

# 5. Frontend Plan

The frontend will be built using:

* JavaScript
* React

The frontend will communicate with the backend using **REST APIs**.

### Customer Interface

Possible pages:

* Home page
* Menu page
* Order page
* Payment page
* Order status page

### Staff Interface

Possible pages:

* Staff login
* Order dashboard
* Order status update page
* Completed orders archive

---

# 6. Backend Plan (Spring Boot Phase)

After completing the core Java logic, we will integrate **Spring Boot**.

The backend structure will include:

controller
Handles API requests.

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
* MenuItem
* Order
* OrderItem
* Payment

repository

* StaffRepository
* OrderRepository

---

# 7. Frontend ↔ Backend Communication

The React frontend will call backend APIs such as:

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

# 8. Database (Later Phase)

The database will store:

* Staff
* Customers
* Menu items
* Orders
* Order items
* Payments

Spring Boot will use **Spring Data JPA** to interact with the database.

---

# 9. Final System Flow

Customer (React Frontend)
↓
API Request
↓
Spring Boot Backend
↓
Service Logic
↓
Database
↓
Response to Frontend
↓
UI Updates

---

# 10. Next Steps

1. Complete model and service classes.
2. Test system logic locally.
3. Learn Spring Boot basics.
4. Convert services into REST APIs.
5. Build the React frontend.
6. Connect frontend with backend APIs.
7. Add database integration.
