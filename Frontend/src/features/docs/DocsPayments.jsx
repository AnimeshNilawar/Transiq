import { Eyebrow, TerminalBlock, Callout, Table, Badge, MethodBadge, ParamTable } from './DocsComponents'

const params = [
  { name: 'amount', type: 'integer', desc: 'Amount in the smallest currency unit (e.g., 129900 = &8377;1,299.00). <strong>Required.</strong>' },
  { name: 'currency', type: 'string', desc: 'Three-letter ISO code. Supported: <InlineCode>INR</InlineCode>, <InlineCode>USD</InlineCode>, <InlineCode>EUR</InlineCode>. <strong>Required.</strong>' },
  { name: 'customer_email', type: 'string', desc: 'Customer email for receipt and communication.' },
  { name: 'customer_name', type: 'string', desc: 'Customer name for reference.' },
  { name: 'order_id', type: 'string', desc: 'Your internal order ID (max 100 chars).' },
  { name: 'description', type: 'string', desc: 'Description shown on payment page (max 500 chars).' },
  { name: 'metadata', type: 'object', desc: 'Key-value pairs for your records (max 20 keys).' },
]

const pmTypes = [
  [<Badge color="blue">Card</Badge>, <InlineCode>card</InlineCode>, <InlineCode>card.network</InlineCode>, <InlineCode>card.issuer_bank</InlineCode>],
  [<Badge color="blue">UPI</Badge>, <InlineCode>upi</InlineCode>, <InlineCode>upi.upi_id</InlineCode>, <InlineCode>—</InlineCode>],
  [<Badge color="blue">Net Banking</Badge>, <InlineCode>net_banking</InlineCode>, <InlineCode>net_banking.bank_code</InlineCode>, <InlineCode>—</InlineCode>],
  [<Badge color="blue">Wallet</Badge>, <InlineCode>wallet</InlineCode>, <InlineCode>wallet.provider</InlineCode>, <InlineCode>—</InlineCode>],
]

import { InlineCode } from './DocsComponents'

export default function DocsPayments() {
  return (
    <>
      <Eyebrow>Guides</Eyebrow>
      <h1 className="text-4xl font-bold tracking-tight text-gray-900 mb-2">Payments</h1>
      <p className="text-lg text-gray-500 max-w-[36rem] mb-6 leading-relaxed">
        The Payments API lets you create payment intents, confirm them with a payment method, and handle the full payment lifecycle.
      </p>

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">Create a payment intent</h2>
      <p className="text-gray-600 mb-3">A payment intent tracks the lifecycle of a payment from creation to completion.</p>

      <TerminalBlock>
        <span className="text-teal-400 select-none">$</span> curl -X POST https://api.transiq.com/v1/payments \<br />
        &nbsp;&nbsp;-H <span className="text-gray-200">"Authorization: sk_test_4fR3..."</span> \<br />
        &nbsp;&nbsp;-H <span className="text-gray-200">"Idempotency-Key: uniq-42"</span> \<br />
        &nbsp;&nbsp;-d <span className="text-gray-200">{'{"amount": 129900, "currency": "INR", "customer_email": "customer@example.com", "order_id": "ORD-001", "description": "Premium Plan — Annual"}'}</span>
        <br /><br />
        <span className="text-green-400">→ 201 Created</span><br />
        <span className="text-gray-200">{'{'}</span><br />
        &nbsp;&nbsp;<span className="text-cyan-300">"id"</span>: <span className="text-green-300">"pay_tq_9kM4n2"</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"status"</span>: <span className="text-green-300">"created"</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"amount"</span>: <span className="text-yellow-300">129900</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"client_secret"</span>: <span className="text-green-300">"cs_tq_7bH3..."</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"expires_at"</span>: <span className="text-green-300">"2026-07-22T02:00:00Z"</span><br />
        <span className="text-gray-200">{'}'}</span>
      </TerminalBlock>

      <h3 className="text-base font-semibold text-gray-900 mt-6 mb-3">Parameters</h3>
      <ParamTable params={params} />

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">Confirm a payment</h2>

      <TerminalBlock>
        <span className="text-teal-400 select-none">$</span> curl -X POST https://api.transiq.com/v1/payments/pay_tq_9kM4n2/confirm \<br />
        &nbsp;&nbsp;-H <span className="text-gray-200">"Authorization: sk_test_4fR3..."</span> \<br />
        &nbsp;&nbsp;-d <span className="text-gray-200">{'{"client_secret": "cs_tq_7bH3...", "payment_method_type": "card", "card": {"network": "visa", "issuer_bank": "icici"}}'}</span>
      </TerminalBlock>

      <h3 className="text-base font-semibold text-gray-900 mt-6 mb-3">Payment method types</h3>
      <Table headers={['Type', 'Value', 'Additional fields']} rows={pmTypes} />

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">Retrieve a payment</h2>

      <TerminalBlock>
        <span className="text-teal-400 select-none">$</span> curl https://api.transiq.com/v1/payments/pay_tq_9kM4n2 \<br />
        &nbsp;&nbsp;-H <span className="text-gray-200">"Authorization: sk_test_4fR3..."</span>
        <br /><br />
        <span className="text-green-400">→ 200 OK</span><br />
        <span className="text-gray-200">{'{'}</span><br />
        &nbsp;&nbsp;<span className="text-cyan-300">"status"</span>: <span className="text-green-300">"succeeded"</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"amount"</span>: <span className="text-yellow-300">129900</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"refunded_amount"</span>: <span className="text-yellow-300">0</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"attempts"</span>: <span className="text-green-300">{'[{"attempt_number": 1, "status": "succeeded", "processing_time_ms": 482}]'}</span><br />
        <span className="text-gray-200">{'}'}</span>
      </TerminalBlock>

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">Retry a failed payment</h2>
      <p className="text-gray-600 mb-3">Only payments in <InlineCode>failed</InlineCode> state can be retried. Each retry creates a new payment attempt.</p>

      <TerminalBlock>
        <span className="text-gray-500 italic"># Retry a failed payment</span>{'\n'}
        <span className="text-teal-400 select-none">$</span> curl -X POST https://api.transiq.com/v1/payments/pay_tq_9kM4n2/retry \<br />
        &nbsp;&nbsp;-H <span className="text-gray-200">"Authorization: sk_test_4fR3..."</span>
      </TerminalBlock>

      <Callout type="info" title="Retry Rules">
        Only payments in <InlineCode>failed</InlineCode> state can be retried. The maximum number of retries is determined by your merchant configuration.
      </Callout>

      <hr className="border-t border-gray-200 my-10" />
      <div className="flex justify-between flex-wrap gap-4 text-sm">
        <a href="/docs/authentication" className="text-teal-600 no-underline hover:underline">&larr; Authentication</a>
        <a href="/docs/refunds" className="text-teal-600 no-underline hover:underline">Refunds &rarr;</a>
      </div>
    </>
  )
}
