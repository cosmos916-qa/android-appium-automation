package com.example.appium_android_automation.flow;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

import com.example.appium_android_automation.infra.AppiumConfig;
import com.example.appium_android_automation.infra.TouchActionHelper;
import com.example.appium_android_automation.marker.ImageAssert;
import com.example.appium_android_automation.marker.Evidence;

/**
 * 구글 계정 로그인 플로우
 *
 * 실행 단계:
 * 1) Unity 타이틀 화면에서 "Google로그인" 버튼 터치 (이미지 매칭)
 * 2) Native 계정 선택 화면에서 특정 계정 선택 (XPath 텍스트 매칭)
 * 3) 로그인 완료 후 Unity 메인 화면 진입 확인 (이미지 매칭)
 *
 * 기술적 특징:
 * - 하이브리드 UI 제어: Unity(이미지) + Native(요소 접근)
 * - 최초 로그인 vs 재로그인 구분 처리
 * - 상세한 에러 처리 및 디버깅 캡처
 */
public class LoginFlow {

    private final AndroidDriver driver;

    public LoginFlow(AndroidDriver driver) {
        this.driver = driver;
    }

    /**
     * 최초 로그인 실행 (계정 선택 포함)
     *
     * @param targetEmail 선택할 구글 계정 (예: "cosmos9169951@gmail.com")
     * @return 로그인 성공 시 true, 실패 시 false
     */
    public boolean runFirstLogin(String targetEmail) {
        System.out.println("🔐 [LoginFlow] === 구글 최초 로그인 시작 ===");
        System.out.println("   📧 대상 계정: " + targetEmail);

        // [1단계] Google로그인 버튼 찾기 및 터치 (Unity UI)
        if (!tapGoogleLoginButton()) {
            System.out.println("❌ [LoginFlow] 1단계 실패: Google로그인 버튼");
            return false;
        }

        // [2단계] 구글 계정 선택 화면 대기 및 처리 (Native UI)
        if (!selectGoogleAccount(targetEmail)) {
            System.out.println("❌ [LoginFlow] 2단계 실패: 계정 선택");
            return false;
        }

        // [3단계] 로그인 완료 및 메인 화면 진입 확인 (Unity UI)
        if (!verifyLoginSuccess()) {
            System.out.println("❌ [LoginFlow] 3단계 실패: 로그인 완료 확인");
            return false;
        }

        System.out.println("✅ [LoginFlow] === 구글 최초 로그인 완료 ===");
        return true;
    }

    /**
     * 재로그인 실행 (구글 세션 유지 시 계정 선택 생략)
     *
     * @return 로그인 성공 시 true, 실패 시 false
     */
    public boolean runReLogin() {
        System.out.println("🔐 [LoginFlow] === 구글 재로그인 시작 (세션 활용) ===");

        // [1단계] Google로그인 버튼 터치
        if (!tapGoogleLoginButton()) {
            System.out.println("❌ [LoginFlow] 재로그인 실패: Google로그인 버튼");
            return false;
        }

        // [2단계] 자동 로그인 처리 대기 (계정 선택 화면 생략됨)
        try {
            System.out.println("   ⏳ 자동 로그인 처리 대기 중... (5초)");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.out.println("   ⚠️ 대기 중 인터럽트 발생");
        }

        // [3단계] 로그인 완료 확인
        if (!verifyLoginSuccess()) {
            System.out.println("❌ [LoginFlow] 재로그인 실패: 로그인 완료 확인");
            return false;
        }

        System.out.println("✅ [LoginFlow] === 구글 재로그인 완료 ===");
        return true;
    }

    // =====================================================================
    // [1단계] Google로그인 버튼 터치 (Unity UI - 이미지 매칭)
    // =====================================================================

    /**
     * 타이틀 화면에서 "Google로그인" 버튼을 찾아 터치합니다.
     * - Unity UI이므로 이미지 매칭 사용
     */
    private boolean tapGoogleLoginButton() {
        System.out.println("   🔍 [1/3] Google로그인 버튼 탐색 중...");

        // Google로그인 버튼 이미지 대기
        boolean buttonVisible = ImageAssert.waitUntilImageVisible(
                driver,
                AppiumConfig.GOOGLE_LOGIN_BUTTON_RESOURCE,
                AppiumConfig.MAIN_MARKER_TIMEOUT_SEC
        );

        if (!buttonVisible) {
            System.out.println("   ❌ Google로그인 버튼 이미지를 찾을 수 없습니다");

            // 디버깅 캡처
            try {
                String debugPath = Evidence.saveScreenshot(driver, "DEBUG_google_login_button_not_found");
                System.out.println("   📸 디버깅 캡처: " + debugPath);
                System.out.println("   💡 이 화면과 google_login_button.png를 비교하세요!");
            } catch (Exception e) {
                // 무시
            }

            return false;
        }

        // 버튼 터치
        try {
            TouchActionHelper.tapOnImageCenter(
                    driver,
                    AppiumConfig.GOOGLE_LOGIN_BUTTON_RESOURCE
            );
            System.out.println("   ✅ Google로그인 버튼 터치 완료");

            // 계정 선택 화면 전환 대기
            Thread.sleep(3000);
            return true;

        } catch (Exception e) {
            System.out.println("   ❌ 버튼 터치 실패: " + e.getMessage());
            return false;
        }
    }

