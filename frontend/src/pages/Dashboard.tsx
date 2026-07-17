import Header from "../components/Header.tsx";
import Sidebar from "../components/Sidebar.tsx";
import { useLeads } from "../context/LeadContext.tsx";
import LeadList from "../components/LeadList.tsx";
import { Route, Routes } from "react-router-dom";
import SearchBar from "../components/SearchBar.tsx";
import {useEffect, useMemo, useState} from "react";

export default function Dashboard() {
  const { leads, getLeads } = useLeads();
  const [query, setQuery] = useState("");

  useEffect(() => {
    getLeads();
  }, []);

  const filteredLeads = useMemo(() => {
    const q = query.trim().toLowerCase();
    return leads.filter((lead) => {
      if (!q) return true;
      return (
        lead.firstname.toLowerCase().includes(q) ||
        lead.lastname.toLowerCase().includes(q) ||
        lead.company.toLowerCase().includes(q) ||
        lead.phone.includes(q) ||
        lead.email.toLowerCase().includes(q)
      );
    });
  }, [leads, query]);

  return (
    <div className="app-screen">
      <Sidebar />
      <div className="main-wrap">
        <Header />
        <Routes>
          <Route index element={
            <>
              <SearchBar query={query} onQueryChange={setQuery} />
              <LeadList leads={filteredLeads} />
            </>
          } />
        </Routes>
      </div>
    </div>
  )
}