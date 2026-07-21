import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { getAdminRefunds } from '@/api/admin'
import { Pagination } from '@/components/shared/Pagination'
import { downloadCsv } from '@/lib/csv'

const PAGE_SIZE = 25

export default function AdminRefundsPage() {
  const [page, setPage] = useState(0)

  const { data, isLoading } = useQuery({
    queryKey: ['admin-refunds', page],
    queryFn: () => getAdminRefunds({ page, size: PAGE_SIZE, sort: 'createdAt,desc' }).then((r) => r.data),
  })

  if (isLoading) return <div className="p-6 text-muted-foreground">Loading refunds...</div>

  const refunds = data?.content ?? []

  return (
    <div className="space-y-6 p-6">
      <h1 className="text-2xl font-bold text-card-foreground">Refunds</h1>
      <div className="bg-card rounded-lg border border-border p-6">
        <div className="flex items-center justify-between mb-4">
          <p className="text-sm text-muted-foreground">{data?.totalElements ?? 0} total refunds</p>
          <button
            onClick={() => downloadCsv(refunds, 'refunds')}
            disabled={refunds.length === 0}
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
                <th className="pb-2 font-medium">Payment</th>
                <th className="pb-2 font-medium">Merchant</th>
                <th className="pb-2 font-medium">Amount</th>
                <th className="pb-2 font-medium">Reason</th>
                <th className="pb-2 font-medium">Status</th>
                <th className="pb-2 font-medium">Date</th>
              </tr>
            </thead>
            <tbody>
              {refunds.length === 0 && (
                <tr><td colSpan={7} className="pt-4 text-center text-muted-foreground">No refunds</td></tr>
              )}
              {refunds.map((r) => (
                <tr key={r.id} className="border-b last:border-0">
                  <td className="py-2 font-mono text-xs text-card-foreground">{r.refundReference}</td>
                  <td className="py-2 font-mono text-xs text-card-foreground">{r.paymentReference}</td>
                  <td className="py-2 text-card-foreground">{r.merchantName}</td>
                  <td className="py-2 font-mono tabular-nums text-card-foreground">₹{(r.amount / 100).toLocaleString()}</td>
                  <td className="py-2 text-muted-foreground">{r.reason}</td>
                  <td className="py-2">
                    <span className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${
                      r.status === 'SUCCEEDED' ? 'bg-success/10 text-success' :
                      r.status === 'FAILED' ? 'bg-destructive/10 text-destructive' :
                      'bg-warning/10 text-warning'
                    }`}>{r.status}</span>
                  </td>
                  <td className="py-2 text-muted-foreground">{new Date(r.createdAt).toLocaleDateString()}</td>
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
