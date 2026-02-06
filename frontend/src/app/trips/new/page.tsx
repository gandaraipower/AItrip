'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { useAuth } from '@/contexts/AuthContext';

type Step = 'info' | 'method' | 'ai' | 'map';

export default function NewTripPage() {
  const { isLoggedIn, isLoading } = useAuth();
  const router = useRouter();
  const [step, setStep] = useState<Step>('info');
  const [tripData, setTripData] = useState({
    title: '',
    startDate: '',
    endDate: '',
    region: '',
    style: '',
    pace: 'normal',
  });

  useEffect(() => {
    if (!isLoading && !isLoggedIn) {
      router.push('/login');
    }
  }, [isLoggedIn, isLoading, router]);

  if (isLoading || !isLoggedIn) {
    return null;
  }

  const handleInfoSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setStep('method');
  };

  const selectMethod = (method: 'ai' | 'map') => {
    setStep(method);
  };

  const renderStep = () => {
    switch (step) {
      case 'info':
        return (
          <form onSubmit={handleInfoSubmit} style={styles.form}>
            <h2>여행 기본 정보</h2>
            <input
              type="text"
              placeholder="여행 제목 (예: 봄 여행)"
              value={tripData.title}
              onChange={(e) => setTripData({ ...tripData, title: e.target.value })}
              style={styles.input}
              required
            />
            <div style={styles.row}>
              <input
                type="date"
                value={tripData.startDate}
                onChange={(e) => setTripData({ ...tripData, startDate: e.target.value })}
                style={styles.input}
                required
              />
              <span style={styles.dateSeparator}>~</span>
              <input
                type="date"
                value={tripData.endDate}
                onChange={(e) => setTripData({ ...tripData, endDate: e.target.value })}
                style={styles.input}
                required
              />
            </div>
            <input
              type="text"
              placeholder="지역 (예: 서울, 부산)"
              value={tripData.region}
              onChange={(e) => setTripData({ ...tripData, region: e.target.value })}
              style={styles.input}
              required
            />
            <input
              type="text"
              placeholder="여행 스타일 (예: 감성적인 카페와 소품샵 위주)"
              value={tripData.style}
              onChange={(e) => setTripData({ ...tripData, style: e.target.value })}
              style={styles.input}
            />
            <select
              value={tripData.pace}
              onChange={(e) => setTripData({ ...tripData, pace: e.target.value })}
              style={styles.input}
            >
              <option value="slow">느긋하게</option>
              <option value="normal">보통</option>
              <option value="fast">알차게</option>
            </select>
            <button type="submit" style={styles.button}>
              다음
            </button>
          </form>
        );

      case 'method':
        return (
          <div style={styles.methodSelect}>
            <h2>여행 일정을 어떻게 만들까요?</h2>
            <div style={styles.methodButtons}>
              <button onClick={() => selectMethod('ai')} style={styles.methodBtn}>
                <span style={styles.methodIcon}>🤖</span>
                <span style={styles.methodTitle}>AI 추천받기</span>
                <span style={styles.methodDesc}>
                  스타일에 맞는 장소와 최적 루트를 자동으로 추천받아요
                </span>
              </button>
              <button onClick={() => selectMethod('map')} style={styles.methodBtn}>
                <span style={styles.methodIcon}>🗺️</span>
                <span style={styles.methodTitle}>지도에서 직접 선택</span>
                <span style={styles.methodDesc}>
                  지도에서 원하는 장소를 직접 골라보세요
                </span>
              </button>
            </div>
            <button onClick={() => setStep('info')} style={styles.backBtn}>
              ← 이전으로
            </button>
          </div>
        );

      case 'ai':
        return (
          <div style={styles.result}>
            <h2>🤖 AI가 추천 중...</h2>
            <div style={styles.loadingBox}>
              <p>"{tripData.region}" 지역의 "{tripData.style || '인기'}" 스타일 장소를 찾고 있어요</p>
              <div style={styles.progressBar}>
                <div style={styles.progress} />
              </div>
            </div>
            <p style={styles.note}>
              * 실제로는 FastAPI AI 서비스와 연동되어 추천 결과가 표시됩니다
            </p>
            <button onClick={() => setStep('method')} style={styles.backBtn}>
              ← 이전으로
            </button>
          </div>
        );

      case 'map':
        return (
          <div style={styles.result}>
            <h2>🗺️ 지도에서 장소 선택</h2>
            <div style={styles.mapPlaceholder}>
              <p>지도가 여기에 표시됩니다</p>
              <p style={styles.note}>* 실제로는 카카오맵/네이버맵이 연동됩니다</p>
            </div>
            <button onClick={() => setStep('method')} style={styles.backBtn}>
              ← 이전으로
            </button>
          </div>
        );
    }
  };

  return (
    <div style={styles.container}>
      <header style={styles.header}>
        <Link href="/" style={styles.backLink}>
          ← 홈으로
        </Link>
        <h1>새 여행 계획</h1>
        <div style={styles.steps}>
          <span style={step === 'info' ? styles.activeStep : styles.step}>정보입력</span>
          <span style={styles.stepArrow}>→</span>
          <span style={step === 'method' ? styles.activeStep : styles.step}>방식선택</span>
          <span style={styles.stepArrow}>→</span>
          <span style={step === 'ai' || step === 'map' ? styles.activeStep : styles.step}>
            일정생성
          </span>
        </div>
      </header>

      <main style={styles.main}>{renderStep()}</main>
    </div>
  );
}

