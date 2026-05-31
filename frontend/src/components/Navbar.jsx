import { NavLink } from "react-router-dom";

const navItems = [
  { to: "/", label: "Dashboard" },
  { to: "/wallets", label: "Wallets" },
  { to: "/trading", label: "Trading" },
];

function Navbar() {
  return (
    <header className="navbar">
      <div className="brand-block">
        <span className="brand-mark">OX</span>
        <div>
          <p className="brand-name">OpenEx</p>
          <p className="brand-tagline">Crypto Exchange Dashboard</p>
        </div>
      </div>

      <nav className="nav-links" aria-label="Primary">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) =>
              isActive ? "nav-link nav-link-active" : "nav-link"
            }
          >
            {item.label}
          </NavLink>
        ))}
      </nav>
    </header>
  );
}

export default Navbar;
