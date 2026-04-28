import { useParams, Link } from 'react-router-dom';
import { useGetStudentQuery, useGetEnrollmentsQuery } from './studentApi';
import { RoleGuard } from '@/components/common/RoleGuard';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { ErrorMessage } from '@/components/common/ErrorMessage';

export function StudentDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { data: student, isLoading, error } = useGetStudentQuery(id!);
  const { data: enrollments } = useGetEnrollmentsQuery(id!, { skip: !id });

  return (
    <RoleGuard allowedRoles={['ADMIN', 'TEACHER']}>
      <div className="max-w-2xl">
        <div className="flex items-center gap-2 mb-6">
          <Link to="/students" className="text-sm text-blue-600 hover:underline">← Students</Link>
        </div>

        {isLoading && <LoadingSpinner />}
        {error && <ErrorMessage message="Student not found." />}

        {student && (
          <>
            <div className="bg-white rounded-xl shadow-sm p-6 mb-4">
              <div className="flex items-start justify-between">
                <div>
                  <p className="text-xs text-gray-400 font-semibold uppercase">Student Code</p>
                  <p className="text-2xl font-bold text-gray-800">{student.studentCode}</p>
                </div>
                <span className={`text-xs px-2 py-1 rounded-full font-medium ${
                  student.status === 'ACTIVE' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'
                }`}>{student.status}</span>
              </div>
              <div className="grid grid-cols-2 gap-4 mt-4 text-sm text-gray-600">
                <div><span className="text-gray-400">Grade Level:</span> {student.gradeLevel}</div>
                <div><span className="text-gray-400">DOB:</span> {student.dateOfBirth ?? '—'}</div>
                <div><span className="text-gray-400">Enrolled:</span> {student.enrollmentDate?.slice(0, 10)}</div>
              </div>
              <div className="mt-4">
                <Link
                  to={`/students/${student.id}/enroll`}
                  className="text-sm bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium"
                >
                  Enroll in Course
                </Link>
              </div>
            </div>

            <div className="bg-white rounded-xl shadow-sm p-5">
              <h2 className="font-semibold text-gray-700 mb-3">Enrollments</h2>
              <ul className="divide-y divide-gray-100">
                {enrollments?.map((e) => (
                  <li key={e.id} className="py-2 flex justify-between text-sm">
                    <span className="text-gray-600">{e.courseId}</span>
                    <span className={`text-xs font-medium px-2 py-0.5 rounded-full ${
                      e.status === 'ACTIVE' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'
                    }`}>{e.status}</span>
                  </li>
                ))}
                {(!enrollments || enrollments.length === 0) && (
                  <li className="py-3 text-sm text-gray-400 text-center">No enrollments yet</li>
                )}
              </ul>
            </div>
          </>
        )}
      </div>
    </RoleGuard>
  );
}
