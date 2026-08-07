import type { ChatMessage } from "../types/ChatMessage.ts";
import { createContext, type ReactNode, useContext, useState } from "react";
import { api } from "../api/axiosInstance.ts";

interface ChatContextType {
  messages: ChatMessage[],
  getMessages: () => Promise<void>,
  sendMessage: (content: string) => Promise<void>,
  appendMessage: (message: ChatMessage) => void,
}

const ChatContext = createContext<ChatContextType | null>(null);

function ChatProvider({ children }: Readonly<{ children: ReactNode }>) {
  const [messages, setMessages] = useState<ChatMessage[]>([]);

  const getMessages = async () => {
    try {
      const { data } = await api.get<ChatMessage[]>("/chat-messages");
      setMessages(data);
    } catch (error) {
      console.error(error);
    }
  }

  const appendMessage = (message: ChatMessage) => {
    setMessages(prev => {
      if (prev.some(existing => existing.id === message.id)) return prev;
      return [...prev, message];
    });
  }

  const sendMessage = async (content: string) => {
    try {
      const { data } = await api.post<ChatMessage>("/chat-messages", { content });
      appendMessage(data);
    } catch (error) {
      console.error(error);
    }
  }

  return (
    <ChatContext.Provider value={{ messages, getMessages, sendMessage, appendMessage }}>
      {children}
    </ChatContext.Provider>
  )
}

function useChat() {
  const context = useContext(ChatContext);
  if (!context) throw new Error("useChat must be used within ChatProvider");
  return context;
}

// eslint-disable-next-line react-refresh/only-export-components
export { ChatProvider, useChat };
