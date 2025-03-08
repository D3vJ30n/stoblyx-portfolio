package com.j30n.stoblyx.api.config;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * ExtentReports 관리 클래스
 */
public class ExtentReportManager {
    
    private static ExtentReports extentReports;
    private static final Map<Long, ExtentTest> testMap = new HashMap<>();
    
    /**
     * ExtentReports 초기화
     */
    public static synchronized ExtentReports getInstance() {
        if (extentReports == null) {
            // 리포트 저장 경로 설정
            String reportPath = "build/reports/api-tests/";
            File reportDir = new File(reportPath);
            if (!reportDir.exists()) {
                reportDir.mkdirs();
            }
            
            // Spark 리포터 설정
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath + "extent-report.html");
            sparkReporter.config().setDocumentTitle("API 테스트 보고서");
            sparkReporter.config().setReportName("REST Assured API 테스트 결과");
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setEncoding("UTF-8");
            sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
            
            // ExtentReports 생성 및 설정
            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            extentReports.setSystemInfo("운영체제", System.getProperty("os.name"));
            extentReports.setSystemInfo("Java 버전", System.getProperty("java.version"));
            extentReports.setSystemInfo("테스트 환경", "테스트");
        }
        
        return extentReports;
    }
    
    /**
     * 현재 쓰레드의 테스트 인스턴스 가져오기
     */
    public static synchronized ExtentTest getTest() {
        return testMap.get(Thread.currentThread().getId());
    }
    
    /**
     * 새 테스트 생성 및 등록
     */
    public static synchronized ExtentTest createTest(String testName, String description) {
        ExtentTest test = getInstance().createTest(testName, description);
        testMap.put(Thread.currentThread().getId(), test);
        return test;
    }
    
    /**
     * 보고서 저장
     */
    public static synchronized void flush() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }
} 