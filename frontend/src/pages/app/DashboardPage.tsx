import Shell from "./components/Shell";
import "./DashboardPage.css";

export default function DashboardPage() {
  return (
    <Shell>
      <div className="dashboard-header mb-4">
        <p className="text-primary fw-semibold text-uppercase small mb-2">
          Dashboard
        </p>
        <h1 className="fw-bold mb-2">Welcome to your AllMail workspace.</h1>
      </div>
    </Shell>
  );
}
