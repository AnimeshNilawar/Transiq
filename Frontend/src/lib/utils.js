import { clsx } from 'clsx'
import { twMerge } from 'tailwind-merge'

/**
 * Merges Tailwind CSS class names with proper conflict resolution
 * @param  {...any} inputs - Class names to merge
 * @returns {string} Merged class string
 */
export function cn(...inputs) {
  return twMerge(clsx(inputs))
}

/**
 * Formats amount from smallest currency unit (paise/cents) to display format
 * @param {number} amount - Amount in smallest currency unit
 * @param {string} currency - Currency code (INR, USD, EUR)
 * @returns {string} Formatted amount string
 */
export function formatAmount(amount, currency = 'INR') {
  const divisor = currency === 'INR' ? 100 : 100
  const formatted = (amount / divisor).toFixed(2)
  const symbols = { INR: '₹', USD: '$', EUR: '€' }
  return `${symbols[currency] || currency} ${formatted}`
}

/**
 * Generates a UUID v4 for idempotency keys
 * @returns {string} UUID string
 */
export function generateIdempotencyKey() {
  return crypto.randomUUID()
}

/**
 * Decode JWT payload without verification (for client-side display only)
 * @param {string} token - JWT access token
 * @returns {Object|null} Decoded payload or null if invalid
 */
export function decodeJwtPayload(token) {
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    )
    return JSON.parse(jsonPayload)
  } catch {
    return null
  }
}
