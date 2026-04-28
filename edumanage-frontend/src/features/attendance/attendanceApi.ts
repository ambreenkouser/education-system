import { createApi } from '@reduxjs/toolkit/query/react';
import { baseQueryWithReauth } from '@/app/baseQuery';
import type { AttendanceRequest, AttendanceResponse, AttendanceSummary } from '@/types/attendance.types';

export const attendanceApi = createApi({
  reducerPath: 'attendanceApi',
  baseQuery: baseQueryWithReauth,
  tagTypes: ['Attendance'],
  endpoints: (builder) => ({
    markAttendance: builder.mutation<AttendanceResponse, AttendanceRequest>({
      query: (body) => ({ url: '/api/attendance/mark', method: 'POST', body }),
      invalidatesTags: ['Attendance'],
    }),
    getStudentAttendance: builder.query<AttendanceResponse[], string>({
      query: (studentId) => `/api/attendance/student/${studentId}`,
      providesTags: ['Attendance'],
    }),
    getCourseAttendanceByDate: builder.query<AttendanceResponse[], { courseId: string; date: string }>({
      query: ({ courseId, date }) => `/api/attendance/course/${courseId}/date/${date}`,
      providesTags: ['Attendance'],
    }),
    getAttendanceSummary: builder.query<AttendanceSummary, { studentId: string; courseId: string }>({
      query: ({ studentId, courseId }) => `/api/attendance/student/${studentId}/course/${courseId}/summary`,
      providesTags: ['Attendance'],
    }),
  }),
});

export const {
  useMarkAttendanceMutation,
  useGetStudentAttendanceQuery,
  useGetCourseAttendanceByDateQuery,
  useGetAttendanceSummaryQuery,
} = attendanceApi;
