---
name: spring-api-rules
description: Spring Boot REST API 개발 표준. 컨트롤러, 서비스, 엔티티, DTO, 인증 등 아키텍처 규칙 및 주석 가이드.
allowed-tools: Read, Write, Edit, Glob, Grep, Bash, LSP
---

# Spring Boot API 개발 표준 가이드

Spring Boot REST API 프로젝트의 공통 개발 규칙입니다.

---

## 1. 핵심 공통 규칙

1. **의존성 주입:** `@RequiredArgsConstructor` 생성자 주입 사용. `@Autowired` 금지.
2. **불변성:** DTO는 Java `record` 사용.
3. **Lombok:** `@Getter` 사용. 엔티티 `@Setter` 금지 → 비즈니스 메서드로 상태 변경.
4. **주석:** Public 메서드(Controller, Service) 위에 한 줄 기능 설명 작성.

---

## 2. 패키지 구조 (도메인형 - 필수!)

```
{base-package}
├── global/                          # 전역 공통 모듈
│   ├── config/
│   │   ├── SecurityConfig.java      # Spring Security + CORS
│   │   ├── SpringDoc.java           # Swagger/OpenAPI + JWT 인증
│   │   └── RedisConfig.java         # RedisTemplate 빈
│   ├── exception/
│   │   ├── BusinessException.java
│   │   └── GlobalExceptionHandler.java
│   ├── jpa/entity/
│   │   └── BaseEntity.java          # createdAt, modifiedAt
│   ├── response/
│   │   ├── ApiResponse.java         # 공통 응답 래퍼
│   │   ├── ErrorCode.java           # 도메인별 에러 코드
│   │   └── ResponseCode.java        # HTTP 응답 코드
│   ├── client/                      # 외부 서버 통신 (AI 서버 등)
│   │   ├── AiClientConfig.java      # WebClient 빈 설정
│   │   ├── AiClient.java            # AI 서버 통신 클라이언트
│   │   └── dto/
│   │       ├── AiScheduleRequest.java
│   │       └── AiScheduleResponse.java
│   ├── init/
│   │   └── BaseInitData.java        # @Profile("dev") 개발용 초기 데이터
│   └── security/
│       ├── jwt/
│       │   ├── JwtTokenProvider.java
│       │   ├── JwtAuthenticationFilter.java
│       │   └── JwtProperties.java
│       ├── CustomUserDetails.java
│       ├── CustomUserDetailsService.java
│       └── AuthenticationEntryPointImpl.java
├── auth/                            # 인증 도메인
│   ├── controller/AuthController.java
│   ├── dto/ (SignupRequest, LoginRequest, TokenResponse, RefreshRequest)
│   └── service/ (AuthService, TokenService)
├── user/                            # 사용자
│   ├── controller/UserController.java
│   ├── domain/User.java
│   ├── dto/UserResponse.java
│   ├── repository/UserRepository.java
│   └── service/UserService.java
├── {도메인}/                         # 비즈니스 도메인 (아래 패턴 반복)
│   ├── controller/{Domain}Controller.java
│   ├── domain/{Domain}.java
│   ├── dto/ ({Domain}Request, {Domain}Response)
│   ├── repository/{Domain}Repository.java
│   └── service/{Domain}Service.java
└── Application.java                 # @EnableJpaAuditing 필수
```

---

## 3. Global 모듈

### BaseEntity

모든 엔티티는 반드시 `BaseEntity`를 상속합니다.

```java
@MappedSuperclass
@Getter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
}
```

### ApiResponse (공통 응답 래퍼)

모든 API 응답은 `ApiResponse<T>`로 래핑합니다.

```java
@AllArgsConstructor
@Getter
@Schema(description = "공통 API 응답")
public class ApiResponse<T> {
    private String code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResponseCode.OK.getCode(), ResponseCode.OK.getMessage(), data);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(ResponseCode.OK.getCode(), ResponseCode.OK.getMessage(), null);
    }

    public static <T> ApiResponse<T> error(ErrorCode code) {
        return new ApiResponse<>(code.getCode(), code.getMessage(), null);
    }
}
```

