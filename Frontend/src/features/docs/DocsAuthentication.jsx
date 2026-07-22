import { Eyebrow, TerminalBlock, Callout, Table, Badge, InlineCode } from './DocsComponents'

const keyTypes = [
  [<Badge color="green">Secret</Badge>, <InlineCode>sk_</InlineCode>, 'Full read/write access', 'Server-side integration'],
  [<Badge color="blue">Publishable</Badge>, <InlineCode>pk_</InlineCode>, 'Create payments and retrieve state', 'Client-side checkout'],
  [<Badge color="amber">Restricted</Badge>, <InlineCode>rk_</InlineCode>, 'Read-only access', 'Monitoring and reporting'],
]

const envs = [
  [<Badge color="amber">Test</Badge>, <InlineCode>https://api.transiq.com/v1</InlineCode>, 'Development and testing'],
  [<Badge color="green">Live</Badge>, <InlineCode>https://api.transiq.com/v1</InlineCode>, 'Real payment processing'],
]

export default function DocsAuthentication() {
  return (
    <>
      <Eyebrow>Getting Started</Eyebrow>
      <h1 className="text-4xl font-bold tracking-tight text-gray-900 mb-2">Authentication</h1>
      <p className="text-lg text-gray-500 max-w-[36rem] mb-6 leading-relaxed">
        Transiq uses API keys to authenticate requests. You can manage keys from the dashboard. Each key has a type that determines its capabilities.
      </p>

      <Callout type="warning" title="Keep your keys safe">
        API keys carry the same privileges as your account. Never share them publicly or embed them in client-side code. Use publishable keys for frontend integration.
      </Callout>

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">API key types</h2>

      <Table headers={['Type', 'Prefix', 'Capabilities', 'Use case']} rows={keyTypes} />

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">Authenticating requests</h2>
      <p className="text-gray-600 mb-3">Send your API key in the <InlineCode>Authorization</InlineCode> header. The <InlineCode>Bearer</InlineCode> prefix is optional.</p>

      <TerminalBlock>
        <span className="text-gray-500 italic"># Authenticate with a secret key</span>{'\n'}
        <span className="text-teal-400 select-none">$</span> curl https://api.transiq.com/v1/payments/pay_tq_abc123 \<br />
        &nbsp;&nbsp;-H <span className="text-gray-200">"Authorization: <span className="text-teal-300">sk_test_4fR3Xn9pW2</span>"</span>
      </TerminalBlock>

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">Managing API keys</h2>
      <p className="text-gray-600 mb-3">Create, list, rotate, and revoke keys from the dashboard or via the API. The full key value is shown only once on creation.</p>

      <TerminalBlock>
        <span className="text-gray-500 italic"># Rotate a compromised key</span>{'\n'}
        <span className="text-teal-400 select-none">$</span> curl -X POST https://api.transiq.com/v1/api-keys/key_tq_abc123/rotate \<br />
        &nbsp;&nbsp;-H <span className="text-gray-200">"Authorization: sk_test_4fR3..."</span>
        <br /><br />
        <span className="text-green-400">→ 200 OK</span><br />
        <span className="text-gray-200">{'{'}</span><br />
        &nbsp;&nbsp;<span className="text-cyan-300">"key"</span>: <span className="text-green-300">"sk_test_8mN2Yp7qW4"</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"type"</span>: <span className="text-green-300">"secret"</span>,<br />
        &nbsp;&nbsp;<span className="text-cyan-300">"environment"</span>: <span className="text-green-300">"test"</span><br />
        <span className="text-gray-200">{'}'}</span>
      </TerminalBlock>

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">Environments</h2>

      <Table headers={['Environment', 'Base URL', 'Purpose']} rows={envs} />

      <Callout type="info" title="Test mode">
        In test mode, no real money moves. Use any card number to simulate successful or failed payments.
      </Callout>

      <h2 className="text-xl font-semibold text-gray-900 mt-8 mb-4">Idempotency</h2>
      <p className="text-gray-600 mb-3">All POST requests require an <InlineCode>Idempotency-Key</InlineCode> header. This ensures retrying a request produces the same result, preventing duplicate charges.</p>

      <TerminalBlock>
        <span className="text-gray-500 italic"># Same key always returns the same result</span>{'\n'}
        <span className="text-teal-400 select-none">$</span> curl -X POST https://api.transiq.com/v1/payments \<br />
        &nbsp;&nbsp;-H <span className="text-gray-200">"Authorization: sk_test_4fR3..."</span> \<br />
        &nbsp;&nbsp;-H <span className="text-gray-200">"Idempotency-Key: <span className="text-teal-300">550e8400-e29b-41d4</span>"</span> \<br />
        &nbsp;&nbsp;-d <span className="text-gray-200">{'{"amount": 4200, "currency": "INR"}'}</span>
      </TerminalBlock>

      <Callout type="warning" title="Idempotency window">
        Idempotency keys are stored for 24 hours. After that, the same key can be reused for a new request.
      </Callout>

      <hr className="border-t border-gray-200 my-10" />
      <div className="flex justify-between flex-wrap gap-4 text-sm">
        <a href="/docs" className="text-teal-600 no-underline hover:underline">&larr; Quick Start</a>
        <a href="/docs/payments" className="text-teal-600 no-underline hover:underline">Payments &rarr;</a>
      </div>
    </>
  )
}
