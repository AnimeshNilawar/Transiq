import { useParams, useNavigate } from 'react-router-dom'
import { useRefund } from '@/hooks/useRefunds'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { DetailSkeleton } from '@/components/shared/LoadingSkeleton'
import { Item } from '@/components/shared/Item'
import { formatAmount } from '@/lib/utils'
import { format } from 'date-fns'
import { ArrowLeft } from 'lucide-react'

export default function RefundDetailPage() {
  const { refundReference } = useParams()
  const navigate = useNavigate()
  const { data: refund, isLoading } = useRefund(refundReference)

  if (isLoading) return <DetailSkeleton />

  if (!refund) {
    return (
      <div className="text-center py-12">
        <p className="text-muted-foreground">Refund not found</p>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <button
          onClick={() => navigate('/refunds')}
          className="inline-flex items-center justify-center rounded-md border border-border p-2 hover:bg-muted transition-colors text-muted-foreground"
        >
          <ArrowLeft className="h-4 w-4" />
        </button>
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-foreground">
            Refund <span className="font-mono">{refund.refundReference}</span>
          </h1>
        </div>
      </div>

      <div className="bg-card rounded-lg border border-border p-6 space-y-4">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-card-foreground">Details</h2>
          <StatusBadge status={refund.status} />
        </div>
        <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
          <Item label="Refund Reference" value={refund.refundReference} mono />
          <Item label="Payment Reference" value={refund.paymentReference} mono />
          <Item label="Amount" value={formatAmount(refund.amount)} mono />
          <Item label="Reason" value={refund.reason} />
          <Item
            label="Created"
            value={format(new Date(refund.createdAt), 'MMM d, yyyy HH:mm:ss')}
          />
        </div>
      </div>
    </div>
  )
}