### ErrorCode (도메인별 에러 코드)

도메인별 접두사를 부여하여 에러 코드를 관리합니다. (예: Auth → A, User → U, Post → P 등)

```java
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 인증 (A) - 공통으로 사용
    INVALID_TOKEN("A001", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN("A002", HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN("A003", HttpStatus.UNAUTHORIZED, "지원하지 않는 토큰 형식입니다."),
    EMPTY_TOKEN("A004", HttpStatus.UNAUTHORIZED, "토큰이 비어있습니다."),
    UNAUTHORIZED("A005", HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    BLACKLISTED_TOKEN("A006", HttpStatus.UNAUTHORIZED, "로그아웃된 토큰입니다."),
    INVALID_REFRESH_TOKEN("A007", HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    INVALID_CREDENTIALS("A008", HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),

    // 사용자 (U) - 공통으로 사용
    DUPLICATE_EMAIL("U001", HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    USER_NOT_FOUND("U002", HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    INVALID_PASSWORD("U003", HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");

    // 비즈니스 도메인 - 프로젝트에 맞게 추가
    // NOT_FOUND_{DOMAIN}("{접두사}001", HttpStatus.NOT_FOUND, "존재하지 않는 {도메인}입니다."),

    private final String code;
    private final HttpStatus status;
    private final String message;
}
```

### 예외 처리

모든 비즈니스 예외는 `BusinessException`으로 던집니다. `IllegalArgumentException`, `RuntimeException` 직접 사용 금지.

```java
// Service에서 예외 던지기
public PostResponse getPost(Long id) {
    Post post = postRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_POST));
    // ...
}
```

---

## 4. 도메인 계층

### 엔티티 (Entity)

- 위치: `{domain}/domain/`
- `BaseEntity` 상속 필수
- `@NoArgsConstructor(access = AccessLevel.PROTECTED)`
- `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- 모든 연관관계 `FetchType.LAZY`
- `@Setter` 금지 → 비즈니스 메서드 사용

```java
@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PostStatus status;

    @Builder
    public Post(User user, String title, String content, PostStatus status) { ... }

    // 비즈니스 메서드 (Setter 대신)
    public void update(String title, String content) { ... }
    public void updateStatus(PostStatus status) { this.status = status; }
}
```

### 리포지토리 (Repository)

```java
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);
}
```

### 서비스 (Service)

- 클래스 레벨: `@Transactional(readOnly = true)`
- 쓰기 메서드: `@Transactional` 추가
- 예외는 `BusinessException` 사용

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 게시글 생성
    @Transactional
    public PostResponse createPost(Long userId, PostRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Post post = request.toEntity(user);
        Post savedPost = postRepository.save(post);
        return PostResponse.from(savedPost);
    }

    // 게시글 상세 조회
    public PostResponse getPost(Long id, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_POST));
        if (!post.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_POST_ACCESS);
        }
        return PostResponse.from(post);
    }
}
```

---

## 5. DTO 전략

- Java `record` 사용 (class 금지)
- Request: `toEntity()` 메서드 필수
- Response: `static from(Entity)` 팩토리 메서드 필수, BaseEntity 필드 포함

### Request DTO

```java
@Schema(description = "게시글 생성/수정 요청")
public record PostRequest(
        @Schema(description = "제목", example = "첫 번째 글")
        @NotBlank(message = "제목은 필수입니다.")
        String title,

        @Schema(description = "내용", example = "본문 내용입니다.")
        @NotBlank(message = "내용은 필수입니다.")
        String content
) {
    public Post toEntity(User user) {
        return Post.builder()
                .user(user)
                .title(title)
                .content(content)
                .status(PostStatus.DRAFT)
                .build();
    }
}
```

### Response DTO

