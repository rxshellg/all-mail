import { useState } from "react";
import { NavLink } from "react-router-dom";
import type { CurrentUser } from "../types";
import "./Navbar.css";

type NavbarProps = {
  user: CurrentUser;
};

export default function Navbar({ user }: NavbarProps) {
  const [dropdownOpen, setDropdownOpen] = useState(false);

  return (
    <nav className="navbar sticky-top bg-white workspace-navbar">
      <div className="container-fluid flex-nowrap">
        <NavLink to="/dashboard" className="navbar-logo-section">
          <img src="/Logo.png" alt="AllMail logo" />
        </NavLink>

        <div className="navbar-search d-none d-md-flex">
          <div className="input-group">
            <span className="input-group-text bg-white border-end-0">
              <i className="bi bi-search"></i>
            </span>
            <input
              className="form-control bg-white border-start-0"
              type="search"
              placeholder="Search mail"
              disabled
            />
          </div>
        </div>

        <div className="d-flex align-items-center gap-2">
          <button className="btn border-0" type="button" disabled>
            <i className="bi bi-bell"></i>
          </button>

          <div className="position-relative">
            <button
              className="btn border-0 d-flex align-items-center gap-2"
              type="button"
              onClick={() => setDropdownOpen((current) => !current)}
            >
              {user.pictureUrl ? (
                <img
                  src={user.pictureUrl}
                  alt={`${user.name}'s profile`}
                  className="navbar-user-avatar"
                  referrerPolicy="no-referrer"
                />
              ) : (
                <span className="navbar-user-avatar navbar-user-placeholder">
                  {user.name.charAt(0).toUpperCase()}
                </span>
              )}

              <span className="d-none d-lg-inline">{user.name}</span>
              <i className="bi bi-chevron-down small"></i>
            </button>

            {dropdownOpen && (
              <div className="navbar-user-menu shadow-sm rounded-3 bg-white border">
                <button className="dropdown-item" type="button" disabled>
                  <i className="bi bi-gear me-2"></i>
                  Settings
                </button>

                <button
                  className="dropdown-item"
                  type="button"
                  onClick={() =>
                    (window.location.href = "http://localhost:8080/logout")
                  }
                >
                  <i className="bi bi-box-arrow-right me-2"></i>
                  Logout
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}
