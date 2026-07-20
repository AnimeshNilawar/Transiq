import { useParams, useNavigate } from 'react-router-dom'
import { usePaymentDetail } from '@/hooks/usePayments'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { DetailSkeleton } from '@/components/shared/LoadingSkeleton'
import { formatAmount } from '@/lib/utils'
import { format } from 'date-fns'
import { ArrowLeft } from 'lucide-react'

export function PaymentDetailPage() {
  const { paymentReference } = useParams()
  const navigate = useNavigate()
  const { data: payment, isLoading } = usePaymentDetail(paymentReference)

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
      <div className="flex items-center gap-4">
        <button
          onClick={() => navigate('/payments')}
          className="inline-flex items-center justify-center rounded-md border p-2 hover:bg-accent transition-colors"
        >
          <ArrowLeft className="h-4 w-4" />
        </button>
        <div>
          <h1 className="text-2xl font-bold tracking-tight">
            Payment {payment.paymentReference}
          </h1>
          <p className="text-sm text-muted-foreground">{payment.id}</p>
        </div>
      </div>

      <div className="rounded-lg border p-6 space-y-4">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold">Details</h2>
          <StatusBadge status={payment.status} />
        </div>
        <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
          <Item label="Amount" value={formatAmount(payment.amount, payment.currency)} />
          <Item label="Currency" value={payment.currency} />
          <Item label="Order ID" value={payment.orderId} />
          <Item label="Customer Email" value={payment.customerEmail} />
          <Item label="Customer Name" value={payment.customerName} />
          <Item label="Description" value={payment.description} />
          <Item
            label="Created"
            value={format(new Date(payment.createdAt), 'MMM d, yyyy HH:mm:ss')}
          />
        </div>
      </div>
    </div>
  )
}

function Item({ label, value }) {
  return (
    <div>
      <p className="text-sm text-muted-foreground">{label}</p>
      <p className="text-sm font-medium mt-0.5">{value || '-'}</p>
    </div>
  )
}
