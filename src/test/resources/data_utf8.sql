-- 외래 키 제약 조건 비활성화
SET FOREIGN_KEY_CHECKS = 0;

-- --------------------------------------------------------
-- 기본 엔티티 데이터 (다른 테이블에서 참조하는 기본 데이터)
-- --------------------------------------------------------

-- 테스트 사용자 데이터 (users): 다른 많은 테이블에서 user_id로 참조
INSERT IGNORE INTO users (username, password, nickname, email, role, accountStatus, profileImageUrl, lastLoginAt, created_at, modified_at, is_deleted)
VALUES 
    ('test_user', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '테스트유저', 'test@example.com', 'USER', 'ACTIVE', 'https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 HOUR), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('test_admin', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '관리자', 'admin@example.com', 'ADMIN', 'ACTIVE', 'https://images.pexels.com/photos/1933873/pexels-photo-1933873.jpeg', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 1 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('regular_user', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '일반사용자', 'regular@example.com', 'USER', 'ACTIVE', 'https://images.pexels.com/photos/1300402/pexels-photo-1300402.jpeg', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 1 HOUR), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('premium_user', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '프리미엄유저', 'premium@example.com', 'USER', 'ACTIVE', 'https://images.pexels.com/photos/1043471/pexels-photo-1043471.jpeg', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 30 MINUTE), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('writer_user', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '콘텐츠작가', 'writer@example.com', 'USER', 'ACTIVE', 'https://images.pexels.com/photos/2379005/pexels-photo-2379005.jpeg', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 사용자 관심사 데이터 (user_interests): users 테이블 참조
INSERT IGNORE INTO user_interests (user_id, interests, created_at, modified_at, is_deleted)
VALUES
    (1, '철학,소설,심리학,미니멀리즘', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (2, '철학,역사,과학,예술', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (3, '소설,판타지,추리,공상과학', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (4, '자기계발,경영,심리학,과학', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (5, '소설,역사,철학,고전문학', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 인증 데이터 (auth): users 테이블 참조
INSERT IGNORE INTO auth (user_id, refreshToken, tokenType, expiryDate, lastUsedAt, created_at, modified_at, is_deleted)
VALUES
    (1, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test-refresh-token-1', 'BEARER', DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL 7 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (2, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test-refresh-token-2', 'BEARER', DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL 7 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (3, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test-refresh-token-3', 'BEARER', DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL 7 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (4, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test-refresh-token-4', 'BEARER', DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL 7 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (5, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test-refresh-token-5', 'BEARER', DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL 7 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 도서 데이터 (books): 콘텐츠의 기본이 되는 도서 정보
INSERT IGNORE INTO books (title, author, isbn, isbn13, description, publisher, publishDate, thumbnailUrl, cover, publicationYear, totalPages, avgReadingTime, averageRating, ratingCount, popularity, priceStandard, priceSales, categoryId, categoryName, link, adult, customerReviewRank, stockStatus, mallType, itemId, created_at, modified_at, is_deleted)
VALUES 
    ('철학의 즐거움', '알랭 드 보통', '9788900000000', '9788900000001', '현대인의 일상 속에서 철학이 어떻게 적용될 수 있는지 쉽고 재미있게 설명하는 책입니다. 플라톤부터 니체까지, 위대한 철학자들의 사상을 현대적 관점에서 재해석합니다.', '세계출판사', '2023-03-15', 'https://image.aladin.co.kr/product/30123/45/cover500/8900000001_1.jpg', 'https://image.aladin.co.kr/product/30123/45/cover/8900000001_1.jpg', 2023, 328, 840, 4.7, 1243, 78, 18000, 16200, '170', '철학/사상', 'https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=123456', 'N', 4.7, '재고있음', 'BOOKS', 'BOOK123456', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    
    ('사피엔스: 유인원에서 인공지능까지, 인간 역사의 대담한 질문', '유발 하라리', '9788934972464', '9788934972471', '인류의 역사와 미래에 대한 통찰력 있는 분석을 제공하는 세계적 베스트셀러. 인간이 어떻게 지구의 지배자가 되었는지, 그리고 어떻게 인지혁명, 농업혁명, 과학혁명을 통해 발전해왔는지를 설명합니다. 저자는 인류의 유전자와 환경보다는 허구를 믿는 능력이 인류 발전의 원동력이었다고 주장합니다.', '김영사', '2015-11-24', 'https://image.aladin.co.kr/product/5464/98/cover500/8934972467_1.jpg', 'https://image.aladin.co.kr/product/5464/98/cover/8934972467_1.jpg', 2015, 432, 840, 4.7, 1243, 78, 24000, 21600, '152', '역사/문명', 'https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=85486835', 'N', 4.8, '재고있음', 'BOOKS', 'BOOK85486835', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    
    ('코스모스', '칼 세이건', '9788983711892', '9788983711898', 
    '우주의 탄생부터 인류 문명의 발전까지 과학적 시각으로 바라본 세계적인 과학 교양서. 칼 세이건은 복잡한 우주 과학을 일반인도 이해할 수 있는 언어로 풀어내며, 인류가 우주에서 차지하는 위치와 의미를 성찰하게 합니다.', 
    '사이언스북스', '2006-12-20', 'https://image.aladin.co.kr/product/23/90/cover500/8983711892_1.jpg', 'https://image.aladin.co.kr/product/23/90/cover/8983711892_1.jpg', 
    2006, 680, 1200, 4.8, 1578, 92, 22000, 19800, '116', '과학/우주', 'https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=233345', 'N', 4.9, '재고있음', 'BOOKS', 'BOOK233345', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    
    ('1984', '조지 오웰', '9788937460777', '9788937460784', 
    '빅브라더가 지배하는 디스토피아 세계를 그린 20세기 최고의 정치 소설. 전체주의 사회의 언어 통제, 사상 감시, 역사 왜곡을 통해 인간성이 파괴되는 과정을 생생하게 묘사하며, 현대 사회에도 여전히 유효한 경고를 담고 있습니다.', 
    '민음사', '2012-08-01', 'https://image.aladin.co.kr/product/26231/42/cover500/8937460777_1.jpg', 'https://image.aladin.co.kr/product/26231/42/cover/8937460777_1.jpg', 
    2012, 355, 780, 4.6, 1892, 88, 13800, 12420, '101', '소설/고전', 'https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=654321', 'N', 4.7, '재고있음', 'BOOKS', 'BOOK654321', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    
    ('해리 포터와 마법사의 돌', 'J.K. 롤링', '9788983920997', '9788983921000', 
    '11살 소년 해리 포터가 자신이 마법사임을 알게 되면서 마법 세계에서 벌어지는 모험을 그린 판타지 소설. 호그와트 마법학교를 배경으로 우정, 용기, 선과 악의 대립을 다루며 전 세계적인 열풍을 일으켰습니다.', 
    '문학수첩', '2019-11-15', 'https://image.aladin.co.kr/product/26/35/cover500/8983920998_3.jpg', 'https://image.aladin.co.kr/product/26/35/cover/8983920998_3.jpg', 
    2019, 302, 720, 4.9, 2541, 96, 15000, 13500, '108', '소설/판타지', 'https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=128456', 'N', 4.9, '재고있음', 'BOOKS', 'BOOK128456', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    
    ('데미안', '헤르만 헤세', '9788931005882', '9788931005899', 
    '에밀 싱클레어라는 소년이 자아를 찾아가는 성장 과정을 그린 소설. 세계 1차 대전의 혼란 속에서 출간된 이 작품은 인간의 내면과 영혼의 성찰을 통해 진정한 자아 발견의 여정을 담고 있습니다.', 
    '민음사', '2009-04-20', 'https://image.aladin.co.kr/product/97/78/cover500/8931005881_1.jpg', 'https://image.aladin.co.kr/product/97/78/cover/8931005881_1.jpg', 
    2009, 248, 540, 4.5, 1245, 84, 10800, 9720, '101', '소설/고전', 'https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=975312', 'N', 4.6, '재고있음', 'BOOKS', 'BOOK975312', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    
    ('이기적 유전자', '리처드 도킨스', '9788932916675', '9788932916682', 
    '진화론의 새로운 관점을 제시한 과학서. 도킨스는 생존과 번식의 주체가 개체가 아닌 유전자라는 혁신적인 이론을 펼치며, 인간 행동의 생물학적 기반에 대한 깊은 통찰을 제공합니다.', 
    '을유문화사', '2018-10-20', 'https://image.aladin.co.kr/product/17094/0/cover500/8932916675_1.jpg', 'https://image.aladin.co.kr/product/17094/0/cover/8932916675_1.jpg', 
    2018, 516, 1100, 4.7, 1426, 87, 22000, 19800, '116', '과학/생물학', 'https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=458127', 'N', 4.8, '재고있음', 'BOOKS', 'BOOK458127', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    
    ('잃어버린 시간을 찾아서', '마르셀 프루스트', '9788937461033', '9788937461040', 
    '20세기 최고의 프랑스 소설로 평가받는 대작. 주인공의 기억을 통해 펼쳐지는 방대한 이야기는 시간, 예술, 사랑, 질투 등 인간 존재의 복잡한 층위를 탐구합니다.', 
    '민음사', '2012-10-15', 'https://image.aladin.co.kr/product/1357/35/cover500/8937461331_1.jpg', 'https://image.aladin.co.kr/product/1357/35/cover/8937461331_1.jpg', 
    2012, 420, 950, 4.4, 856, 72, 18800, 16920, '101', '소설/고전', 'https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=156723', 'N', 4.5, '재고있음', 'BOOKS', 'BOOK156723', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    
    ('소나기', '황순원', '9788937460104', '9788937460111', 
    '순수한 소년과 소녀의 사랑을 그린 한국 문학의 명작 단편소설. 아름답고 서정적인 문체와 비극적 결말을 통해 순수한 사랑의 아름다움과 덧없음을 표현했습니다.', 
    '민음사', '2015-05-10', 'https://image.aladin.co.kr/product/44/46/cover500/8937460106_2.jpg', 'https://image.aladin.co.kr/product/44/46/cover/8937460106_2.jpg', 
    2015, 128, 320, 4.8, 1865, 85, 8000, 7200, '101', '소설/한국문학', 'https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=443157', 'N', 4.7, '재고있음', 'BOOKS', 'BOOK443157', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    
    ('채식주의자', '한강', '9788936433598', '9788936433604', 
    '2016년 인터내셔널 부커상을 수상한 한국 소설. 육식을 거부하게 된 여성의 이야기를 통해 폭력과 욕망, 인간성에 대한 근본적인 질문을 던집니다.', 
    '창비', '2007-10-30', 'https://image.aladin.co.kr/product/631/29/cover500/8936433598_1.jpg', 'https://image.aladin.co.kr/product/631/29/cover/8936433598_1.jpg', 
    2007, 247, 580, 4.6, 1342, 90, 13000, 11700, '101', '소설/한국문학', 'https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=632951', 'N', 4.5, '재고있음', 'BOOKS', 'BOOK632951', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    
    ('스티브 잡스', '월터 아이작슨', '9788934972464', '9788934972471', 
    '애플 창업자 스티브 잡스의 공식 전기. 잡스 본인의 협조로 작성된 이 책은 그의 열정, 완벽주의, 혁신에 대한 집념, 그리고 그 이면의 복잡한 인물상을 생생하게 담아냅니다.', 
    '민음사', '2011-10-24', 'https://image.aladin.co.kr/product/954/46/cover500/8901150999_1.jpg', 'https://image.aladin.co.kr/product/954/46/cover/8901150999_1.jpg', 
    2011, 758, 1400, 4.7, 2134, 91, 25000, 22500, '325', '경제경영/인물', 'https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=125698', 'N', 4.8, '재고있음', 'BOOKS', 'BOOK125698', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    
    ('타이탄의 도구들', '팀 페리스', '9791195089192', '9791195089208', 
    '세계적인 기업가, 운동선수, 예술가 등 200명 이상의 성공한 인물들의 습관과 전략을 분석한 자기계발서. 그들의 사고방식, 일상 루틴, 생산성 향상법 등 실용적인 조언을 담고 있습니다.', 
    '토네이도', '2017-04-03', 'https://image.aladin.co.kr/product/11091/13/cover500/k662533475_1.jpg', 'https://image.aladin.co.kr/product/11091/13/cover/k662533475_1.jpg', 
    2017, 675, 1250, 4.5, 1624, 86, 19800, 17820, '336', '자기계발/성공', 'https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=367428', 'N', 4.6, '재고있음', 'BOOKS', 'BOOK367428', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 도서 장르 데이터 (book_genres): books 테이블 참조
INSERT IGNORE INTO book_genres (book_id, genre)
VALUES
    (1, '철학'),
    (1, '자기계발'),
    (1, '인문학'),
    (2, '역사'),
    (2, '과학'),
    (2, '인문학'),
    (3, '과학'),
    (3, '우주'),
    (3, '교양'),
    (4, '소설'),
    (4, '고전'),
    (4, '디스토피아'),
    (5, '판타지'),
    (5, '소설'),
    (5, '청소년'),
    (6, '소설'),
    (6, '고전'),
    (6, '철학'),
    (7, '과학'),
    (7, '생물학'),
    (7, '진화론'),
    (8, '소설'),
    (8, '고전'),
    (8, '프랑스문학'),
    (9, '소설'),
    (9, '한국문학'),
    (9, '단편'),
    (10, '소설'),
    (10, '한국문학'),
    (10, '현대문학'),
    (11, '전기'),
    (11, '경영'),
    (11, '기술'),
    (12, '자기계발'),
    (12, '경영'),
    (12, '생산성');

-- --------------------------------------------------------
-- 콘텐츠 및 상호작용 데이터
-- --------------------------------------------------------

-- 테스트 문구 데이터 (quotes): users와 books 테이블 참조
INSERT IGNORE INTO quotes (content, page, memo, like_count, save_count, user_id, book_id, created_at, modified_at, is_deleted)
VALUES 
    ('진정한 지혜는 자신이 모른다는 것을 아는 데서 시작한다.', 42, '소크라테스의 명언. 자기 자신에 대한 성찰의 중요성을 강조한다.', 203, 87, 1, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('우리는 한 번도 같은 강물에 발을 담글 수 없다. 모든 것은 흐르고 변한다.', 78, '헤라클레이토스의 유명한 격언. 변화의 본질에 대해 생각해보게 한다.', 158, 42, 1, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('사람들이 인생에서 후회하는 것은 자신이 한 행동이 아니라, 하지 않은 행동들이다.', 128, '도전과 용기에 관한 명언', 345, 120, 3, 2, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 10 DAY), CURRENT_TIMESTAMP(), 0),
    ('모든 진보는 당신의 편안함 영역 밖에 있다.', 67, '성장과 발전에 대한 중요한 인사이트', 290, 85, 4, 12, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 8 DAY), CURRENT_TIMESTAMP(), 0),
    ('삶에서 가장 중요한 것은 다른 사람이 당신에게 기대하는 삶을 사는 것이 아니라, 당신이 원하는 삶을 찾는 것이다.', 205, '자기 결정과 주체성에 대한 깊은 통찰', 412, 178, 5, 6, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 15 DAY), CURRENT_TIMESTAMP(), 0);

-- 테스트 저장된 인용구 데이터
INSERT IGNORE INTO saved_quotes (user_id, quote_id, note, created_at, modified_at, is_deleted)
VALUES
    (1, 1, '내 인생의 좌우명으로 삼고 싶은 문구', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (2, 1, '매일 아침 되새기기 좋은 문구', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (3, 2, '매일 아침 읽고 싶은 명언', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 7 DAY), CURRENT_TIMESTAMP(), 0),
    (4, 3, '내 인생의 지침이 되는 문구', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 5 DAY), CURRENT_TIMESTAMP(), 0),
    (5, 1, '어려운 시기를 극복하는데 도움이 되는 문구', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 3 DAY), CURRENT_TIMESTAMP(), 0);

-- 테스트 좋아요 데이터
INSERT IGNORE INTO likes (user_id, quote_id, created_at, modified_at, is_deleted)
VALUES 
    (1, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (2, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (3, 2, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 6 DAY), CURRENT_TIMESTAMP(), 0),
    (4, 1, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 4 DAY), CURRENT_TIMESTAMP(), 0),
    (5, 3, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 DAY), CURRENT_TIMESTAMP(), 0),
    (5, 2, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 1 DAY), CURRENT_TIMESTAMP(), 0);

-- 테스트 댓글 데이터
INSERT IGNORE INTO comments (content, user_id, quote_id, created_at, modified_at, is_deleted)
VALUES 
    ('이 문구는 제 인생의 모토가 되었습니다. 정말 의미있는 문장이네요.', 1, 1, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 5 DAY), CURRENT_TIMESTAMP(), 0),
    ('헤라클레이토스의 지혜가 담긴 명언입니다. 변화를 받아들이는 법을 배우게 됩니다.', 2, 1, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 4 DAY), CURRENT_TIMESTAMP(), 0),
    ('이 문구는 제 인생을 바꾸는 계기가 되었습니다. 정말 감사합니다.', 3, 3, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 DAY), CURRENT_TIMESTAMP(), 0),
    ('이 책의 핵심을 잘 요약한 인용구네요. 책 전체를 읽지 않아도 이 문장 하나만으로도 많은 것을 배웠습니다.', 4, 2, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 5 DAY), CURRENT_TIMESTAMP(), 0),
    ('매일 이 문구를 읽으며 하루를 시작하고 있습니다. 큰 동기부여가 됩니다.', 5, 1, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 4 DAY), CURRENT_TIMESTAMP(), 0);

-- 테스트 숏폼 콘텐츠 데이터
INSERT IGNORE INTO SHORT_FORM_CONTENTS (title, description, status, duration, viewCount, likeCount, shareCount, commentCount, created_at, modified_at, contentType, subtitles, videoUrl, thumbnailUrl, audioUrl, book_id, quote_id, is_deleted)
VALUES 
    ('철학이 삶에 미치는 영향', '플라톤과 아리스토텔레스의 철학이 현대 사회에 어떤 영향을 미치는지 알아보는 숏폼 콘텐츠입니다. 일상에서 철학적 사고를 적용하는 방법과 그 이점을 설명합니다.', 'PUBLISHED', 87, 24563, 4782, 1254, 342, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 32 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 30 DAY), 'VIDEO', '0:00 인트로\n0:15 철학이란 무엇인가?\n1:05 플라톤의 동굴 비유\n2:10 아리스토텔레스의 행복론\n3:25 현대사회에 적용하기\n4:05 마무리', 'https://www.pexels.com/video/people-walking-in-an-entrance-of-a-building-3129671/', 'https://images.pexels.com/photos/7034639/pexels-photo-7034639.jpeg', 'classpath:bgm/calm.mp3', 1, 1, 0),
    
    ('소설 속 인간 심리의 비밀', '문학 작품 속에 나타난 인간 심리의 다양한 측면을 분석하고, 현대 심리학과의 연관성을 탐구합니다. 소설이 어떻게 인간 본성을 통찰력 있게 표현하는지 알아봅니다.', 'PUBLISHED', 124, 18972, 3854, 987, 201, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 14 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 14 DAY), 'VIDEO', '0:00 도입\n0:20 소설과 심리학\n1:15 도스토예프스키의 인물 분석\n2:35 프로이트와 문학\n3:40 현대 소설의 심리묘사\n4:55 마무리', 'https://www.pexels.com/video/woman-reading-a-book-while-sitting-on-a-chair-2859232/', 'https://images.pexels.com/photos/2041556/pexels-photo-2041556.jpeg', 'classpath:bgm/neutral.mp3', 1, 2, 0),
    
    ('미니멀리즘: 단순함의 힘', '물질적 소유를 줄이고 정신적 풍요를 추구하는 미니멀리즘의 철학을 소개합니다. 일본의 \'단순함\'과 서양의 미니멀리즘을 비교하며 현대인의 삶에 적용할 수 있는 방법을 제시합니다.', 'PUBLISHED', 68, 35241, 8932, 2743, 489, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 7 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 6 DAY), 'VIDEO', '0:00 미니멀리즘이란\n0:45 일본의 와비사비와 미니멀리즘\n1:30 디지털 미니멀리즘\n2:15 미니멀 라이프 시작하기\n3:20 정리하는 습관\n4:10 결론', 'https://www.pexels.com/video/interior-of-a-contemporary-empty-room-3625225/', 'https://images.pexels.com/photos/6679526/pexels-photo-6679526.jpeg', 'classpath:bgm/calm.mp3', 1, 1, 0),
    
    ('고전 읽기의 즐거움', '고전 문학 작품을 현대적 관점에서 재해석하고, 일상에서 고전을 즐기는 방법을 소개합니다. 바쁜 현대인도 쉽게 고전의 지혜를 받아들일 수 있는 실용적인 팁을 제공합니다.', 'DRAFT', 95, 0, 0, 0, 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'AUDIO', '0:00 인트로듀스\n0:30 왜 고전을 읽어야 하는가\n1:25 고전의 현대적 가치\n2:40 짧은 시간에 고전 읽기\n3:55 추천 고전 5선\n4:30 마무리', 'https://www.pexels.com/video/close-up-video-of-turning-the-pages-of-a-book-5752729/', 'https://images.pexels.com/photos/1029141/pexels-photo-1029141.jpeg', 'classpath:bgm/happy.mp3', 1, 2, 0),
    
    ('시간 관리의 기술', '현대인의 시간 부족 문제를 해결하기 위한 철학적, 실용적 접근법을 다룹니다. 고대 스토아 철학자들의 지혜부터 최신 생산성 연구까지 폭넓게 살펴봅니다.', 'PUBLISHED', 112, 42891, 9874, 3421, 678, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 21 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 18 DAY), 'VIDEO', '0:00 시작\n0:25 시간의 주관성\n1:10 파레토 법칙\n2:05 딥 워크의 중요성\n3:15 디지털 디톡스\n4:25 마무리와 실천 방법', 'https://www.pexels.com/video/close-up-of-a-clock-854198/', 'https://images.pexels.com/photos/1095601/pexels-photo-1095601.jpeg', 'classpath:bgm/sad.mp3', 1, 1, 0),
    
    ('성공하는 리더의 7가지 습관', '성공한 리더들이 매일 실천하는 핵심 습관들을 소개합니다. 간단한 실천 방법부터 장기적인 성과까지 얻을 수 있는 방법을 알려드립니다.', 'PUBLISHED', 95, 32450, 6743, 1843, 452, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 25 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 25 DAY), 'VIDEO', '0:00 인트로\n0:30 습관 1: 목표 설정\n1:45 습관 2: 우선순위 정하기\n2:35 습관 3: 먼저 행동하기\n3:45 습관 4: 윈윈 전략\n4:30 마무리', 'https://www.pexels.com/video/people-working-in-an-office-1739310/', 'https://images.pexels.com/photos/935743/pexels-photo-935743.jpeg', 'classpath:bgm/inspirational.mp3', 11, 3, 0),
    
    ('디지털 디톡스의 중요성', '현대인들이 겪는 디지털 피로와 그 해결책으로서의 디지털 디톡스에 대해 알아봅니다. 스마트폰과 소셜미디어 중독에서 벗어나 진정한 자유를 찾는 방법을 소개합니다.', 'PUBLISHED', 73, 27834, 5932, 2105, 376, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 18 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 18 DAY), 'VIDEO', '0:00 도입\n0:35 디지털 중독의 징후\n1:25 디지털이 뇌에 미치는 영향\n2:10 디톡스 시작하기\n3:30 일상에 적용하는 팁\n4:15 결론', 'https://www.pexels.com/video/woman-using-her-smartphone-while-walking-in-the-city-3382624/', 'https://images.pexels.com/photos/3394650/pexels-photo-3394650.jpeg', 'classpath:bgm/peaceful.mp3', 11, 2, 0),
    
    ('독서의 과학: 뇌를 성장시키는 방법', '독서가 뇌의 인지 기능과 창의성을 어떻게 향상시키는지 과학적으로 분석합니다. 효과적인 독서 방법과 뇌를 최대한 활성화하는 독서 습관을 알아봅니다.', 'PUBLISHED', 82, 19356, 4132, 956, 287, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 12 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 12 DAY), 'VIDEO', '0:00 서론\n0:40 독서와 뇌의 관계\n1:30 집중력 향상 효과\n2:15 기억력과 독서\n3:10 창의성 개발\n4:05 마무리', 'https://www.pexels.com/video/hands-of-a-woman-reading-a-book-3510804/', 'https://images.pexels.com/photos/3059748/pexels-photo-3059748.jpeg', 'classpath:bgm/curious.mp3', 12, 1, 0);

-- 테스트 콘텐츠 인터랙션 데이터
INSERT IGNORE INTO CONTENT_INTERACTION (user_id, content_id, liked, bookmarked, viewedAt, created_at, modified_at, is_deleted)
VALUES 
    (1, 1, 1, 1, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 3 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (2, 1, 1, 0, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (3, 3, 1, 1, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 10 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (4, 2, 1, 0, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 8 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (5, 1, 0, 1, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 6 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (3, 2, 1, 1, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 5 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (4, 3, 0, 1, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 4 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 콘텐츠 댓글 데이터
INSERT IGNORE INTO content_comments (content, user_id, content_id, created_at, modified_at, is_deleted)
VALUES 
    ('정말 유익한 콘텐츠입니다! 철학이 어렵게만 느껴졌는데 이렇게 쉽게 설명해주셔서 감사합니다.', 1, 1, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 5 DAY), CURRENT_TIMESTAMP(), 0),
    ('플라톤의 동굴 비유를 이렇게 현대적으로 해석해주시니 훨씬 이해가 잘 되네요. 다음 콘텐츠도 기대됩니다.', 2, 1, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 4 DAY), CURRENT_TIMESTAMP(), 0),
    ('소설 속 인물들의 심리를 이렇게 분석해주시니 소설 읽을 때 더 깊이 이해할 수 있을 것 같아요.', 1, 2, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 DAY), CURRENT_TIMESTAMP(), 0),
    ('이 콘텐츠는 정말 제 삶을 변화시켰어요. 디지털 디톡스를 실천한 후로 정신적으로 훨씬 건강해졌습니다.', 3, 2, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 7 DAY), CURRENT_TIMESTAMP(), 0),
    ('독서의 중요성을 과학적으로 설명해줘서 좋았습니다. 이제 매일 30분씩은 꼭 책을 읽으려고 합니다.', 4, 3, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 6 DAY), CURRENT_TIMESTAMP(), 0),
    ('리더십에 관심이 많았는데, 이 영상에서 제시하는 7가지 습관은 정말 실용적이고 적용하기 쉬워 보입니다.', 5, 1, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 5 DAY), CURRENT_TIMESTAMP(), 0);

-- 테스트 인용구 요약 데이터
INSERT IGNORE INTO quote_summaries (content, algorithm, generatedAt, quality, quote_id, created_at, modified_at, is_deleted)
VALUES 
    ('소크라테스의 이 명언은 자기성찰과 겸손의 가치를 강조합니다. 무지를 인정하는 것이 진정한 지혜의 시작이라는 역설적 진리를 담고 있으며, 현대 사회에서도 매우 유의미한 메시지입니다.', 'GPT-4', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 7 DAY), 0.93, 3, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('이 짧은 문장은 개인의 성장이 편안함의 경계를 넘어설 때 일어난다는 강력한 메시지를 전달합니다. 심리학적으로 볼 때 이는 \'인지 부조화\'와 \'적응 학습\'의 개념과 연결되며, 불편함을 감수할 때 실제 발전이 이루어진다는 진실을 담고 있습니다.', 'GPT-4', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 7 DAY), 0.94, 4, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 미디어 리소스 데이터
INSERT IGNORE INTO media_resources (content_id, `type`, url, thumbnailUrl, description, duration, created_at, modified_at, is_deleted)
VALUES
    (1, 'VIDEO', 'https://www.pexels.com/video/time-lapse-video-of-stars-857195/', 'https://images.pexels.com/photos/1169754/pexels-photo-1169754.jpeg', '철학적 사고의 깊이를 표현한 밤하늘 타임랩스 영상', 120, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (2, 'VIDEO', 'https://www.pexels.com/video/a-woman-thinking-while-writing-5798232/', 'https://images.pexels.com/photos/3767411/pexels-photo-3767411.jpeg', '소설 집필 과정을 담은 영상 클립', 90, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (6, 'VIDEO', 'https://www.pexels.com/video/people-in-a-meeting-2277808/', 'https://images.pexels.com/photos/3184292/pexels-photo-3184292.jpeg', '리더십 워크샵 비디오 클립', 135, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (7, 'AUDIO', 'https://www.pexels.com/video/audio/5587/', 'https://images.pexels.com/photos/3756766/pexels-photo-3756766.jpeg', '명상 및 디지털 디톡스 안내 오디오', 240, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (8, 'VIDEO', 'https://www.pexels.com/video/close-up-footage-of-a-woman-reading-a-book-4722765/', 'https://images.pexels.com/photos/4132936/pexels-photo-4132936.jpeg', '독서와 뇌 활동 관련 교육 영상', 110, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

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
    (2, '행복', 'BOOK', 6, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (3, '심리학', 'BOOK', 12, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 5 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (4, '자기계발', 'QUOTE', 9, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 4 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (5, '철학', 'CONTENT', 7, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 3 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (3, '명상', 'BOOK', 5, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (4, '리더십', 'CONTENT', 10, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 1 DAY), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 검색어 프로필 데이터
INSERT IGNORE INTO search_term_profiles (search_term, search_count, user_demographic_data, related_terms, trend_data, created_at, modified_at, is_deleted)
VALUES
    ('철학', 3452, '{"age": {"20s": 45, "30s": 35, "40s": 20}, "gender": {"male": 60, "female": 40}}', '사상,윤리학,존재,플라톤,니체', '{"2023-01": 425, "2023-02": 380, "2023-03": 402, "2023-04": 512, "2023-05": 487}', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('사랑', 5721, '{"age": {"10s": 15, "20s": 40, "30s": 30, "40s": 15}, "gender": {"male": 35, "female": 65}}', '연애,결혼,관계,감정,로맨스', '{"2023-01": 650, "2023-02": 700, "2023-03": 655, "2023-04": 810, "2023-05": 780}', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 사용자 랭킹 점수 데이터
INSERT IGNORE INTO ranking_user_score (user_id, current_score, previous_score, rank_type, last_activity_date, created_at, modified_at, suspicious_activity, report_count, account_suspended, is_deleted)
VALUES
    (1, 1542, 1450, 'SILVER', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 1 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 30 DAY), CURRENT_TIMESTAMP(), 0, 0, 0, 0),
    (2, 2358, 1750, 'GOLD', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 45 DAY), CURRENT_TIMESTAMP(), 0, 0, 0, 0),
    (3, 1240, 1150, 'SILVER', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 1 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 45 DAY), CURRENT_TIMESTAMP(), 0, 0, 0, 0),
    (4, 3150, 2890, 'GOLD', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 3 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 60 DAY), CURRENT_TIMESTAMP(), 0, 0, 0, 0),
    (5, 5200, 4800, 'PLATINUM', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 75 DAY), CURRENT_TIMESTAMP(), 0, 0, 0, 0);

-- 테스트 사용자 활동 데이터
INSERT IGNORE INTO ranking_user_activity (user_id, activity_type, points, activity_date, reference_id, reference_type, created_at, modified_at, is_deleted)
VALUES
    (1, 'QUOTE_CREATE', 25, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 5 DAY), 1, 'QUOTE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (1, 'CONTENT_LIKE', 10, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 3 DAY), 1, 'CONTENT', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (2, 'COMMENT_CREATE', 15, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 DAY), 1, 'COMMENT', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (3, 'CONTENT_BOOKMARK', 15, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 8 DAY), 2, 'CONTENT', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (4, 'QUOTE_SAVE', 20, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 6 DAY), 3, 'QUOTE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (5, 'DAILY_VISIT', 5, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 4 DAY), NULL, NULL, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (3, 'CONTENT_SHARE', 25, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 3 DAY), 1, 'CONTENT', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (4, 'COMMENT_CREATE', 10, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 DAY), 3, 'COMMENT', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 랭킹 리더보드 데이터
INSERT IGNORE INTO ranking_leaderboard (user_id, username, score, rank_type, leaderboard_type, rank_position, period_start_date, period_end_date, created_at, modified_at, is_deleted)
VALUES
    (1, 'test_user', 1542, 'SILVER', 'WEEKLY', 3, DATE_SUB(CURRENT_DATE(), INTERVAL 7 DAY), CURRENT_DATE(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (2, 'test_admin', 2358, 'GOLD', 'WEEKLY', 1, DATE_SUB(CURRENT_DATE(), INTERVAL 7 DAY), CURRENT_DATE(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (3, 'regular_user', 1240, 'SILVER', 'WEEKLY', 5, DATE_SUB(CURRENT_DATE(), INTERVAL 7 DAY), CURRENT_DATE(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (4, 'premium_user', 3150, 'GOLD', 'WEEKLY', 2, DATE_SUB(CURRENT_DATE(), INTERVAL 7 DAY), CURRENT_DATE(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    (5, 'writer_user', 5200, 'PLATINUM', 'WEEKLY', 1, DATE_SUB(CURRENT_DATE(), INTERVAL 7 DAY), CURRENT_DATE(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 인기 검색어 데이터
INSERT IGNORE INTO popular_search_terms (search_term, search_count, popularity_score, last_updated_at, created_at, modified_at, is_deleted)
VALUES
    ('철학', 3452, 84.7, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 1 HOUR), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('소설', 2876, 76.3, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 HOUR), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('사랑', 5721, 92.8, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 3 HOUR), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('자기계발', 4521, 88.2, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 12 HOUR), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('심리학', 3987, 79.5, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 8 HOUR), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('리더십', 3654, 77.4, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 6 HOUR), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('명상', 2892, 65.3, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 4 HOUR), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0),
    ('미니멀리즘', 2687, 63.1, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 HOUR), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0);

-- 테스트 게이미피케이션 보상 데이터
INSERT IGNORE INTO gamification_rewards (user_id, reward_type, points, description, reference_id, reference_type, is_claimed, expiry_date, created_at, modified_at, is_deleted)
VALUES
    (1, 'DAILY_LOGIN', 10, '2025년 5월 10일 출석 체크 보상', 0, 'DAILY', 1, DATE_ADD(CURRENT_DATE(), INTERVAL 14 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 10 DAY), CURRENT_TIMESTAMP(), 0),
    (1, 'ACHIEVEMENT', 200, '북마스터 뱃지 획득 보상', 1, 'BADGE', 1, DATE_ADD(CURRENT_DATE(), INTERVAL 30 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 15 DAY), CURRENT_TIMESTAMP(), 0),
    (2, 'DAILY_LOGIN', 10, '2025년 5월 11일 출석 체크 보상', 0, 'DAILY', 1, DATE_ADD(CURRENT_DATE(), INTERVAL 7 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 4 DAY), CURRENT_TIMESTAMP(), 0),
    (3, 'CHALLENGE_COMPLETION', 50, '7일 연속 독서 챌린지 완료', 0, 'CHALLENGE', 1, DATE_ADD(CURRENT_DATE(), INTERVAL 14 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 10 DAY), CURRENT_TIMESTAMP(), 0),
    (4, 'CONTENT_CREATION', 100, '첫 숏폼 콘텐츠 생성 보상', 6, 'CONTENT', 1, DATE_ADD(CURRENT_DATE(), INTERVAL 21 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 8 DAY), CURRENT_TIMESTAMP(), 0),
    (5, 'REFERRAL', 30, '친구 초대 보상', 0, 'REFERRAL', 1, DATE_ADD(CURRENT_DATE(), INTERVAL 10 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 6 DAY), CURRENT_TIMESTAMP(), 0),
    (3, 'WEEKLY_ACTIVITY', 25, '주간 활동 보상', 0, 'WEEKLY', 0, DATE_ADD(CURRENT_DATE(), INTERVAL 5 DAY), DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 3 DAY), CURRENT_TIMESTAMP(), 0);

-- 테스트 포스트 데이터
INSERT IGNORE INTO posts (title, content, user_id, thumbnail_url, created_at, modified_at, is_deleted)
VALUES
    ('AI 시대의 독서법', 'AI 기술이 발전하는 시대에 독서의 중요성과 효과적인 독서 방법에 대해 이야기합니다. AI가 생성한 콘텐츠와 전통적인 도서의 차이점, 그리고 디지털 시대에 비판적 사고력을 기르기 위한 독서의 역할에 대해 살펴봅니다.', 3, 'https://images.pexels.com/photos/4143800/pexels-photo-4143800.jpeg', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 15 DAY), CURRENT_TIMESTAMP(), 0),
    ('미니멀리스트의 서재', '100권의 책을 소유하는 것보다 10권의 책을 깊이 읽는 것이 중요합니다. 이 글에서는 미니멀리즘 관점에서 개인 서재를 정리하고, 정말 가치 있는 책만 남기는 방법과 그 효과에 대해 공유합니다.', 4, 'https://images.pexels.com/photos/2228583/pexels-photo-2228583.jpeg', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 12 DAY), CURRENT_TIMESTAMP(), 0),
    ('고전을 읽는 현대적 방법', '플라톤, 아리스토텔레스, 니체와 같은 철학자들의 사상을 현대적 관점에서 해석하고 적용하는 방법에 대해 알아봅니다. 고전 철학이 현대 사회의 문제들에 어떤 통찰을 제공하는지 살펴봅니다.', 5, 'https://images.pexels.com/photos/6474485/pexels-photo-6474485.jpeg', DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 8 DAY), CURRENT_TIMESTAMP(), 0);

-- 외래 키 제약 조건 다시 활성화
SET FOREIGN_KEY_CHECKS = 1;
