import "./Chat.css";
import { useEffect, useRef, useState } from "react";
import { useAuthStore } from "../store/authStore.ts";
import formatInstantTime from "../utils/formatToLocalTime.ts";

// Platzhalter-Nachrichten nur für den Layout-Entwurf.
// Die echte Anbindung (Persistenz + WebSocket-Topic) folgt in einem separaten Schritt.
type MockMessage = {
  id: string;
  author: string;
  isOwn: boolean;
  text: string;
  sentAt: string;
};

function buildMockMessages(ownName: string): MockMessage[] {
  const now = Date.now();
  const minutesAgo = (m: number) => new Date(now - m * 60_000).toISOString();

  return [
    { id: "1", author: "Mia Keller", isOwn: false, text: "Hat schon jemand den Lead von Fischer & Söhne angerufen?", sentAt: minutesAgo(42) },
    { id: "2", author: ownName, isOwn: true, text: "Ja, war heute Vormittag dran – noch nicht erreicht.", sentAt: minutesAgo(38) },
    { id: "3", author: "Tom Bruns", isOwn: false, text: "Ich übernehm den Rückruf morgen früh.", sentAt: minutesAgo(35) },
    { id: "4", author: "Mia Keller", isOwn: false, text: "Super, danke dir! 👍", sentAt: minutesAgo(34) },
    { id: "5", author: ownName, isOwn: true, text: "Perfekt, ich trag das mal in den Verlauf ein.", sentAt: minutesAgo(12) },
  ];
}

export default function Chat() {
  const { user } = useAuthStore();
  const ownName = `${user?.firstname ?? "Du"} ${user?.lastname ?? ""}`.trim();

  const [messages, setMessages] = useState<MockMessage[]>(() => buildMockMessages(ownName));
  const [draft, setDraft] = useState("");
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ block: "end" });
  }, [messages.length]);

  function handleSend(e: React.FormEvent) {
    e.preventDefault();
    const text = draft.trim();
    if (!text) return;

    // Nur lokale Anzeige im Layout-Entwurf – kein Versand an Backend/WebSocket.
    setMessages((prev) => [
      ...prev,
      { id: crypto.randomUUID(), author: ownName, isOwn: true, text, sentAt: new Date().toISOString() },
    ]);
    setDraft("");
  }

  return (
    <div id="page-chat" className="page-inner">
      <div className="chat-shell fade-in">
        <div className="chat-messages">
          {messages.map((message) => (
            <div key={message.id} className={`chat-message${message.isOwn ? " chat-message--own" : ""}`}>
              {!message.isOwn && <div className="chat-avatar">{initials(message.author)}</div>}
              <div className="chat-message-body">
                {!message.isOwn && <span className="chat-message-author">{message.author}</span>}
                <div className="chat-bubble">{message.text}</div>
                <span className="chat-message-time">{formatInstantTime(message.sentAt)}</span>
              </div>
            </div>
          ))}
          <div ref={messagesEndRef} />
        </div>

        <form className="chat-input-row" onSubmit={handleSend}>
          <input
            type="text"
            className="chat-input"
            placeholder="Nachricht an das Team schreiben …"
            value={draft}
            onChange={(e) => setDraft(e.target.value)}
          />
          <button type="submit" className="chat-send-btn" disabled={!draft.trim()} title="Senden">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="2"
                d="M6 12L3.269 3.126A59.77 59.77 0 0121.485 12 59.77 59.77 0 013.27 20.874L5.999 12zm0 0h7.5"
              />
            </svg>
          </button>
        </form>
      </div>
    </div>
  );
}

function initials(name: string): string {
  return name
    .split(" ")
    .filter(Boolean)
    .map((part) => part[0]?.toUpperCase())
    .slice(0, 2)
    .join("");
}
