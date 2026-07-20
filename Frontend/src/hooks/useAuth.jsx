import { createContext, useContext, useState, useCallback, useMemo, useEffect } from 'react'
import { login as apiLogin, register as apiRegister } from '@/api/auth'
import { getMe } from '@/api/me'
import { queryClient } from '@/lib/queryClient'

const AuthContext = createContext(null)

/**
 * @param {{ children: React.ReactNode }} props
 */
export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('jwt_token'))
  const [user, setUser] = useState(null)
  const [authLoading, setAuthLoading] = useState(true)

  const fetchUser = useCallback(async () => {
    try {
      const response = await getMe()
      setUser(response.data)
      return response.data
    } catch {
      setUser(null)
      return null
    }
  }, [])

  useEffect(() => {
    if (token) {
      setAuthLoading(true)
      fetchUser().finally(() => setAuthLoading(false))
    } else {
      setUser(null)
      setAuthLoading(false)
    }
  }, [token, fetchUser])

  const isAuthenticated = useMemo(() => {
    if (!token) return false
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      if (!payload || !payload.exp) return false
      return payload.exp * 1000 > Date.now()
    } catch {
      return false
    }
  }, [token])

  const login = useCallback(async (email, password) => {
    const response = await apiLogin({ email, password })
    const { accessToken } = response.data
    localStorage.setItem('jwt_token', accessToken)
    setToken(accessToken)
    return response.data
  }, [])

  const register = useCallback(async (data) => {
    return apiRegister(data)
  }, [])

  const logout = useCallback(() => {
    localStorage.removeItem('jwt_token')
    sessionStorage.removeItem('active_api_key')
    setToken(null)
    setUser(null)
    queryClient.clear()
  }, [])

  const value = useMemo(
    () => ({ token, user, isAuthenticated, authLoading, login, register, logout, refreshUser: fetchUser }),
    [token, user, isAuthenticated, authLoading, login, register, logout, fetchUser]
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

/**
 * @returns {{ token: string|null, user: import('@/api/me').MeResponse|null, isAuthenticated: boolean, authLoading: boolean, login: Function, register: Function, logout: Function, refreshUser: Function }}
 */
export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}
