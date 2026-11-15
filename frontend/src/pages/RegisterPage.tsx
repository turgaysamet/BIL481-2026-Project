import { FormEvent, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { authApi } from '../api/authApi';
import type { UserType } from '../api/types';
import ErrorMessage from '../components/ErrorMessage';

/** UC-1: registration with institutional e-mail or invitation code (FR-01, FR-02, FR-16). */
export default function RegisterPage() {
  const { t } = useTranslation();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [userType, setUserType] = useState<UserType>('ERASMUS');
  const [invitationCode, setInvitationCode] = useState('');
  const [consent, setConsent] = useState(false);
  const [done, setDone] = useState(false);
  const [error, setError] = useState<unknown>(null);

  async function submit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    try {
      await authApi.register({ email, password, userType, invitationCode: invitationCode || undefined, consentAccepted: consent });
      setDone(true);
    } catch (err) {
      setError(err);
    }
  }

  if (done) return <p className="card success">{t('register.success')}</p>;

  return (
    <form className="card form" onSubmit={submit}>
      <h1>{t('register.title')}</h1>
      <label>{t('register.email')}
        <input type="email" required value={email} onChange={(e) => setEmail(e.target.value)} />
        <small>{t('register.emailHint')}</small>
      </label>
      <label>{t('register.password')}
        <input type="password" required minLength={8} value={password} onChange={(e) => setPassword(e.target.value)} />
      </label>
      <fieldset>
        <legend>{t('register.userType')}</legend>
        <label><input type="radio" checked={userType === 'ERASMUS'} onChange={() => setUserType('ERASMUS')} /> {t('register.erasmus')}</label>
        <label><input type="radio" checked={userType === 'LOCAL'} onChange={() => setUserType('LOCAL')} /> {t('register.local')}</label>
      </fieldset>
      <label>{t('register.invitationCode')}
        <input value={invitationCode} onChange={(e) => setInvitationCode(e.target.value.toUpperCase())} maxLength={8} />
      </label>
      <label className="inline"><input type="checkbox" checked={consent} onChange={(e) => setConsent(e.target.checked)} /> {t('register.consent')}</label>
      <ErrorMessage error={error} />
      <button type="submit" disabled={!consent}>{t('register.submit')}</button>
    </form>
  );
}
