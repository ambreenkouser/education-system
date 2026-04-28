export interface UserProfileRequest {
  firstName: string;
  lastName: string;
  phone?: string;
  address?: string;
  avatarUrl?: string;
}

export interface UserProfileResponse {
  id: string;
  userId: string;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string;
  address?: string;
  userType: string;
  avatarUrl?: string;
  updatedAt: string;
}
