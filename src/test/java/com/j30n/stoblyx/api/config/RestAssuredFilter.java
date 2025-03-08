package com.j30n.stoblyx.api.config;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/**
 * REST Assured 요청 및 응답을 ExtentReports에 기록하는 필터
 */
public class RestAssuredFilter implements Filter {
    
    @Override
    public Response filter(FilterableRequestSpecification requestSpec, 
                          FilterableResponseSpecification responseSpec, 
                          FilterContext filterContext) {
        
        // 요청 시작 시간
        long startTime = System.currentTimeMillis();
        
        // 실제 요청 실행
        Response response = filterContext.next(requestSpec, responseSpec);
        
        // 응답 시간 계산
        long responseTime = System.currentTimeMillis() - startTime;
        
        // ExtentReports에 요청 정보 추가
        try {
            ExtentTest test = ExtentReportManager.getTest();
            
            if (test != null) {
                // 요청 URL 및 메소드 정보
                String requestUrl = requestSpec.getURI();
                String requestMethod = requestSpec.getMethod();
                
                test.info("API 요청: " + requestMethod + " " + requestUrl);
                
                // 요청 헤더 정보
                if (requestSpec.getHeaders() != null && requestSpec.getHeaders().asList().size() > 0) {
                    String headers = formatHeaders(requestSpec);
                    test.info("요청 헤더: " + MarkupHelper.createCodeBlock(headers, CodeLanguage.JSON));
                }
                
                // 요청 바디 정보
                if (requestSpec.getBody() != null) {
                    test.info("요청 본문: " + MarkupHelper.createCodeBlock(requestSpec.getBody().toString(), CodeLanguage.JSON));
                }
                
                // 응답 상태 코드 및 시간
                int statusCode = response.getStatusCode();
                test.info("응답 상태: " + statusCode + " (" + responseTime + "ms)");
                
                // 응답 헤더 정보
                if (response.getHeaders() != null && response.getHeaders().asList().size() > 0) {
                    String responseHeaders = formatResponseHeaders(response);
                    test.info("응답 헤더: " + MarkupHelper.createCodeBlock(responseHeaders, CodeLanguage.JSON));
                }
                
                // 응답 바디 정보
                if (response.getBody() != null && !response.getBody().asString().isEmpty()) {
                    test.info("응답 본문: " + MarkupHelper.createCodeBlock(response.getBody().prettyPrint(), CodeLanguage.JSON));
                }
            }
        } catch (Exception e) {
            System.err.println("REST Assured 로깅 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        
        return response;
    }
    
    /**
     * 요청 헤더 포맷팅
     */
    private String formatHeaders(FilterableRequestSpecification requestSpec) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        requestSpec.getHeaders().forEach(header -> 
            sb.append("  \"").append(header.getName()).append("\": \"")
              .append(header.getValue()).append("\",\n")
        );
        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2); // 마지막 쉼표 제거
        }
        sb.append("\n}");
        return sb.toString();
    }
    
    /**
     * 응답 헤더 포맷팅
     */
    private String formatResponseHeaders(Response response) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        response.getHeaders().forEach(header -> 
            sb.append("  \"").append(header.getName()).append("\": \"")
              .append(header.getValue()).append("\",\n")
        );
        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2); // 마지막 쉼표 제거
        }
        sb.append("\n}");
        return sb.toString();
    }
} 