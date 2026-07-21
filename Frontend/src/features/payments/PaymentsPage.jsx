import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { usePayments } from '@/hooks/usePayments'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { TableSkeleton } from '@/components/shared/LoadingSkeleton'
import { Pagination } from '@/components/shared/Pagination'
import { formatAmount } from '@/lib/utils'
import { exportToCSV } from '@/lib/export'
import { format } from 'date-fns'
import { Search, Download } from 'lucide-react'

export default function PaymentsPage() {
  const navigate = useNavigate()
  const [searchRef, setSearchRef] = useState('')
  const [filters, setFilters] = useState({
    page: 0,
    size: 20,
    status: '',
    orderId: '',
    sort: 'createdAt,desc',
  })

  const queryFilters = {
    page: filters.page,
    size: filters.size,
    sort: filters.sort,
  }
  if (filters.status) queryFilters.status = filters.status
  if (filters.orderId) queryFilters.orderId = filters.orderId

  const { data, isLoading } = usePayments(queryFilters)

  const handleRefSearch = (e) => {
    e.preventDefault()
    if (searchRef.trim()) {
      navigate(`/payments/${searchRef.trim()}`)
    }
  }

  const columns = [
    {
      key: 'paymentReference',
      header: 'Payment Ref',
      render: (val) => <span className="font-mono text-xs">{val}</span>,
    },
    {
      key: 'orderId',
      header: 'Order ID',
      render: (val) => <span className="font-mono text-xs">{val || '-'}</span>,
    },
    {
      key: 'amount',
      header: 'Amount',
      render: (val, row) => <span className="font-mono tabular-nums">{formatAmount(val, row.currency)}</span>,
    },
    {
      key: 'status',
      header: 'Status',
      render: (val) => <StatusBadge status={val} />,
    },
    {
      key: 'customerEmail',
      header: 'Customer',
      render: (val) => val || '-',
    },
    {
      key: 'createdAt',
      header: 'Created',
      render: (val) => format(new Date(val), 'MMM d, HH:mm'),
    },
  ]

  return (
    <div className="space-y-4">
      <div>
        <h1 className="text-2xl font-bold tracking-tight text-foreground">Payments</h1>
        <p className="text-sm text-muted-foreground">
          {data?.totalElements || 0} total payment{(data?.totalElements || 0) !== 1 ? 's' : ''}
        </p>
      </div>

      <div className="flex items-center gap-3 flex-wrap">
        <form onSubmit={handleRefSearch} className="flex gap-2">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <input
              type="text"
              value={searchRef}
              onChange={(e) => setSearchRef(e.target.value)}
              placeholder="Search by ref..."
              className="rounded-md border border-border bg-card pl-9 pr-3 py-1.5 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring w-48"
            />
          </div>
          <button
            type="submit"
            className="inline-flex items-center justify-center rounded-md bg-accent px-3 py-1.5 text-sm font-medium text-accent-foreground hover:bg-accent/90 transition-colors"
          >
            Go
          </button>
        </form>

        <select
          value={filters.status}
          onChange={(e) =>
            setFilters((f) => ({ ...f, status: e.target.value, page: 0 }))
          }
          className="rounded-md border border-border bg-card px-3 py-1.5 text-sm text-card-foreground"
        >
          <option value="">All Statuses</option>
          <option value="CREATED">Created</option>
          <option value="REQUIRES_PAYMENT_METHOD">Requires Payment Method</option>
          <option value="PROCESSING">Processing</option>
          <option value="SUCCEEDED">Succeeded</option>
          <option value="FAILED">Failed</option>
          <option value="CANCELLED">Cancelled</option>
          <option value="REFUNDED">Refunded</option>
          <option value="EXPIRED">Expired</option>
        </select>

        <input
          type="text"
          value={filters.orderId}
          onChange={(e) =>
            setFilters((f) => ({ ...f, orderId: e.target.value, page: 0 }))
          }
          placeholder="Filter by Order ID..."
          className="rounded-md border border-border bg-card px-3 py-1.5 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring w-48"
        />

        <button
          onClick={() => exportToCSV(data?.content || [], columns, 'payments')}
          disabled={!data?.content?.length}
          className="inline-flex items-center gap-2 rounded-md border border-border px-3 py-1.5 text-sm text-card-foreground hover:bg-muted disabled:opacity-50 transition-colors"
        >
          <Download className="h-4 w-4" />
          Export CSV
        </button>
      </div>

      {isLoading ? (
        <TableSkeleton rows={5} columns={6} />
      ) : (
        <>
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
                    key={row.id}
                    className="border-b transition-colors hover:bg-muted/50 cursor-pointer"
                    onClick={() => navigate(`/payments/${row.paymentReference}`)}
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
        </>
      )}
    </div>
  )
}
