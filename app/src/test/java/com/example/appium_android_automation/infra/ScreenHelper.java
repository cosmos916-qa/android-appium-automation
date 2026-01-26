package com.example.appium_android_automation.infra;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ScreenOrientation;

/**
 * 스마트폰 화면 측정 및 좌표 계산 도구 (해상도 독립 처리)
 *
 * 이 클래스는 다양한 스마트폰의 화면 크기 차이를 해결하는 "스마트 자(ruler)"입니다.
 *
 * 📱 왜 필요한가요?
 * 갤럭시 S24 Ultra, 아이폰 15, 저가형 안드로이드... 모두 화면 크기가 다릅니다.
 * 하지만 "화면 중앙을 터치하세요"라는 명령은 모든 기기에서 동일하게 작동해야 합니다.
 *
 * 🚨 Unity SurfaceView의 도전:
 * - 일반 앱: 버튼 ID로 직접 접근 가능 → findViewById("확인버튼")
 * - Unity 게임: 모든 것이 하나의 캔버스 → 좌표로만 접근 가능
 * - 따라서 정확한 좌표 계산이 테스트 성공의 핵심!
 *
 * 🔧 해결 방식:
 * - "왼쪽에서 500픽셀" (❌ 기기마다 다름)
 * - "화면 전체 너비의 50%" (✅ 어떤 기기든 동일)
 *
 * 💡 실전 활용 예시:
 * - TouchActionHelper가 터치 좌표 계산할 때 이 클래스 사용
 * - Evidence가 이미지 매칭 영역을 설정할 때 화면 크기 참조
 * - 게임 내 드래그/스와이프 동작의 시작점과 끝점 계산
 */
public class ScreenHelper {

    /**
     * 현재 연결된 스마트폰의 정확한 화면 크기 측정
     *
     * 이 값은 모든 해상도 독립 좌표 계산의 기준점입니다.
     *
     * 반환 정보:
     * - width: 화면 가로 픽셀 수 (예: 1080, 1440 등)
     * - height: 화면 세로 픽셀 수 (예: 2340, 3200 등)
     *
     * 활용 방법:
     * ```java
     * Dimension size = ScreenHelper.getScreenSize(driver);
     * int screenWidth = size.getWidth();   // 1080
     * int screenHeight = size.getHeight(); // 2340
     *
     * // 화면 우측 하단 20% 지점 계산 (설정 버튼 위치 등)
     * int x = (int)(screenWidth * 0.8);   // 전체 너비의 80% 지점
     * int y = (int)(screenHeight * 0.8);  // 전체 높이의 80% 지점
     * ```
     *
     * 💡 QA 테스터 디버깅 팁:
     * 터치가 엉뚱한 곳을 누를 때 이 값을 먼저 확인하세요.
     * 예상과 다른 해상도라면 기기 설정이나 에뮬레이터 설정을 점검하세요.
     *
     * @param driver 테스트용 스마트폰 제어 도구
     * @return 화면 크기 정보 (가로/세로 픽셀)
     */
    public static Dimension getScreenSize(AndroidDriver driver) {
        // [ACTION] 스마트폰에게 "지금 화면 크기가 얼마니?" 질문
        return driver.manage().window().getSize();
    }

    /**
     * 현재 화면이 세로모드인지 가로모드인지 확인
     *
     * 가로형 게임 특성:
     * - 기본적으로 세로(PORTRAIT) 고정 모드
     * - 기기를 회전해도 게임 화면은 회전하지 않음
     * - 하지만 로딩 중이나 특정 상황에서 예외 발생 가능
     *
     * 반환값:
     * - PORTRAIT: 세로 모드 (일반적인 스마트폰 사용 방향)
     * - LANDSCAPE: 가로 모드 (동영상 시청이나 일부 게임)
     *
     * 활용 예시:
     * ```java
     * if (getOrientation(driver) != ScreenOrientation.PORTRAIT) {
     *     System.out.println("경고: 게임이 세로 모드가 아닙니다!");
     *     // 필요시 화면 회전 또는 테스트 중단 처리
     * }
     * ```
     *
     * @param driver 테스트용 스마트폰 제어 도구
     * @return 현재 화면 방향 (PORTRAIT 또는 LANDSCAPE)
     */
    public static ScreenOrientation getOrientation(AndroidDriver driver) {
        // [ACTION] 스마트폰에게 "지금 세로야 가로야?" 질문
        return driver.getOrientation();
    }

