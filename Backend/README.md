# Transiq Backend — Payment Gateway API

Java 21 / Spring Boot 4.1 / Hibernate 7 / PostgreSQL 16 payment infrastructure backend.

Part of the [Transiq](../README.md) payment infrastructure platform.

## Tech Stack

| Technology | Purpose |
|-----------|---------|
| **Java 21** | Virtual Threads, pattern matching, records, sealed classes |
| **Spring Boot 4.1** | Application framework |
| **Spring Security 6** | 6 ordered security filter chains |
| **Spring Data JPA / Hibernate 7** | ORM with repository abstraction |
| **PostgreSQL 16** | Relational database |
| **Flyway** | Version-controlled schema migrations |
| **jjwt 0.12.7** | JWT parsing and validation |
| **AES Encryption** | Webhook secret storage |

## Architecture Summary

```
REST API (JSON)
    ↓
Security Filter Chains (6 ordered chains)
  ├── Public: health, register
  ├── JWT optional: auth/*
  ├── JWT + ROLE: admin/* (PLATFORM_ADMIN only)
  ├── API Key: payments/*, refunds/*, settlements/*, webhooks/*
  ├── JWT: dashboard/**
  └── JWT: /** (catch-all)
    ↓
Controllers → Services → Repositories → PostgreSQL
    ↓
Domain Events → Event Listeners → Ledger + Webhooks
```

## Security Filter Chains

| Order | Pattern | Auth | Key Filter |
|-------|---------|------|------------|
| 0 | `/actuator/**`, `/merchants/register` | None | — |
| 1 | `/auth/**` | JWT optional | `JwtAuthenticationFilter` |
| 2 | `/admin/**` | JWT + Role | `JwtAuthenticationFilter` + `@PreAuthorize` |
| 3 | API paths | API Key | `ApiKeyAuthenticationFilter` + `ApiKeyScopeFilter` |
| 4 | `/dashboard/**` | JWT | `JwtAuthenticationFilter` |
| 5 | `/**` | JWT | `JwtAuthenticationFilter` |

## Key Packages

| Package | Responsibility |
|---------|---------------|
| `admin/` | Platform admin controller, service, analytics, alerts |
| `apikey/` | API key entity, auth filter, scope filter, service |
| `auth/` | JWT auth: controller, service, entity, filter, UserDetailsService |
| `config/` | SecurityConfig, CORS, RateLimitingFilter |
| `dashboard/` | Merchant dashboard controller, service, DTOs |
| `event/` | Domain event records, publishers, event listeners |
| `merchant/` | Merchant entity, service, controller |
| `payment/` | Payment + Card/UPI details entities |
| `payment/attempt/` | Payment attempt entity, retry logic |
| `payment/gateway/` | Pluggable routing engine, banks, networks, authorization |
| `payment/ledger/` | Double-entry accounting, balance calculation |
| `payment/refund/` | Refund entity, service, controller |
| `payment/settlement/` | Settlement entity, service, controller |
| `payment/chargeback/` | Chargeback entity, service, controller |
| `payment/adjustment/` | Adjustment entity, service, controller |
| `payment/financialEvent/` | Financial event entity, service |
| `payment/expiration/` | Payment expiry scheduler |
| `shared/` | BaseEntity, GlobalExceptionHandler, encryption, generators |
| `webhook/` | Webhook endpoint/event/delivery entities, dispatcher, sender, retry, controller |

## Gateway Architecture

The payment gateway simulates a real-world acquiring pipeline with pluggable interfaces:

```
RoutingEngine (interface)
  → DefaultRoutingEngine
    → BIN Resolver → CardMetadata
    → NetworkRegistry → PaymentNetwork (Visa/Mastercard/RuPay)
    → AcquiringRegistry → AcquiringBank (HDFC/ICICI/SBI/AXIS/Kotak)
    → AuthorizationSimulator → BankDecisionEngine
```

## Getting Started

```bash
# Prerequisites: Java 21+, PostgreSQL 16+, Maven 3.9+

# Configure database
$env:DB_HOST="localhost"
$env:DB_PORT="5432"
$env:DB_NAME="transiq"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="postgres"
$env:CORS_ALLOWED_ORIGINS="http://localhost:5173"

# Run
mvn spring-boot:run
```

Server starts on `http://localhost:8080`. Default admin credentials: `admin@transiq.com` / `admin123`.
