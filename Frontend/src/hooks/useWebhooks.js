import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getWebhooks, getWebhook, createWebhook, deleteWebhook } from '@/api/webhooks'
import { toast } from 'sonner'

/**
 * Query hook to fetch all webhook endpoints (dashboard)
 */
export function useWebhooks() {
  return useQuery({
    queryKey: ['webhooks'],
    queryFn: () => getWebhooks().then((res) => res.data),
  })
}

/**
 * Query hook to fetch a single webhook endpoint by ID
 * @param {string} id
 * @param {boolean} enabled
 */
export function useWebhook(id, enabled = true) {
  return useQuery({
    queryKey: ['webhooks', id],
    queryFn: () => getWebhook(id).then((res) => res.data),
    enabled: enabled && !!id,
  })
}

/**
 * Mutation hook to create a webhook endpoint (API key auth)
 */
export function useCreateWebhook() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: createWebhook,
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: ['webhooks'] })
      toast.success('Webhook endpoint created')
      return response.data
    },
  })
}

/**
 * Mutation hook to delete (disable) a webhook endpoint (API key auth)
 */
export function useDeleteWebhook() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: deleteWebhook,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['webhooks'] })
      toast.success('Webhook endpoint disabled')
    },
  })
}
