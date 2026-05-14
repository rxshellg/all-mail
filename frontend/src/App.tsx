import { Routes, Route } from "react-router-dom";
import HomePage from "./pages/home/HomePage";
import LoginPage from "./pages/login/LoginPage";
import DashboardPage from "./pages/app/DashboardPage";
import InboxPage from "./pages/app/InboxPage";
import AccountInboxPage from "./pages/app/AccountInboxPage";

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/dashboard" element={<DashboardPage />} />
      <Route path="/inbox" element={<InboxPage />} />
      <Route path="/accounts/:accountId/inbox" element={<AccountInboxPage />} />
    </Routes>
  );
}
