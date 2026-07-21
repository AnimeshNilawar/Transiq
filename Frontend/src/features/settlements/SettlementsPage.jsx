import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useSettlements, useDashboardCreateSettlement } from '@/hooks/useSettlements'
import { useBalance } from '@/hooks/useLedger'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { TableSkeleton } from '@/components/shared/LoadingSkeleton'
import { Pagination } from '@/components/shared/Pagination'
import { formatAmount } from '@/lib/utils'
import { exportToCSV } from '@/lib/export'
import { format } from 'date-fns'
import { Download, Banknote, Loader2 } from 'lucide-react'

export default function SettlementsPage() {
  const navigate = useNavigate()
  const createSettlementMutation = useDashboardCreateSettlement()
  const { data: balance } = useBalance()
  const [showConfirmModal, setShowConfirmModal] = useState(false)
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

  const { data, isLoading } = useSettlements(queryFilters)

  const availableBalance = balance?.availableBalance || 0
  const canSettle = availableBalance > 0

  const handleSettle = async () => {
    try {
      await createSettlementMutation.mutateAsync()
      setShowConfirmModal(false)
    } catch {
      // Error handled by interceptor
    }
  }

  const columns = [
    {
      key: 'settlementReference',
      header: 'Settlement Ref',
      render: (val) => <span className="font-mono text-xs">{val}</span>,
    },
    {
      key: 'amount',
      header: 'Amount',
      render: (val, row) => <span className="font-mono tabular-nums">{formatAmount(val, row.currency)}</span>,
    },
    {
      key: 'currency',
      header: 'Currency',
      render: (val) => <span className="font-mono">{val}</span>,
    },
    {
      key: 'status',
      header: 'Status',
      render: (val) => <StatusBadge status={val} />,
    },
    {
      key: 'bankReference',
      header: 'Bank Ref',
      render: (val) => <span className="font-mono text-xs">{val || '-'}</span>,
    },
    {
      key: 'processedAt',
      header: 'Processed',
      render: (val) => (val ? format(new Date(val), 'MMM d, yyyy') : '-'),
    },
  ]

  if (isLoading) return <TableSkeleton rows={5} columns={6} />

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-foreground">Settlements</h1>
          <p className="text-sm text-muted-foreground">
            {data?.totalElements || 0} total settlement{(data?.totalElements || 0) !== 1 ? 's' : ''}
          </p>
        </div>
        <button
          onClick={() => setShowConfirmModal(true)}
          disabled={!canSettle}
          className="inline-flex items-center gap-2 rounded-md bg-accent px-4 py-2 text-sm font-medium text-accent-foreground hover:bg-accent/90 transition-colors disabled:opacity-50"
        >
          <Banknote className="h-4 w-4" />
          Request Settlement
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
          <option value="PENDING">Pending</option>
          <option value="PROCESSING">Processing</option>
          <option value="COMPLETED">Completed</option>
          <option value="FAILED">Failed</option>
        </select>

        <button
          onClick={() => exportToCSV(data?.content || [], columns, 'settlements')}
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
                key={row.settlementReference}
                className="border-b transition-colors hover:bg-muted/50 cursor-pointer"
                onClick={() => navigate(`/settlements/${row.settlementReference}`)}
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

      {showConfirmModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
          <div className="fixed inset-0 bg-black/50" onClick={() => setShowConfirmModal(false)} />
          <div className="relative bg-card rounded-lg border border-border shadow-lg p-6 w-full max-w-md mx-4">
            <h2 className="text-lg font-semibold text-card-foreground mb-2">Request Settlement</h2>
            <p className="text-sm text-muted-foreground mb-4">
              Settle your available balance to your bank account. This action cannot be undone.
            </p>
            <div className="bg-muted rounded-md p-4 mb-4">
              <p className="text-xs text-muted-foreground mb-1">Available Balance</p>
              <p className="text-2xl font-bold font-mono tabular-nums text-card-foreground">
                {formatAmount(availableBalance, balance?.currency || 'INR')}
              </p>
            </div>
            <div className="flex gap-2">
              <button
                onClick={handleSettle}
                disabled={createSettlementMutation.isPending || !canSettle}
                className="flex-1 inline-flex items-center justify-center rounded-md bg-accent px-4 py-2 text-sm font-medium text-accent-foreground hover:bg-accent/90 disabled:opacity-50"
              >
                {createSettlementMutation.isPending && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                Confirm Settlement
              </button>
              <button
                type="button"
                onClick={() => setShowConfirmModal(false)}
                className="inline-flex items-center justify-center rounded-md border border-border px-4 py-2 text-sm text-card-foreground hover:bg-muted"
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
