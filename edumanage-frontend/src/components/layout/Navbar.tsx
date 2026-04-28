import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { useAppDispatch } from '@/app/hooks';
import { logout } from '@/features/auth/authSlice';
import { useLogoutMutation } from '@/features/auth/authApi';

export function Navbar() {
  const { email, role } = useAuth();
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const [logoutApi] = useLogoutMutation();

  async function handleLogout() {
    try { await logoutApi().unwrap(); } catch { /* token already revoked or expired */ }
    dispatch(logout());
    navigate('/login');
  }

  const roleBadgeColor: Record<string, string> = {
    ADMIN: 'bg-purple-100 text-purple-700',
    TEACHER: 'bg-green-100 text-green-700',
    STUDENT: 'bg-blue-100 text-blue-700',
    PARENT: 'bg-orange-100 text-orange-700',
  };

  return (
    <header className="h-14 bg-white border-b border-gray-200 flex items-center justify-end px-6 gap-4">
      {role && (
        <span className={`text-xs font-semibold px-2 py-1 rounded-full ${roleBadgeColor[role] ?? ''}`}>
          {role}
        </span>
      )}
      <span className="text-sm text-gray-600">{email}</span>
      <button
        onClick={handleLogout}
        className="text-sm text-red-500 hover:text-red-700 font-medium"
      >
        Logout
      </button>
    </header>
  );
}
