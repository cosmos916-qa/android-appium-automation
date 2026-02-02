package com.example.appium_android_automation.testcase;

import com.example.appium_android_automation.flow.FirstLaunchFlow;
import com.example.appium_android_automation.flow.LoginFlow;
import com.example.appium_android_automation.flow.StartAppFlow;
import com.example.appium_android_automation.infra.AppiumConfig;
import com.example.appium_android_automation.infra.ScreenHelper;
import com.example.appium_android_automation.infra.TouchActionHelper;
import com.example.appium_android_automation.main.BaseTestCase;
import com.example.appium_android_automation.marker.ImageAssert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.Point;

import static org.junit.Assert.*;

import io.appium.java_client.appmanagement.ApplicationState;

/**
 * 스모크 테스트 스위트 - 핵심 기능 체크리스트
 * TC01: 앱 실행, TC02: 메인 화면, TC03: 메인 진입 동작, TC04: 앱 종료
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)  // TC01, TC02 순서 보장
public class SmokeTestSuite extends BaseTestCase {
    /**
     * TC06: 구글 계정 최초 로그인
     *
     * 전제조건: TC05 완료 (이용약관 동의 후 타이틀 화면)
     */
    @Test
    public void TC06_GoogleFirstLogin() throws Exception {
        System.out.println("=== TC06: 구글 계정 최초 로그인 ===");

        // 로그인 플로우 실행
        LoginFlow loginFlow = new LoginFlow(driver);
        boolean success = loginFlow.runFirstLogin(AppiumConfig.TARGET_GOOGLE_EMAIL);

        // 결과 기록
        recordResult(6, "GoogleFirstLogin", success);
        assertTrue("구글 로그인 실패. 디버깅 캡처를 확인하세요.", success);

        System.out.println("✅ TC06 완료: 구글 로그인 성공");
    }

    //@Test
    public void TC05_FirstLaunchAndSetup() throws Exception {
        System.out.println("=== TC05: 최초 앱 실행 및 초기 설정 ===");

        FirstLaunchFlow firstLaunch = new FirstLaunchFlow(driver);
        boolean success = firstLaunch.run();

        recordResult(5, "FirstLaunchAndSetup", success);

        assertTrue("FirstLaunchFlow 실행 실패", success);
    }

    //@Test
    public void TC01_App_start_verification() throws Exception {
        System.out.println("=== TC01: 앱 실행 검증 시작 ===");

        // 앱 실행
        System.out.println("[1/2] 앱 실행 중...");
        boolean appStarted = StartAppFlow.run(driver);
        System.out.println("→ 앱 실행 결과: " + (appStarted ? "성공 ✓" : "실패 ✗"));

        // 결과 기록 (동적 계산 사용: TC01 → F4 셀)
        System.out.println("[2/2] 결과 기록 중...");
        recordResult(1, "StartApp", appStarted);

        // Assertion
        assertTrue("TC01 실패: 앱 실행 불가", appStarted);

        System.out.println("=== TC01 완료 ===\n");
    }

    //@Test
    public void TC02_Main_screen_logo_verification() throws Exception {
        System.out.println("=== TC02: 메인 화면 로고 검증 시작 ===");

        // [Step 1] 전제조건 확인
        System.out.println("[1/3] 전제조건 확인: 앱 실행 상태 체크...");
        boolean appRunning = StartAppFlow.run(driver);

        if (!appRunning) {
            recordBlock(2, "MainLogo", "앱 실행 불가 (TC01 선행 필요)");
            fail("TC02 실행 불가: 앱이 실행되지 않음");
            return;
        }
        System.out.println("→ 전제조건 통과 ✓");

        // [Step 2] 로고 이미지 검증
        System.out.println("[2/3] 메인 로고 이미지 검증 중...");
        boolean logoVisible = ImageAssert.waitUntilImageVisible(
                driver,
                AppiumConfig.TARGET_LOGO_RESOURCE,
                AppiumConfig.MAIN_MARKER_TIMEOUT_SEC
        );
        System.out.println("→ 로고 검증 결과: " + (logoVisible ? "성공 ✓" : "실패 ✗"));

        // [Step 3] 결과 기록 (동적 계산 사용: TC02 → F4 셀)
        System.out.println("[3/3] 결과 기록 중...");
        recordResult(2, "MainLogo", logoVisible);

        // Assertion
        assertTrue("TC02 실패: 메인 로고 이미지 미발견", logoVisible);

        System.out.println("=== TC02 완료 ===\n");
    }

    //@Test
    public void TC03_game_start_after_drag() throws Exception {
        System.out.println("=== TC03: 드래그 이후 게임시작 검증 시작 ===");

        // [Step 1] 전제조건 확인
        System.out.println("[1/5] 전제조건 확인: 메인 화면 진입 체크...");
        boolean appRunning = StartAppFlow.run(driver);

        if (!appRunning) {
            recordBlock(3, "CheekDragStart", "앱 실행 불가 (TC01 선행 필요)");
            fail("TC03 실행 불가: 앱이 실행되지 않음");
            return;
        }

        boolean onMainScreen = ImageAssert.waitUntilImageVisible(
                driver,
                AppiumConfig.TARGET_LOGO_RESOURCE,
                10
        );

        if (!onMainScreen) {
            recordBlock(3, "CheekDragStart", "메인 화면 미진입 (TC02 선행 필요)");
            fail("TC03 실행 불가: 메인 화면이 아님");
            return;
        }
        System.out.println("→ 전제조건 통과: 메인 화면 확인 ✓");

        // [Step 2] 화면 정보 출력 (디버깅용)
        System.out.println("[2/5] 화면 정보 수집 중...");
        ScreenHelper.printScreenInfo(driver);

        // [Step 3] 캐릭터 볼 당기기 (⭐ 개선된 해상도 독립적 버전!)
        System.out.println("[3/5] 드래그 실행 중...");
        TouchActionHelper.dragCheekAdaptive(driver);    //기존 중앙 방식
        //TouchActionHelper.dragCheekWithOffset(driver);  // 작은 캐릭터 전용
        System.out.println("→ 드래그 완료 ✓");

        // [Step 4] 게임 시작 로딩 대기
        System.out.println("[4/5] 게임 시작 로딩 대기 중...");
        Thread.sleep(3000);

        // [Step 5] 게임 시작 확인
        System.out.println("[5/5] 게임 시작 확인 중...");
        boolean gameStarted = ImageAssert.waitUntilImageVisible(
                driver,
                AppiumConfig.GAME_STARTED_MARKER_RESOURCE,
                AppiumConfig.GAME_START_VERIFY_TIMEOUT_SEC
        );

        System.out.println("→ 게임 시작 검증 결과: " + (gameStarted ? "성공 ✓" : "실패 ✗"));

        // 결과 기록 (TC03 → F6 셀)
        recordResult(3, "CheekDragStart", gameStarted);

        assertTrue("TC03 실패: 드래그 이후 게임 시작 미동작", gameStarted);
        System.out.println("=== TC03 완료 ===\n");
    }

    //@Test
    public void TC04_game_exit_verification() throws Exception {
        System.out.println("=== TC04: 게임 종료 검증 시작 ===");

        // [Step 1] 전제조건 확인: 게임 실행 중
        System.out.println("[1/5] 전제조건 확인: 게임 실행 상태 체크...");
        ApplicationState currentState = driver.queryAppState(AppiumConfig.APP_PACKAGE);

        if (currentState != ApplicationState.RUNNING_IN_FOREGROUND) {
            recordBlock(4, "GameExit", "게임 미실행 (TC01-TC03 선행 필요)");
            fail("TC04 실행 불가: 앱이 포그라운드에서 실행 중이 아님");
            return;
        }
        System.out.println("→ 전제조건 통과: 게임 실행 중 ✓");

        // [Step 2] Android Back 버튼 입력
        System.out.println("[2/5] Android Back 버튼 입력 중...");
        driver.navigate().back();  // 종료 확인 팝업 호출
        System.out.println("→ Back 버튼 입력 완료 ✓");

        // 팝업 애니메이션 대기
        Thread.sleep(1500);

        // [Step 3] 종료 버튼 이미지 탐지 및 좌표 추출
        System.out.println("[3/5] 종료 확인 팝업의 '종료' 버튼 탐지 중...");
        Point exitButtonCenter = ImageAssert.findImageCenter(
                driver,
                AppiumConfig.EXIT_CONFIRM_BUTTON_RESOURCE,
                AppiumConfig.EXIT_BUTTON_TIMEOUT_SEC
        );

        if (exitButtonCenter == null) {
            System.out.println("→ 종료 버튼 미발견 ✗");
            recordResult(4, "GameExit", false);
            fail("TC04 실패: 종료 버튼 이미지 매칭 실패");
            return;
        }
        System.out.println("→ 종료 버튼 발견 ✓");

        // [Step 4] 종료 버튼 터치
        System.out.println("[4/5] 종료 버튼 터치 실행 중...");
        TouchActionHelper.tap(driver, exitButtonCenter);
        System.out.println("→ 터치 완료 ✓");

        // 앱 종료 처리 대기
        Thread.sleep(AppiumConfig.EXIT_VERIFICATION_WAIT_MS);

        // [Step 5] 앱 종료 검증
        System.out.println("[5/5] 앱 종료 확인 중...");
        ApplicationState finalState = driver.queryAppState(AppiumConfig.APP_PACKAGE);
        System.out.println("→ 최종 앱 상태: " + finalState);

        // RUNNING_IN_FOREGROUND가 아니면 종료 성공 (BACKGROUND 또는 NOT_RUNNING)
        boolean appTerminated = (finalState != ApplicationState.RUNNING_IN_FOREGROUND);

        System.out.println("→ 종료 검증 결과: " + (appTerminated ? "성공 ✓" : "실패 ✗"));

        // [Step 6] 결과 기록 (TC04 → F7 셀)
        recordResult(4, "GameExit", appTerminated);

        // Assertion
        assertTrue("TC04 실패: 게임 종료 미동작", appTerminated);

        System.out.println("=== TC04 완료 ===\n");
    }

}