import StatePanel from "./StatePanel";

function ActivityFeed({ orders, loading = false }) {
  return (
    <section className="panel activity-panel">
      <div className="panel-header panel-header-tight">
        <div>
          <p className="eyebrow">Execution Feed</p>
          <h2>Recent Activity</h2>
        </div>
      </div>

      {loading ? (
        <StatePanel title="Loading recent activity..." />
      ) : orders.length === 0 ? (
        <StatePanel title="No recent activity available." />
      ) : (
        <div className="activity-list">
          {orders.map((order) => (
            <article key={order.id} className="activity-card">
              <div className="activity-card-top">
                <div>
                  <p className="activity-pair">
                    {order.baseCurrency}/{order.quoteCurrency}
                  </p>
                  <p className="activity-id">Order #{order.id}</p>
                </div>
                <span
                  className={
                    order.side === "BUY" ? "pill pill-buy" : "pill pill-sell"
                  }
                >
                  {order.side}
                </span>
              </div>

              <div className="activity-grid">
                <div>
                  <span className="activity-label">Status</span>
                  <strong>{order.status.replaceAll("_", " ")}</strong>
                </div>
                <div>
                  <span className="activity-label">Quantity</span>
                  <strong>{order.quantity}</strong>
                </div>
                <div>
                  <span className="activity-label">Price</span>
                  <strong>{order.price ?? "Market"}</strong>
                </div>
              </div>
            </article>
          ))}
        </div>
      )}
    </section>
  );
}

export default ActivityFeed;
