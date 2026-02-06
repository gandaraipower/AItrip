'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { useAuth } from '@/contexts/AuthContext';

export default function SignupPage() {
  const router = useRouter();
  const { signup } = useAuth();
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    passwordConfirm: '',
    name: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const validatePassword = (password: string): boolean => {
    const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,20}$/;
    return regex.test(password);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (formData.password !== formData.passwordConfirm) {
      setError('비밀번호가 일치하지 않습니다.');
      return;
    }

    if (!validatePassword(formData.password)) {
      setError('비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함한 8~20자여야 합니다.');
      return;
    }

    setLoading(true);
    try {
      await signup({
        email: formData.email,
        password: formData.password,
        name: formData.name,
      });
      router.push('/login');
    } catch (err: any) {
      setError(err.response?.data?.message || '회원가입에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h1 style={styles.title}>회원가입</h1>
        <p style={styles.subtitle}>AI Trip에 오신 것을 환영합니다</p>

        <form onSubmit={handleSubmit} style={styles.form}>
          <input
            type="text"
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            placeholder="이름"
            style={styles.input}
            required
          />
          <input
            type="email"
            value={formData.email}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
            placeholder="이메일"
            style={styles.input}
            required
          />
          <input
            type="password"
            value={formData.password}
            onChange={(e) => setFormData({ ...formData, password: e.target.value })}
            placeholder="비밀번호 (대소문자, 숫자, 특수문자 포함 8~20자)"
            style={styles.input}
            required
          />
          <input
            type="password"
            value={formData.passwordConfirm}
            onChange={(e) => setFormData({ ...formData, passwordConfirm: e.target.value })}
            placeholder="비밀번호 확인"
            style={styles.input}
            required
          />
          {error && <p style={styles.error}>{error}</p>}
          <button type="submit" style={styles.button} disabled={loading}>
            {loading ? '가입 중...' : '회원가입'}
          </button>
        </form>

        <p style={styles.linkText}>
          이미 계정이 있으신가요? <Link href="/login">로그인</Link>
        </p>
      </div>
    </div>
  );
}

const styles: { [key: string]: React.CSSProperties } = {
  container: {
    minHeight: '100vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '20px',
  },
  card: {
    background: 'white',
    borderRadius: '16px',
    padding: '40px',
    width: '100%',
    maxWidth: '400px',
    boxShadow: '0 10px 40px rgba(0,0,0,0.2)',
  },
  title: {
    fontSize: '2rem',
    textAlign: 'center',
    color: '#667eea',
    marginBottom: '8px',
  },
  subtitle: {
    textAlign: 'center',
    color: '#666',
    marginBottom: '32px',
  },
  form: {
    display: 'flex',
    flexDirection: 'column',
    gap: '16px',
  },
  input: {
    padding: '14px 16px',
    borderRadius: '8px',
    border: '1px solid #ddd',
    fontSize: '16px',
    outline: 'none',
  },
  button: {
    padding: '14px',
    borderRadius: '8px',
    border: 'none',
    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    color: 'white',
    fontSize: '16px',
    fontWeight: 'bold',
    cursor: 'pointer',
    marginTop: '8px',
  },
  error: {
    color: '#e74c3c',
    fontSize: '14px',
    textAlign: 'center',
  },
  linkText: {
    textAlign: 'center',
    marginTop: '24px',
    color: '#666',
  },
};
