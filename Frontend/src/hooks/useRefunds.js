import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getRefunds, getRefund, createRefund, createDashboardRefund } from '@/api/refunds'
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
 * Mutation hook to create a refund (API key auth, checkout flow)
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

/**
 * Mutation hook to create a refund from the dashboard (JWT auth)
 */
export function useDashboardCreateRefund() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (data) => createDashboardRefund(data),
    onSuccess: (response, variables) => {
      queryClient.invalidateQueries({ queryKey: ['refunds'] })

      const refundAmount = response.data?.amount ?? variables.amount
      queryClient.setQueryData(
        ['payments', variables.paymentReference],
        (old) => {
          if (!old) return old
          const newRefunded = (old.refundedAmount || 0) + refundAmount
          return {
            ...old,
            refundedAmount: newRefunded,
            status: newRefunded >= old.amount ? 'REFUNDED' : old.status,
          }
        }
      )

      queryClient.setQueriesData(
        { queryKey: ['payments'], exact: false },
        (old) => {
          if (!old?.content) return old
          return {
            ...old,
            content: old.content.map((p) =>
              p.paymentReference === variables.paymentReference
                ? {
                    ...p,
                    refundedAmount: (p.refundedAmount || 0) + refundAmount,
                  }
                : p
            ),
          }
        }
      )

      toast.success('Refund created')
      return response.data
    },
  })
}
