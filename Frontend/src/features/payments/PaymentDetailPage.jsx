import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { usePaymentDetail } from '@/hooks/usePayments'
import { useDashboardCreateRefund } from '@/hooks/useRefunds'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { DetailSkeleton } from '@/components/shared/LoadingSkeleton'
import { Item } from '@/components/shared/Item'
import { formatAmount } from '@/lib/utils'
import { format } from 'date-fns'
import { ArrowLeft, RotateCcw, RefreshCw, Loader2, CreditCard, Clock, ChevronDown, ChevronUp } from 'lucide-react'

const REFUND_REASONS = [
  { value: 'REQUESTED_BY_CUSTOMER', label: 'Requested by customer' },
  { value: 'DUPLICATE_PAYMENT', label: 'Duplicate payment' },
  { value: 'FRAUDULENT', label: 'Fraudulent' },
  { value: 'PRODUCT_UNAVAILABLE', label: 'Product unavailable' },
  { value: 'OTHER', label: 'Other' },
]

export default function PaymentDetailPage() {
  const { paymentReference } = useParams()
  const navigate = useNavigate()
  const { data: payment, isLoading } = usePaymentDetail(paymentReference)
  const refundMutation = useDashboardCreateRefund()
  const [showRefundModal, setShowRefundModal] = useState(false)
  const [refundAmount, setRefundAmount] = useState('')
  const [refundReason, setRefundReason] = useState('REQUESTED_BY_CUSTOMER')
  const [refundError, setRefundError] = useState('')
  const [showAttempts, setShowAttempts] = useState(false)

  const refundableAmount = payment
    ? (payment.amount || 0) - (payment.refundedAmount || 0)
    : 0

  const handleRefund = async (e) => {
    e.preventDefault()
    setRefundError('')

    const amountInPaise = Math.round(parseFloat(refundAmount) * 100)
    if (!amountInPaise || amountInPaise <= 0) {
      setRefundError('Enter a valid refund amount')
      return
    }
    if (amountInPaise > refundableAmount) {
      setRefundError(`Refund amount exceeds refundable balance of ${(refundableAmount / 100).toFixed(2)}`)
      return
    }

    try {
      await refundMutation.mutateAsync({
        paymentReference: payment.paymentReference,
        amount: amountInPaise,
        reason: refundReason,
      })
      setShowRefundModal(false)
      setRefundAmount('')
      setRefundReason('REQUESTED_BY_CUSTOMER')
    } catch {
      // Error handled by interceptor
    }
  }

  if (isLoading) {
    return (
      <div className="space-y-6">
        <DetailSkeleton />
      </div>
    )
  }

  if (!payment) {
    return (
      <div className="text-center py-12">
        <p className="text-muted-foreground">Payment not found</p>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <button
            onClick={() => navigate('/payments')}
            className="inline-flex items-center justify-center rounded-md border border-border p-2 hover:bg-muted transition-colors text-muted-foreground"
          >
            <ArrowLeft className="h-4 w-4" />
          </button>
          <div>
            <h1 className="text-2xl font-bold tracking-tight text-foreground">
              Payment <span className="font-mono">{payment.paymentReference}</span>
            </h1>
            <p className="text-sm text-muted-foreground font-mono">{payment.id}</p>
          </div>
        </div>
        <div className="flex items-center gap-2">
          {payment.status === 'FAILED' && (
            <button
              onClick={() => navigate('/payments')}
              className="inline-flex items-center gap-2 rounded-md border border-border px-4 py-2 text-sm font-medium text-card-foreground hover:bg-muted transition-colors"
            >
              <RefreshCw className="h-4 w-4" />
              Retry
            </button>
          )}
          {refundableAmount > 0 && payment.status === 'SUCCEEDED' && (
            <button
              onClick={() => {
                setRefundAmount((refundableAmount / 100).toFixed(2))
                setShowRefundModal(true)
              }}
              className="inline-flex items-center gap-2 rounded-md bg-accent px-4 py-2 text-sm font-medium text-accent-foreground hover:bg-accent/90 transition-colors"
            >
              <RotateCcw className="h-4 w-4" />
              Refund
            </button>
          )}
        </div>
      </div>

      <div className="bg-card rounded-lg border border-border p-6 space-y-4">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-card-foreground">Details</h2>
          <StatusBadge status={payment.status} />
        </div>
        <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
          <Item label="Amount" value={formatAmount(payment.amount, payment.currency)} mono />
          <Item label="Currency" value={payment.currency} mono />
          <Item label="Order ID" value={payment.orderId} mono />
          <Item label="Customer Email" value={payment.customerEmail} />
          <Item label="Customer Name" value={payment.customerName} />
          <Item label="Description" value={payment.description} />
          <Item
            label="Created"
            value={format(new Date(payment.createdAt), 'MMM d, yyyy HH:mm:ss')}
          />
        </div>
      </div>

      {refundableAmount > 0 && (
        <div className="bg-card rounded-lg border border-border p-6 space-y-3">
          <h2 className="text-lg font-semibold text-card-foreground">Refund Progress</h2>
          <div className="space-y-2">
            <div className="flex justify-between text-sm">
              <span className="text-muted-foreground">Refunded</span>
              <span className="font-mono tabular-nums text-card-foreground">
                {formatAmount(payment.refundedAmount || 0, payment.currency)} / {formatAmount(payment.amount, payment.currency)}
              </span>
            </div>
            <div className="w-full bg-muted rounded-full h-2">
              <div
                className="bg-accent rounded-full h-2 transition-all"
                style={{
                  width: `${Math.min(((payment.refundedAmount || 0) / payment.amount) * 100, 100)}%`,
                }}
              />
            </div>
            <p className="text-xs text-muted-foreground">
              {formatAmount(refundableAmount, payment.currency)} refundable
            </p>
          </div>
        </div>
      )}

      {payment.cardDetails && (
        <div className="bg-card rounded-lg border border-border p-6 space-y-4">
          <h2 className="text-lg font-semibold text-card-foreground flex items-center gap-2">
            <CreditCard className="h-5 w-5" />
            Card Details
          </h2>
          <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
            <Item label="Network" value={payment.cardDetails.cardNetwork} />
            <Item label="Issuer" value={payment.cardDetails.issuerBank} />
            <Item label="Card" value={payment.cardDetails.maskedCardNumber} mono />
            <Item
              label="Expiry"
              value={
                payment.cardDetails.expiryMonth && payment.cardDetails.expiryYear
                  ? `${String(payment.cardDetails.expiryMonth).padStart(2, '0')}/${payment.cardDetails.expiryYear}`
                  : '-'
              }
            />
            <Item label="Auth Code" value={payment.cardDetails.authorizationCode} mono />
            <Item label="Gateway Code" value={payment.cardDetails.gatewayResponseCode} mono />
            {payment.cardDetails.gatewayMessage && (
              <Item label="Gateway Message" value={payment.cardDetails.gatewayMessage} />
            )}
          </div>
        </div>
      )}

      {payment.upiDetails && (
        <div className="bg-card rounded-lg border border-border p-6 space-y-4">
          <h2 className="text-lg font-semibold text-card-foreground flex items-center gap-2">
            <span className="text-xl">📱</span>
            UPI Details
          </h2>
          <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
            <Item label="UPI ID" value={payment.upiDetails.upiId} mono />
            {payment.upiDetails.upiTransactionReference && (
              <Item label="UPI Ref" value={payment.upiDetails.upiTransactionReference} mono />
            )}
          </div>
        </div>
      )}

      {payment.attempts && payment.attempts.length > 0 && (
        <div className="bg-card rounded-lg border border-border p-6 space-y-4">
          <button
            onClick={() => setShowAttempts(!showAttempts)}
            className="flex items-center justify-between w-full"
          >
            <h2 className="text-lg font-semibold text-card-foreground flex items-center gap-2">
              <Clock className="h-5 w-5" />
              Payment Attempts ({payment.attempts.length})
            </h2>
            {showAttempts ? <ChevronUp className="h-5 w-5 text-muted-foreground" /> : <ChevronDown className="h-5 w-5 text-muted-foreground" />}
          </button>

          {showAttempts && (
            <div className="space-y-3">
              {payment.attempts.map((attempt) => (
                <div
                  key={attempt.attemptNumber}
                  className="flex items-center gap-4 p-3 rounded-md border border-border"
                >
                  <div className="flex-shrink-0 w-8 h-8 rounded-full bg-muted flex items-center justify-center text-sm font-medium text-card-foreground">
                    {attempt.attemptNumber}
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2">
                      <StatusBadge status={attempt.status} />
                      {attempt.failureCode && (
                        <span className="text-xs text-destructive">{attempt.failureCode}</span>
                      )}
                    </div>
                    {attempt.failureMessage && (
                      <p className="text-xs text-muted-foreground mt-1 truncate">{attempt.failureMessage}</p>
                    )}
                  </div>
                  <div className="text-right text-xs text-muted-foreground flex-shrink-0">
                    {attempt.processingTimeMs != null && (
                      <span className="font-mono tabular-nums">{attempt.processingTimeMs}ms</span>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {showRefundModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
          <div className="fixed inset-0 bg-black/50" onClick={() => setShowRefundModal(false)} />
          <div className="relative bg-card rounded-lg border border-border shadow-lg p-6 w-full max-w-md mx-4">
            <h2 className="text-lg font-semibold text-card-foreground mb-4">Refund Payment</h2>
            <p className="text-sm text-muted-foreground mb-4">
              Refundable amount: <span className="font-mono font-medium text-card-foreground">{formatAmount(refundableAmount, payment.currency)}</span>
            </p>
            <form onSubmit={handleRefund} className="space-y-4">
              <div>
                <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                  Amount (in {payment.currency})
                </label>
                <input
                  type="number"
                  step="0.01"
                  min="0.01"
                  max={(refundableAmount / 100).toFixed(2)}
                  value={refundAmount}
                  onChange={(e) => setRefundAmount(e.target.value)}
                  required
                  className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring font-mono"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                  Reason
                </label>
                <select
                  value={refundReason}
                  onChange={(e) => setRefundReason(e.target.value)}
                  className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground"
                >
                  {REFUND_REASONS.map((r) => (
                    <option key={r.value} value={r.value}>{r.label}</option>
                  ))}
                </select>
              </div>

              {refundError && (
                <p className="text-sm text-destructive bg-destructive/10 p-2 rounded">{refundError}</p>
              )}

              <div className="flex gap-2 pt-2">
                <button
                  type="submit"
                  disabled={refundMutation.isPending}
                  className="flex-1 inline-flex items-center justify-center rounded-md bg-accent px-4 py-2 text-sm font-medium text-accent-foreground hover:bg-accent/90 disabled:opacity-50"
                >
                  {refundMutation.isPending && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                  Confirm Refund
                </button>
                <button
                  type="button"
                  onClick={() => setShowRefundModal(false)}
                  className="inline-flex items-center justify-center rounded-md border border-border px-4 py-2 text-sm text-card-foreground hover:bg-muted"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
