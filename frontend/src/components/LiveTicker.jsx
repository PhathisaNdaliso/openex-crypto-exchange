import StatePanel from "./StatePanel";

function LiveTicker({ items, connectionLabel, loading = false, compact = false }) {
  return (
    <section className={compact ? "panel ticker-panel ticker-panel-compact" : "panel ticker-panel"}>
      <div className={compact ? "panel-header panel-header-tight" : "panel-header"}>
        <div>
          <p className="eyebrow">Streaming Market Data</p>
          <h2>Live Ticker</h2>
        </div>
        <span className="connection-indicator">{connectionLabel}</span>
      </div>

      {loading ? (
        <StatePanel title="Loading market ticker..." />
      ) : items.length === 0 ? (
        <StatePanel title="No market prices available yet." />
      ) : (
        <div className={compact ? "ticker-list ticker-list-compact" : "ticker-list"}>
          {items.map((item) => {
            const positive = Number(item.changePercent) >= 0;
            return (
              <article key={item.symbol} className="ticker-card">
                <div>
                  <p className="ticker-symbol">{item.symbol}</p>
                  <p className="ticker-price">${Number(item.price).toLocaleString()}</p>
                </div>
                <div className={positive ? "ticker-change up" : "ticker-change down"}>
                  {positive ? "+" : ""}
                  {Number(item.changePercent).toFixed(2)}%
                </div>
              </article>
            );
          })}
        </div>
      )}
    </section>
  );
}

export default LiveTicker;
