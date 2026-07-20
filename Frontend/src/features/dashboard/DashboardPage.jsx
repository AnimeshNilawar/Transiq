import { useBalance } from '@/hooks/useLedger'
import { useWebhookDeliveries } from '@/hooks/useWebhookDeliveries'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { CardSkeleton, TableSkeleton } from '@/components/shared/LoadingSkeleton'
import { formatAmount } from '@/lib/utils'
import { format } from 'date-fns'
import { useNavigate } from 'react-router-dom'
import { DollarSign, Webhook, TrendingUp } from 'lucide-react'

export function DashboardPage() {
  const { data: balance, isLoading: balanceLoading } = useBalance()
  const { data: deliveries, isLoading: deliveriesLoading } = useWebhookDeliveries({
    size: 5,
    sort: 'createdAt,desc',
  })
  const navigate = useNavigate()

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">Dashboard</h1>
        <p className="text-sm text-muted-foreground">
          Overview of your account
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {balanceLoading ? (
          <>
            <CardSkeleton />
            <CardSkeleton />
            <CardSkeleton />
          </>
        ) : (
          <>
            <StatCard
              title="Available Balance"
              value={balance ? formatAmount(balance.availableBalance, balance.currency) : '-'}
              icon={DollarSign}
              description="Current ledger balance"
            />
            <StatCard
              title="Currency"
              value={balance?.currency || 'INR'}
              icon={TrendingUp}
              description="Primary currency"
            />
            <StatCard
              title="Recent Deliveries"
              value={deliveries?.totalElements?.toString() || '0'}
              icon={Webhook}
              description="Total webhook deliveries"
            />
          </>
        )}
      </div>

      <div>
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-semibold">Recent Webhook Deliveries</h2>
          <button
            onClick={() => navigate('/webhooks/deliveries')}
            className="text-sm text-primary hover:underline"
          >
            View all
          </button>
        </div>

        {deliveriesLoading ? (
          <TableSkeleton rows={5} columns={5} />
        ) : deliveries?.content?.length > 0 ? (
          <div className="overflow-x-auto rounded-lg border">
            <table className="w-full caption-bottom text-sm">
              <thead className="[&_tr]:border-b">
                <tr className="border-b transition-colors hover:bg-muted/50">
                  <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">
                    Event Type
                  </th>
                  <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">
                    Reference
                  </th>
                  <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">
                    Status
                  </th>
                  <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">
                    HTTP Status
                  </th>
                  <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">
                    Created
                  </th>
                </tr>
              </thead>
              <tbody className="[&_tr:last-child]:border-0">
                {deliveries.content.map((delivery) => (
                  <tr
                    key={delivery.id}
                    className="border-b transition-colors hover:bg-muted/50 cursor-pointer"
                    onClick={() =>
                      navigate(`/webhooks/deliveries/${delivery.id}`)
                    }
                  >
                    <td className="p-4 align-middle font-medium">
                      {delivery.eventType}
                    </td>
                    <td className="p-4 align-middle font-mono text-xs">
                      {delivery.eventReference}
                    </td>
                    <td className="p-4 align-middle">
                      <StatusBadge status={delivery.status} />
                    </td>
                    <td className="p-4 align-middle">
                      {delivery.httpStatus || '-'}
                    </td>
                    <td className="p-4 align-middle text-muted-foreground">
                      {format(new Date(delivery.createdAt), 'MMM d, HH:mm')}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="text-center py-8 text-sm text-muted-foreground border rounded-lg">
            No webhook deliveries yet
          </div>
        )}
      </div>
    </div>
  )
}

function StatCard({ title, value, icon: Icon, description }) {
  return (
    <div className="rounded-lg border p-6">
      <div className="flex items-center justify-between mb-2">
        <p className="text-sm font-medium text-muted-foreground">{title}</p>
        <Icon className="h-4 w-4 text-muted-foreground" />
      </div>
      <div className="text-2xl font-bold">{value}</div>
      <p className="text-xs text-muted-foreground mt-1">{description}</p>
    </div>
  )
}
