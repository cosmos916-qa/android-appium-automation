package com.example.appium_android_automation.flow;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.time.Duration;

import com.example.appium_android_automation.infra.AppiumConfig;
import com.example.appium_android_automation.infra.TouchActionHelper;
import com.example.appium_android_automation.marker.Evidence;
import com.example.appium_android_automation.marker.ImageAssert;

/**
 * 앱 최초 실행 시 필요한 전체 초기화 플로우를 담당합니다.
 *
 * 실행 단계:
 * 1) 앱 데이터 완전 초기화 (pm clear)
 * 2) 앱 재실행 및 Native 권한 팝업 처리
 * 3) Unity 다운로드 팝업에서 '다운로드' 버튼 터치
 * 4) 리소스 다운로드 완료까지 스마트 대기 (Polling 방식)
 * 5) 완료 후 게임 시작 버튼 터치
 * 6) 이용약관 동의 처리
 *
 * 기술적 특징:
 * - Native UI: By.id() 직접 접근 (권한 팝업)
 * - Unity UI: OpenCV 이미지 매칭 + 좌표 터치
 * - 효율적 대기: 5분 무작정 대기 대신 10초마다 완료 상태 확인
 */
public class FirstLaunchFlow {

    private final AndroidDriver driver;

    public FirstLaunchFlow(AndroidDriver driver) {
        this.driver = driver;
    }

    /**
     * 전체 First Launch 플로우를 실행합니다.
     *
     * @return 모든 단계 성공 시 true, 중간 실패 시 false
     */
    public boolean run() {
        System.out.println("🚀 [FirstLaunchFlow] === 최초 실행 플로우 시작 ===");

        // [1단계] 앱 데이터 완전 초기화
        if (!clearAppDataAndRestart()) {
            System.out.println("❌ [FirstLaunchFlow] 1단계 실패: 앱 데이터 초기화");
            return false;
        }

        // [2단계] Native 권한 팝업 처리 (Inspector ID 사용)
        if (!handleNativePermissions()) {
            System.out.println("❌ [FirstLaunchFlow] 2단계 실패: 권한 팝업 처리");
            return false;
        }

        // [3단계] Unity 다운로드 팝업 처리 (이미지 매칭)
        if (!initiateResourceDownload()) {
            System.out.println("❌ [FirstLaunchFlow] 3단계 실패: 다운로드 시작");
            return false;
        }

        // [4단계] 리소스 다운로드 완료까지 스마트 대기 (Polling)
        if (!waitForDownloadCompletion()) {
            System.out.println("❌ [FirstLaunchFlow] 4단계 실패: 다운로드 완료 대기");
            return false;
        }

        // [5단계] 다운로드 완료 후 게임 시작 버튼 터치
        if (!proceedToTermsScreen()) {
            System.out.println("❌ [FirstLaunchFlow] 5단계 실패: 게임 시작 버튼");
            return false;
        }

        // [6단계] 이용약관 동의 처리
        if (!handleTermsAgreement()) {
            System.out.println("❌ [FirstLaunchFlow] 6단계 실패: 이용약관 동의");
            return false;
        }

        System.out.println("✅ [FirstLaunchFlow] === 최초 실행 플로우 완료 ===");
        return true;
    }

    // =====================================================================
    // [1단계] 앱 데이터 완전 초기화
    // =====================================================================

