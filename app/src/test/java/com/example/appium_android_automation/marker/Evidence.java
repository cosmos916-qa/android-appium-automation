package com.example.appium_android_automation.marker;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.OutputType;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Evidence {

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
