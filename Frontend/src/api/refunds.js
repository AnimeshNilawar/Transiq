import jwtClient from './jwtClient'
import apiKeyClient from './apiKeyClient'
import { generateIdempotencyKey } from '@/lib/utils'

/**
 * @typedef {Object} Refund
 * @property {string} refundReference
 * @property {string} paymentReference
 * @property {number} amount
 * @property {'CREATED'|'PROCESSING'|'SUCCEEDED'|'FAILED'} status
 * @property {'REQUESTED_BY_CUSTOMER'|'DUPLICATE_PAYMENT'|'FRAUDULENT'|'PRODUCT_UNAVAILABLE'|'OTHER'} reason
 * @property {string} createdAt
 */

/**
 * @typedef {Object} RefundCreateRequest
 * @property {number} amount
 * @property {'REQUESTED_BY_CUSTOMER'|'DUPLICATE_PAYMENT'|'FRAUDULENT'|'PRODUCT_UNAVAILABLE'|'OTHER'} reason
 */

/**
 * @typedef {Object} RefundQueryParams
 * @property {string} [status]
 * @property {number} [page]
 * @property {number} [size]
 * @property {string} [sort]
 */

/**
 * Get paginated refunds for the dashboard
 * @param {RefundQueryParams} params
 * @returns {Promise<import('axios').AxiosResponse<{ content: Refund[], page: number, size: number, totalElements: number, totalPages: number }>>}
 */
export function getRefunds(params = {}) {
  return jwtClient.get('/dashboard/refunds', { params })
}

/**
 * Get a refund by reference (dashboard)
 * @param {string} refundReference
 * @returns {Promise<import('axios').AxiosResponse<Refund>>}
 */
export function getRefund(refundReference) {
  return jwtClient.get(`/dashboard/refunds/${refundReference}`)
}

/**
 * Create a refund for a payment (API key auth, checkout flow)
 * @param {string} paymentReference
 * @param {RefundCreateRequest} data
 * @returns {Promise<import('axios').AxiosResponse<Refund>>}
 */
export function createRefund(paymentReference, data) {
  return apiKeyClient.post(`/refunds/${paymentReference}`, data, {
    headers: { 'Idempotency-Key': generateIdempotencyKey() },
  })
}

/**
 * Create a refund from the dashboard (JWT auth)
 * @param {{ paymentReference: string, amount: number, reason: string }} data
 * @returns {Promise<import('axios').AxiosResponse<{ refundReference: string, amount: number, status: string }>>}
 */
export function createDashboardRefund(data) {
  return jwtClient.post('/dashboard/refunds', data, {
    params: { paymentReference: data.paymentReference },
  })
}
