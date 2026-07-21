import jwtClient from './jwtClient'

/**
 * @typedef {Object} Settlement
 * @property {string} settlementReference
 * @property {number} amount
 * @property {string} currency
 * @property {'PENDING'|'PROCESSING'|'COMPLETED'|'FAILED'} status
 * @property {string|null} processedAt
 * @property {string|null} bankReference
 */

/**
 * @typedef {Object} SettlementQueryParams
 * @property {string} [status]
 * @property {number} [page]
 * @property {number} [size]
 * @property {string} [sort]
 */

/**
 * Get paginated settlements for the dashboard
 * @param {SettlementQueryParams} params
 * @returns {Promise<import('axios').AxiosResponse<{ content: Settlement[], page: number, size: number, totalElements: number, totalPages: number }>>}
 */
export function getSettlements(params = {}) {
  return jwtClient.get('/dashboard/settlements', { params })
}

/**
 * Get a settlement by reference (dashboard)
 * @param {string} settlementReference
 * @returns {Promise<import('axios').AxiosResponse<Settlement>>}
 */
export function getSettlement(settlementReference) {
  return jwtClient.get(`/dashboard/settlements/${settlementReference}`)
}

/**
 * Create a settlement from the dashboard (JWT auth)
 * @returns {Promise<import('axios').AxiosResponse<{ settlementReference: string, amount: number, currency: string, status: string, createdAt: string }>>}
 */
export function createDashboardSettlement() {
  return jwtClient.post('/dashboard/settlements')
}