```java
@Schema(description = "게시글 응답")
public record PostResponse(
        Long id, Long userId, String title, String content,
        PostStatus status, LocalDateTime createdAt, LocalDateTime modifiedAt
) {
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(), post.getUser().getId(), post.getTitle(),
                post.getContent(), post.getStatus(),
                post.getCreatedAt(), post.getModifiedAt()
        );
    }
}
```

---

## 6. 컨트롤러 (Controller)

- `@RestController` 사용
- 클래스 레벨 `@RequestMapping` 금지 → 메서드에 전체 경로
- 반환: `ResponseEntity<ApiResponse<T>>`
- `ResponseEntity.status(HttpStatus.XXX).body(...)` 형식 (`.ok()` 축약형 금지)
- 인증 필요 API: `@AuthenticationPrincipal CustomUserDetails userDetails`

| HTTP Method | 상태 코드 |
|-------------|-----------|
| GET | `HttpStatus.OK` (200) |
| POST | `HttpStatus.CREATED` (201) |
| PUT/PATCH | `HttpStatus.OK` (200) |
| DELETE | `HttpStatus.OK` (200) |

```java
@RestController
@RequiredArgsConstructor
@Tag(name = "Post", description = "게시글 API")
public class PostController {

    private final PostService postService;

    // 게시글 생성 API
    @PostMapping("/api/posts")
    @Operation(summary = "게시글 생성", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(postService.createPost(userDetails.getUserId(), request)));
    }

    // 게시글 삭제 API
    @DeleteMapping("/api/posts/{id}")
    @Operation(summary = "게시글 삭제", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        postService.deletePost(id, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success());
    }
}
```

---

## 7. API 엔드포인트 설계 패턴

### URL 규칙

- 접두사: `/api/`
- 리소스명: 복수형 소문자 (`/api/posts`, `/api/users`)
- 하위 리소스: `/api/{부모}/{부모Id}/{자식}` (예: `/api/posts/{postId}/comments`)

### CRUD 기본 패턴

| 메서드 | 엔드포인트 | 설명 | 상태 코드 |
|--------|-----------|------|-----------|
| POST | `/api/{도메인}` | 생성 | 201 |
| GET | `/api/{도메인}` | 목록 조회 | 200 |
| GET | `/api/{도메인}/{id}` | 상세 조회 | 200 |
| PUT | `/api/{도메인}/{id}` | 수정 | 200 |
| DELETE | `/api/{도메인}/{id}` | 삭제 | 200 |
| PATCH | `/api/{도메인}/{id}/{필드}` | 부분 수정 | 200 |

### 페이징 처리

목록 조회 API는 `Pageable`을 사용하여 페이징 처리합니다.

**컨트롤러:**
```java
@GetMapping("/api/posts")
@Operation(summary = "게시글 목록 조회")
public ResponseEntity<ApiResponse<Page<PostResponse>>> getPosts(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(postService.getPosts(userDetails.getUserId(), pageable)));
}
```

**서비스:**
```java
public Page<PostResponse> getPosts(Long userId, Pageable pageable) {
    return postRepository.findByUserId(userId, pageable)
            .map(PostResponse::from);
}
```

**리포지토리:**
```java
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUserId(Long userId, Pageable pageable);
}
```

**요청 예시:**
```
GET /api/posts?page=0&size=10&sort=createdAt,desc
```

**응답 형식:**
```json
{
  "code": "200",
  "message": "OK",
  "data": {
    "content": [...],
    "pageable": { "pageNumber": 0, "pageSize": 10 },
    "totalElements": 100,
    "totalPages": 10,
    "last": false,
    "first": true
  }
}
```

---

## 8. Swagger 문서화

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- 인증 필요 API: `@Operation(security = @SecurityRequirement(name = "bearerAuth"))`
- 인증 불필요 API: `@Operation(summary = "...")` (security 생략)

### SpringDoc 설정

