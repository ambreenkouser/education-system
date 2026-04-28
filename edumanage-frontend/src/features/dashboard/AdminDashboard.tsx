import { useGetCoursesQuery } from '@/features/courses/courseApi';
import { useGetStudentsQuery } from '@/features/students/studentApi';
import { useGetOutstandingInvoicesQuery } from '@/features/fees/feeApi';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';

function StatCard({ label, value, color }: { label: string; value: string | number; color: string }) {
  return (
    <div className={`rounded-xl p-5 text-white ${color}`}>
      <p className="text-sm opacity-80">{label}</p>
      <p className="text-3xl font-bold mt-1">{value}</p>
    </div>
  );
}

export function AdminDashboard() {
  const { data: courses, isLoading: lc } = useGetCoursesQuery();
  const { data: students, isLoading: ls } = useGetStudentsQuery();
  const { data: outstanding, isLoading: lf } = useGetOutstandingInvoicesQuery();

  if (lc || ls || lf) return <LoadingSpinner />;

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Admin Dashboard</h1>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <StatCard label="Total Students"    value={students?.length ?? 0}     color="bg-blue-500" />
        <StatCard label="Total Courses"     value={courses?.length ?? 0}      color="bg-green-500" />
        <StatCard label="Active Courses"    value={courses?.filter(c => c.status === 'ACTIVE').length ?? 0} color="bg-indigo-500" />
        <StatCard label="Outstanding Fees"  value={outstanding?.length ?? 0}  color="bg-red-500" />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-xl shadow-sm p-5">
          <h2 className="font-semibold text-gray-700 mb-3">Recent Courses</h2>
          <ul className="divide-y divide-gray-100">
            {courses?.slice(0, 5).map((c) => (
              <li key={c.id} className="py-2 flex justify-between text-sm">
                <span className="text-gray-800">{c.name}</span>
                <span className="text-gray-500">{c.enrolledCount}/{c.maxStudents}</span>
              </li>
            ))}
          </ul>
        </div>

        <div className="bg-white rounded-xl shadow-sm p-5">
          <h2 className="font-semibold text-gray-700 mb-3">Overdue Invoices</h2>
          <ul className="divide-y divide-gray-100">
            {outstanding?.slice(0, 5).map((inv) => (
              <li key={inv.id} className="py-2 flex justify-between text-sm">
                <span className="text-gray-800 truncate">{inv.studentId}</span>
                <span className="text-red-600 font-medium">${inv.amount}</span>
              </li>
            ))}
            {(!outstanding || outstanding.length === 0) && (
              <li className="py-3 text-sm text-gray-400 text-center">No outstanding fees</li>
            )}
          </ul>
        </div>
      </div>
    </div>
  );
}
