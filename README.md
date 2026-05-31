# OpenEx Crypto Exchange Platform

## Week 2 - Frontend & Data Visualization

### Project Overview

OpenEx is a simulated cryptocurrency exchange platform built with a Spring Boot backend and a React frontend. Week 1 established the backend foundation with PostgreSQL persistence, REST APIs, DTOs, validation, and WebSocket infrastructure. Week 2 focuses on the frontend experience: a Vite-powered React dashboard, REST API integration, Chart.js market visualization, and real-time trade streaming over WebSockets.

### Week 2 Objectives

- Build a React frontend dashboard
- Integrate Chart.js for trading visualization
- Connect the frontend to Spring Boot REST APIs
- Implement live WebSocket updates
- Deliver a frontend prototype and API integration demo

### Tech Stack

#### Backend

- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Spring WebSocket
- Maven
- Lombok
- Hibernate

#### Frontend

- React
- Vite
- Axios
- React Router
- Chart.js
- react-chartjs-2
- SockJS client
- STOMP WebSocket client

### Features Implemented

#### Backend

- User, wallet, and order REST APIs
- PostgreSQL datasource configuration and JPA persistence
- DTO-based request and response flow
- Validation and service-layer business rules
- WebSocket STOMP broker configuration on `/ws`
- Simulated trade publishing to `/topic/trades`
- Automatic demo data seeding for users, wallets, orders, and trade history
- Market overview API for ticker, chart history, and dashboard metrics

#### Frontend

- React + Vite application scaffold in `frontend/`
- Reusable dashboard components
- Multi-page navigation with React Router
- Centralized Axios service for REST calls
- Centralized WebSocket client service for trade streaming
- Wallet summary cards
- Orders table
- Live crypto ticker
- Real-time trading chart
- Responsive dark-theme dashboard layout

### Frontend Structure

```text
frontend/
  src/
    components/
      Navbar.jsx
      WalletCard.jsx
      TradingChart.jsx
      OrdersTable.jsx
      LiveTicker.jsx
    pages/
      Dashboard.jsx
      Wallets.jsx
      Trading.jsx
    services/
      api.js
      websocket.js
```

### REST API Integration

The React frontend currently consumes these backend endpoints:

- `GET /api/users`
- `GET /api/wallets`
- `GET /api/orders`
- `GET /api/market/overview`

The Axios service in `frontend/src/services/api.js` centralizes all HTTP calls so UI components stay focused on rendering and state updates.

The market overview endpoint returns:

- seeded ticker prices
- historical chart points
- dashboard trading metrics
- immediate demo-ready market state on startup

### WebSocket Integration

OpenEx uses Spring Boot WebSocket with STOMP and SockJS for real-time frontend updates.

- Endpoint: `/ws`
- Frontend subscription: `/topic/trades`
- Frontend send target available: `/app/trades.broadcast`
- Backend also exposes `/topic/market` and `/topic/orders`

The frontend WebSocket client in `frontend/src/services/websocket.js`:

- Opens a SockJS connection
- Activates a STOMP client
- Subscribes to `/topic/trades`
- Pushes incoming trade data into the dashboard ticker and charts without refresh

### Chart.js Visualization

Chart.js is used through `react-chartjs-2` to render a live updating line chart for market prices. The chart is seeded with realistic BTC and ETH values, then updated with incoming WebSocket trades from the backend trade feed.

### Setup Instructions

#### 1. Start PostgreSQL

Ensure PostgreSQL is running and that the `openex` database matches the backend configuration in `backend/src/main/resources/application.properties`.

#### 2. Start the backend

```bash
cd backend
./mvnw spring-boot:run
```

#### 3. Install frontend dependencies

```bash
cd frontend
npm install
```

#### 4. Start the frontend

```bash
cd frontend
npm run dev
```

#### 5. Optional environment overrides

Use `frontend/.env.example` if you want to point the frontend to a different backend host:

- `VITE_API_BASE_URL`
- `VITE_WS_URL`

### Screenshots

#### Dashboard

![OpenEx Dashboard](docs/screenshots/dashboard.png)

#### Trading Page

![OpenEx Trading Page](docs/screenshots/trading-page.png)

#### Wallet Section

![OpenEx Wallets Page](docs/screenshots/wallets-page.png)

#### Live Chart

![OpenEx Live Chart](docs/screenshots/live-chart.png)

#### Market Metrics

![OpenEx Market Metrics](docs/screenshots/market-metrics.png)

#### Mobile Responsive View

![OpenEx Mobile Dashboard](docs/screenshots/mobile-dashboard.png)

#### API Integration Evidence

![OpenEx API Integration Evidence](docs/screenshots/api-integration.png)

#### WebSocket Stream Evidence

![OpenEx WebSocket Stream Evidence](docs/screenshots/websocket-stream.png)

### Current Status

Week 2 implementation is in place and verified at build level:

- Backend test context passes
- Frontend production build passes
- Real-time backend trade publishing to `/topic/trades` is wired

What still depends on local runtime:

- Seeing live data in the browser requires the backend app to be running
- REST views require records in PostgreSQL
- Redis caching remains optional for Week 2 and still depends on a running Redis instance if you enable it with `OPENEX_CACHE_TYPE=redis`

### Week 1 Reference

The Week 1 ER diagram remains available at [docs/week1-er-diagram.md](docs/week1-er-diagram.md).

### Conclusion

Week 1 established a solid backend foundation for the OpenEx crypto exchange simulation platform. The project now has working domain entities, repositories, services, REST APIs, PostgreSQL persistence, DTO-based request and response handling, and a functional STOMP/WebSocket layer. Redis caching has been integrated in code but still requires a running Redis instance to be considered fully operational in a local environment.
