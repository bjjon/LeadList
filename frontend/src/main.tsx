import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.tsx'
import { BrowserRouter } from "react-router-dom";
import { LeadProvider } from "./context/LeadContext.tsx";
import { UserProvider } from "./context/UserContext.tsx";
import { ChatProvider } from "./context/ChatContext.tsx";
import { WebSocketProvider } from "./context/WebSocketContext.tsx";

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <UserProvider>
      <LeadProvider>
        <ChatProvider>
          <WebSocketProvider>
            <BrowserRouter>
              <App />
            </BrowserRouter>
          </WebSocketProvider>
        </ChatProvider>
      </LeadProvider>
    </UserProvider>
  </StrictMode>,
)
