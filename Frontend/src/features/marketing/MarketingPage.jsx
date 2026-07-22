import { useEffect, useState, useCallback } from 'react'
import { Link, Navigate } from 'react-router-dom'
import useAuth from '@/hooks/useAuth'
import AuthorizationTicker from './AuthorizationTicker'
import { X } from 'lucide-react'
import './marketing.css'

export default function MarketingPage() {
  const { isAuthenticated } = useAuth()

  useEffect(() => {
    fetch('/actuator/health', { method: 'GET' }).catch(() => {})
  }, [])

  if (isAuthenticated) return <Navigate to="/dashboard" replace />

  return (
    <div className="marketing" style={{ background: 'var(--mkt-paper)', color: 'var(--mkt-ink)' }}>
      <Nav />
      <Hero />
      <CodeSnippet />
      <StatsStrip />
      <ArchitecturePillars />
      <Screenshots />
      <PaymentFlow />
      <UnderTheHood />
      <TechManifest />
      <Footer />
    </div>
  )
}

function Nav() {
  return (
    <nav
      className="sticky top-0 z-50"
      style={{ background: 'var(--mkt-ink)' }}
    >
      <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-4">
        <span
          className="text-lg font-bold tracking-tight"
          style={{ color: 'var(--mkt-paper)' }}
        >
          Transiq
        </span>
        <div className="flex items-center gap-6">
          <a
            href="https://github.com/AnimeshNilawar/Transiq"
            target="_blank"
            rel="noopener noreferrer"
            className="text-sm no-underline transition-opacity hover:opacity-80"
            style={{ color: 'var(--mkt-paper)' }}
          >
            GitHub
          </a>
          <Link
            to="/docs"
            className="text-sm no-underline transition-opacity hover:opacity-80"
            style={{ color: 'var(--mkt-paper)' }}
          >
            Docs
          </Link>
          <Link
            to="/checkout-demo"
            className="text-sm no-underline transition-opacity hover:opacity-80"
            style={{ color: 'var(--mkt-paper)' }}
          >
            Checkout Demo
          </Link>
          <Link
            to="/login"
            className="rounded-lg px-4 py-2 text-sm font-semibold no-underline transition-opacity hover:opacity-90"
            style={{ background: 'var(--mkt-accent)', color: '#fff' }}
          >
            Sign in
          </Link>
        </div>
      </div>
    </nav>
  )
}

function Hero() {
  return (
    <section className="mx-auto max-w-6xl px-6 pt-20 pb-16 text-center">
      <h1 className="text-5xl font-bold leading-tight tracking-tight md:text-6xl" style={{ color: 'var(--mkt-ink)' }}>
        A payment gateway
        <br />
        <span className="text-transparent bg-clip-text bg-gradient-to-r from-orange-500 to-orange-600">
          built from scratch
        </span>
      </h1>
      <p
        className="mx-auto mt-4 max-w-2xl text-lg leading-relaxed"
        style={{ color: 'var(--mkt-ink-muted)' }}
      >
        Transiq simulates the full payment lifecycle — routing, authorization, double-entry ledgering,
        and event-driven webhooks — in a single deployable platform. No wrappers, no shortcuts.
      </p>

      <div className="mx-auto mt-10 max-w-2xl">
        <AuthorizationTicker />
      </div>

      <div className="mt-10 flex items-center justify-center gap-4">
        <Link
          to="/login"
          className="rounded-lg px-6 py-3 text-sm font-semibold no-underline transition-opacity hover:opacity-90"
          style={{ background: 'var(--mkt-accent)', color: '#fff' }}
        >
          View the dashboard &rarr;
        </Link>
        <a
          href="https://github.com/AnimeshNilawar/Transiq"
          target="_blank"
          rel="noopener noreferrer"
          className="rounded-lg border-2 px-6 py-3 text-sm font-semibold no-underline transition-opacity hover:opacity-80"
          style={{ borderColor: 'var(--mkt-ink)', color: 'var(--mkt-ink)' }}
        >
          View source
        </a>
      </div>
    </section>
  )
}

