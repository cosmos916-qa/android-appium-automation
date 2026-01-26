package com.example.appium_android_automation.testcase;

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
 * 스모크 테스트 스위트 - 핵심 사용자 여정 점검 (앱 생존 신호 확인)
 *
 * 이 클래스는 앱의 가장 중요한 기능들이 정상 작동하는지 빠르게 확인하는
 * "건물 화재 감지기" 역할을 합니다.
 *
 * 🔥 스모크 테스트(Smoke Test)란?
 * - 건물에 불이 났는지 연기로 감지하듯, 앱의 핵심 기능에 "치명적 문제"가 있는지 빠르게 감지
 * - 모든 기능을 다 테스트하는 것이 아니라 "최소한의 생존 가능성" 확인
 * - 마치 자동차의 시동 → 계기판 → 주행 → 시동 끄기 과정을 점검하는 것과 같음
 *
 * 💡 왜 스모크 테스트가 필요한가요?
 *
 * **시나리오: 새 버전 배포 전 상황**
 * - 개발팀: "버그 수정 완료했습니다!"
 * - QA팀: "전체 테스트는 3일 걸리는데..."
 * - 스모크 테스트: 10분 만에 핵심 기능 정상 여부 확인
 * - 결과: 치명적 문제 없으면 상세 테스트 진행, 있으면 즉시 반려
 *
 * 🎯 이 스위트가 검증하는 핵심 여정:
 * 1. **TC01: 앱 실행** → 자동차 시동이 걸리는가?
 * 2. **TC02: 메인 화면 진입** → 계기판에 정상 표시가 나오는가?
 * 3. **TC03: 메인 화면 진입 동작** → 액셀을 밟으면 실제로 움직이는가?
 * 4. **TC04: 앱 종료** → 시동을 끌 때도 정상적으로 꺼지는가?
 *
 * 📋 테스트 실행 순서 보장:
 * @FixMethodOrder(MethodSorters.NAME_ASCENDING)
 * - TC01 → TC02 → TC03 → TC04 순서로 강제 실행
 * - 각 테스트는 이전 테스트의 성공을 전제로 진행
 * - TC01 실패 시 TC02는 Block 처리 (연쇄 의존성)
 *
 * ⚠️ Unity 앱 특성 반영:
 * - 모든 검증은 이미지 매칭 기반 (SurfaceView로 인한 UI 요소 직접 접근 불가)
 * - 로딩 시간 고려한 타임아웃 설정
 * - 해상도 독립적 좌표 계산 활용
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)  // TC01, TC02, TC03, TC04 순서 보장
public class SmokeTestSuite extends BaseTestCase {

    /**
     * TC01: 앱 실행 검증 (자동차 시동 걸리는지 확인)
     *
     * 가장 기본적인 생존 확인으로, 앱이 "켜지기는 하는가?"를 확인합니다.
     * 이 테스트가 실패하면 이후 모든 테스트가 의미없어집니다.
     *
     * 🎯 검증 목적:
     * - 앱이 크래시 없이 실행되는가?
     * - Appium과 스마트폰 연결이 정상인가?
     * - 앱 패키지와 Activity 설정이 올바른가?
     *
     * 📋 검증 단계:
     * 1. [PRE] 테스트 환경 준비 (BaseTestCase에서 자동 처리)
     * 2. [ACTION] StartAppFlow로 앱 실행 시도
     * 3. [ASSERT] 현재 실행 중인 패키지 확인
     * 4. [REPORT] 결과를 구글 시트 G3 셀에 기록
     *
     * ✅ Pass 조건:
     * - 앱이 포그라운드에서 실행 중
     * - 패키지명이 정상적으로 확인됨
     *
     * ❌ Fail 조건:
     * - 앱 크래시 발생
     * - 다른 앱이 실행됨
     * - 패키지명 확인 불가
     *
     * 💡 실패 시 체크포인트 (우선순위 순):
     * 1. Appium 서버가 실행 중인가?
     * 2. 스마트폰/에뮬레이터가 연결되어 있는가?
     * 3. 앱이 설치되어 있는가?
     * 4. AppiumConfig의 패키지명이 정확한가?
     */
    @Test
    public void TC01_앱_실행_검증() throws Exception {
        System.out.println("=== TC01: 앱 실행 검증 시작 ===");

        // [ACTION] 앱 실행 시도 (자동차 시동 걸기)
        System.out.println("[1/2] 앱 실행 중...");
        boolean appStarted = StartAppFlow.run(driver);
        System.out.println("→ 앱 실행 결과: " + (appStarted ? "성공 ✓" : "실패 ✗"));

        // [REPORT] 결과 기록 및 증거 수집
        // BaseTestCase.recordResult()가 자동으로:
        // 1) 화면 캡처 → TC01_StartApp_Pass/Fail_날짜시간.png
        // 2) 구글 시트 G3 셀에 Pass/Fail 기록
        // 3) 로그 출력 및 파일 경로 표시
        System.out.println("[2/2] 결과 기록 중...");
        recordResult(1, "StartApp", appStarted);

        // [ASSERT] JUnit 단언문으로 테스트 성공/실패 판정
        // 실패 시 이 메시지와 함께 테스트 중단 (이후 TC는 Block 처리됨)
        assertTrue("TC01 실패: 앱 실행 불가", appStarted);

        System.out.println("=== TC01 완료 ===\n");
    }

