import { render, screen } from '@testing-library/react'
import { EmptyState } from '../EmptyState'

describe('EmptyState', () => {
  it('renders default title and description', () => {
    render(<EmptyState />)
    expect(screen.getByText('No data found')).toBeInTheDocument()
    expect(screen.getByText('There are no items to display.')).toBeInTheDocument()
  })

  it('renders custom title and description', () => {
    render(
      <EmptyState title="Custom Title" description="Custom description text" />
    )
    expect(screen.getByText('Custom Title')).toBeInTheDocument()
    expect(screen.getByText('Custom description text')).toBeInTheDocument()
  })

  it('renders action button when provided', () => {
    render(<EmptyState action={<button>Action</button>} />)
    expect(screen.getByRole('button', { name: 'Action' })).toBeInTheDocument()
  })
})
