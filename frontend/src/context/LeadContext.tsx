import type { Lead } from "../types/Lead.ts";
import {createContext, type ReactNode, useContext, useEffect, useState} from "react";
import { api } from "../api/axiosInstance.ts";

interface LeadContextType {
  leads: Lead[],
  getLeads: () => Promise<void>
}

const LeadContext = createContext<LeadContextType | null>(null);

function LeadProvider({ children }: Readonly<{ children: ReactNode }>) {
  const [leads, setLeads] = useState<Lead[]>([]);

  const getLeads = async () => {
    try {
      const { data } = await api.get<Lead[]>("/leads");
      setLeads(data);
    } catch (error) {
      console.error(error);
    }
  }

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    getLeads().catch((err) => console.error(err));
  }, []);

  return (
    <LeadContext.Provider value={{ leads, getLeads }} >
      {children}
    </LeadContext.Provider>
  )
}

function useLeads() {
  const context = useContext(LeadContext);
  if (!context) throw new Error("useLeads must be used within LeadProvider");
  return context;
}

// eslint-disable-next-line react-refresh/only-export-components
export { LeadProvider, useLeads };