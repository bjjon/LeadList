import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.tsx'
import { BrowserRouter } from "react-router-dom";
import { LeadProvider } from "./context/LeadContext.tsx";

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <LeadProvider>
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </LeadProvider>
  </StrictMode>,
)
