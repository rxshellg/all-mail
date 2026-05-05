import "./FeaturesSection.css";

const features = [
  {
    title: "All Inboxes",
    description:
      "See updates from your connected email accounts in one simple overview.",
    icon: "bi-inboxes",
    iconClass: "feature-icon-blue",
  },
  {
    title: "Easy Check-Ins",
    description: "Catch up faster without opening every account one by one.",
    icon: "bi-lightning-charge",
    iconClass: "feature-icon-yellow",
  },
  {
    title: "Clear Account Separation",
    description:
      "Stay organized while still knowing where each message belongs.",
    icon: "bi-grid",
    iconClass: "feature-icon-purple",
  },
  {
    title: "Secure Sign-In",
    description:
      "Connect your email accounts with a secure login flow you already know.",
    icon: "bi-shield-check",
    iconClass: "feature-icon-green",
  },
];

export default function FeaturesSection() {
  return (
    <section className="features py-4">
      <div className="container">
        <div className="row g-4 mb-4">
          <div className="col-lg-6">
            <div className="card border-0 rounded-4 shadow-sm h-100 text-white feature-featured-card">
              <div className="card-body p-4 p-lg-5 d-flex flex-column">
                <h2 className="fw-bold mb-0">
                  Features built for the way you actually check email
                </h2>

                <div className="mt-4">
                  <button className="btn btn-light rounded-pill px-4" disabled>
                    See all features
                    <i className="bi bi-arrow-right ms-2"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div className="col-lg-6">
            <div className="card border-0 rounded-4 shadow-sm h-100">
              <div className="card-body p-4 p-lg-5">
                <div className="feature-icon feature-icon-blue mb-4">
                  <i className="bi bi-inboxes"></i>
                </div>

                <div>
                  <h3 className="h4 fw-bold mb-3">All Inboxes</h3>
                  <p className="text-secondary mb-0">
                    Bring your connected accounts into one calm overview, so
                    checking email feels less scattered from the start.
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="row g-4">
          {features.slice(1).map((feature) => (
            <div className="col-md-6 col-lg-4" key={feature.title}>
              <div className="card border-0 rounded-4 shadow-sm h-100">
                <div className="card-body p-4 d-flex flex-column">
                  <div className={`feature-icon ${feature.iconClass} mb-4`}>
                    <i className={`bi ${feature.icon}`}></i>
                  </div>

                  <h3 className="h4 fw-bold mb-3">{feature.title}</h3>
                  <p className="text-secondary mb-0">{feature.description}</p>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
