import { useEffect, useState } from "react";
import StatePanel from "../components/StatePanel";
import WalletCard from "../components/WalletCard";
import { getWallets } from "../services/api";

function Wallets() {
  const [wallets, setWallets] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let active = true;

    async function loadWallets() {
      try {
        const walletData = await getWallets();
        if (active) {
          setWallets(walletData);
          setError("");
        }
      } catch (requestError) {
        if (active) {
          setError("Unable to fetch wallet balances right now.");
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    }

    loadWallets();
    return () => {
      active = false;
    };
  }, []);

  return (
    <div className="page-stack">
      <section className="page-header">
        <div>
          <p className="eyebrow">Assets & Custody</p>
          <h1>Wallet Portfolio</h1>
        </div>
        <p className="section-copy">
          View wallet balances returned by the Spring Boot backend and grouped
          across simulated exchange users.
        </p>
      </section>

      {error ? <StatePanel title={error} tone="error" /> : null}

      <section className="wallet-grid full-width">
        {loading ? (
          <StatePanel title="Loading wallet balances..." />
        ) : wallets.length === 0 ? (
          <StatePanel title="No demo wallets are available yet." />
        ) : (
          wallets.map((wallet) => (
            <WalletCard key={wallet.id} wallet={wallet} />
          ))
        )}
      </section>
    </div>
  );
}

export default Wallets;
