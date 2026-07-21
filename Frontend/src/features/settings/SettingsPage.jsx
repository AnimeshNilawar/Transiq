import { useState } from 'react'
import useAuth from '@/hooks/useAuth'
import {
  useUsers,
  useInviteUser,
  useUpdateUserRole,
  useDeleteUser,
} from '@/hooks/useUsers'
import { CopyModal } from '@/components/shared/CopyModal'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { TableSkeleton } from '@/components/shared/LoadingSkeleton'
import { Item } from '@/components/shared/Item'
import { Plus, Trash2, Loader2 } from 'lucide-react'

export default function SettingsPage() {
  const { user } = useAuth()
  const { data: users, isLoading: usersLoading } = useUsers()
  const inviteMutation = useInviteUser()
  const roleMutation = useUpdateUserRole()
  const deleteMutation = useDeleteUser()

  const [showInviteModal, setShowInviteModal] = useState(false)
  const [inviteResult, setInviteResult] = useState(null)
  const [inviteForm, setInviteForm] = useState({
    email: '',
    firstName: '',
    lastName: '',
    role: 'MEMBER',
  })

  const canManageUsers = user?.role === 'OWNER' || user?.role === 'ADMIN'

  const handleInvite = async (e) => {
    e.preventDefault()
    try {
      const payload = {
        email: inviteForm.email,
        role: inviteForm.role,
      }
      if (inviteForm.firstName) payload.firstName = inviteForm.firstName
      if (inviteForm.lastName) payload.lastName = inviteForm.lastName

      const response = await inviteMutation.mutateAsync(payload)
      setInviteResult(response.data)
      setShowInviteModal(false)
      setInviteForm({ email: '', firstName: '', lastName: '', role: 'MEMBER' })
    } catch {
      // Error handled by interceptor
    }
  }

  const handleRoleChange = async (userId, newRole) => {
    try {
      await roleMutation.mutateAsync({ id: userId, data: { role: newRole } })
    } catch {
      // 400/403 errors handled by interceptor
    }
  }

  const handleDelete = async (userId) => {
    if (window.confirm('Remove this user from the team?')) {
      try {
        await deleteMutation.mutateAsync(userId)
      } catch {
        // Error handled by interceptor
      }
    }
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight text-foreground">Settings</h1>
        <p className="text-sm text-muted-foreground">
          Account and team management
        </p>
      </div>

      {/* Account Details */}
      <div className="bg-card rounded-lg border border-border p-6 space-y-4">
        <h2 className="text-lg font-semibold text-card-foreground">Account Details</h2>
        <div className="grid grid-cols-2 gap-4">
          <Item label="Name" value={user ? `${user.firstName || ''} ${user.lastName || ''}`.trim() : '-'} />
          <Item label="Email" value={user?.email || '-'} mono />
          <Item label="Role" value={user?.role || '-'} />
          <Item label="Business" value={user?.merchant?.businessName || '-'} />
          <Item label="Business Email" value={user?.merchant?.businessEmail || '-'} mono />
          <Item
            label="Merchant Status"
            value={user?.merchant?.status || '-'}
          />
        </div>
      </div>

      {/* Team Management */}
      <div className="bg-card rounded-lg border border-border p-6 space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-lg font-semibold text-card-foreground">Team Members</h2>
            <p className="text-sm text-muted-foreground">
              {users?.length || 0} member{(users?.length || 0) !== 1 ? 's' : ''}
            </p>
          </div>
          {canManageUsers && (
            <button
              onClick={() => setShowInviteModal(true)}
              className="inline-flex items-center gap-2 rounded-md bg-accent px-4 py-2 text-sm font-medium text-accent-foreground hover:bg-accent/90 transition-colors"
            >
              <Plus className="h-4 w-4" />
              Invite User
            </button>
          )}
        </div>

        {usersLoading ? (
          <TableSkeleton rows={3} columns={4} />
        ) : (
          <div className="overflow-x-auto rounded-lg border border-border">
            <table className="w-full caption-bottom text-sm">
              <thead className="[&_tr]:border-b">
                <tr className="border-b transition-colors hover:bg-muted/50">
                  <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Name</th>
                  <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Email</th>
                  <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Role</th>
                  <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Actions</th>
                </tr>
              </thead>
              <tbody className="[&_tr:last-child]:border-0">
                {users?.map((u) => (
                  <tr key={u.id} className="border-b transition-colors hover:bg-muted/50">
                    <td className="p-4 align-middle text-card-foreground">
                      {u.firstName || u.lastName
                        ? `${u.firstName || ''} ${u.lastName || ''}`.trim()
                        : '-'}
                    </td>
                    <td className="p-4 align-middle font-mono text-xs">
                      {u.email}
                      {u.id === user?.id && (
                        <span className="text-xs text-muted-foreground ml-2 font-sans">(you)</span>
                      )}
                    </td>
                    <td className="p-4 align-middle">
                      {canManageUsers && u.id !== user?.id ? (
                        <select
                          value={u.role}
                          onChange={(e) => handleRoleChange(u.id, e.target.value)}
                          disabled={roleMutation.isPending}
                          className="rounded-md border border-border bg-card px-2 py-1 text-sm text-card-foreground"
                        >
                          <option value="MEMBER">Member</option>
                          <option value="ADMIN">Admin</option>
                          {user?.role === 'OWNER' && (
                            <option value="OWNER">Owner</option>
                          )}
                        </select>
                      ) : (
                        <StatusBadge status={u.role} />
                      )}
                    </td>
                    <td className="p-4 align-middle">
                      {canManageUsers && u.id !== user?.id ? (
                        <button
                          onClick={() => handleDelete(u.id)}
                          disabled={deleteMutation.isPending}
                          className="inline-flex items-center gap-1 text-sm text-destructive hover:underline disabled:opacity-50"
                        >
                          <Trash2 className="h-3.5 w-3.5" />
                          Remove
                        </button>
                      ) : (
                        <span className="text-sm text-muted-foreground">—</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Invite Modal */}
      {showInviteModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
          <div className="fixed inset-0 bg-black/50" onClick={() => setShowInviteModal(false)} />
          <div className="relative bg-card rounded-lg border border-border shadow-lg p-6 w-full max-w-md mx-4">
            <h2 className="text-lg font-semibold text-card-foreground mb-4">Invite Team Member</h2>
            <form onSubmit={handleInvite} className="space-y-4">
              <div>
                <label className="block text-sm font-medium mb-1.5 text-card-foreground">Email *</label>
                <input
                  type="email"
                  value={inviteForm.email}
                  onChange={(e) => setInviteForm((f) => ({ ...f, email: e.target.value }))}
                  required
                  className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring"
                  placeholder="colleague@company.com"
                />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-sm font-medium mb-1.5 text-card-foreground">First Name</label>
                  <input
                    type="text"
                    value={inviteForm.firstName}
                    onChange={(e) => setInviteForm((f) => ({ ...f, firstName: e.target.value }))}
                    className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1.5 text-card-foreground">Last Name</label>
                  <input
                    type="text"
                    value={inviteForm.lastName}
                    onChange={(e) => setInviteForm((f) => ({ ...f, lastName: e.target.value }))}
                    className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring"
                  />
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium mb-1.5 text-card-foreground">Role *</label>
                <select
                  value={inviteForm.role}
                  onChange={(e) => setInviteForm((f) => ({ ...f, role: e.target.value }))}
                  className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground"
                >
                  <option value="MEMBER">Member</option>
                  <option value="ADMIN">Admin</option>
                  {user?.role === 'OWNER' && (
                    <option value="OWNER">Owner</option>
                  )}
                </select>
              </div>
              <div className="flex gap-2 pt-2">
                <button
                  type="submit"
                  disabled={inviteMutation.isPending}
                  className="flex-1 inline-flex items-center justify-center rounded-md bg-accent px-4 py-2 text-sm font-medium text-accent-foreground hover:bg-accent/90 disabled:opacity-50"
                >
                  {inviteMutation.isPending && (
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  )}
                  Send Invite
                </button>
                <button
                  type="button"
                  onClick={() => setShowInviteModal(false)}
                  className="inline-flex items-center justify-center rounded-md border border-border px-4 py-2 text-sm text-card-foreground hover:bg-muted"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Temporary Password Modal */}
      <CopyModal
        open={!!inviteResult}
        onClose={() => setInviteResult(null)}
        title="User Invited"
        secretLabel="Temporary Password"
        secretValue={inviteResult?.temporaryPassword || ''}
        warning={`Share this temporary password with ${inviteResult?.email || 'the user'} securely. It won't be shown again.`}
      />
    </div>
  )
}
