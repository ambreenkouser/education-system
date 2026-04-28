import { configureStore } from '@reduxjs/toolkit';
import authReducer from '@/features/auth/authSlice';
import { authApi } from '@/features/auth/authApi';
import { userApi } from '@/features/users/userApi';
import { studentApi } from '@/features/students/studentApi';
import { courseApi } from '@/features/courses/courseApi';
import { attendanceApi } from '@/features/attendance/attendanceApi';
import { gradeApi } from '@/features/grades/gradeApi';
import { feeApi } from '@/features/fees/feeApi';
import { timetableApi } from '@/features/timetable/timetableApi';
import { reportApi } from '@/features/reports/reportApi';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    [authApi.reducerPath]: authApi.reducer,
    [userApi.reducerPath]: userApi.reducer,
    [studentApi.reducerPath]: studentApi.reducer,
    [courseApi.reducerPath]: courseApi.reducer,
    [attendanceApi.reducerPath]: attendanceApi.reducer,
    [gradeApi.reducerPath]: gradeApi.reducer,
    [feeApi.reducerPath]: feeApi.reducer,
    [timetableApi.reducerPath]: timetableApi.reducer,
    [reportApi.reducerPath]: reportApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(
      authApi.middleware,
      userApi.middleware,
      studentApi.middleware,
      courseApi.middleware,
      attendanceApi.middleware,
      gradeApi.middleware,
      feeApi.middleware,
      timetableApi.middleware,
      reportApi.middleware
    ),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
