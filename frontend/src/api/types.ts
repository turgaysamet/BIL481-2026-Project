// TypeScript mirror of the backend DTOs (component interface between frontend and backend).

export type UserType = 'ERASMUS' | 'LOCAL';
export type Gender = 'FEMALE' | 'MALE' | 'OTHER';
export type CefrLevel = 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2';
export type InterestCategory =
  | 'SPORT' | 'LANGUAGE' | 'CULTURE' | 'MUSIC' | 'TECHNOLOGY' | 'OUTDOOR' | 'FOOD' | 'OTHER';
export type EventCategory = 'SPORT' | 'LANGUAGE' | 'CULTURE' | 'STUDY' | 'OTHER';

export interface ApiError { status: number; code: string; message: string; timestamp: string }

// UC-1
export interface RegisterRequest {
  email: string; password: string; userType: UserType; invitationCode?: string; consentAccepted: boolean;
}
export interface RegisterResponse { userId: number; message: string }
export interface LoginRequest { email: string; password: string }
export interface AuthResponse { token: string; userId: number; userType: UserType }
export interface InvitationCodeResponse { code: string; expiresAt: string }

// UC-2
export interface SpokenLanguage { languageCode: string; level: CefrLevel }
export interface Interest { id: number; name: string; category: InterestCategory }
export interface ProfileRequest {
  displayName: string; homeUniversity?: string; homeCountry?: string; gender?: Gender; bio?: string;
  spokenLanguages: SpokenLanguage[]; learningLanguages: string[]; interestIds: number[]; skills: string[];
}
export interface ProfileResponse {
  userId: number; displayName: string; userType: UserType; homeUniversity?: string; homeCountry?: string;
  gender?: Gender; bio?: string; photoUrl?: string; spokenLanguages: SpokenLanguage[];
  learningLanguages: string[]; interests: Interest[]; skills: string[]; completed: boolean;
}

// UC-3
export interface ScoreBreakdown { interestScore: number; skillScore: number; languageScore: number; total: number }
export interface Suggestion { profile: ProfileResponse; score: ScoreBreakdown }
export interface MatchFilters {
  sameGenderOnly: boolean; targetLanguage?: string; interestCategory?: InterestCategory; userType?: UserType;
}
export type MatchRequestStatus = 'PENDING' | 'ACCEPTED' | 'DECLINED' | 'CANCELLED' | 'REMOVED';
export interface MatchRequest {
  id: number; senderId: number; receiverId: number; status: MatchRequestStatus; createdAt: string; respondedAt?: string;
}

// UC-4
export interface CreateEventRequest {
  title: string; description?: string; category: EventCategory; startsAt: string; location: string;
  capacity: number; languages: string[];
}
export interface EventResponse {
  id: number; title: string; description?: string; category: EventCategory; startsAt: string; location: string;
  capacity: number; participantCount: number; freePlaces: number; languages: string[];
  status: 'SCHEDULED' | 'CANCELLED' | 'FINISHED'; creatorId: number; joinedByMe: boolean;
}
export interface EventSearch { category?: EventCategory; language?: string; q?: string }
