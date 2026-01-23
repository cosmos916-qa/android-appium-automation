package com.example.appium_android_automation.main;

import com.example.appium_android_automation.infra.DriverFactory;
import com.example.appium_android_automation.marker.Evidence;
import com.example.appium_android_automation.marker.ImageAssert;
import com.example.appium_android_automation.reporting.ChecklistReporter;
import com.example.appium_android_automation.reporting.GoogleSheetsClient;
import com.example.appium_android_automation.flow.StartAppFlow;
import com.google.api.services.sheets.v4.Sheets;
import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 트릭컬 리바이브 스모크 테스트 스위트
 *
 * <p>앱 실행부터 Google Sheets 자동 리포팅까지 전체 시나리오를 검증합니다.
 * 게임 로딩 지연을 고려한 2차 검증 로직(Fallback)을 포함하여 안정성을 확보했습니다.</p>
 *
 * @author [Your Name]
 * @since 2025-01-24
 */
public class SmokeSuiteTest {

    private AndroidDriver driver;

    // TODO: 실제 배포 시 환경변수로 분리 권장
    private static final String SPREADSHEET_ID = "1aK2P0oL-WeT4LTU9ZeI5LDsAZjR9NI1ufh0W7ed1_j4";
    private static final String SHEET_NAME = "checklist";

    // 검증용 이미지 리소스 (src/test/resources 기준)
    private static final String MAIN_MARKER_IMAGE = "images/main_marker.png";
    private static final String TARGET_LOGO_IMAGE = "images/target_logo.png";

    @Before
    public void setUp() throws Exception {
        System.out.println("=== 트릭컬 리바이브 테스트 환경 초기화 ===");
        driver = DriverFactory.createAndroidDriver();
        System.out.println("AndroidDriver 생성 완료");
    }

    /**
     * TC01: 앱 실행 및 메인 화면 진입 검증
     *
     * <h3>검증 전략</h3>
     * <ol>
     *   <li>앱 실행 성공 여부 확인</li>
     *   <li>메인 마커 이미지 검증 (1차)</li>
     *   <li>로고 이미지 검증 (2차 Fallback)</li>
     *   <li>증거 스크린샷 자동 저장</li>
     *   <li>Google Sheets 결과 자동 기록</li>
     * </ol>
     */
    @Test
    public void TC01_startApp_and_writeResultToGoogleSheet() throws Exception {
        System.out.println("\n=== TC01: 앱 실행 및 메인 화면 검증 시작 ===");

        // [Step 1] 앱 실행
        System.out.println("[1/4] 트릭컬 리바이브 앱 실행 중...");
        boolean appStarted = StartAppFlow.run(driver);
        System.out.println("앱 실행 결과: " + (appStarted ? "성공 ✓" : "실패 ✗"));

        boolean isPass = false;

        // [Step 2] 이미지 기반 검증 (2단계 Fallback 로직)
        if (appStarted) {
            System.out.println("[2/4] 메인 화면 검증 중...");

            // 1차 검증: 메인 마커 이미지
            boolean markerOk = ImageAssert.waitUntilImageVisible(driver, MAIN_MARKER_IMAGE, 15);
            System.out.println("메인 마커 검증: " + (markerOk ? "성공 ✓" : "실패 ✗"));

            // 2차 검증: 메인 마커 실패 시 로고로 재검증 (게임 로딩 지연 대비)
            boolean logoOk = false;
            if (!markerOk) {
                System.out.println("→ 2차 검증: 로고 이미지로 재시도...");
                logoOk = ImageAssert.waitUntilImageVisible(driver, TARGET_LOGO_IMAGE, 15);
                System.out.println("로고 이미지 검증: " + (logoOk ? "성공 ✓" : "실패 ✗"));
            }

            // 최종 판정: 둘 중 하나라도 성공하면 Pass
            isPass = markerOk || logoOk;
            System.out.println("최종 판정: " + (isPass ? "PASS ✓" : "FAIL ✗"));
        } else {
            System.out.println("앱 실행 실패로 검증 중단");
        }

        // [Step 3] 증거 수집 (Pass/Fail 무관하게 스크린샷 저장)
        System.out.println("[3/4] 증거 스크린샷 저장 중...");
        String shotPath = Evidence.saveScreenshot(driver, isPass ? "TC01_PASS" : "TC01_FAIL");
        System.out.println("증거 저장 완료: " + shotPath);

        // [Step 4] Google Sheets 자동 리포팅
        System.out.println("[4/4] Google Sheets 결과 기록 중...");
        Sheets sheets = GoogleSheetsClient.createSheetsService();
        ChecklistReporter reporter = new ChecklistReporter(sheets, SPREADSHEET_ID, SHEET_NAME);
        reporter.writeResultNo1(isPass ? "Pass" : "Fail");
        System.out.println("Google Sheets 기록 완료!");

        System.out.println("=== TC01 테스트 완료 ===\n");
    }

    @After
    public void tearDown() {
        System.out.println("=== 테스트 환경 정리 ===");
        if (driver != null) {
            driver.quit();
            System.out.println("AndroidDriver 종료 완료");
        }
    }
}