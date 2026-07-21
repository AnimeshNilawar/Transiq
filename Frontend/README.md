# Transiq Frontend — Merchant Dashboard & Admin Console

React 19 SPA with Tailwind CSS 4, TanStack React Query 5, and Recharts.

Part of the [Transiq](../README.md) payment infrastructure platform.

## Tech Stack

| Technology | Purpose |
|-----------|---------|
| **React 19** | UI framework with concurrent features |
| **Vite 8** | Build tool with instant HMR and Oxlint |
| **React Router 7** | Declarative routing with loaders/actions |
| **TanStack React Query 5** | Server state, caching, background refetch, pagination |
| **Tailwind CSS 4** | Utility-first styling with CSS custom property design tokens |
| **Recharts 3** | Declarative chart components (AreaChart, BarChart) |
| **Zod 4** | Runtime form validation schemas |
| **Axios** | HTTP client with request/response interceptors |
| **React Hook Form** | Performant form state management |
| **Lucide React** | Consistent icon set |
| **Sonner** | Toast notifications |
| **date-fns** | Date formatting and manipulation |

## Feature Modules

| Module | Description |
|--------|-------------|
| `auth/` | Login, Register |
| `dashboard/` | Overview with balance trend chart |
| `payments/` | Payment list, detail, retry |
| `refunds/` | Refund list, create, detail |
| `settlements/` | Settlement list, create, detail |
| `ledger/` | Balance card + paginated entries |
| `webhooks/` | Endpoint CRUD, delivery list/detail, retry, event replay |
| `api-keys/` | API key create, list, rotate, revoke |
| `admin/` | 10 pages: dashboard, merchants, payments, refunds, settlements, users, API keys, webhook deliveries |
| `checkout/` | Public checkout form (API-key authenticated) |
| `settings/` | Profile and merchant settings |

## API Layer

Two Axios instances handle authentication automatically:

- **`jwtClient`** — Reads JWT from `localStorage`, attaches as `Bearer` header, redirects to `/login` on 401
- **`apiKeyClient`** — Reads API key from `sessionStorage`, attaches as auth header

## State Management

- Server state: **TanStack React Query 5** (caching, invalidation, paginated queries)
- Auth: `localStorage` for JWT, `sessionStorage` for checkout API key
- Navigation: React Router 7 URL state

## Getting Started

```bash
# Set API base URL
$env:VITE_API_BASE_URL="http://localhost:8080/api/v1"

# Install & run
npm install
npm run dev
```

Runs on `http://localhost:5173`.
