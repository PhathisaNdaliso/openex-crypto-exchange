function StatePanel({ title, tone = "neutral" }) {
  const className =
    tone === "error"
      ? "state-panel state-panel-error"
      : "state-panel";

  return <div className={className}>{title}</div>;
}

export default StatePanel;
