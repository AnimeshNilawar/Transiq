import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getSettlements, getSettlement, createDashboardSettlement } from '@/api/settlements'
import { toast } from 'sonner'

/**
 * Query hook to fetch paginated settlements
 * @param {import('@/api/settlements').SettlementQueryParams} params
 */
export function useSettlements(params = {}) {
  return useQuery({
    queryKey: ['settlements', params],
    queryFn: () => getSettlements(params).then((res) => res.data),
  })
}

/**
 * Query hook to fetch a settlement by reference
 * @param {string} settlementReference
 * @param {boolean} enabled
 */
export function useSettlement(settlementReference, enabled = true) {
  return useQuery({
    queryKey: ['settlements', settlementReference],
    queryFn: () => getSettlement(settlementReference).then((res) => res.data),
    enabled: enabled && !!settlementReference,
  })
}

/**
 * Mutation hook to create a settlement from the dashboard (JWT auth)
 */
export function useDashboardCreateSettlement() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: () => createDashboardSettlement(),
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: ['settlements'] })
      queryClient.invalidateQueries({ queryKey: ['ledger'] })
      toast.success('Settlement created')
      return response.data
    },
  })
}
