import { useState } from 'react';
import { useAuth } from '@/hooks/useAuth';
import { useGetStudentsQuery } from '@/features/students/studentApi';
import { useGetStudentInvoicesQuery, usePayInvoiceMutation } from './feeApi';
import { RoleGuard } from '@/components/common/RoleGuard';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import type { InvoiceResponse } from '@/types/fee.types';

export function InvoiceListPage() {
  const { userId, role } = useAuth();
  const { data: allStudents } = useGetStudentsQuery();
  const myRecord = allStudents?.find((s) =>
    role === 'STUDENT' ? s.userId === userId : s.parentId === userId
  );
  const { data: invoices, isLoading } = useGetStudentInvoicesQuery(myRecord?.id ?? '', { skip: !myRecord });
  const [payInvoice, { isLoading: paying }] = usePayInvoiceMutation();
  const [payingId, setPayingId] = useState<string | null>(null);

  async function handlePay(inv: InvoiceResponse) {
    setPayingId(inv.id);
    await payInvoice({
      invoiceId: inv.id,
      paidAmount: inv.amount,
      paymentMethod: 'ONLINE',
    }).unwrap();
    setPayingId(null);
  }

  return (
    <RoleGuard allowedRoles={['STUDENT', 'PARENT']}>
      <div>
        <h1 className="text-2xl font-bold text-gray-800 mb-6">My Invoices</h1>
        {isLoading && <LoadingSpinner />}

        <div className="bg-white rounded-xl shadow-sm overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 border-b">
              <tr>
                <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Type</th>
                <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Amount</th>
                <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Due</th>
                <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Status</th>
                <th />
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {invoices?.map((inv) => (
                <tr key={inv.id}>
                  <td className="px-4 py-3 font-medium text-gray-800">{inv.feeType}</td>
                  <td className="px-4 py-3 text-gray-700">${inv.amount}</td>
                  <td className="px-4 py-3 text-gray-500">{inv.dueDate}</td>
                  <td className="px-4 py-3">
                    <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${
                      inv.status === 'PAID'    ? 'bg-green-100 text-green-700' :
                      inv.status === 'OVERDUE' ? 'bg-red-100 text-red-700' :
                                                 'bg-yellow-100 text-yellow-700'
                    }`}>{inv.status}</span>
                  </td>
                  <td className="px-4 py-3">
                    {inv.status !== 'PAID' && (
                      <button
                        onClick={() => handlePay(inv)}
                        disabled={paying && payingId === inv.id}
                        className="text-xs bg-blue-600 hover:bg-blue-700 text-white px-3 py-1 rounded font-medium disabled:opacity-60"
                      >
                        {paying && payingId === inv.id ? 'Paying…' : 'Pay'}
                      </button>
                    )}
                  </td>
                </tr>
              ))}
              {(!invoices || invoices.length === 0) && !isLoading && (
                <tr><td colSpan={5} className="px-4 py-6 text-center text-gray-400">No invoices found</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </RoleGuard>
  );
}
