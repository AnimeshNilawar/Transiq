import jwtClient from './jwtClient'

export function getAdminDashboard() {
  return jwtClient.get('/admin/dashboard')
}

export function getAdminMerchants(params) {
  return jwtClient.get('/admin/merchants', { params })
}

export function getAdminMerchantDetail(id) {
  return jwtClient.get(`/admin/merchants/${id}`)
}

export function getAdminPayments(params) {
  return jwtClient.get('/admin/payments', { params })
}

export function getAdminPaymentDetail(reference) {
  return jwtClient.get(`/admin/payments/${reference}`)
}

export function updateAdminPaymentStatus(reference, status) {
  return jwtClient.patch(`/admin/payments/${reference}/status`, { status })
}

export function getAdminRefunds(params) {
  return jwtClient.get('/admin/refunds', { params })
}

export function getAdminSettlements(params) {
  return jwtClient.get('/admin/settlements', { params })
}

export function createAdminSettlement(merchantId) {
  return jwtClient.post('/admin/settlements', null, { params: { merchantId } })
}

export function getAdminUsers(params) {
  return jwtClient.get('/admin/users', { params })
}

export function updateAdminUserStatus(id, enabled) {
  return jwtClient.patch(`/admin/users/${id}/status`, null, { params: { enabled } })
}

export function getAdminApiKeys(params) {
  return jwtClient.get('/admin/api-keys', { params })
}

export function revokeAdminApiKey(id) {
  return jwtClient.delete(`/admin/api-keys/${id}`)
}

export function getAdminWebhookDeliveries(params) {
  return jwtClient.get('/admin/webhook-deliveries', { params })
}

export function retryAdminWebhookDelivery(id) {
  return jwtClient.post(`/admin/webhook-deliveries/${id}/retry`)
}

export function getAdminRevenueTimeSeries() {
  return jwtClient.get('/admin/analytics/revenue')
}

export function getAdminFailureTrend() {
  return jwtClient.get('/admin/analytics/failure-trends')
}

export function getAdminAlerts() {
  return jwtClient.get('/admin/alerts')
}