```java
@Configuration
@OpenAPIDefinition(info = @Info(title = "${프로젝트명} API 서버", version = "v1"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SpringDoc {

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("all").pathsToMatch("/api/**")
                .addOpenApiCustomizer(openApi ->
                        openApi.addSecurityItem(new SecurityRequirement().addList("bearerAuth")))
                .build();
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder().group("auth").pathsToMatch("/api/auth/**").build();
    }

    // 도메인별 GroupedOpenApi를 추가
    // @Bean
    // public GroupedOpenApi postApi() {
    //     return GroupedOpenApi.builder().group("post").pathsToMatch("/api/posts/**").build();
    // }
}
```

---

## 9. 인증/인가 (JWT + Redis)

### 개요

| 항목 | 내용 |
|------|------|
| 인증 방식 | JWT (Access Token + Refresh Token) |
| 토큰 저장 | Redis (RT 저장 + AT 블랙리스트) |
| 비밀번호 정책 | 대소문자+숫자+특수문자 각 1개 이상, 8~20자 |
| 비밀번호 암호화 | BCryptPasswordEncoder |

### Redis 키 설계

| 용도 | Key | Value | TTL |
|------|-----|-------|-----|
| Refresh Token | `RT:{email}` | JWT 문자열 | 7일 |
| 블랙리스트 | `BL:{accessToken}` | `"logout"` | AT 남은 만료 시간 |

### 인증 흐름

- **로그인**: email/password 검증 → AT+RT 발급 → RT를 Redis 저장
- **토큰 갱신**: RT JWT 검증 → Redis RT 일치 확인 → 새 AT+RT 발급 (Rotation)
- **로그아웃**: AT를 BL에 추가 (남은 TTL) → Redis RT 삭제
- **API 요청 필터**: AT 검증 → BL 체크 → Authentication 설정

### TokenService (Redis 기반)

```java
@Service
@RequiredArgsConstructor
public class TokenService {

    private static final String RT_PREFIX = "RT:";
    private static final String BL_PREFIX = "BL:";
    private final RedisTemplate<String, String> redisTemplate;

    public void saveRefreshToken(String email, String refreshToken, long expirationMs) {
        redisTemplate.opsForValue().set(RT_PREFIX + email, refreshToken, expirationMs, TimeUnit.MILLISECONDS);
    }

    public String getRefreshToken(String email) {
        return redisTemplate.opsForValue().get(RT_PREFIX + email);
    }

    public void deleteRefreshToken(String email) {
        redisTemplate.delete(RT_PREFIX + email);
    }

    public void addToBlacklist(String accessToken, long remainingExpirationMs) {
        if (remainingExpirationMs > 0) {
            redisTemplate.opsForValue().set(BL_PREFIX + accessToken, "logout", remainingExpirationMs, TimeUnit.MILLISECONDS);
        }
    }

    public boolean isBlacklisted(String accessToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BL_PREFIX + accessToken));
    }
}
```

### JwtAuthenticationFilter

