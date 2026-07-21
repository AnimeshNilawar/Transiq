import apiKeyClient from './apiKeyClient'
import { generateIdempotencyKey } from '@/lib/utils'

/**
 * @typedef {Object} Payment
 * @property {string} id
 * @property {string} paymentReference
 * @property {string} [clientSecret]
 * @property {number} [amount]
 * @property {string} [currency]
 * @property {'CREATED'|'REQUIRES_PAYMENT_METHOD'|'PROCESSING'|'SUCCEEDED'|'FAILED'|'CANCELLED'|'REFUNDED'|'EXPIRED'} status
 * @property {string} [customerEmail]
 * @property {string} [customerName]
 * @property {string} [orderId]
 * @property {string} [description]
 * @property {string} createdAt
 */

/**
 * @typedef {Object} PaymentCreateRequest
 * @property {number} amount
 * @property {'INR'|'USD'|'EUR'} currency
 * @property {string} [customerEmail]
 * @property {string} [customerName]
 * @property {string} orderId
 * @property {string} [description]
 */

/**
 * @typedef {Object} PaymentConfirmRequest
 * @property {string} clientSecret
 * @property {'CARD'|'UPI'|'NET_BANKING'|'WALLET'|'UNKNOWN'} paymentMethodType
 * @property {'VISA'|'MASTERCARD'|'RUPAY'} [cardNetwork]
 * @property {'HDFC'|'ICICI'|'SBI'|'AXIS'} [issuerBank]
 * @property {string} [maskedCardNumber]
 * @property {number} [expiryMonth]
 * @property {number} [expiryYear]
 * @property {string} [upiId]
 */

// ─── Dashboard (JWT auth) ──────────────────────────────────────────

import jwtClient from './jwtClient'

/**
 * @typedef {Object} DashboardPaymentQueryParams
 * @property {string} [status]
 * @property {string} [from]
 * @property {string} [to]
 * @property {string} [orderId]
 * @property {number} [page]
 * @property {number} [size]
 * @property {string} [sort]
 */

/**
 * Get paginated payments for the dashboard
 * @param {DashboardPaymentQueryParams} params
 * @returns {Promise<import('axios').AxiosResponse<import('./webhookDeliveries').WebhookDeliveryPage & { content: Payment[] }>>}
 */
export function getPayments(params = {}) {
  return jwtClient.get('/dashboard/payments', { params })
}

/**
 * Get a payment detail by reference (dashboard)
 * @param {string} paymentReference
 * @returns {Promise<import('axios').AxiosResponse<Payment>>}
 */
export function getPaymentDetail(paymentReference) {
  return jwtClient.get(`/dashboard/payments/${paymentReference}`)
}

// ─── Checkout (API key auth) ───────────────────────────────────────

/**
 * Create a new payment (checkout flow, API key auth)
 * @param {PaymentCreateRequest} data
 * @returns {Promise<import('axios').AxiosResponse<Payment>>}
 */
export function createPayment(data) {
  return apiKeyClient.post('/payments', data, {
    headers: { 'Idempotency-Key': generateIdempotencyKey() },
  })
}

/**
 * Get a payment by reference (checkout flow, API key auth)
 * @param {string} paymentReference
 * @returns {Promise<import('axios').AxiosResponse<Payment>>}
 */
export function getPayment(paymentReference) {
  return apiKeyClient.get(`/payments/${paymentReference}`)
}

/**
 * Confirm a payment with payment method details (checkout flow, API key auth)
 * @param {string} paymentReference
 * @param {PaymentConfirmRequest} data
 * @returns {Promise<import('axios').AxiosResponse<Payment>>}
 */
export function confirmPayment(paymentReference, data) {
  return apiKeyClient.post(`/payments/${paymentReference}/confirm`, data)
}
