import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getApiKeys, createApiKey, revokeApiKey, rotateApiKey } from '@/api/apiKeys'
import { toast } from 'sonner'

/**
 * Query hook to fetch all API keys
 */
export function useApiKeys() {
  return useQuery({
    queryKey: ['apiKeys'],
    queryFn: () => getApiKeys().then((res) => res.data),
  })
}

/**
 * Mutation hook to create a new API key
 */
export function useCreateApiKey() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: createApiKey,
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: ['apiKeys'] })
      toast.success('API key created successfully')
      return response.data
    },
  })
}

/**
 * Mutation hook to revoke an API key
 */
export function useRevokeApiKey() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: revokeApiKey,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['apiKeys'] })
      toast.success('API key revoked')
    },
  })
}

/**
 * Mutation hook to rotate an API key
 */
export function useRotateApiKey() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: rotateApiKey,
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: ['apiKeys'] })
      toast.success('API key rotated successfully')
      return response.data
    },
  })
}
