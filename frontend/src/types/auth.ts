export interface SignupRequest {
  email: string;
  password: string;
  name: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
}

export interface UserResponse {
  id: number;
  email: string;
  name: string;
  role: string;
  createdAt: string;
  modifiedAt: string;
}

export interface ApiResponse<T> {
  status: 'SUCCESS' | 'ERROR';
  data: T | null;
  message: string | null;
}
