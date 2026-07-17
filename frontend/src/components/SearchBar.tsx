import "./SearchBar.css";

export default function SearchBar() {
  return (
    <div className="leads-toolbar">
      <div className="search-bar">
        <div className="search-inner">
          <svg className="search-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                  d="M21 21l-4.35-4.35M11 19a8 8 0 100-16 8 8 0 000 16z" />
          </svg>

          <input
            type="text"
            className="search-input"
          />
        </div>
      </div>
    </div>
  )
}