# E-Commerce Platform

A full-stack e-commerce application with a Spring Boot REST API and a React/Vite storefront. It supports product browsing, cart and order management, Paymob payments, administration tools, notifications, customer support chat, and personalized product recommendations.

## Features

- Customer registration, login, and JWT-based authentication
- Product catalog with search, category, price, and sorting filters
- Shopping cart, checkout, order history, and Paymob payment initiation
- Customer notifications and real-time support conversations
- Admin dashboards for products, categories, inventory, and orders
- Super-admin account management
- Product image storage through AWS S3
- Flyway database migrations and seed data
- AI-backed recommendations, with a popular-products fallback when no purchase history or AI response is available
- OpenAPI/Swagger documentation

## Technology

| Area | Stack |
| --- | --- |
| Backend | Java 17, Spring Boot 3.5, Spring Security, Spring Data JPA |
| Database | MySQL, Flyway |
| Cache | Redis |
| Messaging | RabbitMQ |
| Frontend | React 19, Vite |
| External services | Paymob, AWS S3, Hugging Face, SMTP |

## Project layout

```text
.
├── src/main/java/          # Spring Boot application and REST API
├── src/main/resources/     # Application configuration and Flyway migrations
├── src/test/               # Backend tests
├── frontend/               # React storefront and administration UI
├── pom.xml                 # Backend dependencies and build configuration
└── README.md
```

## Prerequisites

- Java 17+
- MySQL 8+
- Redis
- RabbitMQ
- Node.js 20+ and npm

You will also need credentials for any enabled external integrations: Paymob, AWS S3, SMTP, and Hugging Face.

## Configuration

The backend reads secrets from environment variables. Set these before running the application:

| Variable | Purpose |
| --- | --- |
| `DB_USERNAME`, `DB_PASSWORD` | MySQL database credentials |
| `JWT_SECRET` | JWT signing secret |
| `REDIS_PASSWORD` | Redis password |
| `RABBIT_USERNAME`, `RABBIT_PASSWORD` | RabbitMQ credentials |
| `PAYMOB_API_KEY`, `PAYMOB_SECRET_KEY`, `PAYMOB_PUBLIC_KEY`, `PAYMOB_HMAC` | Paymob integration |
| `PAYMOB_NOTIFICATION_URL`, `PAYMOB_REDIRECTION_URL` | Paymob callback URLs |
| `MAIL_USERNAME`, `MAIL_PASSWORD` | SMTP credentials |
| `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_REGION`, `AWS_S3_BUCKET` | Product-image storage |
| `HUGGINGFACE_API_KEY` | AI recommendation provider |
| `CORS_ALLOWED_ORIGINS` | Allowed frontend origins (optional) |

The default database URL is `jdbc:mysql://localhost:3306/e_commerce`. Flyway creates and migrates the schema at startup.

For the frontend, copy `frontend/.env.example` to `frontend/.env` and adjust it if needed:

```env
# Empty uses the Vite /api proxy for local development.
VITE_API_BASE_URL=
VITE_PROXY_TARGET=http://localhost:8080
```

## Run locally

Start MySQL, Redis, and RabbitMQ, configure the environment variables, then run the backend:

```powershell
.\mvnw.cmd spring-boot:run
```

The API runs at `http://localhost:8080/api`.

In a separate terminal, start the frontend:

```powershell
cd frontend
npm install
npm run dev
```

Open the Vite URL shown in the terminal, normally `http://localhost:5173`.

## Build and test

```powershell
# Backend tests and package
.\mvnw.cmd test
.\mvnw.cmd package

# Frontend production build
cd frontend
npm run build
```

## API overview

All application endpoints are under the `/api` context path. Authenticated calls use the token returned by login with the `Authorization: Token <jwt>` header.

| Area | Base path |
| --- | --- |
| Authentication | `/auth` |
| Public products and categories | `/product`, `/category` |
| Cart, orders, and payments | `/cart`, `/orders`, `/payment` |
| Notifications and support chat | `/notifications`, `/chat` |
| Recommendations | `/recommendations` |
| Administration | `/admin-products`, `/admin-categories`, `/admin-orders`, `/admin` |
| Super administration | `/super-admin` |

Interactive API documentation is available while the backend is running at [Swagger UI](http://localhost:8080/api/swagger-ui.html).

## Recommendations

`GET /api/recommendations` is available to authenticated users. The service compares a customer's purchased products against active catalog products and returns up to five product IDs with a short reason. The frontend resolves these IDs into product cards in a **Recommended for You** section. For new customers or an unavailable AI provider, it returns recently added popular products instead.

## Notes

- Do not commit `.env` files, credentials, or API keys.
- Product image uploads use the configured S3 bucket.
- The database migrations are located in `src/main/resources/db/migration`.
