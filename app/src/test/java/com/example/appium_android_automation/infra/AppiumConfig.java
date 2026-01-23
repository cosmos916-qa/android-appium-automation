package com.example.appium_android_automation.infra;

public class AppiumConfig {
    public static final String SERVER_URL = "http://127.0.0.1:4723";
    public static final String APP_PACKAGE = "com.epidgames.trickcalrevive";
    public static final String APP_ACTIVITY = "com.google.firebase.MessagingUnityPlayerActivity";

    // 이미지 검증 타임아웃(초)
    public static final int MAIN_MARKER_TIMEOUT_SEC = 30;
    public static final int GAME_START_VERIFY_TIMEOUT_SEC = 15;  // 게임 로딩 고려

    // 종료 버튼 탐지 타임아웃 (초)
    public static final int EXIT_BUTTON_TIMEOUT_SEC = 10;

    // 앱 종료 확인 대기 시간 (밀리초)
    public static final int EXIT_VERIFICATION_WAIT_MS = 3000;

    // 템플릿 이미지 경로
    public static final String TARGET_LOGO_RESOURCE = "images/target_logo.png";
    public static final String GAME_STARTED_MARKER_RESOURCE = "images/target_menu.png";
    public static final String EXIT_CONFIRM_BUTTON_RESOURCE = "images/target_exit_button.png";




    // ⭐ TC03: 게임 시작 드래그 설정 (캐릭터 볼 당기기) 하드 코딩건
    public static final int CHEEK_DRAG_START_X = 1560;
    public static final int CHEEK_DRAG_START_Y = 720;
    public static final int CHEEK_DRAG_END_X = 936;
    public static final int CHEEK_DRAG_END_Y = 720;
    public static final int CHEEK_DRAG_DURATION_MS = 1000;  // 1.0초 자연스러운 드래그

    // TC03 검증용 이미지 (게임 시작 후 나타나는 UI 요소)

}
