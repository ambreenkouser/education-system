import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useNavigate, useParams, Link } from 'react-router-dom';
import { useCreateCourseMutation, useUpdateCourseMutation, useGetCourseQuery } from './courseApi';
import { RoleGuard } from '@/components/common/RoleGuard';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';

const schema = z.object({
  code:        z.string().min(1, 'Required').max(20),
  name:        z.string().min(1, 'Required'),
  description: z.string().optional(),
  credits:     z.coerce.number().min(1).max(10),
  maxStudents: z.coerce.number().min(1),
  teacherId:   z.string().optional(),
});
type FormData = z.infer<typeof schema>;

export function CourseFormPage() {
  const { id } = useParams<{ id?: string }>();
  const isEdit = !!id;
  const navigate = useNavigate();
  const { data: existing, isLoading } = useGetCourseQuery(id!, { skip: !isEdit });
  const [createCourse, { isLoading: creating }] = useCreateCourseMutation();
  const [updateCourse, { isLoading: updating }] = useUpdateCourseMutation();

  const { register, handleSubmit, formState: { errors } } = useForm<FormData>({
    resolver: zodResolver(schema),
    values: existing ? {
      code: existing.code, name: existing.name, description: existing.description ?? '',
      credits: existing.credits, maxStudents: existing.maxStudents, teacherId: existing.teacherId ?? '',
    } : undefined,
  });

  if (isEdit && isLoading) return <LoadingSpinner />;

  async function onSubmit(data: FormData) {
    if (isEdit) {
      await updateCourse({ id: id!, body: data }).unwrap();
    } else {
      await createCourse(data).unwrap();
    }
    navigate('/courses');
  }

  return (
    <RoleGuard allowedRoles={['ADMIN']}>
      <div className="max-w-lg">
        <div className="flex items-center gap-2 mb-6">
          <Link to="/courses" className="text-sm text-blue-600 hover:underline">← Courses</Link>
        </div>
        <h1 className="text-2xl font-bold text-gray-800 mb-6">{isEdit ? 'Edit Course' : 'New Course'}</h1>
        <div className="bg-white rounded-xl shadow-sm p-6">
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            {([
              { name: 'code', label: 'Course Code', type: 'text' },
              { name: 'name', label: 'Course Name', type: 'text' },
              { name: 'description', label: 'Description', type: 'text' },
              { name: 'credits', label: 'Credits', type: 'number' },
              { name: 'maxStudents', label: 'Max Students', type: 'number' },
              { name: 'teacherId', label: 'Teacher ID (UUID)', type: 'text' },
            ] as const).map(({ name, label, type }) => (
              <div key={name}>
                <label className="block text-sm font-medium text-gray-700 mb-1">{label}</label>
                <input
                  {...register(name)}
                  type={type}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                {errors[name] && <p className="text-xs text-red-500 mt-1">{errors[name]?.message}</p>}
              </div>
            ))}
            <button
              type="submit"
              disabled={creating || updating}
              className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 rounded-lg text-sm disabled:opacity-60"
            >
              {(creating || updating) ? 'Saving…' : isEdit ? 'Update Course' : 'Create Course'}
            </button>
          </form>
        </div>
      </div>
    </RoleGuard>
  );
}
