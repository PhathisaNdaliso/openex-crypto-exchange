function WalletCard({ wallet }) {
  const balance = Number(wallet.balance || 0).toLocaleString(undefined, {
    minimumFractionDigits: 2,
    maximumFractionDigits: 8,
  });
  const locked = Number(wallet.lockedBalance || 0).toLocaleString(undefined, {
    minimumFractionDigits: 2,
    maximumFractionDigits: 8,
  });

  return (
    <article className="wallet-card">
      <div className="wallet-card-header">
        <span className="wallet-currency">{wallet.currency}</span>
        <span className="wallet-user">User #{wallet.userId}</span>
      </div>
      <p className="wallet-balance">{balance}</p>
      <div className="wallet-card-footer">
        <div className="wallet-meta">
          <span>Available</span>
          <strong>{wallet.currency}</strong>
        </div>
        <div className="wallet-lock-row">
          <span>Locked</span>
          <strong>{locked}</strong>
        </div>
      </div>
    </article>
  );
}

export default WalletCard;
