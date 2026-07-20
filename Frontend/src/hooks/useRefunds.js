import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getRefunds, getRefund, createRefund } from '@/api/refunds'
import { toast } from 'sonner'

/**
 * Query hook to fetch paginated refunds
 * @param {import('@/api/refunds').RefundQueryParams} params
 */
export function useRefunds(params = {}) {
  return useQuery({
    queryKey: ['refunds', params],
    queryFn: () => getRefunds(params).then((res) => res.data),
  })
}

/**
 * Query hook to fetch a refund by reference
 * @param {string} refundReference
 * @param {boolean} enabled
 */
export function useRefund(refundReference, enabled = true) {
  return useQuery({
    queryKey: ['refunds', refundReference],
    queryFn: () => getRefund(refundReference).then((res) => res.data),
    enabled: enabled && !!refundReference,
  })
}

/**
 * Mutation hook to create a refund
 */
export function useCreateRefund() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ paymentReference, data }) =>
      createRefund(paymentReference, data),
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: ['refunds'] })
      toast.success('Refund created')
      return response.data
    },
  })
}
