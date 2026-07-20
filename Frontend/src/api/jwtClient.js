import axios from 'axios'
import { toast } from 'sonner'

const jwtClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
})

jwtClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('jwt_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

jwtClient.interceptors.response.use(
  (response) => response,
  (error) => {
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