function CodeSnippet() {
  return (
    <section className="mx-auto max-w-4xl px-6 pb-8">
      <div
        className="rounded-xl p-6 md:p-8"
        style={{ background: 'var(--mkt-ink)' }}
      >
        <div className="flex items-center gap-2 mb-4">
          <span className="inline-block w-2 h-2 rounded-full" style={{ background: 'var(--mkt-approved)' }} />
          <span className="text-[11px] font-semibold uppercase tracking-widest" style={{ color: 'rgba(255,255,255,0.5)' }}>
            Create a payment
          </span>
        </div>

        <div
          className="overflow-x-auto"
          style={{ fontFamily: "'IBM Plex Mono', 'JetBrains Mono', monospace" }}
        >
          <pre className="text-sm leading-relaxed text-gray-300">
            <span className="text-gray-500">curl -X POST https://api.transiq.dev/v1/payments \</span>
            <br />
            <span className="text-gray-500">  -H </span><span className="text-amber-300">&quot;Authorization: sk_live_...&quot;</span><span className="text-gray-500"> \</span>
            <br />
            <span className="text-gray-500">  -H </span><span className="text-amber-300">&quot;Idempotency-Key: order-8842-attempt-1&quot;</span><span className="text-gray-500"> \</span>
            <br />
            <span className="text-gray-500">  -H </span><span className="text-amber-300">&quot;Content-Type: application/json&quot;</span><span className="text-gray-500"> \</span>
            <br />
            <span className="text-gray-500">  -d </span><span className="text-gray-300">&#x27;</span>
            <br />
            <span className="text-gray-300">    &quot;amount&quot;: 10000,</span>
            <br />
            <span className="text-gray-300">    &quot;currency&quot;: </span><span className="text-amber-300">&quot;INR&quot;</span><span className="text-gray-300">,</span>
            <br />
            <span className="text-gray-300">    &quot;orderId&quot;: </span><span className="text-amber-300">&quot;order-8842&quot;</span>
            <br />
            <span className="text-gray-300">  </span><span className="text-gray-500">&#x27;</span>
          </pre>
        </div>

        <div
          className="mt-3 flex items-center gap-2"
          style={{ color: 'rgba(255,255,255,0.4)' }}
        >
          <span className="inline-block w-4 border-t" style={{ borderColor: 'rgba(255,255,255,0.2)' }} />
          <span className="text-[11px] font-semibold uppercase tracking-widest">Response</span>
        </div>

        <div
          className="mt-3 overflow-x-auto"
          style={{ fontFamily: "'IBM Plex Mono', 'JetBrains Mono', monospace" }}
        >
          <pre className="text-sm leading-relaxed">
            <span className="text-gray-400">{'{'}</span>
            <br />
            <span className="text-gray-400">  &quot;paymentReference&quot;: </span><span className="text-amber-300">&quot;pay_2x7a9k3m&quot;</span><span className="text-gray-400">,</span>
            <br />
            <span className="text-gray-400">  &quot;clientSecret&quot;: </span><span className="text-amber-300">&quot;cs_5f8e3a1b_secret_suffix&quot;</span><span className="text-gray-400">,</span>
            <br />
            <span className="text-gray-400">  &quot;status&quot;: </span><span className="text-amber-300">&quot;REQUIRES_PAYMENT_METHOD&quot;</span>
            <br />
            <span className="text-gray-400">{'}'}</span>
          </pre>
        </div>
      </div>

      <p className="mt-3 text-xs leading-relaxed text-center" style={{ color: 'var(--mkt-ink-muted)' }}>
        Every payment creation is idempotent &mdash; retry safely with the same key.
      </p>
    </section>
  )
}

const STATS = [
  { value: '5', label: 'acquiring banks' },
  { value: '3', label: 'card networks' },
  { value: '5', label: 'webhook event types' },
  { value: '1m \u2192 16m', label: 'retry schedule' },
]

function StatsStrip() {
  return (
    <section
      className="border-y py-12"
      style={{ borderColor: 'rgba(20,18,43,0.08)', background: '#fff' }}
    >
      <div className="mx-auto flex max-w-4xl flex-wrap justify-center gap-x-12 gap-y-6 px-6">
        {STATS.map((stat) => (
          <div key={stat.label} className="flex items-baseline gap-2">
            <span
              className="text-2xl font-semibold tracking-tight"
              style={{ fontFamily: "'IBM Plex Mono', 'JetBrains Mono', monospace", color: 'var(--mkt-ink)' }}
            >
              {stat.value}
            </span>
            <span className="text-sm" style={{ color: 'var(--mkt-ink-muted)' }}>
              {stat.label}
            </span>
          </div>
        ))}
      </div>
    </section>
  )
}

