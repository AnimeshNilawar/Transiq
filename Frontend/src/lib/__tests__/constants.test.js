import { getStatusColor } from '../constants'

describe('getStatusColor', () => {
  it('returns green classes for SUCCEEDED', () => {
    expect(getStatusColor('SUCCEEDED')).toContain('bg-green-100')
  })

  it('returns red classes for FAILED', () => {
    expect(getStatusColor('FAILED')).toContain('bg-red-100')
  })

  it('returns yellow classes for PENDING', () => {
    expect(getStatusColor('PENDING')).toContain('bg-yellow-100')
  })

  it('returns gray fallback for unknown status', () => {
    expect(getStatusColor('UNKNOWN')).toContain('bg-gray-100')
  })
})
