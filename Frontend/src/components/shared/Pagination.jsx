export function Pagination({ page, totalPages, onPageChange }) {
  if (totalPages <= 1) return null

  return (
    <div className="flex items-center justify-between">
      <p className="text-sm text-muted-foreground">
        Page {page + 1} of {totalPages}
      </p>
      <div className="flex gap-2">
        <button
          onClick={() => onPageChange(Math.max(0, page - 1))}
          disabled={page === 0}
          className="inline-flex items-center justify-center rounded-md border border-border px-3 py-1.5 text-sm text-card-foreground disabled:opacity-50 hover:bg-muted transition-colors"
        >
          Previous
        </button>
        <button
          onClick={() => onPageChange(Math.min(totalPages - 1, page + 1))}
          disabled={page >= totalPages - 1}
          className="inline-flex items-center justify-center rounded-md border border-border px-3 py-1.5 text-sm text-card-foreground disabled:opacity-50 hover:bg-muted transition-colors"
        >
          Next
        </button>
      </div>
    </div>
  )
}
