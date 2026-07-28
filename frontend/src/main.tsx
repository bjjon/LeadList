import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.tsx'
import { BrowserRouter } from "react-router-dom";
import { LeadProvider } from "./context/LeadContext.tsx";
import { WebSocketProvider } from "./context/WebSocketContext.tsx";

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <LeadProvider>
      <WebSocketProvider>
        <BrowserRouter>
          <App />
        </BrowserRouter>
      </WebSocketProvider>
    </LeadProvider>
  </StrictMode>,
)
