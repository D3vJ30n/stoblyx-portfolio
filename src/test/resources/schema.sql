DROP TABLE IF EXISTS likes CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS saved_quotes CASCADE;
DROP TABLE IF EXISTS quote_summaries CASCADE;
DROP TABLE IF EXISTS summaries CASCADE;
DROP TABLE IF EXISTS quotes CASCADE;
DROP TABLE IF EXISTS book_genres CASCADE;
DROP TABLE IF EXISTS books CASCADE;
DROP TABLE IF EXISTS user_interests CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS SHORT_FORM_CONTENTS CASCADE;
DROP TABLE IF EXISTS CONTENT_INTERACTION CASCADE;
DROP TABLE IF EXISTS content_comments CASCADE;
DROP TABLE IF EXISTS media_resources CASCADE;
DROP TABLE IF EXISTS auth CASCADE;
DROP TABLE IF EXISTS user_similarities CASCADE;
DROP TABLE IF EXISTS system_settings CASCADE;
DROP TABLE IF EXISTS search CASCADE;
DROP TABLE IF EXISTS search_term_profiles CASCADE;
DROP TABLE IF EXISTS ranking_user_score CASCADE;
DROP TABLE IF EXISTS ranking_user_activity CASCADE;
DROP TABLE IF EXISTS ranking_leaderboard CASCADE;
DROP TABLE IF EXISTS ranking_badge CASCADE;
DROP TABLE IF EXISTS ranking_achievement CASCADE;
DROP TABLE IF EXISTS posts CASCADE;
DROP TABLE IF EXISTS popular_search_terms CASCADE;
DROP TABLE IF EXISTS gamification_rewards CASCADE;
DROP TABLE IF EXISTS content_likes CASCADE;
DROP TABLE IF EXISTS content_bookmarks CASCADE;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    email VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    accountStatus VARCHAR(20) DEFAULT 'ACTIVE',
    profileImageUrl VARCHAR(255),
    lastLoginAt TIMESTAMP,
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT uk_username UNIQUE (username),
    CONSTRAINT uk_email UNIQUE (email)
);

CREATE TABLE user_interests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    interests TEXT,
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT uk_user_interests UNIQUE (user_id),
    CONSTRAINT fk_user_interests_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE auth (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    refreshToken VARCHAR(255),
    tokenType VARCHAR(20) NOT NULL,
    expiryDate TIMESTAMP NOT NULL,
    lastUsedAt TIMESTAMP,
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT uk_auth_user UNIQUE (user_id),
    CONSTRAINT fk_auth_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(100) NOT NULL,
    isbn VARCHAR(13),
    description VARCHAR(2000),
    publisher VARCHAR(100),
    publishDate DATE,
    thumbnailUrl VARCHAR(255),
    publicationYear INTEGER,
    totalPages INTEGER,
    avgReadingTime INTEGER,
    averageRating DOUBLE,
    ratingCount INTEGER,
    popularity INTEGER DEFAULT 0,
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE book_genres (
    book_id BIGINT NOT NULL,
    genre VARCHAR(100) NOT NULL,
    PRIMARY KEY (book_id, genre),
    CONSTRAINT fk_book_genres_book FOREIGN KEY (book_id) REFERENCES books(id)
);

CREATE TABLE quotes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    page INT,
    memo TEXT,
    like_count INTEGER DEFAULT 0,
    save_count INTEGER DEFAULT 0,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_quotes_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_quotes_book FOREIGN KEY (book_id) REFERENCES books(id)
);

CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    quote_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_comments_quote FOREIGN KEY (quote_id) REFERENCES quotes(id)
);

CREATE TABLE likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    quote_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT uk_likes UNIQUE (user_id, quote_id),
    CONSTRAINT fk_likes_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_likes_quote FOREIGN KEY (quote_id) REFERENCES quotes(id)
);

CREATE TABLE saved_quotes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    quote_id BIGINT NOT NULL,
    note VARCHAR(255),
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT uk_saved_quotes UNIQUE (user_id, quote_id),
    CONSTRAINT fk_saved_quotes_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_saved_quotes_quote FOREIGN KEY (quote_id) REFERENCES quotes(id)
);

CREATE TABLE quote_summaries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(1000) NOT NULL,
    algorithm VARCHAR(50),
    generatedAt TIMESTAMP,
    quality DOUBLE DEFAULT 0.0,
    quote_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_quote_summaries_quote FOREIGN KEY (quote_id) REFERENCES quotes(id)
);

CREATE TABLE SHORT_FORM_CONTENTS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(1000),
    status VARCHAR(20) NOT NULL,
    duration INT,
    viewCount INT DEFAULT 0,
    likeCount INT DEFAULT 0,
    shareCount INT DEFAULT 0,
    commentCount INT DEFAULT 0,
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    contentType VARCHAR(50),
    subtitles TEXT,
    videoUrl TEXT,
    thumbnailUrl TEXT,
    audioUrl TEXT,
    book_id BIGINT,
    quote_id BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_short_form_contents_book FOREIGN KEY (book_id) REFERENCES books(id),
    CONSTRAINT fk_short_form_contents_quote FOREIGN KEY (quote_id) REFERENCES quotes(id)
);

