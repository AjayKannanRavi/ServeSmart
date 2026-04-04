# ServeSmart — Modules & Automation Testing Guide

This document is a **detailed, automation-friendly README** for the ServeSmart project.

It focuses on:
- **All modules used in the project** (Frontend + Backend)
- **Simple, easy actions for each screen/button** (what it does and what to verify)
- A **testing model for automation** (E2E flows, API checks, WebSocket checks, state transitions)

---

## 1) What is ServeSmart?
ServeSmart is a QR-based dine-in ordering and restaurant management system.

**User-facing portals**:
- **Customer (Table QR)**: browse menu, add items to cart, place order, track order status live, view bill, submit review.
- **Kitchen (Staff)**: view live orders and update order status in real-time.
- **Admin (Staff)**: manage menu, see live bills, settle payments, analytics, staff, customers export, tables/QR, inventory, reviews.

---

## 2) Tech Stack & Libraries (Modules Used)

### Backend (Spring Boot)
Location: backend/
- Spring Boot 3.4.x, Java 17
- Spring Web (REST APIs)
- Spring Data JPA (MySQL persistence)
- Spring Validation (request validation patterns)
- Spring WebSocket (STOMP over WebSocket)
- Apache POI (Excel export)
- Lombok (boilerplate reduction)

### Frontend (React + Vite)
Location: frontend/
- React 18
- react-router-dom (routing)
- axios (API calls)
- @stomp/stompjs (WebSocket client)
- xlsx (client-side Excel processing in Admin portal)
- lucide-react (icons)
- TailwindCSS (styling)

---

## 3) Running the Project (Local)

### Ports
- Backend: http://localhost:8085
- Frontend: http://localhost:5173

### Database
- MySQL running on port 3306
- Database is created automatically from backend config

Backend config: backend/src/main/resources/application.yml

### Start Backend
From backend/:
- Windows: .\mvnw spring-boot:run

### Start Frontend
From frontend/:
- npm install
- npm run dev

---

## 4) Access Links, Roles, and Credentials

### Customer (QR simulation)
- URL: http://localhost:5173/login?tableId=1
- Action: enter name + mobile, send OTP, verify OTP.
- OTP is mocked (default): 123456

### Admin Portal
- URL: http://localhost:5173/admin/login
- Default seeded admin:
  - Username: admin
  - Password: admin123

### Kitchen Portal
- URL: http://localhost:5173/kitchen/login
- Example credentials (from ACCESS_GUIDES.txt):
  - Username: chef.muni
  - Password: password123

### Session persistence rules (important for automation)
- Customer session key: customer
  - CustomerMenu checks that:
    - stored tableId matches URL tableId
    - lastVisitedDate is today
- Admin session key: admin_session
  - StaffGuard checks session date is today and role is ADMIN
- Kitchen session key: kitchen_session
  - StaffGuard checks session date is today and role is KITCHEN

Automation note: sessions expire when the day changes (ISO date).

---

## 5) Frontend Application Modules (Screens + Buttons)

Frontend routes are defined in frontend/src/App.jsx.

### 5.1 Customer Portal

#### Route: /login?tableId=<id>  (CustomerLogin)
Purpose: Customer OTP login (mock OTP).

Buttons/actions:
- Send OTP
  - Calls POST /api/customers/otp/send
  - Verifications:
    - requires Name and Mobile
    - on success moves to OTP step
    - on error shows “Failed to send OTP”
- Verify & Access Menu
  - Calls POST /api/customers/otp/verify
  - Stores response in localStorage key customer
  - Redirects to /?tableId=<id>
- Resend
  - Re-triggers Send OTP
- Back (OTP step)
  - Returns to info entry step

Expected UI validations:
- Empty fields → error message
- Wrong OTP → “Invalid OTP”

---

#### Route: /?tableId=<id>  (CustomerMenu)
Purpose: Browse menu + place order.

Main interactive parts (buttons):
- Search toggle (magnifier / X)
  - Shows/hides search bar
- Dark mode toggle (🌙 / ☀️)
  - Changes page theme only (no backend calls)
- Category chips
  - Filter menu items by category
- Sort button
  - Options: Featured, Price Low→High, Price High→Low, Name (A-Z)
- Menu item card actions
  - Add to Cart
  - Increase (+)
  - Decrease (-)
- Cart open
  - Cart icon on header
  - Sticky “View Cart” button when cart has items

