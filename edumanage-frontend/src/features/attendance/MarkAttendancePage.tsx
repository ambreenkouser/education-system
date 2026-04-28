import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useGetCoursesQuery } from '@/features/courses/courseApi';
import { useMarkAttendanceMutation, useGetCourseAttendanceByDateQuery } from './attendanceApi';
import { RoleGuard } from '@/components/common/RoleGuard';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import type { AttendanceStatus } from '@/types/attendance.types';

const schema = z.object({
  studentId:      z.string().uuid('Required'),
  courseId:       z.string().uuid('Required'),
  attendanceDate: z.string().min(1, 'Required'),
  status:         z.enum(['PRESENT', 'ABSENT', 'LATE'] as const),
  remarks:        z.string().optional(),
});
type FormData = z.infer<typeof schema>;

const STATUSES: AttendanceStatus[] = ['PRESENT', 'ABSENT', 'LATE'];

export function MarkAttendancePage() {
  const [courseId, setCourseId] = useState('');
  const [date, setDate] = useState(new Date().toISOString().slice(0, 10));
  const { data: courses } = useGetCoursesQuery();
  const { data: existing, isLoading: le } = useGetCourseAttendanceByDateQuery(
    { courseId, date }, { skip: !courseId || !date }
  );
  const [mark, { isLoading: marking, isSuccess }] = useMarkAttendanceMutation();

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: { status: 'PRESENT', attendanceDate: date, courseId },
  });

  async function onSubmit(data: FormData) {
    await mark(data).unwrap();
    reset({ ...data, studentId: '' });
  }

  return (
    <RoleGuard allowedRoles={['ADMIN', 'TEACHER']}>
      <div className="max-w-2xl">
        <h1 className="text-2xl font-bold text-gray-800 mb-6">Mark Attendance</h1>

        <div className="bg-white rounded-xl shadow-sm p-6 mb-6">
          <div className="flex gap-4 mb-6">
            <div className="flex-1">
              <label className="block text-sm font-medium text-gray-700 mb-1">Course</label>
              <select
                value={courseId}
                onChange={(e) => setCourseId(e.target.value)}
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">Select course…</option>
                {courses?.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Date</label>
              <input
                type="date"
                value={date}
                onChange={(e) => setDate(e.target.value)}
                className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          {courseId && (
            <>
              <div className="mb-4 text-sm text-gray-500">
                {le ? <LoadingSpinner /> : <span>{existing?.length ?? 0} records already marked for this date</span>}
              </div>

              {isSuccess && <div className="mb-4 text-sm text-green-700 bg-green-50 border border-green-200 rounded p-3">Attendance marked.</div>}

              <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                <input type="hidden" {...register('courseId')} value={courseId} />
                <input type="hidden" {...register('attendanceDate')} value={date} />

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Student ID</label>
                  <input
                    {...register('studentId')}
                    placeholder="Student UUID"
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                  {errors.studentId && <p className="text-xs text-red-500 mt-1">{errors.studentId.message}</p>}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Status</label>
                  <div className="flex gap-3">
                    {STATUSES.map((s) => (
                      <label key={s} className="flex items-center gap-1 text-sm cursor-pointer">
                        <input {...register('status')} type="radio" value={s} /> {s}
                      </label>
                    ))}
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Remarks</label>
                  <input
                    {...register('remarks')}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>

                <button
                  type="submit"
                  disabled={marking}
                  className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium disabled:opacity-60"
                >
                  {marking ? 'Saving…' : 'Mark'}
                </button>
              </form>
            </>
          )}
        </div>
      </div>
    </RoleGuard>
  );
}
