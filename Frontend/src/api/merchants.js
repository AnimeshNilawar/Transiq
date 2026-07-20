import jwtClient from './jwtClient'

/**
 * @typedef {Object} Merchant
 * @property {string} id
 * @property {string} businessName
 * @property {string} businessEmail
 * @property {'ACTIVE'|'INACTIVE'|'SUSPENDED'} status
 * @property {string} createdAt
 */

/**
 * @typedef {Object} MerchantRegisterRequest
 * @property {string} businessName
 * @property {string} businessEmail
 */

/**
 * Register a new merchant
 * @param {MerchantRegisterRequest} data
 * @returns {Promise<import('axios').AxiosResponse<Merchant>>}
 */
export function registerMerchant(data) {
  return jwtClient.post('/merchants/register', data)
}
