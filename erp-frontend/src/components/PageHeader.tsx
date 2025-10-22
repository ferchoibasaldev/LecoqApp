export default function PageHeader(
  { title, subtitle, children }:
  { title: string; subtitle?: string; children?: React.ReactNode }
) {
  return (
    <div className="mb-5">
      <div className="flex items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-extrabold text-brand-greenDark">{title}</h1>
          {subtitle && (
            <p className="text-sm text-brand-blue/70 mt-1">{subtitle}</p>
          )}
        </div>
        <div className="flex items-center gap-3">{children}</div>
      </div>
    </div>
  );
}
