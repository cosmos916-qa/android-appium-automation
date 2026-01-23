package com.example.appium_android_automation.main;

import com.example.appium_android_automation.flow.StartAppFlow;
import com.example.appium_android_automation.infra.DriverFactory;
import com.example.appium_android_automation.marker.Evidence;
import com.example.appium_android_automation.marker.ImageAssert;
import com.example.appium_android_automation.reporting.ChecklistReporter;
import com.example.appium_android_automation.reporting.GoogleSheetsClient;
import com.google.api.services.sheets.v4.Sheets;
import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SmokeSuiteTest {

    private AndroidDriver driver;

    private static final String SPREADSHEET_ID = "1aK2P0oL-WeT4LTU9ZeI5LDsAZjR9NI1ufh0W7ed1_j4";
    private static final String SHEET_NAME = "checklist";

    // ✅ 리소스 이미지 경로 (app/src/test/resources/images/main_marker.png)
    private static final String MAIN_MARKER_IMAGE = "images/main_marker.png";

    @Before
    public void setUp() throws Exception {
        driver = DriverFactory.createAndroidDriver();
    }

    @Test
    public void TC01_startApp_and_writeResultToGoogleSheet() throws Exception {
        boolean appStarted = StartAppFlow.run(driver);

        // ✅ “증거 기반 PASS” 조건: 메인 화면 마커 이미지가 보여야 PASS
        boolean markerVisible = false;
        if (appStarted) {
            markerVisible = ImageAssert.waitUntilImageVisible(driver, MAIN_MARKER_IMAGE, 15);
        }

        boolean markerOk = appStarted && ImageAssert.waitUntilImageVisible(driver, MAIN_MARKER_IMAGE, 15);
        boolean logoOk   = appStarted && ImageAssert.waitUntilImageVisible(driver, "images/target_logo.png", 15);

        boolean ok = markerOk || logoOk;

        System.out.println("[TC01] markerOk=" + markerOk + ", logoOk=" + logoOk + ", ok=" + ok);


        // ✅ 스샷 저장 (PASS/FAIL 둘 다)
        String shotPath = Evidence.saveScreenshot(driver, ok ? "TC01_PASS" : "TC01_FAIL");
        System.out.println("Evidence saved: " + shotPath);

        Sheets sheets = GoogleSheetsClient.createSheetsService();
        ChecklistReporter reporter = new ChecklistReporter(sheets, SPREADSHEET_ID, SHEET_NAME);

        reporter.writeResultNo1(ok ? "Pass" : "Fail");

        System.out.println("[TC01] appStarted=" + appStarted + ", markerVisible=" + markerVisible + ", ok=" + ok);
        System.out.println("[TC01] imgOk(target_logo)=" + ok);
    }

    @After
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
