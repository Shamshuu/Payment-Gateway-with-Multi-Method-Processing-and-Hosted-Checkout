# 💳 Payment Gateway with Multi-Method Processing & Hosted Checkout

A full-stack payment gateway simulation built with **Java Spring Boot**, **React**, **PostgreSQL**, and **Docker**. This system mimics a real-world payment infrastructure, featuring a secure REST API, a merchant dashboard, and a customer-facing checkout page with simulated bank latency and success/failure logic.

---

## 🚀 Features

* **Core Payment Engine:**
  * REST API for creating Orders and processing Payments.
  * Strategy Pattern implementation for multiple payment methods (**UPI** & **Credit/Debit Card**).
  * **Simulation Logic:** Mimics real banking delays (5-10 seconds) and random success/failure rates.
* **Security:**
  * `X-Api-Key` and `X-Api-Secret` authentication for Merchant APIs.
  * CORS configuration for secure frontend communication.
* **Merchant Dashboard (Port 3000):**
  * View API Credentials.
  * Real-time analytics (Total Volume, Success Rate).
  * Transaction history table.
* **Hosted Checkout (Port 3001):**
  * Secure payment form for customers.
  * Real-time polling of payment status.
  * Auto-redirect on success/failure.

---

## 🛠️ Tech Stack

* **Backend:** Java 17, Spring Boot 3.x, Maven
* **Database:** PostgreSQL
* **Frontend:** React 18 (Vite), Axios, Nginx
* **Infrastructure:** Docker, Docker Compose

---

## 📂 Project Structure

```text
payment-gateway/
├── backend/                 # Spring Boot API
│   ├── src/main/java/com/gateway/
│   │   ├── config/          # Security & CORS config
│   │   ├── controllers/     # API Endpoints
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── models/          # JPA Entities
│   │   ├── repositories/    # Database Access
│   │   └── services/        # Business Logic
│   └── Dockerfile
├── frontend/                # Merchant Dashboard (React)
│   ├── src/pages/
│   └── Dockerfile
├── checkout-page/           # Customer Checkout (React)
│   ├── src/
│   └── Dockerfile
└── docker-compose.yml
```

---

## ⚙️ Prerequisites

* Docker Desktop (running)

No local Java or Node installation required.

---

## 🏃‍♂️ How to Run

```bash
docker-compose up -d --build
```

Verify containers:

```bash
docker ps
```

---

## 🔗 Access Points

| Service | URL |
|-------|-----|
| Merchant Dashboard | http://localhost:3000 |
| Checkout Page | http://localhost:3001 |
| Backend API | http://localhost:8000 |
| Database | localhost:5432 |

---

## 🧪 End-to-End Testing

### Step 1: Login
- Email: `test@example.com`
- Password: any

### Step 2: Create Order

```bash
curl -X POST http://localhost:8000/api/v1/orders \
-H "Content-Type: application/json" \
-H "X-Api-Key: key_test_abc123" \
-H "X-Api-Secret: secret_test_xyz789" \
-d '{ "amount": 50000, "currency": "INR", "receipt": "receipt_1" }'
```

### Step 3: Checkout

```
http://localhost:3001/checkout?order_id=YOUR_ORDER_ID
```

---

## ⚙️ Simulation Config

Configured via environment variables:

| Variable | Default |
|--------|--------|
| UPI_SUCCESS_RATE | 0.90 |
| CARD_SUCCESS_RATE | 0.95 |
| PROCESSING_DELAY_MIN | 5000 |
| PROCESSING_DELAY_MAX | 10000 |
| TEST_MODE | false |

---

## 🐛 Troubleshooting

- **CORS errors:** verify backend CORS config
- **Stuck processing:** ensure polling endpoint exists
- **DB issues:** restart API container

---