CREATE TABLE CONTENT_INTERACTION (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content_id BIGINT NOT NULL,
    liked BOOLEAN DEFAULT FALSE,
    bookmarked BOOLEAN DEFAULT FALSE,
    viewedAt TIMESTAMP,
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_content_interactions_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_content_interactions_content FOREIGN KEY (content_id) REFERENCES SHORT_FORM_CONTENTS(id),
    CONSTRAINT uk_content_interactions UNIQUE (user_id, content_id)
);

CREATE TABLE content_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    content_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_content_comments_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_content_comments_content FOREIGN KEY (content_id) REFERENCES SHORT_FORM_CONTENTS(id)
);

CREATE TABLE media_resources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    url TEXT NOT NULL,
    thumbnailUrl VARCHAR(255),
    description VARCHAR(1000),
    duration INT,
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_media_resources_content FOREIGN KEY (content_id) REFERENCES SHORT_FORM_CONTENTS(id)
);

CREATE TABLE user_similarities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_user_id BIGINT NOT NULL,
    target_user_id BIGINT NOT NULL,
    similarity_score DOUBLE NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_similarities_source_user FOREIGN KEY (source_user_id) REFERENCES users(id),
    CONSTRAINT fk_similarities_target_user FOREIGN KEY (target_user_id) REFERENCES users(id),
    CONSTRAINT uk_user_similarities UNIQUE (source_user_id, target_user_id)
);

CREATE TABLE system_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(100) NOT NULL,
    setting_value VARCHAR(1000) NOT NULL,
    description VARCHAR(500),
    category VARCHAR(50) NOT NULL,
    is_encrypted BOOLEAN NOT NULL DEFAULT FALSE,
    is_system_managed BOOLEAN NOT NULL DEFAULT FALSE,
    last_modified_by BIGINT,
    default_value VARCHAR(1000),
    validation_pattern VARCHAR(255),
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT uk_setting_key UNIQUE (setting_key)
);

CREATE TABLE search (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    search_term VARCHAR(255) NOT NULL,
    search_type VARCHAR(50) NOT NULL,
    search_count INT DEFAULT 1,
    last_searched_at TIMESTAMP,
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_search_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE search_term_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    search_term VARCHAR(255) NOT NULL,
    search_count INT DEFAULT 0,
    user_demographic_data TEXT,
    related_terms TEXT,
    trend_data TEXT,
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT uk_search_term UNIQUE (search_term)
);

CREATE TABLE ranking_user_score (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    current_score INT NOT NULL,
    previous_score INT,
    rank_type VARCHAR(20) NOT NULL,
    last_activity_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP,
    suspicious_activity BOOLEAN NOT NULL DEFAULT FALSE,
    report_count INT NOT NULL DEFAULT 0,
    account_suspended BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE ranking_user_activity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    activity_type VARCHAR(50) NOT NULL,
    points INT NOT NULL,
    activity_date TIMESTAMP NOT NULL,
    reference_id BIGINT,
    reference_type VARCHAR(50),
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_ranking_activity_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE ranking_leaderboard (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    username VARCHAR(50) NOT NULL,
    score INT NOT NULL,
    rank_type VARCHAR(20) NOT NULL,
    leaderboard_type VARCHAR(20) NOT NULL,
    rank_position INT,
    period_start_date TIMESTAMP NOT NULL,
    period_end_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_leaderboard_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE ranking_badge (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    image_url VARCHAR(255),
    badge_type VARCHAR(50) NOT NULL,
    requirement_type VARCHAR(50) NOT NULL,
    threshold_value INT NOT NULL,
    points_awarded INT DEFAULT 0,
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE ranking_achievement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    badge_id BIGINT NOT NULL,
    achieved_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_achievement_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_achievement_badge FOREIGN KEY (badge_id) REFERENCES ranking_badge(id),
    CONSTRAINT uk_user_badge UNIQUE (user_id, badge_id)
);

CREATE TABLE posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    thumbnail_url VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_posts_user FOREIGN KEY (user_id) REFERENCES users(id)
);

DROP TABLE IF EXISTS popular_search_terms CASCADE;
CREATE TABLE popular_search_terms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    search_term VARCHAR(255) NOT NULL,
    search_count INT NOT NULL DEFAULT 0,
    popularity_score DOUBLE NOT NULL DEFAULT 1.0,
    last_updated_at TIMESTAMP,
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT uk_search_term UNIQUE (search_term)
);

CREATE TABLE gamification_rewards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    reward_type VARCHAR(50) NOT NULL,
    points INT NOT NULL DEFAULT 0,
    description VARCHAR(255),
    reference_id BIGINT,
    reference_type VARCHAR(50),
    is_claimed BOOLEAN DEFAULT FALSE,
    expiry_date TIMESTAMP,
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_rewards_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE content_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_content_likes_content FOREIGN KEY (content_id) REFERENCES SHORT_FORM_CONTENTS(id),
    CONSTRAINT fk_content_likes_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uk_content_likes UNIQUE (content_id, user_id)
);

CREATE TABLE content_bookmarks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_content_bookmarks_content FOREIGN KEY (content_id) REFERENCES SHORT_FORM_CONTENTS(id),
    CONSTRAINT fk_content_bookmarks_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uk_content_bookmarks UNIQUE (content_id, user_id)
);

CREATE TABLE summaries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    chapter VARCHAR(100),
    page VARCHAR(50),
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_summaries_book FOREIGN KEY (book_id) REFERENCES books(id)
);