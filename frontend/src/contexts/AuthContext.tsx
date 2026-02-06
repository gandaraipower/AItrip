'use client';

import { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import { useRouter } from 'next/navigation';
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
  const router = useRouter();

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
    router.push('/login');
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
