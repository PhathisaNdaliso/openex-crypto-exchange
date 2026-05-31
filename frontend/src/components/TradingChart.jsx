import {
  CategoryScale,
  Chart as ChartJS,
  Filler,
  Legend,
  LinearScale,
  LineElement,
  PointElement,
  Tooltip,
} from "chart.js";
import { Line } from "react-chartjs-2";
import StatePanel from "./StatePanel";

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Tooltip,
  Legend,
  Filler
);

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  interaction: {
    intersect: false,
    mode: "index",
  },
  plugins: {
    legend: {
      display: false,
    },
    tooltip: {
      backgroundColor: "rgba(9, 16, 28, 0.96)",
      borderColor: "rgba(50, 211, 194, 0.30)",
      borderWidth: 1,
      padding: 12,
      displayColors: false,
    },
  },
  animation: {
    duration: 500,
    easing: "easeOutQuart",
  },
  scales: {
    x: {
      grid: {
        color: "rgba(255, 255, 255, 0.05)",
      },
      ticks: {
        color: "#93a4c3",
      },
    },
    y: {
      grid: {
        color: "rgba(255, 255, 255, 0.05)",
      },
      ticks: {
        color: "#93a4c3",
      },
    },
  },
};

function TradingChart({
  title,
  pair,
  points,
  loading = false,
  emptyMessage = "No chart points available.",
  priceLabel = "",
  changeLabel = "",
  changePositive = true,
}) {
  const labels = points.map((point) =>
    new Date(point.timestamp).toLocaleTimeString([], {
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
    })
  );

  const data = {
    labels,
    datasets: [
      {
        label: pair,
        data: points.map((point) => point.price),
        borderColor: "#32d3c2",
        backgroundColor: "rgba(50, 211, 194, 0.14)",
        tension: 0.28,
        borderWidth: 2.4,
        pointRadius: 0,
        pointHoverRadius: 5,
        fill: true,
      },
    ],
  };

  return (
    <section className="panel chart-panel chart-panel-hero">
      <div className="panel-header chart-panel-header">
        <div>
          <p className="eyebrow">Live Market View</p>
          <h2>{title}</h2>
          <p className="chart-subtitle">Rolling live market graph with seeded history and trade stream updates.</p>
        </div>
        <div className="chart-hero-meta">
          <span className="pair-badge">{pair}</span>
          {priceLabel ? <strong className="chart-price">{priceLabel}</strong> : null}
          {changeLabel ? (
            <span className={changePositive ? "chart-change chart-change-up" : "chart-change chart-change-down"}>
              {changeLabel}
            </span>
          ) : null}
        </div>
      </div>
      {loading ? (
        <StatePanel title="Loading chart history..." />
      ) : points.length === 0 ? (
        <StatePanel title={emptyMessage} />
      ) : (
        <div className="chart-wrapper">
          <Line data={data} options={chartOptions} />
        </div>
      )}
    </section>
  );
}

export default TradingChart;
