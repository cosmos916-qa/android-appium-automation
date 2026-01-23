package com.example.appium_android_automation.marker;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.InputStream;
import java.time.Duration;
import java.util.Base64;

public class ImageAssert {
    /**
     * 화면에 특정 이미지가 나타날 때까지 대기하고 검증합니다.
     * Appium의 OpenCV 기반 이미지 매칭 기능을 활용합니다.
     */
    public static boolean waitUntilImageVisible(AndroidDriver driver, String resourcePath, int timeoutSec) {
        System.out.println("[IMG] 이미지 매칭 시작: " + resourcePath + " (타임아웃=" + timeoutSec + "초)");

        try {
            // 리소스 이미지를 Base64로 인코딩 (Appium 요구사항)
            String b64 = loadResourceAsBase64(resourcePath);

            // WebDriverWait로 이미지가 나타날 때까지 폴링
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));
            WebElement el = wait.until(d -> {
                try {
                    return d.findElement(AppiumBy.image(b64)); // OpenCV 이미지 매칭
                } catch (NoSuchElementException ex) {
                    return null; // 계속 대기
                }
            });

            boolean ok = (el != null);
            System.out.println("[IMG] 매칭 결과: " + (ok ? "성공 ✓" : "실패 ✗"));
            return ok;

        } catch (TimeoutException te) {
            System.out.println("[IMG] TIMEOUT - 이미지를 찾지 못함");
            return false;
        } catch (Exception e) {
            System.err.println("[IMG] ERROR: " + e.getMessage());
            return false;
        }
    }

    private static String loadResourceAsBase64(String resourcePath) throws Exception {
        InputStream in = ImageAssert.class.getClassLoader().getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IllegalStateException("이미지 리소스 없음: " + resourcePath);
        }
        byte[] bytes = in.readAllBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }
}