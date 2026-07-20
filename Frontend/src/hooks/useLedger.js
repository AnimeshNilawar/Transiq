import { useQuery } from '@tanstack/react-query'
import { getBalance, getLedgerEntries } from '@/api/ledger'

/**
 * Query hook to fetch current ledger balance
 */
export function useBalance() {
  return useQuery({
    queryKey: ['ledger', 'balance'],
    queryFn: () => getBalance().then((res) => res.data),
  })
}

/**
 * Query hook to fetch paginated ledger entries
 * @param {import('@/api/ledger').LedgerEntryQueryParams} params
 */
export function useLedgerEntries(params = {}) {
  return useQuery({
    queryKey: ['ledger', 'entries', params],
    queryFn: () => getLedgerEntries(params).then((res) => res.data),
  })
}
