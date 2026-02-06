# Next.js 인증 가이드

AI Trip API 서버와 연동하기 위한 Next.js (App Router) 인증 구현 가이드입니다.

## API 서버 정보

- **Base URL**: `http://localhost:8080` (개발 환경)
- **인증 방식**: JWT Bearer Token
- **CORS**: `localhost:3000` 허용됨

---

## 1. 의존성 설치

```bash
npm install axios
# 또는
pnpm add axios
```

---

## 2. 환경 변수 설정

```env
# .env.local
NEXT_PUBLIC_API_URL=http://localhost:8080
```

---

## 3. 타입 정의

```typescript
// types/auth.ts
export interface SignupRequest {
  email: string;
  password: string;  // 대소문자, 숫자, 특수문자 포함 8~20자
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
```

---

## 4. API 클라이언트

```typescript
// lib/api/client.ts
import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export const apiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

// 인증 불필요 경로
const PUBLIC_PATHS = ['/api/auth/login', '/api/auth/signup', '/api/auth/refresh'];

// Request Interceptor
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const isPublicPath = PUBLIC_PATHS.some(path => config.url?.includes(path));

    if (!isPublicPath && typeof window !== 'undefined') {
      const accessToken = localStorage.getItem('accessToken');
      if (accessToken) {
        config.headers.Authorization = `Bearer ${accessToken}`;
      }
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor - 토큰 갱신
apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && originalRequest && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) throw new Error('No refresh token');

        const response = await axios.post(`${API_URL}/api/auth/refresh`, {
          refreshToken,
        });

        const { accessToken, refreshToken: newRefreshToken } = response.data.data;
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', newRefreshToken);

        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return apiClient(originalRequest);
      } catch (refreshError) {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);

// axios 타입 확장
declare module 'axios' {
  export interface InternalAxiosRequestConfig {
    _retry?: boolean;
  }
}
```

---

## 5. 인증 API 함수

```typescript
// lib/api/auth.ts
import { apiClient } from './client';
import type {
  SignupRequest,
  LoginRequest,
  TokenResponse,
  UserResponse,
  ApiResponse,
} from '@/types/auth';

export const authApi = {
  /** 회원가입 */
  signup: async (data: SignupRequest): Promise<UserResponse> => {
    const response = await apiClient.post<ApiResponse<UserResponse>>(
      '/api/auth/signup',
      data
    );
    return response.data.data!;
  },

  /** 로그인 */
  login: async (data: LoginRequest): Promise<TokenResponse> => {
    const response = await apiClient.post<ApiResponse<TokenResponse>>(
      '/api/auth/login',
      data
    );
    const tokens = response.data.data!;

    // 토큰 저장
    localStorage.setItem('accessToken', tokens.accessToken);
    localStorage.setItem('refreshToken', tokens.refreshToken);

    return tokens;
  },

  /** 로그아웃 */
  logout: async (): Promise<void> => {
    try {
      await apiClient.post('/api/auth/logout');
    } finally {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
    }
  },

  /** 토큰 갱신 */
  refresh: async (refreshToken: string): Promise<TokenResponse> => {
    const response = await apiClient.post<ApiResponse<TokenResponse>>(
      '/api/auth/refresh',
      { refreshToken }
    );
    return response.data.data!;
  },

  /** 로그인 상태 확인 */
  isLoggedIn: (): boolean => {
    if (typeof window === 'undefined') return false;
    return !!localStorage.getItem('accessToken');
  },
};
```

---

## 6. Auth Context (상태 관리)

```typescript
// contexts/AuthContext.tsx
'use client';

import { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import { authApi } from '@/lib/api/auth';
import type { LoginRequest, SignupRequest, UserResponse } from '@/types/auth';

interface AuthContextType {
  isLoggedIn: boolean;
  isLoading: boolean;
  login: (data: LoginRequest) => Promise<void>;
  signup: (data: SignupRequest) => Promise<UserResponse>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    setIsLoggedIn(authApi.isLoggedIn());
    setIsLoading(false);
  }, []);

  const login = async (data: LoginRequest) => {
    await authApi.login(data);
    setIsLoggedIn(true);
  };

  const signup = async (data: SignupRequest) => {
    return await authApi.signup(data);
  };

  const logout = async () => {
    await authApi.logout();
    setIsLoggedIn(false);
  };

  return (
    <AuthContext.Provider value={{ isLoggedIn, isLoading, login, signup, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
```

---

## 7. 레이아웃에 Provider 적용

