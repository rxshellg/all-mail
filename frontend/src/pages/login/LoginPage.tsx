import "./LoginPage.css";

export default function LoginPage() {
  const handleGoogleLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  };

  return (
    <main className="login-page">
      <div className="container">
        <div className="login-shell rounded-4 overflow-hidden shadow-sm">
          <div className="row g-0">
            <section className="col-lg-6 login-intro p-4 p-lg-5">
              <a className="text-decoration-none" href="/">
                <span className="login-back-icon">
                  <i className="bi bi-arrow-left"></i>
                </span>
              </a>
              <img src="Login-Illustration.png" />
            </section>

            <section className="col-lg-6 bg-white p-4 p-lg-5 d-flex align-items-center">
              <div className="login-panel w-100 mx-auto">
                <h2 className="fw-bold mb-2">Welcome back!</h2>

                <p className="text-secondary mb-4">
                  Choose a sign-in option to access your email workspace.
                </p>

                <div className="d-grid gap-3">
                  <button
                    className="btn btn-light border provider-button"
                    onClick={handleGoogleLogin}
                  >
                    <i className="bi bi-google provider-icon google-icon"></i>
                    <span>Continue with Google</span>
                  </button>

                  <button
                    className="btn btn-light border provider-button"
                    disabled
                  >
                    <i className="bi bi-microsoft provider-icon microsoft-icon"></i>
                    <span>Continue with Outlook</span>
                  </button>

                  <button
                    className="btn btn-light border provider-button"
                    disabled
                  >
                    <i className="bi bi-apple provider-icon apple-icon"></i>
                    <span>Continue with Apple</span>
                  </button>

                  <div className="login-divider">
                    <span>or</span>
                  </div>

                  <button
                    className="btn btn-light border provider-button"
                    disabled
                  >
                    <i className="bi bi-envelope provider-icon magic-icon"></i>
                    <span>Continue with magic link</span>
                  </button>
                </div>
              </div>
            </section>
          </div>
        </div>
      </div>
    </main>
  );
}
