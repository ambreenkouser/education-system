import { createApi } from '@reduxjs/toolkit/query/react';
import { baseQueryWithReauth } from '@/app/baseQuery';
import type { CourseRequest, CourseResponse, EligibilityResponse } from '@/types/course.types';

export const courseApi = createApi({
  reducerPath: 'courseApi',
  baseQuery: baseQueryWithReauth,
  tagTypes: ['Course'],
  endpoints: (builder) => ({
    getCourses: builder.query<CourseResponse[], void>({
      query: () => '/api/courses',
      providesTags: ['Course'],
    }),
    getAvailableCourses: builder.query<CourseResponse[], void>({
      query: () => '/api/courses/available',
      providesTags: ['Course'],
    }),
    getCourse: builder.query<CourseResponse, string>({
      query: (id) => `/api/courses/${id}`,
      providesTags: ['Course'],
    }),
    createCourse: builder.mutation<CourseResponse, CourseRequest>({
      query: (body) => ({ url: '/api/courses', method: 'POST', body }),
      invalidatesTags: ['Course'],
    }),
    updateCourse: builder.mutation<CourseResponse, { id: string; body: CourseRequest }>({
      query: ({ id, body }) => ({ url: `/api/courses/${id}`, method: 'PUT', body }),
      invalidatesTags: ['Course'],
    }),
    deleteCourse: builder.mutation<void, string>({
      query: (id) => ({ url: `/api/courses/${id}`, method: 'DELETE' }),
      invalidatesTags: ['Course'],
    }),
    checkEligibility: builder.query<EligibilityResponse, { courseId: string; studentId: string }>({
      query: ({ courseId, studentId }) => `/api/courses/${courseId}/eligibility?studentId=${studentId}`,
    }),
  }),
});

export const {
  useGetCoursesQuery,
  useGetAvailableCoursesQuery,
  useGetCourseQuery,
  useCreateCourseMutation,
  useUpdateCourseMutation,
  useDeleteCourseMutation,
  useCheckEligibilityQuery,
} = courseApi;
