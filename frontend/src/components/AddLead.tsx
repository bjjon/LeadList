import "./AddLead.css";

export default function AddLead() {
  return (
    <div id="page-add" className="page-inner">
      <div className="card fade-in" style={{ maxWidth: "36rem" }}>
        <div className="card-head">
          <h3>Lead-Daten eingeben</h3>
          <p>Pflichtfelder sind mit * markiert</p>
        </div>
        <form id="addForm" className="card-body">
          <div className="field-grid">
            <div className="field" style={{ margin: "0" }}>
              <label htmlFor="newFirstname">Vorname *</label>
              <input
                type="text"
                id="newFirstname"
                placeholder="Max"
              />
            </div>
            <div className="field" style={{ margin: "0" }}>
              <label htmlFor="newLastname">Nachname *</label>
              <input
                type="text"
                id="newLastname"
                placeholder="Mustermann"
              />
            </div>
            <div className="field" style={{ margin: "0" }}>
              <label htmlFor="newPhone">Telefon *</label>
              <input
                type="tel"
                id="newPhone"
                placeholder="+49 30 123456"
              />
            </div>
            <div className="field" style={{ margin: "0" }}>
              <label htmlFor="newCompany">Firma</label>
              <input type="text" id="newCompany" placeholder="Muster GmbH" />
            </div>
            <div className="field" style={{ margin: "0" }}>
              <label htmlFor="newEmail">E-Mail</label>
              <input type="email" id="newEmail" placeholder="max@muster.de" />
            </div>
          </div>
          <div className="field" style={{ marginTop: "1.25rem" }}>
            <label htmlFor="newNote">Notiz</label>
            <textarea
              id="newNote"
              placeholder="Optionale Notiz …"
            ></textarea>
          </div>
          <div id="addSuccess" className="hidden">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="2"
                d="M5 13l4 4L19 7"
              />
            </svg>
            Lead wurde erfolgreich angelegt.
          </div>
          <div className="btn-row">
            <button type="submit" className="btn-accent">
              <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="2.5"
                  d="M12 4v16m8-8H4"
                />
              </svg>
              Lead anlegen
            </button>
            <button
              type="button"
              className="btn-ghost"
            >
              Abbrechen
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}