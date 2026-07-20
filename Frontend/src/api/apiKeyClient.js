import axios from 'axios'
import { toast } from 'sonner'

const apiKeyClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
})

apiKeyClient.interceptors.request.use((config) => {
  const apiKey = sessionStorage.getItem('active_api_key')
  if (apiKey) {
    config.headers.Authorization = apiKey
  }
  return config
})

apiKeyClient.interceptors.response.use(
  (response) => response,
  (error) => {
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
