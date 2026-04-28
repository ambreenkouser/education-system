import { createApi } from '@reduxjs/toolkit/query/react';
import { baseQueryWithReauth } from '@/app/baseQuery';
import type { StudentSummary, AttendanceReportSummary, GradeSummary, FeeSummary } from '@/types/report.types';

export const reportApi = createApi({
  reducerPath: 'reportApi',
  baseQuery: baseQueryWithReauth,
  tagTypes: ['Report'],
  endpoints: (builder) => ({
    getStudentSummary: builder.query<StudentSummary, string>({
      query: (studentId) => `/api/reports/students/${studentId}/summary`,
      providesTags: ['Report'],
    }),
    getCourseAttendanceSummary: builder.query<AttendanceReportSummary[], string>({
      query: (courseId) => `/api/reports/attendance/course/${courseId}`,
      providesTags: ['Report'],
    }),
    getStudentGradeSummary: builder.query<GradeSummary[], string>({
      query: (studentId) => `/api/reports/grades/student/${studentId}`,
      providesTags: ['Report'],
    }),
    getStudentFeeSummary: builder.query<FeeSummary[], string>({
      query: (studentId) => `/api/reports/fees/student/${studentId}`,
      providesTags: ['Report'],
    }),
    downloadStudentPdf: builder.query<Blob, string>({
      query: (studentId) => ({
        url: `/api/reports/students/${studentId}/pdf`,
        responseHandler: (response) => response.blob(),
      }),
    }),
  }),
});

export const {
  useGetStudentSummaryQuery,
  useGetCourseAttendanceSummaryQuery,
  useGetStudentGradeSummaryQuery,
  useGetStudentFeeSummaryQuery,
  useLazyDownloadStudentPdfQuery,
} = reportApi;
