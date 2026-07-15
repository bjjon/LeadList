import './Sidebar.css';
import { useAuthStore } from "../store/authStore.ts";
import { logout as logoutRequest } from "../api/auth.ts";
import { NavLink } from "react-router-dom";

export default function Sidebar() {
  const toggleSidebar = () => {
    const sidebar = document.getElementById("sidebar")!;
    sidebar.classList.toggle("collapsed");
  };

  const { user, logout } = useAuthStore();

  async function handleLogout() {
    try {
      await logoutRequest();
    } finally {
      logout();
    }
  }

  return (
    <aside id="sidebar">
      <div className="sidebar-header">
        <button className="sidebar-logo" onClick={toggleSidebar}>
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth="2"
              d="M3 5a2 2 0 012-2h2.28a1 1 0 01.95.68l1.5 4.5a1 1 0 01-.5 1.21l-2.26 1.13a11 11 0 005.52 5.52l1.13-2.26a1 1 0 011.21-.5l4.5 1.5a1 1 0 01.68.95V19a2 2 0 01-2 2h-1C9.72 21 3 14.28 3 6V5z"
            />
          </svg>
        </button>
        <div className="sb-text">
          <div className="sidebar-title">Telefonliste</div>
          <div id="statsLabel">0 von 0 aktiv</div>
        </div>
        <button
          id="sidebarToggle"
          onClick={toggleSidebar}
          title="Sidebar ein-/ausklappen"
          aria-label="Sidebar ein-/ausklappen"
        >
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth="2"
              d="M11 19l-7-7 7-7m8 14l-7-7 7-7"
            />
          </svg>
        </button>
      </div>

      <nav className="sidebar-nav">
        <div className="nav-section">
          <span className="sb-text">Übersicht</span>
        </div>
        <NavLink
          to="/"
          end
          className={({isActive}) => `nav-item${isActive ? " active" : ""}`}
          title="Leads"
        >
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth="2"
              d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"
            />
          </svg>
          <span className="sb-text">Leads</span>
        </NavLink>
        <div className="nav-section">
          <span className="sb-text">Hinzufügen</span>
        </div>
        <NavLink
          to="/add"
          className={({ isActive }) => `nav-item${isActive ? " active" : ""}`}
          title="Neuer Lead"
        >
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth="2.5"
              d="M12 4v16m8-8H4"
            />
          </svg>
          <span className="sb-text">Neuer Lead</span>
        </NavLink>
        <NavLink to="/csv" className="nav-item" title="CSV Import">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth="2"
              d="M4 16v2a2 2 0 002 2h12a2 2 0 002-2v-2M12 12V4m0 8l-3-3m3 3l3-3"
            />
          </svg>
          <span className="sb-text">CSV Import</span>
        </NavLink>
      </nav>

      <div className="sidebar-user">
        <button id="logoutBtn" title="Abmelden" aria-label="Abmelden" onClick={handleLogout}>
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth="2"
              d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"
            />
          </svg>
          <span className="sb-text">Abmelden</span>
        </button>
        <div className="user-chip">
          <div className="avatar" id="userAvatar">
            {user?.firstname.charAt(0)}{user?.lastname.charAt(0)}
          </div>
          <span id="userLabel" className="sb-text">
              {user?.firstname} {user?.lastname}
          </span>
        </div>
      </div>
    </aside>
  )
}