필터에서 블랙리스트 체크를 포함합니다.

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(...) throws ServletException, IOException {
        String token = resolveToken(request);

        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)
                && !tokenService.isBlacklisted(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
```

### SecurityConfig (인증 허용 경로)

```java
.authorizeHttpRequests(auth -> auth
        // 인증 불필요
        .requestMatchers("/api/auth/**").permitAll()
        // 공개 조회 API (프로젝트에 맞게 추가)
        // .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
        // Swagger
        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
        // 그 외 인증 필요
        .anyRequest().authenticated()
)
```

### 인증 API 엔드포인트

| 엔드포인트 | 메서드 | 설명 | 인증 |
|-----------|--------|------|------|
| `/api/auth/signup` | POST | 회원가입 | 불필요 |
| `/api/auth/login` | POST | 로그인 (AT+RT 발급) | 불필요 |
| `/api/auth/refresh` | POST | 토큰 갱신 (RT → 새 AT+RT) | 불필요 |
| `/api/auth/logout` | POST | 로그아웃 (AT 블랙리스트) | 필요 |

---

## 10. 테스트 코드

새 도메인 생성 시 반드시 테스트도 함께 생성합니다.

### 테스트 패키지 구조

```
src/test/java/{base-package}/{domain}/
├── domain/{Domain}Test.java           # 엔티티 단위 테스트
├── repository/{Domain}RepositoryTest.java  # @DataJpaTest
├── service/{Domain}ServiceTest.java   # Mockito
└── controller/{Domain}ControllerTest.java  # @SpringBootTest + MockMvc
```

### 테스트 규칙

1. **Given-When-Then** 패턴
2. `@DisplayName`으로 한글 테스트 설명
3. **AssertJ**: `assertThat()` 사용
4. **BDDMockito**: `given().willReturn()` 스타일
5. 테스트 DB: H2 인메모리 (`src/test/resources/application.yaml`)

### 엔티티 테스트

```java
@DisplayName("Post 엔티티 테스트")
class PostTest {

    @Test
    @DisplayName("Post 엔티티 생성")
    void createPost() {
        // given
        User user = User.builder().email("test@example.com").password("pw")
                .name("홍길동").role(User.Role.ROLE_USER).build();

        // when
        Post post = Post.builder().user(user).title("첫 번째 글")
                .content("본문 내용").status(PostStatus.DRAFT).build();

        // then
        assertThat(post.getTitle()).isEqualTo("첫 번째 글");
        assertThat(post.getStatus()).isEqualTo(PostStatus.DRAFT);
    }
}
```

### 서비스 테스트 (Mockito)

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("PostService 테스트")
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("게시글 생성 - 성공")
    void createPost_success() {
        // given
        User user = createUser(1L);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        Post savedPost = createPost(1L, user);
        given(postRepository.save(any(Post.class))).willReturn(savedPost);

        PostRequest request = new PostRequest("첫 번째 글", "본문 내용");

        // when
        PostResponse result = postService.createPost(1L, request);

        // then
        assertThat(result.title()).isEqualTo("첫 번째 글");
    }

    // Reflection으로 ID 설정하는 헬퍼
    private <T> void setId(T entity, Class<T> clazz, Long id) {
        try {
            java.lang.reflect.Field idField = clazz.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

### 컨트롤러 테스트 (@SpringBootTest)

```java
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("PostController 테스트")
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostService postService;

    @Test
    @DisplayName("POST /api/posts - 게시글 생성 (인증 없이 → 401)")
    void createPost_unauthorized() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }
}
```

### 테스트 실행

```bash
./gradlew test
./gradlew test --tests "{base-package}.post.*"
```

---

## 11. 빌드 설정 (Build Configuration)

### 프로젝트 기본 정보

| 항목 | 값 |
|------|------|
| Spring Boot | 3.4.1 |
| Java | 21 |
| Gradle | Kotlin DSL |
| DB | MySQL 8.0 |
| 캐시/토큰 | Redis 7 |

### build.gradle.kts

```kotlin
plugins {
    java
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
}

// group, version, description은 프로젝트에 맞게 설정

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Swagger (SpringDoc)
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.0")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")

    // Database
    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("com.h2database:h2")            // 테스트용

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jar {
    enabled = false
}
```

### 의존성 요약

| 라이브러리 | 버전 | 용도 |
|-----------|------|------|
| springdoc-openapi | 2.8.0 | Swagger UI |
| jjwt | 0.12.3 | JWT 토큰 |
| mysql-connector-j | (Spring 관리) | MySQL 드라이버 |
| h2 | (Spring 관리) | 테스트 DB |
| spring-data-redis | (Spring 관리) | Redis (토큰 관리) |

---

## 12. 인프라 + DB 설정

### Docker Compose

MySQL과 Redis는 Docker Compose로 실행합니다.

```yaml
services:
  mysql:
    image: mysql:8.0
    container_name: ${프로젝트명}-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: ${DB명}
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql

  redis:
    image: redis:7-alpine
    container_name: ${프로젝트명}-redis
    ports:
      - "6379:6379"

volumes:
  mysql-data:
```

```bash
# 시작
docker-compose up -d

