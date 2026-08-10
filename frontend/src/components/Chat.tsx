import "./Chat.css";
import { useEffect, useRef, useState } from "react";
import { useAuthStore } from "../store/authStore.ts";
import { useChat } from "../context/ChatContext.tsx";
import { useUsers } from "../context/UserContext.tsx";
import formatInstantTime from "../utils/formatToLocalTime.ts";
import * as React from "react";

export default function Chat() {
  const { user } = useAuthStore();
  const { messages, getMessages, sendMessage } = useChat();
  const { users } = useUsers();
  const [draft, setDraft] = useState("");
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    void getMessages();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ block: "end" });
  }, [messages.length]);

  function handleSend(e: React.SubmitEvent<HTMLFormElement>) {
    e.preventDefault();
    const content = draft.trim();
    if (!content) return;

    void sendMessage(content);
    setDraft("");
  }

  return (
    <div id="page-chat" className="page-inner">
      <div className="chat-shell fade-in">
        <div className="chat-messages">
          {messages.map((message) => {
            const isOwn = message.sender.id === user?.id;
            const authorName = `${message.sender.firstname} ${message.sender.lastname}`.trim();
            const isOnline = users.some(u => u.id === message.sender.id && u.online);

            return (
              <div key={message.id} className={`chat-message${isOwn ? " chat-message--own" : ""}`}>
                {!isOwn && (
                  <div className={`chat-avatar${isOnline ? " chat-avatar--online" : ""}`} title={isOnline ? "Online" : "Offline"}>
                    {initials(authorName)}
                  </div>
                )}
                <div className="chat-message-body">
                  {!isOwn && <span className="chat-message-author">{authorName}</span>}
                  <div className="chat-bubble">{message.content}</div>
                  <span className="chat-message-time">{formatInstantTime(message.createdAt)}</span>
                </div>
              </div>
            );
          })}
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
