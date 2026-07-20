import { useState } from 'react'
import { createPayment, confirmPayment } from '@/api/payments'
import { CheckCircle, XCircle, Loader2 } from 'lucide-react'

export function CheckoutDemoPage() {
  const [apiKey, setApiKey] = useState('')
  const [step, setStep] = useState('setup') // setup | form | processing | success | failed
  const [error, setError] = useState('')
  const [paymentResult, setPaymentResult] = useState(null)
  const [form, setForm] = useState({
    amount: '',
    currency: 'INR',
    customerEmail: '',
    customerName: '',
    orderId: '',
    description: '',
    cardNumber: '',
    expiryMonth: '',
    expiryYear: '',
    cvv: '',
  })

  const handleCreatePayment = async (e) => {
    e.preventDefault()
    setError('')
    setStep('processing')

    try {
      const response = await createPayment({
        amount: Math.round(parseFloat(form.amount) * 100),
        currency: form.currency,
        customerEmail: form.customerEmail || undefined,
        customerName: form.customerName || undefined,
        orderId: form.orderId,
        description: form.description || undefined,
      })

      // Override the Authorization header for this request
      const payment = response.data
      setPaymentResult(payment)
      setStep('form')
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create payment')
      setStep('setup')
    }
  }

  const handleConfirmPayment = async (e) => {
    e.preventDefault()
    setError('')
    setStep('processing')

    try {
      const maskedCard = form.cardNumber.slice(0, 6) + 'XXXXXX' + form.cardNumber.slice(-4)

      const response = await confirmPayment(paymentResult.paymentReference, {
        clientSecret: paymentResult.clientSecret,
        paymentMethodType: 'CARD',
        cardNetwork: form.cardNumber.startsWith('4') ? 'VISA' : 'MASTERCARD',
        issuerBank: 'HDFC',
        maskedCardNumber: maskedCard,
        expiryMonth: parseInt(form.expiryMonth),
        expiryYear: parseInt(form.expiryYear),
      })

      setPaymentResult(response.data)
      if (response.data.status === 'SUCCEEDED') {
        setStep('success')
      } else {
        setStep('failed')
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to confirm payment')
      setStep('form')
    }
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-white border-b px-6 py-4">
        <div className="max-w-lg mx-auto flex items-center justify-between">
          <h1 className="text-lg font-bold">Transiq Checkout Demo</h1>
          <a
            href="/"
            className="text-sm text-blue-600 hover:underline"
          >
            Back to Dashboard
          </a>
        </div>
      </div>

      <div className="max-w-lg mx-auto p-6">
        {step === 'setup' && (
          <div className="bg-white rounded-lg border shadow-sm p-6 space-y-4">
            <h2 className="text-lg font-semibold">1. Configure</h2>
            <p className="text-sm text-muted-foreground">
              Enter a publishable API key to start the demo flow.
            </p>
            <div>
              <label className="block text-sm font-medium mb-1.5">
                Publishable API Key
              </label>
              <input
                type="password"
                value={apiKey}
                onChange={(e) => setApiKey(e.target.value)}
                className="w-full rounded-md border px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="pk_test_..."
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1.5">
                Order ID
              </label>
              <input
                type="text"
                value={form.orderId}
                onChange={(e) =>
                  setForm((f) => ({ ...f, orderId: e.target.value }))
                }
                required
                className="w-full rounded-md border px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="order-12345"
              />
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-sm font-medium mb-1.5">
                  Amount
                </label>
                <input
                  type="number"
                  value={form.amount}
                  onChange={(e) =>
                    setForm((f) => ({ ...f, amount: e.target.value }))
                  }
                  required
                  className="w-full rounded-md border px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="100.00"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1.5">
                  Currency
                </label>
                <select
                  value={form.currency}
                  onChange={(e) =>
                    setForm((f) => ({ ...f, currency: e.target.value }))
                  }
                  className="w-full rounded-md border px-3 py-2 text-sm"
                >
                  <option value="INR">INR</option>
                  <option value="USD">USD</option>
                  <option value="EUR">EUR</option>
                </select>
              </div>
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-sm font-medium mb-1.5">
                  Customer Name
                </label>
                <input
                  type="text"
                  value={form.customerName}
                  onChange={(e) =>
                    setForm((f) => ({ ...f, customerName: e.target.value }))
                  }
                  className="w-full rounded-md border px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="Jane Smith"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1.5">
                  Customer Email
                </label>
                <input
                  type="email"
                  value={form.customerEmail}
                  onChange={(e) =>
                    setForm((f) => ({ ...f, customerEmail: e.target.value }))
                  }
                  className="w-full rounded-md border px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="jane@example.com"
                />
              </div>
            </div>

            {error && (
              <p className="text-sm text-red-600 bg-red-50 p-2 rounded">
                {error}
              </p>
            )}

            <button
              onClick={handleCreatePayment}
              disabled={!apiKey || !form.orderId || !form.amount}
              className="w-full bg-blue-600 text-white rounded-md py-2 text-sm font-medium hover:bg-blue-700 disabled:opacity-50 transition-colors"
            >
              Create Payment
            </button>
          </div>
        )}

        {step === 'form' && (
          <div className="bg-white rounded-lg border shadow-sm p-6 space-y-4">
            <h2 className="text-lg font-semibold">2. Card Details</h2>
            <p className="text-sm text-muted-foreground">
              Payment ref: <code className="bg-muted px-1 rounded">{paymentResult?.paymentReference}</code>
            </p>

            <form onSubmit={handleConfirmPayment} className="space-y-4">
              <div>
                <label className="block text-sm font-medium mb-1.5">
                  Card Number
                </label>
                <input
                  type="text"
                  value={form.cardNumber}
                  onChange={(e) =>
                    setForm((f) => ({
                      ...f,
                      cardNumber: e.target.value.replace(/\D/g, '').slice(0, 16),
                    }))
                  }
                  required
                  maxLength={16}
                  className="w-full rounded-md border px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-blue-500 font-mono"
                  placeholder="4111111111111111"
                />
              </div>
              <div className="grid grid-cols-3 gap-3">
                <div>
                  <label className="block text-sm font-medium mb-1.5">
                    Exp Month
                  </label>
                  <input
                    type="number"
                    value={form.expiryMonth}
                    onChange={(e) =>
                      setForm((f) => ({ ...f, expiryMonth: e.target.value }))
                    }
                    required
                    min={1}
                    max={12}
                    className="w-full rounded-md border px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="12"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1.5">
                    Exp Year
                  </label>
                  <input
                    type="number"
                    value={form.expiryYear}
                    onChange={(e) =>
                      setForm((f) => ({ ...f, expiryYear: e.target.value }))
                    }
                    required
                    min={2024}
                    max={2040}
                    className="w-full rounded-md border px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="2028"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1.5">
                    CVV
                  </label>
                  <input
                    type="text"
                    value={form.cvv}
                    onChange={(e) =>
                      setForm((f) => ({
                        ...f,
                        cvv: e.target.value.replace(/\D/g, '').slice(0, 4),
                      }))
                    }
                    required
                    maxLength={4}
                    className="w-full rounded-md border px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="123"
                  />
                </div>
              </div>

              {error && (
                <p className="text-sm text-red-600 bg-red-50 p-2 rounded">
                  {error}
                </p>
              )}

              <div className="flex gap-2">
                <button
                  type="submit"
                  className="flex-1 bg-blue-600 text-white rounded-md py-2 text-sm font-medium hover:bg-blue-700 transition-colors"
                >
                  Confirm Payment
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setStep('setup')
                    setPaymentResult(null)
                  }}
                  className="border rounded-md px-4 py-2 text-sm hover:bg-gray-50 transition-colors"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        )}

        {step === 'processing' && (
          <div className="bg-white rounded-lg border shadow-sm p-6 text-center space-y-4">
            <Loader2 className="h-12 w-12 animate-spin text-blue-600 mx-auto" />
            <p className="text-lg font-semibold">Processing...</p>
            <p className="text-sm text-muted-foreground">
              Please wait while we process your payment.
            </p>
          </div>
        )}

        {step === 'success' && (
          <div className="bg-white rounded-lg border shadow-sm p-6 text-center space-y-4">
            <CheckCircle className="h-16 w-16 text-green-500 mx-auto" />
            <h2 className="text-xl font-bold text-green-700">
              Payment Successful!
            </h2>
            <div className="text-sm text-muted-foreground space-y-1">
              <p>
                Reference:{' '}
                <code className="bg-muted px-1 rounded">
                  {paymentResult?.paymentReference}
                </code>
              </p>
              <p>
                Amount: ₹{((paymentResult?.amount || 0) / 100).toFixed(2)}{' '}
                {paymentResult?.currency}
              </p>
              <p>Status: {paymentResult?.status}</p>
            </div>
            <button
              onClick={() => {
                setStep('setup')
                setPaymentResult(null)
                setForm({
                  amount: '',
                  currency: 'INR',
                  customerEmail: '',
                  customerName: '',
                  orderId: '',
                  description: '',
                  cardNumber: '',
                  expiryMonth: '',
                  expiryYear: '',
                  cvv: '',
                })
              }}
              className="bg-blue-600 text-white rounded-md px-4 py-2 text-sm font-medium hover:bg-blue-700 transition-colors"
            >
              Start New Payment
            </button>
          </div>
        )}

        {step === 'failed' && (
          <div className="bg-white rounded-lg border shadow-sm p-6 text-center space-y-4">
            <XCircle className="h-16 w-16 text-red-500 mx-auto" />
            <h2 className="text-xl font-bold text-red-700">
              Payment Failed
            </h2>
            <p className="text-sm text-muted-foreground">
              Reference: {paymentResult?.paymentReference}
            </p>
            <button
              onClick={() => {
                setStep('setup')
                setPaymentResult(null)
              }}
              className="bg-blue-600 text-white rounded-md px-4 py-2 text-sm font-medium hover:bg-blue-700 transition-colors"
            >
              Try Again
            </button>
          </div>
        )}
      </div>
    </div>
  )
}
