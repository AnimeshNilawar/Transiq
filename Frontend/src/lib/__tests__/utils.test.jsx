import { cn, formatAmount, generateIdempotencyKey } from '../utils'

describe('cn', () => {
  it('merges classes correctly', () => {
    expect(cn('foo', 'bar')).toBe('foo bar')
  })

  it('resolves Tailwind conflicts', () => {
    expect(cn('p-4', 'p-2')).toBe('p-2')
  })

  it('handles conditional classes', () => {
    const showBar = false
    expect(cn('foo', showBar && 'bar', 'baz')).toBe('foo baz')
  })
})

describe('formatAmount', () => {
  it('formats INR currency', () => {
    expect(formatAmount(19900, 'INR')).toBe('₹ 199.00')
  })

  it('formats USD currency', () => {
    expect(formatAmount(2500, 'USD')).toBe('$ 25.00')
  })

  it('formats EUR currency', () => {
    expect(formatAmount(5000, 'EUR')).toBe('€ 50.00')
  })

  it('defaults to INR', () => {
    expect(formatAmount(10000)).toBe('₹ 100.00')
  })
})

describe('generateIdempotencyKey', () => {
  it('returns UUID format', () => {
    const key = generateIdempotencyKey()
    expect(key).toMatch(
      /^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i
    )
  })
})
