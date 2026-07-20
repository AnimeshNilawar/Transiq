import jwtClient from './jwtClient'

/**
 * @typedef {Object} User
 * @property {string} id
 * @property {string} email
 * @property {string} firstName
 * @property {string} lastName
 * @property {'OWNER'|'ADMIN'|'MEMBER'} role
 * @property {string} createdAt
 */

/**
 * @typedef {Object} InviteUserRequest
 * @property {string} email
 * @property {string} [firstName]
 * @property {string} [lastName]
 * @property {'OWNER'|'ADMIN'|'MEMBER'} role
 */

/**
 * @typedef {Object} InviteUserResponse
 * @property {string} id
 * @property {string} email
 * @property {string} temporaryPassword
 */

/**
 * Get all users for the merchant
 * @returns {Promise<import('axios').AxiosResponse<User[]>>}
 */
export function getUsers() {
  return jwtClient.get('/dashboard/users')
}

/**
 * Invite a new user
 * @param {InviteUserRequest} data
 * @returns {Promise<import('axios').AxiosResponse<InviteUserResponse>>}
 */
export function inviteUser(data) {
  return jwtClient.post('/dashboard/users/invite', data)
}

/**
 * Update a user's role
 * @param {string} id - User ID
 * @param {{ role: 'OWNER'|'ADMIN'|'MEMBER' }} data
 * @returns {Promise<import('axios').AxiosResponse<User>>}
 */
export function updateUserRole(id, data) {
  return jwtClient.patch(`/dashboard/users/${id}/role`, data)
}

/**
 * Delete a user
 * @param {string} id - User ID
 * @returns {Promise<import('axios').AxiosResponse>}
 */
export function deleteUser(id) {
  return jwtClient.delete(`/dashboard/users/${id}`)
}
