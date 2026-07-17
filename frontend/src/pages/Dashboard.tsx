import Header from "../components/Header.tsx";
import Sidebar from "../components/Sidebar.tsx";
import { useLeads } from "../context/LeadContext.tsx";
import LeadList from "../components/LeadList.tsx";
import { Route, Routes } from "react-router-dom";
import SearchBar from "../components/SearchBar.tsx";

export default function Dashboard() {
  const { leads } = useLeads();

  return (
    <div className="app-screen">
      <Sidebar />
      <div className="main-wrap">
        <Header />
        <Routes>
          <Route index element={
            <>
              <SearchBar />
              <LeadList leads={leads} />
            </>
          } />
        </Routes>
      </div>
    </div>
  )
}