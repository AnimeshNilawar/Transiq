import { Eyebrow, TerminalBlock, Callout, Table, Badge, InlineCode } from './DocsComponents'

const events = [
  [<InlineCode>payment.succeeded</InlineCode>, 'Payment confirmed successfully', 'Payment ID, amount, currency'],
  [<InlineCode>payment.failed</InlineCode>, 'Payment declined or errored', 'Payment ID, failure code, message'],
  [<InlineCode>refund.succeeded</InlineCode>, 'Refund processed', 'Refund ID, payment ID, amount'],
  [<InlineCode>settlement.completed</InlineCode>, 'Settlement transferred to bank', 'Settlement ID, amount, bank ref'],
  [<InlineCode>chargeback.created</InlineCode>, 'Chargeback raised by bank', 'Chargeback ID, payment ID, amount'],
]

export default function DocsWebhooks() {
  return (
    <>
      <Eyebrow>Guides</Eyebrow>
      <h1 className="text-4xl font-bold tracking-tight text-gray-900 mb-2">Webhooks</h1>
      <p className="text-lg text-gray-500 max-w-[36rem] mb-6 leading-relaxed">
        Webhooks let your server receive real-time notifications when events happen in your account. Transiq signs every payload with HMAC-SHA256 so you can verify its authenticity.
      </p>

      <Callout type="info" title="Event-centric architecture">
        When an event occurs, a single <InlineCode>WebhookEvent</InlineCode> is created and delivered to every active endpoint. Each delivery is tracked independently with its own status and retry schedule.
      </Callout>

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">Register a webhook endpoint</h2>

      <TerminalBlock>
        <span className="text-gray-500 italic"># Register a webhook endpoint</span>{'\n'}
        <span className="text-teal-400 select-none">$</span> curl -X POST https://api.transiq.com/v1/webhooks \<br />
        &nbsp;&nbsp;-H <span className="text-gray-200">"Authorization: sk_test_4fR3..."</span> \<br />
        &nbsp;&nbsp;-d <span className="text-gray-200">{'{"url": "https://example.com/transiq-webhook", "version": 1}'}</span>
        <br /><br />
        <span className="text-green-400">→ 201 Created</span><br />
        <span className="text-gray-200">{'{'}</span><br />
        &nbsp;&nbsp;<span className="text-cyan-300">"id"</span>: <span className="text-green-300">"we_tq_8hN2k5"</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"url"</span>: <span className="text-green-300">"https://example.com/transiq-webhook"</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"secret"</span>: <span className="text-green-300">"whsec_a1b2c3d4e5f6..."</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"status"</span>: <span className="text-green-300">"active"</span><br />
        <span className="text-gray-200">{'}'}</span>
      </TerminalBlock>

      <Callout type="warning" title="Save your secret">
        The <InlineCode>secret</InlineCode> is returned only once. Save it securely — you'll need it to verify webhook signatures. If you lose it, create a new endpoint.
      </Callout>

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">Verify webhook signatures</h2>
      <p className="text-gray-600 mb-3">Every webhook includes a <InlineCode>X-Webhook-Signature</InlineCode> header. Verify it with your endpoint secret:</p>

      <TerminalBlock>
        <span className="text-gray-500 italic">// Verifying a webhook signature (Node.js)</span>{'\n'}
        <span className="text-gray-200">import crypto from 'node:crypto';</span>
        <br /><br />
        <span className="text-gray-200">const secret = <span className="text-green-300">'whsec_a1b2c3d4e5f6...'</span>;</span><br />
        <span className="text-gray-200">const signature = request.headers[<span className="text-green-300">'x-webhook-signature'</span>];</span><br />
        <span className="text-gray-200">const payload = JSON.stringify(request.body);</span>
        <br /><br />
        <span className="text-gray-200">const expected = crypto</span><br />
        &nbsp;&nbsp;<span className="text-gray-200">.createHmac(<span className="text-green-300">'sha256'</span>, secret)</span><br />
        &nbsp;&nbsp;<span className="text-gray-200">.update(payload)</span><br />
        &nbsp;&nbsp;<span className="text-gray-200">.digest(<span className="text-green-300">'hex'</span>);</span>
        <br /><br />
        <span className="text-gray-200">if (signature !== expected) {'{'}</span><br />
        &nbsp;&nbsp;<span className="text-gray-200">throw new Error(<span className="text-green-300">'Invalid signature'</span>);</span><br />
        <span className="text-gray-200">{'}'}</span>
      </TerminalBlock>

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">Webhook events</h2>
      <Table headers={['Event type', 'Trigger', 'Payload includes']} rows={events} />

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">Example payload</h2>

      <TerminalBlock>
        <span className="text-gray-500 italic">{'// payment.succeeded payload'}</span>{'\n'}
        <span className="text-gray-200">{'{'}</span><br />
        &nbsp;&nbsp;<span className="text-cyan-300">"event_type"</span>: <span className="text-green-300">"payment.succeeded"</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"reference"</span>: <span className="text-green-300">"pay_tq_9kM4n2"</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"occurred_at"</span>: <span className="text-green-300">"2026-07-22T01:35:00Z"</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"data"</span>: <span className="text-gray-200">{'{'}</span><br />
        &nbsp;&nbsp;&nbsp;&nbsp;<span className="text-cyan-300">"status"</span>: <span className="text-green-300">"succeeded"</span>,<br />
        &nbsp;&nbsp;&nbsp;&nbsp;<span className="text-cyan-300">"amount"</span>: <span className="text-yellow-300">129900</span><br />
        &nbsp;&nbsp;<span className="text-gray-200">{'}'}</span><br />
        <span className="text-gray-200">{'}'}</span>
      </TerminalBlock>

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">Delivery lifecycle</h2>
      <ol className="list-none p-0 my-4">
        {[
          <><strong>Event occurs</strong> — A payment succeeds, refund is processed, etc.</>,
          <><strong>WebhookEvent created</strong> — An event record with the full payload is stored.</>,
          <><strong>Deliveries created</strong> — One delivery per active endpoint.</>,
          <><strong>Delivery attempted</strong> — Transiq POSTs the signed payload to your URL.</>,
          <><strong>Success</strong> — Your server responds with 2xx. Delivery marked <InlineCode>delivered</InlineCode>.</>,
          <><strong>Retry on failure</strong> — Exponential backoff: 1min, 4min, 9min, 16min, 25min (5 attempts total).</>,
        ].map((step, i) => (
          <li key={i} className="pl-10 pb-3 pt-3 border-b border-gray-100 relative m-0 last:border-0">
            <span className="absolute left-0 top-3 w-6 h-6 bg-teal-50 text-teal-600 font-mono text-xs font-semibold flex items-center justify-center rounded" style={{ fontFamily: "'JetBrains Mono', monospace" }}>{i + 1}</span>
            {step}
          </li>
        ))}
      </ol>

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">Best practices</h2>
      <ul className="space-y-2 text-gray-600 ml-5 list-disc">
        <li><strong>Respond quickly</strong> — Acknowledge with <InlineCode>200 OK</InlineCode> immediately. Process asynchronously.</li>
        <li><strong>Verify signatures</strong> — Always check the <InlineCode>X-Webhook-Signature</InlineCode> header.</li>
        <li><strong>Idempotent processing</strong> — Use <InlineCode>event_id</InlineCode> to deduplicate deliveries.</li>
        <li><strong>Use HTTPS</strong> — Transiq only sends webhooks to HTTPS endpoints.</li>
        <li><strong>Monitor deliveries</strong> — Check the dashboard for failed deliveries.</li>
      </ul>

      <hr className="border-t border-gray-200 my-10" />
      <div className="flex justify-between flex-wrap gap-4 text-sm">
        <a href="/docs/settlements" className="text-teal-600 no-underline hover:underline">&larr; Settlements</a>
        <a href="/docs/api-reference" className="text-teal-600 no-underline hover:underline">API Reference &rarr;</a>
      </div>
    </>
  )
}
