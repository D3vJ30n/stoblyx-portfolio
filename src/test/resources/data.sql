-- 테스트 사용자 데이터
INSERT INTO users (username, password, nickname, email, role, created_at, updated_at, is_deleted)
VALUES 
    ('test_user', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '테스트유저', 'test@example.com', 'USER', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    ('test_admin', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '관리자', 'admin@example.com', 'ADMIN', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);

-- 테스트 도서 데이터
INSERT INTO books (title, author, isbn, publisher, published_date, created_at, updated_at, is_deleted)
VALUES 
    ('테스트 도서', '테스트 작가', '9788900000000', '테스트 출판사', CURRENT_DATE(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);

-- 테스트 문구 데이터
INSERT INTO quotes (content, page_number, user_id, book_id, created_at, updated_at, is_deleted)
VALUES 
    ('첫 번째 테스트 문구입니다.', 1, 1, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    ('두 번째 테스트 문구입니다.', 2, 1, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);

-- 테스트 좋아요 데이터
INSERT INTO likes (user_id, quote_id, created_at, updated_at, is_deleted)
VALUES 
    (1, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    (2, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);

-- 테스트 댓글 데이터
INSERT INTO comments (content, user_id, quote_id, created_at, updated_at, is_deleted)
VALUES 
    ('첫 번째 테스트 댓글입니다.', 1, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false),
    ('두 번째 테스트 댓글입니다.', 2, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);