import { useQuery } from '@tanstack/react-query'
import { getSettlements, getSettlement } from '@/api/settlements'

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
