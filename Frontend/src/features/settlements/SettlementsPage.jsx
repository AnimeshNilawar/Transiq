import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useSettlements } from '@/hooks/useSettlements'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { TableSkeleton } from '@/components/shared/LoadingSkeleton'
import { formatAmount } from '@/lib/utils'
import { format } from 'date-fns'

export function SettlementsPage() {
  const navigate = useNavigate()
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

  const columns = [
    {
      key: 'settlementReference',
      header: 'Settlement Ref',
      render: (val) => <span className="font-mono text-xs">{val}</span>,
    },
    {
      key: 'amount',
      header: 'Amount',
      render: (val, row) => formatAmount(val, row.currency),
    },
    { key: 'currency', header: 'Currency' },
    {
      key: 'status',
      header: 'Status',
      render: (val) => <StatusBadge status={val} />,
    },
    {
      key: 'bankReference',
      header: 'Bank Ref',
      render: (val) => val || '-',
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
      <div>
        <h1 className="text-2xl font-bold tracking-tight">Settlements</h1>
        <p className="text-sm text-muted-foreground">
          {data?.totalElements || 0} total settlement{(data?.totalElements || 0) !== 1 ? 's' : ''}
        </p>
      </div>

      <div className="flex items-center gap-3">
        <select
          value={filters.status}
          onChange={(e) =>
            setFilters((f) => ({ ...f, status: e.target.value, page: 0 }))
          }
          className="rounded-md border px-3 py-1.5 text-sm"
        >
          <option value="">All Statuses</option>
          <option value="PENDING">Pending</option>
          <option value="PROCESSING">Processing</option>
          <option value="COMPLETED">Completed</option>
          <option value="FAILED">Failed</option>
        </select>
      </div>

      <div className="overflow-x-auto rounded-lg border">
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
                  <td key={col.key} className="p-4 align-middle">
                    {col.render ? col.render(row[col.key], row) : row[col.key] ?? '-'}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {data?.totalPages > 1 && (
        <div className="flex items-center justify-between">
          <p className="text-sm text-muted-foreground">
            Page {(data.page || 0) + 1} of {data.totalPages}
          </p>
          <div className="flex gap-2">
            <button
              onClick={() => setFilters((f) => ({ ...f, page: Math.max(0, f.page - 1) }))}
              disabled={filters.page === 0}
              className="inline-flex items-center justify-center rounded-md border px-3 py-1.5 text-sm disabled:opacity-50 hover:bg-accent"
            >
              Previous
            </button>
            <button
              onClick={() => setFilters((f) => ({ ...f, page: Math.min((data.totalPages || 1) - 1, f.page + 1) }))}
              disabled={filters.page >= (data.totalPages || 1) - 1}
              className="inline-flex items-center justify-center rounded-md border px-3 py-1.5 text-sm disabled:opacity-50 hover:bg-accent"
            >
              Next
            </button>
          </div>
        </div>
      )}
    </div>
  )
}
