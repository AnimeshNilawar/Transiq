import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  useWebhookDeliveries,
  useRetryWebhookDelivery,
} from '@/hooks/useWebhookDeliveries'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { TableSkeleton } from '@/components/shared/LoadingSkeleton'
import { format } from 'date-fns'
import { RefreshCw } from 'lucide-react'

export function WebhookDeliveriesPage() {
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
        <h1 className="text-2xl font-bold tracking-tight">
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
          className="rounded-md border px-3 py-1.5 text-sm"
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
          className="rounded-md border px-3 py-1.5 text-sm"
        >
          <option value="">All Events</option>
          <option value="PAYMENT_SUCCEEDED">Payment Succeeded</option>
          <option value="PAYMENT_FAILED">Payment Failed</option>
          <option value="REFUND_SUCCEEDED">Refund Succeeded</option>
          <option value="SETTLEMENT_COMPLETED">Settlement Completed</option>
        </select>
      </div>

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
                <td className="p-4 align-middle font-medium">
                  {delivery.eventType}
                </td>
                <td className="p-4 align-middle font-mono text-xs">
                  {delivery.eventReference}
                </td>
                <td className="p-4 align-middle">
                  <StatusBadge status={delivery.status} />
                </td>
                <td className="p-4 align-middle">{delivery.httpStatus || '-'}</td>
                <td className="p-4 align-middle">{delivery.attemptCount}</td>
                <td className="p-4 align-middle">{delivery.durationMs}ms</td>
                <td className="p-4 align-middle text-muted-foreground">
                  {format(new Date(delivery.createdAt), 'MMM d, HH:mm')}
                </td>
                <td className="p-4 align-middle">
                  <button
                    onClick={(e) => handleRetry(e, delivery.id)}
                    disabled={retryMutation.isPending}
                    className="inline-flex items-center gap-1 text-sm text-primary hover:underline disabled:opacity-50"
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

      {data?.totalPages > 1 && (
        <div className="flex items-center justify-between">
          <p className="text-sm text-muted-foreground">
            Page {(data?.page || 0) + 1} of {data?.totalPages || 1}
          </p>
          <div className="flex gap-2">
            <button
              onClick={() =>
                setFilters((f) => ({ ...f, page: Math.max(0, f.page - 1) }))
              }
              disabled={filters.page === 0}
              className="inline-flex items-center justify-center rounded-md border px-3 py-1.5 text-sm disabled:opacity-50 hover:bg-accent"
            >
              Previous
            </button>
            <button
              onClick={() =>
                setFilters((f) => ({
                  ...f,
                  page: Math.min((data?.totalPages || 1) - 1, f.page + 1),
                }))
              }
              disabled={filters.page >= (data?.totalPages || 1) - 1}
              className="inline-flex items-center justify-center rounded-md border px-3 py-1.5 text-sm disabled:opacity-50 hover:bg-accent"
            >
              Next
            </button>
          </div>
        </div>
      )}
    </div>
  )
}
