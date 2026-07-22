import { lazy, Suspense, useEffect } from 'react'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { QueryClientProvider } from '@tanstack/react-query'
import { Toaster } from 'sonner'
import { queryClient } from '@/lib/queryClient'
import AuthProvider from '@/hooks/AuthProvider'
import { ProtectedRoute } from '@/routes/ProtectedRoute'
import { AdminRoute } from '@/routes/AdminRoute'
import { AppShell } from '@/components/layout/AppShell'
import { ErrorBoundary } from '@/components/shared/ErrorBoundary'

const MarketingPage = lazy(() => import('@/features/marketing/MarketingPage'))
const LoginPage = lazy(() => import('@/features/auth/LoginPage'))
const RegisterPage = lazy(() => import('@/features/auth/RegisterPage'))
const NotFoundPage = lazy(() => import('@/features/auth/NotFoundPage'))
const DashboardPage = lazy(() => import('@/features/dashboard/DashboardPage'))
const PaymentsPage = lazy(() => import('@/features/payments/PaymentsPage'))
const PaymentDetailPage = lazy(() => import('@/features/payments/PaymentDetailPage'))
const RefundsPage = lazy(() => import('@/features/refunds/RefundsPage'))
const RefundDetailPage = lazy(() => import('@/features/refunds/RefundDetailPage'))
const SettlementsPage = lazy(() => import('@/features/settlements/SettlementsPage'))
const SettlementDetailPage = lazy(() => import('@/features/settlements/SettlementDetailPage'))
const LedgerPage = lazy(() => import('@/features/ledger/LedgerPage'))
const ApiKeysPage = lazy(() => import('@/features/api-keys/ApiKeysPage'))
const WebhooksPage = lazy(() => import('@/features/webhooks/WebhooksPage'))
const WebhookDeliveriesPage = lazy(() => import('@/features/webhooks/WebhookDeliveriesPage'))
const WebhookDeliveryDetailPage = lazy(() => import('@/features/webhooks/WebhookDeliveryDetailPage'))
const SettingsPage = lazy(() => import('@/features/settings/SettingsPage'))
const CheckoutDemoPage = lazy(() => import('@/features/checkout/CheckoutDemoPage'))
const DocsLayout = lazy(() => import('@/features/docs/DocsLayout'))
const DocsQuickStart = lazy(() => import('@/features/docs/DocsQuickStart'))
const DocsAuthentication = lazy(() => import('@/features/docs/DocsAuthentication'))
const DocsPayments = lazy(() => import('@/features/docs/DocsPayments'))
const DocsRefunds = lazy(() => import('@/features/docs/DocsRefunds'))
const DocsSettlements = lazy(() => import('@/features/docs/DocsSettlements'))
const DocsWebhooks = lazy(() => import('@/features/docs/DocsWebhooks'))
const DocsApiReference = lazy(() => import('@/features/docs/DocsApiReference'))
const AdminDashboardPage = lazy(() => import('@/features/admin/AdminDashboardPage'))
const AdminMerchantsPage = lazy(() => import('@/features/admin/AdminMerchantsPage'))
const AdminMerchantDetailPage = lazy(() => import('@/features/admin/AdminMerchantDetailPage'))
const AdminPaymentsPage = lazy(() => import('@/features/admin/AdminPaymentsPage'))
const AdminPaymentDetailPage = lazy(() => import('@/features/admin/AdminPaymentDetailPage'))
const AdminRefundsPage = lazy(() => import('@/features/admin/AdminRefundsPage'))
const AdminSettlementsPage = lazy(() => import('@/features/admin/AdminSettlementsPage'))
const AdminUsersPage = lazy(() => import('@/features/admin/AdminUsersPage'))
const AdminApiKeysPage = lazy(() => import('@/features/admin/AdminApiKeysPage'))
const AdminWebhookDeliveriesPage = lazy(() => import('@/features/admin/AdminWebhookDeliveriesPage'))

