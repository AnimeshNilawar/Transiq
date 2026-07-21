
<p align="center">
  <img src="https://img.shields.io/badge/Java-21-%23ED8B00?logo=openjdk" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring_Boot-4.1-%236DB33F?logo=springboot" alt="Spring Boot 4.1"/>
  <img src="https://img.shields.io/badge/React-19-%2361DAFB?logo=react" alt="React 19"/>
  <img src="https://img.shields.io/badge/PostgreSQL-16-%234169E1?logo=postgresql" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Vite-8-%23646CFF?logo=vite" alt="Vite 8"/>
  <img src="https://img.shields.io/badge/license-MIT-green" alt="MIT License"/>
</p>

<h1 align="center">Transiq — Cloud-Native Payment Infrastructure</h1>

<p align="center">
  <strong>A production-grade, cloud-native payment gateway built from the ground up with Java 21, Spring Boot 4.1, and React 19.</strong>
  <br/>
  Features full merchant onboarding, JWT/API-key dual authentication, a pluggable gateway routing engine,
  double-entry ledger accounting, event-driven webhook delivery with exponential retry, and a platform admin console with real-time analytics.
</p>

---

## Table of Contents

- [Why Transiq?](#why-transiq)
- [Tech Stack](#tech-stack)
- [Key Features](#key-features)
- [System Architecture](#system-architecture)
  - [Authentication & Authorization](#1-authentication--authorization)
  - [Payment Processing Pipeline](#2-payment-processing-pipeline)
  - [Domain Events & Side Effects](#3-domain-events--side-effects)
  - [Ledger & Financial Accounting](#4-ledger--financial-accounting)
  - [Admin Platform](#5-admin-platform)
- [API Overview](#api-overview)
- [Frontend Architecture](#frontend-architecture)
- [Database Schema](#database-schema)
- [Security Model](#security-model)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Design Decisions & Trade-offs](#design-decisions--trade-offs)
- [What's Next](#whats-next)

---

## Why Transiq?

Transiq was built to demonstrate **production-grade, real-world engineering practices** in building a financial platform. It is **not** a toy project or a CRUD demo — it simulates the complexity of a real payment gateway (think Stripe, Razorpay, or Adyen) with:

- **Pluggable gateway architecture** — routing, acquiring banks, card networks, and issuer authorization are all interface-driven and independently swappable
- **Dual authentication model** — session-based JWT for dashboard users, API-key-based (BCrypt-hashed) for machine-to-machine integration
- **Event-driven side effects** — domain events decouple payment success from webhook dispatch and ledger recording
- **Double-entry ledger** — every financial event creates immutable, auditable entries across multiple accounts
- **Full platform admin** — super admin panel with cross-merchant CRUD, revenue/failure analytics, and alerting

---

## Tech Stack

### Backend

| Category | Technology | Purpose |
|----------|-----------|---------|
| **Language** | Java 21 (Virtual Threads ready) | Modern JVM with pattern matching, records, sealed classes |
| **Framework** | Spring Boot 4.1 + Spring MVC | Industry-standard REST framework |
| **Security** | Spring Security 6 | JWT authentication, API key auth, role-based authorization |
| **Persistence** | Spring Data JPA / Hibernate 7 | ORM with repository abstraction |
| **Database** | PostgreSQL 16 | Relational store with JSONB, UUID support |
| **Migrations** | Flyway | Version-controlled schema migrations |
| **JWT** | jjwt 0.12.7 | JSON Web Token parsing and validation |
| **Encryption** | AES (custom service) | Webhook secret storage |
| **API** | RESTful JSON | All communication via JSON over HTTP |

### Frontend

| Category | Technology | Purpose |
|----------|-----------|---------|
| **Language** | JavaScript (ES2024+) | Modern JS with optional typing via JSDoc |
| **Framework** | React 19 | Latest React with concurrent features |
| **Build** | Vite 8 | Fast HMR, optimized builds with Oxlint |
| **Routing** | React Router 7 | Declarative routing with loaders |
| **State** | TanStack React Query 5 | Server state, caching, pagination |
| **Styling** | Tailwind CSS 4 | Utility-first CSS with design tokens |
| **Charts** | Recharts 3 | Declarative chart components |
| **Validation** | Zod 4 | Runtime schema validation |
| **HTTP** | Axios | Interceptor-based HTTP client |
| **Icons** | Lucide React | Consistent icon set |
| **Toasts** | Sonner | Lightweight toast notifications |
| **Utils** | date-fns, clsx, tailwind-merge | Date formatting, class merging |

---

## Key Features

### Merchant API (API-Key Authenticated)

- **Payment lifecycle**: Create → Confirm → Retry → Webhook notification
- **Refunds**: Full and partial refunds with idempotency
- **Settlements**: Initiate payout of settled balance
- **Chargebacks**: Dispute lifecycle management
- **Adjustments**: Manual credit/debit corrections
- **Ledger**: Immutable double-entry balance inquiry
- **Webhooks**: Create/manage endpoints, deliveries, retries, event replay
- **API Keys**: Manage SECRET, PUBLISHABLE, and RESTRICTED keys with scope-based access

### Merchant Dashboard (JWT Authenticated)

- Real-time payment list with status filtering and date range
- Refund creation with reason classification
- Settlement initiation with history
- Ledger balance + paginated entry log
- Webhook endpoint CRUD + delivery monitoring
- API key management (create, revoke, rotate)
- Team user management (invite, role assignment, removal)
- Merchant profile settings

### Platform Admin (PLATFORM_ADMIN Role)

- Cross-merchant dashboard with aggregate metrics
- Merchant management (detail view with users, API keys, volume)
- Payment management with status override capability
- Refund, settlement, user, and API key cross-merchant views
- Webhook delivery monitoring and manual retry
- **Analytics**: 30-day revenue time-series + failure trend charts
- **Alerts**: High failure rate detection, stalled webhook delivery warnings
- **CSV export** on all list pages
- **Pagination** on all list views with configurable page size

---

## System Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                            CLOUD EDGE                                    │
│  React SPA (Port 5173)        Public Checkout (Port 5173)               │
└───────────────────────────────┬─────────────────────────────────────────┘
                                │ HTTPS / JSON
┌───────────────────────────────▼─────────────────────────────────────────┐
│                         SPRING BOOT API (Port 8080)                      │
│                                                                          │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐      │
│  │   Auth Chain 0    │  │   Auth Chain 1   │  │   Auth Chain 2   │      │
│  │  (Public: health, │  │  (JWT optional:  │  │  (JWT required:  │      │
│  │   register)       │  │   auth/*)        │  │   admin/*)       │      │
│  └────────┬─────────┘  └────────┬─────────┘  └────────┬─────────┘      │
│           │                     │                     │                  │
│  ┌────────▼─────────────────────▼─────────────────────▼──────────┐      │
│  │                     Security Filter Chain (Ordered)             │      │
│  │  ┌──────────────┐  ┌────────────────┐  ┌──────────────────┐   │      │
│  │  │RateLimiting  │  │JwtAuthFilter   │  │ApiKeyAuthFilter  │   │      │
│  │  │   Filter     │  │(Bearer token)  │  │(Prefix+Hash)     │   │      │
│  │  └──────────────┘  └────────────────┘  └──────────────────┘   │      │
│  └───────────────────────────────────────────────────────────────┘      │
│                                                                          │
│  Chain 3: /api/v1/payments|refunds|... → API Key (RateLimit + ApiKey + Scope)  │
│  Chain 4: /api/v1/dashboard/** → JWT required                          │
│  Chain 5: /** (catch-all) → JWT required                                │
└─────────────────────────────────────────────────────────────────────────┘
```

### 1. Authentication & Authorization

Transiq uses a **dual authentication model** served by **six ordered security filter chains**:

| Order | Matcher | Auth Method | Purpose |
|-------|---------|-------------|---------|
| **0** | `/actuator/**`, `/api/v1/merchants/register` | None | Health checks, public registration |
| **1** | `/api/v1/auth/**` | JWT optional | Login/register (public), JWT filter for token still applied |
| **2** | `/api/v1/admin/**` | JWT required | Platform admin — `@PreAuthorize("hasRole('PLATFORM_ADMIN')")` |
| **3** | API-Key-protected paths (`/payments`, `/refunds`, `/settlements`, `/webhooks`, `/chargebacks`, `/adjustments`, `/ledger`) | API Key required | Merchant-to-API machine communication + scope filter |
| **4** | `/api/v1/dashboard/**` | JWT required | Merchant dashboard UI |
| **5** | `/**` (default) | JWT required | Catch-all for unclassified routes |

**JWT Authentication:**
- `JwtAuthenticationFilter` extracts `Bearer` token from `Authorization` header
- `JwtService` parses and validates the JWT using HMAC-SHA
- `CustomUserDetailsService` loads the `MerchantUser` entity with roles
- Builds `AuthenticatedUser` principal with role: `ROLE_OWNER`, `ROLE_ADMIN`, `ROLE_DEVELOPER`, `ROLE_FINANCE`, or `ROLE_PLATFORM_ADMIN`
- **User enable check**: disabled users are rejected both at login (no JWT issued) and on every request (`isEnabled()` verified in the filter)

**API Key Authentication:**
- `ApiKeyAuthenticationFilter` extracts key from `Authorization` header (with or without `Bearer` prefix)
- `ApiKeyAuthenticationService` looks up key prefix in DB, compares BCrypt hash
- Builds `ApiKeyPrincipal` with `merchantId`, `keyType`, `environment`, `keyId`
- `ApiKeyScopeFilter` enforces scope based on key type:

| Key Type | Allowed |
|----------|---------|
| `SECRET` | Full read/write access |
| `PUBLISHABLE` | `POST /payments` + `GET /payments/{ref}` |
| `RESTRICTED` | GET-only (read-only) |

### 2. Payment Processing Pipeline

The payment flow is a **multi-stage pipeline** that demonstrates layered architecture and the Strategy pattern:

```
┌─────────────────────────────────────────────────────────────────────────┐
│  Stage 1: Payment Intent Creation                                        │
│                                                                          │
│  POST /api/v1/payments (Idempotency-Key header)                          │
│    → PaymentService.createPayment()                                      │
│    → Generate paymentReference (unique) + clientSecret (opaque)         │
│    → Store clientSecretHash (BCrypt) for later verification              │
│    → Status: CREATED → Returns clientSecret to frontend                  │
└─────────────────────────────────┬───────────────────────────────────────┘
                                  │
┌─────────────────────────────────▼───────────────────────────────────────┐
│  Stage 2: Payment Confirmation                                           │
│                                                                          │
│  POST /api/v1/payments/{ref}/confirm                                     │
│    → 1. Verify clientSecret against stored hash                          │
│    → 2. Create PaymentAttempt (attempt_number, status=CREATED)           │
│    → 3. RoutingEngine.chooseGateway(payment)                             │
│         ↓                                                                │
│         ┌──────────────────────────────────────────────────┐             │
│         │  Routing Engine (Strategy Pattern)               │             │
│         │                                                  │             │
│         │  DefaultRoutingEngine                            │             │
│         │    ├── BIN Resolver → CardMetadata (network,     │             │
│         │    │                   issuer, bank code)        │             │
│         │    ├── NetworkRegistry → PaymentNetwork          │             │
│         │    │   (Visa / Mastercard / RuPay)               │             │
│         │    └── AcquiringRegistry → AcquiringBank         │             │
│         │        (HDFC / ICICI / SBI / AXIS / Kotak)      │             │
│         │                                                  │             │
│         │  Output: RoutingDecision { network, acquirer,    │             │
│         │           issuerBank, reason }                   │             │
│         └──────────────────────────────────────────────────┘             │
│         ↓                                                                │
│    → 4. GatewayAuthorizationService.authorize(request)                   │
│         ↓                                                                │
│         ┌──────────────────────────────────────────────────┐             │
│         │  Authorization Layer                              │             │
│         │                                                  │             │
│         │  AuthorizationSimulator (Client simulation)      │             │
│         │    ├── CardNetwork.authorize()                    │             │
│         │    ├── AcquiringBank.authorize()                  │             │
│         │    └── BankDecisionEngine (probability engine)    │             │
│         │         Configurable approval rates per bank      │             │
│         └──────────────────────────────────────────────────┘             │
│         ↓                                                                │
│    → 5. On success:                                                      │
│         ├── Payment → SUCCEEDED, record CardPaymentDetails               │
│         ├── PaymentAttempt → SUCCEEDED, processing_time_ms               │
│         └── Publish PaymentSucceededEvent                                │
│    → 6. On failure:                                                      │
│         ├── Payment → FAILED                                              │
│         ├── PaymentAttempt → FAILED, failure_code/reason                 │
│         └── (PaymentFailedEvent ready for webhook)                      │
└─────────────────────────────────┬───────────────────────────────────────┘
                                  │
┌─────────────────────────────────▼───────────────────────────────────────┐
│  Stage 3: Expiration & Retry                                             │
│                                                                          │
│  - PaymentExpirationService (scheduled/on-demand)                        │
│    → Transitions CREATED/REQUIRES_PAYMENT_METHOD → EXPIRED              │
│  - POST /api/v1/payments/{ref}/retry                                    │
│    → New PaymentAttempt (attempt_number incremented)                    │
│    → Re-runs the same confirmation pipeline                             │
└─────────────────────────────────────────────────────────────────────────┘
```

### 3. Domain Events & Side Effects

Payments don't exist in isolation — every state transition triggers **domain events** that propagate through an event-driven architecture:

```
PaymentSucceededEvent (published via ApplicationEventPublisher)
  │
  ├──→ PaymentEventListener (@TransactionalEventListener, sync)
  │     ├── FinancialEventService.create(PAYMENT, reference)
  │     └── LedgerService.recordSuccessfulPayment()
  │           ├── CREDIT → CUSTOMER_RECEIVABLE  (we collected from customer)
  │           └── DEBIT  → MERCHANT_PAYABLE     (we owe the merchant)
  │
  └──→ WebhookEventListener (@TransactionalEventListener, AFTER_COMMIT)
        ├── WebhookEventService.createEvent(type="payment.succeeded")
        ├── WebhookEndpointRepository.findAllActive(merchantId)
        └── For each active endpoint:
              ├── WebhookDeliveryService.createDelivery(status=PENDING)
              └── WebhookDeliveryExecutor.execute()
                    ├── HttpWebhookSender.send(url, payload, HMAC-SHA256 sig)
                    ├── 2xx → delivery.status = DELIVERED
                    └── Failure → delivery.status = FAILED, retry backoff

Automated Retry:
  └── ExponentialWebhookRetryPolicy
        → nextRetryAt = now + (retryCount^2 * 60s) (exponential backoff)
  └── WebhookRetryScheduler (@Scheduled)
        → Picks up FAILED/PENDING deliveries past nextRetryAt
        → Re-executes delivery
```

**All domain events:**
| Event | Published When | Side Effects |
|-------|---------------|--------------|
| `PaymentSucceededEvent` | Payment confirmed | Ledger entry + webhook delivery |
| `RefundSucceededEvent` | Refund processed | Ledger entry + webhook delivery |
| `SettlementCompletedEvent` | Settlement completed | Ledger entry + webhook delivery |
| `ChargebackCreatedEvent` | Chargeback raised | Webhook delivery |

### 4. Ledger & Financial Accounting

Transiq implements a **double-entry accounting system** — every financial transaction creates two offsetting entries:

| Entry Type | Debit Account | Credit Account |
|------------|--------------|----------------|
| Payment | CUSTOMER_RECEIVABLE | MERCHANT_PAYABLE |
| Refund | MERCHANT_PAYABLE | CUSTOMER_RECEIVABLE |
| Settlement | MERCHANT_PAYABLE | SETTLEMENT_ACCOUNT |
| Adjustment (credit) | PLATFORM_REVENUE | MERCHANT_PAYABLE |
| Adjustment (debit) | MERCHANT_PAYABLE | PLATFORM_REVENUE |

**Account types:**
- `CUSTOMER_RECEIVABLE` — Money collected from customers
- `MERCHANT_PAYABLE` — Money owed to the merchant
- `PLATFORM_REVENUE` — Platform fee income
- `TAX_PAYABLE` — Tax collected
- `SETTLEMENT_ACCOUNT` — Settled/payout funds

The `MerchantBalanceCalculator` computes real-time available balance as:
```
available_balance = SUM(MERCHANT_PAYABLE credits) - SUM(MERCHANT_PAYABLE debits)
```

### 5. Admin Platform

The admin platform is a **full super-admin console** with:

```
┌─────────────────────────────────────────────────────────────┐
│                  Admin Dashboard                              │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │Merchants │  │ Payments │  │  Volume  │  │   Rate   │   │
│  │   142    │  │  12,847  │  │ ₹2.4Cr  │  │  97.3%  │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
│  Recent Payments Table                                      │
└─────────────────────────────────────────────────────────────┘

Admin CRUD Screens:
  ├── Merchants (list + detail with users, API keys, volume)
  ├── Payments (list + detail + status override)
  ├── Refunds (cross-merchant view)
  ├── Settlements (list + manual creation per merchant)
  ├── Users (list + enable/disable)
  ├── API Keys (list + revoke)
  └── Webhook Deliveries (list + retry)

Admin Analytics:
  ├── Revenue Trend (30-day time-series AreaChart)
  ├── Failure Trends (30-day bar chart: success vs failure)
  └── Alerts (high failure rate >30%, stalled deliveries)

All list views: Pagination + CSV Export
```

---

## API Overview

All endpoints are under `/api/v1`. Full reference in the [API docs](./docs/api.md).

### Public
| Method | Path | Description |
|--------|------|-------------|
| POST | `/auth/register` | Register new merchant account |
| POST | `/auth/login` | Login, returns JWT |
| POST | `/merchants/register` | Register merchant entity |

### Merchant API (API Key)
| Method | Path | Description |
|--------|------|-------------|
| POST | `/payments` | Create payment intent |
| POST | `/payments/{ref}/confirm` | Confirm payment with details |
| POST | `/payments/{ref}/retry` | Retry failed payment |
| GET | `/payments/{ref}` | Retrieve payment |
| POST | `/refunds/{paymentRef}` | Create refund |
| POST | `/settlements` | Initiate settlement |
| POST | `/webhooks` | Register webhook endpoint |
| POST | `/webhooks/events/{id}/replay` | Replay webhook event |
| GET | `/ledger/balance` | Get account balance |

### Dashboard (JWT)
| Method | Path | Description |
|--------|------|-------------|
| GET | `/dashboard/me` | Current user + merchant info |
| GET | `/dashboard/payments` | Paginated payment list |
| GET | `/dashboard/refunds` | Paginated refund list |
| POST | `/dashboard/refunds` | Create refund |
| GET | `/dashboard/settlements` | Paginated settlement list |
| POST | `/dashboard/settlements` | Create settlement |
| GET | `/dashboard/ledger/balance` | Balance |
| GET | `/dashboard/ledger/entries` | Paginated entries |
| GET | `/dashboard/webhooks` | List webhook endpoints |
| POST | `/dashboard/webhooks` | Create endpoint |
| DELETE | `/dashboard/webhooks/{id}` | Delete endpoint |
| GET | `/dashboard/webhooks/deliveries` | Paginated deliveries |
| POST | `/dashboard/webhooks/deliveries/{id}/retry` | Retry delivery |
| GET | `/dashboard/users` | Team members |
| POST | `/dashboard/users/invite` | Invite user |
| GET | `/api-keys` | API keys |
| POST | `/api-keys` | Create key |
| DELETE | `/api-keys/{id}` | Revoke key |
| POST | `/api-keys/{id}/rotate` | Rotate key |

### Admin (JWT + PLATFORM_ADMIN)
| Method | Path | Description |
|--------|------|-------------|
| GET | `/admin/dashboard` | Aggregate metrics |
| GET | `/admin/merchants` | Paginated merchant list |
| GET | `/admin/merchants/{id}` | Merchant detail |
| GET | `/admin/payments` | Paginated payments |
| PATCH | `/admin/payments/{ref}/status` | Override status |
| GET | `/admin/refunds` | Paginated refunds |
| GET | `/admin/settlements` | Paginated settlements |
| POST | `/admin/settlements` | Create settlement for merchant |
| GET | `/admin/users` | Paginated users |
| PATCH | `/admin/users/{id}/status` | Enable/disable user |
| GET | `/admin/api-keys` | Paginated API keys |
| DELETE | `/admin/api-keys/{id}` | Revoke key |
| GET | `/admin/webhook-deliveries` | Paginated deliveries |
| POST | `/admin/webhook-deliveries/{id}/retry` | Retry delivery |
| GET | `/admin/analytics/revenue` | 30-day revenue (native SQL) |
| GET | `/admin/analytics/failure-trends` | 30-day failure trends (native SQL) |
| GET | `/admin/alerts` | System alerts |

---

## Frontend Architecture

### Design System

Custom design tokens defined as CSS custom properties:

```css
:root {
  --color-background: #ffffff;
  --color-foreground: #0a0a0a;
  --color-primary: #171717;
  --color-muted: #f5f5f5;
  --color-chart-1: #2563eb;
  --color-chart-2: #10b981;
  --color-chart-3: #f59e0b;
  --color-chart-4: #ef4444;
  --color-chart-5: #8b5cf6;
}
```

Dark mode via `.dark` class toggle — all components use theme-aware variables.

### State Management Strategy

| Concern | Solution | Rationale |
|---------|----------|-----------|
| Server state | TanStack React Query 5 | Automatic caching, background refetch, paginated queries, optimistic updates |
| Auth state | `localStorage` (JWT) + React context | Simple, persisted across sessions |
| API key (checkout) | `sessionStorage` | Ephemeral — cleared on tab close |
| Navigation | React Router 7 | URL-driven, supports loaders/actions |
| Notifications | Sonner toasts | Global error/success feedback from Axios interceptors |
| Forms | React Hook Form + Zod | Performant, validated, type-safe form state |

### API Layer

Two Axios instances with distinct behaviors:

- **`jwtClient`**: Attaches `Bearer <token>` from localStorage; on 401 → redirects to `/login`; globally toasts errors
- **`apiKeyClient`**: Attaches API key from sessionStorage (raw or Bearer); globally toasts errors

### Component Tree (Feature Modules)

```
src/features/
├── auth/          Login, Register
├── dashboard/     Overview with balance trend chart (Recharts AreaChart)
├── payments/      List, Detail, Retry
├── refunds/       List, Create, Detail
├── settlements/   List, Create, Detail
├── ledger/        Balance card + paginated entries
├── webhooks/      Endpoint CRUD, Delivery list/detail, Retry, Replay
├── api-keys/      List, Create, Rotate, Revoke
├── admin/         10 pages: Dashboard, Merchants, Payments, Refunds,
│                  Settlements, Users, API Keys, Webhook Deliveries
├── checkout/      Public payment form (no auth, uses apiKeyClient)
└── settings/      Profile & merchant settings
```

Shared components: `Layout`, `Sidebar`, `Pagination`, `DataTable`, `StatusBadge`, `PageHeader`, `ClientOnly`, `AppLayout` (admin layout).

---

## Database Schema

16 core entities, all extending `BaseEntity` (UUID `id`, `Instant createdAt`, `Instant updatedAt`).

### Entity Relationship Diagram (Text)

```
merchants (1) ────── (N) merchant_users
merchants (1) ────── (N) api_keys
merchants (1) ────── (N) payments
merchants (1) ────── (N) refunds
merchants (1) ────── (N) settlements
merchants (1) ────── (N) chargebacks
merchants (1) ────── (N) adjustments
merchants (1) ────── (N) financial_events
merchants (1) ────── (N) ledger_entries
merchants (1) ────── (N) webhook_endpoints
merchants (1) ────── (N) webhook_events

payments (1) ────── (1) card_payment_details
payments (1) ────── (1) upi_payment_details
payments (1) ────── (N) payment_attempts
payments (1) ────── (N) refunds
payments (1) ────── (N) chargebacks

financial_events (1) ────── (N) ledger_entries
webhook_events   (1) ────── (N) webhook_deliveries
webhook_endpoints (1) ───── (N) webhook_deliveries
```

---

## Security Model

### Six Security Filter Chains

| Order | Pattern | Auth | Filters |
|-------|---------|------|---------|
| 0 | `/actuator/**`, `/merchants/register` | None | — |
| 1 | `/auth/**` | JWT optional | `JwtAuthenticationFilter` |
| 2 | `/admin/**` | JWT + Role | `RateLimitingFilter`, `JwtAuthenticationFilter`, `@PreAuthorize` |
| 3 | `/payments`, `/refunds`, `/settlements`, `/webhooks`, `/chargebacks`, `/adjustments`, `/ledger` | API Key | `RateLimitingFilter`, `ApiKeyAuthenticationFilter`, `ApiKeyScopeFilter` |
| 4 | `/dashboard/**` | JWT | `RateLimitingFilter`, `JwtAuthenticationFilter` |
| 5 | `/**` | JWT | `RateLimitingFilter`, `JwtAuthenticationFilter` |

### User Disable Protection (Three Layers)

1. **Login**: `AuthService.login()` checks `MerchantUser.isEnabled()` — disabled users never receive a JWT
2. **Per-request**: `JwtAuthenticationFilter.doFilterInternal()` checks `AuthenticatedUser.isEnabled()` — existing tokens are rejected even if issued before disable
3. **Admin**: `PATCH /admin/users/{id}/status` allows super admins to toggle `enabled`

### CORS

Configurable via `cors.allowed-origins` environment variable (default: `http://localhost:5173`).

---

## Getting Started

### Prerequisites

- **Java 21+** (GraalVM or OpenJDK)
- **Node.js 20+**
- **PostgreSQL 16+**
- **Maven 3.9+**

### 1. Clone & Configure

```bash
git clone https://github.com/your-org/transiq.git
cd transiq
```

### 2. Database Setup

```bash
createdb transiq

# Or via psql:
psql -U postgres -c "CREATE DATABASE transiq;"
```

### 3. Backend

```bash
cd Backend

# Configure environment
$env:DB_HOST="localhost"
$env:DB_PORT="5432"
$env:DB_NAME="transiq"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="postgres"
$env:CORS_ALLOWED_ORIGINS="http://localhost:5173"

# Build & run
mvn spring-boot:run
```

The server starts on `http://localhost:8080`. On first boot, the `AdminDataInitializer` creates:
- Platform merchant: `platform@transiq.com`
- Platform admin: `admin@transiq.com` / `admin123` (role: `PLATFORM_ADMIN`)

### 4. Frontend

```bash
cd Frontend

# Configure environment
$env:VITE_API_BASE_URL="http://localhost:8080/api/v1"

# Install & run
npm install
npm run dev
```

The app runs on `http://localhost:5173`.

### 5. Verify

```bash
# Health check
curl http://localhost:8080/actuator/health

# Login as admin
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@transiq.com","password":"admin123"}'
```

---

## Project Structure

```
transiq/
├── Backend/
│   ├── pom.xml
│   └── src/main/java/com/moddynerd/transiq/
│       ├── TransiqApplication.java
│       ├── admin/          → AdminController, AdminService, DTOs, AdminDataInitializer
│       ├── apikey/         → ApiKey entity, filters, service, controller
│       ├── auth/           → JWT auth: controller, service, entity, filter
│       ├── config/         → SecurityConfig (6 chains), CORS, RateLimitingFilter
│       ├── dashboard/      → Merchant dashboard controller, service, specs
│       ├── event/          → Domain events, publishers, listeners
│       ├── merchant/       → Merchant entity, service, controller
│       ├── payment/        → Payment + CardPaymentDetails + UpiPaymentDetails entities
│       │   ├── attempt/    → PaymentAttempt entity, service
│       │   ├── refund/     → Refund entity, service, controller
│       │   ├── settlement/ → Settlement entity, service, controller
│       │   ├── chargeback/ → Chargeback entity, service, controller
│       │   ├── adjustment/ → Adjustment entity, service, controller
│       │   ├── ledger/     → LedgerEntry, LedgerAccount, balance calculator
│       │   ├── financialEvent/ → FinancialEvent entity, service
│       │   ├── gateway/    → RoutingEngine, banks, networks, authorization
│       │   ├── security/   → ClientSecretService
│       │   ├── expiration/ → PaymentExpirationService
│       │   └── dto/        → Request/response DTOs
│       ├── shared/         → BaseEntity, GlobalExceptionHandler, encryption
│       └── webhook/        → Webhook entities, dispatcher, sender, retry, controller
│
├── Frontend/
│   ├── package.json
│   ├── index.html
│   └── src/
│       ├── main.jsx        → Entry point, QueryClientProvider + Router
│       ├── App.jsx         → Route definitions
│       ├── index.css       → Design system + Tailwind
│       ├── api/            → Axios clients + endpoint modules
│       ├── features/       → 11 feature modules (auth, dashboard, payments, etc.)
│       ├── components/     → Shared UI components
│       ├── hooks/          → Custom React hooks
│       ├── lib/            → Utilities (csv.js, cn.js)
│       └── routes/         → Route configuration
│
└── README.md               ← You are here
```

---

## Design Decisions & Trade-offs

### Why API Key + JWT instead of OAuth2?

OAuth2 adds significant complexity (authorization server, scopes, refresh tokens, client registration) that isn't justified for a two-audience system (dashboard users + API clients). The dual model is simpler while remaining secure: long-lived BCrypt-hashed API keys for machines, short-lived JWTs for humans.

### Why UUID primary keys?

UUIDs prevent enumeration attacks, simplify distributed data migrations, and eliminate auto-increment contention. The trade-off: 16-byte keys vs 4-byte integers, and slightly larger indexes. For a payment platform where security matters, this is the right call.

### Why double-entry ledger?

Single-entry accounting is simpler but impossible to audit. Double-entry means every transaction creates two offsetting entries — debit one account, credit another. This is the standard for financial systems because it makes the books balance by construction.

### Why `@TransactionalEventListener(phase = AFTER_COMMIT)` for webhooks?

Webhook delivery should only happen after the database transaction commits. If the transaction rolls back, the payment wasn't actually successful, and we shouldn't notify the merchant. This prevents phantom webhooks.

### Why `open-in-view = false`?

Spring's OSIV keeps the Hibernate session open through the entire request, which can lead to lazy initialization exceptions being masked or long-running sessions. Setting it to `false` forces explicit transaction boundaries and better performance. The trade-off is more careful coding around lazy-loaded relationships.

### Why a simulated gateway engine?

A real gateway requires downstream bank integrations. The simulation layer (`AuthorizationSimulator`, `BankDecisionEngine`) mimics real-world behavior with configurable approval rates, network-specific rules, and bank-specific authorization logic. This makes the system production-ready when real integrations are plugged in.

---

## What's Next

- [ ] **Landing page** — Public marketing site with feature showcase, pricing, docs links
- [ ] **Developer documentation site** — Interactive API reference, integration guides, SDK docs
- [ ] **Payouts system** — Automated merchant bank payouts with reconciliation
- [ ] **Subscriptions / recurring billing** — Payment plans, invoice generation, dunning
- [ ] **Webhook management UI** — In-app webhook log viewer with search and filtering
- [ ] **Rate limiting refinement** — Per-merchant tier-based rate limits with Redis-backed counters
- [ ] **TypeScript migration** — Frontend migration from JSDoc to full TypeScript
- [ ] **API versioning** — Proper versioning strategy (URL or header-based)
- [ ] **Observability** — Structured logging, metrics (Micrometer/Prometheus), distributed tracing
- [ ] **CI/CD** — GitHub Actions for build, test, lint, and deploy

---

<p align="center">
  <sub>Built with Java, React, and a lot of coffee.</sub>
</p>
