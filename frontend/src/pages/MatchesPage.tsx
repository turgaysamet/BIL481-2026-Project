import { useCallback, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { matchingApi } from '../api/matchingApi';
import type { MatchFilters, MatchRequest, Suggestion, UserType } from '../api/types';
import ErrorMessage from '../components/ErrorMessage';

/** UC-3: match suggestions with filters, and match requests (FR-06, FR-07, FR-08). */
export default function MatchesPage() {
  const { t } = useTranslation();
  const [filters, setFilters] = useState<MatchFilters>({ sameGenderOnly: false });
  const [suggestions, setSuggestions] = useState<Suggestion[]>([]);
  const [incoming, setIncoming] = useState<MatchRequest[]>([]);
  const [requested, setRequested] = useState<number[]>([]);
  const [error, setError] = useState<unknown>(null);

  const load = useCallback(async (withFilters?: MatchFilters) => {
    setError(null);
    try {
      setSuggestions(await matchingApi.getSuggestions(withFilters));
      setIncoming(await matchingApi.getIncoming());
    } catch (err) {
      setError(err);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  async function applyFilters() {
    await matchingApi.savePreferences(filters).catch(setError);
    load(filters);
  }

  async function connect(userId: number) {
    try {
      await matchingApi.sendRequest(userId);
      setRequested([...requested, userId]);
    } catch (err) {
      setError(err);
    }
  }

  async function answer(id: number, accept: boolean) {
    await (accept ? matchingApi.accept(id) : matchingApi.decline(id)).catch(setError);
    setIncoming(incoming.filter((r) => r.id !== id));
  }

  return (
    <div className="layout">
      <aside className="card">
        <h2>{t('matches.filters')}</h2>
        <label className="inline">
          <input type="checkbox" checked={filters.sameGenderOnly} onChange={(e) => setFilters({ ...filters, sameGenderOnly: e.target.checked })} />
          {t('matches.sameGender')}
        </label>
        <label>{t('matches.targetLanguage')}
          <select value={filters.targetLanguage ?? ''} onChange={(e) => setFilters({ ...filters, targetLanguage: e.target.value || undefined })}>
            <option value="">{t('matches.any')}</option>
            {['tr', 'en', 'es', 'de', 'fr', 'it'].map((code) => <option key={code}>{code}</option>)}
          </select>
        </label>
        <label>{t('matches.userType')}
          <select value={filters.userType ?? ''} onChange={(e) => setFilters({ ...filters, userType: (e.target.value || undefined) as UserType | undefined })}>
            <option value="">{t('matches.any')}</option>
            <option value="LOCAL">{t('register.local')}</option>
            <option value="ERASMUS">{t('register.erasmus')}</option>
          </select>
        </label>
        <button onClick={applyFilters}>{t('matches.apply')}</button>
      </aside>

      <section>
        <h1>{t('matches.title')}</h1>
        <ErrorMessage error={error} />
        {incoming.length > 0 && (
          <div className="card">
            <h2>{t('matches.incoming')}</h2>
            {incoming.map((r) => (
              <div key={r.id} className="row">
                <span>#{r.senderId}</span>
                <button onClick={() => answer(r.id, true)}>{t('matches.accept')}</button>
                <button className="secondary" onClick={() => answer(r.id, false)}>{t('matches.decline')}</button>
              </div>
            ))}
          </div>
        )}
        {suggestions.length === 0 && <p>{t('matches.empty')}</p>}
        <div className="grid">
          {suggestions.map(({ profile, score }) => (
            <article key={profile.userId} className="card match">
              <h3>{profile.displayName}</h3>
              <p className="muted">{profile.userType === 'LOCAL' ? t('register.local') : t('register.erasmus')}</p>
              <p className="score">{t('matches.compatibility')}: <strong>{score.total}%</strong></p>
              <p>{profile.interests.map((i) => i.name).join(' · ')}</p>
              <p className="muted">{profile.spokenLanguages.map((l) => `${l.languageCode.toUpperCase()} ${l.level}`).join(', ')}</p>
              <button disabled={requested.includes(profile.userId)} onClick={() => connect(profile.userId)}>
                {requested.includes(profile.userId) ? t('matches.requested') : t('matches.connect')}
              </button>
            </article>
          ))}
        </div>
      </section>
    </div>
  );
}
