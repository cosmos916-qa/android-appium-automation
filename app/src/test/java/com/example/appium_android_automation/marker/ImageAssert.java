package com.example.appium_android_automation.marker;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.InputStream;
import java.time.Duration;
import java.util.Base64;

/**
 * Unity 앱 화면 검증 전문가 (이미지 매칭 탐정 역할)
 *
 * 이 클래스는 Unity SurfaceView 환경에서 "화면에 특정 요소가 있는가?"를 판단하는 전문 탐정입니다.
 * 일반 앱처럼 버튼 ID나 텍스트로 찾을 수 없는 환경에서,
 * 미리 준비한 "정답 이미지"와 실제 화면을 비교하여 일치 여부를 판단합니다.
 *
 * 🔍 Unity 앱의 특수한 상황:
 *
 * **일반 앱 (네이티브 Android):**
 * ```java
 * // 버튼을 ID로 직접 찾기 가능
 * driver.findElement(By.id("login_button")).click();
 * ```
 *
 * **Unity 앱 (SurfaceView):**
 * ```java
 * // ❌ 불가능: 모든 것이 하나의 캔버스로 그려짐
 * driver.findElement(By.id("login_button")); // 찾을 수 없음!
 *
 * // ✅ 유일한 방법: 이미지 매칭
 * boolean found = ImageAssert.waitUntilImageVisible(driver, "login_button.png", 30);
 * ```
 *
 * 🎯 이미지 매칭 원리:
 * 1. **준비 단계**: 테스트하려는 화면 요소를 미리 캡처하여 "정답 이미지" 저장
 * 2. **실행 단계**: 테스트 중 실제 스마트폰 화면 전체를 캡처
 * 3. **비교 단계**: OpenCV 라이브러리가 전체 화면에서 정답 이미지와 일치하는 부분 탐색
 * 4. **결과 반환**: 발견되면 true, 못 찾으면 false
 *
 * 💡 실전 비유:
 * - 정답 이미지 = 수배자의 몽타주
 * - 실제 화면 = CCTV 영상
 * - OpenCV = 몽타주와 영상을 비교하는 얼굴 인식 시스템
 * - 매칭 성공 = "수배자 발견!"
 *
 * ⚠️ 이미지 매칭의 한계와 주의사항:
 *
 * **1. 해상도 의존성**
 * - 정답 이미지를 1080p 기기에서 캡처했다면 720p 기기에서는 크기가 달라 매칭 실패 가능
 *
 * **2. 동적 콘텐츠 문제**
 * - 시계, 점수, 캐릭터 애니메이션 등 계속 변하는 요소는 정답 이미지와 완전히 일치하기 어려움
 *
 * **3. 조명/색상 변화**
 * - 앱 테마 변경, 다크 모드 등으로 색상이 달라지면 매칭 실패
 *
 */
public class ImageAssert {

