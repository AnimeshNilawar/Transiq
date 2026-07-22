import { Eyebrow, TerminalBlock, Callout, Table, Badge, CardGrid, CardLink, InlineCode } from './DocsComponents'

const states = [
  [<Badge color="gray">created</Badge>, 'Payment intent created, awaiting confirmation'],
  [<Badge color="amber">requires_payment_method</Badge>, 'Needs a valid payment method to proceed'],
  [<Badge color="blue">processing</Badge>, 'Payment is being processed by the gateway'],
  [<Badge color="green">succeeded</Badge>, 'Payment completed successfully'],
  [<Badge color="red">failed</Badge>, 'Payment declined or errored'],
  [<Badge color="gray">expired</Badge>, 'Payment intent expired before confirmation'],
  [<Badge color="red">refunded</Badge>, 'Payment has been fully refunded'],
]

export default function DocsQuickStart() {
  return (
    <>
      <Eyebrow>Getting Started</Eyebrow>
      <h1 className="text-4xl font-bold tracking-tight text-gray-900 mb-2">Quick Start</h1>
      <p className="text-lg text-gray-500 max-w-[36rem] mb-6 leading-relaxed">
        Transiq is a payment infrastructure API. Create a payment intent, confirm it, and receive webhook notifications — all in under a minute.
      </p>

      <Callout type="info" title="Base URL">
        All API requests are made to <InlineCode>https://api.transiq.com/v1</InlineCode>. Responses are JSON. You'll need an API key to authenticate — <a href="/docs/authentication">learn how to get one</a>.
      </Callout>

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">Make your first API call</h2>

      <TerminalBlock>
        <span className="text-gray-500 italic"># Create a payment intent</span>{'\n'}
        <span className="text-teal-400 select-none">$</span> curl -X POST https://api.transiq.com/v1/payments \<br />
        &nbsp;&nbsp;-H <span className="text-gray-200">"Authorization: sk_test_<span className="text-teal-300">4fR3...</span>"</span> \<br />
        &nbsp;&nbsp;-H <span className="text-gray-200">"Idempotency-Key: <span className="text-teal-300">order-001</span>"</span> \<br />
        &nbsp;&nbsp;-d <span className="text-gray-200">{'{"amount": 4200, "currency": "INR", "order_id": "order-001"}'}</span>
        <br /><br />
        <span className="text-green-400">→ 201 Created</span> <span className="text-gray-500 italic">— 0.34s</span>
        <br />
        <span className="text-gray-200">{'{'}</span><br />
        &nbsp;&nbsp;<span className="text-cyan-300">"id"</span>: <span className="text-green-300">"pay_tq_2kR4m9"</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"status"</span>: <span className="text-green-300">"created"</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"amount"</span>: <span className="text-yellow-300">4200</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"client_secret"</span>: <span className="text-green-300">"cs_tq_8mN2..."</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"expires_at"</span>: <span className="text-green-300">"2026-07-22T01:45:00Z"</span><br />
        <span className="text-gray-200">{'}'}</span>
      </TerminalBlock>

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">Confirm the payment</h2>

      <TerminalBlock>
        <span className="text-gray-500 italic"># Confirm with a card payment</span>{'\n'}
        <span className="text-teal-400 select-none">$</span> curl -X POST https://api.transiq.com/v1/payments/pay_tq_2kR4m9/confirm \<br />
        &nbsp;&nbsp;-H <span className="text-gray-200">"Authorization: sk_test_4fR3..."</span> \<br />
        &nbsp;&nbsp;-d <span className="text-gray-200">{'{"client_secret": "cs_tq_8mN2...", "payment_method_type": "card", "card": {"network": "visa", "issuer_bank": "hdfc"}}'}</span>
        <br /><br />
        <span className="text-green-400">→ 200 OK</span> <span className="text-gray-500 italic">— 0.52s</span>
        <br />
        <span className="text-gray-200">{'{'}</span><br />
        &nbsp;&nbsp;<span className="text-cyan-300">"id"</span>: <span className="text-green-300">"pay_tq_2kR4m9"</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"status"</span>: <span className="text-green-300">"succeeded"</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"amount"</span>: <span className="text-yellow-300">4200</span><br />
        <span className="text-gray-200">{'}'}</span>
      </TerminalBlock>

      <Callout type="success" title="Payment Succeeded">
        On success, Transiq fires a <InlineCode>payment.succeeded</InlineCode> webhook event to all registered endpoints. <a href="/docs/webhooks">Learn about webhooks &rarr;</a>
      </Callout>

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">Payment lifecycle</h2>

      <Table headers={['State', 'Description']} rows={states} />

      <hr className="border-t border-gray-200 my-10" />

      <h2 className="text-xl font-semibold text-gray-900 mb-4">Next steps</h2>
      <CardGrid>
        <CardLink to="/docs/authentication" title="Authentication &rarr;" desc="Learn about API key types, scopes, and how to manage keys." />
        <CardLink to="/docs/payments" title="Payments &rarr;" desc="Full payment lifecycle: create, confirm, retry, and handle failures." />
        <CardLink to="/docs/webhooks" title="Webhooks &rarr;" desc="Receive real-time payment events with HMAC-signed payloads." />
        <CardLink to="/docs/api-reference" title="API Reference &rarr;" desc="Complete endpoint reference with request and response schemas." />
      </CardGrid>

      <p className="text-xs text-gray-400 text-center mt-10">Transiq v1.0</p>
    </>
  )
}
