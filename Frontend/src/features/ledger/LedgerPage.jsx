import { useState } from 'react'
import { useBalance, useLedgerEntries } from '@/hooks/useLedger'
import { CardSkeleton, TableSkeleton } from '@/components/shared/LoadingSkeleton'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { formatAmount } from '@/lib/utils'
import { format } from 'date-fns'
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from 'recharts'

export function LedgerPage() {
  const { data: balance, isLoading: balanceLoading } = useBalance()
  const [entryFilters, setEntryFilters] = useState({
    page: 0,
    size: 20,
    sort: 'createdAt,desc',
  })

  const { data: entries, isLoading: entriesLoading } = useLedgerEntries(entryFilters)

  const chartData = (entries?.content || []).map((entry) => ({
    date: format(new Date(entry.createdAt), 'MMM d'),
    amount: (entry.type === 'CREDIT' ? entry.amount : -entry.amount) / 100,
    type: entry.type,
  })).reverse()

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">Ledger</h1>
        <p className="text-sm text-muted-foreground">
          Your account balance and activity
        </p>
      </div>

      {balanceLoading ? (
        <CardSkeleton />
      ) : (
        <div className="rounded-lg border p-6">
          <p className="text-sm text-muted-foreground">Available Balance</p>
          <p className="text-3xl font-bold mt-1">
            {balance
              ? formatAmount(balance.availableBalance, balance.currency)
              : '-'}
          </p>
          <p className="text-xs text-muted-foreground mt-1">
            {balance?.currency || 'INR'}
          </p>
        </div>
      )}

      <div className="rounded-lg border p-6">
        <h2 className="text-lg font-semibold mb-4">Balance Trend</h2>
        {chartData.length > 0 ? (
          <ResponsiveContainer width="100%" height={300}>
            <AreaChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="date" />
              <YAxis />
              <Tooltip />
              <Area
                type="monotone"
                dataKey="amount"
                stroke="#18181b"
                fill="#f4f4f5"
                strokeWidth={2}
              />
            </AreaChart>
          </ResponsiveContainer>
        ) : (
          <div className="text-center py-12 text-sm text-muted-foreground">
            {entriesLoading ? 'Loading entries...' : 'No ledger entries yet'}
          </div>
        )}
      </div>

      <div>
        <h2 className="text-lg font-semibold mb-4">Recent Entries</h2>
        {entriesLoading ? (
          <TableSkeleton rows={5} columns={5} />
        ) : (
          <>
            <div className="overflow-x-auto rounded-lg border">
              <table className="w-full caption-bottom text-sm">
                <thead className="[&_tr]:border-b">
                  <tr className="border-b transition-colors hover:bg-muted/50">
                    <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Date</th>
                    <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Account</th>
                    <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Type</th>
                    <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Amount</th>
                    <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Reference</th>
                    <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Description</th>
                  </tr>
                </thead>
                <tbody className="[&_tr:last-child]:border-0">
                  {entries?.content?.map((entry) => (
                    <tr key={entry.id} className="border-b transition-colors hover:bg-muted/50">
                      <td className="p-4 align-middle">
                        {format(new Date(entry.createdAt), 'MMM d, yyyy HH:mm')}
                      </td>
                      <td className="p-4 align-middle font-mono text-xs">{entry.account}</td>
                      <td className="p-4 align-middle">
                        <StatusBadge
                          status={entry.type}
                          className={entry.type === 'CREDIT'
                            ? 'bg-green-100 text-green-800 border-green-200'
                            : 'bg-red-100 text-red-800 border-red-200'}
                        />
                      </td>
                      <td className="p-4 align-middle">
                        {formatAmount(entry.amount, entry.currency)}
                      </td>
                      <td className="p-4 align-middle font-mono text-xs">{entry.reference}</td>
                      <td className="p-4 align-middle text-muted-foreground">{entry.description}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {entries?.totalPages > 1 && (
              <div className="flex items-center justify-between mt-4">
                <p className="text-sm text-muted-foreground">
                  Page {(entries.page || 0) + 1} of {entries.totalPages}
                </p>
                <div className="flex gap-2">
                  <button
                    onClick={() => setEntryFilters((f) => ({ ...f, page: Math.max(0, f.page - 1) }))}
                    disabled={entryFilters.page === 0}
                    className="inline-flex items-center justify-center rounded-md border px-3 py-1.5 text-sm disabled:opacity-50 hover:bg-accent"
                  >
                    Previous
                  </button>
                  <button
                    onClick={() => setEntryFilters((f) => ({ ...f, page: Math.min((entries.totalPages || 1) - 1, f.page + 1) }))}
                    disabled={entryFilters.page >= (entries.totalPages || 1) - 1}
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
    </div>
  )
}
