package com.j30n.stoblyx.e2e.util;

import com.aventstack.extentreports.Status;
import org.junit.jupiter.api.extension.*;

import java.util.Optional;

/**
 * E2E 테스트의 실행 전후에 실행될 확장 클래스
 */
public class E2ETestExtension implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        // ExtentReport 초기화
        ExtentReportManager.init();
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        // 테스트 시작
        String testName = getTestName(context);
        String testDescription = context.getDisplayName();
        ExtentReportManager.startTest(testName, testDescription);
        
        // 테스트 시작 로깅
        ExtentReportManager.log(testName, Status.INFO, "테스트 시작: " + testName);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        // 테스트 결과 로깅
        String testName = getTestName(context);
        Optional<Throwable> exception = context.getExecutionException();
        
        if (exception.isPresent()) {
            // 테스트 실패
            Throwable throwable = exception.get();
            ExtentReportManager.log(testName, Status.FAIL, "테스트 실패: " + throwable.getMessage());
            
            // 스택 트레이스 로깅
            StringBuilder stackTrace = new StringBuilder();
            for (StackTraceElement element : throwable.getStackTrace()) {
                stackTrace.append(element.toString()).append("\n");
            }
            ExtentReportManager.log(testName, Status.FAIL, "스택 트레이스: \n" + stackTrace);
        } else {
            // 테스트 성공
            ExtentReportManager.log(testName, Status.PASS, "테스트 성공: " + testName);
        }
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        // 보고서 저장
        ExtentReportManager.flush();
    }
    
    /**
     * 테스트 이름 가져오기
     * 
     * @param context 확장 컨텍스트
     * @return String 테스트 이름
     */
    private String getTestName(ExtensionContext context) {
        String className = context.getRequiredTestClass().getSimpleName();
        String methodName = context.getRequiredTestMethod().getName();
        return className + "." + methodName;
    }
} 