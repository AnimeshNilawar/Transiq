import { NavLink } from 'react-router-dom'
import {
  LayoutDashboard,
  CreditCard,
  RotateCcw,
  Building2,
  BookOpen,
  Key,
  Webhook,
  Send,
  Settings,
  Shield,
  Users,
  Receipt,
  ArrowLeftRight,
} from 'lucide-react'
import { cn } from '@/lib/utils'
import useAuth from '@/hooks/useAuth'

const navItems = [
  { to: '/', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/payments', label: 'Payments', icon: CreditCard },
  { to: '/refunds', label: 'Refunds', icon: RotateCcw },
  { to: '/settlements', label: 'Settlements', icon: Building2 },
  { to: '/ledger', label: 'Ledger', icon: BookOpen },
  { to: '/api-keys', label: 'API Keys', icon: Key },
  { to: '/webhooks', label: 'Webhooks', icon: Webhook },
  { to: '/webhooks/deliveries', label: 'Deliveries', icon: Send },
  { to: '/settings', label: 'Settings', icon: Settings },
]

export function Sidebar({ open, onClose }) {
  const { user } = useAuth()
  const isAdmin = user?.role === 'PLATFORM_ADMIN'

  return (
    <>
      {open && (
        <div
          className="fixed inset-0 z-40 bg-black/50 lg:hidden"
          onClick={onClose}
        />
      )}

      <aside
        className={cn(
          'fixed inset-y-0 left-0 z-50 flex w-64 flex-col border-r border-sidebar-border bg-sidebar transition-transform duration-200 lg:static lg:translate-x-0',
          open ? 'translate-x-0' : '-translate-x-full'
        )}
      >
        <div className="flex h-14 items-center border-b border-sidebar-border px-4">
          <span className="text-lg font-bold tracking-tight text-sidebar-foreground">Transiq</span>
        </div>
        <nav className="flex-1 space-y-0.5 p-2">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.to === '/'}
              onClick={onClose}
              className={({ isActive }) =>
                cn(
                  'flex items-center gap-3 rounded-r-md px-3 py-2 text-sm font-medium transition-colors',
                  isActive
                    ? 'border-l-2 border-accent bg-sidebar-accent text-sidebar-accent-foreground'
                    : 'border-l-2 border-transparent text-sidebar-foreground/70 hover:bg-sidebar-accent/50 hover:text-sidebar-foreground'
                )
              }
            >
              <item.icon className="h-4 w-4" />
              {item.label}
            </NavLink>
          ))}
          {isAdmin && (
            <>
              <div className="mt-4 mb-1 px-3 text-xs font-semibold uppercase tracking-wider text-sidebar-foreground/50">
                Admin
              </div>
              <NavLink
                to="/admin"
                end
                onClick={onClose}
                className={({ isActive }) =>
                  cn(
                    'flex items-center gap-3 rounded-r-md px-3 py-2 text-sm font-medium transition-colors',
                    isActive
                      ? 'border-l-2 border-accent bg-sidebar-accent text-sidebar-accent-foreground'
                      : 'border-l-2 border-transparent text-sidebar-foreground/70 hover:bg-sidebar-accent/50 hover:text-sidebar-foreground'
                  )
                }
              >
                <Shield className="h-4 w-4" />
                Dashboard
              </NavLink>
              <NavLink
                to="/admin/merchants"
                onClick={onClose}
                className={({ isActive }) =>
                  cn(
                    'flex items-center gap-3 rounded-r-md px-3 py-2 text-sm font-medium transition-colors',
                    isActive
                      ? 'border-l-2 border-accent bg-sidebar-accent text-sidebar-accent-foreground'
                      : 'border-l-2 border-transparent text-sidebar-foreground/70 hover:bg-sidebar-accent/50 hover:text-sidebar-foreground'
                  )
                }
              >
                <Building2 className="h-4 w-4" />
                Merchants
              </NavLink>
              <NavLink
                to="/admin/payments"
                onClick={onClose}
                className={({ isActive }) =>
                  cn(
                    'flex items-center gap-3 rounded-r-md px-3 py-2 text-sm font-medium transition-colors',
                    isActive
                      ? 'border-l-2 border-accent bg-sidebar-accent text-sidebar-accent-foreground'
                      : 'border-l-2 border-transparent text-sidebar-foreground/70 hover:bg-sidebar-accent/50 hover:text-sidebar-foreground'
                  )
                }
              >
                <CreditCard className="h-4 w-4" />
                Payments
              </NavLink>
              <NavLink
                to="/admin/refunds"
                onClick={onClose}
                className={({ isActive }) =>
                  cn(
                    'flex items-center gap-3 rounded-r-md px-3 py-2 text-sm font-medium transition-colors',
                    isActive
                      ? 'border-l-2 border-accent bg-sidebar-accent text-sidebar-accent-foreground'
                      : 'border-l-2 border-transparent text-sidebar-foreground/70 hover:bg-sidebar-accent/50 hover:text-sidebar-foreground'
                  )
                }
              >
                <RotateCcw className="h-4 w-4" />
                Refunds
              </NavLink>
              <NavLink
                to="/admin/settlements"
                onClick={onClose}
                className={({ isActive }) =>
                  cn(
                    'flex items-center gap-3 rounded-r-md px-3 py-2 text-sm font-medium transition-colors',
                    isActive
                      ? 'border-l-2 border-accent bg-sidebar-accent text-sidebar-accent-foreground'
                      : 'border-l-2 border-transparent text-sidebar-foreground/70 hover:bg-sidebar-accent/50 hover:text-sidebar-foreground'
                  )
                }
              >
                <ArrowLeftRight className="h-4 w-4" />
                Settlements
              </NavLink>
              <NavLink
                to="/admin/users"
                onClick={onClose}
                className={({ isActive }) =>
                  cn(
                    'flex items-center gap-3 rounded-r-md px-3 py-2 text-sm font-medium transition-colors',
                    isActive
                      ? 'border-l-2 border-accent bg-sidebar-accent text-sidebar-accent-foreground'
                      : 'border-l-2 border-transparent text-sidebar-foreground/70 hover:bg-sidebar-accent/50 hover:text-sidebar-foreground'
                  )
                }
              >
                <Users className="h-4 w-4" />
                Users
              </NavLink>
              <NavLink
                to="/admin/api-keys"
                onClick={onClose}
                className={({ isActive }) =>
                  cn(
                    'flex items-center gap-3 rounded-r-md px-3 py-2 text-sm font-medium transition-colors',
                    isActive
                      ? 'border-l-2 border-accent bg-sidebar-accent text-sidebar-accent-foreground'
                      : 'border-l-2 border-transparent text-sidebar-foreground/70 hover:bg-sidebar-accent/50 hover:text-sidebar-foreground'
                  )
                }
              >
                <Key className="h-4 w-4" />
                API Keys
              </NavLink>
              <NavLink
                to="/admin/webhook-deliveries"
                onClick={onClose}
                className={({ isActive }) =>
                  cn(
                    'flex items-center gap-3 rounded-r-md px-3 py-2 text-sm font-medium transition-colors',
                    isActive
                      ? 'border-l-2 border-accent bg-sidebar-accent text-sidebar-accent-foreground'
                      : 'border-l-2 border-transparent text-sidebar-foreground/70 hover:bg-sidebar-accent/50 hover:text-sidebar-foreground'
                  )
                }
              >
                <Send className="h-4 w-4" />
                Webhooks
              </NavLink>
            </>
          )}
        </nav>
      </aside>
    </>
  )
}
