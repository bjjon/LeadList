import './LoginPage.css';

export default function LoginPage() {
  return (
    <div className="login-screen">
      <div className="login-card">
        <div className="login-brand">
          <div className="login-logo">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                    d="M3 5a2 2 0 012-2h2.28a1 1 0 01.95.68l1.5 4.5a1 1 0 01-.5 1.21l-2.26 1.13a11 11 0 005.52 5.52l1.13-2.26a1 1 0 011.21-.5l4.5 1.5a1 1 0 01.68.95V19a2 2 0 01-2 2h-1C9.72 21 3 14.28 3 6V5z" />
            </svg>
          </div>
          <h1 className="login-title">Telefonliste</h1>
          <p className="login-subtitle">Anmelden um fortzufahren</p>
        </div>

        <form className="login-form" noValidate>
          <div className="login-field">
            <label htmlFor="login-email">E-Mail</label>
            <input
              id="login-email"
              type="text"
              autoComplete="email"
              autoFocus
              placeholder="z. B. max@example.com"
            />
          </div>

          <div className="login-field">
            <label htmlFor="login-password">Passwort</label>
            <div className="login-pw-wrap">
              <input
                id="login-password"
                autoComplete="current-password"
                placeholder="••••••••"
              />
              <button
                type="button"
                className="login-pw-toggle"

              >
                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                        d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                        d="M2.458 12C3.732 7.943 7.523 5 12 5c4.477 0 8.268 2.943 9.542 7-1.274 4.057-5.065 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
              </button>
            </div>
          </div>

          <button type="submit" className="login-btn">
            Anmelden
          </button>
        </form>
      </div>
    </div>
  )
}