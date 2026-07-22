import axios from 'axios'
import { toast } from 'sonner'
import { coldStartStore } from '@/hooks/coldStartStore'

const jwtClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
})

function generateId() {
  return Math.random().toString(36).slice(2, 10)
}

jwtClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('jwt_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  config.__coldStartId = generateId()
  config.__endColdStart = coldStartStore.startRequest(config.__coldStartId)
  return config
})

jwtClient.interceptors.response.use(
  (response) => {
    if (response.config.__endColdStart) response.config.__endColdStart()
    return response
  },
  (error) => {
    if (error.config?.__endColdStart) error.config.__endColdStart()

    const message =
      error.response?.data?.message ||
      error.response?.data?.error ||
      error.message ||
      'An unexpected error occurred'

    if (error.response?.status === 401) {
      localStorage.removeItem('jwt_token')
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }

    toast.error(message)
    return Promise.reject(error)
  }
)

export default jwtClient
