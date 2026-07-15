import { create } from 'zustand';
import type { AuthUser } from '../api/auth.ts';

interface AuthState {
  token: string | null;
  user: AuthUser | null;
  setAuth: (token: string, user: AuthUser) => void;
  logout: () => void;
}

const TOKEN_KEY = 'auth-token';
const USER_KEY = 'auth-user';

function loadStoredUser() {
  const raw = localStorage.getItem(USER_KEY);
  if (!raw) return null;

  return JSON.parse(raw) as AuthUser | null;
}

export const useAuthStore = create<AuthState>((set) => ({
  token: localStorage.getItem(TOKEN_KEY),
  user: loadStoredUser(),

  setAuth: (token, user) => {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
    set({ token, user });
  },

  logout: () => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    set({ token: null, user: null });
  },
}));

export const useIsAuthenticated = () => useAuthStore((s) => s.token);