```typescript
// app/layout.tsx
import { AuthProvider } from '@/contexts/AuthContext';

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="ko">
      <body>
        <AuthProvider>
          {children}
        </AuthProvider>
      </body>
    </html>
  );
}
```

---

## 8. 로그인 페이지 예시

```typescript
// app/login/page.tsx
'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/contexts/AuthContext';

export default function LoginPage() {
  const router = useRouter();
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    try {
      await login({ email, password });
      router.push('/');
    } catch (err: any) {
      setError(err.response?.data?.message || '로그인에 실패했습니다.');
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        placeholder="이메일"
        required
      />
      <input
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        placeholder="비밀번호"
        required
      />
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <button type="submit">로그인</button>
    </form>
  );
}
```

---

## 9. 회원가입 페이지 예시

```typescript
// app/signup/page.tsx
'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/contexts/AuthContext';

export default function SignupPage() {
  const router = useRouter();
  const { signup } = useAuth();
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    name: '',
  });
  const [error, setError] = useState('');

  // 비밀번호 유효성 검사
  const validatePassword = (password: string): boolean => {
    const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,20}$/;
    return regex.test(password);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!validatePassword(formData.password)) {
      setError('비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함한 8~20자여야 합니다.');
      return;
    }

    try {
      await signup(formData);
      router.push('/login');
    } catch (err: any) {
      setError(err.response?.data?.message || '회원가입에 실패했습니다.');
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="email"
        value={formData.email}
        onChange={(e) => setFormData({ ...formData, email: e.target.value })}
        placeholder="이메일"
        required
      />
      <input
        type="password"
        value={formData.password}
        onChange={(e) => setFormData({ ...formData, password: e.target.value })}
        placeholder="비밀번호 (대소문자, 숫자, 특수문자 포함 8~20자)"
        required
      />
      <input
        type="text"
        value={formData.name}
        onChange={(e) => setFormData({ ...formData, name: e.target.value })}
        placeholder="이름"
        required
      />
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <button type="submit">회원가입</button>
    </form>
  );
}
```

---

## 10. 인증 필요 페이지 보호

```typescript
// components/ProtectedRoute.tsx
'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/contexts/AuthContext';

export default function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isLoggedIn, isLoading } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!isLoading && !isLoggedIn) {
      router.push('/login');
    }
  }, [isLoggedIn, isLoading, router]);

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (!isLoggedIn) {
    return null;
  }

  return <>{children}</>;
}

// 사용 예시
// app/dashboard/page.tsx
import ProtectedRoute from '@/components/ProtectedRoute';

export default function DashboardPage() {
  return (
    <ProtectedRoute>
      <div>인증된 사용자만 볼 수 있는 페이지</div>
    </ProtectedRoute>
  );
}
```

---

## 11. API 응답 형식

모든 API 응답은 다음 형식을 따릅니다:

```json
{
  "status": "SUCCESS",
  "data": { ... },
  "message": null
}
```

에러 응답:
```json
{
  "status": "ERROR",
  "data": null,
  "message": "에러 메시지"
}
```

---

## 12. 비밀번호 규칙

- 8자 이상 20자 이하
- 대문자 최소 1개
- 소문자 최소 1개
- 숫자 최소 1개
- 특수문자(@$!%*?&) 최소 1개

```typescript
const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,20}$/;
```

---

## 13. 인증 불필요 API

다음 API는 토큰 없이 호출 가능합니다:

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | `/api/auth/signup` | 회원가입 |
| POST | `/api/auth/login` | 로그인 |
| POST | `/api/auth/refresh` | 토큰 갱신 |
| GET | `/api/places/**` | 장소 조회 |
| GET | `/api/place-moving-times/**` | 이동시간 조회 |
| GET | `/api/place-crowd-data/**` | 혼잡도 조회 |

---

## 14. Middleware로 보호 (선택사항)

```typescript
// middleware.ts
import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

const protectedPaths = ['/dashboard', '/profile', '/settings'];

export function middleware(request: NextRequest) {
  const token = request.cookies.get('accessToken')?.value;
  const isProtectedPath = protectedPaths.some(path =>
    request.nextUrl.pathname.startsWith(path)
  );

  if (isProtectedPath && !token) {
    return NextResponse.redirect(new URL('/login', request.url));
  }

  return NextResponse.next();
}

export const config = {
  matcher: ['/dashboard/:path*', '/profile/:path*', '/settings/:path*'],
};
```

> **참고**: Middleware에서 쿠키 기반 인증을 사용하려면 로그인 시 쿠키에도 토큰을 저장해야 합니다.
