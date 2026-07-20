# Transiq — Payment Gateway

A cloud-native payment gateway built with Java 21 / Spring Boot 4.1 / Hibernate 7 / Jackson 3 / PostgreSQL.

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                         REST API                             │
│  POST /payments  POST /confirm  POST /refunds  POST /settle │
│  GET /payments   GET /refunds   GET /settle    GET /ledger  │
│  POST /webhooks  GET /deliveries  POST /replay              │
└─────────────────────────┬───────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────────┐
│                     Auth Layer                                │
│  JWT Auth (dashboard)      API Key Auth (merchant API)       │
│  Merchant CRUD             BCrypt hash + prefix lookup       │
└─────────────────────────┬───────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────────┐
│                   Payment Processor                           │
│  Idempotency check → State machine → Gateway auth → Event   │
└─────────────────────────┬───────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────────┐
│              Gateway Authorization Layer                      │
│                                                               │
│  GatewayAuthorizationService                                  │
│       ↓                                                      │
│  GatewayAuthorizationEngine (orchestrator)                    │
│       ↓                                                      │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              Routing Engine                           │   │
│  │  DefaultRoutingEngine → RoutingDecision               │   │
│  │  { acquirer, network, issuerBank, reason }            │   │
│  └──────────────────────────────────────────────────────┘   │
│       ↓                                                      │
│  ┌──────────────────────────────────────────────────────┐   │
│  │           Acquiring Bank Layer                        │   │
│  │  AcquiringRegistry → AcquiringBank.supports(network)  │   │
│  │  HDFC / ICICI / AXIS / SBI / KOTAK                    │   │
│  └──────────────────────────────────────────────────────┘   │
│       ↓                                                      │
│  ┌──────────────────────────────────────────────────────┐   │
│  │           Payment Network Layer                        │   │
│  │  NetworkRegistry → PaymentNetwork.authorize(req)      │   │
│  │  VISA / MASTERCARD / RUPAY                             │   │
│  └──────────────────────────────────────────────────────┘   │
│       ↓                                                      │
│  ┌──────────────────────────────────────────────────────┐   │
│  │           Issuer Bank Layer                            │   │
│  │  IssuerResolver → BankRegistry → Bank.authorize(req)  │   │
│  │  HDFC / ICICI / SBI / AXIS                             │   │
│  │    └─ AuthorizationSimulator                           │   │
│  │       └─ BankDecisionEngine (probability engine)       │   │
│  └──────────────────────────────────────────────────────┘   │
│                                                               │
│  BIN Resolver → CardMetadata → AuthorizationRequest           │
└─────────────────────────┬───────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────────┐
│                  Domain Events & Side Effects                  │
│                                                               │
│  PaymentSucceededEvent                                        │
│    ├── WebhookEventListener ─── WebhookEvent ─── Webhook     │
│    │                             + WebhookDelivery(s)         │
│    ├── PaymentEventListener ─── LedgerEntry + FinancialEvent │
│    └── TransactionListener ─── Settlement + SettlementItems  │
│                                                               │
│  RefundSucceededEvent → LedgerEntry + FinancialEvent          │
│  SettlementCompletedEvent → LedgerEntry + FinancialEvent      │
└─────────────────────────────────────────────────────────────┘
```

## Key Design Decisions

- **Event-centric webhooks**: One `WebhookEvent` (payload stored once) → many `WebhookDelivery` (one per endpoint)
- **Two-factor auth**: JWT for dashboard users, API key (BCrypt hash) for machine-to-machine
- **Immutable ledger**: Double-entry, write-only entries per transaction type
- **Gateway is pluggable**: Interfaces for every layer — routing, acquiring, network, issuer
- **Bank simulation**: Pure-Java `BankDecisionEngine` with configurable approval probabilities
