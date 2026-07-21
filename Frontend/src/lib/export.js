/**
 * Export data to CSV and trigger browser download
 * @param {Array<Object>} data - Array of objects to export
 * @param {Array<{key: string, header: string, render?: Function}>} columns - Column definitions
 * @param {string} filename - Download filename (without extension)
 */
export function exportToCSV(data, columns, filename) {
  const escapeCSV = (value) => {
    if (value == null) return ''
    const str = String(value)
    if (str.includes(',') || str.includes('"') || str.includes('\n')) {
      return `"${str.replace(/"/g, '""')}"`
    }
    return str
  }

  const headers = columns.map((col) => col.header)
  const rows = data.map((row) =>
    columns.map((col) => {
      const raw = row[col.key]
      const display = col.render ? col.render(raw, row) : raw ?? ''
      if (typeof display === 'object' && display !== null) {
        return escapeCSV(raw)
      }
      return escapeCSV(display)
    })
  )

  const csvContent = [headers.join(','), ...rows.map((r) => r.join(','))].join('\n')
  const blob = new Blob(['\uFEFF' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `${filename}.csv`
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}
