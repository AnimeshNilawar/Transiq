import jwtClient from './jwtClient'

/**
 * @typedef {Object} LedgerBalance
 * @property {number} availableBalance
 * @property {string} currency
 */

/**
 * @typedef {Object} LedgerEntry
 * @property {string} id
 * @property {string} account
 * @property {'DEBIT'|'CREDIT'} type
 * @property {number} amount
 * @property {string} currency
 * @property {string} reference
 * @property {string} description
 * @property {string} createdAt
 */

/**
 * @typedef {Object} LedgerEntryQueryParams
 * @property {string} [account]
 * @property {string} [from]
 * @property {string} [to]
 * @property {number} [page]
 * @property {number} [size]
 * @property {string} [sort]
 */

/**
 * Get current ledger balance (dashboard)
 * @returns {Promise<import('axios').AxiosResponse<LedgerBalance>>}
 */
export function getBalance() {
  return jwtClient.get('/dashboard/ledger/balance')
}

/**
 * Get paginated ledger entries
 * @param {LedgerEntryQueryParams} params
 * @returns {Promise<import('axios').AxiosResponse<{ content: LedgerEntry[], page: number, size: number, totalElements: number, totalPages: number }>>}
 */
export function getLedgerEntries(params = {}) {
  return jwtClient.get('/dashboard/ledger/entries', { params })
}
