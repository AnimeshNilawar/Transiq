import { useParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { getAdminMerchantDetail } from '@/api/admin'

export default function AdminMerchantDetailPage() {
  const { id } = useParams()

  const { data: merchant, isLoading } = useQuery({
    queryKey: ['admin-merchant', id],
    queryFn: () => getAdminMerchantDetail(id).then((r) => r.data),
  })

  if (isLoading) return <div className="p-6 text-muted-foreground">Loading merchant...</div>
  if (!merchant) return <div className="p-6 text-muted-foreground">Merchant not found</div>

  const stats = [
    { label: 'Users', value: merchant.userCount },
    { label: 'API Keys', value: merchant.apiKeyCount },
    { label: 'Total Payments', value: merchant.totalPaymentCount },
    { label: 'Total Volume', value: `₹${(merchant.totalPaymentVolume / 100).toLocaleString()}` },
  ]

  return (
    <div className="space-y-6 p-6">
      <h1 className="text-2xl font-bold text-card-foreground">{merchant.businessName}</h1>

      <div className="bg-card rounded-lg border border-border p-6 space-y-4">
        <DetailRow label="Business Email" value={merchant.businessEmail} />
        <DetailRow label="Status" value={merchant.status} />
        <DetailRow label="Created" value={new Date(merchant.createdAt).toLocaleString()} />
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        {stats.map((s) => (
          <div key={s.label} className="bg-card rounded-lg border border-border p-6">
            <p className="text-sm font-medium text-muted-foreground">{s.label}</p>
            <p className="mt-2 text-2xl font-bold text-card-foreground">{s.value}</p>
          </div>
        ))}
      </div>
    </div>
  )
}

function DetailRow({ label, value }) {
  return (
    <div className="flex items-start gap-4">
      <span className="w-36 shrink-0 text-sm font-medium text-muted-foreground">{label}</span>
      <span className="text-sm text-card-foreground">{value}</span>
    </div>
  )
}
