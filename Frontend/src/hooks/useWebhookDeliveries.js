import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import {
  getWebhookDeliveries,
  getWebhookDelivery,
  retryWebhookDelivery,
  replayWebhookEvent,
} from '@/api/webhookDeliveries'
import { toast } from 'sonner'

/**
 * Query hook to fetch paginated webhook deliveries
 * @param {import('@/api/webhookDeliveries').WebhookDeliveryQueryParams} params
 */
export function useWebhookDeliveries(params = {}) {
  return useQuery({
    queryKey: ['webhookDeliveries', params],
    queryFn: () => getWebhookDeliveries(params).then((res) => res.data),
  })
}

/**
 * Query hook to fetch a single webhook delivery
 * @param {string} id
 * @param {boolean} enabled
 */
export function useWebhookDelivery(id, enabled = true) {
  return useQuery({
    queryKey: ['webhookDeliveries', id],
    queryFn: () => getWebhookDelivery(id).then((res) => res.data),
    enabled: enabled && !!id,
  })
}

/**
 * Mutation hook to retry a webhook delivery
 */
export function useRetryWebhookDelivery() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: retryWebhookDelivery,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['webhookDeliveries'] })
      toast.success('Webhook delivery retry initiated')
    },
  })
}

/**
 * Mutation hook to replay a webhook event
 */
export function useReplayWebhookEvent() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: replayWebhookEvent,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['webhookDeliveries'] })
      toast.success('Webhook event replay initiated')
    },
  })
}
