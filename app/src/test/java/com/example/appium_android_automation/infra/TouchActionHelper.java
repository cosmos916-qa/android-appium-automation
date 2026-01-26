package com.example.appium_android_automation.infra;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.Point;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.List;

/**
 * 스마트폰 화면 터치 및 드래그 동작 자동화 도구
 *
 * 이 클래스는 사람의 손가락 동작을 정밀하게 재현하는 "로봇 손가락"입니다.
 *
 * 🎯 핵심 역할:
 * - 화면의 정확한 위치를 터치하거나 드래그
 * - 모든 해상도에서 동일한 비율로 동작하도록 자동 좌표 계산
 * - Unity SurfaceView 환경에서 좌표 기반 상호작용 제공
 *
 * 📱 Unity 앱의 특수성:
 * - 일반 앱: 버튼 ID로 직접 접근 가능 → driver.findElement("확인버튼").click()
 * - Unity 앱: 모든 것이 하나의 캔버스 → 정확한 좌표로만 터치 가능
 * - 따라서 정확한 좌표 계산이 테스트 성공의 핵심입니다!
 *
 * 🔧 해상도 독립성 보장:
 * - "왼쪽에서 500픽셀" (❌ 기기마다 다름)
 * - "화면 중앙에서 좌측으로 20%" (✅ 모든 기기 동일)
 *
 * ⚙️ W3C Actions API 사용:
 * Appium 최신 권장 방식으로 구현되어 안정성과 호환성이 뛰어납니다.
 * 구버전 TouchAction 클래스보다 정확하고 자연스러운 터치 재현이 가능합니다.
 *
 * 💡 실전 활용 시나리오:
 * - 메인 화면 진입을 위한 특정 동작 수행
 * - 팝업의 "확인" 버튼 터치 (이미지 매칭 결과 좌표 활용)
 * - 스와이프 제스처로 페이지 넘기기
 * - 아이템 드래그 앤 드롭
 */
public class TouchActionHelper {

