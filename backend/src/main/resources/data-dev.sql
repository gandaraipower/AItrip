-- =====================================================
-- AI Trip Mock Data for Development
-- 팀원 공유용 개발 데이터
-- =====================================================

-- 테스트 유저 (비밀번호: Test1234!)
-- BCrypt 해시: $2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4G0WbRl9xPKP5gYb5LFr8UuCO5Oe
INSERT INTO users (email, password, name, role, created_at, modified_at)
VALUES
    ('admin@aitrip.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4G0WbRl9xPKP5gYb5LFr8UuCO5Oe', '관리자', 'ROLE_ADMIN', NOW(), NOW()),
    ('user1@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4G0WbRl9xPKP5gYb5LFr8UuCO5Oe', '테스트유저1', 'ROLE_USER', NOW(), NOW()),
    ('user2@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4G0WbRl9xPKP5gYb5LFr8UuCO5Oe', '테스트유저2', 'ROLE_USER', NOW(), NOW())
ON DUPLICATE KEY UPDATE email = email;

-- 서울 주요 관광지
INSERT INTO places (name, region, category, address, latitude, longitude, operating_hours, estimated_stay_time, image_url, source, created_at, modified_at)
VALUES
    ('경복궁', '서울', '관광명소', '서울특별시 종로구 사직로 161', 37.5796212, 126.9770162, '09:00-18:00', 120, NULL, 'visitkorea', NOW(), NOW()),
    ('남산타워', '서울', '관광명소', '서울특별시 용산구 남산공원길 105', 37.5511694, 126.9882266, '10:00-23:00', 90, NULL, 'visitkorea', NOW(), NOW()),
    ('북촌한옥마을', '서울', '관광명소', '서울특별시 종로구 계동길 37', 37.5826354, 126.9859163, '24시간', 60, NULL, 'visitkorea', NOW(), NOW()),
    ('명동', '서울', '쇼핑', '서울특별시 중구 명동길', 37.5636250, 126.9829440, '10:00-22:00', 120, NULL, 'visitkorea', NOW(), NOW()),
    ('홍대거리', '서울', '쇼핑', '서울특별시 마포구 홍익로', 37.5563073, 126.9235349, '24시간', 90, NULL, 'visitkorea', NOW(), NOW()),
    ('광장시장', '서울', '맛집', '서울특별시 종로구 창경궁로 88', 37.5700563, 126.9997916, '09:00-23:00', 60, NULL, 'visitkorea', NOW(), NOW()),
    ('이태원', '서울', '맛집', '서울특별시 용산구 이태원로', 37.5347129, 126.9945606, '24시간', 90, NULL, 'visitkorea', NOW(), NOW())
ON DUPLICATE KEY UPDATE name = name;

-- 부산 주요 관광지
INSERT INTO places (name, region, category, address, latitude, longitude, operating_hours, estimated_stay_time, image_url, source, created_at, modified_at)
VALUES
    ('해운대해수욕장', '부산', '관광명소', '부산광역시 해운대구 해운대해변로 264', 35.1586977, 129.1604355, '24시간', 120, NULL, 'visitkorea', NOW(), NOW()),
    ('광안리해수욕장', '부산', '관광명소', '부산광역시 수영구 광안해변로 219', 35.1531696, 129.1186214, '24시간', 90, NULL, 'visitkorea', NOW(), NOW()),
    ('감천문화마을', '부산', '관광명소', '부산광역시 사하구 감내2로 203', 35.0973969, 129.0105839, '09:00-18:00', 90, NULL, 'visitkorea', NOW(), NOW()),
    ('자갈치시장', '부산', '맛집', '부산광역시 중구 자갈치해안로 52', 35.0966244, 129.0305166, '05:00-22:00', 60, NULL, 'visitkorea', NOW(), NOW()),
    ('서면', '부산', '쇼핑', '부산광역시 부산진구 서면로', 35.1578515, 129.0598865, '10:00-22:00', 90, NULL, 'visitkorea', NOW(), NOW())
ON DUPLICATE KEY UPDATE name = name;

-- 제주 주요 관광지
INSERT INTO places (name, region, category, address, latitude, longitude, operating_hours, estimated_stay_time, image_url, source, created_at, modified_at)
VALUES
    ('성산일출봉', '제주', '관광명소', '제주특별자치도 서귀포시 성산읍 일출로 284-12', 33.4587216, 126.9426052, '07:00-20:00', 90, NULL, 'visitkorea', NOW(), NOW()),
    ('한라산', '제주', '관광명소', '제주특별자치도 제주시 1100로 2070-61', 33.3616666, 126.5291666, '05:00-18:00', 300, NULL, 'visitkorea', NOW(), NOW()),
    ('우도', '제주', '관광명소', '제주특별자치도 제주시 우도면', 33.5060000, 126.9520000, '24시간', 240, NULL, 'visitkorea', NOW(), NOW()),
    ('협재해수욕장', '제주', '관광명소', '제주특별자치도 제주시 한림읍 협재리', 33.3939393, 126.2394523, '24시간', 90, NULL, 'visitkorea', NOW(), NOW()),
    ('동문시장', '제주', '맛집', '제주특별자치도 제주시 관덕로14길 20', 33.5127777, 126.5272222, '07:00-21:00', 60, NULL, 'visitkorea', NOW(), NOW())
ON DUPLICATE KEY UPDATE name = name;
