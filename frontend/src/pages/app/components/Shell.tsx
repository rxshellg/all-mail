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

  const refreshAccounts = (): Promise<void> =>
    apiGet<ConnectedAccount[]>("/api/accounts")
      .then(setAccounts)
      .catch(console.error);

  useEffect(() => {
    apiGet<CurrentUser>("/api/auth/me")
      .then(setUser)
      .catch(() => {
        window.location.href = "/login";
      })
      .finally(() => setLoadingUser(false));

    refreshAccounts();
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
        <Sidebar accounts={accounts} refreshAccounts={refreshAccounts}/>
        <section className="workspace-content">{children}</section>
      </div>
    </main>
  );
}
