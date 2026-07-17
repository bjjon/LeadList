import "./LeadList.css";
import type { Lead } from "../types/Lead.ts";
import LeadRow from "./LeadRow.tsx";
import LeadDetail from "./LeadDetail.tsx";
import { useCallback, useState } from "react";

type LeadListProps = {
  leads: Lead[];
}

export default function LeadList({ leads }: Readonly<LeadListProps>) {
  const [isDetailOpen, setIsDetailOpen] = useState<boolean>(false);
  const [selectedLead, setSelectedLead] = useState<Lead | null>(null);

  const openDetail = useCallback((lead: Lead) => {
    setSelectedLead(lead);
    setIsDetailOpen(true);
  }, []);

  const closeDetail = useCallback(() => {
    setIsDetailOpen(false);
  }, []);

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
            <LeadRow key={lead.id} lead={lead} onOpenDetail={openDetail} />
          ))
        )}
      </div>
      <LeadDetail
        lead={selectedLead}
        isOpen={isDetailOpen}
        onClose={closeDetail}
      />
    </div>
  )
}