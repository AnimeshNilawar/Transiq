import { useSyncExternalStore } from 'react'
import { coldStartStore } from '@/hooks/coldStartStore'
import { Loader2 } from 'lucide-react'

function subscribe(fn) {
  coldStartStore.subscribe(fn)
  return () => {}
}

function getSnapshot() {
  return coldStartStore.getState()
}

export default function ColdStartMessage() {
  const { phase, elapsed } = useSyncExternalStore(subscribe, getSnapshot)

  if (phase === 'idle') return null

  return (
    <div className="flex flex-col items-center gap-2 text-center">
      {phase === 'loading' && (
        <>
          <Loader2 className="h-5 w-5 animate-spin text-muted-foreground" />
          <p className="text-sm text-muted-foreground">Processing...</p>
        </>
      )}
      {phase === 'stalling' && (
        <>
          <Loader2 className="h-5 w-5 animate-spin text-muted-foreground" />
          <p className="text-sm text-muted-foreground">Just a moment...</p>
        </>
      )}
      {phase === 'cold-start' && (
        <>
          <Loader2 className="h-5 w-5 animate-spin text-warning" />
          <p className="text-sm font-medium text-warning">
            Waking up the server &mdash; this can take up to a minute on the first request.
          </p>
          <p className="text-xs text-muted-foreground max-w-xs">
            This demo runs on free hosting that sleeps when idle. Thanks for your patience.
          </p>
        </>
      )}
    </div>
  )
}
