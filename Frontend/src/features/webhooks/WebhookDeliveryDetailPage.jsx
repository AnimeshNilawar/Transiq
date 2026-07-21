import { useParams, useNavigate } from 'react-router-dom'
import {
  useWebhookDelivery,
  useRetryWebhookDelivery,
} from '@/hooks/useWebhookDeliveries'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { DetailSkeleton } from '@/components/shared/LoadingSkeleton'
import { Item } from '@/components/shared/Item'
import { format } from 'date-fns'
import { ArrowLeft, RefreshCw, Loader2 } from 'lucide-react'

export default function WebhookDeliveryDetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { data: delivery, isLoading } = useWebhookDelivery(id)
  const retryMutation = useRetryWebhookDelivery()

  if (isLoading) return <DetailSkeleton />

  if (!delivery) {
    return (
      <div className="text-center py-12">
        <p className="text-muted-foreground">Delivery not found</p>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <button
          onClick={() => navigate('/webhooks/deliveries')}
          className="inline-flex items-center justify-center rounded-md border border-border p-2 hover:bg-muted transition-colors text-muted-foreground"
        >
          <ArrowLeft className="h-4 w-4" />
        </button>
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-foreground">
            Webhook Delivery
          </h1>
          <p className="text-sm text-muted-foreground font-mono">{delivery.id}</p>
        </div>
      </div>

      <div className="bg-card rounded-lg border border-border p-6 space-y-4">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-card-foreground">Details</h2>
          <StatusBadge status={delivery.status} />
        </div>
        <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
          <Item label="Event Type" value={delivery.eventType} />
          <Item label="Event Reference" value={delivery.eventReference} mono />
          <Item label="Endpoint ID" value={delivery.endpointId} mono />
          <Item label="HTTP Status" value={delivery.httpStatus || '-'} mono />
          <Item label="Attempt Count" value={delivery.attemptCount} mono />
          <Item label="Duration" value={`${delivery.durationMs}ms`} mono />
          <Item
            label="Created"
            value={format(
              new Date(delivery.createdAt),
              'MMM d, yyyy HH:mm:ss'
            )}
          />
          <Item
            label="Last Attempt"
            value={
              delivery.lastAttemptAt
                ? format(new Date(delivery.lastAttemptAt), 'MMM d, yyyy HH:mm:ss')
                : '-'
            }
          />
          <Item
            label="Next Retry"
            value={
              delivery.nextRetryAt
                ? format(new Date(delivery.nextRetryAt), 'MMM d, yyyy HH:mm:ss')
                : '-'
            }
          />
        </div>

        {delivery.failureReason && (
          <div className="pt-4 border-t border-border">
            <p className="text-sm font-medium text-destructive mb-1">
              Failure Reason
            </p>
            <p className="text-sm text-muted-foreground bg-muted p-3 rounded-md font-mono">
              {delivery.failureReason}
            </p>
          </div>
        )}
      </div>

      <div className="bg-card rounded-lg border border-border p-6 space-y-4">
        <h2 className="text-lg font-semibold text-card-foreground">Retry Policy</h2>
        <div className="text-sm text-muted-foreground space-y-2">
          <p>
            Failed deliveries are retried with exponential backoff:
          </p>
          <ul className="list-disc list-inside space-y-1 ml-4">
            <li>1st retry: 1 minute</li>
            <li>2nd retry: 2 minutes</li>
            <li>3rd retry: 4 minutes</li>
            <li>4th retry: 8 minutes</li>
            <li>5th retry: 16 minutes</li>
          </ul>
          <p>Maximum 5 retry attempts per delivery.</p>
        </div>

        {delivery.status !== 'DELIVERED' && (
          <button
            onClick={() => retryMutation.mutateAsync(delivery.id)}
            disabled={retryMutation.isPending}
            className="inline-flex items-center gap-2 rounded-md bg-accent px-4 py-2 text-sm font-medium text-accent-foreground hover:bg-accent/90 disabled:opacity-50 transition-colors"
          >
            {retryMutation.isPending ? (
              <Loader2 className="h-4 w-4 animate-spin" />
            ) : (
              <RefreshCw className="h-4 w-4" />
            )}
            Manual Retry
          </button>
        )}
      </div>
    </div>
  )
}
