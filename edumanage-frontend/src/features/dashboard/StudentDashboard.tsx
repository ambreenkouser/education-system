import { useAuth } from '@/hooks/useAuth';
import { useGetStudentsQuery } from '@/features/students/studentApi';
import { useGetStudentGpaQuery } from '@/features/grades/gradeApi';
import { useGetStudentInvoicesQuery } from '@/features/fees/feeApi';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';

export function StudentDashboard() {
  const { userId } = useAuth();
  const { data: allStudents, isLoading: ls } = useGetStudentsQuery();
  const myRecord = allStudents?.find((s) => s.userId === userId);

  const { data: gpa } = useGetStudentGpaQuery(myRecord?.id ?? '', { skip: !myRecord });
  const { data: invoices, isLoading: lf } = useGetStudentInvoicesQuery(myRecord?.id ?? '', { skip: !myRecord });

  if (ls || lf) return <LoadingSpinner />;

  const pendingFees = invoices?.filter((i) => i.status !== 'PAID') ?? [];
  const gpaValue = gpa ? Object.values(gpa)[0]?.toFixed(2) : 'N/A';

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Student Dashboard</h1>

      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
        <div className="bg-blue-500 text-white rounded-xl p-5">
          <p className="text-sm opacity-80">Student Code</p>
          <p className="text-xl font-bold mt-1">{myRecord?.studentCode ?? '—'}</p>
        </div>
        <div className="bg-green-500 text-white rounded-xl p-5">
          <p className="text-sm opacity-80">GPA</p>
          <p className="text-3xl font-bold mt-1">{gpaValue}</p>
        </div>
        <div className="bg-red-500 text-white rounded-xl p-5">
          <p className="text-sm opacity-80">Pending Invoices</p>
          <p className="text-3xl font-bold mt-1">{pendingFees.length}</p>
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-sm p-5">
        <h2 className="font-semibold text-gray-700 mb-3">Pending Fee Invoices</h2>
        <ul className="divide-y divide-gray-100">
          {pendingFees.slice(0, 5).map((inv) => (
            <li key={inv.id} className="py-2 flex justify-between text-sm">
              <span className="text-gray-800">{inv.feeType}</span>
              <span className={`font-medium ${inv.status === 'OVERDUE' ? 'text-red-600' : 'text-yellow-600'}`}>
                ${inv.amount} — {inv.status}
              </span>
            </li>
          ))}
          {pendingFees.length === 0 && <li className="py-3 text-sm text-gray-400 text-center">All fees paid</li>}
        </ul>
      </div>
    </div>
  );
}
