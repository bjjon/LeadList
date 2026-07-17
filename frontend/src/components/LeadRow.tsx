import "./LeadRow.css";
import type { Lead } from "../types/Lead.ts";

type LeadProps = {
  lead: Lead;
};

export default function LeadRow({ lead }: Readonly<LeadProps>) {
  return (
    <div className="lead-row">
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
      </div>
    </div>
  )
}