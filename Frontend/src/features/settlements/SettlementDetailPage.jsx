import { useParams, useNavigate } from 'react-router-dom'
import { useSettlement } from '@/hooks/useSettlements'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { DetailSkeleton } from '@/components/shared/LoadingSkeleton'
import { Item } from '@/components/shared/Item'
import { formatAmount } from '@/lib/utils'
import { ArrowLeft } from 'lucide-react'

export default function SettlementDetailPage() {
  const { settlementReference } = useParams()
  const navigate = useNavigate()
  const { data: settlement, isLoading } = useSettlement(settlementReference)

  if (isLoading) return <DetailSkeleton />

  if (!settlement) {
    return (
      <div className="text-center py-12">
        <p className="text-muted-foreground">Settlement not found</p>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <button
          onClick={() => navigate('/settlements')}
          className="inline-flex items-center justify-center rounded-md border border-border p-2 hover:bg-muted transition-colors text-muted-foreground"
        >
          <ArrowLeft className="h-4 w-4" />
        </button>
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-foreground">
            Settlement <span className="font-mono">{settlement.settlementReference}</span>
          </h1>
        </div>
      </div>

      <div className="bg-card rounded-lg border border-border p-6 space-y-4">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-card-foreground">Details</h2>
          <StatusBadge status={settlement.status} />
        </div>
        <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
          <Item label="Settlement Reference" value={settlement.settlementReference} mono />
          <Item label="Amount" value={formatAmount(settlement.amount, settlement.currency)} mono />
          <Item label="Currency" value={settlement.currency} mono />
          <Item label="Bank Reference" value={settlement.bankReference} mono />
          <Item label="Processed At" value={settlement.processedAt || 'Pending'} />
        </div>
      </div>
    </div>
  )
}
