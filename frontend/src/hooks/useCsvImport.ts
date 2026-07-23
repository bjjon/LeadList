import type { Lead, LeadRequest } from "../types/Lead.ts";
import { api } from "../api/axiosInstance.ts";
import { useState } from "react";
import { useLeads } from "../context/LeadContext.tsx";

export type CsvRowResult =
  | { status: "sending"; leadRequest: LeadRequest }
  | { status: "success"; lead: Lead }
  | { status: "error"; leadRequest: LeadRequest };

const LEAD_FIELDS: (keyof LeadRequest)[] = ["firstname", "lastname", "company", "phone", "email"];

function detectDelimiter(headerLine: string): "," | ";" {
  return headerLine.includes(";") ? ";" : ",";
}

function isLeadField(value: string): value is keyof LeadRequest {
  return (LEAD_FIELDS as string[]).includes(value);
}

function replaceResultAt(results: CsvRowResult[], index: number, result: CsvRowResult): CsvRowResult[] {
  return results.map((entry, i) => (i === index ? result : entry));
}

function useCsvImport() {
  const { getLeads } = useLeads();
  const [leadResponse, setLeadResponse] = useState<CsvRowResult[]>([]);

  async function selectFile(file: File | null): Promise<void> {

    if (!file) return;

    const text = await file.text();
    await parseCsv(text);
  }

  async function parseCsv(text: string): Promise<void> {
    const [headerLine, ...dataLines] = text.split(/\r?\n/).filter((line) => line.trim().length > 0);
    const delimiter = detectDelimiter(headerLine);
    const headers = headerLine.split(delimiter).map((header) => header.trim());

    const leadRequests: LeadRequest[] = dataLines.map((line) => {
      const values = line.split(delimiter).map((value) => value.trim());
      const lead: LeadRequest = { firstname: "", lastname: "", company: "", phone: "", email: "", note: "" };

      headers.forEach((header, i) => {
        if (isLeadField(header)) {
          lead[header] = values[i] ?? "";
        }
      });

      return lead;
    });

    setLeadResponse(leadRequests.map((leadRequest) => ({ status: "sending", leadRequest })));

    await Promise.all(leadRequests.map(async (leadRequest, index) => {
      const result = await sendLeadRequest(leadRequest);
      setLeadResponse((prev) => replaceResultAt(prev, index, result));
    }));

    await getLeads();
  }

  async function sendLeadRequest(leadRequest: LeadRequest): Promise<CsvRowResult> {
    try {
      const { data } = await api.post<Lead>('leads/add', leadRequest);
      return { status: "success", lead: data };
    } catch (error) {
      console.error(error);
      return { status: "error", leadRequest };
    }
  }

  return { selectFile, leadResponse };
}

export default useCsvImport
