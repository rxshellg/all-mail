import "./HowItWorksSection.css";

const steps = [
  {
    icon: "bi-person-check",
    title: "Create your account",
  },
  {
    icon: "bi-link-45deg",
    title: "Connect your inboxes",
  },
  {
    icon: "bi-inboxes",
    title: "Manage everything together",
  },
];

export default function HowItWorksSection() {
  return (
    <section className="how-it-works py-5">
      <div className="container">
        <div className="how-panel rounded-4 p-4 p-lg-5">
          <div className="row align-items-center g-5">
            <div className="col-lg-5">
              <p className="text-primary fw-semibold text-uppercase small mb-3">
                How it works
              </p>

              <h2 className="display-6 fw-bold mb-4">
                It&apos;s easy to start <br />
                with{" "}
                <span className="badge rounded-pill text-bg-primary display-6 bubble">
                  AllMail
                </span>
              </h2>

              <p className="text-secondary mb-4">
                Get up and running in minutes and transform the way you
                communicte.
              </p>

              <div className="how-steps">
                {steps.map((step) => (
                  <div className="how-step-item" key={step.title}>
                    <span className="how-step-icon">
                      <i className={`bi ${step.icon}`}></i>
                    </span>

                    <span className="fw-semibold how-step-text">
                      {step.title}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