const PILLARS = [
  {
    name: 'Routing',
    description:
      'A pluggable routing engine that maps BINs to card networks and selects the optimal acquiring bank from five real institutions. Network and acquirer interfaces are swappable — adding a new bank is implementing one interface.',
  },
  {
    name: 'Ledger',
    description:
      'Double-entry accounting for every financial event. Payments, refunds, settlements, and adjustments each create offsetting credit and debit entries across five account types. Balances are derived, not stored.',
  },
  {
    name: 'Webhooks',
    description:
      'Event-centric webhook system that delivers signed payloads to multiple endpoints with exponential backoff retry. Each event creates one WebhookEvent record and one WebhookDelivery per endpoint — delivery tracking is independent per destination.',
  },
  {
    name: 'Dashboard & Admin',
    description:
      'A full merchant dashboard with JWT auth and a platform admin panel with cross-merchant CRUD, revenue/failure analytics, and system alerting. Both built in React 19 with TanStack Query for server state.',
  },
]

function ArchitecturePillars() {
  return (
    <section className="mx-auto max-w-6xl px-6 py-20">
      <h2 className="text-sm font-semibold uppercase tracking-widest" style={{ color: 'var(--mkt-ink-muted)' }}>
        Architecture
      </h2>
      <p className="mt-2 text-3xl font-bold tracking-tight" style={{ color: 'var(--mkt-ink)' }}>
        What the platform actually does
      </p>
      <div className="mt-10 grid gap-6 md:grid-cols-2">
        {PILLARS.map((pillar) => (
          <div
            key={pillar.name}
            className="rounded-xl border p-6 transition-shadow hover:shadow-md"
            style={{ borderColor: 'rgba(20,18,43,0.08)', background: '#fff' }}
          >
            <h3 className="text-lg font-semibold" style={{ color: 'var(--mkt-ink)' }}>
              {pillar.name}
            </h3>
            <p className="mt-2 text-sm leading-relaxed" style={{ color: 'var(--mkt-ink-muted)' }}>
              {pillar.description}
            </p>
          </div>
        ))}
      </div>
    </section>
  )
}

const SCREENSHOTS = [
  {
    src: '/props/payment-list.png',
    caption: 'Real-time payment status across all methods',
  },
  {
    src: '/props/ledger.png',
    caption: 'Double-entry ledger with running balance',
  },
  {
    src: '/props/admin-dashboard.png',
    caption: 'Cross-merchant revenue and failure analytics',
  },
]

function BrowserFrame({ children }) {
  return (
    <div
      className="overflow-hidden rounded-xl border"
      style={{ borderColor: 'rgba(20,18,43,0.12)', background: '#fff' }}
    >
      <div
        className="flex items-center gap-1.5 px-4 py-2.5 border-b"
        style={{ borderColor: 'rgba(20,18,43,0.08)', background: 'var(--mkt-paper)' }}
      >
        <span className="inline-block w-2.5 h-2.5 rounded-full" style={{ background: '#dc2626' }} />
        <span className="inline-block w-2.5 h-2.5 rounded-full" style={{ background: '#eab308' }} />
        <span className="inline-block w-2.5 h-2.5 rounded-full" style={{ background: '#16a34a' }} />
      </div>
      <div className="p-4">
        {children}
      </div>
    </div>
  )
}

