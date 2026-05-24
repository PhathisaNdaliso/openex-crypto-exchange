# OpenEx Crypto Exchange Backend

## Week 1 - Backend Foundations & API Design

### Project Overview

OpenEx is a simulated cryptocurrency exchange backend built with Spring Boot and PostgreSQL. Week 1 focused on establishing the backend foundation: layered architecture, REST APIs for core exchange resources, PostgreSQL persistence through Spring Data JPA and Hibernate, and initial WebSocket/STOMP support for real-time messaging. Redis caching support has now been added in code and configuration, but it still requires a running Redis server locally to be operational at runtime.

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
- Functional STOMP message handlers for market and order events
- Redis cache integration in code for service-layer reads

### Project Structure

The backend follows a layered Spring Boot package structure under `com.openex.backend`:

- `controller`
- `service`
- `repository`
- `model`
- `dto`
- `config`

### API Endpoints

The following REST endpoints are implemented:

- `GET /api/users`
- `POST /api/users`
- `GET /api/wallets`
- `POST /api/wallets`
- `GET /api/orders`
- `POST /api/orders`

Additional CRUD endpoints are also available:

- `GET /api/users/{id}`
- `PUT /api/users/{id}`
- `DELETE /api/users/{id}`
- `GET /api/wallets/{id}`
- `PUT /api/wallets/{id}`
- `DELETE /api/wallets/{id}`
- `GET /api/orders/{id}`
- `PUT /api/orders/{id}`
- `DELETE /api/orders/{id}`

### Database

PostgreSQL is configured as the primary relational database for the backend through:

- `spring.datasource.url=jdbc:postgresql://localhost:5432/openex`
- Spring Data JPA repositories
- Hibernate `ddl-auto=update`

The current entity model includes:

- `User`
- `Wallet`
- `Order`

Hibernate generates and updates tables automatically from the JPA entity definitions during development. The backend test context has successfully booted against PostgreSQL in this environment.

### WebSocket Configuration

Real-time communication support is configured using Spring Boot WebSocket with STOMP messaging and SockJS fallback support.

- Endpoint: `/ws`
- STOMP enabled
- SockJS enabled
- Broker destinations: `/topic` and `/queue`
- Application destination prefix: `/app`

Implemented WebSocket handlers include:

- `@MessageMapping("/market.broadcast")` -> broadcasts to `/topic/market`
- `@MessageMapping("/orders.broadcast")` -> broadcasts to `/topic/orders`
- `@MessageMapping("/ping")` -> replies on `/user/queue/status`

Order and wallet service events are also published to broker destinations when REST mutations occur.

### Redis Caching Status

Redis support is present in the backend codebase:

- `spring-boot-starter-data-redis` added
- `spring-boot-starter-cache` added
- `RedisConfig` enables Spring caching
- Cache annotations are applied in service classes
- Redis properties are configured in `application.properties`

Current status:

- Redis server was **not listening on `localhost:6379`** during verification
- Caching is implemented in code, but runtime caching depends on starting Redis locally
- The current cached service reads are:
  - `UserService#getAllUsers`
  - `UserService#getUserById`
  - `WalletService#getAllWallets`
  - `WalletService#getWalletById`
  - `OrderService#getAllOrders`
  - `OrderService#getOrderById`

### ER Diagram

The Week 1 ER diagram is documented in [docs/week1-er-diagram.md](docs/week1-er-diagram.md).

## Screenshots

### POST `/api/orders`

![POST /api/orders](backend/post%20orders.png)

### GET `/api/wallets`

![GET /api/wallets](backend/get%20wallets.png)

### GET `/api/users`

![GET /api/users](backend/get%20users.png)

### GET `/api/orders`

![GET /api/orders](backend/get%20orders.png)
### Conclusion

Week 1 established a solid backend foundation for the OpenEx crypto exchange simulation platform. The project now has working domain entities, repositories, services, REST APIs, PostgreSQL persistence, DTO-based request and response handling, and a functional STOMP/WebSocket layer. Redis caching has been integrated in code but still requires a running Redis instance to be considered fully operational in a local environment.
