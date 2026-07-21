import { useState, useMemo } from 'react'
import { useBalance, useLedgerEntries } from '@/hooks/useLedger'
import { getLedgerEntries } from '@/api/ledger'
import { CardSkeleton, TableSkeleton } from '@/components/shared/LoadingSkeleton'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { Pagination } from '@/components/shared/Pagination'
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

export default function LedgerPage() {
  const { data: balance, isLoading: balanceLoading } = useBalance()
  const [entryFilters, setEntryFilters] = useState({
    page: 0,
    size: 20,
    sort: 'createdAt,desc',
  })

  const { data: entries, isLoading: entriesLoading } = useLedgerEntries(entryFilters)
  const [allEntries, setAllEntries] = useState(null)
  const [loadingAll, setLoadingAll] = useState(false)

  const fetchAllEntries = async () => {
    if (allEntries) return allEntries
    setLoadingAll(true)
    try {
      const firstPage = await getLedgerEntries({ page: 0, size: 100, sort: 'createdAt,asc' })
      const totalPages = firstPage.data.totalPages || 1
      let allData = [...(firstPage.data.content || [])]
      for (let p = 1; p < totalPages; p++) {
        const pageData = await getLedgerEntries({ page: p, size: 100, sort: 'createdAt,asc' })
        allData = allData.concat(pageData.data.content || [])
      }
      setAllEntries(allData)
      setLoadingAll(false)
      return allData
    } catch {
      setLoadingAll(false)
      return null
    }
  }

  const chartData = useMemo(() => {
    const pageEntries = entries?.content || []
    const source = allEntries || pageEntries

    const merchantEntries = source.filter((e) => e.account === 'MERCHANT_PAYABLE')
    const sorted = [...merchantEntries].sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))

    let running = 0
    const data = sorted.map((entry) => {
      const delta = entry.type === 'CREDIT' ? entry.amount : -entry.amount
      running += delta
      return {
        date: format(new Date(entry.createdAt), 'MMM d'),
        amount: running / 100,
        type: entry.type,
      }
    })

    if (balance && data.length > 0) {
      const lastPoint = data[data.length - 1]
      const available = balance.availableBalance
      if (Math.abs(lastPoint.amount - available / 100) > 0.01) {
        data.push({
          date: 'Now',
          amount: available / 100,
          type: 'BALANCE',
        })
      }
    }

    return data
  }, [entries, allEntries, balance])

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
          <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Available Balance</p>
          <p className="text-3xl font-bold mt-1 font-mono tabular-nums">
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
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-semibold">Balance Trend</h2>
          {!allEntries && !loadingAll && chartData.length > 0 && (
            <button
              onClick={fetchAllEntries}
              className="text-xs text-muted-foreground hover:text-foreground transition-colors"
            >
              Load full history
            </button>
          )}
        </div>
        {loadingAll ? (
          <div className="flex items-center justify-center py-12">
            <div className="h-6 w-6 animate-spin rounded-full border-2 border-slate-300 border-t-indigo-600" />
          </div>
        ) : chartData.length > 0 ? (
          <ResponsiveContainer width="100%" height={300}>
            <AreaChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" className="stroke-border" />
              <XAxis dataKey="date" className="text-xs" tick={{ fill: 'hsl(var(--color-muted-foreground))' }} />
              <YAxis className="text-xs" tick={{ fill: 'hsl(var(--color-muted-foreground))' }} />
              <Tooltip
                contentStyle={{ backgroundColor: 'hsl(var(--color-card))', border: '1px solid hsl(var(--color-border))', borderRadius: '0.375rem' }}
                formatter={(value) => [`₹${value.toFixed(2)}`, 'Balance']}
              />
              <Area
                type="monotone"
                dataKey="amount"
                stroke="hsl(var(--color-chart-1))"
                fill="hsl(var(--color-chart-1))"
                fillOpacity={0.2}
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
                      <td className="p-4 align-middle font-mono tabular-nums">
                        {formatAmount(entry.amount, entry.currency)}
                      </td>
                      <td className="p-4 align-middle font-mono text-xs">{entry.reference}</td>
                      <td className="p-4 align-middle text-muted-foreground">{entry.description}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <div className="mt-4">
              <Pagination
                page={entryFilters.page}
                totalPages={entries?.totalPages || 1}
                onPageChange={(p) => setEntryFilters((f) => ({ ...f, page: p }))}
              />
            </div>
          </>
        )}
      </div>
    </div>
  )
}
