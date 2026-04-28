import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useEnrollStudentMutation } from './studentApi';
import { useGetCoursesQuery } from '@/features/courses/courseApi';
import { RoleGuard } from '@/components/common/RoleGuard';

const schema = z.object({ courseId: z.string().uuid('Select a valid course') });
type FormData = z.infer<typeof schema>;

export function EnrollStudentForm() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { data: courses } = useGetCoursesQuery();
  const [enroll, { isLoading, error }] = useEnrollStudentMutation();

  const { register, handleSubmit, formState: { errors } } = useForm<FormData>({
    resolver: zodResolver(schema),
  });

  async function onSubmit(data: FormData) {
    await enroll({ studentId: id!, body: data }).unwrap();
    navigate(`/students/${id}`);
  }

  const apiError = error && 'data' in error ? (error.data as { message?: string })?.message : undefined;

  return (
    <RoleGuard allowedRoles={['ADMIN']}>
      <div className="max-w-md">
        <div className="flex items-center gap-2 mb-6">
          <Link to={`/students/${id}`} className="text-sm text-blue-600 hover:underline">← Student</Link>
        </div>
        <h1 className="text-2xl font-bold text-gray-800 mb-6">Enroll in Course</h1>

        {apiError && <div className="mb-4 text-sm text-red-600 bg-red-50 border border-red-200 rounded p-3">{apiError}</div>}

        <div className="bg-white rounded-xl shadow-sm p-6">
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Course</label>
              <select
                {...register('courseId')}
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">Select a course…</option>
                {courses?.filter((c) => c.hasCapacity && c.status === 'ACTIVE').map((c) => (
                  <option key={c.id} value={c.id}>{c.name} ({c.code}) — {c.enrolledCount}/{c.maxStudents}</option>
                ))}
              </select>
              {errors.courseId && <p className="text-xs text-red-500 mt-1">{errors.courseId.message}</p>}
            </div>
            <button
              type="submit"
              disabled={isLoading}
              className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 rounded-lg text-sm disabled:opacity-60"
            >
              {isLoading ? 'Enrolling…' : 'Enroll Student'}
            </button>
          </form>
        </div>
      </div>
    </RoleGuard>
  );
}
