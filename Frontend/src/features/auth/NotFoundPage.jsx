import { Link } from 'react-router-dom'
import { Inbox } from 'lucide-react'

export default function NotFoundPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-background">
      <div className="w-full max-w-md space-y-6 text-center">
        <div className="rounded-full bg-muted p-4 inline-flex mx-auto">
          <Inbox className="h-10 w-10 text-muted-foreground" />
        </div>
        <div>
          <h1 className="text-2xl font-bold text-foreground">404</h1>
          <p className="text-sm text-muted-foreground mt-2">
            The page you're looking for doesn't exist.
          </p>
        </div>
        <Link
          to="/"
          className="inline-flex items-center justify-center rounded-md bg-accent px-4 py-2 text-sm font-medium text-accent-foreground hover:bg-accent/90 transition-colors"
        >
          Go to Dashboard
        </Link>
      </div>
    </div>
  )
}
