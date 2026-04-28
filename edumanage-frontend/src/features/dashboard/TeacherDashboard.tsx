import { useAuth } from '@/hooks/useAuth';
import { useGetCoursesQuery } from '@/features/courses/courseApi';
import { useGetSlotsByTeacherQuery } from '@/features/timetable/timetableApi';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';

export function TeacherDashboard() {
  const { userId } = useAuth();
  const { data: courses, isLoading: lc } = useGetCoursesQuery();
  const { data: slots, isLoading: ls } = useGetSlotsByTeacherQuery(userId!, { skip: !userId });

  if (lc || ls) return <LoadingSpinner />;

  const myCourses = courses?.filter((c) => c.teacherId === userId) ?? [];
  const today = new Date().toLocaleString('en-US', { weekday: 'long' }).toUpperCase();
  const todaySlots = slots?.filter((s) => s.dayOfWeek === today) ?? [];

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Teacher Dashboard</h1>
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-8">
        <div className="bg-blue-500 text-white rounded-xl p-5">
          <p className="text-sm opacity-80">My Courses</p>
          <p className="text-3xl font-bold mt-1">{myCourses.length}</p>
        </div>
        <div className="bg-green-500 text-white rounded-xl p-5">
          <p className="text-sm opacity-80">Today's Classes</p>
          <p className="text-3xl font-bold mt-1">{todaySlots.length}</p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-xl shadow-sm p-5">
          <h2 className="font-semibold text-gray-700 mb-3">My Courses</h2>
          <ul className="divide-y divide-gray-100">
            {myCourses.map((c) => (
              <li key={c.id} className="py-2 flex justify-between text-sm">
                <span className="text-gray-800">{c.name}</span>
                <span className="text-gray-500">{c.enrolledCount} students</span>
              </li>
            ))}
            {myCourses.length === 0 && <li className="py-3 text-sm text-gray-400 text-center">No assigned courses</li>}
          </ul>
        </div>

        <div className="bg-white rounded-xl shadow-sm p-5">
          <h2 className="font-semibold text-gray-700 mb-3">Today's Schedule</h2>
          <ul className="divide-y divide-gray-100">
            {todaySlots.map((s) => (
              <li key={s.id} className="py-2 text-sm">
                <span className="font-medium text-gray-800">{s.startTime} – {s.endTime}</span>
                <span className="ml-2 text-gray-500">Room {s.roomName}</span>
              </li>
            ))}
            {todaySlots.length === 0 && <li className="py-3 text-sm text-gray-400 text-center">No classes today</li>}
          </ul>
        </div>
      </div>
    </div>
  );
}
