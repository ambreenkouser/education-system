import { useAuth } from '@/hooks/useAuth';
import { useGetStudentsQuery } from '@/features/students/studentApi';
import { useGetStudentInvoicesQuery } from '@/features/fees/feeApi';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';

export function ParentDashboard() {
  const { userId } = useAuth();
  const { data: allStudents, isLoading: ls } = useGetStudentsQuery();
  const myChild = allStudents?.find((s) => s.parentId === userId);

  const { data: invoices, isLoading: lf } = useGetStudentInvoicesQuery(myChild?.id ?? '', { skip: !myChild });

  if (ls || lf) return <LoadingSpinner />;

  const pendingFees = invoices?.filter((i) => i.status !== 'PAID') ?? [];

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Parent Dashboard</h1>

      {!myChild ? (
        <div className="bg-yellow-50 border border-yellow-200 rounded-xl p-6 text-yellow-700 text-sm">
          No child account linked yet. Please contact the school administrator.
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-8">
            <div className="bg-indigo-500 text-white rounded-xl p-5">
              <p className="text-sm opacity-80">Child Student Code</p>
              <p className="text-xl font-bold mt-1">{myChild.studentCode}</p>
            </div>
            <div className="bg-red-500 text-white rounded-xl p-5">
              <p className="text-sm opacity-80">Pending Invoices</p>
              <p className="text-3xl font-bold mt-1">{pendingFees.length}</p>
            </div>
          </div>

          <div className="bg-white rounded-xl shadow-sm p-5">
            <h2 className="font-semibold text-gray-700 mb-3">Fee Summary</h2>
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
        </>
      )}
    </div>
  );
}
