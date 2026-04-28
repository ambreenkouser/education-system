import { Routes, Route, Navigate } from 'react-router-dom';
import { PrivateRoute } from '@/components/common/PrivateRoute';
import { AppLayout } from '@/components/layout/AppLayout';

import { LoginPage }            from '@/features/auth/LoginPage';
import { RegisterPage }         from '@/features/auth/RegisterPage';
import { DashboardPage }        from '@/features/dashboard/DashboardPage';
import { ProfilePage }          from '@/features/users/ProfilePage';
import { StudentListPage }      from '@/features/students/StudentListPage';
import { StudentDetailPage }    from '@/features/students/StudentDetailPage';
import { EnrollStudentForm }    from '@/features/students/EnrollStudentForm';
import { CourseListPage }       from '@/features/courses/CourseListPage';
import { CourseDetailPage }     from '@/features/courses/CourseDetailPage';
import { CourseFormPage }       from '@/features/courses/CourseFormPage';
import { TimetableViewPage }    from '@/features/timetable/TimetableViewPage';
import { TimetableManagePage }  from '@/features/timetable/TimetableManagePage';
import { MarkAttendancePage }   from '@/features/attendance/MarkAttendancePage';
import { AttendanceViewPage }   from '@/features/attendance/AttendanceViewPage';
import { ExamManagePage }       from '@/features/grades/ExamManagePage';
import { GradeEntryPage }       from '@/features/grades/GradeEntryPage';
import { GradeViewPage }        from '@/features/grades/GradeViewPage';
import { InvoiceListPage }      from '@/features/fees/InvoiceListPage';
import { OutstandingFeesPage }  from '@/features/fees/OutstandingFeesPage';
import { StudentReportPage }    from '@/features/reports/StudentReportPage';

export function AppRouter() {
  return (
    <Routes>
      {/* Public */}
      <Route path="/login"    element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      {/* Protected */}
      <Route element={<PrivateRoute />}>
        <Route element={<AppLayout />}>
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="dashboard"                  element={<DashboardPage />} />
          <Route path="profile"                    element={<ProfilePage />} />

          <Route path="students"                   element={<StudentListPage />} />
          <Route path="students/:id"               element={<StudentDetailPage />} />
          <Route path="students/:id/enroll"        element={<EnrollStudentForm />} />

          <Route path="courses"                    element={<CourseListPage />} />
          <Route path="courses/new"                element={<CourseFormPage />} />
          <Route path="courses/:id"                element={<CourseDetailPage />} />
          <Route path="courses/:id/edit"           element={<CourseFormPage />} />

          <Route path="timetable"                  element={<TimetableViewPage />} />
          <Route path="timetable/manage"           element={<TimetableManagePage />} />

          <Route path="attendance/mark"            element={<MarkAttendancePage />} />
          <Route path="attendance/my"              element={<AttendanceViewPage />} />

          <Route path="grades/exams"               element={<ExamManagePage />} />
          <Route path="grades/entry"               element={<GradeEntryPage />} />
          <Route path="grades/my"                  element={<GradeViewPage />} />

          <Route path="fees/my"                    element={<InvoiceListPage />} />
          <Route path="fees/outstanding"           element={<OutstandingFeesPage />} />

          <Route path="reports/students/:id"       element={<StudentReportPage />} />
        </Route>
      </Route>

      <Route path="*" element={
        <div className="min-h-screen flex items-center justify-center text-center">
          <div>
            <p className="text-6xl font-bold text-gray-200">404</p>
            <p className="text-gray-500 mt-2">Page not found</p>
          </div>
        </div>
      } />
    </Routes>
  );
}
