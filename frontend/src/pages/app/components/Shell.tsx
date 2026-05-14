import { useEffect, useState } from "react";
import Navbar from "./Navbar";
import Sidebar from "./Sidebar";
import { apiGet } from "../utils/api";
import type { ConnectedAccount, CurrentUser } from "../types";
import "./Shell.css";

type ShellProps = {
  children: React.ReactNode;
};

export default function Shell({ children }: ShellProps) {
  const [user, setUser] = useState<CurrentUser | null>(null);
  const [accounts, setAccounts] = useState<ConnectedAccount[]>([]);
  const [loadingUser, setLoadingUser] = useState(true);

  useEffect(() => {
    apiGet<CurrentUser>("/api/auth/me")
      .then(setUser)
      .catch(() => {
        window.location.href = "/login";
      })
      .finally(() => setLoadingUser(false));

    apiGet<ConnectedAccount[]>("/api/accounts")
      .then(setAccounts)
      .catch((error) => console.error(error));
  }, []);

  if (loadingUser || !user) {
    return (
      <main className="loading-state">
        <p>Loading your workspace...</p>
      </main>
    );
  }

  return (
    <main className="workspace">
      <Navbar user={user} />
      <div className="workspace-body">
        <Sidebar accounts={accounts} />
        <section className="workspace-content">{children}</section>
      </div>
    </main>
  );
}
