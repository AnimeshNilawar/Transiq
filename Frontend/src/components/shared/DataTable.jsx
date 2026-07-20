import { cn } from '@/lib/utils'

/**
 * @param {{ columns: { key: string, header: string, render?: (value: any, row: any) => React.ReactNode, className?: string }[], data: any[], onRowClick?: (row: any) => void, emptyTitle?: string, emptyDescription?: string }} props
 */
export function DataTable({
  columns,
  data,
  onRowClick,
  emptyDescription,
}) {
  if (!data || data.length === 0) {
    return (
      <div className="text-center py-12">
        <p className="text-sm text-muted-foreground">
          {emptyDescription || 'No data available'}
        </p>
      </div>
    )
  }

  return (
    <div className="overflow-x-auto rounded-lg border">
      <table className="w-full caption-bottom text-sm">
        <thead className="[&_tr]:border-b">
          <tr className="border-b transition-colors hover:bg-muted/50">
            {columns.map((col) => (
              <th
                key={col.key}
                className="h-12 px-4 text-left align-middle font-medium text-muted-foreground"
              >
                {col.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="[&_tr:last-child]:border-0">
          {data.map((row, rowIdx) => (
            <tr
              key={rowIdx}
              className={cn(
                'border-b transition-colors hover:bg-muted/50',
                onRowClick && 'cursor-pointer'
              )}
              onClick={() => onRowClick?.(row)}
            >
              {columns.map((col) => (
                <td key={col.key} className="p-4 align-middle">
                  {col.render
                    ? col.render(row[col.key], row)
                    : row[col.key] ?? '-'}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
