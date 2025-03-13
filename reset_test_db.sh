#!/bin/bash
# 스토블릭스 테스트 데이터베이스 초기화 및 테스트 데이터 적용 스크립트

# Docker MySQL 컨테이너 정보 (환경에 맞게 수정하세요)
CONTAINER_NAME="stoblyx-mysql"
DB_USER="stoblyx_user"
DB_PASS="6188"
DB_NAME="stoblyx_sandbox_db"

# 색상 변수 설정
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${YELLOW}스토블릭스 테스트 데이터베이스 초기화 스크립트 (Docker)${NC}"
echo "======================================================"
echo

# 데이터베이스 초기화
echo -e "${GREEN}[1/3]${NC} 데이터베이스 초기화 중..."
docker exec -i $CONTAINER_NAME mysql -u$DB_USER -p$DB_PASS --default-character-set=utf8mb4 -e "DROP DATABASE IF EXISTS $DB_NAME; CREATE DATABASE $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; USE $DB_NAME;"

if [ $? -ne 0 ]; then
    echo -e "${RED}오류: 데이터베이스 초기화 실패${NC}"
    exit 1
fi

# 스키마 적용
echo -e "${GREEN}[2/3]${NC} 스키마 적용 중..."
cat src/test/resources/schema.sql | docker exec -i $CONTAINER_NAME mysql -u$DB_USER -p$DB_PASS --default-character-set=utf8mb4 $DB_NAME

if [ $? -ne 0 ]; then
    echo -e "${RED}오류: 스키마 적용 실패${NC}"
    exit 1
fi

# 데이터 적용
echo -e "${GREEN}[3/3]${NC} 테스트 데이터 적용 중..."
cat src/test/resources/data.sql | docker exec -i $CONTAINER_NAME mysql -u$DB_USER -p$DB_PASS --default-character-set=utf8mb4 $DB_NAME

if [ $? -ne 0 ]; then
    echo -e "${RED}오류: 테스트 데이터 적용 실패${NC}"
    exit 1
fi

echo
echo -e "${GREEN}완료!${NC} 데이터베이스 '$DB_NAME'가 초기화되었고 테스트 데이터가 적용되었습니다."
echo "======================================================"
echo -e "데이터베이스 정보:"
echo -e "  컨테이너: ${YELLOW}$CONTAINER_NAME${NC}"
echo -e "  데이터베이스: ${YELLOW}$DB_NAME${NC}"
echo -e "  사용자: ${YELLOW}$DB_USER${NC}"
echo
echo -e "MySQL에 접속하려면: ${YELLOW}docker exec -it $CONTAINER_NAME mysql -u$DB_USER -p$DB_PASS $DB_NAME${NC}"
echo "======================================================" 