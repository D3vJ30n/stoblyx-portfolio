-- 테스트 사용자 데이터
INSERT INTO users (username, password, nickname, email, role, created_at, modified_at, is_deleted)
VALUES 
    ('test_user', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '테스트유저', 'test@example.com', 'USER', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    ('test_admin', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '관리자', 'admin@example.com', 'ADMIN', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);

-- 테스트 도서 데이터
INSERT INTO book (title, author, isbn, publisher, publishDate, created_at, modified_at, is_deleted)
VALUES 
    ('테스트 도서', '테스트 작가', '9788900000000', '테스트 출판사', CURRENT_DATE(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);

-- 테스트 문구 데이터
INSERT INTO quotes (content, page, memo, like_count, save_count, user_id, book_id, created_at, modified_at, is_deleted)
VALUES 
    ('첫 번째 테스트 문구입니다.', 1, '첫 번째 메모입니다.', 2, 1, 1, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    ('두 번째 테스트 문구입니다.', 2, '두 번째 메모입니다.', 0, 0, 1, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);

-- 테스트 좋아요 데이터
INSERT INTO likes (user_id, quote_id, created_at)
VALUES 
    (1, 1, CURRENT_TIMESTAMP()),
    (2, 1, CURRENT_TIMESTAMP());

-- 테스트 댓글 데이터
INSERT INTO comments (content, user_id, quote_id, created_at, modified_at, is_deleted)
VALUES 
    ('첫 번째 테스트 댓글입니다.', 1, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    ('두 번째 테스트 댓글입니다.', 2, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);

-- 테스트 숏폼 콘텐츠 데이터
INSERT INTO SHORT_FORM_CONTENTS (title, description, status, deleted, duration, viewCount, likeCount, shareCount, commentCount, created_at, modified_at, contentType, subtitles, videoUrl, thumbnailUrl, audioUrl, book_id, quote_id, is_deleted)
VALUES 
    ('철학적 사색', '철학에 관한 숏폼 콘텐츠입니다.', 'PUBLISHED', false, 60, 100, 50, 10, 5, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'VIDEO', '철학이란 무엇인가? 이 영상에서 알아봅니다.', 'https://example.com/video1.mp4', 'https://example.com/thumb1.jpg', 'https://example.com/audio1.mp3', 1, 1, false),
    ('소설 이야기', '소설에 관한 숏폼 콘텐츠입니다.', 'PUBLISHED', false, 90, 80, 30, 5, 2, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'VIDEO', '좋은 소설의 특징을 살펴봅니다.', 'https://example.com/video2.mp4', 'https://example.com/thumb2.jpg', 'https://example.com/audio2.mp3', 1, 2, false);

-- 테스트 콘텐츠 인터랙션 데이터
INSERT INTO CONTENT_INTERACTION (user_id, content_id, liked, bookmarked, viewedAt, created_at, modified_at, is_deleted)
VALUES 
    (1, 1, true, true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    (2, 1, true, false, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    (1, 2, false, true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);

-- 테스트 콘텐츠 댓글 데이터
INSERT INTO content_comments (content, user_id, content_id, created_at, modified_at, is_deleted)
VALUES 
    ('정말 좋은 콘텐츠입니다!', 1, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    ('유익한 정보 감사합니다.', 2, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    ('다음 콘텐츠도 기대됩니다.', 1, 2, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);

-- 테스트 인용구 요약 데이터
INSERT INTO quote_summaries (content, algorithm, generatedAt, quality, quote_id, created_at, modified_at, is_deleted)
VALUES 
    ('첫 번째 인용구에 대한 AI 요약입니다.', 'GPT-4', CURRENT_TIMESTAMP(), 0.95, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    ('두 번째 인용구에 대한 AI 요약입니다.', 'GPT-4', CURRENT_TIMESTAMP(), 0.92, 2, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);

-- 테스트 미디어 리소스 데이터
INSERT INTO media_resources (content_id, type, url, thumbnailUrl, description, duration, created_at, modified_at, is_deleted)
VALUES
    (1, 'VIDEO', 'https://example.com/video_resource1.mp4', 'https://example.com/thumb_resource1.jpg', '첫 번째 테스트 비디오 리소스', 120, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    (1, 'AUDIO', 'https://example.com/audio_resource1.mp3', NULL, '첫 번째 테스트 오디오 리소스', 180, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    (2, 'VIDEO', 'https://example.com/video_resource2.mp4', 'https://example.com/thumb_resource2.jpg', '두 번째 테스트 비디오 리소스', 90, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);

-- 테스트 사용자 유사도 데이터
INSERT INTO user_similarities (source_user_id, target_user_id, similarity_score, is_active, created_at, modified_at, is_deleted)
VALUES
    (1, 2, 0.85, true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);

-- 테스트 시스템 설정 데이터
INSERT INTO system_settings (setting_key, setting_value, description, category, is_encrypted, is_system_managed, created_at, modified_at, is_deleted)
VALUES
    ('max_file_size', '10485760', '업로드 가능한 최대 파일 크기(바이트)', 'UPLOAD', false, true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    ('api_timeout', '30000', 'API 요청 타임아웃 시간(밀리초)', 'API', false, true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    ('openai_api_key', 'sk-test-key-for-testing', 'OpenAI API 키', 'API', true, true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);

-- 테스트 검색 데이터
INSERT INTO search (user_id, search_term, search_type, search_count, last_searched_at, created_at, modified_at, is_deleted)
VALUES
    (1, '철학', 'BOOK', 3, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    (1, '사랑', 'QUOTE', 2, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    (2, '행복', 'BOOK', 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);

-- 테스트 검색어 프로필 데이터
INSERT INTO search_term_profiles (search_term, search_count, user_demographic_data, related_terms, trend_data, created_at, modified_at, is_deleted)
VALUES
    ('철학', 120, '{"age": {"20s": 45, "30s": 35, "40s": 20}, "gender": {"male": 60, "female": 40}}', '사상,윤리학,존재', '{"2023-01": 42, "2023-02": 38, "2023-03": 40}', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    ('사랑', 200, '{"age": {"10s": 15, "20s": 40, "30s": 30, "40s": 15}, "gender": {"male": 35, "female": 65}}', '연애,결혼,관계', '{"2023-01": 65, "2023-02": 70, "2023-03": 65}', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);

-- 테스트 사용자 랭킹 점수 데이터
INSERT INTO ranking_user_score (user_id, current_score, previous_score, rank_type, last_activity_date, created_at, modified_at, suspicious_activity, report_count, account_suspended)
VALUES
    (1, 1500, 1450, 'SILVER', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), FALSE, 0, FALSE),
    (2, 1800, 1750, 'GOLD', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), FALSE, 0, FALSE);

-- 테스트 사용자 활동 데이터
INSERT INTO ranking_user_activity (user_id, activity_type, points, activity_date, reference_id, reference_type, created_at, modified_at, is_deleted)
VALUES
    (1, 'QUOTE_CREATE', 10, CURRENT_TIMESTAMP(), 1, 'QUOTE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    (1, 'CONTENT_LIKE', 5, CURRENT_TIMESTAMP(), 1, 'CONTENT', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    (2, 'COMMENT_CREATE', 5, CURRENT_TIMESTAMP(), 1, 'COMMENT', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);

-- 테스트 랭킹 리더보드 데이터
INSERT INTO ranking_leaderboard (user_id, username, score, rank_type, leaderboard_type, rank_position, period_start_date, period_end_date, created_at, modified_at)
VALUES
    (1, 'test_user', 1250, 'GOLD', 'WEEKLY', 1, DATEADD('DAY', -7, CURRENT_DATE()), CURRENT_DATE(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
    (2, 'test_admin', 980, 'SILVER', 'WEEKLY', 2, DATEADD('DAY', -7, CURRENT_DATE()), CURRENT_DATE(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- 테스트 랭킹 뱃지 데이터
INSERT INTO ranking_badge (name, description, image_url, badge_type, requirement_type, threshold_value, points_awarded, created_at, modified_at, is_deleted)
VALUES
    ('북마스터', '50개 이상의 인용구를 저장한 사용자', 'https://example.com/badges/book_master.png', 'ACHIEVEMENT', 'SAVE_COUNT', 50, 200, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    ('인플루언서', '100개 이상의 좋아요를 받은 사용자', 'https://example.com/badges/influencer.png', 'ACHIEVEMENT', 'LIKE_RECEIVED', 100, 300, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);

-- 테스트 랭킹 업적 데이터
INSERT INTO ranking_achievement (user_id, badge_id, achieved_at, created_at, modified_at, is_deleted)
VALUES
    (1, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);

-- 테스트 포스트 데이터
INSERT INTO posts (title, content, user_id, post_type, view_count, like_count, comment_count, status, created_at, modified_at, is_deleted)
VALUES
    ('독서의 즐거움', '독서는 우리에게 많은 즐거움을 줍니다. 이 글에서는 독서의 다양한 이점에 대해 알아봅니다.', 1, 'BLOG', 150, 25, 8, 'PUBLISHED', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    ('추천 도서 리스트', '이번 달 제가 읽은 책 중에서 추천할 만한 책들을 소개합니다.', 1, 'BLOG', 200, 35, 12, 'PUBLISHED', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);

-- 테스트 인기 검색어 데이터
INSERT INTO popular_search_terms (search_term, search_count, last_searched_at, period_type, period_start, period_end, created_at, modified_at, is_deleted)
VALUES
    ('철학', 120, CURRENT_TIMESTAMP(), 'WEEKLY', DATEADD('DAY', -7, CURRENT_DATE()), CURRENT_DATE(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    ('소설', 95, CURRENT_TIMESTAMP(), 'WEEKLY', DATEADD('DAY', -7, CURRENT_DATE()), CURRENT_DATE(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    ('사랑', 85, CURRENT_TIMESTAMP(), 'WEEKLY', DATEADD('DAY', -7, CURRENT_DATE()), CURRENT_DATE(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);

-- 테스트 게이미피케이션 보상 데이터
INSERT INTO gamification_rewards (user_id, reward_type, points, description, reference_id, reference_type, is_claimed, expiry_date, created_at, modified_at, is_deleted)
VALUES
    (1, 'DAILY_LOGIN', 10, '오늘의 출석 체크 보상', NULL, NULL, true, NULL, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    (1, 'ACHIEVEMENT', 200, '북마스터 뱃지 획득 보상', 1, 'BADGE', true, NULL, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    (2, 'DAILY_LOGIN', 10, '오늘의 출석 체크 보상', NULL, NULL, true, NULL, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);

-- 테스트 콘텐츠 좋아요 데이터
INSERT INTO content_likes (content_id, user_id, created_at, modified_at, is_deleted)
VALUES
    (1, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    (1, 2, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    (2, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);

-- 테스트 콘텐츠 북마크 데이터
INSERT INTO content_bookmarks (content_id, user_id, created_at, modified_at, is_deleted)
VALUES
    (1, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    (2, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    (2, 2, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);

-- 테스트 요약 데이터
INSERT INTO summaries (book_id, content, chapter, page, deleted, created_at, modified_at, is_deleted)
VALUES
    (1, '이 책의 첫 번째 장에서는 주요 철학 사상의 기원과 발전에 대해 다룹니다.', '1장: 철학의 기원', '1-15', false, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    (1, '두 번째 장에서는 현대 철학의 주요 개념과 사상가들에 대해 알아봅니다.', '2장: 현대 철학', '16-35', false, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);