import { createApi } from '@reduxjs/toolkit/query/react';
import { baseQueryWithReauth } from '@/app/baseQuery';
import type { ExamRequest, Exam, GradeRequest, GradeResponse } from '@/types/grade.types';

export const gradeApi = createApi({
  reducerPath: 'gradeApi',
  baseQuery: baseQueryWithReauth,
  tagTypes: ['Exam', 'Grade'],
  endpoints: (builder) => ({
    createExam: builder.mutation<Exam, ExamRequest>({
      query: (body) => ({ url: '/api/grades/exams', method: 'POST', body }),
      invalidatesTags: ['Exam'],
    }),
    getExamsByCourse: builder.query<Exam[], string>({
      query: (courseId) => `/api/grades/exams/course/${courseId}`,
      providesTags: ['Exam'],
    }),
    submitGrade: builder.mutation<GradeResponse, GradeRequest>({
      query: (body) => ({ url: '/api/grades', method: 'POST', body }),
      invalidatesTags: ['Grade'],
    }),
    getStudentGrades: builder.query<GradeResponse[], string>({
      query: (studentId) => `/api/grades/student/${studentId}`,
      providesTags: ['Grade'],
    }),
    getStudentGpa: builder.query<Record<string, number>, string>({
      query: (studentId) => `/api/grades/student/${studentId}/gpa`,
      providesTags: ['Grade'],
    }),
  }),
});

export const {
  useCreateExamMutation,
  useGetExamsByCourseQuery,
  useSubmitGradeMutation,
  useGetStudentGradesQuery,
  useGetStudentGpaQuery,
} = gradeApi;
