import { useState } from 'react'
import { useWebhooks, useCreateWebhook, useDeleteWebhook } from '@/hooks/useWebhooks'
import { CopyModal } from '@/components/shared/CopyModal'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { TableSkeleton } from '@/components/shared/LoadingSkeleton'
import { Plus, Trash2, Loader2 } from 'lucide-react'

export function WebhooksPage() {
  const { data: webhooks, isLoading } = useWebhooks()
  const createMutation = useCreateWebhook()
  const deleteMutation = useDeleteWebhook()
  const [showCreateModal, setShowCreateModal] = useState(false)
  const [newWebhookData, setNewWebhookData] = useState(null)
  const [url, setUrl] = useState('')

  const handleCreate = async (e) => {
    e.preventDefault()
    try {
      const response = await createMutation.mutateAsync({ url })
      setNewWebhookData(response.data)
      setShowCreateModal(false)
      setUrl('')
    } catch {
      // Error handled by interceptor
    }
  }

  const handleDelete = async (id) => {
    if (window.confirm('Disable this webhook endpoint?')) {
      await deleteMutation.mutateAsync(id)
    }
  }

  if (isLoading) return <TableSkeleton rows={3} columns={3} />

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Webhooks</h1>
          <p className="text-sm text-muted-foreground">
            Manage webhook endpoints for event notifications
          </p>
        </div>
        <button
          onClick={() => setShowCreateModal(true)}
          className="inline-flex items-center gap-2 rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90 transition-colors"
        >
          <Plus className="h-4 w-4" />
          Add Endpoint
        </button>
      </div>

      <div className="overflow-x-auto rounded-lg border">
        <table className="w-full caption-bottom text-sm">
          <thead className="[&_tr]:border-b">
            <tr className="border-b transition-colors hover:bg-muted/50">
              <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">
                URL
              </th>
              <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">
                Status
              </th>
              <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">
                Actions
              </th>
            </tr>
          </thead>
          <tbody className="[&_tr:last-child]:border-0">
            {webhooks?.map((webhook) => (
              <tr
                key={webhook.id}
                className="border-b transition-colors hover:bg-muted/50"
              >
                <td className="p-4 align-middle font-mono text-xs">
                  {webhook.url}
                </td>
                <td className="p-4 align-middle">
                  <StatusBadge status={webhook.status} />
                </td>
                <td className="p-4 align-middle">
                  {webhook.status === 'ACTIVE' && (
                    <button
                      onClick={() => handleDelete(webhook.id)}
                      disabled={deleteMutation.isPending}
                      className="inline-flex items-center gap-1 text-sm text-destructive hover:underline disabled:opacity-50"
                    >
                      <Trash2 className="h-3.5 w-3.5" />
                      Disable
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {webhooks?.length === 0 && (
        <div className="text-center py-8 text-sm text-muted-foreground border rounded-lg">
          No webhook endpoints configured yet.
        </div>
      )}

      {showCreateModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
          <div
            className="fixed inset-0 bg-black/50"
            onClick={() => setShowCreateModal(false)}
          />
          <div className="relative bg-background rounded-lg border shadow-lg p-6 w-full max-w-md mx-4">
            <h2 className="text-lg font-semibold mb-4">
              Add Webhook Endpoint
            </h2>
            <form onSubmit={handleCreate} className="space-y-4">
              <div>
                <label className="block text-sm font-medium mb-1.5">
                  Endpoint URL
                </label>
                <input
                  type="url"
                  value={url}
                  onChange={(e) => setUrl(e.target.value)}
                  required
                  className="w-full rounded-md border px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-ring"
                  placeholder="https://api.example.com/webhooks/transiq"
                />
              </div>
              <div className="flex gap-2 pt-2">
                <button
                  type="submit"
                  disabled={createMutation.isPending}
                  className="flex-1 inline-flex items-center justify-center rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90 disabled:opacity-50"
                >
                  {createMutation.isPending && (
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  )}
                  Create
                </button>
                <button
                  type="button"
                  onClick={() => setShowCreateModal(false)}
                  className="inline-flex items-center justify-center rounded-md border px-4 py-2 text-sm hover:bg-accent"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <CopyModal
        open={!!newWebhookData}
        onClose={() => setNewWebhookData(null)}
        title="Webhook Endpoint Created"
        secretLabel="Signing Secret"
        secretValue={newWebhookData?.secret || ''}
        warning="This is the only time you'll see the signing secret. Copy and store it securely. Use it to verify incoming webhook payloads."
      />
    </div>
  )
}
