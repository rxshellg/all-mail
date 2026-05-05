import "./HeroSection.css";

export default function HeroSection() {
  const goToLogin = () => {
    window.location.href = "/login";
  };

  return (
    <section className="hero d-flex align-items-center">
      <div className="container">
        <div className="row align-items-center g-5">
          <div className="col-lg-5">
            <p className="text-primary fw-semibold text-uppercase small mb-3">
              Centralized inbox dashboard
            </p>

            <h1 className="display-4 fw-bold lh-1 mb-4">
              Your email life, finally{" "}
              <span className="text-primary">unified.</span>
            </h1>

            <p className="lead text-secondary mb-4">
              AllMail brings your email accounts into one calm dashboard, so you
              can keep up with everything without bouncing between tabs, logins,
              and inboxes.
            </p>

            <div className="d-flex flex-wrap gap-3 hero-actions">
              <button className="btn btn-primary btn-lg" onClick={goToLogin}>
                <i className="bi bi-envelope me-2"></i>
                Get started
              </button>

              <button className="btn btn-outline-secondary btn-lg" disabled>
                <i className="bi bi-play-fill me-2"></i>
                View demo
              </button>
            </div>

            <div className="d-flex flex-wrap gap-1 text-secondary small fw-semibold hero-points">
              <span className="d-inline-flex align-items-center gap-1">
                <span className="hero-point-icon">
                  <i className="bi bi-shield-check"></i>
                </span>
                Secure & reliable
              </span>

              <span className="d-inline-flex align-items-center gap-1">
                <span className="hero-point-icon">
                  <i className="bi bi-check2-circle"></i>
                </span>
                Easy to set up
              </span>

              <span className="d-inline-flex align-items-center gap-1">
                <span className="hero-point-icon">
                  <i className="bi bi-stars"></i>
                </span>
                Loved by multitaskers
              </span>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
