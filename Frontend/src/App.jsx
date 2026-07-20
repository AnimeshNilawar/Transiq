import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { QueryClientProvider } from '@tanstack/react-query'
import { Toaster } from 'sonner'
import { queryClient } from '@/lib/queryClient'
import { AuthProvider } from '@/hooks/useAuth'
import { ProtectedRoute } from '@/routes/ProtectedRoute'
import { AppShell } from '@/components/layout/AppShell'

import { LoginPage } from '@/features/auth/LoginPage'
import { RegisterPage } from '@/features/auth/RegisterPage'
import { DashboardPage } from '@/features/dashboard/DashboardPage'
import { PaymentsPage } from '@/features/payments/PaymentsPage'
import { PaymentDetailPage } from '@/features/payments/PaymentDetailPage'
import { RefundsPage } from '@/features/refunds/RefundsPage'
import { RefundDetailPage } from '@/features/refunds/RefundDetailPage'
import { SettlementsPage } from '@/features/settlements/SettlementsPage'
import { SettlementDetailPage } from '@/features/settlements/SettlementDetailPage'
import { LedgerPage } from '@/features/ledger/LedgerPage'
import { ApiKeysPage } from '@/features/api-keys/ApiKeysPage'
import { WebhooksPage } from '@/features/webhooks/WebhooksPage'
import { WebhookDeliveriesPage } from '@/features/webhooks/WebhookDeliveriesPage'
import { WebhookDeliveryDetailPage } from '@/features/webhooks/WebhookDeliveryDetailPage'
import { SettingsPage } from '@/features/settings/SettingsPage'
import { CheckoutDemoPage } from '@/features/checkout/CheckoutDemoPage'

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/checkout-demo" element={<CheckoutDemoPage />} />

            <Route element={<ProtectedRoute />}>
              <Route element={<AppShell />}>
                <Route path="/" element={<DashboardPage />} />
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
          </Routes>
        </BrowserRouter>
        <Toaster position="top-right" richColors />
      </AuthProvider>
    </QueryClientProvider>
  )
}