    // =====================================================================
    // [2단계] 구글 계정 선택 (Native UI - XPath 텍스트 매칭)
    // =====================================================================

    /**
     * 구글 계정 선택 화면에서 특정 이메일 계정을 찾아 터치합니다.
     *
     * 핵심 기술:
     * - Native UI이므로 XPath로 텍스트 직접 접근 가능
     * - 이미지 매칭보다 빠르고 정확함
     * - 해상도에 영향받지 않음
     *
     * @param targetEmail 선택할 계정 (예: "cosmos9169951@gmail.com")
     */
    private boolean selectGoogleAccount(String targetEmail) {
        System.out.println("   👤 [2/3] 구글 계정 선택 중: " + targetEmail);

        try {
            // Native UI 요소 대기 (WebDriverWait 사용)
            WebDriverWait wait = new WebDriverWait(driver,
                    Duration.ofSeconds(AppiumConfig.ACCOUNT_SELECTION_TIMEOUT_SEC));

            // XPath로 정확한 이메일 텍스트 매칭
            String xpath = String.format("//android.widget.TextView[@text='%s']", targetEmail);

            WebElement accountElement = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath(xpath))
            );

            System.out.println("   ✅ 타겟 계정 발견: " + accountElement.getText());
            accountElement.click();
            System.out.println("   ✅ 계정 선택 완료");

            // 로그인 처리 대기
            Thread.sleep(5000);
            return true;

        } catch (TimeoutException e) {
            System.out.println("   ❌ 계정 선택 타임아웃: " + targetEmail);

            // 디버깅: 현재 화면의 모든 계정 출력
            try {
                var allTextElements = driver.findElements(
                        By.xpath("//android.widget.TextView[contains(@text, '@')]")
                );
                System.out.println("   📋 화면에 표시된 이메일 계정 목록:");
                for (WebElement element : allTextElements) {
                    String text = element.getText().trim();
                    if (text.contains("@")) {
                        System.out.println("      - " + text);
                    }
                }
            } catch (Exception ex) {
                System.out.println("   ⚠️ 계정 목록 확인 실패");
            }

            // 디버깅 캡처
            captureDebugScreen("account_selection_timeout");
            return false;

        } catch (NoSuchElementException e) {
            System.out.println("   ❌ 계정을 찾을 수 없습니다: " + targetEmail);
            captureDebugScreen("account_not_found");
            return false;

        } catch (Exception e) {
            System.out.println("   ❌ 계정 선택 중 예외 발생: " + e.getMessage());
            return false;
        }
    }

    // =====================================================================
    // [3단계] 로그인 완료 확인 (Unity UI - 이미지 매칭)
    // =====================================================================

    /**
     * 로그인 완료 후 메인 화면에 진입했는지 확인합니다.
     * - Unity 메인 화면의 특정 마커 이미지로 검증
     */
    private boolean verifyLoginSuccess() {
        System.out.println("   🔍 [3/3] 로그인 완료 확인 중...");

        // 메인 화면 마커 확인 (기존 TC02의 로고나 메뉴 버튼 재사용)
        boolean mainScreenVisible = ImageAssert.waitUntilImageVisible(
                driver,
                AppiumConfig.TARGET_LOGO_RESOURCE,
                AppiumConfig.LOGIN_PROCESSING_TIMEOUT_SEC
        );

        if (mainScreenVisible) {
            System.out.println("   ✅ 로그인 완료: 메인 화면 진입 확인");
            return true;
        } else {
            System.out.println("   ❌ 로그인 실패: 메인 화면 마커 미발견");
            captureDebugScreen("login_verification_failed");
            return false;
        }
    }

    /**
     * 디버깅용 화면 캡처 헬퍼 메서드
     */
    private void captureDebugScreen(String suffix) {
        try {
            String debugPath = Evidence.saveScreenshot(driver, "DEBUG_login_" + suffix);
            System.out.println("   📸 디버깅 캡처: " + debugPath);
        } catch (Exception e) {
            System.out.println("   ⚠️ 디버깅 캡처 실패: " + e.getMessage());
        }
    }
}
