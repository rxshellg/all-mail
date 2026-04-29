import { useEffect, useState } from "react";
import { Routes, Route } from "react-router-dom";
import "./App.css";

type CurrentUser = {
  name: string;
  email: string;
  pictureUrl: string;
};

type GmailMessage = {
  id: string;
  threadId: string;
  from: string;
  subject: string;
  snippet: string;
  receivedAt: string;
  unread: boolean;
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
  const [user] = useCurrentUser();
  const [messages, setMessages] = useState<GmailMessage[]>([]);
  const [messagesLoading, setMessagesLoading] = useState(true);

  const handleLogout = () => {
    window.location.href = "http://localhost:8080/logout";
  };

  useEffect(() => {
    fetch("http://localhost:8080/api/gmail/messages", {
      credentials: "include",
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Could not load Gmail messages");
        }

        return response.json();
      })
      .then((data) => setMessages(data))
      .catch((error) => console.error(error))
      .finally(() => setMessagesLoading(false));
  }, []);

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
      <section className="messages-card">
        <h2>Inbox</h2>

        {messagesLoading ? (
          <p>Loading messages...</p>
        ) : messages.length === 0 ? (
          <p>No messages found.</p>
        ) : (
          <ul className="message-list">
            {messages.map((message) => (
              <li
                key={message.id}
                className={message.unread ? "message unread" : "message"}
              >
                <div>
                  <strong>{message.from}</strong>
                  <p>{message.subject || "(No subject)"}</p>
                  <span>{message.snippet}</span>
                </div>

                <small>{message.receivedAt}</small>
              </li>
            ))}
          </ul>
        )}
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
