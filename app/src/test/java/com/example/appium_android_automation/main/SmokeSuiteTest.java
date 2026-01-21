package com.example.appium_android_automation.main;

import com.example.appium_android_automation.flow.StartAppFlow;
import com.example.appium_android_automation.infra.DriverFactory;
import com.example.appium_android_automation.reporting.ChecklistReporter;
import com.example.appium_android_automation.reporting.GoogleSheetsClient;
import com.google.api.services.sheets.v4.Sheets;
import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SmokeSuiteTest {

    private AndroidDriver driver;

    // TODO: 본인 시트 정보로 교체
    private static final String SPREADSHEET_ID = "1aK2P0oL-WeT4LTU9ZeI5LDsAZjR9NI1ufh0W7ed1_j4";
    private static final String SHEET_NAME = "checklist";

    @Before
    public void setUp() throws Exception {
        driver = DriverFactory.createAndroidDriver();
    }

    @Test
    public void TC01_startApp_and_writeResultToGoogleSheet() throws Exception {
        boolean ok = StartAppFlow.run(driver);

        Sheets sheets = GoogleSheetsClient.createSheetsService();
        ChecklistReporter reporter = new ChecklistReporter(sheets, SPREADSHEET_ID, SHEET_NAME);

        reporter.writeResultNo1(ok ? "Pass" : "Fail");
        System.out.println(SHEET_NAME);
    }

    @After
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}