    /**
     * 화면에 특정 이미지가 나타날 때까지 기다린 후 존재 여부 반환
     *
     * Unity 앱의 불규칙한 로딩 시간을 고려하여 타임아웃 기반으로 반복 검증합니다.
     *
     * 🎯 동작 방식:
     * 1. 지정된 시간(timeoutSec) 동안 계속 화면을 확인
     * 2. 매 순간 "정답 이미지가 화면에 있는가?" 체크
     * 3. 발견되면 즉시 true 반환 (더 이상 기다리지 않음)
     * 4. 타임아웃까지 못 찾으면 false 반환
     *
     * 💡 왜 "기다림"이 필요한가요?
     *
     * **Unity 앱의 로딩 특성:**
     * - 로고 화면 → 로딩 → 메인 메뉴 (총 5~30초 소요)
     * - 각 단계마다 걸리는 시간이 기기 성능에 따라 다름
     * - 네트워크 상태에 따라 추가 지연 발생
     *
     * 📐 타임아웃 설정 가이드:
     *
     * **짧은 타임아웃 (5~10초):**
     * - 빠른 화면 전환 (팝업, 토스트 메시지)
     * - 이미 로딩이 완료된 상태에서의 UI 변화
     *
     * **중간 타임아웃 (15~20초):**
     * - 일반적인 화면 전환 (메뉴 → 서브 메뉴)
     * - 간단한 데이터 로딩
     *
     * **긴 타임아웃 (30초 이상):**
     * - 앱 첫 실행 (초기 리소스 다운로드)
     * - 무거운 화면 로딩 (3D 렌더링, 대용량 데이터)
     *
     * 💡 실전 활용 예시:
     * ```java
     * // 앱 시작 후 메인 로고 검증
     * StartAppFlow.run(driver);
     * boolean logoVisible = ImageAssert.waitUntilImageVisible(
     *     driver,
     *     "images/main_logo.png",
     *     30  // 앱 첫 로딩이므로 충분한 시간 제공
     * );
     * recordResult(1, "MainLogo", logoVisible);
     * ```
     *
     * @param driver 테스트용 스마트폰 제어 도구
     * @param resourcePath 정답 이미지 파일 경로 (resources 폴더 기준)
     * @param timeoutSec 최대 대기 시간 (초 단위)
     * @return 타임아웃 내에 이미지 발견 시 true, 못 찾으면 false
     */
    public static boolean waitUntilImageVisible(AndroidDriver driver, String resourcePath, int timeoutSec) {
        // [LOG] 이미지 매칭 시작 알림
        System.out.println("[IMG] 이미지 매칭 시작: " + resourcePath + " (타임아웃=" + timeoutSec + "초)");

        try {
            // [STEP 1] 정답 이미지를 Base64 문자열로 인코딩
            String b64 = loadResourceAsBase64(resourcePath);

            // [STEP 2] WebDriverWait 설정 (폴링 기반 대기)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));

            // [STEP 3] 이미지가 나타날 때까지 반복 체크
            WebElement el = wait.until(d -> {
                try {
                    // AppiumBy.image(b64) = OpenCV 기반 이미지 매칭 시도
                    return d.findElement(AppiumBy.image(b64));
                } catch (NoSuchElementException ex) {
                    // 못 찾으면 null 반환 → 계속 대기
                    return null;
                }
            });

            // [STEP 4] 결과 판정 및 로그 출력
            boolean ok = (el != null);
            System.out.println("[IMG] 매칭 결과: " + (ok ? "성공 ✓" : "실패 ✗"));
            return ok;

        } catch (TimeoutException te) {
            // [TIMEOUT] 지정된 시간 내에 이미지를 찾지 못함
            System.out.println("[IMG] TIMEOUT - 이미지를 찾지 못함");
            return false;
        } catch (Exception e) {
            // [ERROR] 예상치 못한 오류 발생
            System.err.println("[IMG] ERROR: " + e.getMessage());
            return false;
        }
    }

