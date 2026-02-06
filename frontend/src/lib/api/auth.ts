import { apiClient } from './client';
import type {
  SignupRequest,
  LoginRequest,
  TokenResponse,
  UserResponse,
  ApiResponse,
} from '@/types/auth';

export const authApi = {
  signup: async (data: SignupRequest): Promise<UserResponse> => {
    const response = await apiClient.post<ApiResponse<UserResponse>>(
      '/api/auth/signup',
      data
    );
    return response.data.data!;
  },

  login: async (data: LoginRequest): Promise<TokenResponse> => {
    const response = await apiClient.post<ApiResponse<TokenResponse>>(
      '/api/auth/login',
      data
    );
    const tokens = response.data.data!;

    localStorage.setItem('accessToken', tokens.accessToken);
    localStorage.setItem('refreshToken', tokens.refreshToken);

    return tokens;
  },

  logout: async (): Promise<void> => {
    try {
      await apiClient.post('/api/auth/logout');
    } finally {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
    }
  },

  isLoggedIn: (): boolean => {
    if (typeof window === 'undefined') return false;
    return !!localStorage.getItem('accessToken');
  },
};
