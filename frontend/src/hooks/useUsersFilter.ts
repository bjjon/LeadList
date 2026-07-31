import {useCallback, useState} from "react";
import type { User } from "../types/User.ts";
import { useUsers } from "../context/UserContext.tsx";

function useUsersFilter(query: string) {
  const { users, getUsers } = useUsers();
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

  const liveUsersFilters = usersFilters.map((f) => users.find((u) => u.id === f.id) ?? f);

  return { usersFilters: liveUsersFilters, matchedUser, availableUser, getMatchedUser, getAllUsers: getUsers, toggleUser };
}

export default useUsersFilter;
