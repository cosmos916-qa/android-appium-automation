package com.example.appium_android_automation.flow;

import io.appium.java_client.android.AndroidDriver;

public class StartAppFlow {

    public static boolean run(AndroidDriver driver) {
        try {
            // App 활성화 (이미 켜져있어도 안정적)
            driver.activateApp("com.epidgames.trickcalrevive");

            // 최소 확인(패키지 확인)
            String currentPkg = driver.getCurrentPackage();
            return currentPkg != null && !currentPkg.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}