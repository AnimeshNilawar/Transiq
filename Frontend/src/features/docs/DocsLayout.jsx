import { NavLink, Outlet } from 'react-router-dom'

const nav = [
  {
    title: 'Getting Started',
    links: [
      { to: '/docs', label: 'Quick Start', end: true },
      { to: '/docs/authentication', label: 'Authentication' },
    ],
  },
  {
    title: 'Guides',
    links: [
      { to: '/docs/payments', label: 'Payments' },
      { to: '/docs/refunds', label: 'Refunds' },
      { to: '/docs/settlements', label: 'Settlements' },
      { to: '/docs/webhooks', label: 'Webhooks' },
    ],
  },
  {
    title: 'Reference',
    links: [
      { to: '/docs/api-reference', label: 'API Reference' },
    ],
  },
]

export default function DocsLayout() {
  return (
    <div className="flex min-h-screen bg-[#f9fafb]">
      <aside className="w-60 shrink-0 bg-white border-r border-gray-200 sticky top-0 h-screen overflow-y-auto p-6">
        <NavLink to="/docs" className="text-lg font-bold text-gray-900 flex items-center gap-2 mb-6 no-underline hover:no-underline">
          <span className="inline-flex w-6 h-6 bg-teal-600 text-white text-[10px] font-bold items-center justify-center rounded" style={{ fontFamily: "'JetBrains Mono', monospace" }}>TQ</span>
          Transiq
        </NavLink>

        {nav.map((section) => (
          <div key={section.title} className="mb-4">
            <div className="text-[11px] font-semibold uppercase tracking-wider text-gray-400 mb-1.5 px-1.5">
              {section.title}
            </div>
            {section.links.map((link) => (
              <NavLink
                key={link.to}
                to={link.to}
                end={link.end}
                className={({ isActive }) =>
                  `block text-sm py-1.5 px-1.5 rounded-md no-underline transition-colors ${
                    isActive
                      ? 'bg-teal-50 text-teal-600 font-medium'
                      : 'text-gray-600 hover:text-teal-600 hover:bg-teal-50'
                  }`
                }
              >
                {link.label}
              </NavLink>
            ))}
          </div>
        ))}
      </aside>

      <main className="flex-1 max-w-[54rem] px-10 py-12 pb-24">
        <Outlet />
      </main>
    </div>
  )
}
