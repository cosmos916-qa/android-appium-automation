package com.example.appium_android_automation.infra;
/**
 * 테스트 환경 설정 중앙 관리
 * - Appium 서버, 앱 정보, 타임아웃, 이미지 리소스 경로
 */
public class AppiumConfig {
    // Appium 서버 및 대상 앱 정보
    public static final String SERVER_URL = "http://127.0.0.1:4723";
    public static final String APP_PACKAGE = "com.epidgames.trickcalrevive";
    public static final String APP_ACTIVITY = "com.google.firebase.MessagingUnityPlayerActivity";

    // 이미지 검증 타임아웃(초)
    public static final int MAIN_MARKER_TIMEOUT_SEC = 30;          // 메인 화면 로고 대기
    public static final int GAME_START_VERIFY_TIMEOUT_SEC = 15;    // 다음 화면 진입 대기
    public static final int EXIT_BUTTON_TIMEOUT_SEC = 10;          // 종료 버튼 탐지 대기
    public static final int EXIT_VERIFICATION_WAIT_MS = 3000;      // 앱 종료 확인 대기

    // 이미지 매칭용 참조 파일 경로
    public static final String TARGET_LOGO_RESOURCE = "images/target_logo.png";
    public static final String GAME_STARTED_MARKER_RESOURCE = "images/target_menu.png";
    public static final String EXIT_CONFIRM_BUTTON_RESOURCE = "images/target_exit_button.png";
    public static final int CHEEK_DRAG_DURATION_MS = 1000;  // 1.0초 자연스러운 드래그

    // 레거시: 고정 좌표 드래그 (특정 해상도 전용, 사용 비권장)
    public static final int CHEEK_DRAG_START_X = 1560;
    public static final int CHEEK_DRAG_START_Y = 720;
    public static final int CHEEK_DRAG_END_X = 936;
    public static final int CHEEK_DRAG_END_Y = 720;
}
