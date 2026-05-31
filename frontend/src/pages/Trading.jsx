import { useEffect, useRef, useState } from "react";
import LiveTicker from "../components/LiveTicker";
import OrdersTable from "../components/OrdersTable";
import StatePanel from "../components/StatePanel";
import TradingChart from "../components/TradingChart";
import { getMarketOverview, getOrders } from "../services/api";
import { createTradeSocket } from "../services/websocket";

function Trading() {
  const [orders, setOrders] = useState([]);
  const [selectedPair, setSelectedPair] = useState("BTC/USD");
  const [tickerItems, setTickerItems] = useState([]);
  const [tradeLog, setTradeLog] = useState([]);
  const [series, setSeries] = useState({});
  const [connectionLabel, setConnectionLabel] = useState("Connecting...");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const socketRef = useRef(null);

  useEffect(() => {
    let active = true;

    async function loadOrders() {
      try {
        const [orderData, marketOverview] = await Promise.all([
          getOrders(),
          getMarketOverview(),
        ]);
        if (active) {
          setOrders(orderData);
          setTickerItems(marketOverview.tickers);
          setSeries(marketOverview.history);
          setTradeLog(
            marketOverview.tickers.flatMap((ticker) =>
              (marketOverview.history[ticker.symbol] ?? []).map((point) => ({
                symbol: ticker.symbol,
                price: point.price,
                volume: point.volume,
                side: Number(point.price) >= Number(ticker.price) ? "BUY" : "SELL",
                changePercent: ticker.changePercent,
                timestamp: point.timestamp,
              }))
            ).sort(
              (left, right) =>
                new Date(right.timestamp).getTime() - new Date(left.timestamp).getTime()
            ).slice(0, 24)
          );
          setError("");
        }
      } catch (requestError) {
        if (active) {
          setError("Unable to fetch trading orders from the backend.");
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    loadOrders();

    socketRef.current = createTradeSocket({
      onStatus: setConnectionLabel,
      onTrade: (trade) => {
        setTradeLog((current) => [trade, ...current].slice(0, 24));
        setSeries((current) => {
          const symbolPoints = current[trade.symbol] ?? [];
          return {
            ...current,
            [trade.symbol]: [
              ...symbolPoints,
              {
                timestamp: trade.timestamp,
                price: Number(trade.price),
                volume: Number(trade.volume),
              },
            ].slice(-24),
          };
        });
        setTickerItems((current) => {
          const next = [...current];
          const foundIndex = next.findIndex((item) => item.symbol === trade.symbol);
          const normalizedItem = {
            symbol: trade.symbol,
            price: Number(trade.price),
            changePercent: Number(trade.changePercent),
          };

          if (foundIndex === -1) {
            next.push(normalizedItem);
            return next;
          }

          next[foundIndex] = normalizedItem;
          return next;
        });
      },
      onError: () => {
        setConnectionLabel("Trade feed disconnected");
      },
    });

    return () => {
      active = false;
      socketRef.current?.disconnect();
    };
  }, []);

  const chartPoints = series[selectedPair] ?? [];

  return (
    <div className="page-stack">
      <section className="page-header">
        <div>
          <p className="eyebrow">Trading Desk</p>
          <h1>Market execution monitor</h1>
        </div>
        <div className="segmented-toggle">
          {["BTC/USD", "ETH/USD", "SOL/USD", "USDT/USD"].map((pair) => (
            <button
              key={pair}
              type="button"
              className={
                pair === selectedPair
                  ? "toggle-button toggle-button-active"
                  : "toggle-button"
              }
              onClick={() => setSelectedPair(pair)}
            >
              {pair}
            </button>
          ))}
        </div>
      </section>

      {error ? <StatePanel title={error} tone="error" /> : null}

      <LiveTicker items={tickerItems} connectionLabel={connectionLabel} loading={loading} />

      <section className="content-grid">
        <TradingChart
          title="Streaming trade activity"
          pair={selectedPair}
          points={chartPoints}
          loading={loading}
          emptyMessage={`No ${selectedPair} history is available yet.`}
        />

        <section className="panel trade-log-panel">
          <div className="panel-header">
            <div>
              <p className="eyebrow">WebSocket Activity</p>
              <h2>Live Trade Tape</h2>
            </div>
          </div>
          <div className="trade-log">
            {loading ? (
              <StatePanel title="Loading recent trade activity..." />
            ) : tradeLog.length === 0 ? (
              <StatePanel title="Waiting for `/topic/trades` updates from the backend." />
            ) : (
              tradeLog.map((trade, index) => (
                <article key={`${trade.symbol}-${trade.timestamp}-${index}`} className="trade-log-item">
                  <div>
                    <strong>{trade.symbol}</strong>
                    <p>{new Date(trade.timestamp).toLocaleTimeString()}</p>
                  </div>
                  <div>
                    <span className={trade.side === "BUY" ? "pill pill-buy" : "pill pill-sell"}>
                      {trade.side}
                    </span>
                    <p>${Number(trade.price).toLocaleString()}</p>
                  </div>
                </article>
              ))
            )}
          </div>
        </section>
      </section>

      <OrdersTable orders={orders.slice(0, 10)} loading={loading} />
    </div>
  );
}

export default Trading;
