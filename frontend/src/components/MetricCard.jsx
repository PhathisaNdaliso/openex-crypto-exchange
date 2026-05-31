function MetricCard({ label, value, accent = "neutral", detail = "" }) {
  const className =
    accent === "positive"
      ? "metric-card metric-card-positive"
      : accent === "warning"
        ? "metric-card metric-card-warning"
        : "metric-card";

  return (
    <article className={className}>
      <span>{label}</span>
      <strong>{value}</strong>
      {detail ? <p className="metric-detail">{detail}</p> : null}
    </article>
  );
}

export default MetricCard;
