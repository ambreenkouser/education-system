import { NavLink } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import type { Role } from '@/types/auth.types';

interface NavItem { label: string; to: string; roles: Role[] }

const NAV_ITEMS: NavItem[] = [
  { label: 'Dashboard',         to: '/dashboard',           roles: ['ADMIN', 'TEACHER', 'STUDENT', 'PARENT'] },
  { label: 'Students',          to: '/students',            roles: ['ADMIN', 'TEACHER'] },
  { label: 'Courses',           to: '/courses',             roles: ['ADMIN', 'TEACHER', 'STUDENT', 'PARENT'] },
  { label: 'Timetable',         to: '/timetable',           roles: ['ADMIN', 'TEACHER', 'STUDENT', 'PARENT'] },
  { label: 'Manage Timetable',  to: '/timetable/manage',    roles: ['ADMIN'] },
  { label: 'Mark Attendance',   to: '/attendance/mark',     roles: ['ADMIN', 'TEACHER'] },
  { label: 'My Attendance',     to: '/attendance/my',       roles: ['STUDENT', 'PARENT'] },
  { label: 'Exam Management',   to: '/grades/exams',        roles: ['ADMIN', 'TEACHER'] },
  { label: 'Grade Entry',       to: '/grades/entry',        roles: ['ADMIN', 'TEACHER'] },
  { label: 'My Grades',         to: '/grades/my',           roles: ['STUDENT', 'PARENT'] },
  { label: 'My Fees',           to: '/fees/my',             roles: ['STUDENT', 'PARENT'] },
  { label: 'Outstanding Fees',  to: '/fees/outstanding',    roles: ['ADMIN'] },
  { label: 'Profile',           to: '/profile',             roles: ['ADMIN', 'TEACHER', 'STUDENT', 'PARENT'] },
];

export function Sidebar() {
  const { role } = useAuth();
  const visible = NAV_ITEMS.filter((item) => role && item.roles.includes(role));

  return (
    <aside className="w-64 bg-slate-800 min-h-screen flex flex-col">
      <div className="px-6 py-5 border-b border-slate-700">
        <h1 className="text-white font-bold text-lg tracking-wide">EduManage Pro</h1>
      </div>
      <nav className="flex-1 px-3 py-4 space-y-1">
        {visible.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) =>
              `block px-3 py-2 rounded-md text-sm font-medium transition-colors ${
                isActive
                  ? 'bg-blue-600 text-white'
                  : 'text-slate-300 hover:bg-slate-700 hover:text-white'
              }`
            }
          >
            {item.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
