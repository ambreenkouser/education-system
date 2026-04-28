import { useAuth } from '@/hooks/useAuth';
import { useGetStudentsQuery } from '@/features/students/studentApi';
import { useGetStudentAttendanceQuery } from './attendanceApi';
import { RoleGuard } from '@/components/common/RoleGuard';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';

export function AttendanceViewPage() {
  const { userId, role } = useAuth();
  const { data: allStudents } = useGetStudentsQuery();

  const myRecord = allStudents?.find((s) =>
    role === 'STUDENT' ? s.userId === userId : s.parentId === userId
  );

  const { data: records, isLoading } = useGetStudentAttendanceQuery(myRecord?.id ?? '', { skip: !myRecord });

  const present = records?.filter((r) => r.status === 'PRESENT').length ?? 0;
  const total   = records?.length ?? 0;
  const pct     = total > 0 ? ((present / total) * 100).toFixed(1) : '—';

  return (
    <RoleGuard allowedRoles={['STUDENT', 'PARENT']}>
      <div>
        <h1 className="text-2xl font-bold text-gray-800 mb-6">My Attendance</h1>

        {isLoading && <LoadingSpinner />}

        {!isLoading && myRecord && (
          <>
            <div className="grid grid-cols-3 gap-4 mb-6">
              <div className="bg-white rounded-xl shadow-sm p-4 text-center">
                <p className="text-2xl font-bold text-blue-600">{total}</p>
                <p className="text-xs text-gray-500 mt-1">Total Classes</p>
              </div>
              <div className="bg-white rounded-xl shadow-sm p-4 text-center">
                <p className="text-2xl font-bold text-green-600">{present}</p>
                <p className="text-xs text-gray-500 mt-1">Present</p>
              </div>
              <div className="bg-white rounded-xl shadow-sm p-4 text-center">
                <p className="text-2xl font-bold text-indigo-600">{pct}%</p>
                <p className="text-xs text-gray-500 mt-1">Attendance Rate</p>
              </div>
            </div>

            <div className="bg-white rounded-xl shadow-sm overflow-hidden">
              <table className="w-full text-sm">
                <thead className="bg-gray-50 border-b">
                  <tr>
                    <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Date</th>
                    <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Course</th>
                    <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Status</th>
                    <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Remarks</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {records?.map((r) => (
                    <tr key={r.id}>
                      <td className="px-4 py-3">{r.attendanceDate}</td>
                      <td className="px-4 py-3 text-gray-500 truncate max-w-[120px]">{r.courseId}</td>
                      <td className="px-4 py-3">
                        <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${
                          r.status === 'PRESENT' ? 'bg-green-100 text-green-700' :
                          r.status === 'LATE'    ? 'bg-yellow-100 text-yellow-700' :
                                                   'bg-red-100 text-red-700'
                        }`}>{r.status}</span>
                      </td>
                      <td className="px-4 py-3 text-gray-500">{r.remarks ?? '—'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </>
        )}

        {!isLoading && !myRecord && (
          <div className="text-sm text-gray-500 bg-yellow-50 border border-yellow-200 rounded-xl p-6">
            No student record linked to your account.
          </div>
        )}
      </div>
    </RoleGuard>
  );
}
