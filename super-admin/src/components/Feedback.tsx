type AlertProps = {
  kind: "error" | "success";
  children: React.ReactNode;
};

/** Kayar girişli satır içi uyarı (hata / başarı). */
export function Alert({ kind, children }: AlertProps) {
  return <div className={`alert alert-${kind}`} role={kind === "error" ? "alert" : "status"}>{children}</div>;
}

type EmptyStateProps = {
  title: string;
  hint?: string;
};

/** Boş tablo/liste durumu. */
export function EmptyState({ title, hint }: EmptyStateProps) {
  return (
    <div className="empty-state">
      <div className="empty-title">{title}</div>
      {hint ? <div>{hint}</div> : null}
    </div>
  );
}

type TableSkeletonProps = {
  rows?: number;
};

/** Tablo iskeleti (shimmer satırlar). */
export function TableSkeleton({ rows = 6 }: TableSkeletonProps) {
  return (
    <div className="skel-table" aria-label="Yükleniyor">
      {Array.from({ length: rows }, (_, i) => (
        <div key={i} className="skel skel-row" />
      ))}
    </div>
  );
}
