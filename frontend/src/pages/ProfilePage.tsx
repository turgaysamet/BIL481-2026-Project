import { FormEvent, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { profileApi } from '../api/profileApi';
import type { CefrLevel, Interest, SpokenLanguage } from '../api/types';
import ErrorMessage from '../components/ErrorMessage';

const LANGUAGES = ['tr', 'en', 'es', 'de', 'fr', 'it', 'pt', 'pl'];
const LEVELS: CefrLevel[] = ['A1', 'A2', 'B1', 'B2', 'C1', 'C2'];

/** UC-2: create and edit profile (FR-04, FR-05). */
export default function ProfilePage() {
  const { t } = useTranslation();
  const [catalogue, setCatalogue] = useState<Interest[]>([]);
  const [displayName, setDisplayName] = useState('');
  const [homeUniversity, setHomeUniversity] = useState('');
  const [bio, setBio] = useState('');
  const [spoken, setSpoken] = useState<SpokenLanguage[]>([{ languageCode: 'en', level: 'B2' }]);
  const [learning, setLearning] = useState<string[]>([]);
  const [interestIds, setInterestIds] = useState<number[]>([]);
  const [skills, setSkills] = useState('');
  const [saved, setSaved] = useState(false);
  const [error, setError] = useState<unknown>(null);

  useEffect(() => {
    profileApi.getInterests().then(setCatalogue).catch(setError);
    profileApi.getOwn().then((p) => {
      setDisplayName(p.displayName);
      setHomeUniversity(p.homeUniversity ?? '');
      setBio(p.bio ?? '');
      setSpoken(p.spokenLanguages);
      setLearning(p.learningLanguages);
      setInterestIds(p.interests.map((i) => i.id));
      setSkills(p.skills.join(', '));
    }).catch(() => { /* no profile yet: empty form */ });
  }, []);

  const toggle = <T,>(list: T[], value: T) => (list.includes(value) ? list.filter((v) => v !== value) : [...list, value]);

  async function submit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    try {
      await profileApi.saveOwn({
        displayName, homeUniversity, bio, spokenLanguages: spoken, learningLanguages: learning, interestIds,
        skills: skills.split(',').map((s) => s.trim()).filter(Boolean),
      });
      setSaved(true);
    } catch (err) {
      setError(err);
    }
  }

  return (
    <form className="card form" onSubmit={submit}>
      <h1>{t('profile.title')}</h1>
      <label>{t('profile.displayName')}<input required maxLength={50} value={displayName} onChange={(e) => setDisplayName(e.target.value)} /></label>
      <label>{t('profile.homeUniversity')}<input value={homeUniversity} onChange={(e) => setHomeUniversity(e.target.value)} /></label>
      <label>{t('profile.bio')}<textarea maxLength={500} value={bio} onChange={(e) => setBio(e.target.value)} /></label>

      <fieldset>
        <legend>{t('profile.spoken')}</legend>
        {spoken.map((lang, index) => (
          <div key={index} className="row">
            <select value={lang.languageCode} onChange={(e) => setSpoken(spoken.map((l, i) => (i === index ? { ...l, languageCode: e.target.value } : l)))}>
              {LANGUAGES.map((code) => <option key={code}>{code}</option>)}
            </select>
            <select value={lang.level} onChange={(e) => setSpoken(spoken.map((l, i) => (i === index ? { ...l, level: e.target.value as CefrLevel } : l)))}>
              {LEVELS.map((level) => <option key={level}>{level}</option>)}
            </select>
          </div>
        ))}
        <button type="button" className="link" onClick={() => setSpoken([...spoken, { languageCode: 'tr', level: 'A1' }])}>+</button>
      </fieldset>

      <fieldset>
        <legend>{t('profile.learning')}</legend>
        <div className="chips">
          {LANGUAGES.map((code) => (
            <button type="button" key={code} className={learning.includes(code) ? 'chip on' : 'chip'} onClick={() => setLearning(toggle(learning, code))}>{code}</button>
          ))}
        </div>
      </fieldset>

      <fieldset>
        <legend>{t('profile.interests')} ({interestIds.length})</legend>
        <div className="chips">
          {catalogue.map((interest) => (
            <button type="button" key={interest.id} className={interestIds.includes(interest.id) ? 'chip on' : 'chip'}
              onClick={() => setInterestIds(toggle(interestIds, interest.id))}>{interest.name}</button>
          ))}
        </div>
      </fieldset>

      <label>{t('profile.skills')}<input value={skills} onChange={(e) => setSkills(e.target.value)} /></label>
      <ErrorMessage error={error} />
      {saved && <p className="success">{t('profile.saved')}</p>}
      <button type="submit" disabled={interestIds.length < 3}>{t('profile.save')}</button>
    </form>
  );
}
