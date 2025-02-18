-- Users 샘플 데이터
INSERT INTO users (email, password, name, role) VALUES
('admin@stoblyx.com', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '관리자', 'ADMIN'), -- 비밀번호: admin123!
('user1@stoblyx.com', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '홍길동', 'USER'),  -- 비밀번호: user123!
('user2@stoblyx.com', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '김철수', 'USER');   -- 비밀번호: user123!

-- Books 샘플 데이터
INSERT INTO books (title, author, genre, published_at) VALUES
('사피엔스', '유발 하라리', '역사', '2015-02-15'),
('아몬드', '손원평', '소설', '2017-03-31'),
('코스모스', '칼 세이건', '과학', '1980-09-28');

-- Quotes 샘플 데이터
INSERT INTO quotes (content, book_id, user_id) VALUES
('우리는 이야기를 통해 세상을 이해하고, 이야기를 통해 미래를 꿈꾼다.', 1, 2),
('진정한 용기는 두려움을 느끼면서도 앞으로 나아가는 것이다.', 2, 2),
('우주는 우리 안에 있다. 우리는 별로 만들어졌다.', 3, 3);

-- Comments 샘플 데이터
INSERT INTO comments (content, quote_id, user_id) VALUES
('정말 공감되는 문장이네요.', 1, 3),
('이 구절이 제일 마음에 와닿았어요.', 2, 2),
('우주의 신비를 느낄 수 있는 문장입니다.', 3, 2);

-- Likes 샘플 데이터
INSERT INTO likes (quote_id, user_id) VALUES
(1, 2), (1, 3),
(2, 3),
(3, 2);

-- SavedQuotes 샘플 데이터
INSERT INTO saved_quotes (quote_id, user_id) VALUES
(1, 3),
(2, 2),
(3, 2);

-- Videos 샘플 데이터
INSERT INTO videos (video_url, description, quote_id) VALUES
('https://example.com/videos/1', '사피엔스의 핵심 메시지를 담은 영상', 1),
('https://example.com/videos/2', '아몬드의 감동적인 순간', 2),
('https://example.com/videos/3', '코스모스의 우주적 시각', 3);

-- Summaries 샘플 데이터
INSERT INTO summaries (summary_text, original_length, summary_length, book_id) VALUES
('인류의 진화와 문명의 발전 과정을 다룬 책', 1000, 100, 1),
('감정을 느끼지 못하는 소년의 성장 이야기', 800, 80, 2),
('우주의 신비와 과학적 세계관을 다룬 책', 1200, 120, 3);

-- UserInterests 샘플 데이터
INSERT INTO user_interests (recent_searches, favorite_genres, user_id) VALUES
('["사피엔스", "역사", "문명"]', '["역사", "과학"]', 2),
('["소설", "감동", "성장"]', '["소설", "에세이"]', 3);

-- UserRewards 샘플 데이터
INSERT INTO user_rewards (rank, reward_type, rewarded, user_id) VALUES
(1, 'AI_VIDEO_CUSTOM', true, 2),
(2, 'BOOK_GIFT_CARD', false, 3);

-- Rankings 샘플 데이터
INSERT INTO rankings (like_count, save_count, comment_count, period, quote_id) VALUES
(2, 1, 1, 'weekly', 1),
(1, 1, 1, 'weekly', 2),
(1, 1, 1, 'weekly', 3); 