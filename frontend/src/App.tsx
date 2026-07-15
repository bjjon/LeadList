import './App.css'
import { Routes, Route } from 'react-router-dom'
import Dashboard from "./pages/Dashboard.tsx";
import LoginPage from "./pages/LoginPage.tsx";
import ProtectedRoute from "./components/ProtectedRoute.tsx";

function App() {

  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route element={<ProtectedRoute />}>
        <Route path="/*" element={<Dashboard />} />
      </Route>
    </Routes>
  )
}

export default App
