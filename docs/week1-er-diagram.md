# OpenEx Week 1 ER Diagram

```mermaid
erDiagram
    USERS ||--o{ WALLETS : owns
    USERS ||--o{ ORDERS : places

    USERS {
        BIGINT id PK
        VARCHAR username
        VARCHAR email
        VARCHAR password
        BOOLEAN enabled
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    WALLETS {
        BIGINT id PK
        BIGINT user_id FK
        VARCHAR currency
        DECIMAL balance
        DECIMAL locked_balance
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    ORDERS {
        BIGINT id PK
        BIGINT user_id FK
        VARCHAR side
        VARCHAR type
        VARCHAR status
        VARCHAR base_currency
        VARCHAR quote_currency
        DECIMAL quantity
        DECIMAL filled_quantity
        DECIMAL price
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
```

## Relationship Notes

- `users.id` is the primary key for `users`
- `wallets.user_id` is a foreign key to `users.id`
- `orders.user_id` is a foreign key to `users.id`
- One user can own many wallets
- One user can place many orders
