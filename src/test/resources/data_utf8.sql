-- 외래 키 제약 조건 비활성화
SET FOREIGN_KEY_CHECKS = 0;

-- --------------------------------------------------------
-- 기본 엔티티 데이터 (다른 테이블에서 참조하는 기본 데이터)
-- --------------------------------------------------------

-- 테스트 사용자 데이터 (users): 다른 많은 테이블에서 user_id로 참조
INSERT IGNORE INTO users (username, password, nickname, email, role, accountStatus, profileImageUrl, lastLoginAt, created_at, modified_at, is_deleted)
VALUES 
    ('test_user', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '테스트유저', 'test@example.com', 'USER', 'ACTIVE', 'https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 HOUR), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('test_admin', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '관리자', 'admin@example.com', 'ADMIN', 'ACTIVE', 'https://images.pexels.com/photos/1933873/pexels-photo-1933873.jpeg', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 1 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 사용자 관심사 데이터 (user_interests): users 테이블 참조
INSERT IGNORE INTO user_interests (user_id, interests, created_at, modified_at, is_deleted)
VALUES
    (1, '철학,소설,심리학,미니멀리즘', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (2, '철학,역사,과학,예술', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 인증 데이터 (auth): users 테이블 참조
INSERT IGNORE INTO auth (user_id, refreshToken, tokenType, expiryDate, lastUsedAt, created_at, modified_at, is_deleted)
VALUES
    (1, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test-refresh-token-1', 'BEARER', DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL 7 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (2, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test-refresh-token-2', 'BEARER', DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL 7 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 도서 데이터 (book): 콘텐츠의 기본이 되는 도서 정보
INSERT IGNORE INTO book (title, author, isbn, description, publisher, publishDate, thumbnailUrl, publicationYear, totalPages, avgReadingTime, averageRating, ratingCount, popularity, created_at, modified_at, is_deleted)
VALUES 
    ('철학의 즐거움', '알랭 드 보통', '9788900000000', '현대인의 일상 속에서 철학이 어떻게 적용될 수 있는지 쉽고 재미있게 설명하는 책입니다. 플라톤부터 니체까지, 위대한 철학자들의 사상을 현대적 관점에서 재해석합니다.', '세계출판사', '2023-03-15', 'https://images.pexels.com/photos/3747139/pexels-photo-3747139.jpeg', 2023, 328, 840, 4.7, 1243, 78, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 도서 장르 데이터 (book_genres): book 테이블 참조
INSERT IGNORE INTO book_genres (book_id, genre)
VALUES
    (1, '철학'),
    (1, '자기계발'),
    (1, '인문학');

-- --------------------------------------------------------
-- 콘텐츠 및 상호작용 데이터
-- --------------------------------------------------------

-- 테스트 문구 데이터 (quotes): users와 book 테이블 참조
INSERT IGNORE INTO quotes (content, page, memo, like_count, save_count, user_id, book_id, created_at, modified_at, is_deleted)
VALUES 
    ('진정한 지혜는 자신이 모른다는 것을 아는 데서 시작한다.', 42, '소크라테스의 명언. 자기 자신에 대한 성찰의 중요성을 강조한다.', 203, 87, 1, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('우리는 한 번도 같은 강물에 발을 담글 수 없다. 모든 것은 흐르고 변한다.', 78, '헤라클레이토스의 유명한 격언. 변화의 본질에 대해 생각해보게 한다.', 158, 42, 1, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 저장된 인용구 데이터
INSERT IGNORE INTO saved_quotes (user_id, quote_id, note, created_at, modified_at, is_deleted)
VALUES
    (1, 1, '내 인생의 좌우명으로 삼고 싶은 문구', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (2, 1, '매일 아침 되새기기 좋은 문구', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (1, 2, '변화의 본질에 대해 생각하게 해주는 문구', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 좋아요 데이터
INSERT IGNORE INTO likes (user_id, quote_id, created_at, modified_at, is_deleted)
VALUES 
    (1, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (2, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 댓글 데이터
INSERT IGNORE INTO comments (content, user_id, quote_id, created_at, modified_at, is_deleted)
VALUES 
    ('이 문구는 제 인생의 모토가 되었습니다. 정말 의미있는 문장이네요.', 1, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('헤라클레이토스의 지혜가 담긴 명언입니다. 변화를 받아들이는 법을 배우게 됩니다.', 2, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 숏폼 콘텐츠 데이터
INSERT IGNORE INTO SHORT_FORM_CONTENTS (title, description, status, duration, viewCount, likeCount, shareCount, commentCount, created_at, modified_at, contentType, subtitles, videoUrl, thumbnailUrl, audioUrl, book_id, quote_id, is_deleted)
VALUES 
    ('철학이 삶에 미치는 영향', '플라톤과 아리스토텔레스의 철학이 현대 사회에 어떤 영향을 미치는지 알아보는 숏폼 콘텐츠입니다. 일상에서 철학적 사고를 적용하는 방법과 그 이점을 설명합니다.', 'PUBLISHED', 87, 24563, 4782, 1254, 342, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 32 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 30 DAY), 'VIDEO', '0:00 인트로\n0:15 철학이란 무엇인가?\n1:05 플라톤의 동굴 비유\n2:10 아리스토텔레스의 행복론\n3:25 현대사회에 적용하기\n4:05 마무리', 'https://www.pexels.com/video/people-walking-in-an-entrance-of-a-building-3129671/', 'https://images.pexels.com/photos/7034639/pexels-photo-7034639.jpeg', 'classpath:bgm/calm.mp3', 1, 1, 0),
    
    ('소설 속 인간 심리의 비밀', '문학 작품 속에 나타난 인간 심리의 다양한 측면을 분석하고, 현대 심리학과의 연관성을 탐구합니다. 소설이 어떻게 인간 본성을 통찰력 있게 표현하는지 알아봅니다.', 'PUBLISHED', 124, 18972, 3854, 987, 201, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 14 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 14 DAY), 'VIDEO', '0:00 도입\n0:20 소설과 심리학\n1:15 도스토예프스키의 인물 분석\n2:35 프로이트와 문학\n3:40 현대 소설의 심리묘사\n4:55 마무리', 'https://www.pexels.com/video/woman-reading-a-book-while-sitting-on-a-chair-2859232/', 'https://images.pexels.com/photos/2041556/pexels-photo-2041556.jpeg', 'classpath:bgm/neutral.mp3', 1, 2, 0),
    
    ('미니멀리즘: 단순함의 힘', '물질적 소유를 줄이고 정신적 풍요를 추구하는 미니멀리즘의 철학을 소개합니다. 일본의 \'단순함\'과 서양의 미니멀리즘을 비교하며 현대인의 삶에 적용할 수 있는 방법을 제시합니다.', 'PUBLISHED', 68, 35241, 8932, 2743, 489, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 7 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 6 DAY), 'VIDEO', '0:00 미니멀리즘이란\n0:45 일본의 와비사비와 미니멀리즘\n1:30 디지털 미니멀리즘\n2:15 미니멀 라이프 시작하기\n3:20 정리하는 습관\n4:10 결론', 'https://www.pexels.com/video/interior-of-a-contemporary-empty-room-3625225/', 'https://images.pexels.com/photos/6679526/pexels-photo-6679526.jpeg', 'classpath:bgm/calm.mp3', 1, 1, 0),
    
    ('고전 읽기의 즐거움', '고전 문학 작품을 현대적 관점에서 재해석하고, 일상에서 고전을 즐기는 방법을 소개합니다. 바쁜 현대인도 쉽게 고전의 지혜를 받아들일 수 있는 실용적인 팁을 제공합니다.', 'DRAFT', 95, 0, 0, 0, 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'AUDIO', '0:00 인트로듀스\n0:30 왜 고전을 읽어야 하는가\n1:25 고전의 현대적 가치\n2:40 짧은 시간에 고전 읽기\n3:55 추천 고전 5선\n4:30 마무리', 'https://www.pexels.com/video/close-up-video-of-turning-the-pages-of-a-book-5752729/', 'https://images.pexels.com/photos/1029141/pexels-photo-1029141.jpeg', 'classpath:bgm/happy.mp3', 1, 2, 0),
    
    ('시간 관리의 기술', '현대인의 시간 부족 문제를 해결하기 위한 철학적, 실용적 접근법을 다룹니다. 고대 스토아 철학자들의 지혜부터 최신 생산성 연구까지 폭넓게 살펴봅니다.', 'PUBLISHED', 112, 42891, 9874, 3421, 678, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 21 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 18 DAY), 'VIDEO', '0:00 시작\n0:25 시간의 주관성\n1:10 파레토 법칙\n2:05 딥 워크의 중요성\n3:15 디지털 디톡스\n4:25 마무리와 실천 방법', 'https://www.pexels.com/video/close-up-of-a-clock-854198/', 'https://images.pexels.com/photos/1095601/pexels-photo-1095601.jpeg', 'classpath:bgm/sad.mp3', 1, 1, 0);

-- 테스트 콘텐츠 인터랙션 데이터
INSERT IGNORE INTO CONTENT_INTERACTION (user_id, content_id, liked, bookmarked, viewedAt, created_at, modified_at, is_deleted)
VALUES 
    (1, 1, 1, 1, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 3 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (2, 1, 1, 0, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (1, 2, 0, 1, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 1 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 콘텐츠 댓글 데이터
INSERT IGNORE INTO content_comments (content, user_id, content_id, created_at, modified_at, is_deleted)
VALUES 
    ('정말 유익한 콘텐츠입니다! 철학이 어렵게만 느껴졌는데 이렇게 쉽게 설명해주셔서 감사합니다.', 1, 1, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 5 DAY), CURRENT_TIMESTAMP(), 0),
    ('플라톤의 동굴 비유를 이렇게 현대적으로 해석해주시니 훨씬 이해가 잘 되네요. 다음 콘텐츠도 기대됩니다.', 2, 1, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 4 DAY), CURRENT_TIMESTAMP(), 0),
    ('소설 속 인물들의 심리를 이렇게 분석해주시니 소설 읽을 때 더 깊이 이해할 수 있을 것 같아요.', 1, 2, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 DAY), CURRENT_TIMESTAMP(), 0);

-- 테스트 인용구 요약 데이터
INSERT IGNORE INTO quote_summaries (content, algorithm, generatedAt, quality, quote_id, created_at, modified_at, is_deleted)
VALUES 
    ('소크라테스의 이 명언은 자기성찰과 겸손의 가치를 강조합니다. 무지를 인정하는 것이 진정한 지혜의 시작이라는 역설적 진리를 담고 있으며, 현대 사회에서도 매우 유의미한 메시지입니다.', 'GPT-4', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 7 DAY), 0.95, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('헤라클레이토스의 이 명언은 영원한 변화의 법칙을 설명합니다. 모든 것은 끊임없이 변화하며, 같은 상태로 머무르지 않는다는 우주의 근본 원리를 간결하게 표현했습니다.', 'GPT-4', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 5 DAY), 0.92, 2, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 미디어 리소스 데이터
INSERT IGNORE INTO media_resources (content_id, `type`, url, thumbnailUrl, description, duration, created_at, modified_at, is_deleted)
VALUES
    (1, 'VIDEO', 'https://www.pexels.com/video/time-lapse-video-of-stars-857195/', 'https://images.pexels.com/photos/1169754/pexels-photo-1169754.jpeg', '철학적 사고의 깊이를 표현한 밤하늘 타임랩스 영상', 120, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (1, 'AUDIO', 'https://www.pexels.com/video/audio/4672/', 'https://images.pexels.com/photos/33597/guitar-classical-guitar-acoustic-guitar-electric-guitar.jpg', '철학 콘텐츠에 사용된 명상 배경 음악', 180, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (2, 'VIDEO', 'https://www.pexels.com/video/a-woman-thinking-while-writing-5798232/', 'https://images.pexels.com/photos/3767411/pexels-photo-3767411.jpeg', '소설 집필 과정을 담은 영상 클립', 90, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 사용자 유사도 데이터
INSERT IGNORE INTO user_similarities (source_user_id, target_user_id, similarity_score, is_active, created_at, modified_at, is_deleted)
VALUES
    (1, 2, 0.85, 1, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 10 DAY), CURRENT_TIMESTAMP(), 0);

-- 테스트 시스템 설정 데이터
INSERT IGNORE INTO system_settings (setting_key, setting_value, description, category, is_encrypted, is_system_managed, last_modified_by, default_value, validation_pattern, created_at, modified_at, is_deleted)
VALUES
    ('max_file_size', '10485760', '업로드 가능한 최대 파일 크기(바이트)', 'UPLOAD', 0, 1, 'admin', '10485760', '^[0-9]+$', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('api_timeout', '30000', 'API 요청 타임아웃 시간(밀리초)', 'API', 0, 1, 'admin', '30000', '^[0-9]+$', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('openai_api_key', 'sk-test-key-for-testing', 'OpenAI API 키', 'API', 1, 1, 'admin', 'sk-default-key', '^sk-[a-zA-Z0-9-]+$', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 검색 데이터
INSERT IGNORE INTO search (user_id, search_term, search_type, search_count, last_searched_at, created_at, modified_at, is_deleted)
VALUES
    (1, '철학', 'BOOK', 15, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 1 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (1, '사랑', 'QUOTE', 8, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 3 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (2, '행복', 'BOOK', 6, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 검색어 프로필 데이터
INSERT IGNORE INTO search_term_profiles (search_term, search_count, user_demographic_data, related_terms, trend_data, created_at, modified_at, is_deleted)
VALUES
    ('철학', 3452, '{"age": {"20s": 45, "30s": 35, "40s": 20}, "gender": {"male": 60, "female": 40}}', '사상,윤리학,존재,플라톤,니체', '{"2023-01": 425, "2023-02": 380, "2023-03": 402, "2023-04": 512, "2023-05": 487}', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('사랑', 5721, '{"age": {"10s": 15, "20s": 40, "30s": 30, "40s": 15}, "gender": {"male": 35, "female": 65}}', '연애,결혼,관계,감정,로맨스', '{"2023-01": 650, "2023-02": 700, "2023-03": 655, "2023-04": 810, "2023-05": 780}', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 사용자 랭킹 점수 데이터
INSERT IGNORE INTO ranking_user_score (user_id, current_score, previous_score, rank_type, last_activity_date, created_at, modified_at, suspicious_activity, report_count, account_suspended, is_deleted)
VALUES
    (1, 1542, 1450, 'SILVER', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 1 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 30 DAY), CURRENT_TIMESTAMP(), 0, 0, 0, 0),
    (2, 2358, 1750, 'GOLD', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 45 DAY), CURRENT_TIMESTAMP(), 0, 0, 0, 0);

-- 테스트 사용자 활동 데이터
INSERT IGNORE INTO ranking_user_activity (user_id, activity_type, points, activity_date, reference_id, reference_type, created_at, modified_at, is_deleted)
VALUES
    (1, 'QUOTE_CREATE', 25, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 5 DAY), 1, 'QUOTE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (1, 'CONTENT_LIKE', 10, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 3 DAY), 1, 'CONTENT', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (2, 'COMMENT_CREATE', 15, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 DAY), 1, 'COMMENT', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 랭킹 리더보드 데이터
INSERT IGNORE INTO ranking_leaderboard (user_id, username, score, rank_type, leaderboard_type, rank_position, period_start_date, period_end_date, created_at, modified_at, is_deleted)
VALUES
    (1, 'test_user', 1542, 'SILVER', 'WEEKLY', 3, DATE_SUB(CURRENT_DATE(), INTERVAL 7 DAY), CURRENT_DATE(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (2, 'test_admin', 2358, 'GOLD', 'WEEKLY', 1, DATE_SUB(CURRENT_DATE(), INTERVAL 7 DAY), CURRENT_DATE(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 랭킹 뱃지 데이터
INSERT IGNORE INTO ranking_badge (name, description, image_url, badge_type, requirement_type, threshold_value, points_awarded, created_at, modified_at, is_deleted)
VALUES
    ('북마스터', '50개 이상의 인용구를 저장한 사용자', 'https://images.pexels.com/photos/3659999/pexels-photo-3659999.jpeg', 'ACHIEVEMENT', 'SAVE_COUNT', 50, 200, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('인플루언서', '100개 이상의 좋아요를 받은 사용자', 'https://images.pexels.com/photos/2449600/pexels-photo-2449600.png', 'ACHIEVEMENT', 'LIKE_RECEIVED', 100, 300, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 랭킹 업적 데이터
INSERT IGNORE INTO ranking_achievement (user_id, badge_id, achieved_at, created_at, modified_at, is_deleted)
VALUES
    (1, 1, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 15 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 포스트 데이터
INSERT IGNORE INTO posts (title, content, user_id, thumbnail_url, created_at, modified_at, is_deleted)
VALUES
    ('독서의 즐거움과 효과', '독서는 우리에게 많은 즐거움과 지식을 선사합니다. 이 글에서는 독서의 다양한 이점과 효과적인 독서법에 대해 알아봅니다. 하루 30분의 독서만으로도 스트레스가 감소하고 어휘력이 향상된다는 연구 결과가 있습니다.', 1, 'https://images.pexels.com/photos/256431/pexels-photo-256431.jpeg', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 10 DAY), CURRENT_TIMESTAMP(), 0),
    ('2025년 상반기 추천 도서 리스트', '2025년 상반기에 제가 읽은 책 중에서 추천할 만한 10권의 책을 소개합니다. 각 분야별로 엄선한 도서들로 철학, 소설, 자기계발, 역사 등 다양한 장르를 다루고 있습니다.', 1, 'https://images.pexels.com/photos/1370298/pexels-photo-1370298.jpeg', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 5 DAY), CURRENT_TIMESTAMP(), 0);

-- 테스트 인기 검색어 데이터
INSERT IGNORE INTO popular_search_terms (search_term, search_count, popularity_score, last_updated_at, created_at, modified_at, is_deleted)
VALUES
    ('철학', 3452, 84.7, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 1 HOUR), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('소설', 2876, 76.3, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 HOUR), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('사랑', 5721, 92.8, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 3 HOUR), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 게이미피케이션 보상 데이터
INSERT IGNORE INTO gamification_rewards (user_id, reward_type, points, description, reference_id, reference_type, is_claimed, expiry_date, created_at, modified_at, is_deleted)
VALUES
    (1, 'DAILY_LOGIN', 10, '2025년 5월 10일 출석 체크 보상', 0, 'DAILY', 1, DATE_ADD(CURRENT_DATE(), INTERVAL 7 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 5 DAY), CURRENT_TIMESTAMP(), 0),
    (1, 'ACHIEVEMENT', 200, '북마스터 뱃지 획득 보상', 1, 'BADGE', 1, DATE_ADD(CURRENT_DATE(), INTERVAL 30 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 15 DAY), CURRENT_TIMESTAMP(), 0),
    (2, 'DAILY_LOGIN', 10, '2025년 5월 11일 출석 체크 보상', 0, 'DAILY', 1, DATE_ADD(CURRENT_DATE(), INTERVAL 7 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 4 DAY), CURRENT_TIMESTAMP(), 0);

-- --------------------------------------------------------
-- 콘텐츠 좋아요 및 북마크 데이터
-- --------------------------------------------------------

-- 테스트 콘텐츠 좋아요 데이터 (content_likes): SHORT_FORM_CONTENTS와 users 테이블 참조
-- 이 부분은 SHORT_FORM_CONTENTS에 데이터가 있어야 참조 가능합니다
INSERT IGNORE INTO content_likes (content_id, user_id, created_at, modified_at, is_deleted)
VALUES
    (1, 1, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 10 DAY), CURRENT_TIMESTAMP(), 0),
    (1, 2, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 9 DAY), CURRENT_TIMESTAMP(), 0),
    (2, 1, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 5 DAY), CURRENT_TIMESTAMP(), 0);

-- 테스트 콘텐츠 북마크 데이터 (content_bookmarks): SHORT_FORM_CONTENTS와 users 테이블 참조
-- 이 부분은 SHORT_FORM_CONTENTS에 데이터가 있어야 참조 가능합니다
INSERT IGNORE INTO content_bookmarks (content_id, user_id, created_at, modified_at, is_deleted)
VALUES
    (1, 1, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 8 DAY), CURRENT_TIMESTAMP(), 0),
    (2, 1, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 4 DAY), CURRENT_TIMESTAMP(), 0),
    (2, 2, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 3 DAY), CURRENT_TIMESTAMP(), 0);

-- --------------------------------------------------------
-- 요약 데이터
-- --------------------------------------------------------

-- 테스트 요약 데이터 (summaries): book 테이블 참조
INSERT IGNORE INTO summaries (book_id, content, chapter, page, created_at, modified_at, is_deleted)
VALUES
    (1, '이 장에서는 소크라테스, 플라톤, 아리스토텔레스 등 고대 그리스 철학자들의 주요 사상을 소개하며, 이들이 서양 철학의 기초를 어떻게 세웠는지 설명합니다. 특히 소크라테스의 대화법과 플라톤의 이데아론, 아리스토텔레스의 실천적 지혜 개념을 중점적으로 다룹니다.', '1장: 고대 그리스 철학의 기원', '1-32', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (1, '이 장에서는 칸트, 헤겔, 니체 등 근대 이후 주요 사상가들의 철학을 현대적 관점에서 재해석합니다. 특히 칸트의 정언명령과 니체의 초인 사상이 현대 윤리학과 실존주의에 미친 영향을 분석합니다.', '2장: 현대 철학의 흐름', '33-78', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 외래 키 제약 조건 다시 활성화
SET FOREIGN_KEY_CHECKS = 1;
