import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';
import type { LoginResponse, Role } from '@/types/auth.types';

interface AuthState {
  userId: string | null;
  email: string | null;
  role: Role | null;
  accessToken: string | null;
}

const initialState: AuthState = {
  userId: null,
  email: null,
  role: null,
  accessToken: null,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setCredentials(state, action: PayloadAction<LoginResponse & { email?: string }>) {
      state.userId = action.payload.userId;
      state.role = action.payload.role;
      state.accessToken = action.payload.accessToken;
      state.email = action.payload.email ?? null;
      localStorage.setItem('edu_refresh_token', action.payload.refreshToken);
    },
    logout(state) {
      state.userId = null;
      state.email = null;
      state.role = null;
      state.accessToken = null;
      localStorage.removeItem('edu_refresh_token');
    },
  },
});

export const { setCredentials, logout } = authSlice.actions;
export default authSlice.reducer;
