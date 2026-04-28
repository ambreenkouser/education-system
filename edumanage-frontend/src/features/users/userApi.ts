import { createApi } from '@reduxjs/toolkit/query/react';
import { baseQueryWithReauth } from '@/app/baseQuery';
import type { UserProfileRequest, UserProfileResponse } from '@/types/user.types';

export const userApi = createApi({
  reducerPath: 'userApi',
  baseQuery: baseQueryWithReauth,
  tagTypes: ['UserProfile'],
  endpoints: (builder) => ({
    getMe: builder.query<UserProfileResponse, void>({
      query: () => '/api/users/me',
      providesTags: ['UserProfile'],
    }),
    getUser: builder.query<UserProfileResponse, string>({
      query: (userId) => `/api/users/${userId}`,
      providesTags: ['UserProfile'],
    }),
    updateUser: builder.mutation<UserProfileResponse, { userId: string; body: UserProfileRequest }>({
      query: ({ userId, body }) => ({ url: `/api/users/${userId}`, method: 'PUT', body }),
      invalidatesTags: ['UserProfile'],
    }),
  }),
});

export const { useGetMeQuery, useGetUserQuery, useUpdateUserMutation } = userApi;
