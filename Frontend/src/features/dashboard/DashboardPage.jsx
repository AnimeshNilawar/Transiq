import { useBalance } from '@/hooks/useLedger'
import { usePayments } from '@/hooks/usePayments'
import { useRefunds } from '@/hooks/useRefunds'
import { useSettlements } from '@/hooks/useSettlements'
import { useWebhookDeliveries } from '@/hooks/useWebhookDeliveries'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { CardSkeleton, TableSkeleton } from '@/components/shared/LoadingSkeleton'
import { formatAmount } from '@/lib/utils'
import { format } from 'date-fns'
import { useNavigate } from 'react-router-dom'
import { Wallet, CreditCard, RotateCcw, CheckCircle, Banknote } from 'lucide-react'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell } from 'recharts'

export default function DashboardPage() {
  const { data: balance, isLoading: balanceLoading } = useBalance()
  const { data: paymentsData, isLoading: paymentsLoading } = usePayments({
    page: 0,
    size: 5,
    sort: 'createdAt,desc',
  })
  const { data: refundsData } = useRefunds({
    page: 0,
    size: 100,
    sort: 'createdAt,desc',
  })
  const { data: settlementsData } = useSettlements({
    page: 0,
    size: 100,
    sort: 'createdAt,desc',
  })
  const { data: deliveries, isLoading: deliveriesLoading } = useWebhookDeliveries({
    size: 5,
    sort: 'createdAt,desc',
  })
  const navigate = useNavigate()

  const allPayments = paymentsData?.content || []
  const totalPayments = paymentsData?.totalElements || 0
  const succeededPayments = allPayments.filter((p) => p.status === 'SUCCEEDED').length
  const successRate = allPayments.length > 0 ? ((succeededPayments / allPayments.length) * 100).toFixed(1) : '0.0'
  const totalRefunds = refundsData?.totalElements || 0
  const totalRefundAmount = (refundsData?.content || []).reduce((sum, r) => sum + (r.amount || 0), 0)
  const totalSettlements = settlementsData?.totalElements || 0

  const statusCounts = allPayments.reduce((acc, p) => {
    acc[p.status] = (acc[p.status] || 0) + 1
    return acc
  }, {})

  const chartData = [
    { name: 'Succeeded', count: statusCounts.SUCCEEDED || 0, fill: 'var(--color-success)' },
    { name: 'Failed', count: statusCounts.FAILED || 0, fill: 'var(--color-destructive)' },
    { name: 'Processing', count: statusCounts.PROCESSING || 0, fill: 'var(--color-warning)' },
    { name: 'Created', count: statusCounts.CREATED || 0, fill: 'var(--color-muted-foreground)' },
    { name: 'Cancelled', count: statusCounts.CANCELLED || 0, fill: 'var(--color-chart-3)' },
  ].filter((d) => d.count > 0)

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight text-foreground">Dashboard</h1>
        <p className="text-sm text-muted-foreground">
          Overview of your account
        </p>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
        {balanceLoading ? (
          <>
            <CardSkeleton />
            <CardSkeleton />
            <CardSkeleton />
            <CardSkeleton />
            <CardSkeleton />
          </>
        ) : (
          <>
            <StatCard
              title="Available Balance"
              value={balance ? formatAmount(balance.availableBalance, balance.currency) : '-'}
              icon={Wallet}
              description="Current ledger balance"
            />
            <StatCard
              title="Total Payments"
              value={totalPayments.toString()}
              icon={CreditCard}
              description="All-time payments"
            />
            <StatCard
              title="Success Rate"
              value={`${successRate}%`}
              icon={CheckCircle}
              description="Payment success rate"
            />
            <StatCard
              title="Total Refunds"
              value={`${totalRefunds} (${totalRefunds > 0 ? formatAmount(totalRefundAmount) : '₹ 0.00'})`}
              icon={RotateCcw}
              description="All-time refunds"
            />
            <StatCard
              title="Settlements"
              value={totalSettlements.toString()}
              icon={Banknote}
              description="All-time settlements"
            />
          </>
        )}
      </div>

      {paymentsLoading ? (
        <CardSkeleton />
      ) : chartData.length > 0 ? (
        <div className="bg-card rounded-lg border border-border p-6">
          <h2 className="text-lg font-semibold text-card-foreground mb-4">Payment Status Distribution</h2>
          <ResponsiveContainer width="100%" height={250}>
            <BarChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" className="stroke-border" />
              <XAxis dataKey="name" className="text-xs" tick={{ fill: 'hsl(var(--color-muted-foreground))' }} />
              <YAxis className="text-xs" tick={{ fill: 'hsl(var(--color-muted-foreground))' }} allowDecimals={false} />
              <Tooltip
                contentStyle={{ backgroundColor: 'hsl(var(--color-card))', border: '1px solid hsl(var(--color-border))', borderRadius: '0.375rem' }}
              />
              <Bar dataKey="count" radius={[4, 4, 0, 0]}>
                {chartData.map((entry, index) => (
                  <Cell key={index} fill={entry.fill} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      ) : null}

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div>
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-foreground">Recent Payments</h2>
            <button
              onClick={() => navigate('/payments')}
              className="text-sm text-accent font-medium hover:underline"
            >
              View all
            </button>
          </div>

          {paymentsLoading ? (
            <TableSkeleton rows={5} columns={4} />
          ) : allPayments.length > 0 ? (
            <div className="overflow-x-auto rounded-lg border border-border">
              <table className="w-full caption-bottom text-sm">
                <thead className="[&_tr]:border-b">
                  <tr className="border-b transition-colors hover:bg-muted/50">
                    <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Reference</th>
                    <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Amount</th>
                    <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Status</th>
                    <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Created</th>
                  </tr>
                </thead>
                <tbody className="[&_tr:last-child]:border-0">
                  {allPayments.map((payment) => (
                    <tr
                      key={payment.id}
                      className="border-b transition-colors hover:bg-muted/50 cursor-pointer"
                      onClick={() => navigate(`/payments/${payment.paymentReference}`)}
                    >
                      <td className="p-4 align-middle font-mono text-xs text-card-foreground">{payment.paymentReference}</td>
                      <td className="p-4 align-middle font-mono tabular-nums text-card-foreground">{formatAmount(payment.amount, payment.currency)}</td>
                      <td className="p-4 align-middle"><StatusBadge status={payment.status} /></td>
                      <td className="p-4 align-middle text-muted-foreground">{format(new Date(payment.createdAt), 'MMM d, HH:mm')}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="text-center py-8 text-sm text-muted-foreground border border-border rounded-lg">
              No payments yet
            </div>
          )}
        </div>

        <div>
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-foreground">Recent Webhook Deliveries</h2>
            <button
              onClick={() => navigate('/webhooks/deliveries')}
              className="text-sm text-accent font-medium hover:underline"
            >
              View all
            </button>
          </div>

          {deliveriesLoading ? (
            <TableSkeleton rows={5} columns={4} />
          ) : deliveries?.content?.length > 0 ? (
            <div className="overflow-x-auto rounded-lg border border-border">
              <table className="w-full caption-bottom text-sm">
                <thead className="[&_tr]:border-b">
                  <tr className="border-b transition-colors hover:bg-muted/50">
                    <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Event</th>
                    <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Reference</th>
                    <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Status</th>
                    <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Created</th>
                  </tr>
                </thead>
                <tbody className="[&_tr:last-child]:border-0">
                  {deliveries.content.map((delivery) => (
                    <tr
                      key={delivery.id}
                      className="border-b transition-colors hover:bg-muted/50 cursor-pointer"
                      onClick={() => navigate(`/webhooks/deliveries/${delivery.id}`)}
                    >
                      <td className="p-4 align-middle font-medium text-card-foreground text-sm">{delivery.eventType}</td>
                      <td className="p-4 align-middle font-mono text-xs text-card-foreground">{delivery.eventReference}</td>
                      <td className="p-4 align-middle"><StatusBadge status={delivery.status} /></td>
                      <td className="p-4 align-middle text-muted-foreground text-sm">{format(new Date(delivery.createdAt), 'MMM d, HH:mm')}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="text-center py-8 text-sm text-muted-foreground border border-border rounded-lg">
              No webhook deliveries yet
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

function StatCard({ title, value, icon: Icon, description }) {
  return (
    <div className="bg-card rounded-lg border border-border p-6">
      <div className="flex items-center justify-between mb-2">
        <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">{title}</p>
        <Icon className="h-4 w-4 text-muted-foreground" />
      </div>
      <div className="text-2xl font-bold font-mono tabular-nums text-card-foreground">{value}</div>
      <p className="text-xs text-muted-foreground mt-1">{description}</p>
    </div>
  )
}
