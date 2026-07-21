import { useState } from 'react'
import {
  useApiKeys,
  useCreateApiKey,
  useRevokeApiKey,
} from '@/hooks/useApiKeys'
import { CopyModal } from '@/components/shared/CopyModal'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { TableSkeleton } from '@/components/shared/LoadingSkeleton'
import { format } from 'date-fns'
import { Plus, Trash2, Loader2 } from 'lucide-react'

export default function ApiKeysPage() {
  const { data: apiKeys, isLoading } = useApiKeys()
  const createMutation = useCreateApiKey()
  const revokeMutation = useRevokeApiKey()
  const [showCreateModal, setShowCreateModal] = useState(false)
  const [newKeyData, setNewKeyData] = useState(null)
  const [form, setForm] = useState({
    name: '',
    environment: 'LIVE',
    type: 'SECRET',
  })

  const handleCreate = async (e) => {
    e.preventDefault()
    try {
      const response = await createMutation.mutateAsync(form)
      setNewKeyData(response.data)
      setShowCreateModal(false)
      setForm({ name: '', environment: 'LIVE', type: 'SECRET' })
    } catch {
      // Error handled by interceptor
    }
  }

  const handleRevoke = async (id) => {
    if (window.confirm('Are you sure you want to revoke this API key?')) {
      await revokeMutation.mutateAsync(id)
    }
  }

  if (isLoading) return <TableSkeleton rows={3} columns={5} />

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-foreground">API Keys</h1>
          <p className="text-sm text-muted-foreground">
            Manage your API keys for authentication
          </p>
        </div>
        <button
          onClick={() => setShowCreateModal(true)}
          className="inline-flex items-center gap-2 rounded-md bg-accent px-4 py-2 text-sm font-medium text-accent-foreground hover:bg-accent/90 transition-colors"
        >
          <Plus className="h-4 w-4" />
          Create API Key
        </button>
      </div>

      <div className="overflow-x-auto rounded-lg border border-border">
        <table className="w-full caption-bottom text-sm">
          <thead className="[&_tr]:border-b">
            <tr className="border-b transition-colors hover:bg-muted/50">
              <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">
                Name
              </th>
              <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">
                Prefix
              </th>
              <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">
                Environment
              </th>
              <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">
                Type
              </th>
              <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">
                Status
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
            {apiKeys?.map((key) => (
              <tr
                key={key.id}
                className="border-b transition-colors hover:bg-muted/50"
              >
                <td className="p-4 align-middle font-medium text-card-foreground">{key.name}</td>
                <td className="p-4 align-middle font-mono text-xs">
                  {key.prefix}
                </td>
                <td className="p-4 align-middle">
                  <StatusBadge
                    status={key.environment}
                    className={
                      key.environment === 'LIVE'
                        ? 'bg-blue-100 text-blue-800 border-blue-200'
                        : 'bg-purple-100 text-purple-800 border-purple-200'
                    }
                  />
                </td>
                <td className="p-4 align-middle font-mono text-xs">{key.type}</td>
                <td className="p-4 align-middle">
                  <StatusBadge status={key.status} />
                </td>
                <td className="p-4 align-middle text-muted-foreground">
                  {format(new Date(key.createdAt), 'MMM d, yyyy')}
                </td>
                <td className="p-4 align-middle">
                  {key.status === 'ACTIVE' ? (
                    <button
                      onClick={() => handleRevoke(key.id)}
                      disabled={revokeMutation.isPending}
                      className="inline-flex items-center gap-1 text-sm text-destructive hover:underline disabled:opacity-50"
                    >
                      <Trash2 className="h-3.5 w-3.5" />
                      Revoke
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

      {apiKeys?.length === 0 && (
        <div className="text-center py-8 text-sm text-muted-foreground border border-border rounded-lg">
          No API keys yet. Create one to get started.
        </div>
      )}

      {showCreateModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
          <div
            className="fixed inset-0 bg-black/50"
            onClick={() => setShowCreateModal(false)}
          />
          <div className="relative bg-card rounded-lg border border-border shadow-lg p-6 w-full max-w-md mx-4">
            <h2 className="text-lg font-semibold text-card-foreground mb-4">Create API Key</h2>
            <form onSubmit={handleCreate} className="space-y-4">
              <div>
                <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                  Name
                </label>
                <input
                  type="text"
                  value={form.name}
                  onChange={(e) =>
                    setForm((f) => ({ ...f, name: e.target.value }))
                  }
                  required
                  className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring"
                  placeholder="Production Secret Key"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                  Environment
                </label>
                <select
                  value={form.environment}
                  onChange={(e) =>
                    setForm((f) => ({ ...f, environment: e.target.value }))
                  }
                  className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground"
                >
                  <option value="TEST">Test</option>
                  <option value="LIVE">Live</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                  Type
                </label>
                <select
                  value={form.type}
                  onChange={(e) =>
                    setForm((f) => ({ ...f, type: e.target.value }))
                  }
                  className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground"
                >
                  <option value="SECRET">Secret</option>
                  <option value="PUBLISHABLE">Publishable</option>
                  <option value="RESTRICTED">Restricted</option>
                </select>
              </div>
              <div className="flex gap-2 pt-2">
                <button
                  type="submit"
                  disabled={createMutation.isPending}
                  className="flex-1 inline-flex items-center justify-center rounded-md bg-accent px-4 py-2 text-sm font-medium text-accent-foreground hover:bg-accent/90 disabled:opacity-50"
                >
                  {createMutation.isPending && (
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  )}
                  Create
                </button>
                <button
                  type="button"
                  onClick={() => setShowCreateModal(false)}
                  className="inline-flex items-center justify-center rounded-md border border-border px-4 py-2 text-sm text-card-foreground hover:bg-muted"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <CopyModal
        open={!!newKeyData}
        onClose={() => setNewKeyData(null)}
        title="API Key Created"
        secretLabel="Your API Key"
        secretValue={newKeyData?.apiKey || ''}
        warning="This is the only time you'll see this key. Copy and store it securely. You won't be able to retrieve it later."
      />
    </div>
  )
}
