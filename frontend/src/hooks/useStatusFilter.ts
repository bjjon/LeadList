import { api } from "../api/axiosInstance.ts";
import type { Status } from "../types/Status.ts";
import { useCallback, useEffect, useState } from "react";

function useStatusFilter(query: string) {
  const [status, setStatus] = useState<Status[]>([]);
  const [statusFilters, setStatusFilters] = useState<Status[]>([]);
  const matchedStatus: Status[] = getMatchedStatus(query);

  function getMatchedStatus(query: string): Status[] {
    const q = query.trim().toLowerCase();
    return status.filter(
      (s) => !q || s.label.toLowerCase().includes(q) || s.value.toLowerCase().includes(q),
    )
  }

  const availableStatus = matchedStatus.filter(
    (s) => !statusFilters.some((f) => f.value === s.value),
  );

  const toggleStatus = useCallback((status: Status) => {
    setStatusFilters((prev) =>
      prev.some((s) => s.value === status.value)
        ? prev.filter((s) => s.value !== status.value)
        : [...prev, status],
    );
  }, []);

  useEffect(() => {
    void getAllStatus();
  }, []);

  async function getAllStatus(): Promise<void> {
    try {
      const { data } = await api.get<Status[]>('status');
      setStatus(data);
    } catch (error) {
      console.error(error);
    }
  }

  return { statusFilters, availableStatus, toggleStatus, getAllStatus };
}

export default useStatusFilter;