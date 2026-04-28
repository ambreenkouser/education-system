import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useGetStudentsQuery } from './studentApi';
import { RoleGuard } from '@/components/common/RoleGuard';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { ErrorMessage } from '@/components/common/ErrorMessage';

export function StudentListPage() {
  const [gradeLevel, setGradeLevel] = useState('');
  const { data: students, isLoading, error } = useGetStudentsQuery(gradeLevel ? { gradeLevel } : undefined);

  return (
    <RoleGuard allowedRoles={['ADMIN', 'TEACHER']}>
      <div>
        <h1 className="text-2xl font-bold text-gray-800 mb-6">Students</h1>

        <div className="flex gap-3 mb-4">
          <input
            value={gradeLevel}
            onChange={(e) => setGradeLevel(e.target.value)}
            placeholder="Filter by grade level…"
            className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        {isLoading && <LoadingSpinner />}
        {error && <ErrorMessage message="Failed to load students." />}

        <div className="bg-white rounded-xl shadow-sm overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Code</th>
                <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Grade</th>
                <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Status</th>
                <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Enrolled</th>
                <th />
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {students?.map((s) => (
                <tr key={s.id} className="hover:bg-gray-50">
                  <td className="px-4 py-3 font-medium text-gray-800">{s.studentCode}</td>
                  <td className="px-4 py-3 text-gray-600">{s.gradeLevel}</td>
                  <td className="px-4 py-3">
                    <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${
                      s.status === 'ACTIVE' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'
                    }`}>{s.status}</span>
                  </td>
                  <td className="px-4 py-3 text-gray-600">{s.enrollmentDate?.slice(0, 10)}</td>
                  <td className="px-4 py-3">
                    <Link to={`/students/${s.id}`} className="text-blue-600 hover:underline text-xs">View</Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </RoleGuard>
  );
}
