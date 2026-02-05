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

    //권한 팝업 처리용
    public static final int IMPLICIT_WAIT_SEC = 10;                // 기본 암시적 대기시간


    // 이미지 매칭용 참조 파일 경로
    public static final String TARGET_LOGO_RESOURCE = "images/target_logo.png";
    public static final String GAME_STARTED_MARKER_RESOURCE = "images/target_menu.png";
    public static final String EXIT_CONFIRM_BUTTON_RESOURCE = "images/target_exit_button.png";
    public static final int CHEEK_DRAG_DURATION_MS = 1000;  // 1.0초 자연스러운 드래그

    // 타임아웃 설정 (초 단위)
    /** 리소스 다운로드 최대 대기시간 (5분 + 여유시간) */
    public static final int RESOURCE_DOWNLOAD_TIMEOUT_SEC = 360;
    /** 권한 팝업 대기시간 */
    public static final int PERMISSION_POPUP_TIMEOUT_SEC = 10;
    /** 다운로드 완료 확인 주기 (Polling 간격) */
    public static final int DOWNLOAD_CHECK_INTERVAL_SEC = 10;

    // Native UI 식별자 (Inspector 정보 기반)
    /** Android 표준 알림 권한 허용 버튼 ID */
    public static final String NOTIFICATION_ALLOW_BUTTON_ID = "com.android.permissioncontroller:id/permission_allow_button";

    // Unity 게임 화면 이미지 리소스 경로
    /** 최초 다운로드 팝업의 '다운로드' 버튼 (스크린샷 2번) */
    public static final String FIRST_DOWNLOAD_BUTTON_RESOURCE = "images/first_download_button.png";

    /** 다운로드 완료 후 우측 하단 버튼 (스크린샷 4번 - 트릭컬로 출발) */
    public static final String DOWNLOAD_COMPLETE_BUTTON_RESOURCE = "images/download_complete_button.png";

    /** 이용약관 화면 마커 (스크린샷 5번 상단) */
    public static final String TERMS_SCREEN_MARKER_RESOURCE = "images/terms_screen_marker.png";

    /** 이용약관 '모두 동의하고 시작' 버튼 (스크린샷 5번 하단) */
    public static final String TERMS_AGREE_ALL_BUTTON_RESOURCE = "images/terms_agree_all_button.png";

    // ========== 구글 로그인 관련 설정 ==========

    /** 타이틀 화면 "Google로그인" 버튼 이미지 */
    public static final String GOOGLE_LOGIN_BUTTON_RESOURCE = "images/google_login_button.png";

    /** 로그인 완료 후 메인 화면 마커 (기존 타겟 로고 재사용) */
    public static final String LOGIN_SUCCESS_MARKER_RESOURCE = "images/target_logo.png";

    /** 구글 계정 선택 화면 대기시간 */
    public static final int ACCOUNT_SELECTION_TIMEOUT_SEC = 15;

    /** 로그인 처리 완료 대기시간 */
    public static final int LOGIN_PROCESSING_TIMEOUT_SEC = 20;

    /** 테스트용 구글 계정 이메일 */
    public static final String TARGET_GOOGLE_EMAIL = "cosmos9169951@gmail.com";

    /** 게임 로비 [≡] 메뉴 버튼 */
    public static final String MENU_BUTTON_RESOURCE = "images/menu_button.png";

    /** 메뉴 팝업 마커 ("메뉴" 텍스트) */
    public static final String MENU_POPUP_MARKER_RESOURCE = "images/menu_popup_marker.png";

    /** 설정 버튼 (메뉴 팝업 내) */
    public static final String SETTINGS_BUTTON_RESOURCE = "images/settings_button.png";

    /** 설정 팝업 마커 ("설정" 텍스트) */
    public static final String SETTINGS_POPUP_MARKER_RESOURCE = "images/settings_popup_marker.png";

    /** 기타 버튼 (설정 팝업 내) */
    public static final String ETC_BUTTON_RESOURCE = "images/etc_button.png";

    /** 로그아웃 버튼 (기타 섹션 내) */
    public static final String LOGOUT_BUTTON_RESOURCE = "images/logout_button.png";

    /** 로그아웃 확인 팝업 마커 */
    public static final String LOGOUT_CONFIRM_POPUP_RESOURCE = "images/logout_confirm_popup.png";

    /** 로그아웃 확인 버튼 */
    public static final String LOGOUT_CONFIRM_BUTTON_RESOURCE = "images/logout_confirm_button.png";

    /** 팝업 전환 대기시간 */
    public static final int POPUP_TRANSITION_TIMEOUT_SEC = 10;

    /** 로그아웃 완료 확인 대기시간 */
    public static final int LOGOUT_VERIFICATION_TIMEOUT_SEC = 15;

    // 레거시: 고정 좌표 드래그 (특정 해상도 전용, 사용 비권장)
    public static final int CHEEK_DRAG_START_X = 1560;
    public static final int CHEEK_DRAG_START_Y = 720;
    public static final int CHEEK_DRAG_END_X = 936;
    public static final int CHEEK_DRAG_END_Y = 720;

}
