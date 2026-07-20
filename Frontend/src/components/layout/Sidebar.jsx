import { NavLink } from 'react-router-dom'
import {
  LayoutDashboard,
  CreditCard,
  RotateCcw,
  Building2,
  BookOpen,
  Key,
  Webhook,
  Settings,
} from 'lucide-react'
import { cn } from '@/lib/utils'

const navItems = [
  { to: '/', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/payments', label: 'Payments', icon: CreditCard },
  { to: '/refunds', label: 'Refunds', icon: RotateCcw },
  { to: '/settlements', label: 'Settlements', icon: Building2 },
  { to: '/ledger', label: 'Ledger', icon: BookOpen },
  { to: '/api-keys', label: 'API Keys', icon: Key },
  { to: '/webhooks', label: 'Webhooks', icon: Webhook },
  { to: '/settings', label: 'Settings', icon: Settings },
]

export function Sidebar() {
  return (
    <aside className="flex h-full w-64 flex-col border-r bg-sidebar">
      <div className="flex h-14 items-center border-b px-4">
        <span className="text-lg font-bold tracking-tight">Transiq</span>
      </div>
      <nav className="flex-1 space-y-1 p-2">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.to === '/'}
            className={({ isActive }) =>
              cn(
                'flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors',
                isActive
                  ? 'bg-sidebar-accent text-sidebar-accent-foreground'
                  : 'text-sidebar-foreground/70 hover:bg-sidebar-accent/50 hover:text-sidebar-foreground'
              )
            }
          >
            <item.icon className="h-4 w-4" />
            {item.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  )
}
