import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import {
  getPayments,
  getPaymentDetail,
  createPayment,
  confirmPayment,
} from '@/api/payments'
import { toast } from 'sonner'

/**
 * Query hook to fetch paginated payments
 * @param {import('@/api/payments').DashboardPaymentQueryParams} params
 */
export function usePayments(params = {}) {
  return useQuery({
    queryKey: ['payments', params],
    queryFn: () => getPayments(params).then((res) => res.data),
  })
}

/**
 * Query hook to fetch a payment by reference (dashboard)
 * @param {string} paymentReference
 * @param {boolean} enabled
 */
export function usePaymentDetail(paymentReference, enabled = true) {
  return useQuery({
    queryKey: ['payments', paymentReference],
    queryFn: () => getPaymentDetail(paymentReference).then((res) => res.data),
    enabled: enabled && !!paymentReference,
  })
}

/**
 * Mutation hook to create a new payment (checkout flow)
 */
export function useCreatePayment() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: createPayment,
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: ['payments'] })
      toast.success('Payment created')
      return response.data
    },
  })
}

/**
 * Mutation hook to confirm a payment (checkout flow)
 */
export function useConfirmPayment() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ paymentReference, data }) =>
      confirmPayment(paymentReference, data),
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: ['payments'] })
      toast.success('Payment confirmed')
      return response.data
    },
  })
}
