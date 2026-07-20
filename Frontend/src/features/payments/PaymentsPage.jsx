import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { usePayments } from '@/hooks/usePayments'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { TableSkeleton } from '@/components/shared/LoadingSkeleton'
import { formatAmount } from '@/lib/utils'
import { format } from 'date-fns'
import { Search } from 'lucide-react'

export function PaymentsPage() {
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
      render: (val) => val || '-',
    },
    {
      key: 'amount',
      header: 'Amount',
      render: (val, row) => formatAmount(val, row.currency),
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
        <h1 className="text-2xl font-bold tracking-tight">Payments</h1>
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
              className="rounded-md border pl-9 pr-3 py-1.5 text-sm outline-none focus:ring-2 focus:ring-ring w-48"
            />
          </div>
          <button
            type="submit"
            className="inline-flex items-center justify-center rounded-md bg-primary px-3 py-1.5 text-sm font-medium text-primary-foreground hover:bg-primary/90 transition-colors"
          >
            Go
          </button>
        </form>

        <select
          value={filters.status}
          onChange={(e) =>
            setFilters((f) => ({ ...f, status: e.target.value, page: 0 }))
          }
          className="rounded-md border px-3 py-1.5 text-sm"
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
          className="rounded-md border px-3 py-1.5 text-sm outline-none focus:ring-2 focus:ring-ring w-48"
        />
      </div>

      {isLoading ? (
        <TableSkeleton rows={5} columns={6} />
      ) : (
        <>
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
                    key={row.id}
                    className="border-b transition-colors hover:bg-muted/50 cursor-pointer"
                    onClick={() => navigate(`/payments/${row.paymentReference}`)}
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
        </>
      )}
    </div>
  )
}
