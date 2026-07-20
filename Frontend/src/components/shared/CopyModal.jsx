import { useState, useCallback } from 'react'
import { Copy, Check, AlertTriangle } from 'lucide-react'
import { cn } from '@/lib/utils'

/**
 * @param {{ open: boolean, onClose: () => void, title: string, secretLabel: string, secretValue: string, warning?: string }} props
 */
export function CopyModal({
  open,
  onClose,
  title,
  secretLabel,
  secretValue,
  warning = "You won't be able to see this again. Make sure to copy and store it safely.",
}) {
  const [copied, setCopied] = useState(false)

  const handleCopy = useCallback(async () => {
    await navigator.clipboard.writeText(secretValue)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }, [secretValue])

  if (!open) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div className="fixed inset-0 bg-black/50" onClick={onClose} />
      <div className="relative bg-background rounded-lg border shadow-lg p-6 w-full max-w-md mx-4">
        <h2 className="text-lg font-semibold mb-4">{title}</h2>

        <div className="bg-yellow-50 border border-yellow-200 rounded-md p-3 mb-4 flex gap-2">
          <AlertTriangle className="h-5 w-5 text-yellow-600 shrink-0 mt-0.5" />
          <p className="text-sm text-yellow-800">{warning}</p>
        </div>

        <div className="space-y-2 mb-6">
          <label className="text-sm font-medium text-muted-foreground">
            {secretLabel}
          </label>
          <div className="flex items-center gap-2">
            <code className="flex-1 p-3 bg-muted rounded-md text-sm font-mono break-all">
              {secretValue}
            </code>
            <button
              onClick={handleCopy}
              className={cn(
                'shrink-0 inline-flex items-center justify-center rounded-md h-10 w-10 border',
                'hover:bg-accent hover:text-accent-foreground transition-colors'
              )}
            >
              {copied ? (
                <Check className="h-4 w-4 text-green-600" />
              ) : (
                <Copy className="h-4 w-4" />
              )}
            </button>
          </div>
        </div>

        <button
          onClick={onClose}
          className="w-full inline-flex items-center justify-center rounded-md text-sm font-medium h-10 px-4 py-2 bg-primary text-primary-foreground hover:bg-primary/90 transition-colors"
        >
          I've saved it
        </button>
      </div>
    </div>
  )
}
