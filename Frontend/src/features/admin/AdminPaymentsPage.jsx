import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { getAdminPayments } from '@/api/admin'
import { Pagination } from '@/components/shared/Pagination'
import { downloadCsv } from '@/lib/csv'

const PAGE_SIZE = 25

export default function AdminPaymentsPage() {
  const [page, setPage] = useState(0)

  const { data, isLoading } = useQuery({
    queryKey: ['admin-payments', page],
    queryFn: () => getAdminPayments({ page, size: PAGE_SIZE, sort: 'createdAt,desc' }).then((r) => r.data),
  })

  if (isLoading) return <div className="p-6 text-muted-foreground">Loading payments...</div>

  const payments = data?.content ?? []

  return (
    <div className="space-y-6 p-6">
      <h1 className="text-2xl font-bold text-card-foreground">Payments</h1>
      <div className="bg-card rounded-lg border border-border p-6">
        <div className="flex items-center justify-between mb-4">
          <p className="text-sm text-muted-foreground">{data?.totalElements ?? 0} total payments</p>
          <button
            onClick={() => downloadCsv(payments, 'payments')}
            disabled={payments.length === 0}
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
                <th className="pb-2 font-medium">Customer</th>
                <th className="pb-2 font-medium">Date</th>
              </tr>
            </thead>
            <tbody>
              {payments.length === 0 && (
                <tr><td colSpan={6} className="pt-4 text-center text-muted-foreground">No payments</td></tr>
              )}
              {payments.map((p) => (
                <tr key={p.id} className="border-b last:border-0">
                  <td className="py-2">
                    <Link to={`/admin/payments/${p.paymentReference}`} className="font-mono text-xs text-accent hover:underline">
                      {p.paymentReference}
                    </Link>
                  </td>
                  <td className="py-2 text-card-foreground">{p.merchantName}</td>
                  <td className="py-2 font-mono tabular-nums text-card-foreground">₹{(p.amount / 100).toLocaleString()}</td>
                  <td className="py-2">
                    <span className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${
                      p.status === 'SUCCEEDED' ? 'bg-success/10 text-success' :
                      p.status === 'FAILED' ? 'bg-destructive/10 text-destructive' :
                      p.status === 'REFUNDED' ? 'bg-warning/10 text-warning' :
                      'bg-muted text-muted-foreground'
                    }`}>{p.status}</span>
                  </td>
                  <td className="py-2 text-muted-foreground">{p.customerEmail || '-'}</td>
                  <td className="py-2 text-muted-foreground">{new Date(p.createdAt).toLocaleDateString()}</td>
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
