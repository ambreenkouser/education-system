import { createApi } from '@reduxjs/toolkit/query/react';
import { baseQueryWithReauth } from '@/app/baseQuery';
import type { StudentRequest, StudentResponse, EnrollmentRequest, EnrollmentResponse } from '@/types/student.types';

export const studentApi = createApi({
  reducerPath: 'studentApi',
  baseQuery: baseQueryWithReauth,
  tagTypes: ['Student', 'Enrollment'],
  endpoints: (builder) => ({
    getStudents: builder.query<StudentResponse[], { gradeLevel?: string } | void>({
      query: (params) => ({
        url: '/api/students',
        params: params ? { gradeLevel: (params as { gradeLevel?: string }).gradeLevel } : undefined,
      }),
      providesTags: ['Student'],
    }),
    getStudent: builder.query<StudentResponse, string>({
      query: (id) => `/api/students/${id}`,
      providesTags: ['Student'],
    }),
    createStudent: builder.mutation<StudentResponse, StudentRequest>({
      query: (body) => ({ url: '/api/students', method: 'POST', body }),
      invalidatesTags: ['Student'],
    }),
    enrollStudent: builder.mutation<EnrollmentResponse, { studentId: string; body: EnrollmentRequest }>({
      query: ({ studentId, body }) => ({ url: `/api/students/${studentId}/enroll`, method: 'POST', body }),
      invalidatesTags: ['Enrollment'],
    }),
    getEnrollments: builder.query<EnrollmentResponse[], string>({
      query: (studentId) => `/api/students/${studentId}/enrollments`,
      providesTags: ['Enrollment'],
    }),
  }),
});

export const {
  useGetStudentsQuery,
  useGetStudentQuery,
  useCreateStudentMutation,
  useEnrollStudentMutation,
  useGetEnrollmentsQuery,
} = studentApi;
