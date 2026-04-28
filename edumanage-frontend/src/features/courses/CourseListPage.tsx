import { Link } from 'react-router-dom';
import { useGetCoursesQuery } from './courseApi';
import { useRoleAccess } from '@/hooks/useRoleAccess';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { ErrorMessage } from '@/components/common/ErrorMessage';

export function CourseListPage() {
  const { data: courses, isLoading, error } = useGetCoursesQuery();
  const { canManageCourses } = useRoleAccess();

  if (isLoading) return <LoadingSpinner />;
  if (error) return <ErrorMessage message="Failed to load courses." />;

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Courses</h1>
        {canManageCourses && (
          <Link to="/courses/new" className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium">
            + New Course
          </Link>
        )}
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {courses?.map((course) => (
          <Link key={course.id} to={`/courses/${course.id}`} className="bg-white rounded-xl shadow-sm p-5 hover:shadow-md transition-shadow">
            <div className="flex items-start justify-between">
              <div>
                <span className="text-xs font-semibold text-blue-600 bg-blue-50 px-2 py-0.5 rounded">{course.code}</span>
                <h3 className="font-semibold text-gray-800 mt-2">{course.name}</h3>
                <p className="text-sm text-gray-500 mt-1 line-clamp-2">{course.description}</p>
              </div>
            </div>
            <div className="mt-4 flex items-center justify-between text-sm text-gray-500">
              <span>{course.credits} credits</span>
              <span className={course.hasCapacity ? 'text-green-600' : 'text-red-500'}>
                {course.enrolledCount}/{course.maxStudents}
              </span>
            </div>
            <div className="mt-2">
              <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${
                course.status === 'ACTIVE' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'
              }`}>{course.status}</span>
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
}