function Screenshots() {
  const [lightboxIdx, setLightboxIdx] = useState(null)

  const close = useCallback(() => setLightboxIdx(null), [])

  useEffect(() => {
    if (lightboxIdx === null) return
    const handler = (e) => { if (e.key === 'Escape') close() }
    document.addEventListener('keydown', handler)
    document.body.style.overflow = 'hidden'
    return () => {
      document.removeEventListener('keydown', handler)
      document.body.style.overflow = ''
    }
  }, [lightboxIdx, close])

  return (
    <section className="border-y py-20" style={{ borderColor: 'rgba(20,18,43,0.08)', background: '#fff' }}>
      <div className="mx-auto max-w-6xl px-6">
        <h2 className="text-sm font-semibold uppercase tracking-widest" style={{ color: 'var(--mkt-ink-muted)' }}>
          Product
        </h2>
        <p className="mt-2 text-3xl font-bold tracking-tight" style={{ color: 'var(--mkt-ink)' }}>
          See it in action
        </p>
        <div className="mt-10 grid gap-8 md:grid-cols-3">
          {SCREENSHOTS.map((shot, i) => (
            <div key={shot.src}>
              <button
                type="button"
                onClick={() => setLightboxIdx(i)}
                className="w-full text-left cursor-pointer"
              >
                <BrowserFrame>
                  <img
                    src={shot.src}
                    alt={shot.caption}
                    className="w-full h-auto rounded-lg"
                    loading={i === 0 ? 'eager' : 'lazy'}
                  />
                </BrowserFrame>
              </button>
              <p
                className="mt-2 text-xs text-center"
                style={{ color: 'var(--mkt-ink-muted)', fontFamily: "'IBM Plex Mono', 'JetBrains Mono', monospace" }}
              >
                {shot.caption}
              </p>
            </div>
          ))}
        </div>
      </div>

      {lightboxIdx !== null && (
        <div
          className="fixed inset-0 z-[100] flex items-center justify-center bg-black/70 p-4"
          onClick={close}
          style={{ backdropFilter: 'blur(4px)' }}
        >
          <div
            className="relative max-h-[90vh] max-w-[90vw] overflow-hidden rounded-xl"
            style={{ background: 'var(--mkt-ink)' }}
            onClick={(e) => e.stopPropagation()}
          >
            <button
              type="button"
              onClick={close}
              className="absolute top-3 right-3 z-10 flex h-8 w-8 items-center justify-center rounded-full bg-black/50 text-white hover:bg-black/70 transition-colors"
            >
              <X className="h-4 w-4" />
            </button>
            <img
              src={SCREENSHOTS[lightboxIdx].src}
              alt={SCREENSHOTS[lightboxIdx].caption}
              className="max-h-[85vh] w-auto object-contain"
            />
          </div>
        </div>
      )}
    </section>
  )
}

const FLOW_STEPS = [
  { label: 'Create', desc: 'Payment intent created with amount and currency' },
  { label: 'Confirm', desc: 'Client verifies with client_secret, selects payment method' },
  { label: 'Route', desc: 'BIN resolved, network and acquirer selected' },
  { label: 'Authorize', desc: 'Card network and acquiring bank process the transaction' },
  { label: 'Ledger', desc: 'Double-entry records created for the financial event' },
  { label: 'Notify', desc: 'Webhook delivered to all registered endpoints' },
]

