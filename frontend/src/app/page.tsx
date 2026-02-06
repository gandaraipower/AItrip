'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { useAuth } from '@/contexts/AuthContext';

export default function HomePage() {
  const { isLoggedIn, isLoading, logout } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!isLoading && !isLoggedIn) {
      router.push('/login');
    }
  }, [isLoggedIn, isLoading, router]);

  if (isLoading) {
    return (
      <div style={styles.loading}>
        <div style={styles.spinner} />
        <p>로딩 중...</p>
      </div>
    );
  }

  if (!isLoggedIn) {
    return null;
  }

  return (
    <div style={styles.container}>
      <header style={styles.header}>
        <h1 style={styles.logo}>AI Trip</h1>
        <button onClick={logout} style={styles.logoutBtn}>
          로그아웃
        </button>
      </header>

      <main style={styles.main}>
        <div style={styles.welcome}>
          <h2>환영합니다!</h2>
          <p>AI가 당신만을 위한 완벽한 여행을 계획해드립니다.</p>
        </div>

        <div style={styles.actions}>
          <Link href="/trips/new" style={styles.primaryBtn}>
            + 새 여행 계획
          </Link>
        </div>

        <div style={styles.tripList}>
          <h3>내 여행 목록</h3>
          <div style={styles.emptyState}>
            <p>아직 계획된 여행이 없습니다.</p>
            <p>새 여행을 계획해보세요!</p>
          </div>
        </div>
      </main>
    </div>
  );
}

const styles: { [key: string]: React.CSSProperties } = {
  loading: {
    minHeight: '100vh',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    color: 'white',
    gap: '16px',
  },
  spinner: {
    width: '40px',
    height: '40px',
    border: '3px solid rgba(255,255,255,0.3)',
    borderTop: '3px solid white',
    borderRadius: '50%',
    animation: 'spin 1s linear infinite',
  },
  container: {
    minHeight: '100vh',
    color: 'white',
  },
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '20px 40px',
    background: 'rgba(0,0,0,0.1)',
  },
  logo: {
    fontSize: '1.5rem',
    fontWeight: 'bold',
  },
  logoutBtn: {
    padding: '8px 16px',
    borderRadius: '8px',
    border: '1px solid white',
    background: 'transparent',
    color: 'white',
    cursor: 'pointer',
  },
  main: {
    padding: '40px',
    maxWidth: '800px',
    margin: '0 auto',
  },
  welcome: {
    textAlign: 'center',
    marginBottom: '40px',
  },
  actions: {
    display: 'flex',
    justifyContent: 'center',
    marginBottom: '40px',
  },
  primaryBtn: {
    display: 'inline-block',
    padding: '16px 32px',
    borderRadius: '12px',
    background: 'white',
    color: '#667eea',
    fontWeight: 'bold',
    fontSize: '18px',
    textDecoration: 'none',
    boxShadow: '0 4px 20px rgba(0,0,0,0.2)',
  },
  tripList: {
    background: 'rgba(255,255,255,0.1)',
    borderRadius: '16px',
    padding: '24px',
  },
  emptyState: {
    textAlign: 'center',
    padding: '40px',
    opacity: 0.8,
  },
};