# 중지
docker-compose down
```

### 공통 설정 (src/main/resources/application.yaml)

```yaml
spring:
  application:
    name: ${프로젝트명}
  datasource:
    url: jdbc:mysql://localhost:3306/${DB명}
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:root}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    open-in-view: false
  data:
    redis:
      host: ${SPRING_DATA_REDIS_HOST:localhost}
      port: ${SPRING_DATA_REDIS_PORT:6379}

jwt:
  secret: ${JWT_SECRET:your-256-bit-secret-key-here-must-be-at-least-32-characters}
  access-token-expiration: 3600000
  refresh-token-expiration: 604800000
```

### 개발용 (src/main/resources/application-dev.yaml)

```yaml
# 실행: ./gradlew bootRun --args='--spring.profiles.active=dev'
# BaseInitData가 자동으로 테스트 계정과 샘플 데이터를 생성합니다.

spring:
  jpa:
    hibernate:
      ddl-auto: create    # 서버 시작 시 테이블 재생성
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

### 운영용 (src/main/resources/application-prod.yaml)

```yaml
# EC2 실행: docker-compose -f docker-compose.prod.yml up -d

spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3306/aitrip}
  jpa:
    hibernate:
      ddl-auto: validate  # 스키마 검증만, 자동 변경 안함
    show-sql: false
```

### 테스트용 (src/test/resources/application.yaml)

```yaml
spring:
  application:
    name: ${프로젝트명}
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    open-in-view: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
  data:
    redis:
      host: localhost
      port: 6379

jwt:
  secret: test-secret-key-for-testing-purposes-must-be-at-least-32-characters-long
  access-token-expiration: 3600000
  refresh-token-expiration: 604800000
```

### 환경별 요약

| 환경 | DB | ddl-auto | BaseInitData | 용도 |
|------|-----|----------|--------------|------|
| 기본 (application.yaml) | MySQL | update | X | 공통 설정 |
| 개발 (application-dev.yaml) | MySQL | create | O | 로컬 개발 |
| 운영 (application-prod.yaml) | MySQL | validate | X | EC2 배포 |
| 테스트 (test/application.yaml) | H2 | create-drop | X | 단위/통합 테스트 |

### H2 예약어 주의 (테스트 환경)

H2에서 `hour`, `year`, `month`, `day`, `time` 등은 예약어입니다. 엔티티 컬럼명으로 사용 시 반드시 `@Column(name = "crowd_hour")` 등으로 변경하세요.

### Docker Compose 파일 분리

로컬과 운영 환경은 별도의 Docker Compose 파일을 사용합니다.

| 파일 | 환경 | MySQL | 용도 |
|------|------|-------|------|
| `docker-compose.yml` | 로컬 | 컨테이너 | 개발용 (MySQL + Redis 포함) |
| `docker-compose.prod.yml` | EC2 | RDS | 운영용 (Redis만 포함) |

**로컬 실행:**
```bash
docker-compose up -d
# MySQL + Redis + Backend + AI + Frontend 전부 실행
```

**EC2 실행:**
```bash
docker-compose -f docker-compose.prod.yml up -d
# Redis + Backend + AI + Frontend 실행 (MySQL은 RDS 사용)
```

### EC2 배포 설정

EC2에 `.env` 파일을 생성하여 운영 환경변수를 관리합니다.

```bash
# EC2 서버에서 최초 1회 설정
cat > .env << 'EOF'
DB_HOST=aitrip-db.xxxx.ap-northeast-2.rds.amazonaws.com
DB_NAME=aitrip
DB_USERNAME=admin
DB_PASSWORD=운영DB비밀번호
JWT_SECRET=운영용-32자이상-시크릿키-여기에-실제값
OPENAI_API_KEY=sk-xxxx
EOF

# 배포
docker-compose -f docker-compose.prod.yml up -d --build
```

**주의:** `.env` 파일은 민감한 정보를 포함하므로 **Git에 절대 커밋하지 않습니다.**

```gitignore
# .gitignore
.env
```

### 배포 방법 비교