Cart panel actions:
- Close cart (X)
- Increase (+) / Decrease (-)
- Remove item
- Place Order
  - Calls POST /api/orders
  - On success:
    - clears cart
    - navigates to /tracker?orderId=<id>&tableId=<tableId>

Important backend mapping:
- Menu list: GET /api/menu
- Categories: GET /api/menu/categories
- Place order: POST /api/orders
  - Backend currently uses only { tableId, items[] }
  - UI sends extra fields (customerName, customerPhone) but backend OrderRequest ignores them.

Automation checks:
- When item.available = false, item is hidden (filtered in UI)
- Total calculation = Σ(price × qty)

---

#### Route: /tracker?tableId=<id>  (CustomerOrderTracker)
Purpose: Track all active orders in the current table session.

Key actions:
- Automatic load of “session orders”
  - Calls GET /api/orders/session/<tableId>
- Live updates via WebSocket
  - Subscribes to /topic/customer/<tableId>
- When order status becomes SERVED
  - Choice modal appears after a short delay

Buttons/actions:
- Choose Action (for served order)
  - Opens modal
- Modal: Add More Items
  - Navigates to /?tableId=<id>
- Modal: Finish Order
  - Confirms receipt:
    - PUT /api/orders/<orderId>/status?status=COMPLETED for each served order
  - Navigates to /bill?tableId=<id>
- Bottom bar actions:
  - Add More
  - View Bill
  - New Visit (when fully paid)

Automation checks:
- StatusBadge mapping must match backend status values
- StatusBar must progress correctly across statuses
- For REJECTED:
  - UI shows rejection reason

---

#### Route: /bill?tableId=<id>  (BillPage)
Purpose: Show bill, wait for payment confirmation, then redirect to Review.

Behavior:
- Loads session orders: GET /api/orders/session/<tableId>
- Live updates via WebSocket: /topic/customer/<tableId>
- When all orders in session are PAID:
  - shows “Payment Confirmed”
  - redirects to /review?tableId=<id>&sessionId=<sessionId>

Note: Payment is triggered by Admin portal “Settle Full Bill”.

---

#### Route: /review?tableId=<id>&sessionId=<sessionId>  (ReviewPage)
Purpose: Collect review after payment.

Buttons/actions:
- Star rating (1–5)
- Submit Review
  - POST /api/reviews
  - Backend rejects duplicate reviews for same sessionId
- Skip for now
  - Navigates to /?tableId=<id>
- Start New Visit (after successful submit)
  - Navigates to /?tableId=<id>

Automation checks:
- Rating required (overallRating != 0)
- Duplicate submission for same sessionId should return 400

---

### 5.2 Kitchen Portal

#### Route: /kitchen/login  (StaffLogin role=KITCHEN)
Purpose: staff login (kitchen).

Buttons/actions:
- Sign In to Dashboard
  - POST /api/staff/login
  - On success stores localStorage kitchen_session
  - Redirects to /kitchen
- Back to Menu
  - Navigates to /

---

#### Route: /kitchen  (KitchenDashboard)
Purpose: view & process orders (live updates).

Data sources:
- Initial fetch: GET /api/orders
- Live updates via WebSocket:
  - subscribe /topic/kitchen

Tabs:
- LIVE ORDERS
- TODAY’S HISTORY

Buttons/actions (LIVE tab):
- Accept (PENDING → ACCEPTED)
  - PUT /api/orders/<id>/status?status=ACCEPTED
- Reject (PENDING → REJECTED)
  - opens reject modal
  - Confirm Reject: PUT /api/orders/<id>/status/reject  { reason }
- Start Preparing (ACCEPTED → PREPARING)
- Ready (PREPARING → READY)
- Mark Served (READY → SERVED)
- Refresh (Header)
  - Manually re-fetches the live orders list.

Buttons/actions (header):
- LOGOUT
  - clears kitchen_session
  - redirects to /kitchen/login

Automation checks:
- Correct buttons appear only for matching statuses
- Rejected orders display rejection reason

---

### 5.3 Admin Portal

#### Route: /admin/login  (StaffLogin role=ADMIN)
- Same login flow as kitchen, stored key: admin_session

---

#### Route: /admin  (AdminMenuManager)
This is a multi-module admin dashboard. Navigation items:
- Analytics
- Menu
- Live Bills
- History
- Customers
- Rejected
- Staff
- Reviews
- Inventory
- Hotel & Tables

