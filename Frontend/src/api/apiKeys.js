import jwtClient from './jwtClient'

/**
 * @typedef {Object} ApiKey
 * @property {string} id
 * @property {string} name
 * @property {string} prefix
 * @property {'TEST'|'LIVE'} environment
 * @property {'SECRET'|'PUBLISHABLE'|'RESTRICTED'} type
 * @property {'ACTIVE'|'REVOKED'} status
 * @property {string|null} lastUsedAt
 * @property {string} createdAt
 */

/**
 * @typedef {Object} ApiKeyCreateRequest
 * @property {string} name
 * @property {'TEST'|'LIVE'} environment
 * @property {'SECRET'|'PUBLISHABLE'|'RESTRICTED'} type
 */

/**
 * @typedef {Object} ApiKeyCreateResponse
 * @property {string} id
 * @property {string} apiKey
 * @property {string} prefix
 * @property {string} createdAt
 */

/**
 * Get all API keys for the merchant
 * @returns {Promise<import('axios').AxiosResponse<ApiKey[]>>}
 */
export function getApiKeys() {
  return jwtClient.get('/api-keys')
}

/**
 * Create a new API key
 * @param {ApiKeyCreateRequest} data
 * @returns {Promise<import('axios').AxiosResponse<ApiKeyCreateResponse>>}
 */
export function createApiKey(data) {
  return jwtClient.post('/api-keys', data)
}

/**
 * Revoke (delete) an API key
 * @param {string} id - API key ID
 * @returns {Promise<import('axios').AxiosResponse>}
 */
export function revokeApiKey(id) {
  return jwtClient.delete(`/api-keys/${id}`)
}

/**
 * Rotate an API key
 * @param {string} id - API key ID
 * @returns {Promise<import('axios').AxiosResponse<ApiKeyCreateResponse>>}
 */
export function rotateApiKey(id) {
  return jwtClient.post(`/api-keys/${id}/rotate`)
}
