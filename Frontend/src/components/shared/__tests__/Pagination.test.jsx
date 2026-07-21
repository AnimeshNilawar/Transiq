import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { Pagination } from '../Pagination'

describe('Pagination', () => {
  it('renders page info', () => {
    render(<Pagination page={0} totalPages={5} onPageChange={() => {}} />)
    expect(screen.getByText('Page 1 of 5')).toBeInTheDocument()
  })

  it('returns null when totalPages <= 1', () => {
    const { container } = render(
      <Pagination page={0} totalPages={1} onPageChange={() => {}} />
    )
    expect(container.innerHTML).toBe('')
  })

  it('disables Previous on first page', () => {
    render(<Pagination page={0} totalPages={5} onPageChange={() => {}} />)
    expect(screen.getByText('Previous')).toBeDisabled()
  })

  it('enables Previous when not on first page', () => {
    render(<Pagination page={2} totalPages={5} onPageChange={() => {}} />)
    expect(screen.getByText('Previous')).not.toBeDisabled()
  })

  it('disables Next on last page', () => {
    render(<Pagination page={4} totalPages={5} onPageChange={() => {}} />)
    expect(screen.getByText('Next')).toBeDisabled()
  })

  it('enables Next when not on last page', () => {
    render(<Pagination page={0} totalPages={5} onPageChange={() => {}} />)
    expect(screen.getByText('Next')).not.toBeDisabled()
  })

  it('calls onPageChange when Next is clicked', async () => {
    const user = userEvent.setup()
    const onPageChange = vi.fn()
    render(<Pagination page={1} totalPages={5} onPageChange={onPageChange} />)
    await user.click(screen.getByText('Next'))
    expect(onPageChange).toHaveBeenCalledWith(2)
  })

  it('calls onPageChange when Previous is clicked', async () => {
    const user = userEvent.setup()
    const onPageChange = vi.fn()
    render(<Pagination page={2} totalPages={5} onPageChange={onPageChange} />)
    await user.click(screen.getByText('Previous'))
    expect(onPageChange).toHaveBeenCalledWith(1)
  })
})
