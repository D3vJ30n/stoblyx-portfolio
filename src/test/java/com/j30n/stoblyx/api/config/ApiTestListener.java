package com.j30n.stoblyx.api.config;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * REST Assured API 테스트를 위한 JUnit 리스너
 */
public class ApiTestListener implements BeforeEachCallback, AfterEachCallback, AfterAllCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        String testName = getTestName(context);
        String testDescription = getTestDescription(context);
        
        // 테스트 시작 시 ExtentReports에 테스트 추가
        ExtentReportManager.createTest(testName, testDescription);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        ExtentTest test = ExtentReportManager.getTest();
        
        // 테스트 결과에 따라 상태 갱신
        if (context.getExecutionException().isPresent()) {
            Throwable exception = context.getExecutionException().get();
            test.log(Status.FAIL, "테스트 실패: " + exception.getMessage());
            test.log(Status.FAIL, exception);
        } else {
            test.log(Status.PASS, "테스트 성공");
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        // 모든 테스트 완료 후 보고서 저장
        ExtentReportManager.flush();
    }
    
    /**
     * 테스트 이름 가져오기
     */
    private String getTestName(ExtensionContext context) {
        Optional<Method> testMethod = context.getTestMethod();
        String testClassName = context.getRequiredTestClass().getSimpleName();
        
        if (testMethod.isPresent()) {
            return testClassName + " - " + testMethod.get().getName();
        }
        
        return testClassName;
    }
    
    /**
     * 테스트 설명 가져오기
     */
    private String getTestDescription(ExtensionContext context) {
        Optional<Method> testMethod = context.getTestMethod();
        
        if (testMethod.isPresent()) {
            Method method = testMethod.get();
            if (method.isAnnotationPresent(DisplayName.class)) {
                return method.getAnnotation(DisplayName.class).value();
            }
        }
        
        return "";
    }
} 