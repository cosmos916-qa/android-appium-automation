package com.example.appium_android_automation.infra;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.URL;
import java.time.Duration;

public class DriverFactory {

    public static AndroidDriver createAndroidDriver() throws Exception {
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName("Android")
                .setAutomationName("UiAutomator2")
                .setAppPackage(AppiumConfig.APP_PACKAGE)
                .setAppActivity(AppiumConfig.APP_ACTIVITY)
                .setNoReset(true)
                .setNewCommandTimeout(Duration.ofSeconds(1800));

        return new AndroidDriver(new URL(AppiumConfig.SERVER_URL), options);
    }
}