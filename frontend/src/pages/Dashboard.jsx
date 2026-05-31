import { useEffect, useRef, useState } from "react";
import ActivityFeed from "../components/ActivityFeed";
import LiveTicker from "../components/LiveTicker";
import MetricCard from "../components/MetricCard";
import StatePanel from "../components/StatePanel";
import TradingChart from "../components/TradingChart";
import WalletCard from "../components/WalletCard";
import { getMarketOverview, getOrders, getUsers, getWallets } from "../services/api";
import { createTradeSocket } from "../services/websocket";

function Dashboard() {
  const [users, setUsers] = useState([]);
  const [wallets, setWallets] = useState([]);
  const [orders, setOrders] = useState([]);
  const [series, setSeries] = useState({});
  const [tickerItems, setTickerItems] = useState([]);
  const [metrics, setMetrics] = useState({
    volume24h: 0,
    marketCap: 0,
    btcDominance: 0,
    activeTradesCount: 0,
  });
  const [connectionLabel, setConnectionLabel] = useState("Connecting...");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const socketRef = useRef(null);

  useEffect(() => {
    let active = true;

    async function loadDashboard() {
      try {
        const [usersData, walletsData, ordersData, marketOverview] = await Promise.all([
          getUsers(),
          getWallets(),
          getOrders(),
          getMarketOverview(),
        ]);

        if (!active) {
          return;
        }

        setUsers(usersData);
        setWallets(walletsData);
        setOrders(
          [...ordersData].sort(
            (left, right) =>
              new Date(right.createdAt || 0).getTime() -
              new Date(left.createdAt || 0).getTime()
          )
        );
        setTickerItems(marketOverview.tickers);
        setSeries(marketOverview.history);
        setMetrics(marketOverview.metrics);
        setError("");
      } catch (requestError) {
        if (active) {
          setError("Unable to fetch dashboard data from the backend.");
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    loadDashboard();

    socketRef.current = createTradeSocket({
      onStatus: setConnectionLabel,
      onTrade: (trade) => {
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

        setSeries((current) => {
          const symbolPoints = current[trade.symbol] ?? [];
          const nextPoints = [
            ...symbolPoints,
            {
              timestamp: trade.timestamp,
              price: Number(trade.price),
              volume: Number(trade.volume),
            },
          ].slice(-24);

          return {
            ...current,
            [trade.symbol]: nextPoints,
          };
        });

        setMetrics((current) => ({
          ...current,
          activeTradesCount: current.activeTradesCount,
        }));
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

  const totalWallets = wallets.length;
  const totalUsers = users.length;
  const chartPoints = series["BTC/USD"] ?? [];
  const btcTicker = tickerItems.find((item) => item.symbol === "BTC/USD");
  const formattedVolume = `$${Number(metrics.volume24h || 0).toLocaleString()}`;
  const formattedMarketCap = `$${Number(metrics.marketCap || 0).toLocaleString()}`;
  const formattedDominance = `${Number(metrics.btcDominance || 0).toFixed(2)}%`;
  const heroPrice = btcTicker
    ? `$${Number(btcTicker.price).toLocaleString()}`
    : "";
  const heroChange = btcTicker
    ? `${Number(btcTicker.changePercent) >= 0 ? "+" : ""}${Number(btcTicker.changePercent).toFixed(2)}%`
    : "";

  return (
    <div className="page-stack dashboard-page">
      <section className="dashboard-intro">
        <div>
          <p className="eyebrow">OpenEx Market Overview</p>
          <h1>Trade, monitor, and react from one clear operating surface.</h1>
          <p className="hero-copy">
            OpenEx focuses the dashboard on live market movement first, with wallet exposure
            and execution activity supporting the decision flow rather than competing with it.
          </p>
        </div>
        <div className="dashboard-intro-meta">
          <span className="connection-indicator connection-indicator-hero">{connectionLabel}</span>
          <p className="intro-caption">{totalUsers} traders · {totalWallets} funded wallets</p>
        </div>
      </section>

      {error ? <StatePanel title={error} tone="error" /> : null}

      <section className="hero-market-layout">
        <TradingChart
          title="BTC market depth"
          pair="BTC/USD"
          points={chartPoints}
          loading={loading}
          emptyMessage="No seeded BTC history is available."
          priceLabel={heroPrice}
          changeLabel={heroChange}
          changePositive={Number(btcTicker?.changePercent || 0) >= 0}
        />

        <aside className="market-rail">
          <section className="metrics-grid metrics-grid-stack">
            <MetricCard label="24h Volume" value={formattedVolume} accent="positive" detail="Aggregate traded value" />
            <MetricCard label="Market Cap" value={formattedMarketCap} detail="Simulated listed assets" />
            <MetricCard label="BTC Dominance" value={formattedDominance} accent="warning" detail="Share of market cap" />
            <MetricCard label="Active Trades" value={metrics.activeTradesCount} detail="Open and partially filled" />
          </section>

          <LiveTicker
            items={tickerItems}
            connectionLabel={connectionLabel}
            loading={loading}
            compact
          />
        </aside>
      </section>

      <section className="dashboard-lower-grid">
        <section className="panel wallet-summary-panel wallet-summary-panel-refined">
          <div className="panel-header">
            <div>
              <p className="eyebrow">Portfolio Snapshot</p>
              <h2>Funded Wallets</h2>
            </div>
            <span className="section-note">Most relevant balances</span>
          </div>

          <div className="wallet-grid">
            {loading ? (
              <StatePanel title="Loading wallet balances..." />
            ) : wallets.length === 0 ? (
              <StatePanel title="No wallet data returned by the backend yet." />
            ) : (
              wallets.slice(0, 4).map((wallet) => (
                <WalletCard key={wallet.id} wallet={wallet} />
              ))
            )}
          </div>
        </section>

        <ActivityFeed orders={orders.slice(0, 6)} loading={loading} />
      </section>
    </div>
  );
}

export default Dashboard;
