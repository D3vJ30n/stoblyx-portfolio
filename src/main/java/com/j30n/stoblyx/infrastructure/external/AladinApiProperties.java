package com.j30n.stoblyx.infrastructure.external;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * 알라딘 API 연동에 필요한 설정 프로퍼티를 관리하는 클래스
 */
@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "aladin.api")
public class AladinApiProperties {
    
    /**
     * 알라딘 API TTB 키
     */
    private String ttbKey;
    
    /**
     * 알라딘 API 기본 URL (상품 리스트 API)
     */
    private String apiUrl = "http://www.aladin.co.kr/ttb/api/ItemList.aspx";
    
    /**
     * 알라딘 검색 API URL
     */
    private String searchApiUrl = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx";
    
    /**
     * 알라딘 아이템 검색 API URL (상품 조회 API)
     */
    private String itemApiUrl = "http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx";
    
    // 명시적으로 기본 생성자 추가
    public AladinApiProperties() {
    }
    
    // 생성자 기반 바인딩 추가 (kebab-case를 camelCase로 변환)
    @ConstructorBinding
    public AladinApiProperties(
            String ttbKey,
            @DefaultValue("http://www.aladin.co.kr/ttb/api/ItemList.aspx") String apiUrl,
            @DefaultValue("http://www.aladin.co.kr/ttb/api/ItemSearch.aspx") String searchApiUrl,
            @DefaultValue("http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx") String itemApiUrl) {
        this.ttbKey = ttbKey;
        this.apiUrl = apiUrl;
        this.searchApiUrl = searchApiUrl;
        this.itemApiUrl = itemApiUrl;
    }
    
    // ttbKey 명시적 getter (Lombok이 생성하는 것과 동일하지만 명시적으로 추가)
    public String getTtbKey() {
        return this.ttbKey;
    }
    
    // getTtbkey 메서드 추가 (이전 코드와의 호환성 유지)
    public String getTtbkey() {
        return this.ttbKey;
    }
} 