function PageSpinner() {
  return (
    <div className="flex h-screen items-center justify-center bg-background">
      <div className="h-8 w-8 animate-spin rounded-full border-2 border-muted border-t-accent" />
    </div>
  )
}

function applyTheme() {
  try {
    const stored = localStorage.getItem('theme')
    if (stored === 'dark' || stored === 'light') {
      document.documentElement.classList.toggle('dark', stored === 'dark')
    } else if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
      document.documentElement.classList.add('dark')
    }
  } catch {}
}

export default function App() {
  useEffect(() => { applyTheme() }, [])

  return (
    <ErrorBoundary>
      <QueryClientProvider client={queryClient}>
        <AuthProvider>
          <BrowserRouter>
            <Suspense fallback={<PageSpinner />}>
              <Routes>
                <Route path="/" element={<MarketingPage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/checkout-demo" element={<CheckoutDemoPage />} />

                <Route element={<DocsLayout />}>
                  <Route path="/docs" element={<DocsQuickStart />} />
                  <Route path="/docs/authentication" element={<DocsAuthentication />} />
                  <Route path="/docs/payments" element={<DocsPayments />} />
                  <Route path="/docs/refunds" element={<DocsRefunds />} />
                  <Route path="/docs/settlements" element={<DocsSettlements />} />
                  <Route path="/docs/webhooks" element={<DocsWebhooks />} />
                  <Route path="/docs/api-reference" element={<DocsApiReference />} />
                </Route>

                <Route element={<ProtectedRoute />}>
                  <Route element={<AppShell />}>
                    <Route path="/dashboard" element={<DashboardPage />} />
                    <Route path="/payments" element={<PaymentsPage />} />
                    <Route
                      path="/payments/:paymentReference"
                      element={<PaymentDetailPage />}
                    />
                    <Route path="/refunds" element={<RefundsPage />} />
                    <Route
                      path="/refunds/:refundReference"
                      element={<RefundDetailPage />}
                    />
                    <Route path="/settlements" element={<SettlementsPage />} />
                    <Route
                      path="/settlements/:settlementReference"
                      element={<SettlementDetailPage />}
                    />
                    <Route path="/ledger" element={<LedgerPage />} />
                    <Route path="/api-keys" element={<ApiKeysPage />} />
                    <Route path="/webhooks" element={<WebhooksPage />} />
                    <Route
                      path="/webhooks/deliveries"
                      element={<WebhookDeliveriesPage />}
                    />
                    <Route
                      path="/webhooks/deliveries/:id"
                      element={<WebhookDeliveryDetailPage />}
                    />
                    <Route path="/settings" element={<SettingsPage />} />
                  </Route>
                </Route>

                <Route element={<AdminRoute />}>
                  <Route element={<AppShell />}>
                    <Route path="/admin" element={<AdminDashboardPage />} />
                    <Route path="/admin/merchants" element={<AdminMerchantsPage />} />
                    <Route path="/admin/merchants/:id" element={<AdminMerchantDetailPage />} />
                    <Route path="/admin/payments" element={<AdminPaymentsPage />} />
                    <Route path="/admin/payments/:reference" element={<AdminPaymentDetailPage />} />
                    <Route path="/admin/refunds" element={<AdminRefundsPage />} />
                    <Route path="/admin/settlements" element={<AdminSettlementsPage />} />
                    <Route path="/admin/users" element={<AdminUsersPage />} />
                    <Route path="/admin/api-keys" element={<AdminApiKeysPage />} />
                    <Route path="/admin/webhook-deliveries" element={<AdminWebhookDeliveriesPage />} />
                  </Route>
                </Route>

                <Route path="*" element={<NotFoundPage />} />
              </Routes>
            </Suspense>
          </BrowserRouter>
          <Toaster position="top-right" richColors />
        </AuthProvider>
      </QueryClientProvider>
    </ErrorBoundary>
  )
}
