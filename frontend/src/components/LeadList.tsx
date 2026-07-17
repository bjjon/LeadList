import "./LeadList.css";
import type { Lead } from "../types/Lead.ts";
import LeadRow from "./LeadRow.tsx";

type LeadListProps = {
  leads: Lead[];
}

export default function LeadList({ leads }: Readonly<LeadListProps>) {


  return (
    <div className="leads-list fade-in">
      <div id="leadList">
        {leads.length === 0 ? (
          <p
            style={{
              color: "var(--text-muted)",
              padding: "1rem 0",
              fontSize: "0.875rem",
            }}
          >
            Keine Leads gefunden.
          </p>
        ) : (
          leads.map((lead) => (
            <LeadRow key={lead.id} lead={lead} />
          ))
        )}
      </div>
    </div>
  )
}