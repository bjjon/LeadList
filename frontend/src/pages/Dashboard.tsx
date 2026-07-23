import Header from "../components/Header.tsx";
import Sidebar from "../components/Sidebar.tsx";
import { useLeads } from "../context/LeadContext.tsx";
import LeadList from "../components/LeadList.tsx";
import { Route, Routes } from "react-router-dom";
import SearchBar from "../components/SearchBar.tsx";
import { useEffect, useMemo, useState } from "react";
import AddLead from "../components/AddLead.tsx";
import CsvImport from "../components/CsvImport.tsx";
import useStatusFilter from "../hooks/useStatusFilter.ts";

export default function Dashboard() {
  const { leads, getLeads } = useLeads();
  const [query, setQuery] = useState("");
  const { statusFilters, availableStatus, toggleStatus } = useStatusFilter(query);

  useEffect(() => {
    void getLeads();
  }, [getLeads]);

  const filteredLeads = useMemo(() => {
    const q = query.trim().toLowerCase();
    return leads.filter((lead) => {
      if (statusFilters.length > 0 && !statusFilters.some((s) => s.value === lead.status.value)) return false;
      if (!q) return true;
      return (
        lead.firstname.toLowerCase().includes(q) ||
        lead.lastname.toLowerCase().includes(q) ||
        lead.company.toLowerCase().includes(q) ||
        lead.phone.includes(q) ||
        lead.email.toLowerCase().includes(q)
      );
    });
  }, [leads, query, statusFilters]);

  return (
    <div className="app-screen">
      <Sidebar />
      <div className="main-wrap">
        <Header />
        <Routes>
          <Route index element={
            <>
              <SearchBar
                query={query}
                onQueryChange={setQuery}
                availableStatus={availableStatus}
                statusFilters={statusFilters}
                toggleStatus={toggleStatus}
              />
              <LeadList leads={filteredLeads} />
            </>
          } />
          <Route path={"/add"} element={<AddLead />} />
          <Route path={"/csv"} element={<CsvImport />} />
        </Routes>
      </div>
    </div>
  )
}