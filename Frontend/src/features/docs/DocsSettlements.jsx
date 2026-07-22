import { Eyebrow, TerminalBlock, Callout, Table, Badge, InlineCode } from './DocsComponents'

const settlementTypes = [
  ['Settlement cycle', 'T+1', 'T+0 (instant)'],
  ['Minimum amount', '&8377;100', '&8377;100'],
  ['Maximum amount', '&8377;5 Crores', '&8377;5 Crores'],
  ['Timings', 'Business hours', '24x7'],
  ['Holidays', 'Bank holidays', 'None'],
  ['Channel', 'NEFT / RTGS', 'IMPS'],
  ['API support', 'Yes', 'Yes'],
]

const states = [
  [<Badge color="gray">pending</Badge>, 'Settlement initiated, awaiting processing'],
  [<Badge color="blue">processing</Badge>, 'Settlement being processed by banking partner'],
  [<Badge color="green">completed</Badge>, 'Funds transferred to your bank account'],
  [<Badge color="red">failed</Badge>, 'Settlement could not be processed'],
]

export default function DocsSettlements() {
  return (
    <>
      <Eyebrow>Guides</Eyebrow>
      <h1 className="text-4xl font-bold tracking-tight text-gray-900 mb-2">Settlements</h1>
      <p className="text-lg text-gray-500 max-w-[36rem] mb-6 leading-relaxed">
        Transfer your available balance to your bank account. Settlements are processed on a T+1 cycle with instant settlement available for eligible merchants.
      </p>

      <Callout type="info" title="Feature Request">
        Instant Settlements is an on-demand feature. Contact support to get it activated on your account.
      </Callout>

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">Settlement types</h2>

      <Table headers={['Feature', 'Standard', 'Instant']} rows={settlementTypes} />

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">Check available balance</h2>

      <TerminalBlock>
        <span className="text-teal-400 select-none">$</span> curl https://api.transiq.com/v1/ledger/balance \<br />
        &nbsp;&nbsp;-H <span className="text-gray-200">"Authorization: sk_test_4fR3..."</span>
        <br /><br />
        <span className="text-green-400">→ 200 OK</span><br />
        <span className="text-gray-200">{'{'}</span><br />
        &nbsp;&nbsp;<span className="text-cyan-300">"available_balance"</span>: <span className="text-yellow-300">4528000</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"pending_balance"</span>: <span className="text-yellow-300">1200000</span><br />
        <span className="text-gray-200">{'}'}</span>
      </TerminalBlock>

      <Callout type="info" title="Balance explained">
        <strong>Available balance</strong> is what you can settle now. <strong>Pending balance</strong> is from recent payments that haven't cleared.
      </Callout>

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">Initiate a settlement</h2>

      <TerminalBlock>
        <span className="text-teal-400 select-none">$</span> curl -X POST https://api.transiq.com/v1/settlements \<br />
        &nbsp;&nbsp;-H <span className="text-gray-200">"Authorization: sk_test_4fR3..."</span>
        <br /><br />
        <span className="text-green-400">→ 201 Created</span><br />
        <span className="text-gray-200">{'{'}</span><br />
        &nbsp;&nbsp;<span className="text-cyan-300">"settlement_reference"</span>: <span className="text-green-300">"stl_tq_5hN8k3"</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"amount"</span>: <span className="text-yellow-300">4528000</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"status"</span>: <span className="text-green-300">"pending"</span><br />
        <span className="text-gray-200">{'}'}</span>
      </TerminalBlock>

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">Settlement states</h2>
      <Table headers={['State', 'Description']} rows={states} />

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">How settlements work</h2>
      <ol className="list-none p-0 my-4">
        {[
          <><strong>Payment captured</strong> — Payment confirmed. Amount added to pending balance.</>,
          <><strong>Clearing period</strong> — After T+1, funds move from pending to available.</>,
          <><strong>Initiate settlement</strong> — Create a request for your available balance.</>,
          <><strong>Processing</strong> — Transiq processes via NEFT/RTGS/IMPS.</>,
          <><strong>Completed</strong> — Funds arrive in your bank account. A <InlineCode>settlement.completed</InlineCode> webhook fires.</>,
        ].map((step, i) => (
          <li key={i} className="pl-10 pb-3 pt-3 border-b border-gray-100 relative m-0 last:border-0">
            <span className="absolute left-0 top-3 w-6 h-6 bg-teal-50 text-teal-600 font-mono text-xs font-semibold flex items-center justify-center rounded" style={{ fontFamily: "'JetBrains Mono', monospace" }}>{i + 1}</span>
            {step}
          </li>
        ))}
      </ol>

      <Callout type="success" title="Double-entry accounting">
        Every financial event creates two offsetting entries. Accounts: <InlineCode>CUSTOMER_RECEIVABLE</InlineCode>, <InlineCode>MERCHANT_PAYABLE</InlineCode>, <InlineCode>PLATFORM_REVENUE</InlineCode>, <InlineCode>TAX_PAYABLE</InlineCode>, <InlineCode>SETTLEMENT_ACCOUNT</InlineCode>.
      </Callout>

      <hr className="border-t border-gray-200 my-10" />
      <div className="flex justify-between flex-wrap gap-4 text-sm">
        <a href="/docs/refunds" className="text-teal-600 no-underline hover:underline">&larr; Refunds</a>
        <a href="/docs/webhooks" className="text-teal-600 no-underline hover:underline">Webhooks &rarr;</a>
      </div>
    </>
  )
}
