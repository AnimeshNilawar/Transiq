export function Eyebrow({ children }) {
  return <span className="text-[11px] font-semibold uppercase tracking-widest text-teal-600 mb-2 block">{children}</span>
}

export function Callout({ type, title, children }) {
  const styles = {
    info: 'bg-blue-50 border-l-4 border-blue-600',
    warning: 'bg-amber-50 border-l-4 border-amber-600',
    danger: 'bg-red-50 border-l-4 border-red-600',
    success: 'bg-green-50 border-l-4 border-green-600',
  }
  const titleColors = {
    info: 'text-blue-700',
    warning: 'text-amber-700',
    danger: 'text-red-700',
    success: 'text-green-700',
  }
  return (
    <div className={`${styles[type]} rounded-lg p-4 my-5 text-sm leading-relaxed`}>
      <div className={`font-semibold text-[13px] uppercase tracking-wider mb-1 flex items-center gap-1.5 ${titleColors[type]}`}>
        {title}
      </div>
      <div className="text-gray-700">{children}</div>
    </div>
  )
}

export function TerminalBlock({ children }) {
  return (
    <pre className="bg-gray-900 text-gray-200 rounded-lg p-4 pr-6 text-sm leading-relaxed overflow-x-auto my-4 font-mono whitespace-pre-wrap" style={{ fontFamily: "'JetBrains Mono', monospace" }}>
      {children}
    </pre>
  )
}

export function Table({ headers, rows }) {
  return (
    <div className="bg-white border border-gray-200 rounded-lg overflow-hidden my-4">
      <table className="w-full text-sm border-collapse">
        <thead>
          <tr>
            {headers.map((h, i) => (
              <th key={i} className="text-left text-[11px] font-semibold uppercase tracking-wider text-gray-500 bg-gray-50 px-4 py-2.5 border-b border-gray-200">{h}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, i) => (
            <tr key={i}>
              {row.map((cell, j) => (
                <td key={j} className="px-4 py-2.5 border-b border-gray-100 align-top text-gray-800">{cell}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export function Badge({ children, color = 'green' }) {
  const colors = {
    green: 'bg-green-50 text-green-700',
    blue: 'bg-blue-50 text-blue-700',
    amber: 'bg-amber-50 text-amber-700',
    red: 'bg-red-50 text-red-700',
    gray: 'bg-gray-100 text-gray-600',
  }
  return <span className={`inline-block font-mono text-xs font-medium px-1.5 py-0.5 rounded ${colors[color]}`}>{children}</span>
}

export function MethodBadge({ method }) {
  const colors = {
    GET: 'bg-blue-50 text-blue-700',
    POST: 'bg-green-50 text-green-700',
    DELETE: 'bg-red-50 text-red-700',
  }
  return <span className={`font-mono text-xs font-semibold px-1.5 py-0.5 rounded ${colors[method] || colors.GET}`}>{method}</span>
}

export function StatusBadge({ status }) {
  const colors = {
    succeeded: 'bg-green-50 text-green-700',
    failed: 'bg-red-50 text-red-700',
    processing: 'bg-blue-50 text-blue-700',
    pending: 'bg-gray-100 text-gray-600',
    created: 'bg-gray-100 text-gray-700',
  }
  return <span className={`font-mono text-xs font-medium px-1.5 py-0.5 rounded ${colors[status] || colors.pending}`}>{status}</span>
}

export function CardGrid({ children }) {
  return <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 my-4">{children}</div>
}

export function CardLink({ to, title, desc }) {
  return (
    <Link to={to} className="block bg-white border border-gray-200 rounded-lg p-5 no-underline hover:border-teal-300 hover:shadow-[0_0_0_3px_rgba(13,148,136,0.08)] transition-all">
      <h3 className="text-base font-semibold text-gray-900 mb-1">{title}</h3>
      <p className="text-sm text-gray-600 m-0 leading-relaxed">{desc}</p>
    </Link>
  )
}

import { Link } from 'react-router-dom'

export function InlineCode({ children }) {
  return <code className="bg-teal-50 text-teal-700 text-[13px] font-medium px-1 py-0.5 rounded" style={{ fontFamily: "'JetBrains Mono', monospace" }}>{children}</code>
}

export function ParamTable({ params }) {
  return (
    <Table
      headers={['Parameter', 'Type', 'Description']}
      rows={params.map((p) => [
        <span className="font-mono text-[13px] font-medium text-gray-900" style={{ fontFamily: "'JetBrains Mono', monospace", whiteSpace: 'nowrap' }}>{p.name}</span>,
        <span className="text-xs text-gray-500 font-mono">{p.type}</span>,
        <span className="text-sm text-gray-700">{p.desc}</span>,
      ])}
    />
  )
}

export function Steps({ children }) {
  return <ol className="list-none p-0 my-4 m-0">{children}</ol>
}

export function Step({ children }) {
  return (
    <li className="pl-10 pb-3 pt-3 border-b border-gray-100 relative m-0 last:border-0">
      {children}
    </li>
  )
}
