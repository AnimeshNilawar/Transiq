import { render, screen } from '@testing-library/react'
import { StatusBadge } from '../StatusBadge'

describe('StatusBadge', () => {
  it('renders status text', () => {
    render(<StatusBadge status="SUCCEEDED" />)
    expect(screen.getByText('SUCCEEDED')).toBeInTheDocument()
  })

  it('applies green classes for SUCCEEDED status', () => {
    render(<StatusBadge status="SUCCEEDED" />)
    const badge = screen.getByText('SUCCEEDED')
    expect(badge.className).toContain('bg-green-100')
    expect(badge.className).toContain('text-green-800')
  })

  it('applies red classes for FAILED status', () => {
    render(<StatusBadge status="FAILED" />)
    const badge = screen.getByText('FAILED')
    expect(badge.className).toContain('bg-red-100')
    expect(badge.className).toContain('text-red-800')
  })

  it('applies yellow classes for PENDING status', () => {
    render(<StatusBadge status="PENDING" />)
    const badge = screen.getByText('PENDING')
    expect(badge.className).toContain('bg-yellow-100')
    expect(badge.className).toContain('text-yellow-800')
  })
})
