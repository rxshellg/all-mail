import "./SolutionsSection.css";

const solutions = [
  {
    category: "For students",
    title: "Keep school emails from getting buried.",
    description:
      "Class updates, professor replies, deadlines, and campus notices are easier to catch when they show up beside the rest of your day.",
    icon: "bi-mortarboard",
    audience: "School inboxes",
  },
  {
    category: "For job seekers",
    title: "Track opportunities without inbox roulette.",
    description:
      "Applications, recruiter replies, assessments, and follow-ups often land across different accounts. AllMail helps you keep the search visible.",
    icon: "bi-briefcase",
    audience: "Career updates",
  },
  {
    category: "For side projects",
    title: "Separate the work without losing the thread.",
    description:
      "Client messages, business updates, and project emails can stay tied to their account while still being easy to review in one place.",
    icon: "bi-kanban",
    audience: "Projects & clients",
  },
];

export default function SolutionsSection() {
  return (
    <section className="solutions py-4">
      <div className="container">
        <div className="solutions-panel rounded-4 p-4 p-lg-5">
          <h2 className="text-center text-white fw-bold mb-5">
            For every version of your{" "}
            <span className="solution-title-pill">email life</span>
          </h2>

          <div className="row g-4 justify-content-center">
            {solutions.map((solution) => (
              <div className="col-md-6 col-lg-4" key={solution.title}>
                <div className="card border-0 rounded-4 h-100 shadow-sm">
                  <div className="card-body p-4">
                    <div className="solution-illustration rounded-4 mb-4">
                      <i className={`bi ${solution.icon}`}></i>
                    </div>

                    <small className="text-secondary">
                      {solution.category}
                    </small>

                    <h3 className="h5 fw-bold mt-2 mb-4">{solution.title}</h3>

                    <div className="d-flex justify-content-between align-items-center text-secondary small">
                      <span>
                        <i className="bi bi-star-fill text-warning me-1"></i>
                        Learn more
                      </span>

                      <span>{solution.audience}</span>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </section>
  );
}