| 방법 | 복잡도 | 보안 | 추천 시점 |
|------|--------|------|----------|
| EC2에 .env 직접 생성 | 쉬움 | 보통 | MVP, 초기 개발 |
| GitHub Secrets + Actions | 중간 | 높음 | CI/CD 구축 시 |
| AWS Secrets Manager | 복잡 | 높음 | 규모 확장 시 |

---

## 13. 외부 서버 통신 (AI 서버 연동)

AI 서버(FastAPI/Node.js)와의 HTTP 통신은 `global/client/` 패키지에서 관리합니다.

### 패키지 구조

```
global/client/
├── AiClientConfig.java       # WebClient 빈 설정
├── AiClient.java             # AI 서버 통신 클라이언트
└── dto/
    ├── AiScheduleRequest.java
    └── AiScheduleResponse.java
```

### 의존성 추가 (build.gradle.kts)

```kotlin
dependencies {
    // WebClient (Spring WebFlux)
    implementation("org.springframework.boot:spring-boot-starter-webflux")
}
```

### WebClient 설정

```java
@Configuration
public class AiClientConfig {

    @Value("${ai.server.url}")
    private String aiServerUrl;

    @Bean
    public WebClient aiWebClient() {
        return WebClient.builder()
                .baseUrl(aiServerUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
```

### application.yaml 설정

```yaml
ai:
  server:
    url: ${AI_SERVER_URL:http://localhost:8000}
    timeout: 30000  # 30초
```

### AI Client 구현

```java
@Component
@RequiredArgsConstructor
public class AiClient {

    private final WebClient aiWebClient;

    // AI 서버에 일정 생성 요청
    public AiScheduleResponse requestSchedule(AiScheduleRequest request) {
        return aiWebClient.post()
                .uri("/api/schedule/generate")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        Mono.error(new BusinessException(ErrorCode.AI_SERVER_ERROR)))
                .bodyToMono(AiScheduleResponse.class)
                .timeout(Duration.ofSeconds(30))
                .block();
    }

    // AI 서버 헬스체크
    public boolean healthCheck() {
        try {
            aiWebClient.get()
                    .uri("/health")
                    .retrieve()
                    .bodyToMono(Void.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

### AI 통신용 DTO

```java
// 요청 DTO
public record AiScheduleRequest(
        String region,
        LocalDate startDate,
        LocalDate endDate,
        String tripStyle,
        List<String> preferences
) {}

// 응답 DTO
public record AiScheduleResponse(
        boolean success,
        String message,
        List<AiPlaceRecommendation> recommendations
) {}

public record AiPlaceRecommendation(
        String placeName,
        String category,
        Integer visitOrder,
        Integer estimatedMinutes,
        String reason
) {}
```

### 서비스에서 사용

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripService {

    private final AiClient aiClient;
    private final TripRepository tripRepository;

    // AI 기반 일정 생성
    @Transactional
    public TripResponse createTripWithAi(Long userId, TripRequest request) {
        // 1. AI 서버에 일정 추천 요청
        AiScheduleRequest aiRequest = new AiScheduleRequest(
                request.region(),
                request.startDate(),
                request.endDate(),
                request.tripStyle().name(),
                request.preferences()
        );
        AiScheduleResponse aiResponse = aiClient.requestSchedule(aiRequest);

        // 2. AI 응답으로 Trip 엔티티 생성
        // ...
    }
}
```

### ErrorCode 추가

```java
public enum ErrorCode {
    // 기존 에러 코드...

    // AI 서버 (AI)
    AI_SERVER_ERROR("AI001", HttpStatus.SERVICE_UNAVAILABLE, "AI 서버 연결에 실패했습니다."),
    AI_TIMEOUT("AI002", HttpStatus.GATEWAY_TIMEOUT, "AI 서버 응답 시간이 초과되었습니다."),
    AI_INVALID_RESPONSE("AI003", HttpStatus.BAD_GATEWAY, "AI 서버 응답을 처리할 수 없습니다.");
}
```

