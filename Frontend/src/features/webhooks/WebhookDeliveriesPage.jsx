import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  useWebhookDeliveries,
  useRetryWebhookDelivery,
} from '@/hooks/useWebhookDeliveries'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { TableSkeleton } from '@/components/shared/LoadingSkeleton'
import { Pagination } from '@/components/shared/Pagination'
import { format } from 'date-fns'
import { RefreshCw } from 'lucide-react'

export default function WebhookDeliveriesPage() {
  const navigate = useNavigate()
  const retryMutation = useRetryWebhookDelivery()
  const [filters, setFilters] = useState({
    page: 0,
    size: 20,
    status: '',
    eventType: '',
    sort: 'createdAt,desc',
  })

  const queryFilters = {
    page: filters.page,
    size: filters.size,
    sort: filters.sort,
  }
  if (filters.status) queryFilters.status = filters.status
  if (filters.eventType) queryFilters.eventType = filters.eventType

  const { data, isLoading } = useWebhookDeliveries(queryFilters)

  const handleRetry = async (e, id) => {
    e.stopPropagation()
    await retryMutation.mutateAsync(id)
  }

  if (isLoading) return <TableSkeleton rows={10} columns={7} />

  return (
    <div className="space-y-4">
      <div>
        <h1 className="text-2xl font-bold tracking-tight text-foreground">
          Webhook Deliveries
        </h1>
        <p className="text-sm text-muted-foreground">
          {data?.totalElements || 0} total deliveries
        </p>
      </div>

      <div className="flex items-center gap-3">
        <select
          value={filters.status}
          onChange={(e) =>
            setFilters((f) => ({ ...f, status: e.target.value, page: 0 }))
          }
          className="rounded-md border border-border bg-card px-3 py-1.5 text-sm text-card-foreground"
        >
          <option value="">All Statuses</option>
          <option value="PENDING">Pending</option>
          <option value="DELIVERED">Delivered</option>
          <option value="FAILED">Failed</option>
        </select>

        <select
          value={filters.eventType}
          onChange={(e) =>
            setFilters((f) => ({ ...f, eventType: e.target.value, page: 0 }))
          }
          className="rounded-md border border-border bg-card px-3 py-1.5 text-sm text-card-foreground"
        >
          <option value="">All Events</option>
          <option value="PAYMENT_SUCCEEDED">Payment Succeeded</option>
          <option value="PAYMENT_FAILED">Payment Failed</option>
          <option value="REFUND_SUCCEEDED">Refund Succeeded</option>
          <option value="SETTLEMENT_COMPLETED">Settlement Completed</option>
        </select>
      </div>

      <div className="overflow-x-auto rounded-lg border border-border">
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
                HTTP
              </th>
              <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">
                Attempts
              </th>
              <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">
                Duration
              </th>
              <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">
                Created
              </th>
              <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">
                Actions
              </th>
            </tr>
          </thead>
          <tbody className="[&_tr:last-child]:border-0">
            {data?.content?.map((delivery) => (
              <tr
                key={delivery.id}
                className="border-b transition-colors hover:bg-muted/50 cursor-pointer"
                onClick={() =>
                  navigate(`/webhooks/deliveries/${delivery.id}`)
                }
              >
                <td className="p-4 align-middle font-medium text-card-foreground">
                  {delivery.eventType}
                </td>
                <td className="p-4 align-middle font-mono text-xs">
                  {delivery.eventReference}
                </td>
                <td className="p-4 align-middle">
                  <StatusBadge status={delivery.status} />
                </td>
                <td className="p-4 align-middle font-mono tabular-nums">{delivery.httpStatus || '-'}</td>
                <td className="p-4 align-middle font-mono tabular-nums">{delivery.attemptCount}</td>
                <td className="p-4 align-middle font-mono tabular-nums">{delivery.durationMs}ms</td>
                <td className="p-4 align-middle text-muted-foreground">
                  {format(new Date(delivery.createdAt), 'MMM d, HH:mm')}
                </td>
                <td className="p-4 align-middle">
                  <button
                    onClick={(e) => handleRetry(e, delivery.id)}
                    disabled={retryMutation.isPending}
                    className="inline-flex items-center gap-1 text-sm text-accent font-medium hover:underline disabled:opacity-50"
                  >
                    <RefreshCw className="h-3.5 w-3.5" />
                    Retry
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Pagination
        page={filters.page}
        totalPages={data?.totalPages || 1}
        onPageChange={(p) => setFilters((f) => ({ ...f, page: p }))}
      />
    </div>
  )
}
