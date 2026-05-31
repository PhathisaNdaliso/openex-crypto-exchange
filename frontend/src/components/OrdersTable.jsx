import StatePanel from "./StatePanel";

function OrdersTable({ orders, loading = false }) {
  return (
    <section className="panel">
      <div className="panel-header">
        <div>
          <p className="eyebrow">Execution Feed</p>
          <h2>Recent Orders</h2>
        </div>
      </div>

      <div className="table-wrap">
        {loading ? (
          <StatePanel title="Loading recent orders..." />
        ) : (
          <table className="data-table">
            <thead>
              <tr>
                <th>Order</th>
                <th>Pair</th>
                <th>Side</th>
                <th>Status</th>
                <th>Quantity</th>
                <th>Price</th>
              </tr>
            </thead>
            <tbody>
              {orders.length === 0 ? (
                <tr>
                  <td colSpan="6" className="empty-state-cell">
                    No orders available yet.
                  </td>
                </tr>
              ) : (
                orders.map((order) => (
                  <tr key={order.id}>
                    <td>#{order.id}</td>
                    <td>
                      {order.baseCurrency}/{order.quoteCurrency}
                    </td>
                    <td>
                      <span
                        className={
                          order.side === "BUY" ? "pill pill-buy" : "pill pill-sell"
                        }
                      >
                        {order.side}
                      </span>
                    </td>
                    <td>{order.status}</td>
                    <td>{order.quantity}</td>
                    <td>{order.price ?? "Market"}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        )}
      </div>
    </section>
  );
}

export default OrdersTable;
