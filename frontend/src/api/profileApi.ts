import { api } from './client';
import type { Interest, ProfileRequest, ProfileResponse } from './types';

export const profileApi = {
  getOwn: () => api<ProfileResponse>('/profiles/me'),
  saveOwn: (body: ProfileRequest) => api<ProfileResponse>('/profiles/me', { method: 'PUT', body: JSON.stringify(body) }),
  getPublic: (userId: number) => api<ProfileResponse>(`/profiles/${userId}`),
  getInterests: () => api<Interest[]>('/interests'),
};
