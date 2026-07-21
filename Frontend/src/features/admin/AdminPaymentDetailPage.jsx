import { useParams } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getAdminPaymentDetail, updateAdminPaymentStatus } from '@/api/admin'
import { toast } from 'sonner'

export default function AdminPaymentDetailPage() {
  const { reference } = useParams()
  const queryClient = useQueryClient()

  const { data: payment, isLoading } = useQuery({
    queryKey: ['admin-payment', reference],
    queryFn: () => getAdminPaymentDetail(reference).then((r) => r.data),
  })

  const updateMutation = useMutation({
    mutationFn: (status) => updateAdminPaymentStatus(reference, status),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-payment', reference] })
      toast.success('Payment status updated')
    },
    onError: () => toast.error('Failed to update status'),
  })

  if (isLoading) return <div className="p-6 text-muted-foreground">Loading payment...</div>
  if (!payment) return <div className="p-6 text-muted-foreground">Payment not found</div>

  const statuses = ['CREATED', 'REQUIRES_PAYMENT_METHOD', 'PROCESSING', 'SUCCEEDED', 'FAILED', 'CANCELLED', 'REFUNDED', 'EXPIRED']

  return (
    <div className="space-y-6 p-6">
      <h1 className="text-2xl font-bold text-card-foreground">Payment Detail</h1>

      <div className="bg-card rounded-lg border border-border p-6 space-y-4">
        <DetailRow label="Reference" value={payment.paymentReference} mono />
        <DetailRow label="Amount" value={`₹${(payment.amount / 100).toLocaleString()}`} />
        <DetailRow label="Currency" value={payment.currency} />
        <DetailRow label="Status" value={payment.status} />
        <DetailRow label="Payment Method" value={payment.paymentMethodType || '-'} />
        <DetailRow label="Merchant" value={`${payment.merchantName} (${payment.merchantEmail})`} />
        <DetailRow label="Customer" value={payment.customerName || payment.customerEmail || '-'} />
        <DetailRow label="Order ID" value={payment.orderId || '-'} />
        <DetailRow label="Description" value={payment.description || '-'} />
        <DetailRow label="Idempotency Key" value={payment.idempotencyKey || '-'} mono />
        <DetailRow label="Refunded Amount" value={`₹${(payment.refundedAmount / 100).toLocaleString()}`} />
        <DetailRow label="Created" value={new Date(payment.createdAt).toLocaleString()} />
        <DetailRow label="Expires" value={new Date(payment.expiresAt).toLocaleString()} />
      </div>

      <div className="bg-card rounded-lg border border-border p-6">
        <h2 className="text-lg font-semibold text-card-foreground mb-4">Admin Actions</h2>
        <div className="flex flex-wrap gap-2">
          {statuses.map((s) => (
            <button
              key={s}
              disabled={s === payment.status || updateMutation.isPending}
              onClick={() => updateMutation.mutate(s)}
              className="rounded-md px-3 py-1.5 text-sm font-medium bg-muted text-card-foreground hover:bg-accent hover:text-accent-foreground disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
            >
              Set {s}
            </button>
          ))}
        </div>
      </div>
    </div>
  )
}

function DetailRow({ label, value, mono }) {
  return (
    <div className="flex items-start gap-4">
      <span className="w-36 shrink-0 text-sm font-medium text-muted-foreground">{label}</span>
      <span className={`text-sm text-card-foreground ${mono ? 'font-mono text-xs' : ''}`}>{value}</span>
    </div>
  )
}
