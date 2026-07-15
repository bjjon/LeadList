import './Header.css';
import { useLocation } from 'react-router-dom';
import { PAGE_META } from '../utils/const.ts';

export default function Header() {
  const location = useLocation();

  const meta = PAGE_META[location.pathname];

  return (
    <header className="main-header">
      <div>
        <h2 id="pageTitle">{meta.title}</h2>
        <p id="pageSubtitle">{meta.subtitle}</p>
      </div>
    </header>
  );
}