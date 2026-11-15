import { api, toQuery } from './client';
import type { MatchFilters, MatchRequest, Suggestion } from './types';

export const matchingApi = {
  getSuggestions: (filters?: MatchFilters) =>
    api<Suggestion[]>(`/matches/suggestions${filters ? toQuery({ useFilters: true, ...filters }) : ''}`),
  savePreferences: (filters: MatchFilters) =>
    api<void>('/matches/preferences', { method: 'PUT', body: JSON.stringify(filters) }),
  sendRequest: (receiverId: number) =>
    api<MatchRequest>('/match-requests', { method: 'POST', body: JSON.stringify({ receiverId }) }),
  getIncoming: () => api<MatchRequest[]>('/match-requests/incoming'),
  accept: (id: number) => api<MatchRequest>(`/match-requests/${id}/accept`, { method: 'POST' }),
  decline: (id: number) => api<MatchRequest>(`/match-requests/${id}/decline`, { method: 'POST' }),
  getMatches: () => api<MatchRequest[]>('/matches'),
};
