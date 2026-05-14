import { NavLink } from "react-router-dom";
import type { ConnectedAccount } from "../types";
import "./Sidebar.css";

type SidebarProps = {
  accounts: ConnectedAccount[];
};

const ACCOUNT_COLORS = [
  { bg: "#EBEDFE", color: "#2C42F1" },
  { bg: "#FFF1CF", color: "#D99A00" },
  { bg: "#DEF5E8", color: "#3F9D68" },
  { bg: "#FFE8EC", color: "#E85D75" },
];

type NavItemProps = {
  to: string;
  icon: string;
  label: string;
  extraClass?: string;
};

function NavItem({ to, icon, label, extraClass = "" }: NavItemProps) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) =>
        `nav-link sidebar-link ${extraClass} ${isActive ? "active" : ""}`.trim()
      }
    >
      <i className={`bi ${icon} sidebar-icon`}></i>
      <span className="sidebar-text">{label}</span>
    </NavLink>
  );
}

export default function Sidebar({ accounts }: SidebarProps) {
  return (
    <aside className="sidebar sidebar-narrow-unfoldable shadow-sm bg-white">
      <nav className="nav flex-column h-100 p-3">
        <NavItem to="/dashboard" icon="bi-grid" label="Dashboard" />
        <NavItem to="/inbox" icon="bi-inboxes" label="All Inboxes" />

        <p className="sidebar-title fw-semibold text-uppercase small mt-4 mb-2">
          Accounts
        </p>

        {accounts.map(({ id, emailAddress }, index) => {
          const { bg, color } = ACCOUNT_COLORS[index % ACCOUNT_COLORS.length];

          return (
            <NavLink
              key={id}
              to={`/accounts/${id}/inbox`}
              className={({ isActive }) =>
                `nav-link sidebar-link ${isActive ? "active" : ""}`.trim()
              }
            >
              <span
                className="sidebar-account-icon"
                style={
                  {
                    "--account-bg": bg,
                    "--account-color": color,
                  } as React.CSSProperties
                }
              >
                <i className="bi bi-envelope"></i>
              </span>

              <span className="sidebar-text">{emailAddress}</span>
            </NavLink>
          );
        })}

        <button
          className="nav-link sidebar-link mt-2"
          type="button"
          onClick={() => {
            window.location.href =
              "http://localhost:8080/api/accounts/connect/google";
          }}
        >
          <i className="bi bi-plus-circle sidebar-icon"></i>
          <span className="sidebar-text">Add account</span>
        </button>
      </nav>
    </aside>
  );
}
