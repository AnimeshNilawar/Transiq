import { getStatusColor } from '@/lib/constants'
import { cn } from '@/lib/utils'

/**
 * @param {{ status: string, className?: string }} props
 */
export function StatusBadge({ status, className }) {
  return (
    <span
      className={cn(
        'inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-medium',
        getStatusColor(status),
        className
      )}
    >
      {status}
    </span>
  )
}