const styles: { [key: string]: React.CSSProperties } = {
  container: {
    minHeight: '100vh',
    color: 'white',
  },
  header: {
    padding: '20px 40px',
    background: 'rgba(0,0,0,0.1)',
  },
  backLink: {
    color: 'white',
    textDecoration: 'none',
    opacity: 0.8,
  },
  steps: {
    display: 'flex',
    gap: '8px',
    marginTop: '12px',
    fontSize: '14px',
  },
  step: {
    opacity: 0.5,
  },
  activeStep: {
    fontWeight: 'bold',
  },
  stepArrow: {
    opacity: 0.3,
  },
  main: {
    padding: '40px',
    maxWidth: '600px',
    margin: '0 auto',
  },
  form: {
    display: 'flex',
    flexDirection: 'column',
    gap: '16px',
    background: 'rgba(255,255,255,0.1)',
    padding: '32px',
    borderRadius: '16px',
  },
  input: {
    padding: '14px 16px',
    borderRadius: '8px',
    border: 'none',
    fontSize: '16px',
    outline: 'none',
    flex: 1,
  },
  row: {
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
  },
  dateSeparator: {
    fontSize: '20px',
  },
  button: {
    padding: '16px',
    borderRadius: '8px',
    border: 'none',
    background: 'white',
    color: '#667eea',
    fontSize: '16px',
    fontWeight: 'bold',
    cursor: 'pointer',
    marginTop: '8px',
  },
  methodSelect: {
    textAlign: 'center',
  },
  methodButtons: {
    display: 'flex',
    flexDirection: 'column',
    gap: '16px',
    marginTop: '32px',
  },
  methodBtn: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    gap: '8px',
    padding: '24px',
    borderRadius: '16px',
    border: '2px solid rgba(255,255,255,0.3)',
    background: 'rgba(255,255,255,0.1)',
    color: 'white',
    cursor: 'pointer',
    transition: 'all 0.2s',
  },
  methodIcon: {
    fontSize: '48px',
  },
  methodTitle: {
    fontSize: '20px',
    fontWeight: 'bold',
  },
  methodDesc: {
    fontSize: '14px',
    opacity: 0.8,
  },
  backBtn: {
    marginTop: '24px',
    padding: '12px 24px',
    borderRadius: '8px',
    border: '1px solid white',
    background: 'transparent',
    color: 'white',
    cursor: 'pointer',
  },
  result: {
    textAlign: 'center',
  },
  loadingBox: {
    background: 'rgba(255,255,255,0.1)',
    padding: '32px',
    borderRadius: '16px',
    marginTop: '24px',
  },
  progressBar: {
    height: '4px',
    background: 'rgba(255,255,255,0.2)',
    borderRadius: '2px',
    marginTop: '16px',
    overflow: 'hidden',
  },
  progress: {
    width: '60%',
    height: '100%',
    background: 'white',
    animation: 'loading 1.5s ease-in-out infinite',
  },
  note: {
    fontSize: '12px',
    opacity: 0.6,
    marginTop: '16px',
  },
  mapPlaceholder: {
    background: 'rgba(255,255,255,0.1)',
    padding: '80px 32px',
    borderRadius: '16px',
    marginTop: '24px',
  },
};