function PaymentFlow() {
  return (
    <section style={{ background: 'var(--mkt-ink)' }} className="py-20">
      <div className="mx-auto max-w-6xl px-6">
        <h2 className="text-sm font-semibold uppercase tracking-widest" style={{ color: 'rgba(255,255,255,0.5)' }}>
          Payment Flow
        </h2>
        <p className="mt-2 text-3xl font-bold tracking-tight text-white">
          Lifecycle of a transaction
        </p>
        <div className="mt-10 grid gap-4 md:grid-cols-6">
          {FLOW_STEPS.map((step, i) => (
            <div key={step.label} className="text-center">
              <span
                className="inline-flex h-8 w-8 items-center justify-center rounded-lg text-sm font-bold"
                style={{ background: 'var(--mkt-accent)', color: '#fff', fontFamily: "'IBM Plex Mono', 'JetBrains Mono', monospace" }}
              >
                {i + 1}
              </span>
              <p className="mt-3 text-sm font-semibold text-white">{step.label}</p>
              <p className="mt-1 text-xs leading-relaxed" style={{ color: 'rgba(255,255,255,0.5)' }}>
                {step.desc}
              </p>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}

const DECISIONS = [
  {
    label: 'Idempotency',
    text: 'Payment and refund creation accept an Idempotency-Key header, enabling safe retries without duplicate charges. If a network error drops the response, the caller retries with the same key and receives the original result — no double charge.',
  },
  {
    label: 'Webhook delivery',
    text: 'Webhook events fire via @TransactionalEventListener(phase = AFTER_COMMIT), meaning delivery only happens after the database transaction commits. If the payment transaction rolls back, no phantom webhook is sent to the merchant.',
  },
  {
    label: 'Dual auth',
    text: 'Short-lived JWTs for dashboard sessions, BCrypt-hashed API keys for machine-to-machine integration. Avoids the complexity of a full OAuth2 stack while keeping the two audiences securely separated.',
  },
  {
    label: 'Double-entry',
    text: 'Every financial event creates offsetting credit and debit entries across typed accounts — payments, refunds, settlements, and adjustments. Balances are derived by summing entries, not stored as denormalized counters, so the books balance by construction.',
  },
  {
    label: 'Gateway simulation',
    text: 'Routing, acquiring banks, and card networks are all interface-driven. The simulation layer (AuthorizationSimulator, BankDecisionEngine) mimics real-world approval rates and network rules today, and can be swapped for real bank integrations without touching the pipeline.',
  },
]

function UnderTheHood() {
  return (
    <section className="mx-auto max-w-4xl px-6 py-20">
      <h2 className="text-sm font-semibold uppercase tracking-widest" style={{ color: 'var(--mkt-ink-muted)' }}>
        Engineering
      </h2>
      <p className="mt-2 text-3xl font-bold tracking-tight" style={{ color: 'var(--mkt-ink)' }}>
        Under the hood
      </p>
      <p className="mt-2 text-sm" style={{ color: 'var(--mkt-ink-muted)' }}>
        A few decisions worth explaining.
      </p>
      <div className="mt-10 space-y-10">
        {DECISIONS.map((d) => (
          <div key={d.label}>
            <p
              className="text-xs font-semibold uppercase tracking-widest mb-2"
              style={{ color: 'var(--mkt-ink)', fontFamily: "'IBM Plex Mono', 'JetBrains Mono', monospace" }}
            >
              {d.label}
            </p>
            <p className="text-sm leading-relaxed" style={{ color: 'var(--mkt-ink-muted)' }}>
              {d.text}
            </p>
          </div>
        ))}
      </div>
    </section>
  )
}

const TECH = ['Java 21', 'Spring Boot 4.1', 'PostgreSQL', 'React 19', 'Vite']

function TechManifest() {
  return (
    <section className="mx-auto max-w-6xl px-6 py-16 text-center">
      <p className="text-sm" style={{ color: 'var(--mkt-ink-muted)' }}>
        Built with
      </p>
      <div
        className="mt-4 flex flex-wrap justify-center gap-x-8 gap-y-3 text-lg font-medium"
        style={{ fontFamily: "'IBM Plex Mono', 'JetBrains Mono', monospace", color: 'var(--mkt-ink)' }}
      >
        {TECH.map((t) => (
          <span key={t}>{t}</span>
        ))}
      </div>
    </section>
  )
}

function Footer() {
  return (
    <footer className="border-t py-12 text-center" style={{ borderColor: 'rgba(20,18,43,0.08)' }}>
      <div className="mx-auto max-w-6xl px-6">
        <div className="flex items-center justify-center gap-4">
          <Link
            to="/login"
            className="rounded-lg px-6 py-3 text-sm font-semibold no-underline transition-opacity hover:opacity-90"
            style={{ background: 'var(--mkt-accent)', color: '#fff' }}
          >
            View the dashboard &rarr;
          </Link>
          <a
            href="https://github.com/AnimeshNilawar/Transiq"
            target="_blank"
            rel="noopener noreferrer"
            className="rounded-lg border-2 px-6 py-3 text-sm font-semibold no-underline transition-opacity hover:opacity-80"
            style={{ borderColor: 'var(--mkt-ink)', color: 'var(--mkt-ink)' }}
          >
            View source
          </a>
        </div>
        <p className="mt-8 text-xs leading-relaxed" style={{ color: 'var(--mkt-ink-muted)' }}>
          Transiq is a portfolio project demonstrating payment infrastructure engineering.
          <br />
          No real payments are processed. No customer data is stored.
        </p>
      </div>
    </footer>
  )
}
