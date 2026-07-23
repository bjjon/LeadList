import "./SearchBar.css";
import type { Status } from "../types/Status.ts";
import useOutsideClick from "../hooks/OutsideClick.ts";
import { useState } from "react";

type SearchBarType = {
  query: string;
  onQueryChange: (q: string) => void;
  availableStatus: Status[];
  statusFilters: Status[];
  toggleStatus: (status: Status) => void;
}

export default function SearchBar({ query, onQueryChange, availableStatus, statusFilters, toggleStatus }: Readonly<SearchBarType>) {
  const [isOpen, setIsOpen] = useState(false);
  const containerRef = useOutsideClick(() => setIsOpen(false));

  const hasSuggestions = availableStatus.length > 0;

  const selectStatus = (status: Status) => {
    toggleStatus(status);
    onQueryChange("");
  };

  return (
    <div className="leads-toolbar">
      <div className="search-bar" ref={containerRef}>
        <div className="search-inner">
          <svg className="search-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                  d="M21 21l-4.35-4.35M11 19a8 8 0 100-16 8 8 0 000 16z" />
          </svg>

          {[...statusFilters].map((s) => (
            <span key={s.value} className="filter-chip">
              <span className="filter-chip-dot" style={{ background: s.color }} />
              {s.label}
              <button
                className="filter-chip-remove"
                onMouseDown={(e) => e.preventDefault()}
                onClick={() => toggleStatus(s)}
              >
                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2.5" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </span>
          ))}

          <input
            type="text"
            className="search-input"
            placeholder={
              statusFilters.length > 0
                ? "Weiter filtern…"
                : "Suchen oder filtern …"
            }
            value={query}
            onFocus={() => setIsOpen(true)}
            onChange={(e) => {
              onQueryChange(e.target.value);
              setIsOpen(true);
            }}
          />

          {isOpen && hasSuggestions && (
            <div className="ac-panel">
              {availableStatus.length > 0 && (
                <div className="ac-group">
                  <div className="ac-label">Status</div>
                  <div className="ac-badge-row">
                    {availableStatus.map((s) => (
                      <button
                        key={s.value}
                        className={`ac-badge${s.value ? " ac-badge--on" : ""}`}
                        onMouseDown={(e) => e.preventDefault()}
                        onClick={() => selectStatus(s)}
                      >
                        <span className="ac-badge-dot" style={{ background: s.color }} />
                        {s.label}
                      </button>
                    ))}
                  </div>
                </div>
              )}
            </div>
          )}

        </div>
      </div>
    </div>
  )
}