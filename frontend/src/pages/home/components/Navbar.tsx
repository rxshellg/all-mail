export default function Navbar() {
  const goToLogin = () => {
    window.location.href = "/login";
  };

  return (
    <nav className="navbar sticky-top navbar-expand-lg bg-white">
      <div className="container-fluid">
        <a className="navbar-brand" href="/">
          <span>AllMail</span>
        </a>

        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#homeNavbarContent"
          aria-controls="homeNavbarContent"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>

        <div className="collapse navbar-collapse" id="homeNavbarContent">
          <ul className="navbar-nav ms-lg-5 me-auto gap-4">
            <li className="nav-item">
              <button className="nav-link disabled" disabled>
                Features
              </button>
            </li>
            <li className="nav-item">
              <button className="nav-link disabled" disabled>
                How it works
              </button>
            </li>
            <li className="nav-item">
              <button className="nav-link disabled" disabled>
                Security
              </button>
            </li>
            <li className="nav-item">
              <button className="nav-link disabled" disabled>
                FAQ
              </button>
            </li>
          </ul>

          <div className="d-flex gap-2">
            <button className="btn btn-outline-primary" onClick={goToLogin}>
              Log in
            </button>
            <button className="btn btn-primary" onClick={goToLogin}>
              Get Started
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
}
