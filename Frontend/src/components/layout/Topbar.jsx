import { useNavigate } from 'react-router-dom'
import { LogOut, Menu, Sun, Moon } from 'lucide-react'
import useAuth from '@/hooks/useAuth'

export function Topbar({ onMenuToggle, theme, onThemeToggle }) {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const displayName = user
    ? `${user.firstName || ''} ${user.lastName || ''}`.trim() || user.email
    : 'Merchant'

  const roleDisplay = user?.role ? ` (${user.role})` : ''

  return (
    <header className="flex h-14 items-center justify-between border-b border-border bg-card px-6">
      <div className="flex items-center gap-4">
        <button
          onClick={onMenuToggle}
          className="inline-flex items-center justify-center rounded-md p-2 text-muted-foreground hover:bg-muted transition-colors lg:hidden"
        >
          <Menu className="h-5 w-5" />
        </button>
        <div className="text-sm text-muted-foreground">Welcome,</div>
        <div className="text-sm font-medium text-card-foreground">
          {displayName}
          {roleDisplay && (
            <span className="text-xs text-muted-foreground ml-1">{roleDisplay}</span>
          )}
        </div>
      </div>

      <div className="flex items-center gap-3">
        {user?.merchant?.businessName && (
          <span className="text-sm text-muted-foreground hidden sm:inline">
            {user.merchant.businessName}
          </span>
        )}

        <button
          onClick={onThemeToggle}
          className="inline-flex items-center justify-center rounded-md border border-border p-2 text-muted-foreground hover:bg-muted transition-colors"
          title={theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode'}
        >
          {theme === 'dark' ? <Sun className="h-4 w-4" /> : <Moon className="h-4 w-4" />}
        </button>

        <button
          onClick={handleLogout}
          className="inline-flex items-center gap-2 rounded-md border border-border px-3 py-1.5 text-sm text-muted-foreground hover:bg-muted transition-colors"
        >
          <LogOut className="h-3.5 w-3.5" />
          Logout
        </button>
      </div>
    </header>
  )
}
