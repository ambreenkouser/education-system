import { useGetOutstandingInvoicesQuery } from './feeApi';
import { RoleGuard } from '@/components/common/RoleGuard';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';

export function OutstandingFeesPage() {
  const { data: invoices, isLoading } = useGetOutstandingInvoicesQuery();

  return (
    <RoleGuard allowedRoles={['ADMIN']}>
      <div>
        <h1 className="text-2xl font-bold text-gray-800 mb-6">Outstanding Fees</h1>
        {isLoading && <LoadingSpinner />}

        <div className="bg-white rounded-xl shadow-sm overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 border-b">
              <tr>
                <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Student</th>
                <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Type</th>
                <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Amount</th>
                <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Due Date</th>
                <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {invoices?.map((inv) => (
                <tr key={inv.id}>
                  <td className="px-4 py-3 text-gray-600 truncate max-w-[140px]">{inv.studentId}</td>
                  <td className="px-4 py-3 text-gray-700">{inv.feeType}</td>
                  <td className="px-4 py-3 font-semibold text-gray-800">${inv.amount}</td>
                  <td className="px-4 py-3 text-gray-500">{inv.dueDate}</td>
                  <td className="px-4 py-3">
                    <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${
                      inv.status === 'OVERDUE' ? 'bg-red-100 text-red-700' : 'bg-yellow-100 text-yellow-700'
                    }`}>{inv.status}</span>
                  </td>
                </tr>
              ))}
              {(!invoices || invoices.length === 0) && !isLoading && (
                <tr><td colSpan={5} className="px-4 py-6 text-center text-gray-400">No outstanding invoices</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </RoleGuard>
  );
}
