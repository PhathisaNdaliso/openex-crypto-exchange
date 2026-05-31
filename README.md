# OpenEx Crypto Exchange Platform

OpenEx is a simulated cryptocurrency exchange platform built to demonstrate how a modern trading product can evolve from backend foundations into a live, data-driven frontend experience. The project combines a Spring Boot backend, PostgreSQL persistence, real-time WebSocket streaming, and a React dashboard designed for monitoring markets, wallets, and order activity.

## Project Overview

OpenEx is structured as a sprint-based build:

- Week 1 focused on backend foundations, API design, persistence, and service architecture.
- Week 2 focused on frontend experience, market visualization, REST integration, and real-time updates.

The root README serves as the project entry point, while each sprint has its own standalone documentation under `docs/`.

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Redis cache support
- Spring WebSocket with STOMP
- Maven
- React
- Vite
- Axios
- React Router
- Chart.js with `react-chartjs-2`
- Playwright for documentation screenshots

## Architecture Overview

The codebase is split into a backend service, a frontend client, and sprint documentation:

```text
openex-crypto-exchange/
  backend/
    src/main/java/com/openex/backend/
      config/
      controller/
      dto/
      model/
      repository/
      service/
  frontend/
    src/
      components/
      pages/
      services/
  docs/
    week-1/
      README.md
    week-2/
      README.md
    screenshots/
```

### Backend

- Layered Spring Boot architecture with controllers, services, repositories, entities, DTOs, and configuration classes
- PostgreSQL-backed persistence with JPA and Hibernate
- WebSocket trade streaming for live market updates

### Frontend

- React + Vite dashboard with route-based pages
- Centralized REST and WebSocket service modules
- Live charts, wallet summaries, trade activity, and market metrics

## Setup Instructions

### 1. Start PostgreSQL

Ensure PostgreSQL is running and that the `openex` database exists.

### 2. Start the backend

```bash
cd backend
./mvnw spring-boot:run
```

### 3. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

### 4. Optional frontend environment overrides

Use [frontend/.env.example](frontend/.env.example) if you want to point the frontend at a different backend host.

## Weekly Deliverables

- [Week 1: Backend Foundations & API Design](docs/week-1/README.md)
- [Week 2: Frontend & Data Visualization](docs/week-2/README.md)

## Documentation Navigation

- [Project Overview](README.md)
- [Week 1 Documentation](docs/week-1/README.md)
- [Week 2 Documentation](docs/week-2/README.md)

## Current State

OpenEx now includes a working Spring Boot backend, a React trading dashboard, seeded demo market data, live trade streaming, and sprint-based documentation that can be reviewed independently by week.
