import { useState, useEffect, useRef, useCallback } from 'react'

const BANKS = ['HDFC', 'ICICI', 'SBI', 'AXIS', 'KOTAK']
const NETWORKS = ['VISA', 'MASTERCARD', 'RUPAY']
const BINS = { VISA: '411111', MASTERCARD: '531234', RUPAY: '652150' }
const FAILURE_REASONS = ['insufficient_funds', 'bank_declined', 'network_error']

function generateRow() {
  const approved = Math.random() < 0.9
  const network = NETWORKS[Math.floor(Math.random() * NETWORKS.length)]
  const bank = BANKS[Math.floor(Math.random() * BANKS.length)]
  const bin = BINS[network]
  const now = new Date()
  const timestamp = now.toTimeString().slice(0, 8) + '.' +
    String(now.getMilliseconds()).padStart(3, '0')
  const authRef = 'auth_' + Math.random().toString(16).slice(2, 10)

  return {
    id: Math.random().toString(36).slice(2, 10),
    timestamp,
    bin,
    bank,
    network,
    status: approved ? 'APPROVED' : 'DECLINED',
    reason: approved ? null : FAILURE_REASONS[Math.floor(Math.random() * FAILURE_REASONS.length)],
    authRef,
  }
}

function getDelay() {
  return 800 + Math.random() * 700
}

export default function AuthorizationTicker() {
  const [rows, setRows] = useState(() => [generateRow(), generateRow(), generateRow()])
  const timerRef = useRef(null)
  const scrollRef = useRef(null)
  const prefersReduced = useRef(false)

  const addRow = useCallback(() => {
    setRows((prev) => {
      const next = [...prev, generateRow()]
      return next.length > 30 ? next.slice(next.length - 30) : next
    })
    scheduleNext()
  }, [])

  const scheduleNext = useCallback(() => {
    timerRef.current = setTimeout(addRow, prefersReduced.current ? getDelay() * 4 : getDelay())
  }, [addRow])

  useEffect(() => {
    prefersReduced.current = window.matchMedia('(prefers-reduced-motion: reduce)').matches
    const mediaQuery = window.matchMedia('(prefers-reduced-motion: reduce)')
    const handler = (e) => { prefersReduced.current = e.matches }
    mediaQuery.addEventListener('change', handler)

    scheduleNext()
    return () => {
      clearTimeout(timerRef.current)
      mediaQuery.removeEventListener('change', handler)
    }
  }, [scheduleNext])

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight
    }
  }, [rows])

  return (
    <div className="ticker-container">
      <div className="flex items-center gap-2 mb-2">
        <span className="live-dot inline-block w-2 h-2 rounded-full bg-[var(--mkt-approved)]" />
        <span className="text-[11px] font-semibold uppercase tracking-widest text-[var(--mkt-ink-muted)]">
          Live Authorization Feed
        </span>
      </div>
      <div
        ref={scrollRef}
        className="bg-[var(--mkt-ink)] rounded-xl p-4 font-mono text-xs leading-relaxed overflow-y-auto max-h-72 border border-white/5"
        style={{ fontFamily: "'IBM Plex Mono', 'JetBrains Mono', monospace" }}
        aria-live="polite"
        aria-label="Live payment authorization feed"
      >
        <div className="flex text-[10px] text-gray-500 uppercase tracking-wider pb-1.5 border-b border-white/10 mb-1.5">
          <span className="w-24 shrink-0">Timestamp</span>
          <span className="w-16 shrink-0">BIN</span>
          <span className="w-28 shrink-0">Route</span>
          <span className="w-20 shrink-0">Status</span>
          <span className="w-32 shrink-0">Reference</span>
        </div>
        {rows.map((row, i) => (
          <div key={row.id} className="ticker-row flex py-1">
            <span className="w-24 shrink-0 text-gray-400">{row.timestamp}</span>
            <span className="w-16 shrink-0 text-gray-300">BIN {row.bin}</span>
            <span className="w-28 shrink-0 text-gray-300">
              {row.bank} &rarr; {row.network}
            </span>
            <span
              className={`w-20 shrink-0 font-semibold ${
                row.status === 'APPROVED' ? 'text-[var(--mkt-approved)]' : 'text-[var(--mkt-declined)]'
              }`}
            >
              {row.status}
            </span>
            <span className="text-gray-400">{row.reason || row.authRef}</span>
          </div>
        ))}
      </div>
    </div>
  )
}
