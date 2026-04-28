import { useAuth } from '@/hooks/useAuth';
import { AdminDashboard } from './AdminDashboard';
import { TeacherDashboard } from './TeacherDashboard';
import { StudentDashboard } from './StudentDashboard';
import { ParentDashboard } from './ParentDashboard';

export function DashboardPage() {
  const { role } = useAuth();
  if (role === 'ADMIN')   return <AdminDashboard />;
  if (role === 'TEACHER') return <TeacherDashboard />;
  if (role === 'STUDENT') return <StudentDashboard />;
  if (role === 'PARENT')  return <ParentDashboard />;
  return null;
}
