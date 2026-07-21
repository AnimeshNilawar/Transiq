import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getAdminSettlements, createAdminSettlement } from '@/api/admin'
import { Pagination } from '@/components/shared/Pagination'
import { downloadCsv } from '@/lib/csv'
import { toast } from 'sonner'
import { useState } from 'react'

const PAGE_SIZE = 25

export default function AdminSettlementsPage() {
  const queryClient = useQueryClient()
  const [page, setPage] = useState(0)
  const [merchantId, setMerchantId] = useState('')

  const { data, isLoading } = useQuery({
    queryKey: ['admin-settlements', page],
    queryFn: () => getAdminSettlements({ page, size: PAGE_SIZE, sort: 'createdAt,desc' }).then((r) => r.data),
  })

  const createMutation = useMutation({
    mutationFn: () => createAdminSettlement(merchantId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-settlements'] })
      toast.success('Settlement created')
      setMerchantId('')
    },
    onError: (err) => toast.error(err?.response?.data?.message || 'Failed to create settlement'),
  })

  if (isLoading) return <div className="p-6 text-muted-foreground">Loading settlements...</div>

  const settlements = data?.content ?? []

  return (
    <div className="space-y-6 p-6">
      <h1 className="text-2xl font-bold text-card-foreground">Settlements</h1>

      <div className="bg-card rounded-lg border border-border p-6">
        <h2 className="text-lg font-semibold text-card-foreground mb-4">Trigger Settlement</h2>
        <div className="flex gap-2">
          <input
            value={merchantId}
            onChange={(e) => setMerchantId(e.target.value)}
            placeholder="Merchant UUID"
            className="flex-1 rounded-md border border-input bg-background px-3 py-2 text-sm text-card-foreground placeholder:text-muted-foreground"
          />
          <button
            disabled={!merchantId || createMutation.isPending}
            onClick={() => createMutation.mutate()}
            className="rounded-md bg-accent px-4 py-2 text-sm font-medium text-accent-foreground hover:opacity-90 disabled:opacity-40 transition-opacity"
          >
            {createMutation.isPending ? 'Creating...' : 'Create'}
          </button>
        </div>
      </div>

      <div className="bg-card rounded-lg border border-border p-6">
        <div className="flex items-center justify-between mb-4">
          <p className="text-sm text-muted-foreground">{data?.totalElements ?? 0} total settlements</p>
          <button
            onClick={() => downloadCsv(settlements, 'settlements')}
            disabled={settlements.length === 0}
            className="rounded-md border border-border px-3 py-1.5 text-xs text-card-foreground hover:bg-muted disabled:opacity-40 transition-colors"
          >
            Download CSV
          </button>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b text-left text-muted-foreground">
                <th className="pb-2 font-medium">Reference</th>
                <th className="pb-2 font-medium">Merchant</th>
                <th className="pb-2 font-medium">Amount</th>
                <th className="pb-2 font-medium">Status</th>
                <th className="pb-2 font-medium">Bank Ref</th>
                <th className="pb-2 font-medium">Processed</th>
                <th className="pb-2 font-medium">Date</th>
              </tr>
            </thead>
            <tbody>
              {settlements.length === 0 && (
                <tr><td colSpan={7} className="pt-4 text-center text-muted-foreground">No settlements</td></tr>
              )}
              {settlements.map((s) => (
                <tr key={s.id} className="border-b last:border-0">
                  <td className="py-2 font-mono text-xs text-card-foreground">{s.settlementReference}</td>
                  <td className="py-2 text-card-foreground">{s.merchantName}</td>
                  <td className="py-2 font-mono tabular-nums text-card-foreground">₹{(s.amount / 100).toLocaleString()}</td>
                  <td className="py-2">
                    <span className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${
                      s.status === 'COMPLETED' ? 'bg-success/10 text-success' :
                      s.status === 'FAILED' ? 'bg-destructive/10 text-destructive' :
                      'bg-warning/10 text-warning'
                    }`}>{s.status}</span>
                  </td>
                  <td className="py-2 text-muted-foreground">{s.bankReference || '-'}</td>
                  <td className="py-2 text-muted-foreground">{s.processedAt ? new Date(s.processedAt).toLocaleDateString() : '-'}</td>
                  <td className="py-2 text-muted-foreground">{new Date(s.createdAt).toLocaleDateString()}</td>
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
