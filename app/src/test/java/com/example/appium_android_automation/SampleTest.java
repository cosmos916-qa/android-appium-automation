package com.example.appium_android_automation;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.time.Duration;

public class SampleTest {

    private AndroidDriver driver;

    @Before
    public void setUp() throws Exception {
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName("Android")
                .setAutomationName("UiAutomator2")
                .setAppPackage("com.epidgames.trickcalrevive")
                .setAppActivity("com.google.firebase.MessagingUnityPlayerActivity")
                .setNoReset(true)
                .setAutoGrantPermissions(true);

        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/"), options);

        System.out.println("SessionId=" + driver.getSessionId());
    }

    @Test
    public void smoke() {
        // 세션 생성만 성공하면 1차 목표 달성
        driver.activateApp("com.epidgames.trickcalrevive");
        System.out.println("After activateApp = " + driver.getCurrentPackage());
        System.out.println("CurrentPackage=" + driver.getCurrentPackage());
    }

    @After
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
