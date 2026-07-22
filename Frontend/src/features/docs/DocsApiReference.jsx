import { Eyebrow, Callout, Table, MethodBadge, Badge, InlineCode } from './DocsComponents'

export default function DocsApiReference() {
  return (
    <>
      <Eyebrow>Reference</Eyebrow>
      <h1 className="text-4xl font-bold tracking-tight text-gray-900 mb-2">API Reference</h1>
      <p className="text-lg text-gray-500 max-w-[36rem] mb-6 leading-relaxed">
        Complete reference for all Transiq API endpoints. Base URL: <InlineCode>https://api.transiq.com/v1</InlineCode>. All responses are JSON.
      </p>

      <Callout type="info" title="Authentication">
        API key endpoints use the <InlineCode>Authorization</InlineCode> header with your secret key. Dashboard endpoints use a JWT from <InlineCode>POST /auth/login</InlineCode>. <a href="/docs/authentication">See the Authentication guide</a>.
      </Callout>

      <Section title="Payments API" auth={<Badge color="green">API Key</Badge>}>
        <EndpointRow method="POST" path="/payments" desc="Create a new payment intent. <strong>Idempotent.</strong>" />
        <EndpointRow method="POST" path="/payments/{ref}/confirm" desc="Confirm a payment with payment method details." />
        <EndpointRow method="GET" path="/payments/{ref}" desc="Retrieve a payment intent by reference." />
        <EndpointRow method="POST" path="/payments/{ref}/retry" desc="Retry a failed payment. Creates a new attempt." />
      </Section>

      <Section title="Refunds API" auth={<Badge color="green">API Key</Badge>}>
        <EndpointRow method="POST" path="/refunds/{paymentRef}" desc="Create a refund. <strong>Idempotent.</strong>" />
        <EndpointRow method="GET" path="/refunds" desc="List all refunds for the merchant." />
        <EndpointRow method="GET" path="/refunds/{ref}" desc="Retrieve a refund by reference." />
      </Section>

      <Section title="Settlements API" auth={<Badge color="green">API Key</Badge>}>
        <EndpointRow method="POST" path="/settlements" desc="Initiate a settlement of available balance." />
        <EndpointRow method="GET" path="/settlements" desc="List all settlements." />
        <EndpointRow method="GET" path="/settlements/{ref}" desc="Retrieve a settlement by reference." />
      </Section>

      <Section title="Webhooks API" auth={<Badge color="green">API Key</Badge>}>
        <EndpointRow method="POST" path="/webhooks" desc="Register a new webhook endpoint." />
        <EndpointRow method="GET" path="/webhooks" desc="List all webhook endpoints." />
        <EndpointRow method="DELETE" path="/webhooks/{id}" desc="Delete a webhook endpoint." />
        <EndpointRow method="GET" path="/webhooks/deliveries" desc="List deliveries (filterable by status, event type)." />
        <EndpointRow method="POST" path="/webhooks/deliveries/{id}/retry" desc="Retry a failed delivery." />
        <EndpointRow method="POST" path="/webhooks/events/{eventId}/replay" desc="Replay an event to all active endpoints." />
      </Section>

      <Section title="Ledger API" auth={<Badge color="green">API Key</Badge>}>
        <EndpointRow method="GET" path="/ledger/balance" desc="Get current available, pending, and settlement balances." />
      </Section>

      <Section title="Chargebacks API" auth={<Badge color="green">API Key</Badge>}>
        <EndpointRow method="POST" path="/chargebacks" desc="Create a chargeback. <strong>Idempotent.</strong>" />
        <EndpointRow method="GET" path="/chargebacks" desc="List all chargebacks." />
        <EndpointRow method="GET" path="/chargebacks/{ref}" desc="Retrieve a chargeback by reference." />
      </Section>

      <Section title="Adjustments API" auth={<Badge color="green">API Key</Badge>}>
        <EndpointRow method="POST" path="/adjustments" desc="Create a manual credit or debit adjustment." />
        <EndpointRow method="GET" path="/adjustments" desc="List all adjustments." />
        <EndpointRow method="GET" path="/adjustments/{ref}" desc="Retrieve an adjustment by reference." />
      </Section>

      <Section title="API Keys API" auth={<Badge color="blue">JWT</Badge>}>
        <EndpointRow method="GET" path="/api-keys" desc="List all API keys." />
        <EndpointRow method="POST" path="/api-keys" desc="Create a new API key (shown only once)." />
        <EndpointRow method="DELETE" path="/api-keys/{id}" desc="Revoke an API key." />
        <EndpointRow method="POST" path="/api-keys/{id}/rotate" desc="Rotate a key (old key immediately invalidated)." />
      </Section>

      <Section title="Dashboard API" auth={<Badge color="blue">JWT</Badge>}>
        <EndpointRow method="GET" path="/dashboard/me" desc="Current user profile and merchant info." />
        <EndpointRow method="GET" path="/dashboard/payments" desc="Paginated payment list with filters." />
        <EndpointRow method="GET" path="/dashboard/payments/{ref}" desc="Payment detail view." />
        <EndpointRow method="GET" path="/dashboard/refunds" desc="Paginated refund list." />
        <EndpointRow method="POST" path="/dashboard/refunds" desc="Create a refund." />
        <EndpointRow method="GET" path="/dashboard/settlements" desc="Paginated settlement list." />
        <EndpointRow method="POST" path="/dashboard/settlements" desc="Create a settlement." />
        <EndpointRow method="GET" path="/dashboard/ledger/balance" desc="Get current balance." />
        <EndpointRow method="GET" path="/dashboard/ledger/entries" desc="Paginated ledger entry list." />
        <EndpointRow method="GET" path="/dashboard/webhooks" desc="List webhook endpoints." />
        <EndpointRow method="POST" path="/dashboard/webhooks" desc="Create a webhook endpoint." />
        <EndpointRow method="GET" path="/dashboard/webhooks/deliveries" desc="Paginated delivery list." />
        <EndpointRow method="POST" path="/dashboard/webhooks/deliveries/{id}/retry" desc="Retry a delivery." />
        <EndpointRow method="GET" path="/dashboard/users" desc="List team members." />
        <EndpointRow method="POST" path="/dashboard/users/invite" desc="Invite a team member." />
      </Section>

      <Section title="Admin API" auth={<Badge color="red">JWT + Admin</Badge>}>
        <EndpointRow method="GET" path="/admin/dashboard" desc="Aggregate platform metrics." />
        <EndpointRow method="GET" path="/admin/merchants" desc="Paginated merchant list." />
        <EndpointRow method="GET" path="/admin/payments" desc="Cross-merchant payment list." />
        <EndpointRow method="GET" path="/admin/analytics/revenue" desc="30-day revenue time series." />
        <EndpointRow method="GET" path="/admin/analytics/failure-trends" desc="30-day failure trend data." />
        <EndpointRow method="GET" path="/admin/alerts" desc="System alerts (high failure rate, stalled webhooks)." />
      </Section>

      <h2 className="text-xl font-semibold text-gray-900 mt-10 mb-4">Error codes</h2>
      <Table headers={['Status', 'Code', 'Description']} rows={[
        [<Badge color="red">400</Badge>, <InlineCode>bad_request</InlineCode>, 'Invalid request body or parameters'],
        [<Badge color="red">401</Badge>, <InlineCode>unauthorized</InlineCode>, 'Missing or invalid API key / JWT'],
        [<Badge color="red">403</Badge>, <InlineCode>forbidden</InlineCode>, 'Insufficient permissions'],
        [<Badge color="red">404</Badge>, <InlineCode>not_found</InlineCode>, 'Resource not found'],
        [<Badge color="red">409</Badge>, <InlineCode>conflict</InlineCode>, 'Idempotency key reused with different params'],
        [<Badge color="red">422</Badge>, <InlineCode>unprocessable</InlineCode>, 'Business logic error'],
        [<Badge color="red">429</Badge>, <InlineCode>rate_limited</InlineCode>, 'Too many requests'],
        [<Badge color="red">500</Badge>, <InlineCode>internal_error</InlineCode>, 'Server error'],
      ]} />

      <hr className="border-t border-gray-200 my-10" />
      <div className="flex justify-between flex-wrap gap-4 text-sm">
        <a href="/docs/webhooks" className="text-teal-600 no-underline hover:underline">&larr; Webhooks</a>
      </div>
    </>
  )
}

function Section({ title, auth, children }) {
  return (
    <>
      <h2 className="text-xl font-semibold text-gray-900 mt-10 mb-1">{title}</h2>
      <p className="text-xs text-gray-500 mb-3">Authenticated with {auth}</p>
      {children}
    </>
  )
}

function EndpointRow({ method, path, desc }) {
  return (
    <div className="flex items-start gap-3 py-2.5 border-b border-gray-100 text-sm">
      <span className="shrink-0 w-14"><MethodBadge method={method} /></span>
      <span className="font-mono text-[13px] text-gray-900 shrink-0" style={{ fontFamily: "'JetBrains Mono', monospace" }}>{path}</span>
      <span className="text-gray-600" dangerouslySetInnerHTML={{ __html: desc }} />
    </div>
  )
}
