package com.example.appium_android_automation.testcase;

import com.example.appium_android_automation.flow.StartAppFlow;
import com.example.appium_android_automation.infra.AppiumConfig;
import com.example.appium_android_automation.main.BaseTestCase;
import com.example.appium_android_automation.marker.ImageAssert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

/**
 * 스모크 테스트 스위트 - 핵심 기능 체크리스트
 *
 * <p>트릭컬 리바이브의 핵심 기능들을 빠르게 검증하는 스모크 테스트</p>
 *
 * <h3>포함 TC</h3>
 * <ul>
 *   <li>TC01: 앱 실행 검증</li>
 *   <li>TC02: 메인 로고 검증</li>
 *   <li>TC03-TC05: 향후 확장 예정</li>
 * </ul>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)  // TC01, TC02 순서 보장
public class SmokeTestSuite extends BaseTestCase {

    private static final String TARGET_LOGO_IMAGE = "images/target_logo.png";

    @Test
    public void TC01_앱_실행_검증() throws Exception {
        System.out.println("=== TC01: 앱 실행 검증 시작 ===");

        // 앱 실행
        System.out.println("[1/2] 트릭컬 리바이브 앱 실행 중...");
        boolean appStarted = StartAppFlow.run(driver);
        System.out.println("→ 앱 실행 결과: " + (appStarted ? "성공 ✓" : "실패 ✗"));

        // 결과 기록 (동적 계산 사용: TC01 → F3 셀)
        System.out.println("[2/2] 결과 기록 중...");
        recordResult(1, "StartApp", appStarted);

        // Assertion
        assertTrue("TC01 실패: 앱 실행 불가", appStarted);

        System.out.println("=== TC01 완료 ===\n");
    }

    @Test
    public void TC02_메인_화면_로고_검증() throws Exception {
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
                TARGET_LOGO_IMAGE,
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

    // TC03, TC04, TC05... 여기에 메서드로 계속 추가
    // 10-15개 정도까지는 이 파일에서 관리 가능
}