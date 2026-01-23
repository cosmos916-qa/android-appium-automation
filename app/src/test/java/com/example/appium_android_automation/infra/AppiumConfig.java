package com.example.appium_android_automation.infra;

public class AppiumConfig {
    public static final String SERVER_URL = "http://127.0.0.1:4723";
    public static final String APP_PACKAGE = "com.epidgames.trickcalrevive";
    public static final String APP_ACTIVITY = "com.google.firebase.MessagingUnityPlayerActivity";
    // 이미지 검증 타임아웃(초)
    public static final int MAIN_MARKER_TIMEOUT_SEC = 30;

    // 템플릿 이미지 경로 (test resources)
    public static final String MAIN_MARKER_RESOURCE = "images/main_marker.png";
}