Below is a simple “what to click / what it does” list for each module.

##### A) Analytics (Business Analytics)
Primary actions:
- Export Report
  - Calls GET /api/analytics/export (Excel blob)
- Filters:
  - Search Analytics
  - From Date / To Date
  - Category scoping
  - Reset Filters

Key automation assertions:
- Summary cards render without null errors
- Charts reflect filter date range changes

Backend endpoints:
- GET /api/analytics/summary
- GET /api/analytics/dishes
- GET /api/analytics/dishes/bottom
- GET /api/analytics/categories
- GET /api/analytics/trend
- GET /api/analytics/peak-hours
- GET /api/analytics/payments
- GET /api/analytics/inventory
- GET /api/analytics/reviews
- GET /api/analytics/export

##### B) Menu (Menu Management)
Primary actions:
- Add Item
- Edit item (pencil)
- Delete item (trash)
- Filters:
  - Search item
  - Category
  - Availability
- Toggle Availability (in list)

Backend endpoints:
- GET /api/menu
- GET /api/menu/categories
- POST /api/menu (multipart form-data)
- PUT /api/menu/<id> (multipart form-data)
- PUT /api/menu/<id> (JSON update)
- DELETE /api/menu/<id>

Uploads:
- Saved images are served from http://localhost:8085/uploads/<file>

##### C) Live Bills
Primary actions:
- Print Bill
  - triggers browser print
- Settle Full Bill
  - opens payment modal
  - choose payment method
  - confirm → PUT /api/orders/<someOrderIdInSession>/payment  { status: "PAID", paymentMethod }

Important behavior:
- Backend settles the *full session*:
  - marks all orders for session as PAID
  - sets each order status to COMPLETED
  - clears table.currentSessionId and sets table AVAILABLE
  - creates a Payment record (payments table)

##### D) History
Primary actions:
- Export Excel
- Filters:
  - Date range
  - Table
  - Payment method
  - Clear Filters

Backend data source:
- Admin uses GET /api/admin/orders and filters in UI

##### E) Customers
Primary actions:
- Search customer by name/mobile
- Sort behavior: Frequent / Recent / Sparse
- Export CSV/Excel
  - GET /api/customers/export

##### F) Rejected Orders
Primary actions:
- Filter by date range
- Inspect rejection reasons

##### G) Staff
Primary actions:
- Add Staff
- Edit staff
- Delete staff

Backend endpoint:
- GET /api/staff
- POST /api/staff
- PUT /api/staff/<id>
- DELETE /api/staff/<id>

##### H) Reviews
Primary actions:
- View review cards
- Delete review

Backend endpoints:
- GET /api/reviews
- DELETE /api/reviews/<id>

WebSocket:
- Admin subscribes to /topic/reviews for real-time review updates

##### I) Inventory (Raw Materials)
Primary actions:
- Add Material
- Edit Material
- Delete Material
- Close Day
  - POST /api/admin/closing
- View Usage History
  - Toggles between current stock and a historical audit trail.
- Date Filtering (Usage History)
  - Filters usage logs by a specific month or custom range.
- Export to Excel
  - GET /api/analytics/inventory/export (Excel blob)

Backend endpoints:
- GET /api/raw-materials
- POST /api/raw-materials
- PUT /api/raw-materials/<id>
- DELETE /api/raw-materials/<id>
- POST /api/admin/closing

##### J) Hotel & Tables
Primary actions:
- Update hotel info
  - PUT /api/restaurant/<id>
- Add Table
  - POST /api/tables
- Generate QR
  - POST /api/tables/<id>/generate-qr
- View QR / Print QR
  - UI-only print behavior

Backend endpoints:
- GET /api/restaurant
- PUT /api/restaurant/<id>
- GET /api/tables
- POST /api/tables
- PUT /api/tables/<id>
- POST /api/tables/<id>/generate-qr

---

## 6) Backend Modules (API Inventory)

Base URL: http://localhost:8085

### Menu Module
Controller: MenuController
- GET /api/menu
- GET /api/menu/categories
- POST /api/menu (multipart)
- PUT /api/menu/<id> (multipart)
- PUT /api/menu/<id> (JSON)
- DELETE /api/menu/<id>

