import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { getAdminMerchants } from '@/api/admin'
import { Pagination } from '@/components/shared/Pagination'
import { downloadCsv } from '@/lib/csv'

const PAGE_SIZE = 25

export default function AdminMerchantsPage() {
  const [page, setPage] = useState(0)

  const { data, isLoading } = useQuery({
    queryKey: ['admin-merchants', page],
    queryFn: () => getAdminMerchants({ page, size: PAGE_SIZE, sort: 'createdAt,desc' }).then((res) => res.data),
  })

  if (isLoading) {
    return <div className="p-6 text-muted-foreground">Loading merchants...</div>
  }

  const merchants = data?.content ?? []

  return (
    <div className="space-y-6 p-6">
      <h1 className="text-2xl font-bold text-card-foreground">All Merchants</h1>

      <div className="bg-card rounded-lg border border-border p-6">
        <div className="flex items-center justify-between mb-4">
          <p className="text-sm text-muted-foreground">
            {data?.totalElements ?? 0} merchant{data?.totalElements !== 1 ? 's' : ''} registered
          </p>
          <button
            onClick={() => downloadCsv(merchants, 'merchants')}
            disabled={merchants.length === 0}
            className="rounded-md border border-border px-3 py-1.5 text-xs text-card-foreground hover:bg-muted disabled:opacity-40 transition-colors"
          >
            Download CSV
          </button>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b text-left text-muted-foreground">
                <th className="pb-2 font-medium">Business Name</th>
                <th className="pb-2 font-medium">Email</th>
                <th className="pb-2 font-medium">Status</th>
                <th className="pb-2 font-medium">Created</th>
              </tr>
            </thead>
            <tbody>
              {merchants.length === 0 && (
                <tr><td colSpan={4} className="pt-4 text-center text-muted-foreground">No merchants found</td></tr>
              )}
              {merchants.map((m) => (
                <tr key={m.id} className="border-b last:border-0">
                  <td className="py-2">
                    <Link to={`/admin/merchants/${m.id}`} className="text-card-foreground hover:text-accent hover:underline">
                      {m.businessName}
                    </Link>
                  </td>
                  <td className="py-2 text-muted-foreground">{m.businessEmail}</td>
                  <td className="py-2">
                    <span className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${
                      m.status === 'ACTIVE' ? 'bg-success/10 text-success' :
                      m.status === 'SUSPENDED' ? 'bg-destructive/10 text-destructive' :
                      'bg-warning/10 text-warning'
                    }`}>
                      {m.status}
                    </span>
                  </td>
                  <td className="py-2 text-muted-foreground">
                    {new Date(m.createdAt).toLocaleDateString()}
                  </td>
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
