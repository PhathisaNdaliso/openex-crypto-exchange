# OpenEx Crypto Exchange Backend

## Week 1 - Backend Foundations & API Design

### Project Overview

OpenEx is a simulated cryptocurrency exchange backend built with Spring Boot and PostgreSQL. The goal of the project is to model the core backend capabilities of a crypto trading platform, including user management, wallet tracking, order handling, real-time messaging, and persistent data storage. Week 1 focused on establishing the backend architecture, designing REST APIs, configuring WebSocket communication, and integrating the application with PostgreSQL through Spring Data JPA and Hibernate.

### Technologies Used

- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Spring WebSocket
- Maven
- Lombok
- Thunder Client
- Hibernate

### Features Implemented

- User management APIs
- Wallet management APIs
- Order management APIs
- PostgreSQL integration
- DTO-based API communication
- Validation logic
- Layered architecture (`controller` / `service` / `repository` / `model`)
- WebSocket STOMP configuration

### Project Structure

The backend follows a layered Spring Boot package structure under `com.openex.backend`:

- `controller`
- `service`
- `repository`
- `model`
- `dto`
- `config`

### API Endpoints

The following core Week 1 endpoints were implemented:

- `GET /api/users`
- `POST /api/users`
- `GET /api/wallets`
- `POST /api/wallets`
- `GET /api/orders`
- `POST /api/orders`

Additional update and delete endpoints were also scaffolded as part of the CRUD backend design.

### Database

PostgreSQL was configured successfully as the primary relational database for the backend. Using Spring Data JPA and Hibernate, the application maps entity classes such as `User`, `Wallet`, and `Order` to database tables automatically. Hibernate is configured to generate and update the schema from the JPA entity definitions during development.

### WebSocket Configuration

Real-time communication support was configured using Spring Boot WebSocket with STOMP messaging and SockJS fallback support.

- Endpoint: `/ws`
- STOMP enabled
- SockJS enabled
- Broker destinations: `/topic` and `/queue`

This configuration provides the foundation for future real-time features such as market data streaming, order updates, and live trading notifications.

### Screenshots

#### POST `/api/orders`

![POST /api/orders](</Users/boniswandaliso/Desktop/Phathisa openex crypto/post orders.png>)

#### GET `/api/wallets`

![GET /api/wallets](</Users/boniswandaliso/Desktop/Phathisa openex crypto/get wallets.png>)

#### GET `/api/users`

![GET /api/users](</Users/boniswandaliso/Desktop/Phathisa openex crypto/get users.png>)

#### GET `/api/orders`

![GET /api/orders](</Users/boniswandaliso/Desktop/Phathisa openex crypto/get orders.png>)

### Conclusion

Week 1 successfully established the backend foundation for the OpenEx crypto exchange simulation platform. With the core layered architecture, REST APIs, PostgreSQL integration, DTO-driven communication, validation logic, and WebSocket configuration now in place, the project is well prepared for future work on trading logic, authentication, and real-time exchange features.
