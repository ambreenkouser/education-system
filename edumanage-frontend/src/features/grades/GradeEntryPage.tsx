import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useSubmitGradeMutation, useGetExamsByCourseQuery } from './gradeApi';
import { useGetCoursesQuery } from '@/features/courses/courseApi';
import { RoleGuard } from '@/components/common/RoleGuard';
import { useState } from 'react';

const schema = z.object({
  studentId:    z.string().uuid('Valid UUID required'),
  examId:       z.string().uuid('Select an exam'),
  marksObtained: z.coerce.number().min(0).max(1000),
});
type FormData = z.infer<typeof schema>;

export function GradeEntryPage() {
  const [selectedCourse, setSelectedCourse] = useState('');
  const { data: courses } = useGetCoursesQuery();
  const { data: exams } = useGetExamsByCourseQuery(selectedCourse, { skip: !selectedCourse });
  const [submitGrade, { isLoading, isSuccess, data: result }] = useSubmitGradeMutation();

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormData>({
    resolver: zodResolver(schema),
  });

  async function onSubmit(data: FormData) {
    await submitGrade(data).unwrap();
    reset({ studentId: '', examId: data.examId, marksObtained: 0 });
  }

  return (
    <RoleGuard allowedRoles={['ADMIN', 'TEACHER']}>
      <div className="max-w-lg">
        <h1 className="text-2xl font-bold text-gray-800 mb-6">Grade Entry</h1>

        <div className="bg-white rounded-xl shadow-sm p-6">
          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Course</label>
            <select
              value={selectedCourse}
              onChange={(e) => setSelectedCourse(e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Select course to load exams…</option>
              {courses?.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
          </div>

          {isSuccess && result && (
            <div className="mb-4 text-sm text-green-700 bg-green-50 border border-green-200 rounded p-3">
              Grade saved: {result.gradeLetter} ({result.gradePoints} pts)
            </div>
          )}

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Exam</label>
              <select {...register('examId')}
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
                <option value="">Select exam…</option>
                {exams?.map((e) => <option key={e.id} value={e.id}>{e.title} ({e.totalMarks} marks)</option>)}
              </select>
              {errors.examId && <p className="text-xs text-red-500 mt-1">{errors.examId.message}</p>}
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Student ID</label>
              <input {...register('studentId')} placeholder="Student UUID"
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
              {errors.studentId && <p className="text-xs text-red-500 mt-1">{errors.studentId.message}</p>}
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Marks Obtained</label>
              <input {...register('marksObtained')} type="number" step="0.5"
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
              {errors.marksObtained && <p className="text-xs text-red-500 mt-1">{errors.marksObtained.message}</p>}
            </div>
            <button type="submit" disabled={isLoading}
              className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 rounded-lg text-sm disabled:opacity-60">
              {isLoading ? 'Saving…' : 'Submit Grade'}
            </button>
          </form>
        </div>
      </div>
    </RoleGuard>
  );
}
