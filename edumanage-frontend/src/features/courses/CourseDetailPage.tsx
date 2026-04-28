import { useParams, Link } from 'react-router-dom';
import { useGetCourseQuery, useDeleteCourseMutation } from './courseApi';
import { useRoleAccess } from '@/hooks/useRoleAccess';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { ErrorMessage } from '@/components/common/ErrorMessage';
import { useNavigate } from 'react-router-dom';

export function CourseDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { data: course, isLoading, error } = useGetCourseQuery(id!);
  const [deleteCourse, { isLoading: deleting }] = useDeleteCourseMutation();
  const { canManageCourses } = useRoleAccess();

  if (isLoading) return <LoadingSpinner />;
  if (error || !course) return <ErrorMessage message="Course not found." />;

  async function handleDelete() {
    if (!confirm('Delete this course?')) return;
    await deleteCourse(course!.id).unwrap();
    navigate('/courses');
  }

  return (
    <div className="max-w-2xl">
      <div className="flex items-center gap-2 mb-6">
        <Link to="/courses" className="text-sm text-blue-600 hover:underline">← Courses</Link>
      </div>

      <div className="bg-white rounded-xl shadow-sm p-6">
        <div className="flex items-start justify-between">
          <div>
            <span className="text-xs font-semibold text-blue-600 bg-blue-50 px-2 py-0.5 rounded">{course.code}</span>
            <h1 className="text-2xl font-bold text-gray-800 mt-2">{course.name}</h1>
            <p className="text-gray-500 mt-2">{course.description}</p>
          </div>
          <span className={`text-xs px-2 py-1 rounded-full font-medium ${
            course.status === 'ACTIVE' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'
          }`}>{course.status}</span>
        </div>

        <div className="grid grid-cols-2 gap-4 mt-6">
          <div className="bg-gray-50 rounded-lg p-3">
            <p className="text-xs text-gray-500">Credits</p>
            <p className="text-lg font-semibold">{course.credits}</p>
          </div>
          <div className="bg-gray-50 rounded-lg p-3">
            <p className="text-xs text-gray-500">Enrollment</p>
            <p className="text-lg font-semibold">{course.enrolledCount} / {course.maxStudents}</p>
          </div>
        </div>

        {canManageCourses && (
          <div className="flex gap-3 mt-6">
            <Link
              to={`/courses/${course.id}/edit`}
              className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium"
            >
              Edit
            </Link>
            <button
              onClick={handleDelete}
              disabled={deleting}
              className="bg-red-100 hover:bg-red-200 text-red-700 px-4 py-2 rounded-lg text-sm font-medium disabled:opacity-60"
            >
              {deleting ? 'Deleting…' : 'Delete'}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