    /**
     * 화면의 정중앙 X 좌표(가로 위치) 자동 계산
     *
     * 어떤 해상도든 정확히 화면 가로 중앙을 찾아줍니다.
     *
     * 계산 방식: 전체 가로 픽셀 ÷ 2 = 중앙점
     * - 1080 픽셀 화면 → 540이 중앙
     * - 1440 픽셀 화면 → 720이 중앙
     *
     * 언제 사용하나요?
     * - 화면 중앙에 뜬 팝업의 "확인" 버튼 터치
     * - 캐릭터 선택 화면에서 중앙 캐릭터 선택
     * - 게임 내 중앙 UI 요소 조작
     * - 드래그 동작의 시작점이나 끝점 설정
     *
     * ⚠️ 주의사항:
     * 이 값만으로는 터치할 수 없습니다. Y 좌표(getCenterY)도 함께 필요합니다.
     *
     * @param driver 테스트용 스마트폰 제어 도구
     * @return 화면 중앙의 X 좌표 (픽셀 단위)
     */
    public static int getCenterX(AndroidDriver driver) {
        // 화면 가로 길이를 2로 나누면 정중앙 X 좌표
        return getScreenSize(driver).getWidth() / 2;
    }

    /**
     * 화면의 정중앙 Y 좌표(세로 위치) 자동 계산
     *
     * 어떤 해상도든 정확히 화면 세로 중앙을 찾아줍니다.
     *
     * 스마트폰 좌표 체계 이해:
     * - Y = 0: 화면 맨 위 (상태바, 노치 영역)
     * - Y = 중간값: 화면 정중앙 (주요 게임 콘텐츠 영역)
     * - Y = 최대값: 화면 맨 아래 (네비게이션 바, 제스처 영역)
     *
     * 실전 활용:
     * ```java
     * int centerX = getCenterX(driver);  // 가로 중앙
     * int centerY = getCenterY(driver);  // 세로 중앙
     *
     * // (centerX, centerY)를 터치하면 정확히 화면 정중앙 터치
     * TouchActionHelper.tap(driver, centerX, centerY);
     * ```
     *
     * @param driver 테스트용 스마트폰 제어 도구
     * @return 화면 중앙의 Y 좌표 (픽셀 단위)
     */
    public static int getCenterY(AndroidDriver driver) {
        // 화면 세로 길이를 2로 나누면 정중앙 Y 좌표
        return getScreenSize(driver).getHeight() / 2;
    }

    /**
     * 현재 화면의 모든 정보를 콘솔에 정리해서 출력 (디버깅 전용)
     *
     * 테스트 실패나 예상과 다른 동작이 발생했을 때 가장 먼저 확인해야 할 정보들을
     * 한눈에 볼 수 있도록 정리해서 보여줍니다.
     *
     * 출력 내용:
     * ```
     * === 화면 정보 ===
     * 해상도: 1080 x 2340
     * 방향: PORTRAIT
     * 중앙 좌표: (540, 1170)
     * ================
     * ```
     *
     * 언제 사용하나요?
     * 1. **새로운 기기에서 첫 테스트 실행 시**
     *    → "이 기기의 화면 정보가 예상과 맞나?" 확인
     *
     * 2. **터치가 엉뚱한 곳을 누를 때**
     *    → "계산된 좌표가 올바른가?" 검증
     *
     * 3. **이미지 매칭이 실패할 때**
     *    → "해상도 차이로 인한 문제인가?" 판단
     *
     * 4. **화면 회전 관련 문제 발생 시**
     *    → "게임이 올바른 방향인가?" 확인
     *
     * 5. **여러 기기 테스트 결과 비교 시**
     *    → 갤럭시/아이폰/태블릿 각각의 환경 기록
     *
     * 💡 QA 테스터 활용 팁:
     * 테스트 시작 시 항상 이 메서드를 먼저 호출하여 환경을 확인하고,
     * 문제 발생 시 스크린샷과 함께 이 로그를 개발자에게 전달하면
     * 문제 해결이 훨씬 빨라집니다.
     *
     * @param driver 테스트용 스마트폰 제어 도구
     */
    public static void printScreenInfo(AndroidDriver driver) {
        // [COLLECT] 현재 화면의 모든 정보 수집
        Dimension size = getScreenSize(driver);           // 해상도 정보
        ScreenOrientation orientation = getOrientation(driver); // 방향 정보

        // [DISPLAY] 보기 좋게 정리하여 콘솔에 출력
        System.out.println("=== 화면 정보 ===");
        System.out.println("해상도: " + size.getWidth() + " x " + size.getHeight());
        System.out.println("방향: " + orientation);
        System.out.println("중앙 좌표: (" + getCenterX(driver) + ", " + getCenterY(driver) + ")");
        System.out.println("================");
    }
}
