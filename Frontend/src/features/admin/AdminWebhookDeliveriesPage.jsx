import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getAdminWebhookDeliveries, retryAdminWebhookDelivery } from '@/api/admin'
import { Pagination } from '@/components/shared/Pagination'
import { downloadCsv } from '@/lib/csv'
import { toast } from 'sonner'

const PAGE_SIZE = 25

export default function AdminWebhookDeliveriesPage() {
  const queryClient = useQueryClient()
  const [page, setPage] = useState(0)

  const { data, isLoading } = useQuery({
    queryKey: ['admin-webhook-deliveries', page],
    queryFn: () => getAdminWebhookDeliveries({ page, size: PAGE_SIZE, sort: 'createdAt,desc' }).then((r) => r.data),
  })

  const retryMutation = useMutation({
    mutationFn: (id) => retryAdminWebhookDelivery(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-webhook-deliveries'] })
      toast.success('Delivery queued for retry')
    },
    onError: () => toast.error('Failed to retry delivery'),
  })

  if (isLoading) return <div className="p-6 text-muted-foreground">Loading deliveries...</div>

  const deliveries = data?.content ?? []

  return (
    <div className="space-y-6 p-6">
      <h1 className="text-2xl font-bold text-card-foreground">Webhook Deliveries</h1>
      <div className="bg-card rounded-lg border border-border p-6">
        <div className="flex items-center justify-between mb-4">
          <p className="text-sm text-muted-foreground">{data?.totalElements ?? 0} total deliveries</p>
          <button
            onClick={() => downloadCsv(deliveries, 'webhook-deliveries')}
            disabled={deliveries.length === 0}
            className="rounded-md border border-border px-3 py-1.5 text-xs text-card-foreground hover:bg-muted disabled:opacity-40 transition-colors"
          >
            Download CSV
          </button>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b text-left text-muted-foreground">
                <th className="pb-2 font-medium">Event Type</th>
                <th className="pb-2 font-medium">Reference</th>
                <th className="pb-2 font-medium">Merchant</th>
                <th className="pb-2 font-medium">Endpoint</th>
                <th className="pb-2 font-medium">Status</th>
                <th className="pb-2 font-medium">HTTP</th>
                <th className="pb-2 font-medium">Attempts</th>
                <th className="pb-2 font-medium">Actions</th>
                <th className="pb-2 font-medium">Date</th>
              </tr>
            </thead>
            <tbody>
              {deliveries.length === 0 && (
                <tr><td colSpan={9} className="pt-4 text-center text-muted-foreground">No deliveries</td></tr>
              )}
              {deliveries.map((d) => (
                <tr key={d.id} className="border-b last:border-0">
                  <td className="py-2 text-card-foreground">{d.eventType}</td>
                  <td className="py-2 font-mono text-xs text-card-foreground">{d.eventReference}</td>
                  <td className="py-2 text-card-foreground">{d.merchantName}</td>
                  <td className="py-2 max-w-[200px] truncate text-muted-foreground" title={d.endpointUrl}>{d.endpointUrl}</td>
                  <td className="py-2">
                    <span className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${
                      d.status === 'DELIVERED' ? 'bg-success/10 text-success' :
                      d.status === 'FAILED' ? 'bg-destructive/10 text-destructive' :
                      'bg-warning/10 text-warning'
                    }`}>{d.status}</span>
                  </td>
                  <td className="py-2 text-card-foreground">{d.httpStatus || '-'}</td>
                  <td className="py-2 text-card-foreground">{d.attemptCount}</td>
                  <td className="py-2">
                    {d.status === 'FAILED' && (
                      <button
                        disabled={retryMutation.isPending}
                        onClick={() => retryMutation.mutate(d.id)}
                        className="rounded-md px-2 py-1 text-xs font-medium bg-muted text-card-foreground hover:bg-accent hover:text-accent-foreground disabled:opacity-40 transition-colors"
                      >
                        Retry
                      </button>
                    )}
                  </td>
                  <td className="py-2 text-muted-foreground">{new Date(d.createdAt).toLocaleDateString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <div className="mt-4">
          <Pagination page={page} totalPages={data?.totalPages ?? 0} onPageChange={setPage} />
        </div>
      </div>
    </div>
  )
}
