import { useAppSelector } from '@/app/hooks';

export function useAuth() {
  const { userId, email, role, accessToken } = useAppSelector((s) => s.auth);
  return { userId, email, role, accessToken, isAuthenticated: !!accessToken };
}
