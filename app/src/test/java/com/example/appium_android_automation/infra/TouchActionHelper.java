package com.example.appium_android_automation.infra;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.List;

/**
 * 게임 내 터치/드래그 액션 유틸리티
 *
 * <p>Unity SurfaceView 기반 게임에서 좌표 기반 인터랙션 제공</p>
 *
 * @author SeongSoo Park
 * @since 2025-01-24
 */
public class TouchActionHelper {

    /**
     * 지정된 좌표에서 드래그 앤 드롭을 수행합니다.
     *
     * <h3>사용 사례</h3>
     * - 트릭컬 리바이브 캐릭터 볼 당기기
     * - 게임 내 스와이프 제스처
     *
     * @param driver AndroidDriver 인스턴스
     * @param startX 시작 X 좌표
     * @param startY 시작 Y 좌표
     * @param endX 끝 X 좌표
     * @param endY 끝 Y 좌표
     * @param durationMs 드래그 지속 시간 (밀리초)
     */
    public static void dragAndDrop(AndroidDriver driver,
                                   int startX, int startY,
                                   int endX, int endY,
                                   int durationMs) {

        System.out.println("[TouchAction] 드래그 시작: (" + startX + "," + startY + ") → " +
                "종료: (" + endX + "," + endY + "), 지속시간: " + durationMs + "ms");

        // W3C Actions API 사용 (Appium 권장 최신 방식)
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence dragSequence = new Sequence(finger, 1);

        // 1. 시작 지점으로 포인터 이동
        dragSequence.addAction(finger.createPointerMove(
                Duration.ZERO,
                PointerInput.Origin.viewport(),
                startX, startY
        ));

        // 2. 터치 다운 (누르기)
        dragSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));

        // 3. 끝 지점으로 드래그 (지정된 시간 동안 자연스럽게)
        dragSequence.addAction(finger.createPointerMove(
                Duration.ofMillis(durationMs),
                PointerInput.Origin.viewport(),
                endX, endY
        ));

        // 4. 터치 업 (떼기)
        dragSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        // 액션 실행
        driver.perform(List.of(dragSequence));

        System.out.println("[TouchAction] 드래그 완료 ✓");
    }
    public static void dragCheekAdaptive(AndroidDriver driver) {
        System.out.println("\n=== 캐릭터 볼 당기기 (해상도 독립적) ===");

        // [Step 1] 화면 정보 수집
        Dimension size = ScreenHelper.getScreenSize(driver);
        ScreenOrientation orientation = ScreenHelper.getOrientation(driver);

        int screenWidth = size.getWidth();
        int screenHeight = size.getHeight();

        System.out.println("화면 해상도: " + screenWidth + " x " + screenHeight);
        System.out.println("화면 방향: " + orientation);

        // [Step 2] 중앙 좌표 계산
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        // [Step 3] 드래그 거리 계산 (화면 너비의 20% - 안전한 범위)
        int dragDistance = (int)(screenWidth * 0.2);

        // [Step 4] 시작/종료 좌표 설정
        int startX = centerX;
        int startY = centerY;
        int endX = centerX - dragDistance;  // 좌측으로 드래그
        int endY = centerY;                 // Y축 고정 (수평 드래그)

        System.out.println("계산된 좌표:");
        System.out.println("  중앙: (" + centerX + ", " + centerY + ")");
        System.out.println("  시작: (" + startX + ", " + startY + ")");
        System.out.println("  종료: (" + endX + ", " + endY + ")");
        System.out.println("  드래그 거리: " + dragDistance + "px (" +
                String.format("%.0f", (dragDistance * 100.0 / screenWidth)) + "%)");

        // [Step 5] 실제 드래그 수행
        dragAndDrop(driver, startX, startY, endX, endY, AppiumConfig.CHEEK_DRAG_DURATION_MS);

        System.out.println("=== 캐릭터 볼 당기기 완료 ===\n");
    }

    /**
     * 기존 하드코딩 버전 (하위 호환성 유지)
     * @deprecated 해상도 독립적 버전인 dragCheekAdaptive() 사용 권장
     */
    @Deprecated
    public static void dragCheek(AndroidDriver driver) {
        System.out.println("[경고] 하드코딩된 좌표 사용 중 (특정 해상도에서만 동작)");
        dragAndDrop(
                driver,
                AppiumConfig.CHEEK_DRAG_START_X,
                AppiumConfig.CHEEK_DRAG_START_Y,
                AppiumConfig.CHEEK_DRAG_END_X,
                AppiumConfig.CHEEK_DRAG_END_Y,
                AppiumConfig.CHEEK_DRAG_DURATION_MS
        );
    }
}