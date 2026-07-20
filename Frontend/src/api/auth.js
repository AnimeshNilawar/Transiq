import axios from 'axios'

const BASE_URL = import.meta.env.VITE_API_BASE_URL

/**
 * @typedef {Object} LoginRequest
 * @property {string} email
 * @property {string} password
 */

/**
 * @typedef {Object} LoginResponse
 * @property {string} accessToken
 * @property {string} tokenType
 */

/**
 * @typedef {Object} RegisterRequest
 * @property {string} businessName
 * @property {string} businessEmail
 * @property {string} [firstName]
 * @property {string} [lastName]
 * @property {string} [email]
 * @property {string} [password]
 */

/**
 * Login with email and password
 * @param {LoginRequest} credentials
 * @returns {Promise<import('axios').AxiosResponse<LoginResponse>>}
 */
export function login(credentials) {
  return axios.post(`${BASE_URL}/auth/login`, credentials)
}

/**
 * Register a new merchant account
 * @param {RegisterRequest} data
 * @returns {Promise<import('axios').AxiosResponse>}
 */
export function register(data) {
  return axios.post(`${BASE_URL}/auth/register`, data)
}
