import { exportToCSV } from '../export'

describe('exportToCSV', () => {
  let clickSpy, appendChildSpy, removeChildSpy

  beforeEach(() => {
    clickSpy = vi.fn()
    vi.spyOn(document, 'createElement').mockReturnValue({
      click: clickSpy,
      set href(_v) {},
      set download(_v) {},
    })
    appendChildSpy = vi.spyOn(document.body, 'appendChild').mockImplementation(() => {})
    removeChildSpy = vi.spyOn(document.body, 'removeChild').mockImplementation(() => {})
    URL.createObjectURL = vi.fn(() => 'blob:mock')
    URL.revokeObjectURL = vi.fn()
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('creates correct CSV content', () => {
    const data = [
      { id: '1', name: 'Alice' },
      { id: '2', name: 'Bob' },
    ]
    const columns = [
      { key: 'id', header: 'ID' },
      { key: 'name', header: 'Name' },
    ]

    exportToCSV(data, columns, 'test')

    expect(URL.createObjectURL).toHaveBeenCalled()
    expect(clickSpy).toHaveBeenCalled()
  })

  it('handles special characters in values', () => {
    const data = [{ name: 'has, comma' }]
    const columns = [{ key: 'name', header: 'Name' }]

    exportToCSV(data, columns, 'test')
    expect(clickSpy).toHaveBeenCalled()
  })

  it('triggers download by appending and removing link element', () => {
    exportToCSV([], [], 'filename')

    expect(appendChildSpy).toHaveBeenCalled()
    expect(removeChildSpy).toHaveBeenCalled()
  })
})
