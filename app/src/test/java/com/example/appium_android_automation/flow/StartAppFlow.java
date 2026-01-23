package com.example.appium_android_automation.flow;

import io.appium.java_client.android.AndroidDriver;

public class StartAppFlow {

    public static boolean run(AndroidDriver driver) {
        try {
            driver.activateApp("com.epidgames.trickcalrevive");
            String currentPkg = driver.getCurrentPackage();
            return currentPkg != null && !currentPkg.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
