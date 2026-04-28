import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useCreateExamMutation, useGetExamsByCourseQuery } from './gradeApi';
import { useGetCoursesQuery } from '@/features/courses/courseApi';
import { RoleGuard } from '@/components/common/RoleGuard';
import { useState } from 'react';
import type { ExamType } from '@/types/grade.types';

const EXAM_TYPES: ExamType[] = ['MIDTERM', 'FINAL', 'QUIZ', 'ASSIGNMENT'];

const schema = z.object({
  courseId:  z.string().uuid('Select a course'),
  title:     z.string().min(1, 'Required'),
  examDate:  z.string().min(1, 'Required'),
  totalMarks: z.coerce.number().positive(),
  type:      z.enum(['MIDTERM', 'FINAL', 'QUIZ', 'ASSIGNMENT'] as const),
});
type FormData = z.infer<typeof schema>;

export function ExamManagePage() {
  const [selectedCourse, setSelectedCourse] = useState('');
  const { data: courses } = useGetCoursesQuery();
  const { data: exams } = useGetExamsByCourseQuery(selectedCourse, { skip: !selectedCourse });
  const [createExam, { isLoading, isSuccess }] = useCreateExamMutation();

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: { type: 'QUIZ' },
  });

  async function onSubmit(data: FormData) {
    await createExam(data).unwrap();
    setSelectedCourse(data.courseId);
    reset({ ...data, title: '', examDate: '' });
  }

  return (
    <RoleGuard allowedRoles={['ADMIN', 'TEACHER']}>
      <div className="max-w-2xl">
        <h1 className="text-2xl font-bold text-gray-800 mb-6">Exam Management</h1>

        <div className="bg-white rounded-xl shadow-sm p-6 mb-6">
          <h2 className="font-semibold text-gray-700 mb-4">Create Exam</h2>
          {isSuccess && <div className="mb-4 text-sm text-green-700 bg-green-50 border border-green-200 rounded p-3">Exam created.</div>}
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Course</label>
              <select
                {...register('courseId')}
                onChange={(e) => setSelectedCourse(e.target.value)}
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">Select course…</option>
                {courses?.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
              </select>
              {errors.courseId && <p className="text-xs text-red-500 mt-1">{errors.courseId.message}</p>}
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Title</label>
                <input {...register('title')} className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
                {errors.title && <p className="text-xs text-red-500 mt-1">{errors.title.message}</p>}
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Type</label>
                <select {...register('type')} className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
                  {EXAM_TYPES.map((t) => <option key={t} value={t}>{t}</option>)}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Date</label>
                <input {...register('examDate')} type="date" className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Total Marks</label>
                <input {...register('totalMarks')} type="number" className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
              </div>
            </div>

            <button type="submit" disabled={isLoading}
              className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium disabled:opacity-60">
              {isLoading ? 'Creating…' : 'Create Exam'}
            </button>
          </form>
        </div>

        {selectedCourse && exams && (
          <div className="bg-white rounded-xl shadow-sm p-5">
            <h2 className="font-semibold text-gray-700 mb-3">Exams for Selected Course</h2>
            <ul className="divide-y divide-gray-100">
              {exams.map((e) => (
                <li key={e.id} className="py-2 flex justify-between text-sm">
                  <span className="font-medium text-gray-800">{e.title}</span>
                  <span className="text-gray-500">{e.type} · {e.examDate} · {e.totalMarks} marks</span>
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>
    </RoleGuard>
  );
}