    /**
     * TC02: 메인 화면 로고 검증 (계기판 정상 표시 확인)
     *
     * 앱이 시동만 걸린 게 아니라 "제대로 된 화면"까지 진입했는지 확인합니다.
     * 자동차가 시동만 걸린 게 아니라 계기판에 정상 표시가 나오는지 확인하는 것과 같습니다.
     *
     * 🎯 검증 목적:
     * - 앱이 메인 화면까지 정상 로딩되었는가?
     * - 초기 리소스 다운로드가 완료되었는가?
     * - 서버 연결 및 패치 확인이 정상인가?
     *
     * 📋 검증 단계:
     * 1. [PRE] 전제조건: 앱이 실행 중인지 확인 (TC01 의존성)
     * 2. [ACTION] 메인 로고 이미지 매칭 (최대 30초 대기)
     * 3. [ASSERT] 로고 발견 여부 검증
     * 4. [REPORT] 결과를 구글 시트 G4 셀에 기록
     *
     * ⏰ 타임아웃 30초 이유:
     * - 앱 첫 실행 시 초기 리소스 다운로드 필요
     * - 서버 연결 및 패치 확인 시간 소요
     * - 저사양 기기에서의 느린 로딩 고려
     *
     * ✅ Pass 조건:
     * - 30초 이내에 메인 로고 이미지 발견
     *
     * ❌ Fail 조건:
     * - 30초 동안 로고 이미지 미발견
     * - 다른 화면에 머물러 있음 (에러 팝업 등)
     *
     * 🚫 Block 조건:
     * - TC01 실패로 앱이 실행되지 않음
     *
     * 💡 실패 시 체크포인트 (우선순위 순):
     * 1. 로고 이미지 파일이 올바른 위치에 있는가? (resources/images/)
     * 2. 이미지 파일 경로가 AppiumConfig에 정확히 설정되어 있는가?
     * 3. 앱 버전 업데이트로 로고가 변경되지 않았는가?
     * 4. 네트워크 연결이 정상인가? (서버 연결 필요 시)
     */
    @Test
    public void TC02_메인_화면_로고_검증() throws Exception {
        System.out.println("=== TC02: 메인 화면 로고 검증 시작 ===");

        // [PRE] 전제조건 확인: 앱 실행 상태 체크
        System.out.println("[1/3] 전제조건 확인: 앱 실행 상태 체크...");
        boolean appRunning = StartAppFlow.run(driver);

        if (!appRunning) {
            // [BLOCK] 앱이 실행되지 않아 테스트 진행 불가
            // 계기판을 확인하려면 먼저 시동이 걸려있어야 함
            recordBlock(2, "MainLogo", "앱 실행 불가 (TC01 선행 필요)");
            fail("TC02 실행 불가: 앱이 실행되지 않음");
            return;
        }
        System.out.println("→ 전제조건 통과 ✓");

        // [ACTION] 메인 로고 이미지 매칭 검증 (Unity SurfaceView 환경에서 유일한 방법)
        System.out.println("[2/3] 메인 로고 이미지 검증 중...");
        boolean logoVisible = ImageAssert.waitUntilImageVisible(
                driver,
                AppiumConfig.TARGET_LOGO_RESOURCE,      // 정답 이미지: images/target_logo.png
                AppiumConfig.MAIN_MARKER_TIMEOUT_SEC    // 30초 타임아웃
        );
        System.out.println("→ 로고 검증 결과: " + (logoVisible ? "성공 ✓" : "실패 ✗"));

        // [REPORT] 결과 기록 및 증거 수집
        System.out.println("[3/3] 결과 기록 중...");
        recordResult(2, "MainLogo", logoVisible);

        // [ASSERT] 로고가 발견되지 않으면 테스트 실패
        assertTrue("TC02 실패: 메인 로고 이미지 미발견", logoVisible);

        System.out.println("=== TC02 완료 ===\n");
    }

