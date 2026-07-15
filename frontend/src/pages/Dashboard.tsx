import Header from "../components/Header.tsx";
import Sidebar from "../components/Sidebar.tsx";

export default function Dashboard() {
  return (
    <div className="app-screen">
      <Sidebar />
      <div className="main-wrap">
        <Header />
      </div>
    </div>
  )
}