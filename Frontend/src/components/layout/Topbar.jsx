import { useNavigate } from 'react-router-dom'
import { LogOut } from 'lucide-react'
import { useAuth } from '@/hooks/useAuth'

export function Topbar() {
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
    <header className="flex h-14 items-center justify-between border-b bg-background px-6">
      <div className="flex items-center gap-4">
        <div className="text-sm text-muted-foreground">Welcome,</div>
        <div className="text-sm font-medium">
          {displayName}
          {roleDisplay && (
            <span className="text-xs text-muted-foreground ml-1">{roleDisplay}</span>
          )}
        </div>
      </div>

      <div className="flex items-center gap-3">
        {user?.merchant?.businessName && (
          <span className="text-sm text-muted-foreground">
            {user.merchant.businessName}
          </span>
        )}

        <button
          onClick={handleLogout}
          className="inline-flex items-center gap-2 rounded-md border px-3 py-1.5 text-sm hover:bg-accent transition-colors text-muted-foreground"
        >
          <LogOut className="h-3.5 w-3.5" />
          Logout
        </button>
      </div>
    </header>
  )
}
