import { useState } from 'react'
import { createPayment, confirmPayment } from '@/api/payments'
import { CheckCircle, XCircle, Loader2 } from 'lucide-react'

const PAYMENT_METHODS = [
  { value: 'CARD', label: 'Credit / Debit Card' },
  { value: 'UPI', label: 'UPI' },
]

export default function CheckoutDemoPage() {
  const [apiKey, setApiKey] = useState('')
  const [step, setStep] = useState('setup')
  const [error, setError] = useState('')
  const [paymentResult, setPaymentResult] = useState(null)
  const [paymentMethod, setPaymentMethod] = useState('CARD')
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
    upiId: '',
  })

  const handleCreatePayment = async (e) => {
    e.preventDefault()
    setError('')
    setStep('processing')

    try {
      sessionStorage.setItem('active_api_key', apiKey)

      const response = await createPayment({
        amount: Math.round(parseFloat(form.amount) * 100),
        currency: form.currency,
        customerEmail: form.customerEmail || undefined,
        customerName: form.customerName || undefined,
        orderId: form.orderId,
        description: form.description || undefined,
      })

      const payment = response.data
      setPaymentResult(payment)
      setStep('form')
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create payment')
      setStep('setup')
    }
  }

  const detectCardNetwork = (number) => {
    if (number.startsWith('4')) return 'VISA'
    if (number.startsWith('5') || number.startsWith('2')) return 'MASTERCARD'
    if (number.startsWith('3')) return 'AMEX'
    if (number.startsWith('6')) return 'RUPAY'
    return 'MASTERCARD'
  }

  const handleConfirmPayment = async (e) => {
    e.preventDefault()
    setError('')
    setStep('processing')

    try {
      const payload = {
        clientSecret: paymentResult.clientSecret,
        paymentMethodType: paymentMethod,
      }

      if (paymentMethod === 'CARD') {
        const maskedCard = form.cardNumber.slice(0, 6) + 'XXXXXX' + form.cardNumber.slice(-4)
        payload.cardNetwork = detectCardNetwork(form.cardNumber)
        payload.issuerBank = 'HDFC'
        payload.maskedCardNumber = maskedCard
        payload.expiryMonth = parseInt(form.expiryMonth)
        payload.expiryYear = parseInt(form.expiryYear)
      } else {
        payload.upiId = form.upiId
      }

      const response = await confirmPayment(paymentResult.paymentReference, payload)

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

  const resetAll = () => {
    setStep('setup')
    setPaymentResult(null)
    setPaymentMethod('CARD')
    sessionStorage.removeItem('active_api_key')
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
      upiId: '',
    })
  }

  return (
    <div className="min-h-screen bg-muted">
      <div className="bg-card border-b border-border px-6 py-4">
        <div className="max-w-lg mx-auto flex items-center justify-between">
          <h1 className="text-lg font-bold text-card-foreground">Transiq Checkout Demo</h1>
          <a
            href="/"
            className="text-sm text-accent hover:underline"
          >
            Back to Dashboard
          </a>
        </div>
      </div>

      <div className="max-w-lg mx-auto p-6">
        {step === 'setup' && (
          <div className="bg-card rounded-lg border border-border shadow-sm p-6 space-y-4">
            <h2 className="text-lg font-semibold text-card-foreground">1. Configure</h2>
            <p className="text-sm text-muted-foreground">
              Enter a publishable API key to start the demo flow.
            </p>
            <div>
              <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                Publishable API Key
              </label>
              <input
                type="password"
                value={apiKey}
                onChange={(e) => setApiKey(e.target.value)}
                className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring"
                placeholder="pk_test_..."
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                Order ID
              </label>
              <input
                type="text"
                value={form.orderId}
                onChange={(e) =>
                  setForm((f) => ({ ...f, orderId: e.target.value }))
                }
                required
                className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring"
                placeholder="order-12345"
              />
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                  Amount
                </label>
                <input
                  type="number"
                  value={form.amount}
                  onChange={(e) =>
                    setForm((f) => ({ ...f, amount: e.target.value }))
                  }
                  required
                  className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring"
                  placeholder="100.00"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                  Currency
                </label>
                <select
                  value={form.currency}
                  onChange={(e) =>
                    setForm((f) => ({ ...f, currency: e.target.value }))
                  }
                  className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground"
                >
                  <option value="INR">INR</option>
                  <option value="USD">USD</option>
                  <option value="EUR">EUR</option>
                </select>
              </div>
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                  Customer Name
                </label>
                <input
                  type="text"
                  value={form.customerName}
                  onChange={(e) =>
                    setForm((f) => ({ ...f, customerName: e.target.value }))
                  }
                  className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring"
                  placeholder="Jane Smith"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                  Customer Email
                </label>
                <input
                  type="email"
                  value={form.customerEmail}
                  onChange={(e) =>
                    setForm((f) => ({ ...f, customerEmail: e.target.value }))
                  }
                  className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring"
                  placeholder="jane@example.com"
                />
              </div>
            </div>

            {error && (
              <p className="text-sm text-destructive bg-destructive/10 p-2 rounded">
                {error}
              </p>
            )}

            <button
              onClick={handleCreatePayment}
              disabled={!apiKey || !form.orderId || !form.amount}
              className="w-full bg-accent text-accent-foreground rounded-md py-2 text-sm font-medium hover:bg-accent/90 disabled:opacity-50 transition-colors"
            >
              Create Payment
            </button>
          </div>
        )}

        {step === 'form' && (
          <div className="bg-card rounded-lg border border-border shadow-sm p-6 space-y-4">
            <h2 className="text-lg font-semibold text-card-foreground">2. Payment Details</h2>
            <p className="text-sm text-muted-foreground">
              Payment ref: <code className="bg-muted px-1 rounded text-card-foreground">{paymentResult?.paymentReference}</code>
            </p>

            <div>
              <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                Payment Method
              </label>
              <div className="flex gap-2">
                {PAYMENT_METHODS.map((m) => (
                  <button
                    key={m.value}
                    type="button"
                    onClick={() => setPaymentMethod(m.value)}
                    className={`flex-1 rounded-md py-2 text-sm font-medium border transition-colors ${
                      paymentMethod === m.value
                        ? 'bg-accent text-accent-foreground border-accent'
                        : 'bg-card text-card-foreground border-border hover:bg-muted'
                    }`}
                  >
                    {m.label}
                  </button>
                ))}
              </div>
            </div>

            <form onSubmit={handleConfirmPayment} className="space-y-4">
              {paymentMethod === 'CARD' ? (
                <>
                  <div>
                    <label className="block text-sm font-medium mb-1.5 text-card-foreground">
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
                      className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring font-mono"
                      placeholder="4111111111111111"
                    />
                  </div>
                  <div className="grid grid-cols-3 gap-3">
                    <div>
                      <label className="block text-sm font-medium mb-1.5 text-card-foreground">
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
                        className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring"
                        placeholder="12"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium mb-1.5 text-card-foreground">
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
                        className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring"
                        placeholder="2028"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium mb-1.5 text-card-foreground">
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
                        className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring"
                        placeholder="123"
                      />
                    </div>
                  </div>
                </>
              ) : (
                <div>
                  <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                    UPI ID
                  </label>
                  <input
                    type="text"
                    value={form.upiId}
                    onChange={(e) =>
                      setForm((f) => ({ ...f, upiId: e.target.value }))
                    }
                    required
                    className="w-full rounded-md border border-border bg-card px-3 py-2 text-sm text-card-foreground outline-none focus:ring-2 focus:ring-ring font-mono"
                    placeholder="username@paytm"
                  />
                  <p className="text-xs text-muted-foreground mt-1">
                    Enter your UPI ID (e.g., username@paytm, user@oksbi, name@axl, etc.)
                  </p>
                  <div className="mt-3 bg-muted rounded-md p-3 text-center">
                    <p className="text-xs text-muted-foreground mb-1">Scan with any UPI app</p>
                    <div className="inline-flex items-center justify-center w-40 h-40 bg-white rounded-md">
                      <div className="text-center">
                        <div className="text-3xl mb-1">📱</div>
                        <div className="text-[8px] text-gray-400 leading-tight">
                          SIMULATED<br />QR CODE
                        </div>
                      </div>
                    </div>
                    <p className="text-xs text-muted-foreground mt-2">
                      ₹{parseFloat(form.amount || 0).toFixed(2)} payable
                    </p>
                  </div>
                </div>
              )}

              {error && (
                <p className="text-sm text-destructive bg-destructive/10 p-2 rounded">
                  {error}
                </p>
              )}

              <div className="flex gap-2">
                <button
                  type="submit"
                  className="flex-1 bg-accent text-accent-foreground rounded-md py-2 text-sm font-medium hover:bg-accent/90 transition-colors"
                >
                  Pay ₹{parseFloat(form.amount || 0).toFixed(2)}
                </button>
                <button
                  type="button"
                  onClick={resetAll}
                  className="border border-border rounded-md px-4 py-2 text-sm text-card-foreground hover:bg-muted transition-colors"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        )}

        {step === 'processing' && (
          <div className="bg-card rounded-lg border border-border shadow-sm p-6 text-center space-y-4">
            <Loader2 className="h-12 w-12 animate-spin text-accent mx-auto" />
            <p className="text-lg font-semibold text-card-foreground">Processing...</p>
            <p className="text-sm text-muted-foreground">
              Please wait while we process your payment.
            </p>
          </div>
        )}

        {step === 'success' && (
          <div className="bg-card rounded-lg border border-border shadow-sm p-6 text-center space-y-4">
            <CheckCircle className="h-16 w-16 text-success mx-auto" />
            <h2 className="text-xl font-bold text-success">
              Payment Successful!
            </h2>
            <div className="text-sm text-muted-foreground space-y-1">
              <p>
                Reference:{' '}
                <code className="bg-muted px-1 rounded text-card-foreground">
                  {paymentResult?.paymentReference}
                </code>
              </p>
              <p>
                Amount: ₹{((paymentResult?.amount || 0) / 100).toFixed(2)}{' '}
                {paymentResult?.currency}
              </p>
              <p>Method: {paymentResult?.paymentMethodType}</p>
              {paymentResult?.upiDetails && (
                <p>
                  UPI ID: <code className="bg-muted px-1 rounded text-card-foreground">{paymentResult.upiDetails.upiId}</code>
                </p>
              )}
              {paymentResult?.upiDetails?.upiTransactionReference && (
                <p>
                  UPI Ref: <code className="bg-muted px-1 rounded text-card-foreground">{paymentResult.upiDetails.upiTransactionReference}</code>
                </p>
              )}
              <p>Status: {paymentResult?.status}</p>
            </div>
            <button
              onClick={resetAll}
              className="bg-accent text-accent-foreground rounded-md px-4 py-2 text-sm font-medium hover:bg-accent/90 transition-colors"
            >
              Start New Payment
            </button>
          </div>
        )}

        {step === 'failed' && (
          <div className="bg-card rounded-lg border border-border shadow-sm p-6 text-center space-y-4">
            <XCircle className="h-16 w-16 text-destructive mx-auto" />
            <h2 className="text-xl font-bold text-destructive">
              Payment Failed
            </h2>
            <p className="text-sm text-muted-foreground">
              Reference: {paymentResult?.paymentReference}
            </p>
            <button
              onClick={resetAll}
              className="bg-accent text-accent-foreground rounded-md px-4 py-2 text-sm font-medium hover:bg-accent/90 transition-colors"
            >
              Try Again
            </button>
          </div>
        )}
      </div>
    </div>
  )
}
