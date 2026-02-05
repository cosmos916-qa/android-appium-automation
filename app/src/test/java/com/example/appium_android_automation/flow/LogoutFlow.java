package com.example.appium_android_automation.flow;

import io.appium.java_client.android.AndroidDriver;
import com.example.appium_android_automation.infra.AppiumConfig;
import com.example.appium_android_automation.infra.TouchActionHelper;
import com.example.appium_android_automation.marker.ImageAssert;
import com.example.appium_android_automation.marker.Evidence;

/**
 * 로그아웃 플로우 (9단계 네비게이션)
 *
 * 실행 순서:
 * 로비 확인 → 메뉴 → 메뉴 팝업 확인 → 설정 → 설정 팝업 확인
 * → 기타 → 로그아웃 → 확인 → 이용약관 화면 복귀
 */
public class LogoutFlow {

    private final AndroidDriver driver;

    public LogoutFlow(AndroidDriver driver) {
        this.driver = driver;
    }

    public boolean run() {
        System.out.println("🚪 [LogoutFlow] === 로그아웃 플로우 시작 (9단계) ===");

        // 9단계 순차 실행
        if (!step1_VerifyLobby()) return false;
        if (!step2_TapMenuButton()) return false;
        if (!step3_VerifyMenuPopup()) return false;
        if (!step4_TapSettingsButton()) return false;
        if (!step5_VerifySettingsPopup()) return false;
        if (!step6_TapEtcButton()) return false;
        if (!step7_TapLogoutButton()) return false;
        if (!step8_ConfirmLogout()) return false;
        if (!step9_VerifyLogoutSuccess()) return false;

        System.out.println("✅ [LogoutFlow] === 로그아웃 플로우 완료 (9단계) ===");
        return true;
    }

    // [1/9] 게임 로비 화면 확인
    private boolean step1_VerifyLobby() {
        System.out.println("   🎮 [1/9] 게임 로비 화면 확인 중...");

        boolean isVisible = ImageAssert.waitUntilImageVisible(
                driver,
                AppiumConfig.GAME_STARTED_MARKER_RESOURCE,
                AppiumConfig.MAIN_MARKER_TIMEOUT_SEC
        );

        if (isVisible) {
            System.out.println("   ✅ 게임 로비 화면 확인 완료");
            return true;
        } else {
            System.out.println("   ❌ 게임 로비 화면 마커 미발견");
            captureDebugScreen("lobby_not_found");
            return false;
        }
    }

    // [2/9] 메뉴 버튼 터치
    private boolean step2_TapMenuButton() {
        System.out.println("   📋 [2/9] [≡] 메뉴 버튼 터치 중...");

        if (!ImageAssert.waitUntilImageVisible(driver, AppiumConfig.MENU_BUTTON_RESOURCE,
                AppiumConfig.MAIN_MARKER_TIMEOUT_SEC)) {
            System.out.println("   ❌ 메뉴 버튼 이미지 미발견");
            captureDebugScreen("menu_button_not_found");
            return false;
        }

        try {
            TouchActionHelper.tapOnImageCenter(driver, AppiumConfig.MENU_BUTTON_RESOURCE);
            System.out.println("   ✅ 메뉴 버튼 터치 완료");
            Thread.sleep(1500); // 팝업 애니메이션 대기
            return true;
        } catch (Exception e) {
            System.out.println("   ❌ 메뉴 버튼 터치 실패: " + e.getMessage());
            return false;
        }
    }

    // [3/9] 메뉴 팝업 진입 확인
    private boolean step3_VerifyMenuPopup() {
        System.out.println("   🔍 [3/9] 메뉴 팝업 진입 확인 중...");

        boolean isVisible = ImageAssert.waitUntilImageVisible(
                driver,
                AppiumConfig.MENU_POPUP_MARKER_RESOURCE,
                AppiumConfig.POPUP_TRANSITION_TIMEOUT_SEC
        );

        if (isVisible) {
            System.out.println("   ✅ 메뉴 팝업 진입 확인");
            return true;
        } else {
            System.out.println("   ❌ 메뉴 팝업 마커 미발견");
            captureDebugScreen("menu_popup_not_found");
            return false;
        }
    }

    // [4/9] 설정 버튼 터치
    private boolean step4_TapSettingsButton() {
        System.out.println("   ⚙️ [4/9] [설정] 버튼 터치 중...");

        if (!ImageAssert.waitUntilImageVisible(driver, AppiumConfig.SETTINGS_BUTTON_RESOURCE,
                AppiumConfig.MAIN_MARKER_TIMEOUT_SEC)) {
            System.out.println("   ❌ 설정 버튼 이미지 미발견");
            captureDebugScreen("settings_button_not_found");
            return false;
        }

        try {
            TouchActionHelper.tapOnImageCenter(driver, AppiumConfig.SETTINGS_BUTTON_RESOURCE);
            System.out.println("   ✅ 설정 버튼 터치 완료");
            Thread.sleep(1500);
            return true;
        } catch (Exception e) {
            System.out.println("   ❌ 설정 버튼 터치 실패: " + e.getMessage());
            return false;
        }
    }

