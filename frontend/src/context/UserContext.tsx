import type { User } from "../types/User.ts";
import { createContext, type ReactNode, useContext, useEffect, useState } from "react";
import { api } from "../api/axiosInstance.ts";

interface UserContextType {
  users: User[],
  getUsers: () => Promise<void>,
  upsertUser: (user: User) => void,
}

const UserContext = createContext<UserContextType | null>(null);

function UserProvider({ children }: Readonly<{ children: ReactNode }>) {
  const [users, setUsers] = useState<User[]>([]);

  const getUsers = async () => {
    try {
      const { data } = await api.get<User[]>("/users");
      setUsers(data);
    } catch (error) {
      console.error(error);
    }
  }

  const upsertUser = (updated: User) => {
    setUsers(prev => {
      const exists = prev.some(user => user.id === updated.id);
      return exists
        ? prev.map(user => user.id === updated.id ? updated : user)
        : [...prev, updated];
    });
  }

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    void getUsers();
  }, []);

  return (
    <UserContext.Provider value={{ users, getUsers, upsertUser }}>
      {children}
    </UserContext.Provider>
  )
}

function useUsers() {
  const context = useContext(UserContext);
  if (!context) throw new Error("useUsers must be used within UserProvider");
  return context;
}

// eslint-disable-next-line react-refresh/only-export-components
export { UserProvider, useUsers };
