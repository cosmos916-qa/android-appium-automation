package com.example.appium_android_automation.infra;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import java.net.URL;
import java.time.Duration;

/**
 * AndroidDriver 생성 및 Appium 연결 담당
 */
public class DriverFactory {

    // AndroidDriver 생성하고 대상 앱과 연결
    // 성공 시: 제어 가능한 driver 반환, 실패 시: Exception 발생
    public static AndroidDriver createAndroidDriver() throws Exception {
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName("Android")
                .setAutomationName("UiAutomator2")
                .setAppPackage(AppiumConfig.APP_PACKAGE)
                .setAppActivity(AppiumConfig.APP_ACTIVITY)
                .setNoReset(true)  // 앱 데이터 보존 (로그인 상태 유지)
                .setNewCommandTimeout(Duration.ofSeconds(1800));  // 30분 세션 유지

        return new AndroidDriver(new URL(AppiumConfig.SERVER_URL), options);
    }
}
