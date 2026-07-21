import { Component } from 'react'
import { AlertTriangle } from 'lucide-react'

export class ErrorBoundary extends Component {
  constructor(props) {
    super(props)
    this.state = { hasError: false, error: null }
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error }
  }

  componentDidCatch(error, errorInfo) {
    console.error('ErrorBoundary caught:', error, errorInfo)
  }

  handleReset = () => {
    this.setState({ hasError: false, error: null })
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="flex min-h-screen items-center justify-center bg-background">
          <div className="w-full max-w-md space-y-6 text-center">
            <div className="rounded-full bg-destructive/10 p-4 inline-flex mx-auto">
              <AlertTriangle className="h-10 w-10 text-destructive" />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-foreground">Something went wrong</h1>
              <p className="text-sm text-muted-foreground mt-2">
                An unexpected error occurred. Please try again.
              </p>
              {this.state.error?.message && (
                <p className="text-xs text-muted-foreground mt-2 font-mono bg-muted p-2 rounded-md">
                  {this.state.error.message}
                </p>
              )}
            </div>
            <button
              onClick={this.handleReset}
              className="inline-flex items-center justify-center rounded-md bg-accent px-4 py-2 text-sm font-medium text-accent-foreground hover:bg-accent/90 transition-colors"
            >
              Try Again
            </button>
          </div>
        </div>
      )
    }

    return this.props.children
  }
}
