import { useAuth } from '@/hooks/useAuth';
import { useGetSlotsQuery, useGetSlotsByTeacherQuery } from './timetableApi';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import type { DayOfWeek } from '@/types/timetable.types';

const DAYS: DayOfWeek[] = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'];

export function TimetableViewPage() {
  const { userId, role } = useAuth();
  const isTeacher = role === 'TEACHER';

  const { data: allSlots, isLoading: la } = useGetSlotsQuery(undefined, { skip: isTeacher });
  const { data: teacherSlots, isLoading: lt } = useGetSlotsByTeacherQuery(userId!, { skip: !isTeacher || !userId });

  const slots = isTeacher ? teacherSlots : allSlots;
  const isLoading = la || lt;

  if (isLoading) return <LoadingSpinner />;

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Timetable</h1>

      <div className="bg-white rounded-xl shadow-sm overflow-x-auto">
        <table className="w-full text-sm border-collapse">
          <thead>
            <tr className="border-b border-gray-200">
              <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase w-28">Day</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Time</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Course</th>
              <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Room</th>
            </tr>
          </thead>
          <tbody>
            {DAYS.map((day) => {
              const daySlots = slots?.filter((s) => s.dayOfWeek === day) ?? [];
              if (daySlots.length === 0) return null;
              return daySlots.map((slot, i) => (
                <tr key={slot.id} className="border-b border-gray-100 hover:bg-gray-50">
                  {i === 0 && (
                    <td rowSpan={daySlots.length} className="px-4 py-3 font-semibold text-gray-700 align-top">
                      {day.slice(0, 3)}
                    </td>
                  )}
                  <td className="px-4 py-3 text-gray-600">{slot.startTime} – {slot.endTime}</td>
                  <td className="px-4 py-3 text-gray-800">{slot.courseId}</td>
                  <td className="px-4 py-3 text-gray-500">{slot.roomName}</td>
                </tr>
              ));
            })}
          </tbody>
        </table>
        {(!slots || slots.length === 0) && (
          <div className="px-4 py-6 text-center text-gray-400">No timetable slots available</div>
        )}
      </div>
    </div>
  );
}
