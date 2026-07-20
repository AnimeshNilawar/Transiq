import { Inbox } from 'lucide-react'

/**
 * @param {{ title?: string, description?: string, icon?: React.ReactNode, action?: React.ReactNode }} props
 */
export function EmptyState({
  title = 'No data found',
  description = 'There are no items to display.',
  icon,
  action,
}) {
  return (
    <div className="flex flex-col items-center justify-center py-12 text-center">
      <div className="rounded-full bg-muted p-4 mb-4">
        {icon || <Inbox className="h-8 w-8 text-muted-foreground" />}
      </div>
      <h3 className="text-lg font-semibold mb-1">{title}</h3>
      <p className="text-sm text-muted-foreground mb-4 max-w-sm">
        {description}
      </p>
      {action}
    </div>
  )
}
