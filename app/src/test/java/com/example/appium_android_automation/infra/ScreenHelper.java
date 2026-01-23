package com.example.appium_android_automation.infra;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ScreenOrientation;

/**
 * 화면 해상도 및 방향 처리 유틸리티
 *
 * <p>다양한 디바이스 해상도와 화면 방향에 대응하는 좌표 계산 제공</p>
 */
public class ScreenHelper {

    /**
     * 현재 화면 해상도 정보를 가져옵니다.
     */
    public static Dimension getScreenSize(AndroidDriver driver) {
        return driver.manage().window().getSize();
    }

    /**
     * 현재 화면 방향을 가져옵니다.
     */
    public static ScreenOrientation getOrientation(AndroidDriver driver) {
        return driver.getOrientation();
    }

    /**
     * 화면 중앙 X 좌표를 계산합니다.
     */
    public static int getCenterX(AndroidDriver driver) {
        return getScreenSize(driver).getWidth() / 2;
    }

    /**
     * 화면 중앙 Y 좌표를 계산합니다.
     */
    public static int getCenterY(AndroidDriver driver) {
        return getScreenSize(driver).getHeight() / 2;
    }

    /**
     * 화면 정보를 콘솔에 출력합니다 (디버깅용).
     */
    public static void printScreenInfo(AndroidDriver driver) {
        Dimension size = getScreenSize(driver);
        ScreenOrientation orientation = getOrientation(driver);

        System.out.println("=== 화면 정보 ===");
        System.out.println("해상도: " + size.getWidth() + " x " + size.getHeight());
        System.out.println("방향: " + orientation);
        System.out.println("중앙 좌표: (" + getCenterX(driver) + ", " + getCenterY(driver) + ")");
        System.out.println("================");
    }
}
