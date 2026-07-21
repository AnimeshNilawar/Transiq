export function Item({ label, value, mono }) {
  return (
    <div>
      <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">{label}</p>
      <p className={`text-sm font-medium mt-0.5 text-card-foreground ${mono ? 'font-mono tabular-nums' : ''}`}>{value || '-'}</p>
    </div>
  )
}
