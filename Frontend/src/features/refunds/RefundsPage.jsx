import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useRefunds, useDashboardCreateRefund } from '@/hooks/useRefunds'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { TableSkeleton } from '@/components/shared/LoadingSkeleton'
import { Pagination } from '@/components/shared/Pagination'
import { formatAmount } from '@/lib/utils'
import { exportToCSV } from '@/lib/export'
import { format } from 'date-fns'
import { Download, Plus, Loader2 } from 'lucide-react'

const REFUND_REASONS = [
  { value: 'REQUESTED_BY_CUSTOMER', label: 'Requested by customer' },
  { value: 'DUPLICATE_PAYMENT', label: 'Duplicate payment' },
  { value: 'FRAUDULENT', label: 'Fraudulent' },
  { value: 'PRODUCT_UNAVAILABLE', label: 'Product unavailable' },
  { value: 'OTHER', label: 'Other' },
]

export default function RefundsPage() {
  const navigate = useNavigate()
  const createRefundMutation = useDashboardCreateRefund()
  const [showCreateModal, setShowCreateModal] = useState(false)
  const [refundForm, setRefundForm] = useState({
    paymentReference: '',
    amount: '',
    reason: 'REQUESTED_BY_CUSTOMER',
  })
  const [refundError, setRefundError] = useState('')
  const [filters, setFilters] = useState({
    page: 0,
    size: 20,
    status: '',
    sort: 'createdAt,desc',
  })

  const queryFilters = {
    page: filters.page,
    size: filters.size,
    sort: filters.sort,
  }
  if (filters.status) queryFilters.status = filters.status

  const { data, isLoading } = useRefunds(queryFilters)

  const handleCreateRefund = async (e) => {
    e.preventDefault()
    setRefundError('')

    const amountInPaise = Math.round(parseFloat(refundForm.amount) * 100)
    if (!amountInPaise || amountInPaise <= 0) {
      setRefundError('Enter a valid refund amount')
      return
    }

    try {
      await createRefundMutation.mutateAsync({
        paymentReference: refundForm.paymentReference,
        amount: amountInPaise,
        reason: refundForm.reason,
      })
      setShowCreateModal(false)
      setRefundForm({ paymentReference: '', amount: '', reason: 'REQUESTED_BY_CUSTOMER' })
    } catch {
      // Error handled by interceptor
    }
  }

  const columns = [
    {
      key: 'refundReference',
      header: 'Refund Ref',
      render: (val) => <span className="font-mono text-xs">{val}</span>,
    },
    {
      key: 'paymentReference',
      header: 'Payment Ref',
      render: (val) => <span className="font-mono text-xs">{val}</span>,
    },
    {
      key: 'amount',
      header: 'Amount',
      render: (val, row) => <span className="font-mono tabular-nums">{formatAmount(val, row.currency || 'INR')}</span>,
    },
    {
      key: 'status',
      header: 'Status',
      render: (val) => <StatusBadge status={val} />,
    },
    { key: 'reason', header: 'Reason' },
    {
      key: 'createdAt',
      header: 'Created',
      render: (val) => format(new Date(val), 'MMM d, HH:mm'),
    },
  ]

  if (isLoading) return <TableSkeleton rows={5} columns={6} />

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-foreground">Refunds</h1>
          <p className="text-sm text-muted-foreground">
            {data?.totalElements || 0} total refund{(data?.totalElements || 0) !== 1 ? 's' : ''}
          </p>
        </div>
        <button
          onClick={() => setShowCreateModal(true)}
          className="inline-flex items-center gap-2 rounded-md bg-accent px-4 py-2 text-sm font-medium text-accent-foreground hover:bg-accent/90 transition-colors"
        >
          <Plus className="h-4 w-4" />
          Create Refund
        </button>
      </div>

      <div className="flex items-center gap-3">
        <select
          value={filters.status}
          onChange={(e) =>
            setFilters((f) => ({ ...f, status: e.target.value, page: 0 }))
          }
          className="rounded-md border border-border bg-card px-3 py-1.5 text-sm text-card-foreground"
        >
          <option value="">All Statuses</option>
          <option value="CREATED">Created</option>
          <option value="PROCESSING">Processing</option>
          <option value="SUCCEEDED">Succeeded</option>
          <option value="FAILED">Failed</option>
        </select>

        <button
          onClick={() => exportToCSV(data?.content || [], columns, 'refunds')}
          disabled={!data?.content?.length}
          className="inline-flex items-center gap-2 rounded-md border border-border px-3 py-1.5 text-sm text-card-foreground hover:bg-muted disabled:opacity-50 transition-colors"
        >
          <Download className="h-4 w-4" />
          Export CSV
        </button>
      </div>

      <div className="overflow-x-auto rounded-lg border border-border">
        <table className="w-full caption-bottom text-sm">
          <thead className="[&_tr]:border-b">
            <tr className="border-b transition-colors hover:bg-muted/50">
              {columns.map((col) => (
                <th key={col.key} className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">
                  {col.header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="[&_tr:last-child]:border-0">
            {data?.content?.map((row) => (
              <tr
                key={row.refundReference}
                className="border-b transition-colors hover:bg-muted/50 cursor-pointer"
                onClick={() => navigate(`/refunds/${row.refundReference}`)}
              >
                {columns.map((col) => (
                  <td key={col.key} className="p-4 align-middle text-card-foreground">
                    {col.render ? col.render(row[col.key], row) : row[col.key] ?? '-'}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Pagination
        page={filters.page}
        totalPages={data?.totalPages || 1}
        onPageChange={(p) => setFilters((f) => ({ ...f, page: p }))}
      />

      {showCreateModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
          <div className="fixed inset-0 bg-black/50" onClick={() => setShowCreateModal(false)} />
          <div className="relative bg-card rounded-lg border border-border shadow-lg p-6 w-full max-w-md mx-4">
            <h2 className="text-lg font-semibold text-card-foreground mb-4">Create Refund</h2>
            <form onSubmit={handleCreateRefund} className="space-y-4">
              <div>
                <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                  Payment Reference
                </label>
                <input
                  type="text"
                  value={refundForm.paymentReference}
                  onChange={(e) => setRefundForm((f) => ({ ...f, paymentReference: e.target.value }))}
                  required
                  className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring font-mono"
                  placeholder="pay_xxxx"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                  Amount (in INR)
                </label>
                <input
                  type="number"
                  step="0.01"
                  min="0.01"
                  value={refundForm.amount}
                  onChange={(e) => setRefundForm((f) => ({ ...f, amount: e.target.value }))}
                  required
                  className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring font-mono"
                  placeholder="100.00"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                  Reason
                </label>
                <select
                  value={refundForm.reason}
                  onChange={(e) => setRefundForm((f) => ({ ...f, reason: e.target.value }))}
                  className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground"
                >
                  {REFUND_REASONS.map((r) => (
                    <option key={r.value} value={r.value}>{r.label}</option>
                  ))}
                </select>
              </div>

              {refundError && (
                <p className="text-sm text-destructive bg-destructive/10 p-2 rounded">{refundError}</p>
              )}

              <div className="flex gap-2 pt-2">
                <button
                  type="submit"
                  disabled={createRefundMutation.isPending}
                  className="flex-1 inline-flex items-center justify-center rounded-md bg-accent px-4 py-2 text-sm font-medium text-accent-foreground hover:bg-accent/90 disabled:opacity-50"
                >
                  {createRefundMutation.isPending && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                  Create Refund
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
    </div>
  )
}
