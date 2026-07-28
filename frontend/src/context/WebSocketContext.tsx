import { createContext, type ReactNode, useContext, useEffect, useRef, useState } from "react";
import { Client, type IMessage } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { useAuthStore } from "../store/authStore.ts";
import { useLeads } from "./LeadContext.tsx";
import type { Lead } from "../types/Lead.ts";

interface WebSocketContextType {
  connected: boolean;
}

const WebSocketContext = createContext<WebSocketContextType | null>(null);

function WebSocketProvider({ children }: Readonly<{ children: ReactNode }>) {
  const [connected, setConnected] = useState(false);
  const { upsertLead } = useLeads();
  const token = useAuthStore(s => s.token);

  const upsertLeadRef = useRef(upsertLead);
  useEffect(() => {
    upsertLeadRef.current = upsertLead;
  });

  useEffect(() => {
    if (!token) {
      return;
    }

    const client = new Client({
      webSocketFactory: () => new SockJS("/ws"),
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 5000,
      onConnect: () => {
        setConnected(true);
        client.subscribe("/topic/leads", (message: IMessage) => {
          const lead: Lead = JSON.parse(message.body);
          upsertLeadRef.current(lead);
        });
      },
      onStompError: () => setConnected(false),
      onWebSocketClose: () => setConnected(false),
    });

    client.activate();

    return () => {
      client.deactivate();
    };
  }, [token]);

  return (
    <WebSocketContext.Provider value={{ connected }}>
      {children}
    </WebSocketContext.Provider>
  )
}

function useWebSocket() {
  const context = useContext(WebSocketContext);
  if (!context) throw new Error("useWebSocket must be used within WebSocketProvider");
  return context;
}

// eslint-disable-next-line react-refresh/only-export-components
export { WebSocketProvider, useWebSocket };
