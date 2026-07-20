import "./LeadRow.css";
import type { Lead } from "../types/Lead.ts";
import { useAuthStore } from "../store/authStore.ts";

type LeadProps = {
  lead: Lead;
  onOpenDetail: (lead: Lead) => void;
};

export default function LeadRow({ lead, onOpenDetail }: Readonly<LeadProps>) {
  const { user } = useAuthStore();
  const isMine = lead.assignedTo?.id === user?.id;

  return (
    <div className="lead-row" onMouseDown={() => onOpenDetail(lead)}>
      <div className="lead-avatar">
        {lead.firstname.charAt(0) + lead.lastname.charAt(0)}
      </div>
      <div className="lead-info">
        <div className="lead-name-row">
          <span className="lead-name">
              {lead.firstname} {lead.lastname}
          </span>
          <span className="lead-company">· {lead.company}</span>
        </div>
        <div className="lead-phone-row">
          <span className="lead-phone">{lead.phone}</span>
          {lead.status.value === "ERREICHT" && (
            <span className="lead-reached-ok">✓ erreicht</span>
          )}
          {lead.status.value === "NICHT_ERREICHT" && (
            <span className="lead-reached-no">✕ nicht erreicht</span>
          )}
        </div>
      </div>
      <div className="lead-meta">
        <div className="status-pill">
          <span
            className="status-dot"
            style={{
              background: lead.status?.color,
            }}
          ></span>
          {lead.status?.label}
        </div>
        {isMine ? (
          <div className="lead-mine">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                    d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
            </svg>
            Ich
          </div>
        ) : lead.assignedTo && (
          <div className="lead-lock">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                    d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
            </svg>
            {lead.assignedTo.firstname} {lead.assignedTo.lastname}
          </div>
        )}
      </div>
    </div>
  )
}