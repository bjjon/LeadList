import type { Lead } from "../types/Lead.ts";
import {createContext, type ReactNode, useContext, useState} from "react";
import { api } from "../api/axiosInstance.ts";
import type { CallLog } from "../types/CallLog.ts";
import type { LeadFormValues } from "../schemas/leadSchema.ts";

interface LeadContextType {
  leads: Lead[],
  getLeads: () => Promise<void>,
  logs: CallLog[],
  getCallLogs: (id: string) => Promise<void>,
  assign: (id: string) => Promise<void>;
  unassign: (id: string) => Promise<void>;
  logCall: (id: string, payload: { result: string, notes: string }) => Promise<void>;
  addLead: (payload: LeadFormValues) => Promise<void>;
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

  const assign = async (id: string) => {
    try {
      const { data } = await api.put<Lead>(`/leads/${id}/assign`);
      setLeads(prev => prev.map(lead => lead.id === id ? data : lead));
    } catch (error) {
      console.error(error);
    }
  }

  const unassign = async (id: string) => {
    try {
      const { data } = await api.put<Lead>(`/leads/${id}/unassign`);
      setLeads(prev => prev.map(lead => lead.id === id ? data : lead));
    } catch (error) {
      console.error(error);
    }
  }

  const logCall = async (id: string, payload: { result: string, notes: string }) => {
    try {
      const { data } =  await api.post<Lead>(`/leads/${id}/call-logs`, payload);
      setLeads(prev => prev.map(lead => lead.id === id ? data : lead));
      await getCallLogs(id);
    } catch (error) {
      console.error(error);
    }
  }

  const addLead = async (payload: LeadFormValues) => {
    try {
      const { data } =  await api.post<Lead>('leads/add', payload);
      setLeads(prev => [data, ...prev]);
    } catch (error) {
      console.error(error);
    }
  }


  return (
    <LeadContext.Provider value={{ leads, getLeads, logs, getCallLogs, assign, unassign, logCall, addLead }} >
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