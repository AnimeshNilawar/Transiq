import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getAdminUsers, updateAdminUserStatus } from '@/api/admin'
import { Pagination } from '@/components/shared/Pagination'
import { downloadCsv } from '@/lib/csv'
import { toast } from 'sonner'

const PAGE_SIZE = 25

export default function AdminUsersPage() {
  const queryClient = useQueryClient()
  const [page, setPage] = useState(0)

  const { data, isLoading } = useQuery({
    queryKey: ['admin-users', page],
    queryFn: () => getAdminUsers({ page, size: PAGE_SIZE, sort: 'createdAt,desc' }).then((r) => r.data),
  })

  const toggleMutation = useMutation({
    mutationFn: ({ id, enabled }) => updateAdminUserStatus(id, enabled),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-users'] })
      toast.success('User status updated')
    },
    onError: () => toast.error('Failed to update user'),
  })

  if (isLoading) return <div className="p-6 text-muted-foreground">Loading users...</div>

  const users = data?.content ?? []

  const roleColors = {
    PLATFORM_ADMIN: 'bg-accent/10 text-accent',
    OWNER: 'bg-chart-2/10 text-chart-2',
    ADMIN: 'bg-chart-1/10 text-chart-1',
    DEVELOPER: 'bg-chart-4/10 text-chart-4',
    FINANCE: 'bg-chart-5/10 text-chart-5',
  }

  return (
    <div className="space-y-6 p-6">
      <h1 className="text-2xl font-bold text-card-foreground">Users</h1>
      <div className="bg-card rounded-lg border border-border p-6">
        <div className="flex items-center justify-between mb-4">
          <p className="text-sm text-muted-foreground">{data?.totalElements ?? 0} total users</p>
          <button
            onClick={() => downloadCsv(users, 'users')}
            disabled={users.length === 0}
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
                <th className="pb-2 font-medium">Email</th>
                <th className="pb-2 font-medium">Role</th>
                <th className="pb-2 font-medium">Merchant</th>
                <th className="pb-2 font-medium">Status</th>
                <th className="pb-2 font-medium">Actions</th>
                <th className="pb-2 font-medium">Created</th>
              </tr>
            </thead>
            <tbody>
              {users.length === 0 && (
                <tr><td colSpan={7} className="pt-4 text-center text-muted-foreground">No users</td></tr>
              )}
              {users.map((u) => (
                <tr key={u.id} className="border-b last:border-0">
                  <td className="py-2 text-card-foreground">{u.firstName} {u.lastName}</td>
                  <td className="py-2 text-muted-foreground">{u.email}</td>
                  <td className="py-2">
                    <span className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${roleColors[u.role] || 'bg-muted text-muted-foreground'}`}>
                      {u.role}
                    </span>
                  </td>
                  <td className="py-2 text-card-foreground">{u.merchantName}</td>
                  <td className="py-2">
                    <span className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${
                      u.enabled ? 'bg-success/10 text-success' : 'bg-destructive/10 text-destructive'
                    }`}>{u.enabled ? 'Active' : 'Disabled'}</span>
                  </td>
                  <td className="py-2">
                    <button
                      disabled={toggleMutation.isPending}
                      onClick={() => toggleMutation.mutate({ id: u.id, enabled: !u.enabled })}
                      className="rounded-md px-2 py-1 text-xs font-medium bg-muted text-card-foreground hover:bg-accent hover:text-accent-foreground disabled:opacity-40 transition-colors"
                    >
                      {u.enabled ? 'Disable' : 'Enable'}
                    </button>
                  </td>
                  <td className="py-2 text-muted-foreground">{new Date(u.createdAt).toLocaleDateString()}</td>
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
