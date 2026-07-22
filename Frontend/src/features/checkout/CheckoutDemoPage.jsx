import { useState } from 'react'
import { Link } from 'react-router-dom'
import { createPayment, confirmPayment } from '@/api/payments'
import { CheckCircle, XCircle, Loader2, CreditCard, Globe, Zap, Shield, Lock } from 'lucide-react'
import ColdStartMessage from '@/components/shared/ColdStartMessage'

const PAYMENT_METHODS = [
  { value: 'CARD', label: 'Credit / Debit Card' },
  { value: 'UPI', label: 'UPI' },
]

const PRODUCTS = [
  { name: 'Pro Plan — Monthly', desc: 'Full access to analytics, API, and support', amount: 1999 },
  { name: 'Standard Plan — Monthly', desc: 'Core features for individuals', amount: 999 },
  { name: 'Enterprise — Monthly', desc: 'Custom integrations and dedicated support', amount: 4999 },
]

function detectCardNetwork(number) {
  if (number.startsWith('4')) return 'VISA'
  if (number.startsWith('5') || number.startsWith('2')) return 'MASTERCARD'
  if (number.startsWith('3')) return 'AMEX'
  if (number.startsWith('6')) return 'RUPAY'
  return null
}

export default function CheckoutDemoPage() {
  const [apiKey, setApiKey] = useState('')
  const [step, setStep] = useState('setup')
  const [error, setError] = useState('')
  const [paymentResult, setPaymentResult] = useState(null)
  const [paymentMethod, setPaymentMethod] = useState('CARD')
  const [selectedProduct, setSelectedProduct] = useState(PRODUCTS[0])
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
  const [cardNetwork, setCardNetwork] = useState(null)

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
      setPaymentResult(response.data)
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
      const payload = {
        clientSecret: paymentResult.clientSecret,
        paymentMethodType: paymentMethod,
      }

      if (paymentMethod === 'CARD') {
        const masked = form.cardNumber.slice(0, 6) + 'XXXXXX' + form.cardNumber.slice(-4)
        payload.cardNetwork = detectCardNetwork(form.cardNumber) || 'MASTERCARD'
        payload.issuerBank = 'HDFC'
        payload.maskedCardNumber = masked
        payload.expiryMonth = parseInt(form.expiryMonth)
        payload.expiryYear = parseInt(form.expiryYear)
      } else {
        payload.upiId = form.upiId
      }

      const response = await confirmPayment(paymentResult.paymentReference, payload)
      setPaymentResult(response.data)
      setStep(response.data.status === 'SUCCEEDED' ? 'success' : 'failed')
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to confirm payment')
      setStep('form')
    }
  }

  const handleCardNumberChange = (val) => {
    const digits = val.replace(/\D/g, '').slice(0, 16)
    setForm((f) => ({ ...f, cardNumber: digits }))
    setCardNetwork(digits.length >= 2 ? detectCardNetwork(digits) : null)
  }

  const startNew = () => {
    setStep('setup')
    setPaymentResult(null)
    setPaymentMethod('CARD')
    setCardNetwork(null)
    sessionStorage.removeItem('active_api_key')
    setForm({
      amount: '', currency: 'INR', customerEmail: '', customerName: '',
      orderId: '', description: '', cardNumber: '', expiryMonth: '',
      expiryYear: '', cvv: '', upiId: '',
    })
  }

  const useProduct = (product) => {
    setSelectedProduct(product)
    setForm((f) => ({
      ...f,
      amount: (product.amount / 100).toFixed(2),
      description: product.name,
    }))
  }

  const amountInr = parseFloat(form.amount || 0).toFixed(2)

  const leftPanel = (
    <div className="flex h-full flex-col justify-between p-8 md:p-12" style={{ background: 'var(--mkt-ink, #1E1B4B)' }}>
      <div>
        <div className="flex items-center gap-2 mb-10">
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-white/10 text-sm font-bold text-white">
            T
          </div>
          <span className="text-lg font-bold tracking-tight text-white">Transiq</span>
        </div>

        <h2 className="text-2xl font-bold tracking-tight text-white md:text-3xl">
          {step === 'success' ? 'Payment complete' : 'Complete your purchase'}
        </h2>
        <p className="mt-2 text-sm leading-relaxed text-white/60">
          {step === 'success'
            ? 'Your transaction has been processed successfully.'
            : 'This is a simulated checkout flow using the Transiq payment API.'}
        </p>

        <div className="mt-8 space-y-3">
          {PRODUCTS.map((p) => (
            <button
              key={p.name}
              type="button"
              onClick={() => useProduct(p)}
              disabled={step !== 'setup'}
              className={`w-full rounded-xl border p-4 text-left transition-all ${
                selectedProduct.name === p.name && step === 'setup'
                  ? 'border-white/40 bg-white/10 text-white'
                  : 'border-white/10 text-white/70 hover:border-white/25 hover:bg-white/5'
              } ${step !== 'setup' ? 'opacity-50 cursor-default' : 'cursor-pointer'}`}
            >
              <div className="flex items-center justify-between">
                <span className="font-semibold text-sm">{p.name}</span>
                <span className="font-mono text-sm font-bold text-white/90">
                  ₹{(p.amount / 100).toFixed(2)}
                </span>
              </div>
              <p className="mt-0.5 text-xs text-white/50">{p.desc}</p>
            </button>
          ))}
        </div>

        {step !== 'setup' && paymentResult && (
          <div className="mt-6 rounded-xl border border-white/10 bg-white/5 p-4">
            <p className="text-xs font-semibold uppercase tracking-widest text-white/40">Order</p>
            <div className="mt-2 flex items-center justify-between text-sm">
              <span className="text-white/80">{paymentResult.description || 'Payment'}</span>
              <span className="font-mono text-white font-bold">₹{((paymentResult.amount || 0) / 100).toFixed(2)}</span>
            </div>
            <div className="mt-1.5 border-t border-white/10 pt-1.5 flex items-center justify-between text-sm">
              <span className="text-white/60">Total</span>
              <span className="font-mono text-white font-bold">₹{((paymentResult.amount || 0) / 100).toFixed(2)}</span>
            </div>
          </div>
        )}
      </div>

      <div className="mt-8 space-y-3">
        <div className="flex items-center gap-2 text-xs text-white/40">
          <Lock className="h-3 w-3" />
          <span>Simulated — no real payment is processed</span>
        </div>
        <div className="flex items-center gap-2 text-xs text-white/40">
          <Shield className="h-3 w-3" />
          <span>Demo data only — do not use real card details</span>
        </div>
      </div>
    </div>
  )

  return (
    <div className="flex min-h-screen flex-col md:flex-row">
      <div className="md:w-[420px] lg:w-[480px] shrink-0">
        {leftPanel}
      </div>

      <div className="flex flex-1 flex-col bg-background">
        <header className="flex items-center justify-between border-b border-border px-6 py-4">
          <h1 className="text-sm font-semibold text-foreground">Checkout Demo</h1>
          <Link
            to="/"
            className="text-sm text-muted-foreground hover:text-foreground transition-colors"
          >
            &larr; Back
          </Link>
        </header>

        <div className="flex-1 overflow-y-auto p-6 md:p-10">
          <div className="mx-auto max-w-xl">
            {step === 'setup' && (
              <form onSubmit={handleCreatePayment} className="space-y-6">
                <div>
                  <h2 className="text-lg font-semibold text-foreground">Configure</h2>
                  <p className="text-sm text-muted-foreground mt-1">
                    Enter an API key and order details to create a payment intent.
                  </p>
                </div>

                <div className="rounded-xl border border-border bg-card p-5 space-y-4">
                  <div>
                    <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                      API Key
                    </label>
                    <input
                      type="password"
                      value={apiKey}
                      onChange={(e) => setApiKey(e.target.value)}
                      className="w-full rounded-lg border border-border bg-background px-3 py-2.5 text-sm text-foreground outline-none focus:ring-2 focus:ring-ring font-mono"
                      placeholder="sk_test_..."
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                      Order ID
                    </label>
                    <input
                      type="text"
                      value={form.orderId}
                      onChange={(e) => setForm((f) => ({ ...f, orderId: e.target.value }))}
                      required
                      className="w-full rounded-lg border border-border bg-background px-3 py-2.5 text-sm text-foreground outline-none focus:ring-2 focus:ring-ring"
                      placeholder="order-8842"
                    />
                  </div>
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                        Amount (₹)
                      </label>
                      <input
                        type="number"
                        value={form.amount}
                        onChange={(e) => setForm((f) => ({ ...f, amount: e.target.value }))}
                        required
                        className="w-full rounded-lg border border-border bg-background px-3 py-2.5 text-sm text-foreground outline-none focus:ring-2 focus:ring-ring"
                        placeholder="100.00"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                        Currency
                      </label>
                      <select
                        value={form.currency}
                        onChange={(e) => setForm((f) => ({ ...f, currency: e.target.value }))}
                        className="w-full rounded-lg border border-border bg-background px-3 py-2.5 text-sm text-foreground"
                      >
                        <option value="INR">INR</option>
                        <option value="USD">USD</option>
                        <option value="EUR">EUR</option>
                      </select>
                    </div>
                  </div>
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                        Customer Name
                      </label>
                      <input
                        type="text"
                        value={form.customerName}
                        onChange={(e) => setForm((f) => ({ ...f, customerName: e.target.value }))}
                        className="w-full rounded-lg border border-border bg-background px-3 py-2.5 text-sm text-foreground outline-none focus:ring-2 focus:ring-ring"
                        placeholder="Jane Smith"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                        Email
                      </label>
                      <input
                        type="email"
                        value={form.customerEmail}
                        onChange={(e) => setForm((f) => ({ ...f, customerEmail: e.target.value }))}
                        className="w-full rounded-lg border border-border bg-background px-3 py-2.5 text-sm text-foreground outline-none focus:ring-2 focus:ring-ring"
                        placeholder="jane@example.com"
                      />
                    </div>
                  </div>
                </div>

                {error && (
                  <p className="text-sm text-destructive bg-destructive/10 rounded-lg px-4 py-2.5">
                    {error}
                  </p>
                )}

                <button
                  type="submit"
                  disabled={!apiKey || !form.orderId || !form.amount}
                  className="w-full rounded-xl bg-accent px-6 py-3 text-sm font-semibold text-accent-foreground hover:bg-accent/90 disabled:opacity-50 transition-colors"
                >
                  Create Payment Intent
                </button>
              </form>
            )}

            {step === 'form' && (
              <form onSubmit={handleConfirmPayment} className="space-y-6">
                <div>
                  <h2 className="text-lg font-semibold text-foreground">Payment details</h2>
                  <p className="text-sm text-muted-foreground mt-1 flex items-center gap-1.5">
                    Ref:{' '}
                    <code className="bg-muted px-1.5 py-0.5 rounded text-xs text-card-foreground">
                      {paymentResult?.paymentReference}
                    </code>
                  </p>
                </div>

                <div className="rounded-xl border border-border bg-card p-5 space-y-5">
                  <div>
                    <label className="block text-sm font-medium mb-2 text-card-foreground">
                      Payment method
                    </label>
                    <div className="grid grid-cols-2 gap-2">
                      {PAYMENT_METHODS.map((m) => (
                        <button
                          key={m.value}
                          type="button"
                          onClick={() => setPaymentMethod(m.value)}
                          className={`flex items-center justify-center gap-2 rounded-lg py-2.5 text-sm font-medium border transition-colors ${
                            paymentMethod === m.value
                              ? 'bg-accent text-accent-foreground border-accent'
                              : 'bg-background text-card-foreground border-border hover:bg-muted'
                          }`}
                        >
                          {m.value === 'CARD' ? <CreditCard className="h-4 w-4" /> : <Globe className="h-4 w-4" />}
                          {m.label}
                        </button>
                      ))}
                    </div>
                  </div>

                  {paymentMethod === 'CARD' ? (
                    <div className="space-y-4">
                      <div>
                        <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                          Card number
                        </label>
                        <div className="relative">
                          <input
                            type="text"
                            value={form.cardNumber}
                            onChange={(e) => handleCardNumberChange(e.target.value)}
                            required
                            maxLength={19}
                            className="w-full rounded-lg border border-border bg-background px-3 py-2.5 text-sm text-foreground outline-none focus:ring-2 focus:ring-ring font-mono tracking-wider"
                            placeholder="4111 1111 1111 1111"
                          />
                          {cardNetwork && (
                            <span className="absolute right-3 top-1/2 -translate-y-1/2 text-xs font-semibold text-muted-foreground">
                              {cardNetwork}
                            </span>
                          )}
                        </div>
                      </div>
                      <div className="grid grid-cols-3 gap-3">
                        <div>
                          <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                            Month
                          </label>
                          <input
                            type="number"
                            value={form.expiryMonth}
                            onChange={(e) => setForm((f) => ({ ...f, expiryMonth: e.target.value }))}
                            required
                            min={1}
                            max={12}
                            className="w-full rounded-lg border border-border bg-background px-3 py-2.5 text-sm text-foreground outline-none focus:ring-2 focus:ring-ring"
                            placeholder="12"
                          />
                        </div>
                        <div>
                          <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                            Year
                          </label>
                          <input
                            type="number"
                            value={form.expiryYear}
                            onChange={(e) => setForm((f) => ({ ...f, expiryYear: e.target.value }))}
                            required
                            min={2024}
                            max={2040}
                            className="w-full rounded-lg border border-border bg-background px-3 py-2.5 text-sm text-foreground outline-none focus:ring-2 focus:ring-ring"
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
                            onChange={(e) => setForm((f) => ({ ...f, cvv: e.target.value.replace(/\D/g, '').slice(0, 4) }))}
                            required
                            maxLength={4}
                            className="w-full rounded-lg border border-border bg-background px-3 py-2.5 text-sm text-foreground outline-none focus:ring-2 focus:ring-ring font-mono"
                            placeholder="123"
                          />
                        </div>
                      </div>
                    </div>
                  ) : (
                    <div>
                      <label className="block text-sm font-medium mb-1.5 text-card-foreground">
                        UPI ID
                      </label>
                      <input
                        type="text"
                        value={form.upiId}
                        onChange={(e) => setForm((f) => ({ ...f, upiId: e.target.value }))}
                        required
                        className="w-full rounded-lg border border-border bg-background px-3 py-2.5 text-sm text-foreground outline-none focus:ring-2 focus:ring-ring font-mono"
                        placeholder="username@paytm"
                      />
                      <p className="text-xs text-muted-foreground mt-1.5">
                        Accepted handles: @paytm, @oksbi, @axl, @ybl, @ibl, @payu
                      </p>
                      <div className="mt-4 rounded-lg border border-dashed border-border bg-background p-5 text-center">
                        <Globe className="h-8 w-8 text-muted-foreground mx-auto" />
                        <p className="text-xs text-muted-foreground mt-2">
                          UPI QR scanning is simulated in this demo.
                        </p>
                      </div>
                    </div>
                  )}
                </div>

                {error && (
                  <p className="text-sm text-destructive bg-destructive/10 rounded-lg px-4 py-2.5">
                    {error}
                  </p>
                )}

                <div className="flex gap-3">
                  <button
                    type="submit"
                    className="flex-1 rounded-xl bg-accent px-6 py-3 text-sm font-semibold text-accent-foreground hover:bg-accent/90 transition-colors"
                  >
                    Pay ₹{amountInr}
                  </button>
                  <button
                    type="button"
                    onClick={startNew}
                    className="rounded-xl border border-border px-6 py-3 text-sm font-medium text-card-foreground hover:bg-muted transition-colors"
                  >
                    Cancel
                  </button>
                </div>
              </form>
            )}

            {step === 'processing' && (
              <div className="text-center space-y-6 py-12">
                <Loader2 className="h-12 w-12 animate-spin text-accent mx-auto" />
                <div>
                  <p className="text-lg font-semibold text-foreground">Processing your payment</p>
                  <p className="text-sm text-muted-foreground mt-1">
                    Please wait while we process your transaction.
                  </p>
                </div>
                <ColdStartMessage />
              </div>
            )}

            {step === 'success' && (
              <div className="text-center space-y-6 py-8">
                <div className="inline-flex h-16 w-16 items-center justify-center rounded-full bg-success/10">
                  <CheckCircle className="h-10 w-10 text-success" />
                </div>
                <div>
                  <h2 className="text-xl font-bold text-foreground">Payment succeeded</h2>
                  <p className="text-sm text-muted-foreground mt-1">
                    Your transaction has been completed.
                  </p>
                </div>

                <div className="rounded-xl border border-border bg-card p-5 text-left space-y-2">
                  {[
                    ['Reference', paymentResult?.paymentReference],
                    ['Amount', `₹${((paymentResult?.amount || 0) / 100).toFixed(2)} ${paymentResult?.currency}`],
                    ['Method', paymentResult?.paymentMethodType],
                    ['Status', paymentResult?.status],
                  ].map(([label, val]) => (
                    <div key={label} className="flex items-center justify-between text-sm">
                      <span className="text-muted-foreground">{label}</span>
                      <span className="font-mono text-xs text-card-foreground font-medium">{val}</span>
                    </div>
                  ))}
                </div>

                <button
                  onClick={startNew}
                  className="rounded-xl bg-accent px-6 py-3 text-sm font-semibold text-accent-foreground hover:bg-accent/90 transition-colors"
                >
                  Start new payment
                </button>
              </div>
            )}

            {step === 'failed' && (
              <div className="text-center space-y-6 py-8">
                <div className="inline-flex h-16 w-16 items-center justify-center rounded-full bg-destructive/10">
                  <XCircle className="h-10 w-10 text-destructive" />
                </div>
                <div>
                  <h2 className="text-xl font-bold text-foreground">Payment failed</h2>
                  <p className="text-sm text-muted-foreground mt-1">
                    The transaction could not be completed.
                  </p>
                </div>

                <div className="rounded-xl border border-border bg-card p-5 text-left space-y-2">
                  {[
                    ['Reference', paymentResult?.paymentReference],
                    ['Amount', `₹${((paymentResult?.amount || 0) / 100).toFixed(2)} ${paymentResult?.currency}`],
                    ['Status', paymentResult?.status],
                  ].map(([label, val]) => (
                    <div key={label} className="flex items-center justify-between text-sm">
                      <span className="text-muted-foreground">{label}</span>
                      <span className="font-mono text-xs text-card-foreground font-medium">{val}</span>
                    </div>
                  ))}
                </div>

                <button
                  onClick={startNew}
                  className="rounded-xl bg-accent px-6 py-3 text-sm font-semibold text-accent-foreground hover:bg-accent/90 transition-colors"
                >
                  Try again
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}
