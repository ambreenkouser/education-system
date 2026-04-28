import { useAuth } from '@/hooks/useAuth';
import { useGetStudentsQuery } from '@/features/students/studentApi';
import { useGetStudentGradesQuery, useGetStudentGpaQuery } from './gradeApi';
import { RoleGuard } from '@/components/common/RoleGuard';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';

export function GradeViewPage() {
  const { userId, role } = useAuth();
  const { data: allStudents } = useGetStudentsQuery();
  const myRecord = allStudents?.find((s) =>
    role === 'STUDENT' ? s.userId === userId : s.parentId === userId
  );

  const { data: grades, isLoading: lg } = useGetStudentGradesQuery(myRecord?.id ?? '', { skip: !myRecord });
  const { data: gpa } = useGetStudentGpaQuery(myRecord?.id ?? '', { skip: !myRecord });

  if (lg) return <LoadingSpinner />;

  const gpaVal = gpa ? Object.values(gpa)[0]?.toFixed(2) : null;

  return (
    <RoleGuard allowedRoles={['STUDENT', 'PARENT']}>
      <div>
        <h1 className="text-2xl font-bold text-gray-800 mb-6">My Grades</h1>

        {gpaVal && (
          <div className="bg-indigo-600 text-white rounded-xl p-5 mb-6 inline-block">
            <p className="text-sm opacity-80">Cumulative GPA</p>
            <p className="text-4xl font-bold mt-1">{gpaVal}</p>
            <p className="text-xs opacity-70 mt-1">out of 4.0</p>
          </div>
        )}

        <div className="bg-white rounded-xl shadow-sm overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 border-b">
              <tr>
                <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Exam</th>
                <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Marks</th>
                <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Grade</th>
                <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Points</th>
                <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Date</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {grades?.map((g) => (
                <tr key={g.id}>
                  <td className="px-4 py-3 font-medium text-gray-800">{g.examTitle}</td>
                  <td className="px-4 py-3 text-gray-600">{g.marksObtained}/{g.totalMarks}</td>
                  <td className="px-4 py-3">
                    <span className="font-bold text-indigo-700">{g.gradeLetter}</span>
                  </td>
                  <td className="px-4 py-3 text-gray-600">{g.gradePoints}</td>
                  <td className="px-4 py-3 text-gray-500">{g.gradedAt?.slice(0, 10)}</td>
                </tr>
              ))}
              {(!grades || grades.length === 0) && (
                <tr><td colSpan={5} className="px-4 py-6 text-center text-gray-400">No grades yet</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </RoleGuard>
  );
}
