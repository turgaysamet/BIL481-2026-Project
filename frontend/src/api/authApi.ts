import { api, setToken } from './client';
import type { AuthResponse, InvitationCodeResponse, LoginRequest, RegisterRequest, RegisterResponse } from './types';

export const authApi = {
  register: (body: RegisterRequest) =>
    api<RegisterResponse>('/auth/register', { method: 'POST', body: JSON.stringify(body) }),

  verify: (token: string) => api<void>(`/auth/verify?token=${encodeURIComponent(token)}`),

  login: async (body: LoginRequest) => {
    const auth = await api<AuthResponse>('/auth/login', { method: 'POST', body: JSON.stringify(body) });
    setToken(auth.token);
    return auth;
  },

  logout: () => setToken(null),

  createInvitationCode: () => api<InvitationCodeResponse>('/auth/invitation-codes', { method: 'POST' }),
};
