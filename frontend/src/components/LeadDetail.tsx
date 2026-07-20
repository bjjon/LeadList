import "./LeadDetail.css";
import type { Lead } from "../types/Lead.ts";
import { useAuthStore } from "../store/authStore.ts";
import { useLeads } from "../context/LeadContext.tsx";
import { useEffect, useState } from "react";
import formatInstantTime from "../utils/formatToLocalTime.ts";

type LeadDetailProps = {
  lead: Lead | null;
  isOpen: boolean;
  onClose: () => void;
};

export default function LeadDetail({ lead, isOpen, onClose }: Readonly<LeadDetailProps>) {
  const [note, setNote] = useState<string>("");

  const { user } = useAuthStore();
  const { logs, getCallLogs, assign, unassign, logCall } = useLeads();

  const isMine = lead?.assignedTo?.id === user?.id;
  const isLockedByOther = lead?.assignedTo && !isMine;

  useEffect(() => {
    if (!lead) return
    getCallLogs(lead.id);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [lead?.id]);

  const handleUnassign = async (id: string) => {
    await unassign(id);
  }

  const handleAssign = async (id: string) => {
    await assign(id);
  }

  const handleLogCall = async (id: string, result: string) => {
    await logCall(id, { result, notes: note });
  }

  function isMineOrOpenClass() {
    if (isMine) {
      return " assign-block--me";
    }

    return isLockedByOther ? " assign-block--locked" : ""
  }

  function isBlockedBy() {
    if (isLockedByOther) {
      return (
        <>
          <svg className="assign-icon assign-icon--lock" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                  d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
          </svg>
          <div>
            <div className="assign-label">Gesperrt</div>
            <div className="assign-sub">Bearbeitet von <span>{lead?.assignedTo?.firstname} {lead?.assignedTo?.lastname}</span></div>
          </div>
        </>
      )
    }

    if (isMine) {
      return (
        <>
          <svg className="assign-icon assign-icon--me" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                  d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
          </svg>
          <div>
            <div className="assign-label">Zugewiesen</div>
            <div className="assign-sub">Von mir übernommen</div>
          </div>
        </>
      )
    }

    return (
      <>
        <svg className="assign-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                d="M12 4v16m8-8H4" />
        </svg>
        <div>
          <div className="assign-label">Nicht zugewiesen</div>
          <div className="assign-sub">Noch von niemandem übernommen</div>
        </div>
      </>
    )
  }

  return (
    <>
      <div id="detailBackdrop" className={isOpen ? "open" : ""} onMouseDown={onClose} />
      <div id="detailPanel" className={isOpen ? "open" : ""}>
        {lead && (
          <div key={lead.id}>
            <div className="panel-head">
              <div>
                <p id="modalSubtitle">{lead.company}</p>
                <h2 id="modalTitle">
                  {lead.firstname} {lead.lastname}
                </h2>
              </div>
              <button className="panel-close" onClick={onClose} aria-label="Schließen">
                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                        d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            <div id="modalBody">

              {/* ── Block 1: Lead-Status ── */}
              <div className="status-block status-block-neutral">
                <div className="status-block-inner">
                  <span className="status-dot" style={{ background: lead.status.color }} />
                  <div className="status-name">{lead.status.label}</div>
                </div>
              </div>

              {/* ── Block 2: Zuweisung / Sperre ── */}
              <div className={`assign-block${isMineOrOpenClass()}`}>
                <div className="assign-block-inner">
                  {isBlockedBy()}
                </div>

                {!isLockedByOther && (
                  isMine ? (
                    <button className="btn-release" onClick={() => handleUnassign(lead.id)}>
                      Freigeben
                    </button>
                  ) : (
                    <button className="btn-claim" onClick={() => handleAssign(lead.id)}>
                      Mir zuweisen
                    </button>
                  )
                )}
              </div>

              {/* ── Kontakt ── */}
              <div className="contact-grid">
                <div>
                  <p className="contact-label">Telefon</p>
                  {lead.phone
                    ? <a href={`tel:${lead.phone}`} className="contact-val">{lead.phone}</a>
                    : <span className="contact-empty">–</span>}
                </div>
                <div>
                  <p className="contact-label">E-Mail</p>
                  {lead.email
                    ? <a href={`mailto:${lead.email}`} className="contact-val">{lead.email}</a>
                    : <span className="contact-empty">–</span>}
                </div>
              </div>

              {/* ── Notiz ── */}
              <div>
                <p className="note-label">Notiz</p>
                <textarea
                  className="note-input"
                  disabled={!isMine}
                  rows={3}
                  value={note}
                  onChange={(e) => setNote(e.target.value)}
                  placeholder={isMine ? "Notiz eingeben …" : ""}
                />
              </div>

              {/* ── Erreichbarkeit ── */}
              <div>
                <p className="reach-label">Erreichbarkeit</p>
                <div className="reach-btns">
                  <button
                    className={`reach-btn${lead.status.value === "REACHED" ? " active-ok" : ""}`}
                    disabled={!isMine}
                    onClick={() => handleLogCall(lead.id, "REACHED")}
                  >
                    ✓ Erreicht
                  </button>
                  <button
                    className={`reach-btn reach-no${lead.status.value === "NOT_REACHED" ? " active-no" : ""}`}
                    disabled={!isMine}
                    onClick={() => handleLogCall(lead.id, "NOT_REACHED")}
                  >
                    ✕ Nicht erreicht
                  </button>
                </div>
              </div>

              {/* ── Verlauf ── */}
              <div className="history-section">
                <p className="history-label">Verlauf</p>
                {logs.length === 0 ? (
                  <p className="history-empty">Noch kein Verlauf vorhanden.</p>
                ) : (
                  <ul className="history-list">
                    {logs.map((log) => (
                      <li key={`${log.userId}-${log.calledAt}`} className="history-item">
                        <span className="history-time">{formatInstantTime(log.calledAt)}</span>
                        <span>
                          {log.result === "REACHED" ? "Als erreicht markiert." : "Als nicht erreicht markiert."}
                          {log.notes ? ` „${log.notes}“` : ""}
                        </span>
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </>
  )
}