    // [5/9] 설정 팝업 진입 확인
    private boolean step5_VerifySettingsPopup() {
        System.out.println("   🔍 [5/9] 설정 팝업 진입 확인 중...");

        boolean isVisible = ImageAssert.waitUntilImageVisible(
                driver,
                AppiumConfig.SETTINGS_POPUP_MARKER_RESOURCE,
                AppiumConfig.POPUP_TRANSITION_TIMEOUT_SEC
        );

        if (isVisible) {
            System.out.println("   ✅ 설정 팝업 진입 확인");
            return true;
        } else {
            System.out.println("   ❌ 설정 팝업 마커 미발견");
            captureDebugScreen("settings_popup_not_found");
            return false;
        }
    }

    // [6/9] 기타 버튼 터치
    private boolean step6_TapEtcButton() {
        System.out.println("   📂 [6/9] [기타] 버튼 터치 중...");

        if (!ImageAssert.waitUntilImageVisible(driver, AppiumConfig.ETC_BUTTON_RESOURCE,
                AppiumConfig.MAIN_MARKER_TIMEOUT_SEC)) {
            System.out.println("   ❌ 기타 버튼 이미지 미발견");
            captureDebugScreen("etc_button_not_found");
            return false;
        }

        try {
            TouchActionHelper.tapOnImageCenter(driver, AppiumConfig.ETC_BUTTON_RESOURCE);
            System.out.println("   ✅ 기타 버튼 터치 완료");
            Thread.sleep(1000);
            return true;
        } catch (Exception e) {
            System.out.println("   ❌ 기타 버튼 터치 실패: " + e.getMessage());
            return false;
        }
    }

    // [7/9] 로그아웃 버튼 터치
    private boolean step7_TapLogoutButton() {
        System.out.println("   🚪 [7/9] [로그아웃] 버튼 터치 중...");

        if (!ImageAssert.waitUntilImageVisible(driver, AppiumConfig.LOGOUT_BUTTON_RESOURCE,
                AppiumConfig.MAIN_MARKER_TIMEOUT_SEC)) {
            System.out.println("   ❌ 로그아웃 버튼 이미지 미발견");
            captureDebugScreen("logout_button_not_found");
            return false;
        }

        try {
            TouchActionHelper.tapOnImageCenter(driver, AppiumConfig.LOGOUT_BUTTON_RESOURCE);
            System.out.println("   ✅ 로그아웃 버튼 터치 완료");
            Thread.sleep(1000);
            return true;
        } catch (Exception e) {
            System.out.println("   ❌ 로그아웃 버튼 터치 실패: " + e.getMessage());
            return false;
        }
    }

    // [8/9] 로그아웃 확인 팝업에서 확인 버튼 터치
    private boolean step8_ConfirmLogout() {
        System.out.println("   ✔️ [8/9] 로그아웃 확인 팝업 처리 중...");

        // 로그아웃 확인 팝업 대기 (선택사항)
        boolean popupVisible = ImageAssert.waitUntilImageVisible(
                driver,
                AppiumConfig.LOGOUT_CONFIRM_POPUP_RESOURCE,
                AppiumConfig.POPUP_TRANSITION_TIMEOUT_SEC
        );

        if (!popupVisible) {
            System.out.println("   ⚠️ 로그아웃 확인 팝업 미발견 (확인 버튼 직접 시도)");
        }

        // 확인 버튼 터치
        if (!ImageAssert.waitUntilImageVisible(driver, AppiumConfig.LOGOUT_CONFIRM_BUTTON_RESOURCE,
                AppiumConfig.MAIN_MARKER_TIMEOUT_SEC)) {
            System.out.println("   ❌ 로그아웃 확인 버튼 미발견");
            captureDebugScreen("logout_confirm_button_not_found");
            return false;
        }

        try {
            TouchActionHelper.tapOnImageCenter(driver, AppiumConfig.LOGOUT_CONFIRM_BUTTON_RESOURCE);
            System.out.println("   ✅ 로그아웃 확인 버튼 터치 완료");
            Thread.sleep(3000); // 로그아웃 처리 대기
            return true;
        } catch (Exception e) {
            System.out.println("   ❌ 확인 버튼 터치 실패: " + e.getMessage());
            return false;
        }
    }

    // [9/9] 이용약관 화면 노출로 로그아웃 완료 확인
    private boolean step9_VerifyLogoutSuccess() {
        System.out.println("   🔍 [9/9] 로그아웃 완료 확인 중...");

        boolean termsVisible = ImageAssert.waitUntilImageVisible(
                driver,
                AppiumConfig.TERMS_SCREEN_MARKER_RESOURCE,
                AppiumConfig.LOGOUT_VERIFICATION_TIMEOUT_SEC
        );

        if (termsVisible) {
            System.out.println("   ✅ 로그아웃 완료: 이용약관 화면 확인");
            return true;
        } else {
            System.out.println("   ❌ 로그아웃 완료 확인 실패: 이용약관 화면 미발견");
            captureDebugScreen("logout_verification_failed");
            return false;
        }
    }

    private void captureDebugScreen(String suffix) {
        try {
            String debugPath = Evidence.saveScreenshot(driver, "DEBUG_logout_" + suffix);
            System.out.println("   📸 디버깅 캡처: " + debugPath);
        } catch (Exception e) {
            System.out.println("   ⚠️ 디버깅 캡처 실패: " + e.getMessage());
        }
    }
}