    /**
     * adb shell pm clear로 앱을 "방금 설치한 상태"로 초기화합니다.
     * - 로그인 정보, 캐시, 설정 등 모든 데이터 삭제
     * - 초기화 후 앱 재실행
     */
    private boolean clearAppDataAndRestart() {
        System.out.println("🧹 [1/6] 앱 데이터 완전 초기화 중...");

        try {
            // pm clear 명령어 실행
            String command = String.format("adb shell pm clear %s", AppiumConfig.APP_PACKAGE);
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                System.out.println("   ❌ pm clear 명령어 실패 (exitCode: " + exitCode + ")");
                return false;
            }

            System.out.println("   ✓ 앱 데이터 초기화 완료");

            // 초기화 후 잠시 대기
            Thread.sleep(3000);

            // 앱 재실행
            System.out.println("   📱 앱 재실행 중...");
            driver.activateApp(AppiumConfig.APP_PACKAGE);
            Thread.sleep(5000); // Unity 엔진 로딩 대기

            System.out.println("   ✓ 앱 재실행 완료");
            return true;

        } catch (IOException | InterruptedException e) {
            System.out.println("   ❌ 앱 초기화 중 예외 발생: " + e.getMessage());
            return false;
        }
    }

    // =====================================================================
    // [2단계] Native 권한 팝업 처리 (Inspector 정보 활용)
    // =====================================================================

    /**
     * Android 시스템 알림 권한 팝업을 처리합니다.
     * - Inspector에서 확인한 표준 ID 사용
     * - 팝업이 없는 경우(이미 허용됨) 조용히 통과
     */
    private boolean handleNativePermissions() {
        System.out.println("🛡️ [2/6] Native 권한 팝업 확인 중...");

        try {
            // 짧은 대기시간으로 권한 팝업 찾기
            driver.manage().timeouts().implicitlyWait(
                    Duration.ofSeconds(AppiumConfig.PERMISSION_POPUP_TIMEOUT_SEC)
            );

            WebElement allowButton = driver.findElement(
                    By.id(AppiumConfig.NOTIFICATION_ALLOW_BUTTON_ID)
            );

            allowButton.click();
            System.out.println("   ✓ 알림 권한 '허용' 버튼 터치 완료");

            // 🆕 Unity 화면 전환 및 다운로드 팝업 로딩 대기
            System.out.println("   ⏳ Unity 화면 전환 대기 중... (5초)");
            Thread.sleep(5000);  // Native → Unity 컨텍스트 전환 시간 확보

            return true;

        } catch (NoSuchElementException | TimeoutException e) {
            // 권한 팝업이 없는 경우 (이미 허용된 단말 등)
            System.out.println("   ⚠️ 권한 팝업 없음 (이미 허용되었거나 불필요). 계속 진행.");

            // 팝업이 없어도 앱 초기 로딩 시간은 필요
            try {
                System.out.println("   ⏳ 앱 초기 로딩 대기 중... (3초)");
                Thread.sleep(3000);
            } catch (InterruptedException ie) {
                System.out.println("   ⚠️ 대기 중 인터럽트 발생");
            }

            return true;

        } catch (InterruptedException e) {  // Thread.sleep 예외 처리
            System.out.println("   ❌ 대기 중 인터럽트 발생: " + e.getMessage());
            return false;

        } finally {
            // 암시적 대기시간 원래 값으로 복구
            driver.manage().timeouts().implicitlyWait(
                    Duration.ofSeconds(AppiumConfig.IMPLICIT_WAIT_SEC)
            );
        }
    }

    // =====================================================================
    // [3단계] Unity 다운로드 팝업 처리 (이미지 매칭)
    // =====================================================================

    /**
     * "최신 데이터가 있습니다. 3.6GB" 팝업에서 '다운로드' 버튼을 찾아 터치합니다.
     * - OpenCV 이미지 매칭 사용 (Unity SurfaceView 내부 UI)
     */
    private boolean initiateResourceDownload() {
        System.out.println("⬇️ [3/6] 리소스 다운로드 팝업 처리 중...");

        // 다운로드 버튼 이미지가 나타날 때까지 대기
        boolean isDownloadButtonVisible = ImageAssert.waitUntilImageVisible(
                driver,
                AppiumConfig.FIRST_DOWNLOAD_BUTTON_RESOURCE,
                AppiumConfig.MAIN_MARKER_TIMEOUT_SEC
        );

        if (!isDownloadButtonVisible) {
            System.out.println("   ❌ 다운로드 버튼 이미지를 찾을 수 없습니다");
            return false;
        }

        try {
            // 이미지 중앙 좌표 계산 후 터치
            TouchActionHelper.tapOnImageCenter(
                    driver,
                    AppiumConfig.FIRST_DOWNLOAD_BUTTON_RESOURCE
            );
            System.out.println("   ✓ '다운로드' 버튼 터치 완료");
            System.out.println("   📥 리소스 다운로드 시작 (예상 소요시간: 4-5분)");
            return true;

        } catch (Exception e) {
            System.out.println("   ❌ 다운로드 버튼 터치 실패: " + e.getMessage());
            return false;
        }
    }

    // =====================================================================
    // [4단계] 리소스 다운로드 완료까지 스마트 대기 (핵심 로직)
    // =====================================================================

    /**
     * 리소스 다운로드 완료를 효율적으로 감지합니다.
     *
     * 전략: 5분 무작정 대기 대신 Polling 방식 사용
     * - 10초마다 "다운로드 완료 마커 이미지" 확인
     * - 완료되면 즉시 다음 단계로 진행 (시간 절약)
     * - 최대 6분까지 대기 (여유시간 포함)
     */
    private boolean waitForDownloadCompletion() {
        System.out.println("⏳ [4/6] 리소스 다운로드 완료 대기 중...");

        // [Phase 1] 오탐지 방지를 위한 최소 안전 대기 (5분)
        int minWaitSeconds = 270;  // 다운로드가 절대 이보다 빠를 수 없음
        System.out.printf("   🛡️ 오탐지 방지: %d초간 무조건 대기합니다...%n", minWaitSeconds);

        long phaseStartTime = System.currentTimeMillis();

        try {
            // 30초마다 진행 상황 출력
            for (int i = 0; i < minWaitSeconds / 30; i++) {
                Thread.sleep(30000);
                int elapsed = (i + 1) * 30;
                System.out.printf("   ⏱️ 다운로드 진행 중... (%d초 / %d초)%n", elapsed, minWaitSeconds);
            }
        } catch (InterruptedException e) {
            System.out.println("   ❌ 최소 대기 중 인터럽트 발생");
            return false;
        }

        System.out.println("   ✅ 최소 대기 완료. 이제부터 완료 마커 검사 시작");

        // [Phase 2] 실제 완료 여부 검사 (남은 시간 동안)
        long remainingTimeoutSec = AppiumConfig.RESOURCE_DOWNLOAD_TIMEOUT_SEC - minWaitSeconds;
        long endTime = System.currentTimeMillis() + (remainingTimeoutSec * 1000L);
        int checkCount = 0;

        while (System.currentTimeMillis() < endTime) {
            checkCount++;

            // 다운로드 완료 마커 확인
            boolean isCompleted = ImageAssert.isImageVisible(
                    driver,
                    AppiumConfig.DOWNLOAD_COMPLETE_BUTTON_RESOURCE
            );

            if (isCompleted) {
                long totalElapsed = (System.currentTimeMillis() - phaseStartTime) / 1000;
                System.out.printf("   ✅ 다운로드 완료 감지! (총 소요시간: %d초, 검사횟수: %d회)%n",
                        totalElapsed, checkCount);

                // 🆕 완료 시점 디버깅 캡처 (검증용)
                try {
                    String debugPath = Evidence.saveScreenshot(driver,
                            "DEBUG_download_complete_verified_" + totalElapsed + "sec");
                    System.out.println("   📸 완료 시점 화면 캡처: " + debugPath);
                } catch (Exception e) {
                    System.out.println("   ⚠️ 디버깅 캡처 실패: " + e.getMessage());
                }

                return true;
            }

            // 진행 표시
            if (checkCount % 6 == 0) {
                long totalElapsed = (System.currentTimeMillis() - phaseStartTime) / 1000;
                System.out.printf("   🔍 완료 검사 중... (%d초 경과)%n", totalElapsed);
            } else {
                System.out.print(".");
            }

            try {
                Thread.sleep(AppiumConfig.DOWNLOAD_CHECK_INTERVAL_SEC * 1000L);
            } catch (InterruptedException e) {
                System.out.println("   ❌ 검사 대기 중 인터럽트 발생");
                return false;
            }
        }

        // 타임아웃 발생
        long totalElapsed = (System.currentTimeMillis() - phaseStartTime) / 1000;
        System.out.printf("   ❌ 다운로드 완료 타임아웃 (총 %d초 경과)%n", totalElapsed);

        // 🆕 타임아웃 시점 디버깅 캡처
        try {
            String debugPath = Evidence.saveScreenshot(driver, "DEBUG_download_timeout_" + totalElapsed + "sec");
            System.out.println("   📸 타임아웃 시점 화면 캡처: " + debugPath);
        } catch (Exception e) {
            // 무시
        }

        return false;
    }

    // =====================================================================
    // [5단계] 다운로드 완료 후 게임 시작 버튼 터치
    // =====================================================================

    /**
     * 다운로드 완료 후 우측 하단에 나타나는 버튼을 터치합니다.
     * - 스크린샷 4번: "교주.. 안일어나?" / "트릭컬로 출발!" 말풍선
     */
    private boolean proceedToTermsScreen() {
        System.out.println("🎮 [5/6] 게임 시작 버튼 터치 중...");

        try {
            TouchActionHelper.tapOnImageCenter(
                    driver,
                    AppiumConfig.DOWNLOAD_COMPLETE_BUTTON_RESOURCE
            );
            System.out.println("   ✓ 게임 시작 버튼 터치 완료");

            // 🆕 이용약관 화면 로딩 대기 증가
            System.out.println("   ⏳ 이용약관 화면 전환 대기 중... (5초)");
            Thread.sleep(10000);  // 10초 대기
            return true;

        } catch (Exception e) {
            System.out.println("   ❌ 게임 시작 버튼 터치 실패: " + e.getMessage());
            return false;
        }
    }

    // =====================================================================
    // [6단계] 이용약관 동의 처리
    // =====================================================================

    /**
     * 이용약관 화면에서 "모두 동의하고 시작" 처리를 수행합니다.
     * - 스크린샷 5번: EPID 이용약관 화면
     */
    private boolean handleTermsAgreement() {
        System.out.println("📋 [6/6] 이용약관 동의 처리 중...");

        // 이용약관 화면 로딩 대기
        boolean isTermsScreenVisible = ImageAssert.waitUntilImageVisible(
                driver,
                AppiumConfig.TERMS_SCREEN_MARKER_RESOURCE,
                AppiumConfig.MAIN_MARKER_TIMEOUT_SEC
        );

        if (!isTermsScreenVisible) {
            System.out.println("   ⚠️ 이용약관 화면 마커를 찾지 못했지만 동의 버튼 직접 시도");

            // 🆕 현재 화면 상태 캡처 (디버깅용)
            try {
                String debugPath = Evidence.saveScreenshot(driver, "DEBUG_terms_screen_not_found");
                System.out.println("   📸 현재 화면 캡처: " + debugPath);
            } catch (Exception e) {
                // 무시
            }
        } else {
            // 🆕 화면 완전 로딩 대기
            try {
                System.out.println("   ⏳ 이용약관 UI 완성 대기 (2초)");
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {}
        }

        // "모두 동의하고 시작" 버튼 찾기 및 터치
        boolean isAgreeButtonVisible = ImageAssert.waitUntilImageVisible(
                driver,
                AppiumConfig.TERMS_AGREE_ALL_BUTTON_RESOURCE,
                AppiumConfig.MAIN_MARKER_TIMEOUT_SEC
        );

        if (!isAgreeButtonVisible) {
            System.out.println("   ❌ '모두 동의하고 시작' 버튼을 찾을 수 없습니다");

            // 🆕 현재 화면 캡처 (디버깅용)
            try {
                String debugPath = Evidence.saveScreenshot(driver, "DEBUG_terms_agree_button_not_found");
                System.out.println("   📸 현재 화면 캡처: " + debugPath);
                System.out.println("   💡 이 화면과 terms_agree_all_button.png를 비교하세요!");
            } catch (Exception e) {
                // 무시
            }

            return false;
        }

        try {
            TouchActionHelper.tapOnImageCenter(
                    driver,
                    AppiumConfig.TERMS_AGREE_ALL_BUTTON_RESOURCE
            );
            System.out.println("   ✓ 이용약관 동의 완료");

            // 최종 화면 전환 대기
            Thread.sleep(3000);
            return true;

        } catch (Exception e) {
            System.out.println("   ❌ 이용약관 동의 버튼 터치 실패: " + e.getMessage());
            return false;
        }
    }
}
