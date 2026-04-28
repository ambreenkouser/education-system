import { useParams, Link } from 'react-router-dom';
import {
  useGetStudentSummaryQuery,
  useGetStudentGradeSummaryQuery,
  useGetStudentFeeSummaryQuery,
  useLazyDownloadStudentPdfQuery,
} from './reportApi';
import { RoleGuard } from '@/components/common/RoleGuard';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { ErrorMessage } from '@/components/common/ErrorMessage';

export function StudentReportPage() {
  const { id } = useParams<{ id: string }>();
  const { data: summary, isLoading: ls, error } = useGetStudentSummaryQuery(id!);
  const { data: grades } = useGetStudentGradeSummaryQuery(id!);
  const { data: fees } = useGetStudentFeeSummaryQuery(id!);
  const [downloadPdf, { isFetching: downloading }] = useLazyDownloadStudentPdfQuery();

  async function handleDownload() {
    const result = await downloadPdf(id!);
    if (result.data) {
      const url = URL.createObjectURL(result.data as Blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `student-report-${id}.pdf`;
      a.click();
      URL.revokeObjectURL(url);
    }
  }

  return (
    <RoleGuard allowedRoles={['ADMIN']}>
      <div className="max-w-2xl">
        <div className="flex items-center gap-2 mb-6">
          <Link to="/students" className="text-sm text-blue-600 hover:underline">← Students</Link>
        </div>

        {ls && <LoadingSpinner />}
        {error && <ErrorMessage message="Report not found." />}

        {summary && (
          <>
            <div className="bg-white rounded-xl shadow-sm p-6 mb-4">
              <div className="flex items-start justify-between">
                <div>
                  <p className="text-xs text-gray-400 font-semibold uppercase">Student Report</p>
                  <p className="text-2xl font-bold text-gray-800 mt-1">{summary.studentCode}</p>
                  <p className="text-sm text-gray-500 mt-1">Enrollments: {summary.totalEnrollments}</p>
                </div>
                <button
                  onClick={handleDownload}
                  disabled={downloading}
                  className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg text-sm font-medium disabled:opacity-60"
                >
                  {downloading ? 'Generating…' : 'Download PDF'}
                </button>
              </div>
            </div>

            <div className="bg-white rounded-xl shadow-sm p-5 mb-4">
              <h2 className="font-semibold text-gray-700 mb-3">Grade History</h2>
              <ul className="divide-y divide-gray-100">
                {grades?.map((g) => (
                  <li key={g.id} className="py-2 flex justify-between text-sm">
                    <span className="text-gray-800">{g.examTitle}</span>
                    <span className="font-medium text-indigo-700">{g.gradeLetter} ({g.gradePoints})</span>
                  </li>
                ))}
                {(!grades || grades.length === 0) && <li className="py-3 text-sm text-gray-400 text-center">No grades</li>}
              </ul>
            </div>

            <div className="bg-white rounded-xl shadow-sm p-5">
              <h2 className="font-semibold text-gray-700 mb-3">Payment History</h2>
              <ul className="divide-y divide-gray-100">
                {fees?.map((f) => (
                  <li key={f.id} className="py-2 flex justify-between text-sm">
                    <span className="text-gray-800">{f.paymentMethod}</span>
                    <span className="text-green-700 font-medium">${f.paidAmount} — {f.paidAt?.slice(0, 10)}</span>
                  </li>
                ))}
                {(!fees || fees.length === 0) && <li className="py-3 text-sm text-gray-400 text-center">No payments</li>}
              </ul>
            </div>
          </>
        )}
      </div>
    </RoleGuard>
  );
}
