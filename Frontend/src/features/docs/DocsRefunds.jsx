import { Eyebrow, TerminalBlock, Callout, Table, Badge, ParamTable, InlineCode } from './DocsComponents'

const states = [
  [<Badge color="gray">created</Badge>, 'Refund request created, pending processing'],
  [<Badge color="blue">processing</Badge>, 'Refund is being processed by the gateway'],
  [<Badge color="green">succeeded</Badge>, 'Refund completed. Funds returned to the customer.'],
  [<Badge color="red">failed</Badge>, 'Refund could not be processed'],
]

const reasons = [
  [<InlineCode>requested_by_customer</InlineCode>, 'Customer changed their mind or no longer needs the product'],
  [<InlineCode>duplicate_payment</InlineCode>, 'Customer was charged more than once'],
  [<InlineCode>fraudulent</InlineCode>, 'Payment appears fraudulent or unauthorized'],
  [<InlineCode>product_unavailable</InlineCode>, 'Product or service is out of stock'],
  [<InlineCode>other</InlineCode>, 'Any reason not covered by the above'],
]

export default function DocsRefunds() {
  return (
    <>
      <Eyebrow>Guides</Eyebrow>
      <h1 className="text-4xl font-bold tracking-tight text-gray-900 mb-2">Refunds</h1>
      <p className="text-lg text-gray-500 max-w-[36rem] mb-6 leading-relaxed">
        Refund a payment partially or in full. The Refunds API is idempotent — same request, same result, safe to retry on network failures.
      </p>

      <Callout type="info" title="Refund window">
        Refunds can be processed within 180 days of the original payment. After this period, contact support to process the refund manually.
      </Callout>

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">Create a refund</h2>

      <TerminalBlock>
        <span className="text-gray-500 italic"># Full refund</span>{'\n'}
        <span className="text-teal-400 select-none">$</span> curl -X POST https://api.transiq.com/v1/refunds/pay_tq_9kM4n2 \<br />
        &nbsp;&nbsp;-H <span className="text-gray-200">"Authorization: sk_test_4fR3..."</span> \<br />
        &nbsp;&nbsp;-H <span className="text-gray-200">"Idempotency-Key: refund-001"</span> \<br />
        &nbsp;&nbsp;-d <span className="text-gray-200">{'{"reason": "requested_by_customer"}'}</span>
        <br /><br />
        <span className="text-green-400">→ 201 Created</span><br />
        <span className="text-gray-200">{'{'}</span><br />
        &nbsp;&nbsp;<span className="text-cyan-300">"refund_reference"</span>: <span className="text-green-300">"ref_tq_3xN7m1"</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"amount"</span>: <span className="text-yellow-300">129900</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"status"</span>: <span className="text-green-300">"succeeded"</span><br />
        <span className="text-gray-200">{'}'}</span>
      </TerminalBlock>

      <h3 className="text-base font-semibold text-gray-900 mt-6 mb-3">Parameters</h3>
      <ParamTable params={[
        { name: 'amount', type: 'integer', desc: 'Refund amount. If omitted, the full payment is refunded.' },
        { name: 'reason', type: 'string', desc: 'One of the reason codes listed below.' },
      ]} />

      <Callout type="warning" title="Partial refunds">
        You can refund a partial amount by specifying the <InlineCode>amount</InlineCode> field. A payment is marked as <InlineCode>refunded</InlineCode> only when the total refunded equals the original amount.
      </Callout>

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">Refund states</h2>
      <Table headers={['State', 'Description']} rows={states} />

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">List refunds</h2>

      <TerminalBlock>
        <span className="text-teal-400 select-none">$</span> curl https://api.transiq.com/v1/refunds \<br />
        &nbsp;&nbsp;-H <span className="text-gray-200">"Authorization: sk_test_4fR3..."</span>
      </TerminalBlock>

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">Refund reasons</h2>
      <Table headers={['Reason', 'When to use']} rows={reasons} />

      <hr className="border-t border-gray-200 my-10" />
      <div className="flex justify-between flex-wrap gap-4 text-sm">
        <a href="/docs/payments" className="text-teal-600 no-underline hover:underline">&larr; Payments</a>
        <a href="/docs/settlements" className="text-teal-600 no-underline hover:underline">Settlements &rarr;</a>
      </div>
    </>
  )
}
