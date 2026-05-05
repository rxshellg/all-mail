export default function LoginPage() {
  const handleGoogleLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  };

  return (
    <main className="page">
      <section className="card">
        <h1>Log in to AllMail</h1>
        <button onClick={handleGoogleLogin}>Login with Google</button>
      </section>
    </main>
  );
}