    /**
     * TC03: 메인 화면 진입 동작 검증 (액셀 작동 확인)
     *
     * 단순히 화면을 보는 것을 넘어 "실제로 조작이 가능한가?"를 확인합니다.
     * 자동차가 계기판만 켜진 게 아니라 액셀을 밟으면 실제로 움직이는지 확인하는 것과 같습니다.
     *
     * 🎯 검증 목적:
     * - 메인 화면에서 특정 동작이 정상적으로 작동하는가?
     * - 해상도 독립적 좌표 계산이 올바른가?
     * - 동작 후 다음 화면으로 정상 전환되는가?
     *
     * 📋 검증 단계:
     * 1. [PRE] 전제조건: 앱 실행 및 메인 화면 진입 확인 (TC01, TC02 의존성)
     * 2. [INFO] 현재 화면 정보 수집 (디버깅용)
     * 3. [ACTION] 해상도 독립적 드래그 동작 수행
     * 4. [WAIT] 로딩 대기 (3초)
     * 5. [ASSERT] 다음 화면 진입 확인 (이미지 매칭)
     * 6. [REPORT] 결과를 구글 시트 G5 셀에 기록
     *
     * 🎮 드래그 동작 방식:
     * - dragCheekAdaptive(): 화면 중앙 기준 (기본)
     * - dragCheekWithOffset(): 중앙에서 우하단 10% 치우친 위치 (작은 요소 대응)
     * - 모든 해상도에서 동일한 비율로 작동 (화면 너비의 20% 좌측 이동)
     *
     * ⏰ 타임아웃 15초 이유:
     * - 이미 앱이 로딩된 상태이므로 메인 화면보다 짧음
     * - 동작 후 화면 전환 애니메이션 시간 고려
     * - 다음 화면 초기 렌더링 시간 포함
     *
     * ✅ Pass 조건:
     * - 드래그 동작 후 15초 이내에 다음 화면 UI 요소 발견
     *
     * ❌ Fail 조건:
     * - 드래그 동작이 반응하지 않음
     * - 다음 화면으로 전환되지 않음
     * - 다음 화면 UI 요소를 찾지 못함
     *
     * 🚫 Block 조건:
     * - TC01 실패로 앱이 실행되지 않음
     * - TC02 실패로 메인 화면에 진입하지 못함
     *
     * 💡 실패 시 체크포인트 (우선순위 순):
     * 1. 드래그 좌표가 실제 요소 위치와 일치하는가?
     * 2. dragCheekAdaptive 실패 시 dragCheekWithOffset 시도
     * 3. 다음 화면 마커 이미지가 올바른가? (GAME_STARTED_MARKER_RESOURCE)
     * 4. 앱 업데이트로 UI 위치가 변경되지 않았는가?
     */
    @Test
    public void TC03_메인_화면_진입_동작_검증() throws Exception {
        System.out.println("=== TC03: 메인 화면 진입 동작 검증 시작 ===");

        // [PRE] 전제조건 1: 앱 실행 상태 확인
        System.out.println("[1/5] 전제조건 확인: 앱 실행 상태 체크...");
        boolean appRunning = StartAppFlow.run(driver);

        if (!appRunning) {
            recordBlock(3, "MainScreenEntry", "앱 실행 불가 (TC01 선행 필요)");
            fail("TC03 실행 불가: 앱이 실행되지 않음");
            return;
        }

        // [PRE] 전제조건 2: 메인 화면 진입 상태 확인
        // 액셀을 밟으려면 먼저 제대로 된 화면에 있어야 함
        boolean onMainScreen = ImageAssert.waitUntilImageVisible(
                driver,
                AppiumConfig.TARGET_LOGO_RESOURCE,
                10  // 이미 로딩된 상태이므로 짧은 타임아웃
        );

        if (!onMainScreen) {
            recordBlock(3, "MainScreenEntry", "메인 화면 미진입 (TC02 선행 필요)");
            fail("TC03 실행 불가: 메인 화면이 아님");
            return;
        }
        System.out.println("→ 전제조건 통과: 메인 화면 확인 ✓");

        // [INFO] 화면 정보 수집 (디버깅 및 검증용)
        // 해상도 문제 발생 시 이 로그로 원인 파악 가능
        System.out.println("[2/5] 화면 정보 수집 중...");
        ScreenHelper.printScreenInfo(driver);

        // [ACTION] 메인 화면 진입 동작 수행 (해상도 독립적 드래그)
        System.out.println("[3/5] 메인 화면 진입 동작 실행 중...");

        // 💡 두 가지 방식 중 선택 (현재는 작은 요소 대응용 사용):
        // TouchActionHelper.dragCheekAdaptive(driver);    // 화면 중앙 기준 (기본)
        TouchActionHelper.dragCheekWithOffset(driver);  // 중앙 우하단 10% 치우친 위치

        System.out.println("→ 드래그 완료 ✓");

        // [WAIT] 다음 화면 로딩 대기 (화면 전환 애니메이션 및 초기 렌더링)
        System.out.println("[4/5] 다음 화면 로딩 대기 중...");
        Thread.sleep(3000);

        // [ASSERT] 다음 화면 진입 확인 (이미지 매칭으로 검증)
        System.out.println("[5/5] 다음 화면 진입 확인 중...");
        boolean nextScreenVisible = ImageAssert.waitUntilImageVisible(
                driver,
                AppiumConfig.GAME_STARTED_MARKER_RESOURCE,  // 다음 화면 마커: images/target_menu.png
                AppiumConfig.GAME_START_VERIFY_TIMEOUT_SEC  // 15초 타임아웃
        );

        System.out.println("→ 다음 화면 진입 검증 결과: " + (nextScreenVisible ? "성공 ✓" : "실패 ✗"));

        // [REPORT] 결과 기록 및 증거 수집
        recordResult(3, "MainScreenEntry", nextScreenVisible);

        // [ASSERT] 다음 화면 진입 실패 시 테스트 실패
        assertTrue("TC03 실패: 메인 화면 진입 동작 미동작", nextScreenVisible);

        System.out.println("=== TC03 완료 ===\n");
    }

