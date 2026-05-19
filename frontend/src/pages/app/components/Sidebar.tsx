import { useState, type CSSProperties } from "react";
import { NavLink } from "react-router-dom";
import type { ConnectedAccount } from "../types";
import AccountsModal from "./AccountsModal";
import "./Sidebar.css";

type SidebarProps = {
  accounts: ConnectedAccount[];
  refreshAccounts: () => Promise<void>;
};

const ACCOUNT_COLORS = [
  { bg: "#EBEDFE", color: "#2C42F1" },
  { bg: "#FFF1CF", color: "#D99A00" },
  { bg: "#DEF5E8", color: "#3F9D68" },
  { bg: "#FFE8EC", color: "#E85D75" },
];

function navClass(...extra: string[]) {
  return ({ isActive }: { isActive: boolean }) =>
    ["nav-link sidebar-link", ...extra, isActive && "active"]
      .filter(Boolean)
      .join(" ");
}

function NavItem({
  to,
  icon,
  label,
}: {
  to: string;
  icon: string;
  label: string;
}) {
  return (
    <NavLink to={to} className={navClass()}>
      <i className={`bi ${icon} sidebar-icon`} />
      <span className="sidebar-text">{label}</span>
    </NavLink>
  );
}

function AccountNavItem({
  id,
  emailAddress,
  index,
}: ConnectedAccount & { index: number }) {
  const { bg, color } = ACCOUNT_COLORS[index % ACCOUNT_COLORS.length];

  return (
    <NavLink to={`/accounts/${id}/inbox`} className={navClass()}>
      <span
        className="sidebar-account-icon"
        style={
          { "--account-bg": bg, "--account-color": color } as CSSProperties
        }
      >
        <i className="bi bi-envelope" />
      </span>
      <span className="sidebar-text">{emailAddress}</span>
    </NavLink>
  );
}

export default function Sidebar({ accounts, refreshAccounts }: SidebarProps) {
  const [modalOpen, setModalOpen] = useState(false);

  return (
    <>
      <aside className="sidebar sidebar-narrow-unfoldable shadow-sm bg-white">
        <nav className="nav flex-column h-100 p-3">
          <NavItem to="/dashboard" icon="bi-grid" label="Dashboard" />
          <NavItem to="/inbox" icon="bi-inboxes" label="All Inboxes" />

          <p className="sidebar-title fw-semibold text-uppercase small mt-4 mb-2">
            Accounts
          </p>

          {accounts.map((account, index) => (
            <AccountNavItem key={account.id} {...account} index={index} />
          ))}

          <button
            className="nav-link sidebar-link mt-2"
            type="button"
            onClick={() => setModalOpen(true)}
          >
            <i className="bi bi-pencil-square sidebar-icon" />
            <span className="sidebar-text">Manage accounts</span>
          </button>
        </nav>
      </aside>

      {modalOpen && (
        <AccountsModal
          accounts={accounts}
          onClose={() => setModalOpen(false)}
          refreshAccounts={refreshAccounts}
        />
      )}
    </>
  );
}