    /**
     * 화면의 한 지점에서 다른 지점으로 손가락을 끌어당기는 드래그 동작 수행
     *
     * 이 메서드는 모든 드래그 동작의 기반이 되는 "핵심 엔진"입니다.
     * 사람이 손가락으로 화면을 터치한 채 이동하는 동작을 정밀하게 재현합니다.
     *
     * 🎬 동작 과정 (4단계):
     * 1. 시작 위치로 손가락 이동 (아직 터치 안함)
     * 2. 화면 터치 (손가락 누르기)
     * 3. 끝 위치까지 지정된 시간 동안 자연스럽게 이동
     * 4. 손가락 떼기 (터치 해제)
     *
     * 💡 실전 활용 예시:
     *
     * **예시 1: 메인 화면 진입을 위한 특정 동작**
     * ```java
     * // 화면 중앙에서 좌측으로 끌어당기기 (화면 너비의 20% 거리)
     * int centerX = ScreenHelper.getCenterX(driver);
     * int centerY = ScreenHelper.getCenterY(driver);
     * int dragDistance = (int)(ScreenHelper.getScreenSize(driver).getWidth() * 0.2);
     *
     * dragAndDrop(driver, centerX, centerY, centerX - dragDistance, centerY, 1000);
     * ```
     *
     * **예시 2: 화면 스크롤 (아래에서 위로)**
     * ```java
     * int startY = (int)(screenHeight * 0.8);  // 하단 80% 지점
     * int endY = (int)(screenHeight * 0.2);    // 상단 20% 지점
     * int middleX = screenWidth / 2;
     *
     * dragAndDrop(driver, middleX, startY, middleX, endY, 500);  // 0.5초 스크롤
     * ```
     *
     * ⚙️ 지속시간(durationMs) 설정 가이드:
     * - 100~300ms: 빠른 스와이프 (페이지 넘기기, 빠른 제스처)
     * - 500~1000ms: 자연스러운 드래그 (아이템 이동, 슬라이더 조작)
     * - 1000ms 이상: 느린 드래그 (정밀한 조작 필요 시)
     *
     * ⚠️ 주의사항:
     * - 너무 빠른 드래그(50ms 이하)는 앱이 터치로 인식하지 못할 수 있음
     * - 너무 느린 드래그(3000ms 이상)는 부자연스럽고 테스트 시간 증가
     * - 화면 경계 밖의 좌표는 오류 발생 가능 (안전 여백 확보 권장)
     *
     * @param driver 테스트용 스마트폰 제어 도구
     * @param startX 드래그 시작 X 좌표 (픽셀)
     * @param startY 드래그 시작 Y 좌표 (픽셀)
     * @param endX 드래그 끝 X 좌표 (픽셀)
     * @param endY 드래그 끝 Y 좌표 (픽셀)
     * @param durationMs 드래그 동작이 완료되는 데 걸리는 시간 (밀리초)
     */
    public static void dragAndDrop(AndroidDriver driver,
                                   int startX, int startY,
                                   int endX, int endY,
                                   int durationMs) {

        // [LOG] 드래그 동작 시작 정보 기록 (디버깅 시 유용)
        System.out.println("[TouchAction] 드래그 시작: (" + startX + "," + startY + ") → " +
                "종료: (" + endX + "," + endY + "), 지속시간: " + durationMs + "ms");

        // [SETUP] W3C Actions API 기반 터치 포인터 생성
        // "finger"라는 이름의 가상 손가락을 만듭니다
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");

        // 이 손가락이 수행할 동작들을 순서대로 담을 시퀀스 생성
        Sequence dragSequence = new Sequence(finger, 1);

        // [ACTION 1] 시작 지점으로 손가락 이동 (아직 화면에 닿지 않음)
        // Duration.ZERO = 즉시 이동 (순간이동처럼)
        // viewport() = 화면 좌상단(0,0)을 기준으로 한 좌표계 사용
        dragSequence.addAction(finger.createPointerMove(
                Duration.ZERO,
                PointerInput.Origin.viewport(),
                startX, startY
        ));

        // [ACTION 2] 화면 터치 시작 (손가락 누르기)
        // 이 시점부터 앱은 "사용자가 터치했다"고 인식합니다
        dragSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));

        // [ACTION 3] 끝 지점까지 자연스럽게 이동 (터치 유지한 채)
        // durationMs 시간 동안 부드럽게 이동 (사람 손가락의 자연스러운 속도 재현)
        // 이 과정에서 앱은 "드래그 중"이라고 인식합니다
        dragSequence.addAction(finger.createPointerMove(
                Duration.ofMillis(durationMs),
                PointerInput.Origin.viewport(),
                endX, endY
        ));

        // [ACTION 4] 손가락 떼기 (터치 해제)
        // 이 시점에서 앱은 "드래그가 완료되었다"고 인식하고 결과를 처리합니다
        dragSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        // [EXECUTE] 위에서 정의한 모든 동작을 실제로 실행
        // List.of()로 감싸는 이유: 여러 손가락 동작을 동시에 수행할 수 있도록 설계됨
        driver.perform(List.of(dragSequence));

        // [LOG] 드래그 완료 표시
        System.out.println("[TouchAction] 드래그 완료 ✓");
    }

    /**
     * 메인 화면 진입을 위한 특정 동작 수행 (해상도 자동 계산)
     *
     * 모든 해상도의 스마트폰에서 동일한 비율로 동작하도록 자동 계산합니다.
     * 갤럭시, 아이폰, 태블릿 모두에서 정확히 같은 위치를 터치합니다.
     *
     * 🎯 동작 방식:
     * 1. 현재 연결된 스마트폰의 화면 크기 측정
     * 2. 화면 중앙 좌표 자동 계산
     * 3. 화면 너비의 20%만큼 좌측으로 드래그
     * 4. 수평 방향으로만 이동 (Y축 고정)
     *
     * 📐 좌표 계산 공식:
     * - 시작점: (화면 너비 ÷ 2, 화면 높이 ÷ 2) = 정중앙
     * - 끝점: (시작점 - 화면 너비 × 0.2, 시작점과 같은 Y) = 좌측으로 20% 이동
     * - 드래그 시간: AppiumConfig에서 설정된 값 사용 (기본 1000ms = 1초)
     *
     * 💡 왜 20%인가요?
     * - 너무 짧으면: 앱이 드래그로 인식하지 못하고 단순 터치로 오해
     * - 너무 길면: 화면 밖으로 나가거나 의도하지 않은 동작 발생
     * - 20%는 대부분의 앱에서 안정적으로 작동하는 최적 거리
     *
     * 🔍 실행 중 출력되는 정보:
     * ```
     * === 메인 화면 진입 동작 (해상도 독립적) ===
     * 화면 해상도: 1080 x 2340
     * 화면 방향: PORTRAIT
     * 계산된 좌표:
     *   중앙: (540, 1170)
     *   시작: (540, 1170)
     *   종료: (324, 1170)
     *   드래그 거리: 216px (20%)
     * [TouchAction] 드래그 시작: (540,1170) → 종료: (324,1170), 지속시간: 1000ms
     * [TouchAction] 드래그 완료 ✓
     * === 메인 화면 진입 동작 완료 ===
     * ```
     *
     * 🛠️ QA 테스터 활용 가이드:
     * - 새로운 기기에서 첫 테스트 시 출력된 좌표값 확인
     * - 동작이 실패하면 로그의 좌표가 예상 범위 내인지 검증
     * - 다른 해상도 기기와 좌표 비율 비교 (항상 20%여야 함)
     *
     * @param driver 테스트용 스마트폰 제어 도구
     */
    public static void dragCheekAdaptive(AndroidDriver driver) {
        System.out.println("\n=== 메인 화면 진입 동작 (해상도 독립적) ===");

        // [STEP 1] 현재 스마트폰의 화면 정보 수집
        Dimension size = ScreenHelper.getScreenSize(driver);
        ScreenOrientation orientation = ScreenHelper.getOrientation(driver);

        int screenWidth = size.getWidth();
        int screenHeight = size.getHeight();

        System.out.println("화면 해상도: " + screenWidth + " x " + screenHeight);
        System.out.println("화면 방향: " + orientation);

        // [STEP 2] 화면 중앙 좌표 자동 계산
        // 어떤 해상도든 정확히 가운데를 찾습니다
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        // [STEP 3] 드래그 거리 계산 (화면 너비의 20%)
        // 0.2를 곱하면 전체 너비의 20%가 됩니다
        // 예: 1080픽셀 화면 → 216픽셀 이동
        int dragDistance = (int)(screenWidth * 0.2);

        // [STEP 4] 시작점과 끝점 좌표 설정
        int startX = centerX;                    // 중앙에서 시작
        int startY = centerY;                    // 중앙 높이 유지
        int endX = centerX - dragDistance;       // 좌측으로 20% 이동
        int endY = centerY;                      // Y축 고정 (수평 드래그)

        // [LOG] 계산된 모든 좌표값 출력 (디버깅 및 검증용)
        System.out.println("계산된 좌표:");
        System.out.println("  중앙: (" + centerX + ", " + centerY + ")");
        System.out.println("  시작: (" + startX + ", " + startY + ")");
        System.out.println("  종료: (" + endX + ", " + endY + ")");
        System.out.println("  드래그 거리: " + dragDistance + "px (" +
                String.format("%.0f", (dragDistance * 100.0 / screenWidth)) + "%)");

        // [EXECUTE] 계산된 좌표로 실제 드래그 동작 수행
        dragAndDrop(driver, startX, startY, endX, endY, AppiumConfig.CHEEK_DRAG_DURATION_MS);

        System.out.println("=== 메인 화면 진입 동작 완료 ===\n");
    }

    /**
     * 메인 화면 진입 동작 (우하단 오프셋 버전) - 작은 요소 대응
     *
     * 기본 버전(dragCheekAdaptive)이 정중앙을 터치하는 반면,
     * 이 버전은 중앙에서 약간 우하단으로 치우친 위치를 터치합니다.
     *
     * 🎯 언제 사용하나요?
     * - 화면 중앙의 요소가 작아서 정확히 중앙만 터치하면 벗어나는 경우
     * - 요소가 중앙에서 약간 아래/오른쪽에 치우쳐 있는 경우
     * - 기본 동작(dragCheekAdaptive)이 실패했을 때의 대안
     *
     * 📐 좌표 계산 방식:
     * - 기본 중앙: (화면 너비 ÷ 2, 화면 높이 ÷ 2)
     * - 오프셋 적용: (중앙 + 너비×10%, 중앙 + 높이×10%)
     * - 결과: 중앙에서 우측으로 10%, 하단으로 10% 이동한 지점
     *
     * 🛡️ 안전장치:
     * 계산된 좌표가 화면 밖으로 나가지 않도록 자동 보정합니다.
     * - 최소값: 화면 가장자리에서 50픽셀 안쪽
     * - 최대값: 화면 반대쪽 가장자리에서 50픽셀 안쪽
     * - 이렇게 하면 어떤 해상도에서도 안전하게 터치 가능
     *
     * 💡 QA 테스터를 위한 사용 순서:
     * 1. 먼저 dragCheekAdaptive() 시도
     * 2. 실패 시 dragCheekWithOffset() 시도
     * 3. 둘 다 실패 시 화면 캡처 후 개발자에게 문의
     *
     * @param driver 테스트용 스마트폰 제어 도구
     */
    public static void dragCheekWithOffset(AndroidDriver driver) {
        System.out.println("\n=== 메인 화면 진입 동작 (우하단 오프셋 버전) ===");

        // [STEP 1] 화면 정보 수집
        Dimension size = ScreenHelper.getScreenSize(driver);
        ScreenOrientation orientation = ScreenHelper.getOrientation(driver);

        int screenWidth = size.getWidth();
        int screenHeight = size.getHeight();

        System.out.println("화면 해상도: " + screenWidth + " x " + screenHeight);
        System.out.println("화면 방향: " + orientation);

        // [STEP 2] 기본 중앙 좌표 계산
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        // [STEP 3] 우하단 10% 오프셋 계산 및 적용
        // 화면이 클수록 오프셋도 비례해서 커집니다 (해상도 독립적)
        int offsetX = (int)(screenWidth * 0.1);   // 너비의 10%
        int offsetY = (int)(screenHeight * 0.1);  // 높이의 10%

        int startX = centerX + offsetX;  // 중앙에서 우측으로 10%
        int startY = centerY + offsetY;  // 중앙에서 하단으로 10%

        // [STEP 4] 드래그 거리 계산 (화면 너비의 20%)
        int dragDistance = (int)(screenWidth * 0.2);

        int endX = startX - dragDistance;  // 시작점에서 좌측으로 드래그
        int endY = startY;                 // Y축 고정 (수평 드래그)

        // [STEP 5] 안전장치: 화면 경계 확인 및 자동 보정
        // 계산된 좌표가 화면 밖이면 안전한 범위로 자동 조정
        int margin = 50;  // 화면 가장자리에서 50픽셀 여백 확보

        // Math.max(최소값, Math.min(최대값, 실제값)) = 범위 제한 공식
        startX = Math.max(margin, Math.min(screenWidth - margin, startX));
        startY = Math.max(margin, Math.min(screenHeight - margin, startY));
        endX = Math.max(margin, Math.min(screenWidth - margin, endX));
        endY = Math.max(margin, Math.min(screenHeight - margin, endY));

        // [LOG] 모든 계산 과정 상세 출력
        System.out.println("좌표 계산 결과:");
        System.out.println("  화면 중앙: (" + centerX + ", " + centerY + ")");
        System.out.println("  오프셋 적용: 우+" + offsetX + "px, 하+" + offsetY + "px");
        System.out.println("  시작 좌표: (" + startX + ", " + startY + ") [중앙 우하단 10%]");
        System.out.println("  종료 좌표: (" + endX + ", " + endY + ")");
        System.out.println("  드래그 거리: " + dragDistance + "px (" +
                String.format("%.0f", (dragDistance * 100.0 / screenWidth)) + "% 좌측)");

        // [EXECUTE] 보정된 좌표로 실제 드래그 수행
        dragAndDrop(driver, startX, startY, endX, endY, AppiumConfig.CHEEK_DRAG_DURATION_MS);

        System.out.println("=== 메인 화면 진입 동작 완료 ===\n");
    }

    /**
     * 레거시 버전: 고정 좌표 기반 드래그 (특정 해상도 전용)
     *
     * ⚠️ 사용 중단 권장 (Deprecated)
     *
     * 이 메서드는 과거 방식으로, 특정 해상도(2440x1080)에서만 작동합니다.
     * 다른 해상도의 기기에서는 엉뚱한 위치를 터치하게 됩니다.
     *
     * 🚫 문제점:
     * - 갤럭시 S24: 화면이 커서 의도한 위치보다 왼쪽 위를 터치
     * - 저가형 폰: 화면이 작아서 의도한 위치보다 오른쪽 아래를 터치
     * - 태블릿: 완전히 엉뚱한 곳을 터치할 가능성 높음
     *
     * ✅ 대신 사용하세요:
     * - dragCheekAdaptive(): 모든 해상도 자동 대응 (권장)
     * - dragCheekWithOffset(): 미세 조정이 필요한 경우
     *
     * 💡 왜 삭제하지 않고 남겨두나요?
     * - 기존 테스트 코드와의 호환성 유지
     * - 특정 기기 전용 긴급 테스트 시 참고용
     * - 개발 히스토리 및 개선 과정 기록
     *
     * @param driver 테스트용 스마트폰 제어 도구
     * @deprecated 해상도 독립적 버전인 dragCheekAdaptive() 사용 권장
     */
    @Deprecated
    public static void dragCheek(AndroidDriver driver) {
        // [WARNING] 하드코딩된 좌표 사용 경고 출력
        System.out.println("[경고] 하드코딩된 좌표 사용 중 (특정 해상도에서만 동작)");

        // AppiumConfig에 저장된 고정 좌표값 사용
        dragAndDrop(
                driver,
                AppiumConfig.CHEEK_DRAG_START_X,   // 1560 (고정값)
                AppiumConfig.CHEEK_DRAG_START_Y,   // 720 (고정값)
                AppiumConfig.CHEEK_DRAG_END_X,     // 936 (고정값)
                AppiumConfig.CHEEK_DRAG_END_Y,     // 720 (고정값)
                AppiumConfig.CHEEK_DRAG_DURATION_MS // 1000ms
        );
    }

    /**
     * 화면의 특정 좌표를 짧게 터치 (단순 탭)
     *
     * 사람이 손가락으로 화면을 "톡" 건드리는 동작을 재현합니다.
     * 버튼 클릭, 메뉴 선택 등 가장 기본적인 터치 동작입니다.
     *
     * 🎯 드래그와의 차이점:
     * - 드래그: 터치 → 이동 → 떼기 (3단계)
     * - 탭: 터치 → 짧은 대기 → 떼기 (이동 없음)
     *
     * 💡 실전 활용 예시:
     *
     * **예시 1: 화면 중앙의 "시작" 버튼 터치**
     * ```java
     * int centerX = ScreenHelper.getCenterX(driver);
     * int centerY = ScreenHelper.getCenterY(driver);
     * tap(driver, centerX, centerY);
     * ```
     *
     * **예시 2: 이미지 매칭으로 찾은 버튼 터치**
     * ```java
     * Point buttonLocation = Evidence.findImageLocation(driver, "확인버튼.png");
     * tap(driver, buttonLocation);  // Point 객체 직접 사용
     * ```
     *
     * ⏱️ 왜 100ms 대기하나요?
     * - 0ms: 너무 빨라서 앱이 터치로 인식 못할 수 있음
     * - 100ms: 사람의 자연스러운 터치 시간 (0.1초)
     * - 500ms 이상: 불필요하게 느리고 "길게 누르기"로 오해될 수 있음
     *
     * @param driver 테스트용 스마트폰 제어 도구
     * @param x 터치할 X 좌표 (픽셀)
     * @param y 터치할 Y 좌표 (픽셀)
     */
    public static void tap(AndroidDriver driver, int x, int y) {
        // [LOG] 터치 동작 시작 정보 기록
        System.out.println("[TouchAction] 터치 실행: (" + x + ", " + y + ")");

        // [SETUP] W3C Actions API 기반 터치 포인터 생성
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tapSequence = new Sequence(finger, 1);

        // [ACTION 1] 터치 위치로 손가락 순간이동
        tapSequence.addAction(finger.createPointerMove(
                Duration.ZERO,  // 즉시 이동
                PointerInput.Origin.viewport(),
                x, y
        ));

        // [ACTION 2] 화면 터치 (손가락 누르기)
        tapSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));

        // [ACTION 3] 짧은 대기 (자연스러운 터치 재현)
        // 사람이 손가락을 화면에 대고 있는 시간 = 약 0.1초
        tapSequence.addAction(new org.openqa.selenium.interactions.Pause(finger, Duration.ofMillis(100)));

        // [ACTION 4] 손가락 떼기 (터치 해제)
        tapSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        // [EXECUTE] 정의된 모든 동작 실행
        driver.perform(List.of(tapSequence));

        // [LOG] 터치 완료 표시
        System.out.println("[TouchAction] 터치 완료 ✓");
    }

    /**
     * Point 객체를 사용한 터치 (좌표 래핑 버전)
     *
     * 이미지 매칭이나 다른 클래스에서 Point 객체로 좌표를 전달받았을 때
     * 별도 변환 없이 바로 터치할 수 있도록 하는 편의 메서드입니다.
     *
     * 💡 활용 예시:
     * ```java
     * // Evidence 클래스가 이미지 매칭 결과를 Point로 반환
     * Point exitButtonLocation = Evidence.findImageLocation(driver, "종료버튼.png");
     *
     * // Point 객체를 직접 전달하여 터치 (x, y 따로 추출할 필요 없음)
     * tap(driver, exitButtonLocation);
     * ```
     *
     * 🔧 내부 동작:
     * Point 객체에서 x, y 좌표를 추출하여 기본 tap() 메서드 호출
     *
     * @param driver 테스트용 스마트폰 제어 도구
     * @param point 터치할 좌표를 담은 Point 객체
     */
    public static void tap(AndroidDriver driver, Point point) {
        // Point 객체에서 x, y 좌표 추출 후 기본 tap() 메서드로 위임
        tap(driver, point.getX(), point.getY());
    }
}
