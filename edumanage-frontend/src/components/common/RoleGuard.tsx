import type { Role } from '@/types/auth.types';
import { useAuth } from '@/hooks/useAuth';

interface Props {
  allowedRoles: Role[];
  children: React.ReactNode;
}

export function RoleGuard({ allowedRoles, children }: Props) {
  const { role } = useAuth();
  if (!role || !allowedRoles.includes(role)) {
    return (
      <div className="flex flex-col items-center justify-center h-64 text-center">
        <p className="text-4xl font-bold text-gray-300">403</p>
        <p className="mt-2 text-gray-500">You don't have permission to view this page.</p>
      </div>
    );
  }
  return <>{children}</>;
}
