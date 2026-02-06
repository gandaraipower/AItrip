# Flutter 인증 가이드

AI Trip API 서버와 연동하기 위한 Flutter 인증 구현 가이드입니다.

## API 서버 정보

- **Base URL**: `http://localhost:8080` (개발 환경)
- **인증 방식**: JWT Bearer Token

---

## 1. 의존성 추가

```yaml
# pubspec.yaml
dependencies:
  dio: ^5.4.0
  flutter_secure_storage: ^9.0.0
```

---

## 2. API 클라이언트 설정

```dart
// lib/core/api/api_client.dart
import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class ApiClient {
  static const String baseUrl = 'http://localhost:8080';
  // Android 에뮬레이터: 'http://10.0.2.2:8080'
  // iOS 시뮬레이터: 'http://localhost:8080'

  final Dio _dio;
  final FlutterSecureStorage _storage = const FlutterSecureStorage();

  ApiClient() : _dio = Dio(BaseOptions(
    baseUrl: baseUrl,
    contentType: 'application/json',
    connectTimeout: const Duration(seconds: 10),
    receiveTimeout: const Duration(seconds: 10),
  )) {
    _dio.interceptors.add(AuthInterceptor(_dio, _storage));
  }

  Dio get dio => _dio;
}
```

---

## 3. 인증 인터셉터

```dart
// lib/core/api/auth_interceptor.dart
import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class AuthInterceptor extends Interceptor {
  final Dio _dio;
  final FlutterSecureStorage _storage;

  AuthInterceptor(this._dio, this._storage);

  @override
  void onRequest(RequestOptions options, RequestInterceptorHandler handler) async {
    // 인증 불필요 경로
    final publicPaths = ['/api/auth/login', '/api/auth/signup', '/api/auth/refresh'];
    if (publicPaths.any((path) => options.path.contains(path))) {
      return handler.next(options);
    }

    final accessToken = await _storage.read(key: 'accessToken');
    if (accessToken != null) {
      options.headers['Authorization'] = 'Bearer $accessToken';
    }
    handler.next(options);
  }

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) async {
    if (err.response?.statusCode == 401) {
      final refreshed = await _refreshToken();
      if (refreshed) {
        // 원래 요청 재시도
        final accessToken = await _storage.read(key: 'accessToken');
        err.requestOptions.headers['Authorization'] = 'Bearer $accessToken';

        final response = await _dio.fetch(err.requestOptions);
        return handler.resolve(response);
      }
    }
    handler.next(err);
  }

  Future<bool> _refreshToken() async {
    try {
      final refreshToken = await _storage.read(key: 'refreshToken');
      if (refreshToken == null) return false;

      final response = await _dio.post(
        '/api/auth/refresh',
        data: {'refreshToken': refreshToken},
      );

      final data = response.data['data'];
      await _storage.write(key: 'accessToken', value: data['accessToken']);
      await _storage.write(key: 'refreshToken', value: data['refreshToken']);
      return true;
    } catch (e) {
      await _storage.deleteAll();
      return false;
    }
  }
}
```

---

## 4. 인증 서비스

```dart
// lib/features/auth/auth_service.dart
import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class AuthService {
  final Dio _dio;
  final FlutterSecureStorage _storage = const FlutterSecureStorage();

  AuthService(this._dio);

  /// 회원가입
  Future<UserResponse> signup({
    required String email,
    required String password,
    required String name,
  }) async {
    final response = await _dio.post('/api/auth/signup', data: {
      'email': email,
      'password': password,  // 대소문자, 숫자, 특수문자 포함 8~20자
      'name': name,
    });
    return UserResponse.fromJson(response.data['data']);
  }

  /// 로그인
  Future<TokenResponse> login({
    required String email,
    required String password,
  }) async {
    final response = await _dio.post('/api/auth/login', data: {
      'email': email,
      'password': password,
    });

    final data = response.data['data'];
    await _storage.write(key: 'accessToken', value: data['accessToken']);
    await _storage.write(key: 'refreshToken', value: data['refreshToken']);

    return TokenResponse.fromJson(data);
  }

  /// 로그아웃
  Future<void> logout() async {
    try {
      await _dio.post('/api/auth/logout');
    } finally {
      await _storage.deleteAll();
    }
  }

  /// 토큰 존재 여부 확인
  Future<bool> isLoggedIn() async {
    final token = await _storage.read(key: 'accessToken');
    return token != null;
  }

  /// 저장된 토큰 가져오기
  Future<String?> getAccessToken() async {
    return await _storage.read(key: 'accessToken');
  }
}
```

---

## 5. 모델 클래스

```dart
// lib/features/auth/models/token_response.dart
class TokenResponse {
  final String accessToken;
  final String refreshToken;

  TokenResponse({required this.accessToken, required this.refreshToken});

  factory TokenResponse.fromJson(Map<String, dynamic> json) {
    return TokenResponse(
      accessToken: json['accessToken'],
      refreshToken: json['refreshToken'],
    );
  }
}

// lib/features/auth/models/user_response.dart
class UserResponse {
  final int id;
  final String email;
  final String name;
  final String role;
  final DateTime createdAt;
  final DateTime modifiedAt;

  UserResponse({
    required this.id,
    required this.email,
    required this.name,
    required this.role,
    required this.createdAt,
    required this.modifiedAt,
  });

  factory UserResponse.fromJson(Map<String, dynamic> json) {
    return UserResponse(
      id: json['id'],
      email: json['email'],
      name: json['name'],
      role: json['role'],
      createdAt: DateTime.parse(json['createdAt']),
      modifiedAt: DateTime.parse(json['modifiedAt']),
    );
  }
}
```

---

## 6. 사용 예시

```dart
// 초기화
final apiClient = ApiClient();
final authService = AuthService(apiClient.dio);

// 회원가입
try {
  final user = await authService.signup(
    email: 'user@example.com',
    password: 'Password1!',  // 대문자, 소문자, 숫자, 특수문자 필수
    name: '홍길동',
  );
  print('가입 완료: ${user.name}');
} on DioException catch (e) {
  print('가입 실패: ${e.response?.data}');
}

// 로그인
try {
  final token = await authService.login(
    email: 'user@example.com',
    password: 'Password1!',
  );
  print('로그인 성공');
} on DioException catch (e) {
  print('로그인 실패: ${e.response?.data}');
}

// 인증 필요한 API 호출 (토큰 자동 첨부)
final response = await apiClient.dio.get('/api/some-protected-endpoint');

// 로그아웃
await authService.logout();
```

---

## 7. API 응답 형식

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

## 8. 비밀번호 규칙

- 8자 이상 20자 이하
- 대문자 최소 1개
- 소문자 최소 1개
- 숫자 최소 1개
- 특수문자(@$!%*?&) 최소 1개

```dart
// 정규식 패턴
final passwordRegex = RegExp(
  r'^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,20}$'
);
```

---

## 9. 인증 불필요 API

다음 API는 토큰 없이 호출 가능합니다:

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | `/api/auth/signup` | 회원가입 |
| POST | `/api/auth/login` | 로그인 |
| POST | `/api/auth/refresh` | 토큰 갱신 |
| GET | `/api/places/**` | 장소 조회 |
| GET | `/api/place-moving-times/**` | 이동시간 조회 |
| GET | `/api/place-crowd-data/**` | 혼잡도 조회 |
