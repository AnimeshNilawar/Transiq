import { render, screen } from '@testing-library/react'
import { Item } from '../Item'

describe('Item', () => {
  it('renders label and value', () => {
    render(<Item label="Email" value="test@example.com" />)
    expect(screen.getByText('Email')).toBeInTheDocument()
    expect(screen.getByText('test@example.com')).toBeInTheDocument()
  })

  it('applies mono class when mono prop is true', () => {
    render(<Item label="ID" value="abc-123" mono />)
    const value = screen.getByText('abc-123')
    expect(value.className).toContain('font-mono')
    expect(value.className).toContain('tabular-nums')
  })

  it('does not apply mono class by default', () => {
    render(<Item label="Name" value="John" />)
    const value = screen.getByText('John')
    expect(value.className).not.toContain('font-mono')
  })

  it('shows dash for null value', () => {
    render(<Item label="Empty" value={null} />)
    expect(screen.getByText('-')).toBeInTheDocument()
  })

  it('shows dash for undefined value', () => {
    render(<Item label="Empty" />)
    expect(screen.getByText('-')).toBeInTheDocument()
  })
})