### 주의사항

1. **타임아웃 설정 필수**: AI 서버 응답이 느릴 수 있으므로 적절한 타임아웃 설정
2. **에러 핸들링**: AI 서버 장애 시 사용자에게 명확한 에러 메시지 반환
3. **비동기 처리 고려**: 긴 작업은 `@Async` 또는 메시지 큐 사용 검토
4. **재시도 정책**: 일시적 장애 대비 재시도 로직 추가 가능 (Resilience4j 등)
5. **`.block()` 사용 주의**: 위 예시는 동기 호출 (`.block()`) 사용. MVP에서는 괜찮지만, 트래픽 증가 시 스레드 블로킹으로 성능 저하 가능. 필요시 비동기 (`Mono<T>` 반환) 전환 검토

**비동기 전환 예시:**
```java
// 동기 (현재) - 스레드 블로킹
public AiScheduleResponse requestSchedule(AiScheduleRequest request) {
    return aiWebClient.post()
            .uri("/api/schedule/generate")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(AiScheduleResponse.class)
            .block();  // 스레드 블로킹
}

// 비동기 (확장 시) - 논블로킹
public Mono<AiScheduleResponse> requestScheduleAsync(AiScheduleRequest request) {
    return aiWebClient.post()
            .uri("/api/schedule/generate")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(AiScheduleResponse.class);
}
```

---

## 14. 개발 환경 Mock 데이터 (BaseInitData)

팀원 간 동일한 테스트 데이터로 개발하기 위해 `BaseInitData`를 사용합니다.

### 패키지 구조

```
global/init/
└── BaseInitData.java    # @Profile("dev") - 개발 환경에서만 실행
```

### 사용 방법

```bash
# dev 프로파일로 실행 (Mock 데이터 자동 생성)
./gradlew bootRun --args='--spring.profiles.active=dev'

# Windows
gradlew.bat bootRun --args='--spring.profiles.active=dev'
```

### 테스트 계정

| 이메일 | 비밀번호 | 역할 |
|--------|----------|------|
| admin@aitrip.com | Test1234! | ROLE_ADMIN |
| user1@test.com | Test1234! | ROLE_USER |
| user2@test.com | Test1234! | ROLE_USER |

### BaseInitData 구현 패턴

```java
@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class BaseInitData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // 이미 데이터가 있으면 스킵
        if (userRepository.count() > 0) {
            log.info("데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("=== 개발용 Mock 데이터 초기화 시작 ===");
        createUsers();
        log.info("=== 개발용 Mock 데이터 초기화 완료 ===");
    }

    private void createUsers() {
        String encodedPassword = passwordEncoder.encode("Test1234!");

        userRepository.save(User.builder()
                .email("user1@test.com")
                .password(encodedPassword)
                .name("테스트유저1")
                .role(User.Role.ROLE_USER)
                .build());

        log.info("테스트 계정 생성 완료");
    }
}
```

### SQL 대비 장점

| 항목 | data-dev.sql | BaseInitData |
|------|--------------|--------------|
| 엔티티 변경 시 | SQL 수동 수정 필요 | 컴파일 에러로 바로 감지 |
| 연관관계 데이터 | FK 순서 직접 관리 | JPA가 자동 처리 |
| 비밀번호 | 미리 인코딩된 값 하드코딩 | `passwordEncoder` 사용 |
| IDE 지원 | 없음 | 리팩토링, 자동완성 지원 |

### Mock 데이터 추가 규칙

1. `BaseInitData.java`에 메서드 추가 (예: `createPlaces()`)
2. 새 엔티티 추가 시 해당 Repository 주입
3. PR로 팀원들과 공유 → `git pull` + 서버 재시작으로 적용

---

## 15. 협업 규칙

1. **범위 확인:** 작업 시작 전 담당 도메인 파악.
2. **격리:** 담당 도메인 외부 패키지 수정 금지.
3. **동의 구하기:** `global/` 모듈이나 다른 도메인 수정 시 반드시 사전 협의.
