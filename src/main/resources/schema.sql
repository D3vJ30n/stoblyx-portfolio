-- Users 테이블
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '사용자 고유 식별자',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '사용자 이메일',
    password VARCHAR(100) NOT NULL COMMENT '암호화된 비밀번호',
    name VARCHAR(50) NOT NULL COMMENT '사용자 이름',
    role VARCHAR(20) NOT NULL COMMENT '사용자 권한',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    created_by VARCHAR(100) COMMENT '생성자',
    modified_by VARCHAR(100) COMMENT '수정자',
    INDEX idx_user_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자';

-- Books 테이블
CREATE TABLE IF NOT EXISTS books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '책 고유 식별자',
    title VARCHAR(255) NOT NULL COMMENT '책 제목',
    author VARCHAR(100) NOT NULL COMMENT '저자',
    genre VARCHAR(100) COMMENT '책 장르',
    published_at DATE COMMENT '출판일',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    created_by VARCHAR(100) COMMENT '생성자',
    modified_by VARCHAR(100) COMMENT '수정자',
    INDEX idx_book_title (title),
    INDEX idx_book_author (author)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='책';

-- Quotes 테이블
CREATE TABLE IF NOT EXISTS quotes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '문구 고유 식별자',
    content TEXT NOT NULL COMMENT '문구 내용',
    book_id BIGINT NOT NULL COMMENT '문구가 속한 책',
    user_id BIGINT NOT NULL COMMENT '문구를 등록한 사용자',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    created_by VARCHAR(100) COMMENT '생성자',
    modified_by VARCHAR(100) COMMENT '수정자',
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_quote_book_id (book_id),
    INDEX idx_quote_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='문구';

-- Comments 테이블
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '댓글 고유 식별자',
    content TEXT NOT NULL COMMENT '댓글 내용',
    quote_id BIGINT NOT NULL COMMENT '댓글이 달린 문구',
    user_id BIGINT NOT NULL COMMENT '댓글을 작성한 사용자',
    parent_id BIGINT COMMENT '부모 댓글 (대댓글인 경우)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    created_by VARCHAR(100) COMMENT '생성자',
    modified_by VARCHAR(100) COMMENT '수정자',
    FOREIGN KEY (quote_id) REFERENCES quotes(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (parent_id) REFERENCES comments(id),
    INDEX idx_comment_quote_id (quote_id),
    INDEX idx_comment_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='댓글';

-- Likes 테이블
CREATE TABLE IF NOT EXISTS likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '좋아요 고유 식별자',
    quote_id BIGINT NOT NULL COMMENT '좋아요가 달린 문구',
    user_id BIGINT NOT NULL COMMENT '좋아요를 누른 사용자',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    created_by VARCHAR(100) COMMENT '생성자',
    modified_by VARCHAR(100) COMMENT '수정자',
    FOREIGN KEY (quote_id) REFERENCES quotes(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uk_like_quote_user (quote_id, user_id),
    INDEX idx_like_quote_id (quote_id),
    INDEX idx_like_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='좋아요';

-- SavedQuotes 테이블
CREATE TABLE IF NOT EXISTS saved_quotes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '저장된 문구 고유 식별자',
    quote_id BIGINT NOT NULL COMMENT '저장된 문구',
    user_id BIGINT NOT NULL COMMENT '문구를 저장한 사용자',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    created_by VARCHAR(100) COMMENT '생성자',
    modified_by VARCHAR(100) COMMENT '수정자',
    FOREIGN KEY (quote_id) REFERENCES quotes(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uk_saved_quote_quote_user (quote_id, user_id),
    INDEX idx_saved_quote_quote_id (quote_id),
    INDEX idx_saved_quote_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='저장된 문구';

-- Videos 테이블
CREATE TABLE IF NOT EXISTS videos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '영상 고유 식별자',
    video_url VARCHAR(255) NOT NULL COMMENT '영상 URL',
    description TEXT COMMENT '영상 설명',
    quote_id BIGINT NOT NULL UNIQUE COMMENT '연관된 문구',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    created_by VARCHAR(100) COMMENT '생성자',
    modified_by VARCHAR(100) COMMENT '수정자',
    FOREIGN KEY (quote_id) REFERENCES quotes(id),
    INDEX idx_video_quote_id (quote_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='영상';

-- Summaries 테이블
CREATE TABLE IF NOT EXISTS summaries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '요약 고유 식별자',
    summary_text TEXT NOT NULL COMMENT '요약 내용',
    original_length INT NOT NULL COMMENT '원본 텍스트 길이',
    summary_length INT NOT NULL COMMENT '요약된 텍스트 길이',
    book_id BIGINT NOT NULL UNIQUE COMMENT '요약된 책',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    created_by VARCHAR(100) COMMENT '생성자',
    modified_by VARCHAR(100) COMMENT '수정자',
    FOREIGN KEY (book_id) REFERENCES books(id),
    INDEX idx_summary_book_id (book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='요약';

-- UserInterests 테이블
CREATE TABLE IF NOT EXISTS user_interests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '사용자 관심사 고유 식별자',
    recent_searches TEXT COMMENT '최근 검색어 목록 (JSON 형식)',
    favorite_genres TEXT COMMENT '선호 장르 (JSON 형식)',
    user_id BIGINT NOT NULL UNIQUE COMMENT '관심사를 가진 사용자',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    created_by VARCHAR(100) COMMENT '생성자',
    modified_by VARCHAR(100) COMMENT '수정자',
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_interest_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 관심사';

-- UserRewards 테이블
CREATE TABLE IF NOT EXISTS user_rewards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '보상 고유 식별자',
    rank INT NOT NULL COMMENT '사용자의 랭킹 위치',
    reward_type VARCHAR(50) NOT NULL COMMENT '지급된 보상 유형',
    rewarded BOOLEAN NOT NULL DEFAULT FALSE COMMENT '보상 지급 여부',
    user_id BIGINT NOT NULL COMMENT '보상을 받은 사용자',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    created_by VARCHAR(100) COMMENT '생성자',
    modified_by VARCHAR(100) COMMENT '수정자',
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_reward_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 보상';

-- Rankings 테이블
CREATE TABLE IF NOT EXISTS rankings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '랭킹 고유 식별자',
    like_count INT NOT NULL DEFAULT 0 COMMENT '좋아요 수',
    save_count INT NOT NULL DEFAULT 0 COMMENT '저장 횟수',
    comment_count INT NOT NULL DEFAULT 0 COMMENT '댓글 수',
    period VARCHAR(20) NOT NULL COMMENT '집계 기간 (weekly, monthly)',
    quote_id BIGINT NOT NULL COMMENT '랭킹을 집계할 문구',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    created_by VARCHAR(100) COMMENT '생성자',
    modified_by VARCHAR(100) COMMENT '수정자',
    FOREIGN KEY (quote_id) REFERENCES quotes(id),
    INDEX idx_ranking_quote_id (quote_id),
    INDEX idx_ranking_period (period)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='랭킹'; 