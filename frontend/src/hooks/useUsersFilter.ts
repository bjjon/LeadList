import { api } from "../api/axiosInstance.ts";
import {useCallback, useEffect, useState} from "react";
import type { User } from "../types/User.ts";

function useUsersFilter(query: string) {
  const [users, setUsers] = useState<User[]>([]);
  const [usersFilters, setUsersFilters] = useState<User[]>([]);
  const matchedUser: User[] = getMatchedUser(query);

  function getMatchedUser(query: string): User[] {
    const q = query.trim().toLowerCase();
    return users.filter(
      (u) => !q || u.firstname.toLowerCase().includes(q) || u.lastname.toLowerCase().includes(q),
    )
  }

  const availableUser = matchedUser.filter(
    (u) => !usersFilters.some((f) => f.id === u.id),
  );

  const toggleUser = useCallback((user: User) => {
    setUsersFilters((prev) =>
      prev.some((u) => u.id === user.id)
        ? prev.filter((u) => u.id !== user.id)
        : [...prev, user],
    );
  }, []);

  useEffect(() => {
    void getAllUsers();
  }, []);

  async function getAllUsers(): Promise<void> {
    try {
      const { data } = await api.get<User[]>('users');
      setUsers(data);
    } catch (error) {
      console.error(error);
    }
  }

  return { usersFilters, matchedUser, availableUser, getMatchedUser, getAllUsers, toggleUser };
}

export default useUsersFilter;