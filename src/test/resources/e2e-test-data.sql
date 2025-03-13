-- 테스트 사용자 생성
INSERT INTO users (id, username, email, password, nickname, created_at, updated_at) 
VALUES (1, 'testuser', 'test@example.com', '$2a$10$eDhncK/4cNH2KE.Y51AWpeL8K2/wSKVYOA/XTPdCJNRh/BfzOkwIK', '테스트유저', NOW(), NOW());

-- 테스트 관리자 생성
INSERT INTO users (id, username, email, password, nickname, created_at, updated_at) 
VALUES (2, 'admin', 'admin@example.com', '$2a$10$eDhncK/4cNH2KE.Y51AWpeL8K2/wSKVYOA/XTPdCJNRh/BfzOkwIK', '관리자', NOW(), NOW());

-- 테스트 일반 사용자 생성
INSERT INTO users (id, username, email, password, nickname, created_at, updated_at) 
VALUES (3, 'user', 'user@example.com', '$2a$10$eDhncK/4cNH2KE.Y51AWpeL8K2/wSKVYOA/XTPdCJNRh/BfzOkwIK', '일반사용자', NOW(), NOW());

-- 테스트 두 번째 사용자 생성
INSERT INTO users (id, username, email, password, nickname, created_at, updated_at) 
VALUES (4, 'testuser2', 'test2@example.com', '$2a$10$eDhncK/4cNH2KE.Y51AWpeL8K2/wSKVYOA/XTPdCJNRh/BfzOkwIK', '테스트유저2', NOW(), NOW());

-- 사용자 권한 설정 (테이블 구조에 따라 수정 필요)
-- INSERT INTO user_roles (user_id, role_id) VALUES (1, 1); -- 일반 사용자 권한
-- INSERT INTO user_roles (user_id, role_id) VALUES (2, 2); -- 관리자 권한

-- 테스트용 명언 데이터
INSERT INTO quotes (id, content, author, created_at, updated_at, created_by) 
VALUES (1, '성공한 사람이 되려고 노력하기보다 가치있는 사람이 되려고 노력하라.', '알버트 아인슈타인', NOW(), NOW(), 1);

INSERT INTO quotes (id, content, author, created_at, updated_at, created_by) 
VALUES (2, '현재에 집중할 수 있다면 행복할 것이다.', '파울로 코엘료', NOW(), NOW(), 1);

-- 테스트용 콘텐츠 데이터
INSERT INTO contents (id, title, content, type, created_at, updated_at, created_by, quote_id) 
VALUES (1, '가치 있는 삶을 위한 성찰', '아인슈타인의 명언을 통해 배우는 가치 있는 삶에 대한 고찰', 'ARTICLE', NOW(), NOW(), 1, 1);

-- 테스트용 댓글 데이터
INSERT INTO comments (id, content_id, text, created_at, updated_at, created_by) 
VALUES (1, 1, '정말 인상 깊은 글입니다. 많은 생각을 하게 되네요.', NOW(), NOW(), 3);

-- 테스트용 답글 데이터
INSERT INTO comments (id, content_id, parent_id, text, created_at, updated_at, created_by) 
VALUES (2, 1, 1, '감사합니다. 더 좋은 글로 보답하겠습니다.', NOW(), NOW(), 1);

-- 테스트용 그룹 데이터
INSERT INTO groups (id, name, description, is_private, created_at, updated_at, created_by) 
VALUES (1, '독서 토론 모임', '다양한 책을 읽고 토론하는 모임입니다.', 0, NOW(), NOW(), 1);

-- 테스트용 그룹 멤버 데이터
-- INSERT INTO group_members (group_id, user_id, role, joined_at) 
-- VALUES (1, 1, 'OWNER', NOW()); 

-- 테스트 사용자 데이터
INSERT INTO users (username, password, email, nickname, role, created_at, updated_at) 
VALUES ('test_admin', '$2a$10$eDIJO.xBpYZv8aDYWAQO/uXYwCn37oQXv4aWQEQDVZcvmJUn5zKSa', 'admin@stoblyx.com', '관리자', 'ROLE_ADMIN', NOW(), NOW());

INSERT INTO users (username, password, email, nickname, role, created_at, updated_at) 
VALUES ('test_user', '$2a$10$eDIJO.xBpYZv8aDYWAQO/uXYwCn37oQXv4aWQEQDVZcvmJUn5zKSa', 'user@stoblyx.com', '일반사용자', 'ROLE_USER', NOW(), NOW());

-- 테스트 명언 데이터
INSERT INTO quotes (content, author, created_at, updated_at) 
VALUES ('삶이 있는 한 희망은 있다', '키케로', NOW(), NOW());

INSERT INTO quotes (content, author, created_at, updated_at) 
VALUES ('산다는것 그것은 치열한 전투이다', '로망로랑', NOW(), NOW());

-- 테스트 콘텐츠 데이터
INSERT INTO contents (user_id, title, content, content_type, status, view_count, like_count, created_at, updated_at) 
VALUES (2, '인생에 대한 고찰', '삶은 의미를 찾아가는 여정입니다. 키케로가 말했듯이 "삶이 있는 한 희망은 있다"라는 말을 항상 기억해야 합니다.', 'ARTICLE', 'PUBLISHED', 100, 50, NOW(), NOW());

-- 테스트 댓글 데이터
INSERT INTO comments (user_id, content_id, content, created_at, updated_at) 
VALUES (2, 1, '정말 좋은 글이네요. 많은 생각을 하게 됩니다.', NOW(), NOW());

-- 테스트 답글 데이터
INSERT INTO replies (user_id, comment_id, content, created_at, updated_at) 
VALUES (1, 1, '감사합니다. 앞으로도 좋은 글 올리겠습니다.', NOW(), NOW());

-- 테스트 그룹 데이터
INSERT INTO groups (name, description, created_by, created_at, updated_at) 
VALUES ('독서토론모임', '다양한 책을 읽고 토론하는 모임입니다.', 1, NOW(), NOW()); 