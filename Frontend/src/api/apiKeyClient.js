import axios from 'axios'
import { toast } from 'sonner'
import { coldStartStore } from '@/hooks/coldStartStore'

const apiKeyClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
})

function generateId() {
  return Math.random().toString(36).slice(2, 10)
}

apiKeyClient.interceptors.request.use((config) => {
  const apiKey = sessionStorage.getItem('active_api_key')
  if (apiKey) {
    config.headers.Authorization = apiKey
  }
  config.__coldStartId = generateId()
  config.__endColdStart = coldStartStore.startRequest(config.__coldStartId)
  return config
})

apiKeyClient.interceptors.response.use(
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

    toast.error(message)
    return Promise.reject(error)
  }
)

export default apiKeyClient
