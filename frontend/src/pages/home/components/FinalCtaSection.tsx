import "./FinalCtaSection.css";

export default function FinalCtaSection() {
  const goToLogin = () => {
    window.location.href = "/login";
  };

  return (
    <section className="final-cta py-5">
      <div className="container">
        <div className="final-cta-card rounded-4 p-4 p-lg-5 mb-4">
          <div className="row align-items-center g-4">
            <div className="col-lg-5">
              <div className="final-cta-illustration rounded-4">
                <i className="bi bi-envelope-heart"></i>
              </div>
            </div>

            <div className="col-lg-7">
              <h2 className="fw-bold mb-3">
                Ready to transform your email experience?
              </h2>

              <p className="mb-4">
                Start with Google, connect the accounts you actually use, and
                make checking email feel less scattered.
              </p>

              <button className="btn btn-dark btn-lg" onClick={goToLogin}>
                Get started
                <i className="bi bi-arrow-right ms-2"></i>
              </button>
            </div>
          </div>
        </div>

        <div className="final-access-card rounded-4 p-4 text-center">
          <h3 className="fw-bold mb-2">
            More ways to manage email are coming.
          </h3>

          <p className="text-secondary mb-4">
            AllMail is starting with Gmail inboxes, with more account types and
            email actions planned as the workspace grows.
          </p>

          <div className="d-flex justify-content-center gap-3 flex-wrap">
            <button className="btn btn-outline-dark rounded-pill px-4" disabled>
              Outlook support
              <i className="bi bi-clock ms-2"></i>
            </button>

            <button className="btn btn-outline-dark rounded-pill px-4" disabled>
              Compose & send
              <i className="bi bi-clock ms-2"></i>
            </button>
          </div>
        </div>
      </div>
    </section>
  );
}
