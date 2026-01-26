package com.example.appium_android_automation.main;
import com.example.appium_android_automation.infra.DriverFactory;
import com.example.appium_android_automation.marker.Evidence;
import com.example.appium_android_automation.reporting.ChecklistReporter;
import com.example.appium_android_automation.reporting.GoogleSheetsClient;
import com.google.api.services.sheets.v4.Sheets;
import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Before;

/**
 * 모든 테스트의 공통 기반 클래스
 * - 드라이버 초기화/정리, 구글 시트 리포터 설정, 결과 기록
 */

public abstract class BaseTestCase {

    protected AndroidDriver driver;
    protected ChecklistReporter reporter;

    //https://docs.google.com/spreadsheets/d/1aK2P0oL-WeT4LTU9ZeI5LDsAZjR9NI1ufh0W7ed1_j4/edit
    //구글스프레드시트 링크에서 'd/{여기서부터}/edit
    //{여기서부터}의 값을 복사해야한다.
    private static final String SPREADSHEET_ID = "1aK2P0oL-WeT4LTU9ZeI5LDsAZjR9NI1ufh0W7ed1_j4";
    private static final String SHEET_NAME = "checklist";

    // 테스트 시작 전 자동 실행
    @Before
    public void setUp() throws Exception {
        System.out.println("=== 테스트 환경 초기화 ===");

        driver = DriverFactory.createAndroidDriver();
        Sheets sheets = GoogleSheetsClient.createSheetsService();
        reporter = new ChecklistReporter(sheets, SPREADSHEET_ID, SHEET_NAME);

        System.out.println("✓ 준비 완료\n");
    }

    // 테스트 종료 후 자동 실행
    @After
    public void tearDown() {
        System.out.println("=== 테스트 환경 정리 ===");
        if (driver != null) {
            driver.quit();
            System.out.println("✓ Driver 종료\n");
        }
    }

    // TC 결과 기록 (Pass/Fail) - 화면 캡처 + 구글 시트 기록
    protected void recordResult(int tcNo, String tcName, boolean isPass) throws Exception {
        String result = isPass ? "Pass" : "Fail";

        // 증거 수집
        String evidencePath = Evidence.saveScreenshot(
                driver,
                "TC" + String.format("%02d", tcNo) + "_" + tcName + "_" + result
        );
        System.out.println("→ 증거 저장: " + evidencePath);

        // Google Sheets 기록 (개선된 동적 방식 사용)
        reporter.writeTCResult(tcNo, result);
        System.out.println("→ 결과 기록 완료: " + result);
    }

    // Block 상태 기록 (전제조건 미충족)
    protected void recordBlock(int tcNo, String tcName, String reason) throws Exception {
        System.out.println("→ TC" + String.format("%02d", tcNo) + " Block: " + reason);

        Evidence.saveScreenshot(driver, "TC" + String.format("%02d", tcNo) + "_" + tcName + "_BLOCK");
        reporter.writeTCResult(tcNo, "Block");
    }
}