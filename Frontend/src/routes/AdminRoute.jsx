import { Navigate, Outlet } from 'react-router-dom'
import useAuth from '@/hooks/useAuth'

export function AdminRoute() {
  const { user, isAuthenticated, authLoading } = useAuth()

  if (authLoading) return null

  if (!isAuthenticated) return <Navigate to="/login" replace />

  if (user?.role !== 'PLATFORM_ADMIN') return <Navigate to="/" replace />

  return <Outlet />
}
