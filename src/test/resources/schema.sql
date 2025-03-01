DROP TABLE IF EXISTS likes CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS saved_quotes CASCADE;
DROP TABLE IF EXISTS summaries CASCADE;
DROP TABLE IF EXISTS videos CASCADE;
DROP TABLE IF EXISTS quotes CASCADE;
DROP TABLE IF EXISTS book CASCADE;
DROP TABLE IF EXISTS user_interests CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    email VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT uk_username UNIQUE (username),
    CONSTRAINT uk_email UNIQUE (email)
);

CREATE TABLE user_interests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    interests TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT uk_user_interests UNIQUE (user_id),
    CONSTRAINT fk_user_interests_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE book (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(100) NOT NULL,
    isbn VARCHAR(13),
    publisher VARCHAR(100),
    published_date DATE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE quotes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    page_number INT,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_quotes_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_quotes_book FOREIGN KEY (book_id) REFERENCES book(id)
);

CREATE TABLE videos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quote_id BIGINT NOT NULL,
    url VARCHAR(255) NOT NULL,
    thumbnail_url VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT uk_videos_quote UNIQUE (quote_id),
    CONSTRAINT fk_videos_quote FOREIGN KEY (quote_id) REFERENCES quotes(id)
);

CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    quote_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_comments_quote FOREIGN KEY (quote_id) REFERENCES quotes(id)
);

CREATE TABLE likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    quote_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    CONSTRAINT uk_likes UNIQUE (user_id, quote_id),
    CONSTRAINT fk_likes_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_likes_quote FOREIGN KEY (quote_id) REFERENCES quotes(id)
);

CREATE TABLE saved_quotes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    quote_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    CONSTRAINT uk_saved_quotes UNIQUE (user_id, quote_id),
    CONSTRAINT fk_saved_quotes_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_saved_quotes_quote FOREIGN KEY (quote_id) REFERENCES quotes(id)
);

CREATE TABLE summaries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_summaries_book FOREIGN KEY (book_id) REFERENCES book(id)
);