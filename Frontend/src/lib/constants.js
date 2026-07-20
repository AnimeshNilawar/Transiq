export const PAYMENT_STATUS = {
  CREATED: 'CREATED',
  REQUIRES_PAYMENT_METHOD: 'REQUIRES_PAYMENT_METHOD',
  PROCESSING: 'PROCESSING',
  SUCCEEDED: 'SUCCEEDED',
  FAILED: 'FAILED',
  CANCELLED: 'CANCELLED',
  REFUNDED: 'REFUNDED',
  EXPIRED: 'EXPIRED',
}

export const REFUND_STATUS = {
  CREATED: 'CREATED',
  PROCESSING: 'PROCESSING',
  SUCCEEDED: 'SUCCEEDED',
  FAILED: 'FAILED',
}

export const REFUND_REASON = {
  REQUESTED_BY_CUSTOMER: 'REQUESTED_BY_CUSTOMER',
  DUPLICATE_PAYMENT: 'DUPLICATE_PAYMENT',
  FRAUDULENT: 'FRAUDULENT',
  PRODUCT_UNAVAILABLE: 'PRODUCT_UNAVAILABLE',
  OTHER: 'OTHER',
}

export const SETTLEMENT_STATUS = {
  PENDING: 'PENDING',
  PROCESSING: 'PROCESSING',
  COMPLETED: 'COMPLETED',
  FAILED: 'FAILED',
}

export const API_KEY_STATUS = {
  ACTIVE: 'ACTIVE',
  REVOKED: 'REVOKED',
}

export const API_KEY_ENVIRONMENT = {
  TEST: 'TEST',
  LIVE: 'LIVE',
}

export const API_KEY_TYPE = {
  SECRET: 'SECRET',
  PUBLISHABLE: 'PUBLISHABLE',
  RESTRICTED: 'RESTRICTED',
}

export const WEBHOOK_STATUS = {
  ACTIVE: 'ACTIVE',
  DISABLED: 'DISABLED',
}

export const WEBHOOK_DELIVERY_STATUS = {
  PENDING: 'PENDING',
  DELIVERED: 'DELIVERED',
  FAILED: 'FAILED',
}

export const WEBHOOK_EVENT_TYPE = {
  PAYMENT_SUCCEEDED: 'PAYMENT_SUCCEEDED',
  PAYMENT_FAILED: 'PAYMENT_FAILED',
  REFUND_SUCCEEDED: 'REFUND_SUCCEEDED',
  SETTLEMENT_COMPLETED: 'SETTLEMENT_COMPLETED',
}

export const MERCHANT_STATUS = {
  ACTIVE: 'ACTIVE',
  INACTIVE: 'INACTIVE',
  SUSPENDED: 'SUSPENDED',
}

export const PAYMENT_METHOD_TYPE = {
  CARD: 'CARD',
  UPI: 'UPI',
  NET_BANKING: 'NET_BANKING',
  WALLET: 'WALLET',
  UNKNOWN: 'UNKNOWN',
}

export const CARD_NETWORK = {
  VISA: 'VISA',
  MASTERCARD: 'MASTERCARD',
  RUPAY: 'RUPAY',
}

export const ISSUER_BANK = {
  HDFC: 'HDFC',
  ICICI: 'ICICI',
  SBI: 'SBI',
  AXIS: 'AXIS',
}

export const CURRENCIES = ['INR', 'USD', 'EUR']

/**
 * Get color class for a given status
 * @param {string} status - Status value
 * @returns {string} Tailwind class string for badge styling
 */
export function getStatusColor(status) {
  const colorMap = {
    // Green
    SUCCEEDED: 'bg-green-100 text-green-800 border-green-200',
    DELIVERED: 'bg-green-100 text-green-800 border-green-200',
    COMPLETED: 'bg-green-100 text-green-800 border-green-200',
    ACTIVE: 'bg-green-100 text-green-800 border-green-200',
    // Red
    FAILED: 'bg-red-100 text-red-800 border-red-200',
    DECLINED: 'bg-red-100 text-red-800 border-red-200',
    REVOKED: 'bg-red-100 text-red-800 border-red-200',
    DISABLED: 'bg-red-100 text-red-800 border-red-200',
    // Yellow
    PENDING: 'bg-yellow-100 text-yellow-800 border-yellow-200',
    PROCESSING: 'bg-yellow-100 text-yellow-800 border-yellow-200',
    CREATED: 'bg-yellow-100 text-yellow-800 border-yellow-200',
    REQUIRES_PAYMENT_METHOD: 'bg-yellow-100 text-yellow-800 border-yellow-200',
    // Gray
    CANCELLED: 'bg-gray-100 text-gray-800 border-gray-200',
    EXPIRED: 'bg-gray-100 text-gray-800 border-gray-200',
  }
  return colorMap[status] || 'bg-gray-100 text-gray-800 border-gray-200'
}
