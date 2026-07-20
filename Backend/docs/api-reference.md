# Transiq API Reference

Base URL: `http://localhost:8080/api/v1`

- [Authentication](#authentication)
- [Merchant Management](#merchant-management)
- [API Keys](#api-keys)
- [Payments](#payments)
- [Refunds](#refunds)
- [Settlements](#settlements)
- [Ledger](#ledger)
- [Webhook Endpoints](#webhook-endpoints)
- [Webhook Deliveries](#webhook-deliveries)
- [Webhook Events & Replay](#webhook-events--replay)
- [Common Error Responses](#common-error-responses)

---

## Authentication

### POST /auth/register

Register a new merchant and admin user.

**Request body:**

```json
{
  "businessName": "Acme Corp",
  "businessEmail": "billing@acme.com",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@acme.com",
  "password": "securePassword123"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `businessName` | string | yes | Merchant business name |
| `businessEmail` | string | yes | Merchant billing email (must be valid email) |
| `firstName` | string | no | Admin user first name |
| `lastName` | string | no | Admin user last name |
| `email` | string | no | Admin user login email |
| `password` | string | no | Admin user password |

**Response:** `201 Created` — no body.

**Errors:**

| Status | Condition |
|--------|-----------|
| `409 Conflict` | Business email already registered |
| `400 Bad Request` | Validation failure (missing/invalid fields) |

---

### POST /auth/login

Authenticate a merchant user and receive a JWT.

**Request body:**

```json
{
  "email": "john@acme.com",
  "password": "securePassword123"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `email` | string | yes | User email |
| `password` | string | yes | User password |

**Response:** `200 OK`

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer"
}
```

| Field | Type | Description |
|-------|------|-------------|
| `accessToken` | string | JWT token for authorization |
| `tokenType` | string | Always `Bearer` |

**Errors:**

| Status | Condition |
|--------|-----------|
| `401 Unauthorized` | Invalid email or password |

---

## Merchant Management

All endpoints require JWT authentication (`Authorization: Bearer <token>`).

### POST /merchants/register

Register a new merchant entity (dashboard-only, distinct from `/auth/register`).

**Request body:**

```json
{
  "businessName": "Acme Corp",
  "businessEmail": "billing@acme.com"
}
```

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| `businessName` | string | yes | `@NotBlank` |
| `businessEmail` | string | yes | `@NotBlank`, valid `@Email` format |

**Response:** `201 Created`

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "businessName": "Acme Corp",
  "businessEmail": "billing@acme.com",
  "status": "ACTIVE",
  "createdAt": "2026-07-20T10:30:00Z"
}
```

| Field | Type | Description |
|-------|------|-------------|
| `id` | UUID | Merchant ID |
| `businessName` | string | Business name |
| `businessEmail` | string | Business email |
| `status` | enum | `ACTIVE`, `INACTIVE`, `SUSPENDED` |
| `createdAt` | ISO-8601 | Creation timestamp |

---

## API Keys

All endpoints require JWT authentication.

### GET /api-keys

List all API keys for the authenticated merchant.

**Response:** `200 OK`

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Production Secret Key",
    "prefix": "sk_live_abc1",
    "environment": "LIVE",
    "type": "SECRET",
    "status": "ACTIVE",
    "lastUsedAt": "2026-07-20T10:30:00Z",
    "createdAt": "2026-07-01T08:00:00Z"
  }
]
```

| Field | Type | Description |
|-------|------|-------------|
| `id` | UUID | Key ID |
| `name` | string | Human-readable label |
| `prefix` | string | First 16 chars of the key |
| `environment` | enum | `TEST`, `LIVE` |
| `type` | enum | `SECRET`, `PUBLISHABLE`, `RESTRICTED` |
| `status` | enum | `ACTIVE`, `REVOKED` |
| `lastUsedAt` | ISO-8601 or null | Last usage timestamp |
| `createdAt` | ISO-8601 | Creation timestamp |

---

### POST /api-keys

Create a new API key.

**Request body:**

```json
{
  "name": "Production Secret Key",
  "environment": "LIVE",
  "type": "SECRET"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `name` | string | yes | Human-readable label |
| `environment` | enum | yes | `TEST` or `LIVE` |
| `type` | enum | yes | `SECRET`, `PUBLISHABLE`, or `RESTRICTED` |

**Response:** `201 Created`

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "apiKey": "sk_live_abc123...<full_key>",
  "prefix": "sk_live_abc1",
  "createdAt": "2026-07-20T10:30:00Z"
}
```

> **Note:** The full `apiKey` value is returned only at creation time. It cannot be retrieved later. Store it securely.

| Field | Type | Description |
|-------|------|-------------|
| `id` | UUID | Key ID |
| `apiKey` | string | Full API key (returned once only) |
| `prefix` | string | First 16 chars of the key |
| `createdAt` | ISO-8601 | Creation timestamp |

**Rate limit:** Maximum 3 keys per merchant per environment+type combination.

---

### DELETE /api-keys/{id}

Revoke an API key.

**Path parameter:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | UUID | Key ID |

**Response:** `204 No Content`

---

### POST /api-keys/{id}/rotate

Rotate an API key (revoke old, generate new).

**Path parameter:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | UUID | Key ID |

**Response:** `200 OK` — returns same shape as creation response, with new `apiKey`.

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "apiKey": "pk_test_xyz789...<new_full_key>",
  "prefix": "pk_test_xyz7",
  "createdAt": "2026-07-20T11:00:00Z"
}
```

---

## Payments

All payment endpoints require **API Key authentication** (`Authorization: <api_key>`).

Header required on create:

| Header | Required | Description |
|--------|----------|-------------|
| `Idempotency-Key` | yes | Unique string for idempotent payment creation |

### POST /payments

Create a new payment intent.

**Request body:**

```json
{
  "amount": 10000,
  "currency": "INR",
  "customerEmail": "customer@example.com",
  "customerName": "Jane Smith",
  "orderId": "order-12345",
  "description": "Premium Plan - Monthly"
}
```

| Field | Type | Required | Constraints | Description |
|-------|------|----------|-------------|-------------|
| `amount` | long | yes | `@Min(1)` | Amount in smallest currency unit (paise/cents) |
| `currency` | enum | yes | — | `INR`, `USD`, `EUR` |
| `customerEmail` | string | no | `@Email` | Customer email |
| `customerName` | string | no | — | Customer name |
| `orderId` | string | yes | `@NotBlank` | Merchant's internal order ID |
| `description` | string | no | — | Payment description |

**Response:** `201 Created`

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "paymentReference": "pay_2x7a9k3m",
  "clientSecret": "cs_5f8e3a1b_secret_suffix",
  "status": "REQUIRES_PAYMENT_METHOD",
  "createdAt": "2026-07-20T10:30:00Z"
}
```

| Field | Type | Description |
|-------|------|-------------|
| `id` | UUID | Payment ID |
| `paymentReference` | string | Globally unique reference (`pay_` prefix) |
| `clientSecret` | string | Secret required for confirmation (returned once) |
| `status` | enum | Initial status: `REQUIRES_PAYMENT_METHOD` |
| `createdAt` | ISO-8601 | Creation timestamp |

**Errors:**

| Status | Condition |
|--------|-----------|
| `409 Conflict` | Duplicate `Idempotency-Key` |
| `400 Bad Request` | Validation failure |

---

### POST /payments/{paymentReference}/confirm

Confirm a payment with a payment method.

**Path parameter:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `paymentReference` | string | Payment reference (`pay_` prefix) |

**Request body:**

```json
{
  "clientSecret": "cs_5f8e3a1b_secret_suffix",
  "paymentMethodType": "CARD",
  "cardNetwork": "VISA",
  "issuerBank": "HDFC",
  "maskedCardNumber": "411111XXXXXX1111",
  "expiryMonth": 12,
  "expiryYear": 2028
}
```

| Field | Type | Required | Constraints | Description |
|-------|------|----------|-------------|-------------|
| `clientSecret` | string | yes | `@NotNull` | Client secret from create response |
| `paymentMethodType` | enum | yes | `@NotNull` | `CARD`, `UPI`, `NET_BANKING`, `WALLET`, `UNKNOWN` |
| `cardNetwork` | enum | if CARD | — | `VISA`, `MASTERCARD`, `RUPAY` |
| `issuerBank` | enum | if CARD | — | `HDFC`, `ICICI`, `SBI`, `AXIS` |
| `maskedCardNumber` | string | if CARD | — | Masked PAN (e.g. `411111XXXXXX1111`) |
| `expiryMonth` | integer | if CARD | `@Min(1)` `@Max(12)` | Expiry month (1–12) |
| `expiryYear` | integer | if CARD | `@Min(2024)` `@Max(2040)` | Expiry year |

> **Note:** `cardNetwork`, `issuerBank`, `maskedCardNumber`, `expiryMonth`, and `expiryYear` are only required when `paymentMethodType` is `CARD`. For non-CARD methods (`UPI`, `NET_BANKING`, `WALLET`), these fields are ignored.

**Response:** `200 OK`

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "paymentReference": "pay_2x7a9k3m",
  "amount": 10000,
  "currency": "INR",
  "status": "SUCCEEDED",
  "customerEmail": "customer@example.com",
  "orderId": "order-12345",
  "createdAt": "2026-07-20T10:30:00Z"
}
```

| Field | Type | Description |
|-------|------|-------------|
| `id` | UUID | Payment ID |
| `paymentReference` | string | Payment reference |
| `amount` | long | Amount in smallest currency unit |
| `currency` | enum | `INR`, `USD`, `EUR` |
| `status` | enum | `REQUIRES_PAYMENT_METHOD` → `PROCESSING` → `SUCCEEDED` or `FAILED` |
| `customerEmail` | string or null | Customer email |
| `orderId` | string | Order ID |
| `createdAt` | ISO-8601 | Creation timestamp |

**Payment statuses:** `CREATED`, `REQUIRES_PAYMENT_METHOD`, `PROCESSING`, `SUCCEEDED`, `FAILED`, `CANCELLED`, `REFUNDED`, `EXPIRED`

**Errors:**

| Status | Condition |
|--------|-----------|
| `400 Bad Request` | Invalid client secret or missing card details |
| `404 Not Found` | Payment reference not found |
| `409 Conflict` | Payment not in confirmable state |

---

### GET /payments/{paymentReference}

Retrieve payment details.

**Path parameter:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `paymentReference` | string | Payment reference (`pay_` prefix) |

**Response:** `200 OK` — same shape as confirm response.

---

### POST /payments/{paymentReference}/retry

Retry a failed payment (resets to `REQUIRES_PAYMENT_METHOD`).

**Path parameter:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `paymentReference` | string | Payment reference |

**Response:** `200 OK` — returns updated payment response.

---

## Refunds

All refund endpoints require **API Key authentication**.

Header required on create:

| Header | Required | Description |
|--------|----------|-------------|
| `Idempotency-Key` | yes | Unique string for idempotent refund creation |

### POST /refunds/{paymentReference}

Create a refund for a succeeded payment.

**Path parameter:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `paymentReference` | string | Payment reference |

**Request body:**

```json
{
  "amount": 5000,
  "reason": "REQUESTED_BY_CUSTOMER"
}
```

| Field | Type | Required | Constraints | Description |
|-------|------|----------|-------------|-------------|
| `amount` | long | yes | `@DecimalMin("0.01")` | Refund amount in smallest currency unit |
| `reason` | enum | yes | `@NotNull` | `REQUESTED_BY_CUSTOMER`, `DUPLICATE_PAYMENT`, `FRAUDULENT`, `PRODUCT_UNAVAILABLE`, `OTHER` |

**Response:** `200 OK`

```json
{
  "refundReference": "ref_3b8d2c5f",
  "amount": 5000,
  "status": "CREATED"
}
```

| Field | Type | Description |
|-------|------|-------------|
| `refundReference` | string | Globally unique refund reference (`ref_` prefix) |
| `amount` | long | Refund amount |
| `status` | string | Initial status: `CREATED` |

**Refund statuses:** `CREATED`, `PROCESSING`, `SUCCEEDED`, `FAILED`

**Errors:**

| Status | Condition |
|--------|-----------|
| `400 Bad Request` | Amount exceeds unsettled balance |
| `404 Not Found` | Payment not found |
| `409 Conflict` | Payment not in a refundable state |
| `409 Conflict` | Duplicate `Idempotency-Key` |

---

### GET /refunds

List all refunds for the authenticated merchant.

**Response:** `200 OK`

```json
[
  {
    "refundReference": "ref_3b8d2c5f",
    "paymentReference": "pay_2x7a9k3m",
    "amount": 5000,
    "status": "SUCCEEDED",
    "reason": "REQUESTED_BY_CUSTOMER",
    "createdAt": "2026-07-20T11:00:00Z"
  }
]
```

| Field | Type | Description |
|-------|------|-------------|
| `refundReference` | string | Refund reference |
| `paymentReference` | string | Original payment reference |
| `amount` | long | Refund amount |
| `status` | enum | `CREATED`, `PROCESSING`, `SUCCEEDED`, `FAILED` |
| `reason` | enum | Refund reason |
| `createdAt` | ISO-8601 | Creation timestamp |

---

### GET /refunds/{refundReference}

Retrieve a specific refund.

**Path parameter:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `refundReference` | string | Refund reference |

**Response:** `200 OK` — same shape as list response entry.

---

## Settlements

All settlement endpoints require **API Key authentication**.

### POST /settlements

Create a settlement for the merchant.

**Response:** `200 OK`

```json
{
  "settlementReference": "stl_7f9e1d4a",
  "amount": 9500,
  "currency": "INR",
  "status": "PENDING",
  "createdAt": "2026-07-20T12:00:00Z"
}
```

| Field | Type | Description |
|-------|------|-------------|
| `settlementReference` | string | Globally unique settlement reference (`stl_` prefix) |
| `amount` | long | Settlement amount in smallest currency unit |
| `currency` | string | Currency code |
| `status` | string | `PENDING` |
| `createdAt` | ISO-8601 | Creation timestamp |

**Settlement statuses:** `PENDING`, `PROCESSING`, `COMPLETED`, `FAILED`

---

### GET /settlements

List all settlements for the authenticated merchant.

**Response:** `200 OK`

```json
[
  {
    "settlementReference": "stl_7f9e1d4a",
    "amount": 9500,
    "currency": "INR",
    "status": "PENDING",
    "processedAt": null,
    "bankReference": null
  }
]
```

| Field | Type | Description |
|-------|------|-------------|
| `settlementReference` | string | Settlement reference |
| `amount` | long | Settlement amount |
| `currency` | string | Currency code |
| `status` | string | `PENDING`, `PROCESSING`, `COMPLETED`, `FAILED` |
| `processedAt` | ISO-8601 or null | Processing completion timestamp |
| `bankReference` | string or null | Bank reference number |

---

### GET /settlements/{settlementReference}

Retrieve a specific settlement.

**Path parameter:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `settlementReference` | string | Settlement reference |

**Response:** `200 OK` — same shape as list response entry.

---

## Ledger

All ledger endpoints require **API Key authentication**.

### GET /ledger/balance

Get the merchant's available balance.

**Response:** `200 OK`

```json
{
  "availableBalance": 500000,
  "currency": "INR"
}
```

| Field | Type | Description |
|-------|------|-------------|
| `availableBalance` | long | Available balance in smallest currency unit |
| `currency` | string | Currency code |

---

## Webhook Endpoints

All webhook endpoints require **API Key authentication**.

### POST /webhooks

Create a new webhook endpoint.

**Request body:**

```json
{
  "url": "https://api.acme.com/webhooks/transiq"
}
```

| Field | Type | Required | Constraints | Description |
|-------|------|----------|-------------|-------------|
| `url` | string | yes | `@NotBlank` | HTTPS URL to receive webhook payloads |

**Response:** `200 OK`

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "url": "https://api.acme.com/webhooks/transiq",
  "secret": "whsec_abc123...<full_secret>"
}
```

| Field | Type | Description |
|-------|------|-------------|
| `id` | UUID | Webhook endpoint ID |
| `url` | string | Webhook URL |
| `secret` | string | Signing secret (returned once only). Used to verify webhook signatures. |

> **Note:** The `secret` value is returned only at creation time. It cannot be retrieved later. Store it securely.

---

### GET /webhooks

List all webhook endpoints.

**Response:** `200 OK`

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "url": "https://api.acme.com/webhooks/transiq",
    "status": "ACTIVE"
  }
]
```

| Field | Type | Description |
|-------|------|-------------|
| `id` | UUID | Endpoint ID |
| `url` | string | Webhook URL |
| `status` | enum | `ACTIVE`, `DISABLED` |

---

### DELETE /webhooks/{id}

Disable a webhook endpoint (soft-delete — sets status to `DISABLED`).

**Path parameter:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | UUID | Webhook endpoint ID |

**Response:** `204 No Content`

---

## Webhook Deliveries

All delivery endpoints require **API Key authentication**.

### GET /webhooks/deliveries

Query webhook deliveries with filters and pagination.

**Query parameters (all optional):**

| Parameter | Type | Description |
|-----------|------|-------------|
| `status` | enum | Filter by delivery status: `PENDING`, `DELIVERED`, `FAILED` |
| `eventType` | enum | Filter by event type: `PAYMENT_SUCCEEDED`, `PAYMENT_FAILED`, `REFUND_SUCCEEDED`, `SETTLEMENT_COMPLETED` |
| `endpointId` | UUID | Filter by endpoint |
| `eventId` | UUID | Filter by event |
| `from` | ISO-8601 | Start date for delivery creation |
| `to` | ISO-8601 | End date for delivery creation |
| `page` | integer | Page number (0-indexed, default: 0) |
| `size` | integer | Page size (default: 20) |
| `sort` | string | Sort field and direction (e.g. `createdAt,desc`) |

**Response:** `200 OK` — Spring Data `Page` response.

```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "eventId": "660e8400-e29b-41d4-a716-446655440001",
      "endpointId": "770e8400-e29b-41d4-a716-446655440002",
      "eventReference": "pay_2x7a9k3m",
      "eventType": "PAYMENT_SUCCEEDED",
      "status": "DELIVERED",
      "attemptCount": 1,
      "httpStatus": 200,
      "failureReason": null,
      "durationMs": 342,
      "deliveredAt": "2026-07-20T10:30:01Z",
      "createdAt": "2026-07-20T10:30:00Z",
      "lastAttemptAt": "2026-07-20T10:30:01Z",
      "nextRetryAt": null
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

| Field | Type | Description |
|-------|------|-------------|
| `id` | UUID | Delivery ID |
| `eventId` | UUID | Parent webhook event ID |
| `endpointId` | UUID | Target webhook endpoint ID |
| `eventReference` | string | Reference (payment/refund/settlement reference) |
| `eventType` | enum | Event type |
| `status` | enum | `PENDING`, `DELIVERED`, `FAILED` |
| `attemptCount` | integer | Number of delivery attempts (max 5) |
| `httpStatus` | integer or null | HTTP status code from endpoint |
| `failureReason` | string or null | Error message if failed |
| `durationMs` | long or null | Request duration in milliseconds |
| `deliveredAt` | ISO-8601 or null | Successful delivery timestamp |
| `createdAt` | ISO-8601 | Delivery creation timestamp |
| `lastAttemptAt` | ISO-8601 or null | Most recent attempt timestamp |
| `nextRetryAt` | ISO-8601 or null | Next scheduled retry (null if delivered or max retries reached) |

> **Note:** The retry policy uses exponential backoff: 1m → 2m → 4m → 8m → 16m (max 5 attempts).

---

### GET /webhooks/deliveries/{id}

Retrieve a specific delivery.

**Path parameter:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | UUID | Delivery ID |

**Response:** `200 OK` — same shape as a single content entry from the list response.

---

### POST /webhooks/deliveries/{id}/retry

Manually retry a failed delivery.

**Path parameter:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | UUID | Delivery ID |

**Response:** `202 Accepted` — delivery will be retried asynchronously.

---

## Webhook Events & Replay

All endpoints require **API Key authentication**.

### POST /webhooks/events/{eventId}/replay

Replay a webhook event — creates new delivery entries for all active endpoints.

**Path parameter:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `eventId` | UUID | Webhook event ID |

**Response:** `202 Accepted` — deliveries will be created and dispatched asynchronously.

---

## Common Error Responses

All errors follow a consistent format.

```json
{
  "timestamp": "2026-07-20T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Human-readable error description",
  "path": "/api/v1/payments"
}
```

| Field | Type | Description |
|-------|------|-------------|
| `timestamp` | ISO-8601 | When the error occurred |
| `status` | integer | HTTP status code |
| `error` | string | HTTP status reason phrase |
| `message` | string | Error description |
| `path` | string | Request URI that caused the error |

### HTTP Status Codes Summary

| Status | Meaning | Common Scenarios |
|--------|---------|------------------|
| `200 OK` | Success | Resource retrieved/created |
| `201 Created` | Created | Payment, API key created |
| `202 Accepted` | Accepted | Retry/replay accepted for async processing |
| `204 No Content` | Deleted/Disabled | DELETE operations |
| `400 Bad Request` | Invalid input | Missing fields, validation errors |
| `401 Unauthorized` | Auth failure | Invalid API key, JWT, or email/password |
| `403 Forbidden` | Insufficient permissions | API key does not have required scope |
| `404 Not Found` | Resource not found | Reference or ID does not exist |
| `409 Conflict` | Conflict | Duplicate idempotency key, invalid state transition |
| `500 Internal Server Error` | Server error | Unexpected failure (generic message returned) |

### Auth Header Rules

| Endpoint | Auth Method | Header Format |
|----------|-------------|---------------|
| `/auth/*` | None (public) | — |
| Dashboard endpoints | JWT | `Authorization: Bearer <jwt>` |
| Payment/Webhook/etc. API | API Key | `Authorization: <api_key>` |
| Payment creation (`POST /payments`) | API Key + Idempotency-Key | `Idempotency-Key: <unique_string>` |
| Refund creation (`POST /refunds/{ref}`) | API Key + Idempotency-Key | `Idempotency-Key: <unique_string>` |
