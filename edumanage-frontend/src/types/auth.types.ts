export type Role = 'ADMIN' | 'TEACHER' | 'STUDENT' | 'PARENT';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  role: Role;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  role: Role;
  userId: string;
}
