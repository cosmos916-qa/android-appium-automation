package com.example.appium_android_automation.flow;

import io.appium.java_client.android.AndroidDriver;

/**
 * StartAppFlow은 다음과 같은 사항을 진행 합니다.
 * 앱 실행 플로우 - 앱 활성화 및 실행 상태 검증
 */
public class StartAppFlow {
    //앱을 포그라운드로 활성화하고 정상 실행 여부를 반환합니다.
    //성공 시 : True, 실패 시 : False
    public static boolean run(AndroidDriver driver) {
        try {
            // 앱 활성화 (백그라운드→포그라운드 또는 새로 실행)
            driver.activateApp("com.epidgames.trickcalrevive");

            // 현재 패키지명으로 실행 상태 검증
            String currentPkg = driver.getCurrentPackage();
            return currentPkg != null && !currentPkg.isEmpty();
        } catch (Exception e) {
            System.err.println("[StartAppFlow] 앱 실행 실패: " + e.getMessage());
            return false;
        }
    }
}
