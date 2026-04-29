import { useEffect, useState } from "react";
import { Routes, Route } from "react-router-dom";
import "./App.css";

type CurrentUser = {
  name: string;
  email: string;
  pictureUrl: string;
};

function Home() {
  const handleLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  };

  return (
    <main className="page">
      <section className="card">
        <h1>AllMail</h1>
        <p>
          A centralized inbox dashboard for managing messages across multiple
          email accounts.
        </p>

        <button onClick={handleLogin}>Login with Google</button>
      </section>
    </main>
  );
}

function Dashboard() {
  const [user, setUser] = useCurrentUser();

  const handleLogout = () => {
    window.location.href = "http://localhost:8080/logout";
  };

  if (!user) {
    return (
      <main className="page">
        <section className="card">
          <p>Loading your account...</p>
        </section>
      </main>
    );
  }

  return (
    <main className="page">
      <section className="card">
        {user.pictureUrl ? (
          <img
            src={user.pictureUrl}
            alt={`${user.name}'s profile`}
            className="avatar"
            referrerPolicy="no-referrer"
          />
        ) : (
          <div className="avatar avatar-placeholder">
            {user.name.charAt(0).toUpperCase()}
          </div>
        )}

        <h1>Welcome, {user.name}</h1>
        <p>You are logged into AllMail.</p>
        <p>{user.email}</p>

        <button onClick={handleLogout}>Logout</button>
      </section>
    </main>
  );
}

function useCurrentUser(): [
  CurrentUser | null,
  (user: CurrentUser | null) => void,
] {
  const [user, setUser] = useState<CurrentUser | null>(null);

  useEffect(() => {
    fetch("http://localhost:8080/api/auth/me", {
      credentials: "include",
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("User is not authenticated");
        }

        return response.json();
      })
      .then((data) => setUser(data))
      .catch(() => {
        window.location.href = "/";
      });
  }, []);

  return [user, setUser];
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/dashboard" element={<Dashboard />} />
    </Routes>
  );
}
