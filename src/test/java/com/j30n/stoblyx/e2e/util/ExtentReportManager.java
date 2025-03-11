package com.j30n.stoblyx.e2e.util;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * ExtentReport를 사용하여 테스트 결과를 보고서로 생성하는 관리자 클래스
 */
public class ExtentReportManager {
    private static final ExtentReports extentReports = new ExtentReports();
    private static final Map<String, ExtentTest> testMap = new HashMap<>();
    private static boolean isInitialized = false;
    
    /**
     * ExtentReport 초기화
     */
    public static synchronized void init() {
        if (isInitialized) {
            return;
        }
        
        String reportTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String reportFileName = "e2e-test-report-" + reportTime + ".html";
        String reportDir = "build/reports/e2e-tests";
        String reportPath = Paths.get(reportDir, reportFileName).toString();
        
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle("스토블릭스 E2E 테스트 보고서");
        sparkReporter.config().setReportName("E2E 테스트 결과");
        sparkReporter.config().setEncoding("UTF-8");
        
        extentReports.attachReporter(sparkReporter);
        extentReports.setSystemInfo("환경", "테스트");
        extentReports.setSystemInfo("OS", System.getProperty("os.name"));
        extentReports.setSystemInfo("Java 버전", System.getProperty("java.version"));
        
        isInitialized = true;
    }
    
    /**
     * 테스트 시작
     * 
     * @param testName 테스트 이름
     * @param description 테스트 설명
     * @return ExtentTest 테스트 객체
     */
    public static synchronized ExtentTest startTest(String testName, String description) {
        if (!isInitialized) {
            init();
        }
        
        ExtentTest test = extentReports.createTest(testName, description);
        testMap.put(testName, test);
        return test;
    }
    
    /**
     * 테스트 객체 가져오기
     * 
     * @param testName 테스트 이름
     * @return ExtentTest 테스트 객체
     */
    public static synchronized ExtentTest getTest(String testName) {
        return testMap.get(testName);
    }
    
    /**
     * 테스트 로그 추가
     * 
     * @param testName 테스트 이름
     * @param status 상태
     * @param details 세부 정보
     */
    public static synchronized void log(String testName, Status status, String details) {
        ExtentTest test = testMap.get(testName);
        if (test != null) {
            test.log(status, details);
        }
    }
    
    /**
     * 보고서 종료 및 저장
     */
    public static synchronized void flush() {
        extentReports.flush();
    }
} 