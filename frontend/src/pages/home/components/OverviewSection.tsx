import "./OverviewSection.css";

const metrics = [
  {
    icon: "bi-envelope-check",
    iconClass: "metric-icon-blue",
    value: "2+",
    label: "Accounts in one view",
  },
  {
    icon: "bi-shield-lock",
    iconClass: "metric-icon-green",
    value: "OAuth",
    label: "Secure Google login",
  },
  {
    icon: "bi-inboxes",
    iconClass: "metric-icon-yellow",
    value: "1",
    label: "Unified inbox",
  },
  {
    icon: "bi-lightning-charge",
    iconClass: "metric-icon-coral",
    value: "Live",
    label: "Gmail message preview",
  },
];

export default function OverviewSection() {
  return (
    <section className="overview py-4">
      <div className="container">
        <div className="card border-0 shadow-sm rounded-4 mb-4">
          <div className="card-body py-4">
            <div className="row text-center px-5">
              {metrics.map((metric) => (
                <div className="col-6 col-lg-3" key={metric.label}>
                  <div className="d-flex align-items-center justify-content-center gap-4">
                    <i
                      className={`bi ${metric.icon} fs-2 ${metric.iconClass}`}
                    ></i>

                    <div className="text-start">
                      <p className="h4 fw-bold mb-0">{metric.value}</p>
                      <small className="text-secondary">{metric.label}</small>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        <div className="card border-0 shadow-sm rounded-4 overflow-hidden overview-panel">
          <div className="card-body p-4 p-lg-5">
            <div className="row align-items-center g-5 ">
              <div className="col-lg-6">
                <p className="text-primary fw-semibold text-uppercase small mb-3">
                  Made for real-life inbox chaos
                </p>

                <h2 className="display-6 fw-bold mb-4">
                  One check-in for all the inboxes you{" "}
                  <span className="badge rounded-pill text-bg-primary display-6 bubble">
                    actually
                  </span>{" "}
                  use
                </h2>

                <p className="lead text-secondary mb-0">
                  When every account has a different purpose, important messages
                  can get buried simply because you forgot to check the right
                  inbox. AllMail gives you one place to catch up, sort through
                  updates, and keep your day moving.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
