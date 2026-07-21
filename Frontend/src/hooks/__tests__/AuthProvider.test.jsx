import { render, screen } from '@testing-library/react'
import { useContext } from 'react'
import AuthProvider from '../AuthProvider'
import { AuthContext } from '@/lib/auth-context'

vi.mock('@/api/auth', () => ({
  login: vi.fn(),
  register: vi.fn(),
}))

vi.mock('@/api/me', () => ({
  getMe: vi.fn(),
}))

vi.mock('@/lib/queryClient', () => ({
  queryClient: { clear: vi.fn() },
}))

function TestConsumer() {
  const ctx = useContext(AuthContext)
  return (
    <div>
      <span data-testid="is-authenticated">{String(!!ctx?.isAuthenticated)}</span>
      <span data-testid="has-login">{String(typeof ctx?.login === 'function')}</span>
      <span data-testid="has-logout">{String(typeof ctx?.logout === 'function')}</span>
      <span data-testid="has-register">{String(typeof ctx?.register === 'function')}</span>
    </div>
  )
}

describe('AuthProvider', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  it('renders children', () => {
    render(
      <AuthProvider>
        <div>Test child</div>
      </AuthProvider>
    )
    expect(screen.getByText('Test child')).toBeInTheDocument()
  })

  it('provides auth context with login/logout/register functions', async () => {
    render(
      <AuthProvider>
        <TestConsumer />
      </AuthProvider>
    )
    await screen.findByTestId('has-login')
    expect(screen.getByTestId('has-login').textContent).toBe('true')
    expect(screen.getByTestId('has-logout').textContent).toBe('true')
    expect(screen.getByTestId('has-register').textContent).toBe('true')
  })

  it('checks JWT expiration on mount with expired token', async () => {
    const payload = { exp: Math.floor(Date.now() / 1000) - 100 }
    const token = `header.${btoa(JSON.stringify(payload))}.sig`
    localStorage.setItem('jwt_token', token)

    render(
      <AuthProvider>
        <TestConsumer />
      </AuthProvider>
    )
    await screen.findByTestId('is-authenticated')
    expect(screen.getByTestId('is-authenticated').textContent).toBe('false')
  })
})