    /**
     * TC04: 앱 종료 검증 (정상 시동 끄기 확인)
     *
     * 앱이 시작만 잘 되는 게 아니라 "깔끔하게 종료"도 되는지 확인합니다.
     * 자동차가 시동만 걸리는 게 아니라 시동을 끌 때도 정상적으로 꺼지는지 확인하는 것과 같습니다.
     *
     * 🎯 검증 목적:
     * - 종료 프로세스가 정상적으로 작동하는가?
     * - 이미지 매칭으로 종료 버튼을 찾을 수 있는가?
     * - 터치 동작이 올바르게 수행되는가?
     * - 앱이 실제로 종료되는가?
     *
     * 📋 검증 단계:
     * 1. [PRE] 전제조건: 앱이 포그라운드에서 실행 중인지 확인
     * 2. [ACTION] Android Back 버튼으로 종료 팝업 호출
     * 3. [ACTION] 이미지 매칭으로 "종료" 버튼 위치 탐지
     * 4. [ACTION] 찾은 좌표를 터치하여 종료 실행
     * 5. [ASSERT] 앱 상태 확인으로 종료 여부 검증
     * 6. [REPORT] 결과를 구글 시트 G6 셀에 기록
     *
     * 🔍 종료 검증 방식:
     * - queryAppState()로 앱 상태 확인
     * - RUNNING_IN_FOREGROUND(화면에 앱이 떠있는 상태)가 아니면 종료 성공
     * - RUNNING_IN_BACKGROUND(백그라운드) 또는 NOT_RUNNING(완전 종료) 모두 Pass
     *
     * ⏰ 타임아웃 및 대기 시간:
     * - 종료 버튼 탐지: 10초 (팝업은 즉시 나타나지만 안전 여유)
     * - 팝업 애니메이션: 1.5초 (부드러운 등장 효과 대기)
     * - 종료 처리 대기: 3초 (데이터 저장 및 메모리 정리 시간)
     *
     * ✅ Pass 조건:
     * - 종료 버튼 이미지 매칭 성공
     * - 터치 후 앱이 RUNNING_IN_FOREGROUND 상태가 아님
     *
     * ❌ Fail 조건:
     * - 종료 버튼 이미지를 찾지 못함
     * - 터치 후에도 앱이 여전히 포그라운드에서 실행 중
     *
     * 🚫 Block 조건:
     * - TC01~TC03 실패로 앱이 정상 실행되지 않음
     *
     * 💡 실패 시 체크포인트 (우선순위 순):
     * 1. 종료 버튼 이미지가 올바른가? (EXIT_CONFIRM_BUTTON_RESOURCE)
     * 2. Back 버튼이 종료 팝업을 호출하는가?
     * 3. 앱 업데이트로 종료 UI가 변경되지 않았는가?
     * 4. 터치 좌표가 정확한가? (findImageCenter 결과 확인)
     */
    @Test
    public void TC04_앱_종료_검증() throws Exception {
        System.out.println("=== TC04: 앱 종료 검증 시작 ===");

        // [PRE] 전제조건 확인: 앱이 포그라운드에서 실행 중인지
        System.out.println("[1/5] 전제조건 확인: 앱 실행 상태 체크...");
        ApplicationState currentState = driver.queryAppState(AppiumConfig.APP_PACKAGE);

        if (currentState != ApplicationState.RUNNING_IN_FOREGROUND) {
            // 시동을 끄려면 먼저 시동이 걸려있어야 함
            recordBlock(4, "AppExit", "앱 미실행 (TC01-TC03 선행 필요)");
            fail("TC04 실행 불가: 앱이 포그라운드에서 실행 중이 아님");
            return;
        }
        System.out.println("→ 전제조건 통과: 앱 실행 중 ✓");

        // [ACTION] Android Back 버튼으로 종료 팝업 호출
        System.out.println("[2/5] Android Back 버튼 입력 중...");
        driver.navigate().back();  // 안드로이드 뒤로가기 = 종료 확인 팝업 트리거
        System.out.println("→ Back 버튼 입력 완료 ✓");

        // 팝업 애니메이션 대기 (부드러운 등장 효과)
        Thread.sleep(1500);

        // [ACTION] 종료 버튼 이미지 탐지 및 좌표 추출
        System.out.println("[3/5] 종료 확인 팝업의 '종료' 버튼 탐지 중...");
        Point exitButtonCenter = ImageAssert.findImageCenter(
                driver,
                AppiumConfig.EXIT_CONFIRM_BUTTON_RESOURCE,  // 종료 버튼 정답 이미지
                AppiumConfig.EXIT_BUTTON_TIMEOUT_SEC        // 10초 타임아웃
        );

        if (exitButtonCenter == null) {
            // [FAIL] 종료 버튼을 찾지 못함
            System.out.println("→ 종료 버튼 미발견 ✗");
            recordResult(4, "AppExit", false);
            fail("TC04 실패: 종료 버튼 이미지 매칭 실패");
            return;
        }
        System.out.println("→ 종료 버튼 발견 ✓");

        // [ACTION] 종료 버튼 터치 실행
        System.out.println("[4/5] 종료 버튼 터치 실행 중...");
        TouchActionHelper.tap(driver, exitButtonCenter);
        System.out.println("→ 터치 완료 ✓");

        // 앱 종료 처리 대기 (데이터 저장, 메모리 정리 등)
        Thread.sleep(AppiumConfig.EXIT_VERIFICATION_WAIT_MS);  // 3000ms = 3초

        // [ASSERT] 앱 종료 검증
        System.out.println("[5/5] 앱 종료 확인 중...");
        ApplicationState finalState = driver.queryAppState(AppiumConfig.APP_PACKAGE);
        System.out.println("→ 최종 앱 상태: " + finalState);

        // RUNNING_IN_FOREGROUND가 아니면 종료 성공
        // (RUNNING_IN_BACKGROUND 또는 NOT_RUNNING 모두 Pass)
        boolean appTerminated = (finalState != ApplicationState.RUNNING_IN_FOREGROUND);

        System.out.println("→ 종료 검증 결과: " + (appTerminated ? "성공 ✓" : "실패 ✗"));

        // [REPORT] 결과 기록 및 증거 수집
        recordResult(4, "AppExit", appTerminated);

        // [ASSERT] 앱이 종료되지 않았으면 테스트 실패
        assertTrue("TC04 실패: 앱 종료 미동작", appTerminated);

        System.out.println("=== TC04 완료 ===\n");
    }
}
