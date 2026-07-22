import axios from 'axios'
import { coldStartStore } from '@/hooks/coldStartStore'

const BASE_URL = import.meta.env.VITE_API_BASE_URL

const authClient = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
})

function generateId() {
  return Math.random().toString(36).slice(2, 10)
}

authClient.interceptors.request.use((config) => {
  config.__coldStartId = generateId()
  config.__endColdStart = coldStartStore.startRequest(config.__coldStartId)
  return config
})

authClient.interceptors.response.use(
  (response) => {
    if (response.config.__endColdStart) response.config.__endColdStart()
    return response
  },
  (error) => {
    if (error.config?.__endColdStart) error.config.__endColdStart()
    return Promise.reject(error)
  }
)

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
  return authClient.post('/auth/login', credentials)
}

/**
 * Register a new merchant account
 * @param {RegisterRequest} data
 * @returns {Promise<import('axios').AxiosResponse>}
 */
export function register(data) {
  return authClient.post('/auth/register', data)
}
