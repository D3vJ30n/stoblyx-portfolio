package com.j30n.stoblyx.e2e.util;

import java.util.UUID;

/**
 * E2E 테스트에서 사용할 테스트 데이터를 생성하는 유틸리티 클래스
 */
public class TestDataGenerator {

    /**
     * 고유한 사용자 이름 생성
     * 
     * @return String 사용자 이름
     */
    public static String generateUniqueUsername() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    /**
     * 고유한 이메일 생성
     * 
     * @return String 이메일
     */
    public static String generateUniqueEmail() {
        return "test_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    }
    
    /**
     * 고유한 닉네임 생성
     * 
     * @return String 닉네임
     */
    public static String generateUniqueNickname() {
        return "닉네임_" + UUID.randomUUID().toString().substring(0, 4);
    }
    
    /**
     * 표준 테스트 비밀번호 생성
     * 
     * @return String 비밀번호
     */
    public static String generatePassword() {
        return "Password123!";
    }
    
    /**
     * 테스트 사용자 정보 생성
     * 
     * @return TestUser 테스트 사용자 정보
     */
    public static TestUser generateTestUser() {
        return new TestUser(
            generateUniqueUsername(),
            generateUniqueEmail(),
            generatePassword(),
            generateUniqueNickname()
        );
    }
    
    /**
     * 테스트 사용자 정보를 담는 내부 클래스
     */
    public static class TestUser {
        private final String username;
        private final String email;
        private final String password;
        private final String nickname;
        
        public TestUser(String username, String email, String password, String nickname) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.nickname = nickname;
        }
        
        public String getUsername() {
            return username;
        }
        
        public String getEmail() {
            return email;
        }
        
        public String getPassword() {
            return password;
        }
        
        public String getNickname() {
            return nickname;
        }
        
        @Override
        public String toString() {
            return "TestUser{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
        }
    }
} 