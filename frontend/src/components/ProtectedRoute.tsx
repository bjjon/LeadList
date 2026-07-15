import { Navigate, Outlet } from 'react-router-dom';
import { useIsAuthenticated } from "../store/authStore.ts";

export default function ProtectedRoute() {
  const isAuthenticated = useIsAuthenticated();

  if (!isAuthenticated) {
    return <Navigate to="/login" />;
  }

  return <Outlet />;
}