### Orders Module
Controller: OrderController
- POST /api/orders
- POST /api/orders/<tableId>/add-items
- GET /api/orders
- GET /api/orders/<id>
- PUT /api/orders/<id>/status?status=<OrderStatus>
- PUT /api/orders/<id>/status/reject
- PUT /api/orders/<id>/payment
- GET /api/orders/session/<tableId>

### Admin Orders Module
Controller: AdminOrderController
- GET /api/admin/orders (optional query: tableId)

### Customer Module
Controller: CustomerController
- POST /api/customers/otp/send
- POST /api/customers/otp/verify
- GET /api/customers/admin
- GET /api/customers/export

### Staff Module
Controller: StaffController
- GET /api/staff
- POST /api/staff
- PUT /api/staff/<id>
- DELETE /api/staff/<id>
- POST /api/staff/login

### Reviews Module
Controller: ReviewController
- POST /api/reviews
- GET /api/reviews
- GET /api/reviews/session/<sessionId>
- DELETE /api/reviews/<id>

### Analytics Module
Controller: AnalyticsController
- GET /api/analytics/summary
- GET /api/analytics/dishes
- GET /api/analytics/dishes/bottom
- GET /api/analytics/categories
- GET /api/analytics/trend
- GET /api/analytics/peak-hours
- GET /api/analytics/payments
- GET /api/analytics/inventory
- GET /api/analytics/reviews
- GET /api/analytics/inventory/logs?start=...&end=...
- GET /api/analytics/inventory/export?start=...&end=...
- GET /api/analytics/export

### Tables Module
Controller: TableController
- GET /api/tables
- POST /api/tables
- PUT /api/tables/<id>
- POST /api/tables/<id>/generate-qr
- DELETE /api/tables/<id> (returns 403; deletion blocked)

### Restaurant Module
Controller: RestaurantController
- GET /api/restaurant
- PUT /api/restaurant/<id>

### Raw Materials Module
Controller: RawMaterialController
- GET /api/raw-materials
- POST /api/raw-materials
- PUT /api/raw-materials/<id>
- DELETE /api/raw-materials/<id>

### Day Closing Module
Controller: ClosingController
- POST /api/admin/closing

---

## 7) WebSocket / Real-time Model

### WebSocket endpoint
- ws://localhost:8085/ws

