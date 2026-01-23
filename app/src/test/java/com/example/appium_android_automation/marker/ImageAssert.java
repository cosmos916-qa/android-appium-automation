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
    /**
     * 이미지를 찾고 중앙 좌표를 반환합니다 (터치용).
     *
     * @param driver AndroidDriver 인스턴스
     * @param resourcePath 이미지 리소스 경로
     * @param timeoutSec 타임아웃 (초)
     * @return 이미지 중앙 좌표, 실패 시 null
     */
    public static Point findImageCenter(AndroidDriver driver, String resourcePath, int timeoutSec) {
        System.out.println("[IMG] 이미지 좌표 탐색 시작: " + resourcePath + " (타임아웃=" + timeoutSec + "초)");

        try {
            String b64 = loadResourceAsBase64(resourcePath);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));
            WebElement element = wait.until(d -> {
                try {
                    return d.findElement(AppiumBy.image(b64));
                } catch (NoSuchElementException ex) {
                    return null;
                }
            });

            if (element != null) {
                // 이미지 요소의 위치와 크기 가져오기
                Point location = element.getLocation();
                Dimension size = element.getSize();

                // 중앙 좌표 계산
                int centerX = location.getX() + (size.getWidth() / 2);
                int centerY = location.getY() + (size.getHeight() / 2);

                System.out.println("[IMG] 이미지 발견 ✓");
                System.out.println("  위치: (" + location.getX() + ", " + location.getY() + ")");
                System.out.println("  크기: " + size.getWidth() + " x " + size.getHeight());
                System.out.println("  중앙 좌표: (" + centerX + ", " + centerY + ")");

                return new Point(centerX, centerY);
            } else {
                System.out.println("[IMG] 이미지 미발견 ✗");
                return null;
            }

        } catch (TimeoutException te) {
            System.out.println("[IMG] TIMEOUT - 이미지 좌표 탐색 실패");
            return null;
        } catch (Exception e) {
            System.err.println("[IMG] ERROR: " + e.getMessage());
            e.printStackTrace();
            return null;
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