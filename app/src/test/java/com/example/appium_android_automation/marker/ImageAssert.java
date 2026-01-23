package com.example.appium_android_automation.marker;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.InputStream;
import java.time.Duration;
import java.util.Base64;

public class ImageAssert {

    public static boolean waitUntilImageVisible(AndroidDriver driver, String resourcePath, int timeoutSec) {
        System.out.println("[IMG] start match: " + resourcePath + " timeout=" + timeoutSec + "s");

        try {
            String b64 = loadResourceAsBase64(resourcePath);
            System.out.println("[IMG] template loaded. b64 length=" + b64.length());

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));
            WebElement el = wait.until(d -> {
                try {
                    return d.findElement(AppiumBy.image(b64));
                } catch (NoSuchElementException ex) {
                    return null; // 계속 기다림
                }
            });

            boolean ok = (el != null);
            System.out.println("[IMG] match result=" + ok);
            return ok;

        } catch (TimeoutException te) {
            System.out.println("[IMG] TIMEOUT (not matched)");
            return false;

        } catch (Exception e) {
            System.out.println("[IMG] ERROR: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static String loadResourceAsBase64(String resourcePath) throws Exception {
        InputStream in = ImageAssert.class.getClassLoader().getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IllegalStateException("resource not found: " + resourcePath + " (put it under app/src/test/resources/...)");
        }
        byte[] bytes = in.readAllBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }
}
