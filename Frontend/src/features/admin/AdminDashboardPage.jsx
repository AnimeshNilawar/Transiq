import { useQueries } from '@tanstack/react-query'
import { getAdminDashboard, getAdminRevenueTimeSeries, getAdminFailureTrend, getAdminAlerts } from '@/api/admin'
import {
  AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  BarChart, Bar, Legend,
} from 'recharts'

export default function AdminDashboardPage() {
  const results = useQueries({
    queries: [
      { queryKey: ['admin-dashboard'], queryFn: () => getAdminDashboard().then((r) => r.data) },
      { queryKey: ['admin-revenue'], queryFn: () => getAdminRevenueTimeSeries().then((r) => r.data) },
      { queryKey: ['admin-failure-trend'], queryFn: () => getAdminFailureTrend().then((r) => r.data) },
      { queryKey: ['admin-alerts'], queryFn: () => getAdminAlerts().then((r) => r.data) },
    ],
  })

  const [dashboard, revenue, failureTrend, alerts] = results.map((r) => r.data)
  const loading = results.some((r) => r.isLoading)

  if (loading) return <div className="p-6 text-muted-foreground">Loading admin dashboard...</div>

  const stats = [
    { label: 'Total Merchants', value: dashboard?.totalMerchants ?? 0 },
    { label: 'Total Payments', value: dashboard?.totalPayments ?? 0 },
    { label: 'Total Volume', value: `₹${((dashboard?.totalVolume ?? 0) / 100).toLocaleString()}` },
    { label: 'Total Refunds', value: dashboard?.totalRefunds ?? 0 },
  ]

  const chartColor = 'var(--color-chart-1)'
  const successColor = 'var(--color-chart-4)'
  const failureColor = 'var(--color-destructive)'

  return (
    <div className="space-y-6 p-6">
      <h1 className="text-2xl font-bold text-card-foreground">Admin Dashboard</h1>

      {/* Alerts */}
      {alerts && alerts.length > 0 && (
        <div className="space-y-2">
          <h2 className="text-lg font-semibold text-card-foreground">Alerts</h2>
          {alerts.map((a, i) => (
            <div
              key={i}
              className={`rounded-lg border px-4 py-3 text-sm ${
                a.severity === 'error'
                  ? 'border-destructive/30 bg-destructive/5 text-destructive'
                  : 'border-warning/30 bg-warning/5 text-warning'
              }`}
            >
              <span className="font-semibold">{a.merchantName}:</span> {a.message}
            </div>
          ))}
        </div>
      )}

      {/* Stats */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        {stats.map((stat) => (
          <div key={stat.label} className="bg-card rounded-lg border border-border p-6">
            <p className="text-sm font-medium text-muted-foreground">{stat.label}</p>
            <p className="mt-2 text-2xl font-bold text-card-foreground">{stat.value}</p>
          </div>
        ))}
      </div>

      {/* Revenue Chart */}
      <div className="bg-card rounded-lg border border-border p-6">
        <h2 className="text-lg font-semibold text-card-foreground mb-4">Revenue (Last 30 Days)</h2>
        {revenue && revenue.length > 0 ? (
          <ResponsiveContainer width="100%" height={300}>
            <AreaChart data={revenue}>
              <defs>
                <linearGradient id="revenueGrad" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor={chartColor} stopOpacity={0.3} />
                  <stop offset="95%" stopColor={chartColor} stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)" />
              <XAxis dataKey="date" tick={{ fill: 'var(--color-muted-foreground)', fontSize: 12 }} />
              <YAxis tick={{ fill: 'var(--color-muted-foreground)', fontSize: 12 }} />
              <Tooltip
                contentStyle={{ backgroundColor: 'var(--color-card)', border: '1px solid var(--color-border)', borderRadius: '8px' }}
                formatter={(value) => [`₹${(Number(value) / 100).toLocaleString()}`, 'Volume']}
              />
              <Area type="monotone" dataKey="volume" stroke={chartColor} fill="url(#revenueGrad)" strokeWidth={2} />
            </AreaChart>
          </ResponsiveContainer>
        ) : (
          <p className="text-sm text-muted-foreground">No revenue data yet</p>
        )}
      </div>

      {/* Failure Trend Chart */}
      <div className="bg-card rounded-lg border border-border p-6">
        <h2 className="text-lg font-semibold text-card-foreground mb-4">Payment Status (Last 30 Days)</h2>
        {failureTrend && failureTrend.length > 0 ? (
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={failureTrend}>
              <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)" />
              <XAxis dataKey="date" tick={{ fill: 'var(--color-muted-foreground)', fontSize: 12 }} />
              <YAxis tick={{ fill: 'var(--color-muted-foreground)', fontSize: 12 }} />
              <Tooltip
                contentStyle={{ backgroundColor: 'var(--color-card)', border: '1px solid var(--color-border)', borderRadius: '8px' }}
              />
              <Legend />
              <Bar dataKey="succeeded" fill={successColor} name="Succeeded" stackId="a" />
              <Bar dataKey="failed" fill={failureColor} name="Failed" stackId="a" />
            </BarChart>
          </ResponsiveContainer>
        ) : (
          <p className="text-sm text-muted-foreground">No payment data yet</p>
        )}
      </div>

      {/* Recent Payments */}
      <div className="bg-card rounded-lg border border-border p-6">
        <h2 className="text-lg font-semibold text-card-foreground mb-4">Recent Payments</h2>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b text-left text-muted-foreground">
                <th className="pb-2 font-medium">Reference</th>
                <th className="pb-2 font-medium">Merchant</th>
                <th className="pb-2 font-medium">Amount</th>
                <th className="pb-2 font-medium">Status</th>
                <th className="pb-2 font-medium">Date</th>
              </tr>
            </thead>
            <tbody>
              {dashboard?.recentPayments?.length === 0 && (
                <tr><td colSpan={5} className="pt-4 text-center text-muted-foreground">No payments yet</td></tr>
              )}
              {dashboard?.recentPayments?.map((p) => (
                <tr key={p.paymentReference} className="border-b last:border-0">
                  <td className="py-2 font-mono text-xs text-card-foreground">{p.paymentReference}</td>
                  <td className="py-2 text-card-foreground">{p.merchantName}</td>
                  <td className="py-2 font-mono tabular-nums text-card-foreground">₹{(p.amount / 100).toLocaleString()}</td>
                  <td className="py-2">
                    <span className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${
                      p.status === 'SUCCEEDED' ? 'bg-success/10 text-success' :
                      p.status === 'FAILED' ? 'bg-destructive/10 text-destructive' :
                      'bg-warning/10 text-warning'
                    }`}>{p.status}</span>
                  </td>
                  <td className="py-2 text-muted-foreground">{new Date(p.createdAt).toLocaleDateString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
