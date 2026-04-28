import { useAuth } from './useAuth';

export function useRoleAccess() {
  const { role } = useAuth();
  return {
    isAdmin: role === 'ADMIN',
    isTeacher: role === 'TEACHER',
    isStudent: role === 'STUDENT',
    isParent: role === 'PARENT',
    canManageCourses: role === 'ADMIN',
    canMarkAttendance: role === 'TEACHER' || role === 'ADMIN',
    canSubmitGrades: role === 'TEACHER' || role === 'ADMIN',
    canViewStudents: role === 'ADMIN' || role === 'TEACHER',
    canViewReports: role === 'ADMIN' || role === 'TEACHER',
    canViewOwnData: role === 'STUDENT' || role === 'PARENT',
  };
}
