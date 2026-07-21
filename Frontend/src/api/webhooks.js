import jwtClient from './jwtClient'
import apiKeyClient from './apiKeyClient'

/**
 * @typedef {Object} WebhookEndpoint
 * @property {string} id
 * @property {string} url
 * @property {'ACTIVE'|'DISABLED'} status
 */

/**
 * @typedef {Object} WebhookCreateResponse
 * @property {string} id
 * @property {string} url
 * @property {string} secret
 */

/**
 * Get all webhook endpoints (dashboard, JWT auth)
 * @returns {Promise<import('axios').AxiosResponse<WebhookEndpoint[]>>}
 */
export function getWebhooks() {
  return jwtClient.get('/dashboard/webhooks')
}

/**
 * Get a webhook endpoint by ID (dashboard, JWT auth)
 * @param {string} id
 * @returns {Promise<import('axios').AxiosResponse<WebhookEndpoint>>}
 */
export function getWebhook(id) {
  return jwtClient.get(`/dashboard/webhooks/${id}`)
}

/**
 * Create a new webhook endpoint (API key auth)
 * @param {{ url: string }} data
 * @returns {Promise<import('axios').AxiosResponse<WebhookCreateResponse>>}
 */
export function createWebhook(data) {
  return apiKeyClient.post('/webhooks', data)
}

/**
 * Delete (disable) a webhook endpoint (dashboard, JWT auth)
 * @param {string} id - Webhook endpoint ID
 * @returns {Promise<import('axios').AxiosResponse>}
 */
export function deleteWebhook(id) {
  return jwtClient.delete(`/dashboard/webhooks/${id}`)
}
