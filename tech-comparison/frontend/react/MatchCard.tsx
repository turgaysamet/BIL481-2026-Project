// React + TypeScript version of the match suggestion card (simplified from frontend/src/pages/MatchesPage.tsx).
import { useTranslation } from 'react-i18next';
import type { Suggestion } from '../../../frontend/src/api/types';

interface Props {
  suggestion: Suggestion;
  requested: boolean;
  onConnect: (userId: number) => void;
}

export function MatchCard({ suggestion: { profile, score }, requested, onConnect }: Props) {
  const { t } = useTranslation();
  return (
    <article className="card match">
      <h3>{profile.displayName}</h3>
      <p className="score">{t('matches.compatibility')}: <strong>{score.total}%</strong></p>
      <p>{profile.interests.map((i) => i.name).join(' · ')}</p>
      <button disabled={requested} onClick={() => onConnect(profile.userId)}>
        {requested ? t('matches.requested') : t('matches.connect')}
      </button>
    </article>
  );
}
// JSX is plain TypeScript: props and API types are checked by the compiler (tsc).
