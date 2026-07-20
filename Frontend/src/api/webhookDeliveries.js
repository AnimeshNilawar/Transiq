import jwtClient from './jwtClient'

/**
 * @typedef {Object} WebhookDelivery
 * @property {string} id
 * @property {string} eventId
 * @property {string} endpointId
 * @property {string} eventReference
 * @property {'PAYMENT_SUCCEEDED'|'PAYMENT_FAILED'|'REFUND_SUCCEEDED'|'SETTLEMENT_COMPLETED'} eventType
 * @property {'PENDING'|'DELIVERED'|'FAILED'} status
 * @property {number} attemptCount
 * @property {number} httpStatus
 * @property {string|null} failureReason
 * @property {number} durationMs
 * @property {string|null} deliveredAt
 * @property {string} createdAt
 * @property {string|null} lastAttemptAt
 * @property {string|null} nextRetryAt
 */

/**
 * @typedef {Object} WebhookDeliveryPage
 * @property {WebhookDelivery[]} content
 * @property {number} page
 * @property {number} size
 * @property {number} totalElements
 * @property {number} totalPages
 */

/**
 * @typedef {Object} WebhookDeliveryQueryParams
 * @property {string} [status]
 * @property {string} [eventType]
 * @property {string} [endpointId]
 * @property {string} [eventId]
 * @property {string} [from]
 * @property {string} [to]
 * @property {number} [page]
 * @property {number} [size]
 * @property {string} [sort]
 */

/**
 * Get paginated webhook deliveries (dashboard, JWT auth)
 * @param {WebhookDeliveryQueryParams} params
 * @returns {Promise<import('axios').AxiosResponse<WebhookDeliveryPage>>}
 */
export function getWebhookDeliveries(params = {}) {
  return jwtClient.get('/dashboard/webhooks/deliveries', { params })
}

/**
 * Get a single webhook delivery by ID (dashboard, JWT auth)
 * @param {string} id
 * @returns {Promise<import('axios').AxiosResponse<WebhookDelivery>>}
 */
export function getWebhookDelivery(id) {
  return jwtClient.get(`/dashboard/webhooks/deliveries/${id}`)
}

/**
 * Retry a failed webhook delivery (dashboard, JWT auth)
 * @param {string} id
 * @returns {Promise<import('axios').AxiosResponse>}
 */
export function retryWebhookDelivery(id) {
  return jwtClient.post(`/dashboard/webhooks/deliveries/${id}/retry`)
}

/**
 * Replay a webhook event to all active endpoints (dashboard, JWT auth)
 * @param {string} eventId
 * @returns {Promise<import('axios').AxiosResponse>}
 */
export function replayWebhookEvent(eventId) {
  return jwtClient.post(`/dashboard/webhooks/events/${eventId}/replay`)
}
