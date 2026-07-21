import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getAdminApiKeys, revokeAdminApiKey } from '@/api/admin'
import { Pagination } from '@/components/shared/Pagination'
import { downloadCsv } from '@/lib/csv'
import { toast } from 'sonner'

const PAGE_SIZE = 25

export default function AdminApiKeysPage() {
  const queryClient = useQueryClient()
  const [page, setPage] = useState(0)

  const { data, isLoading } = useQuery({
    queryKey: ['admin-api-keys', page],
    queryFn: () => getAdminApiKeys({ page, size: PAGE_SIZE, sort: 'createdAt,desc' }).then((r) => r.data),
  })

  const revokeMutation = useMutation({
    mutationFn: (id) => revokeAdminApiKey(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-api-keys'] })
      toast.success('API key revoked')
    },
    onError: () => toast.error('Failed to revoke key'),
  })

  if (isLoading) return <div className="p-6 text-muted-foreground">Loading API keys...</div>

  const keys = data?.content ?? []

  return (
    <div className="space-y-6 p-6">
      <h1 className="text-2xl font-bold text-card-foreground">API Keys</h1>
      <div className="bg-card rounded-lg border border-border p-6">
        <div className="flex items-center justify-between mb-4">
          <p className="text-sm text-muted-foreground">{data?.totalElements ?? 0} total API keys</p>
          <button
            onClick={() => downloadCsv(keys, 'api-keys')}
            disabled={keys.length === 0}
            className="rounded-md border border-border px-3 py-1.5 text-xs text-card-foreground hover:bg-muted disabled:opacity-40 transition-colors"
          >
            Download CSV
          </button>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b text-left text-muted-foreground">
                <th className="pb-2 font-medium">Name</th>
                <th className="pb-2 font-medium">Prefix</th>
                <th className="pb-2 font-medium">Type</th>
                <th className="pb-2 font-medium">Environment</th>
                <th className="pb-2 font-medium">Status</th>
                <th className="pb-2 font-medium">Merchant</th>
                <th className="pb-2 font-medium">Last Used</th>
                <th className="pb-2 font-medium">Actions</th>
              </tr>
            </thead>
            <tbody>
              {keys.length === 0 && (
                <tr><td colSpan={8} className="pt-4 text-center text-muted-foreground">No API keys</td></tr>
              )}
              {keys.map((k) => (
                <tr key={k.id} className="border-b last:border-0">
                  <td className="py-2 text-card-foreground">{k.name}</td>
                  <td className="py-2 font-mono text-xs text-card-foreground">{k.keyPrefix}...</td>
                  <td className="py-2 text-card-foreground">{k.type}</td>
                  <td className="py-2">
                    <span className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${
                      k.environment === 'LIVE' ? 'bg-success/10 text-success' : 'bg-warning/10 text-warning'
                    }`}>{k.environment}</span>
                  </td>
                  <td className="py-2">
                    <span className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${
                      k.status === 'ACTIVE' ? 'bg-success/10 text-success' : 'bg-destructive/10 text-destructive'
                    }`}>{k.status}</span>
                  </td>
                  <td className="py-2 text-card-foreground">{k.merchantName}</td>
                  <td className="py-2 text-muted-foreground">{k.lastUsedAt ? new Date(k.lastUsedAt).toLocaleDateString() : 'Never'}</td>
                  <td className="py-2">
                    {k.status === 'ACTIVE' && (
                      <button
                        disabled={revokeMutation.isPending}
                        onClick={() => revokeMutation.mutate(k.id)}
                        className="rounded-md px-2 py-1 text-xs font-medium bg-destructive/10 text-destructive hover:bg-destructive/20 disabled:opacity-40 transition-colors"
                      >
                        Revoke
                      </button>
                    )}
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
