import type { Lead } from "../types/Lead.ts";
import {createContext, type ReactNode, useContext, useState} from "react";
import { api } from "../api/axiosInstance.ts";
import type { CallLog } from "../types/CallLog.ts";

interface LeadContextType {
  leads: Lead[],
  getLeads: () => Promise<void>,
  logs: CallLog[],
  getCallLogs: (id: string) => Promise<void>,
}

const LeadContext = createContext<LeadContextType | null>(null);

function LeadProvider({ children }: Readonly<{ children: ReactNode }>) {
  const [leads, setLeads] = useState<Lead[]>([]);
  const [logs, setLogs] = useState<CallLog[]>([]);

  const getLeads = async () => {
    try {
      const { data } = await api.get<Lead[]>("/leads");
      setLeads(data);
    } catch (error) {
      console.error(error);
    }
  }

  const getCallLogs = async (id: string) => {
    try {
      const { data } = await api.get<CallLog[]>(`/leads/${id}/call-logs`);
      setLogs(data);
    } catch (error) {
      console.error(error);
    }
  }

  return (
    <LeadContext.Provider value={{ leads, getLeads, logs, getCallLogs }} >
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