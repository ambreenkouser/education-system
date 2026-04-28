import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useCreateSlotMutation, useGetSlotsQuery, useDeleteSlotMutation, useGetRoomsQuery, useCreateRoomMutation } from './timetableApi';
import { useGetCoursesQuery } from '@/features/courses/courseApi';
import { RoleGuard } from '@/components/common/RoleGuard';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import type { DayOfWeek } from '@/types/timetable.types';

const DAYS: DayOfWeek[] = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];

const slotSchema = z.object({
  courseId:  z.string().uuid('Required'),
  teacherId: z.string().uuid('Required'),
  roomId:    z.string().uuid('Required'),
  dayOfWeek: z.enum(['MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY'] as const),
  startTime: z.string().min(1),
  endTime:   z.string().min(1),
});
type SlotForm = z.infer<typeof slotSchema>;

const roomSchema = z.object({
  name:     z.string().min(1, 'Required'),
  capacity: z.coerce.number().min(1),
  building: z.string().optional(),
});
type RoomForm = z.infer<typeof roomSchema>;

export function TimetableManagePage() {
  const { data: courses } = useGetCoursesQuery();
  const { data: rooms } = useGetRoomsQuery();
  const { data: slots, isLoading } = useGetSlotsQuery();
  const [createSlot, { isLoading: cs }] = useCreateSlotMutation();
  const [deleteSlot] = useDeleteSlotMutation();
  const [createRoom, { isLoading: cr }] = useCreateRoomMutation();

  const slotForm = useForm<SlotForm>({ resolver: zodResolver(slotSchema), defaultValues: { dayOfWeek: 'MONDAY' } });
  const roomForm = useForm<RoomForm>({ resolver: zodResolver(roomSchema) });

  async function onSlotSubmit(data: SlotForm) {
    await createSlot(data).unwrap();
    slotForm.reset({ dayOfWeek: 'MONDAY', courseId: '', teacherId: '', roomId: '', startTime: '', endTime: '' });
  }

  async function onRoomSubmit(data: RoomForm) {
    await createRoom(data).unwrap();
    roomForm.reset();
  }

  return (
    <RoleGuard allowedRoles={['ADMIN']}>
      <div>
        <h1 className="text-2xl font-bold text-gray-800 mb-6">Manage Timetable</h1>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
          <div className="bg-white rounded-xl shadow-sm p-5">
            <h2 className="font-semibold text-gray-700 mb-4">Add Time Slot</h2>
            <form onSubmit={slotForm.handleSubmit(onSlotSubmit)} className="space-y-3">
              <select {...slotForm.register('courseId')} className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
                <option value="">Course…</option>
                {courses?.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
              </select>
              <select {...slotForm.register('roomId')} className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
                <option value="">Room…</option>
                {rooms?.map((r) => <option key={r.id} value={r.id}>{r.name}</option>)}
              </select>
              <input {...slotForm.register('teacherId')} placeholder="Teacher UUID" className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
              <select {...slotForm.register('dayOfWeek')} className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm">
                {DAYS.map((d) => <option key={d} value={d}>{d}</option>)}
              </select>
              <div className="flex gap-2">
                <input {...slotForm.register('startTime')} type="time" className="flex-1 border border-gray-300 rounded-lg px-3 py-2 text-sm" />
                <input {...slotForm.register('endTime')} type="time" className="flex-1 border border-gray-300 rounded-lg px-3 py-2 text-sm" />
              </div>
              <button type="submit" disabled={cs} className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2 rounded-lg text-sm font-medium disabled:opacity-60">
                {cs ? 'Adding…' : 'Add Slot'}
              </button>
            </form>
          </div>

          <div className="bg-white rounded-xl shadow-sm p-5">
            <h2 className="font-semibold text-gray-700 mb-4">Add Room</h2>
            <form onSubmit={roomForm.handleSubmit(onRoomSubmit)} className="space-y-3">
              <input {...roomForm.register('name')} placeholder="Room name" className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
              <input {...roomForm.register('capacity')} type="number" placeholder="Capacity" className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
              <input {...roomForm.register('building')} placeholder="Building (optional)" className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
              <button type="submit" disabled={cr} className="w-full bg-green-600 hover:bg-green-700 text-white py-2 rounded-lg text-sm font-medium disabled:opacity-60">
                {cr ? 'Adding…' : 'Add Room'}
              </button>
            </form>
          </div>
        </div>

        {isLoading ? <LoadingSpinner /> : (
          <div className="bg-white rounded-xl shadow-sm overflow-hidden">
            <table className="w-full text-sm">
              <thead className="bg-gray-50 border-b">
                <tr>
                  <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Day</th>
                  <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Time</th>
                  <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase">Room</th>
                  <th />
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {slots?.map((s) => (
                  <tr key={s.id}>
                    <td className="px-4 py-3 text-gray-700">{s.dayOfWeek}</td>
                    <td className="px-4 py-3 text-gray-600">{s.startTime} – {s.endTime}</td>
                    <td className="px-4 py-3 text-gray-500">{s.roomName}</td>
                    <td className="px-4 py-3">
                      <button onClick={() => deleteSlot(s.id)} className="text-xs text-red-500 hover:text-red-700">Delete</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </RoleGuard>
  );
}
