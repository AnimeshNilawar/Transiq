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

