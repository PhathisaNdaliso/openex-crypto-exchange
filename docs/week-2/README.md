# Week 2 – Frontend & Data Visualization

[Back to Project Overview](../../README.md) | [Back to Week 1](../week-1/README.md)

## Objectives

- Build a React frontend dashboard for OpenEx
- Connect the frontend to Spring Boot REST APIs
- Visualize live market data with Chart.js
- Integrate WebSocket streaming for real-time trade updates
- Deliver a realistic frontend prototype backed by seeded demo data

## Technologies Used

- React
- Vite
- Axios
- React Router
- Chart.js
- `react-chartjs-2`
- STOMP WebSocket client
- Spring Boot backend
- PostgreSQL
- Playwright

## React Frontend Architecture

The frontend is organized for modular growth:

```text
frontend/src/
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

Design principles used in Week 2:

- reusable presentation components
- route-level pages for major product surfaces
- centralized API and WebSocket services
- responsive layouts for desktop and mobile

## API Integration

The frontend consumes backend data through a centralized Axios service.

Integrated endpoints:

- `GET /api/users`
- `GET /api/wallets`
- `GET /api/orders`
- `GET /api/market/overview`

What this enables on first load:

- populated wallet cards
- recent order activity
- seeded market history for charts
- precomputed market metrics

## Chart.js Integration

Week 2 uses Chart.js through `react-chartjs-2` to render the main market chart.

Implemented behavior:

- seeded historical market points on initial load
- line chart visualization for live market movement
- rolling updates as new trade events arrive
- responsive chart surface designed as the hero component of the dashboard

## WebSocket Integration

The frontend connects to the backend trade feed over STOMP.

Configuration:

- endpoint: `/ws`
- subscription: `/topic/trades`

Week 2 WebSocket outcomes:

- live trade data streams into the UI without refresh
- ticker prices update as new trades arrive
- trading chart series append new points dynamically
- live trade tape reflects current trade activity

## Market Simulation

To support a demo-ready interface, the backend provides seeded and simulated market behavior.

Simulation features:

- seeded demo users, wallets, and orders
- seeded market overview history
- recurring simulated trade publishing
- BTC, ETH, SOL, and USDT ticker activity
- rolling metrics such as 24h volume and active trades

## Dashboard Features

The dashboard was refined to feel closer to a fintech product than a generic admin panel.

Features delivered:

- chart-first hero layout
- wallet summary cards
- live market ticker
- market metrics rail
- readable recent activity feed
- error and loading states
- layered dark-theme surfaces with improved hierarchy

## Trading Page Features

The trading page focuses on execution visibility and market movement.

Features delivered:

- pair switching between major assets
- live trade chart updates
- live WebSocket trade tape
- recent orders table
- real-time connection status feedback

## Responsive Design

Week 2 included responsive adjustments for both desktop and mobile use.

Responsive improvements:

- stacked layout for smaller screens
- preserved chart readability on mobile
- flexible card layouts for wallet and activity sections
- screenshot-backed mobile verification

## Screenshots

### Dashboard

![Dashboard](../screenshots/dashboard.png)

### Trading Page

![Trading Page](../screenshots/trading-page.png)

### Wallets Page

![Wallets Page](../screenshots/wallets-page.png)

### Live Chart

![Live Chart](../screenshots/live-chart.png)

### Market Metrics

![Market Metrics](../screenshots/market-metrics.png)

### Mobile Dashboard

![Mobile Dashboard](../screenshots/mobile-dashboard.png)

### API Integration Evidence

![API Integration Evidence](../screenshots/api-integration.png)

### WebSocket Stream Evidence

![WebSocket Stream Evidence](../screenshots/websocket-stream.png)

## Challenges Faced

- Moving from a static prototype to a believable live simulation
- Keeping the dashboard visually intentional rather than template-like
- Making WebSocket behavior observable in documentation, not only in code
- Balancing seeded startup data with real-time updates cleanly

## Learning Outcomes

- How to structure a React frontend around reusable product surfaces
- How to combine REST hydration with WebSocket streaming
- How live charts require state management beyond simple rendering
- How UX polish, spacing, and hierarchy materially improve technical demos
- How documentation artifacts can prove runtime integration without relying only on source review

## Deliverables Achieved

- React + Vite frontend
- REST API integration with the Spring Boot backend
- seeded live market dashboard
- Chart.js trading visualization
- WebSocket streaming integration
- responsive UI prototype
- runtime screenshots for API and WebSocket evidence

[Back to Project Overview](../../README.md) | [Back to Week 1](../week-1/README.md)