    /**
     * 이미지를 찾고 중앙 좌표를 반환 (터치 동작용)
     *
     * waitUntilImageVisible()과 유사하지만, 단순 존재 여부가 아닌
     * "어디에 있는가?"까지 알려줍니다.
     *
     * 🎯 활용 목적:
     * - 이미지 매칭으로 버튼 위치 찾기
     * - 찾은 위치를 TouchActionHelper로 터치
     * - Unity 앱에서 버튼 클릭의 유일한 방법
     *
     * 💡 실전 활용 예시:
     * ```java
     * // 1단계: 종료 버튼 이미지 찾기
     * Point exitButtonCenter = ImageAssert.findImageCenter(
     *     driver,
     *     "images/exit_button.png",
     *     10
     * );
     *
     * if (exitButtonCenter != null) {
     *     // 2단계: 찾은 위치 터치
     *     TouchActionHelper.tap(driver, exitButtonCenter);
     *     System.out.println("종료 버튼 터치 완료");
     * } else {
     *     System.out.println("종료 버튼을 찾을 수 없음");
     * }
     * ```
     *
     * 📐 좌표 계산 방식:
     * - centerX = location.x + (size.width / 2)
     * - centerY = location.y + (size.height / 2)
     *
     * 🔍 출력 정보 해석:
     * ```
     * [IMG] 이미지 좌표 탐색 시작: images/exit_button.png (타임아웃=10초)
     * [IMG] 이미지 발견 ✓
     *   위치: (856, 1920)      ← 좌상단 모서리 좌표
     *   크기: 208 x 120        ← 이미지 가로×세로 픽셀
     *   중앙 좌표: (960, 1980) ← 터치할 정확한 위치
     * ```
     *
     * ⚠️ 주의사항:
     * - 이미지를 못 찾으면 null 반환
     * - null 체크 없이 터치하면 NullPointerException 발생
     * - 항상 if (point != null) 조건 확인 후 사용
     *
     * @param driver 테스트용 스마트폰 제어 도구
     * @param resourcePath 정답 이미지 파일 경로
     * @param timeoutSec 최대 대기 시간 (초 단위)
     * @return 이미지의 중앙 좌표 (Point 객체), 못 찾으면 null
     */
    public static Point findImageCenter(AndroidDriver driver, String resourcePath, int timeoutSec) {
        // [LOG] 좌표 탐색 시작 알림
        System.out.println("[IMG] 이미지 좌표 탐색 시작: " + resourcePath + " (타임아웃=" + timeoutSec + "초)");

        try {
            // [STEP 1] 정답 이미지 Base64 인코딩
            String b64 = loadResourceAsBase64(resourcePath);

            // [STEP 2] WebDriverWait 설정 및 이미지 탐색
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));
            WebElement element = wait.until(d -> {
                try {
                    return d.findElement(AppiumBy.image(b64));
                } catch (NoSuchElementException ex) {
                    return null;
                }
            });

            if (element != null) {
                // [STEP 3] 이미지 요소의 위치와 크기 정보 가져오기
                Point location = element.getLocation();  // 좌상단 좌표
                Dimension size = element.getSize();      // 가로×세로 크기

                // [STEP 4] 중앙 좌표 계산
                int centerX = location.getX() + (size.getWidth() / 2);
                int centerY = location.getY() + (size.getHeight() / 2);

                // [LOG] 상세 정보 출력 (디버깅 및 검증용)
                System.out.println("[IMG] 이미지 발견 ✓");
                System.out.println("  위치: (" + location.getX() + ", " + location.getY() + ")");
                System.out.println("  크기: " + size.getWidth() + " x " + size.getHeight());
                System.out.println("  중앙 좌표: (" + centerX + ", " + centerY + ")");

                // [RETURN] Point 객체로 중앙 좌표 반환
                return new Point(centerX, centerY);
            } else {
                System.out.println("[IMG] 이미지 미발견 ✗");
                return null;
            }

        } catch (TimeoutException te) {
            System.out.println("[IMG] TIMEOUT - 이미지 좌표 탐색 실패");
            return null;
        } catch (Exception e) {
            System.err.println("[IMG] ERROR: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 리소스 폴더의 이미지 파일을 Base64 문자열로 인코딩 (내부 전용)
     *
     * Appium Image Plugin이 이미지를 Base64 형식으로 요구하기 때문에 필요한 변환 작업입니다.
     *
     * 🔧 Base64란?
     * - 이진 데이터(이미지, 파일)를 텍스트 형식으로 변환하는 인코딩 방식
     * - 네트워크로 전송하거나 JSON에 포함시키기 위해 사용
     *
     * @param resourcePath 리소스 파일 경로 (예: "images/main_logo.png")
     * @return Base64로 인코딩된 이미지 문자열
     * @throws Exception 파일이 없거나 읽기 실패 시
     */
    private static String loadResourceAsBase64(String resourcePath) throws Exception {
        // [STEP 1] 클래스패스에서 리소스 파일 스트림 가져오기
        InputStream in = ImageAssert.class.getClassLoader().getResourceAsStream(resourcePath);

        // [STEP 2] 파일 존재 여부 확인
        if (in == null) {
            throw new IllegalStateException("이미지 리소스 없음: " + resourcePath);
        }

        // [STEP 3] 파일 내용을 바이트 배열로 읽기
        byte[] bytes = in.readAllBytes();

        // [STEP 4] Base64 인코딩하여 문자열 반환
        return Base64.getEncoder().encodeToString(bytes);
    }
}
