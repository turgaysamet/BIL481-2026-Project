import { api, toQuery } from './client';
import type { CreateEventRequest, EventResponse, EventSearch } from './types';

export const eventsApi = {
  search: (search: EventSearch = {}) => api<EventResponse[]>(`/events${toQuery(search)}`),
  get: (id: number) => api<EventResponse>(`/events/${id}`),
  create: (body: CreateEventRequest) => api<EventResponse>('/events', { method: 'POST', body: JSON.stringify(body) }),
  join: (id: number) => api<EventResponse>(`/events/${id}/participants`, { method: 'POST' }),
  leave: (id: number) => api<EventResponse>(`/events/${id}/participants/me`, { method: 'DELETE' }),
  cancel: (id: number) => api<EventResponse>(`/events/${id}/cancel`, { method: 'POST' }),
};
