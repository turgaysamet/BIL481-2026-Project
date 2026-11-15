import { NavLink, Navigate, Route, Routes } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { changeLanguage, SUPPORTED_LANGUAGES } from './i18n';
import { getToken } from './api/client';
import { authApi } from './api/authApi';
import RegisterPage from './pages/RegisterPage';
import LoginPage from './pages/LoginPage';
import ProfilePage from './pages/ProfilePage';
import MatchesPage from './pages/MatchesPage';
import EventsPage from './pages/EventsPage';

export default function App() {
  const { t, i18n } = useTranslation();
  const loggedIn = getToken() !== null;

  return (
    <div className="app">
      <header className="topbar">
        <strong className="brand">CampusBridge</strong>
        <nav>
          {loggedIn ? (
            <>
              <NavLink to="/matches">{t('nav.matches')}</NavLink>
              <NavLink to="/events">{t('nav.events')}</NavLink>
              <NavLink to="/profile">{t('nav.profile')}</NavLink>
              <a href="/login" onClick={() => authApi.logout()}>{t('nav.logout')}</a>
            </>
          ) : (
            <>
              <NavLink to="/login">{t('nav.login')}</NavLink>
              <NavLink to="/register">{t('nav.register')}</NavLink>
            </>
          )}
        </nav>
        <select aria-label="Language" value={i18n.language} onChange={(e) => changeLanguage(e.target.value)}>
          {SUPPORTED_LANGUAGES.map((lang) => <option key={lang} value={lang}>{lang.toUpperCase()}</option>)}
        </select>
      </header>
      <main>
        <Routes>
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/matches" element={<MatchesPage />} />
          <Route path="/events" element={<EventsPage />} />
          <Route path="*" element={<Navigate to={loggedIn ? '/matches' : '/login'} replace />} />
        </Routes>
      </main>
    </div>
  );
}
