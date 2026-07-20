import jwtClient from './jwtClient'

/**
 * @typedef {Object} MeResponse
 * @property {string} id
 * @property {string} email
 * @property {string} firstName
 * @property {string} lastName
 * @property {'OWNER'|'ADMIN'|'MEMBER'} role
 * @property {{ id: string, businessName: string, businessEmail: string, status: string }} merchant
 */

/**
 * Get current user profile
 * @returns {Promise<import('axios').AxiosResponse<MeResponse>>}
 */
export function getMe() {
  return jwtClient.get('/dashboard/me')
}
