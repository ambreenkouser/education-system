import { createApi } from '@reduxjs/toolkit/query/react';
import { baseQueryWithReauth } from '@/app/baseQuery';
import type { TimeSlotRequest, TimeSlotResponse, RoomRequest, Room } from '@/types/timetable.types';

export const timetableApi = createApi({
  reducerPath: 'timetableApi',
  baseQuery: baseQueryWithReauth,
  tagTypes: ['TimeSlot', 'Room'],
  endpoints: (builder) => ({
    getSlots: builder.query<TimeSlotResponse[], void>({
      query: () => '/api/timetable/slots',
      providesTags: ['TimeSlot'],
    }),
    getSlotsByCourse: builder.query<TimeSlotResponse[], string>({
      query: (courseId) => `/api/timetable/slots/course/${courseId}`,
      providesTags: ['TimeSlot'],
    }),
    getSlotsByTeacher: builder.query<TimeSlotResponse[], string>({
      query: (teacherId) => `/api/timetable/slots/teacher/${teacherId}`,
      providesTags: ['TimeSlot'],
    }),
    createSlot: builder.mutation<TimeSlotResponse, TimeSlotRequest>({
      query: (body) => ({ url: '/api/timetable/slots', method: 'POST', body }),
      invalidatesTags: ['TimeSlot'],
    }),
    deleteSlot: builder.mutation<void, string>({
      query: (id) => ({ url: `/api/timetable/slots/${id}`, method: 'DELETE' }),
      invalidatesTags: ['TimeSlot'],
    }),
    getRooms: builder.query<Room[], void>({
      query: () => '/api/timetable/rooms',
      providesTags: ['Room'],
    }),
    createRoom: builder.mutation<Room, RoomRequest>({
      query: (body) => ({ url: '/api/timetable/rooms', method: 'POST', body }),
      invalidatesTags: ['Room'],
    }),
  }),
});

export const {
  useGetSlotsQuery,
  useGetSlotsByCourseQuery,
  useGetSlotsByTeacherQuery,
  useCreateSlotMutation,
  useDeleteSlotMutation,
  useGetRoomsQuery,
  useCreateRoomMutation,
} = timetableApi;