### Broker destinations
- Subscriptions use /topic/**

### Topics used by the app
- /topic/kitchen
  - KitchenDashboard subscribes
  - receives full Order object updates
- /topic/admin
  - AdminMenuManager uses for live order list updates
- /topic/customer/<tableId>
  - CustomerOrderTracker + BillPage subscribe
- /topic/reviews
  - AdminMenuManager subscribes for live review updates

Automation tip:
- In UI E2E tests, you can assert real-time changes by:
  - placing order → verify it appears in Kitchen without refresh
  - updating status in Kitchen → verify tracker updates without refresh

---

## 8) Core Data Model (Entities) — Automation “Model Review”

### Order lifecycle
Entity: Order
Key fields:
- id
- restaurantTable
- sessionId
- status (OrderStatus)
- totalAmount
- paymentStatus (PaymentStatus)
- paymentMethod (CASH/UPI/CARD)
- isActive
- rejectionReason
- createdAt
- items[] (OrderItem)

OrderStatus enum:
- PENDING → ACCEPTED → PREPARING → READY → SERVED → COMPLETED
- REJECTED (can happen from most non-paid states)

Important server rules (OrderService.updateOrderStatus)
- REJECTED cannot be updated further
- COMPLETED cannot be updated further
- SERVED can only go to COMPLETED
- Backward transitions are blocked

### Table session model
Entity: RestaurantTable
- currentSessionId is set when the first order is created
- settlement clears currentSessionId and sets status AVAILABLE

### Payments
Payment settlement is session-based:
- Admin “Settle Full Bill” calls updatePaymentStatus(orderId, PAID)
- Backend finds all orders by sessionId and marks them PAID + COMPLETED
- Creates Payment row containing subtotal/tax/service/total

### Customers
Customer OTP is mocked:
- sendOtp always sets otp=123456
- verifyOtp increments visitCount and updates lastVisitedDate/lastTableUsed

### Menu
MenuItem has:
- available boolean; customer UI hides unavailable items
- imageUrl stored as /uploads/<filename>

### Reviews
Review is unique per sessionId (server blocks duplicates).

---

## 9) Automation Testing Strategy (Practical)

### 9.1 Recommended test layers
1) API automation (fast)
- Validate endpoints, status codes, payload schemas
- Use Postman collection: ServeSmart_Postman_Collection.json

2) UI E2E (critical paths)
- Use Playwright or Cypress
- Focus on a few end-to-end flows + role-based portals

3) Real-time tests
- Validate WebSocket-driven updates for order tracking

---

### 9.2 E2E Flow (Golden Path) — what to automate

Flow: Customer → Kitchen → Customer → Admin → Customer → Review
1) Customer login (OTP)
- open /login?tableId=1
- fill name + mobile
- Send OTP
- enter 123456
- Verify

2) Place order
- add 1–2 items
- open cart
- Place Order
- assert redirected to /tracker?tableId=1

3) Kitchen processes
- login to /kitchen/login
- Accept → Start Preparing → Ready → Mark Served

4) Customer confirms receipt
- tracker shows SERVED
- Finish Order
- navigates to bill

5) Admin settles
- login to /admin/login
- go to Live Bills
- choose table
- Settle Full Bill
- choose payment method (UPI/CASH/CARD)
- confirm

6) Customer bill auto-updates
- bill page receives PAID updates over WebSocket
- redirects to review

7) Customer submits review
- choose rating
- submit

Key assertions:
- Order appears in kitchen/admin without refresh
- Status updates propagate to customer in real time
- Payment settlement closes the session (table becomes AVAILABLE)
- Review cannot be posted twice for same sessionId

---

### 9.3 Suggested Page Object Model (POM) for UI automation

Create page objects per route:
- CustomerLoginPage
  - actions: sendOtp(name, mobile), verifyOtp(otp)
- CustomerMenuPage
  - actions: search(text), selectCategory(name), addItemByName(name), openCart(), placeOrder()
- CartPanel
  - actions: increase(name), decrease(name), remove(name)
- CustomerTrackerPage
  - actions: waitForStatus(orderIndex, status), openChoiceModal(), finishOrder(), addMoreItems()
- KitchenLoginPage / KitchenDashboardPage
  - actions: accept(orderId), startPreparing(orderId), markReady(orderId), markServed(orderId), reject(orderId, reason)
- AdminLoginPage / AdminDashboardPage
  - actions: goToLiveBills(), settleBillForTable(tableNumber, method)
- BillPage
  - actions: waitForPaid(), waitForRedirectToReview()
- ReviewPage
  - actions: setRating(n), submit(comment)

Locator strategy (stable):
- Prefer getByRole + button text (e.g., “Send OTP”, “Verify & Access Menu”, “Place Order”)
- Avoid Tailwind class-based selectors (they are not stable)

---

### 9.4 API automation checklist

Key API tests:
- Menu
  - GET /api/menu returns list
  - GET /api/menu/categories returns list
- Orders
  - POST /api/orders with valid tableId and items returns Order
  - PUT /api/orders/<id>/status enforces transitions
  - PUT reject sets rejectionReason and status=REJECTED
  - GET /api/orders/session/<tableId> returns current session orders
- Payments
  - PUT /api/orders/<id>/payment with status=PAID marks entire session paid
- Customers
  - OTP send + verify returns customer
- Reviews
  - POST review works once per sessionId
- Inventory Reports
  - GET /api/analytics/inventory/logs with date range returns usage history
  - GET /api/analytics/inventory/export returns a valid .xlsx blob

---

## 10) Known Behaviors / Edge Cases (Automation Notes)

- CustomerMenu requires tableId in URL, otherwise shows “Invalid Table”.
- Customer session is per-table and per-day; tests should not reuse stale localStorage.
- Staff sessions are per-day; for long-running CI, recreate sessions each run.
- Order status transitions are strict; attempting backward transitions should error.
- Table deletion is blocked (API returns 403).

---

## 11) Quick Reference: Data Seeds

At backend startup (CommandLineRunner):
- Categories: Starters, Main Course, Beverages, Desserts
- Sample Menu Items (example)
- Tables 1 and 2
- Raw materials Flour/Chicken/Milk

Default admin is also ensured via StaffController @PostConstruct if missing.

---

If you want, I can also:
- add a “Test IDs” convention (data-testid) to key buttons to make UI automation much more stable, OR
- generate a Playwright E2E skeleton + sample tests for the golden path.
