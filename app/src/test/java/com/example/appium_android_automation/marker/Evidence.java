package com.example.appium_android_automation.marker;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.OutputType;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 테스트 증거 수집 - 화면 캡처 및 파일 저장
 */
public class Evidence {

    // 현재 화면을 캡처하여 build/reports/evidence 폴더에 저장
    // 파일명: {namePrefix}_yyyyMMdd_HHmmss.png
    // 반환: 저장된 파일 경로

    public static String saveScreenshot(AndroidDriver driver, String namePrefix) throws Exception {
        File src = driver.getScreenshotAs(OutputType.FILE);

        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path outDir = Path.of("build", "reports", "evidence");
        Files.createDirectories(outDir);

        Path out = outDir.resolve(namePrefix + "_" + ts + ".png");
        Files.copy(src.toPath(), out);

        return out.toString();
    }
}
