import { FormEvent, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { authApi } from '../api/authApi';
import ErrorMessage from '../components/ErrorMessage';

/** UC-1: log in (FR-03). */
export default function LoginPage() {
  const { t } = useTranslation();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<unknown>(null);

  async function submit(e: FormEvent) {
    e.preventDefault();
    try {
      await authApi.login({ email, password });
      window.location.assign('/matches');
    } catch (err) {
      setError(err);
    }
  }

  return (
    <form className="card form" onSubmit={submit}>
      <h1>{t('login.title')}</h1>
      <label>{t('register.email')}<input type="email" required value={email} onChange={(e) => setEmail(e.target.value)} /></label>
      <label>{t('register.password')}<input type="password" required value={password} onChange={(e) => setPassword(e.target.value)} /></label>
      <ErrorMessage error={error} />
      <button type="submit">{t('login.submit')}</button>
    </form>
  );
}
