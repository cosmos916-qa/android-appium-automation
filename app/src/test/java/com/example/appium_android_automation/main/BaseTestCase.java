package com.example.appium_android_automation.main;

import com.example.appium_android_automation.infra.DriverFactory;
import com.example.appium_android_automation.marker.Evidence;
import com.example.appium_android_automation.reporting.ChecklistReporter;
import com.example.appium_android_automation.reporting.GoogleSheetsClient;
import com.google.api.services.sheets.v4.Sheets;
import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Before;

/**
 * 모든 테스트의 공통 기반 클래스 (테스트 무대의 스태프 역할)
 *
 * 이 클래스는 테스트를 위한 "무대 준비와 정리"를 담당하는 전문 스태프입니다.
 * 배우(테스트 케이스)가 연기(테스트 시나리오)에만 집중할 수 있도록
 * 무대 설치와 철거를 자동으로 처리합니다.
 *
 * 🎭 무대 스태프 비유로 이해하기:
 * - @Before: 공연 시작 전 무대 설치 (조명, 소품, 음향 체크)
 * - 실제 테스트: 배우의 연기 (앱 조작 및 검증)
 * - @After: 공연 종료 후 무대 정리 (장비 철거, 청소)
 *
 * 🔧 자동으로 처리하는 작업들:
 * 1. **스마트폰 연결 및 앱 실행** → 무대 조명 켜기
 * 2. **구글 시트 연결 및 리포터 준비** → 채점표 준비
 * 3. **테스트 결과 자동 기록** (Pass/Fail/Block) → 점수 자동 기입
 * 4. **화면 캡처 및 증거 보관** → 공연 장면 사진 촬영
 * 5. **테스트 종료 후 리소스 정리** → 무대 정리 및 장비 반납
 *
 * 💡 왜 이 클래스가 필요한가요?
 *
 * **이 클래스 없이 테스트 작성 시:**
 * ```java
 * public class SmokeTestSuite {
 *     public void testAppStart() {
 *         // 매번 드라이버 생성 코드 반복
 *         driver = DriverFactory.createAndroidDriver();
 *         // 매번 구글 시트 연결 코드 반복
 *         Sheets sheets = GoogleSheetsClient.createSheetsService();
 *         // 테스트 실행...
 *         // 매번 정리 코드 반복
 *         driver.quit();
 *     }
 * }
 * ```
 *
 * **이 클래스 활용 시:**
 * ```java
 * public class SmokeTestSuite extends BaseTestCase {
 *     public void testAppStart() {
 *         // 준비는 자동으로 완료됨!
 *         boolean appStarted = StartAppFlow.run(driver);
 *         recordResult(1, "AppStart", appStarted);
 *         // 정리도 자동으로 완료됨!
 *     }
 * }
 * ```
 *
 * 🎯 QA 테스터를 위한 핵심 개념:
 * - 이 클래스는 직접 실행하지 않습니다 (abstract = 추상 클래스)
 * - 실제 테스트 클래스가 이 클래스를 "상속"받아 기능 활용
 * - 상속받은 클래스는 자동으로 setUp(준비)과 tearDown(정리) 실행
 * - 모든 테스트가 동일한 방식으로 시작하고 종료되어 일관성 확보
 */
public abstract class BaseTestCase {

    /**
     * 스마트폰 제어 도구 (모든 테스트에서 공통 사용)
     *
     * protected = 이 클래스를 상속받은 자식 클래스에서 접근 가능
     * 예: SmokeTestSuite에서 driver.activateApp() 같은 명령 사용 가능
     */
    protected AndroidDriver driver;

    /**
     * 테스트 결과 기록 도구 (구글 시트 자동 업데이트)
     *
     * 테스트 케이스별 Pass/Fail/Block 결과를 실시간으로
     * 지정된 구글 시트에 기록합니다.
     */
    protected ChecklistReporter reporter;

    /**
     * 테스트 결과를 기록할 구글 스프레드시트 고유 식별 번호
     *
     * 구글 시트 URL에서 추출:
     * https://docs.google.com/spreadsheets/d/[이 부분이 SPREADSHEET_ID]/edit
     *
     * 💡 변경 방법:
     * 1. 새로운 구글 시트 생성 (또는 기존 시트 URL 복사)
     * 2. URL에서 "/d/"와 "/edit" 사이의 긴 문자열 복사
     * 3. 이 상수값 교체
     * 4. 모든 테스트가 새 시트에 기록됨
     */
    private static final String SPREADSHEET_ID = "1aK2P0oL-WeT4LTU9ZeI5LDsAZjR9NI1ufh0W7ed1_j4";

    /**
     * 스프레드시트 내에서 결과를 기록할 특정 시트 이름
     *
     * 하나의 스프레드시트 문서 안에는 여러 개의 시트(탭)가 있을 수 있습니다.
     * 예: "checklist", "버그리포트", "통계" 등
     *
     * 현재 설정: "checklist" 시트에 테스트 결과 기록
     *
     * 💡 여러 테스트 스위트를 다른 시트에 기록하려면:
     * - SmokeTestSuite → "smoke_checklist"
     * - RegressionTestSuite → "regression_checklist"
     * 이런 식으로 시트명을 다르게 설정 가능
     */
    private static final String SHEET_NAME = "checklist";

    /**
     * 테스트 시작 전 자동 실행: 무대 설치 단계
     *
     * JUnit의 @Before 애노테이션으로 인해 모든 @Test 메서드 실행 전에
     * 자동으로 한 번씩 실행됩니다.
     *
     * 🎬 실행 순서:
     * 1. setUp() 자동 실행 ← 지금 여기
     * 2. @Test 메서드 실행 (예: testTC01_AppStart)
     * 3. tearDown() 자동 실행
     *
     * 📋 수행 작업 목록:
     *
     * **1단계: 스마트폰 연결 및 앱 실행**
     * - DriverFactory를 통해 Appium 서버 연결
     * - 대상 앱 자동 실행 (이미 실행 중이면 포그라운드로)
     * - 제어 준비가 완료된 AndroidDriver 객체 생성
     *
     * **2단계: 구글 시트 연결**
     * - GoogleSheetsClient로 구글 API 인증
     * - 지정된 스프레드시트 접근 권한 확보
     * - Sheets 서비스 객체 생성
     *
     * **3단계: 리포터 초기화**
     * - ChecklistReporter에 구글 시트 정보 전달
     * - 테스트 결과 기록 준비 완료
     *
     * ⚠️ 에러 발생 시:
     * - Appium 서버 미실행 → "Connection refused" 에러
     * - 스마트폰 미연결 → "No devices attached" 에러
     * - 구글 인증 실패 → "Authentication failed" 에러
     * - 위 문제 발생 시 모든 테스트 실행 중단 (Fail Fast 전략)
     *
     * @throws Exception 연결 실패, 인증 오류 등 모든 예외 상위로 전파
     */
    @Before
    public void setUp() throws Exception {
        System.out.println("=== 테스트 환경 초기화 ===");

        // [STEP 1] 스마트폰 제어 도구 생성 및 앱 실행
        // 이 시점에서 실제로:
        // - Appium 서버와 연결
        // - USB로 연결된 스마트폰 또는 에뮬레이터 탐지
        // - 앱 실행 (이미 실행 중이면 포그라운드로)
        driver = DriverFactory.createAndroidDriver();

        // [STEP 2] 구글 시트 API 서비스 생성
        // 구글 계정 인증 및 스프레드시트 접근 권한 획득
        Sheets sheets = GoogleSheetsClient.createSheetsService();

        // [STEP 3] 테스트 결과 리포터 초기화
        // 어느 시트의 어느 탭에 기록할지 설정
        reporter = new ChecklistReporter(sheets, SPREADSHEET_ID, SHEET_NAME);

        System.out.println("✓ 준비 완료\n");
    }

    /**
     * 테스트 종료 후 자동 실행: 무대 정리 단계
     *
     * JUnit의 @After 애노테이션으로 인해 모든 @Test 메서드 실행 후에
     * 자동으로 한 번씩 실행됩니다.
     *
     * 🎬 실행 순서:
     * 1. setUp() 자동 실행
     * 2. @Test 메서드 실행
     * 3. tearDown() 자동 실행 ← 지금 여기
     *
     * 🧹 정리 작업:
     * - AndroidDriver 종료 (스마트폰 연결 해제)
     * - Appium 세션 종료 (메모리 정리)
     * - 리소스 누수 방지
     *
     * ⚠️ 중요한 특징:
     * - **테스트가 성공하든 실패하든 항상 실행됨**
     * - 테스트 중 예외 발생해도 정리는 반드시 수행
     * - driver.quit() 실패 시에도 프로그램 종료는 정상 진행
     *
     * 💡 왜 항상 실행되어야 하나요?
     * 만약 정리를 안 하면:
     * - Appium 세션이 계속 쌓여서 메모리 부족
     * - 다음 테스트 실행 시 이전 상태가 남아있어 오염
     * - 스마트폰이 계속 연결 상태로 배터리 소모
     */
    @After
    public void tearDown() {
        System.out.println("=== 테스트 환경 정리 ===");

        // [CLEANUP] AndroidDriver 종료 (안전 체크 포함)
        if (driver != null) {
            // driver.quit()의 역할:
            // 1. 현재 Appium 세션 종료
            // 2. 스마트폰 연결 해제
            // 3. 앱은 종료하지 않음 (NoReset 설정 유지)
            driver.quit();
            System.out.println("✓ Driver 종료\n");
        }

        // 구글 시트 연결은 자동으로 정리됨 (명시적 종료 불필요)
        // ChecklistReporter는 이미 모든 기록을 완료한 상태
    }

    /**
     * 테스트 케이스 결과 자동 기록 (Pass 또는 Fail)
     *
     * 이 메서드는 테스트 결과를 "증거와 함께" 기록하는 올인원 도구입니다.
     * 하나의 메서드 호출로 화면 캡처, 파일 저장, 구글 시트 업데이트를 모두 처리합니다.
     *
     * 🎯 수행 작업:
     * 1. **현재 화면 캡처** (Evidence.saveScreenshot)
     * 2. **파일명 자동 생성** (TC번호_테스트명_결과.png)
     * 3. **증거 파일 저장** (screenshots 폴더)
     * 4. **구글 시트에 결과 기록** (해당 TC 행에 Pass/Fail 표시)
     *
     * 💡 실전 활용 예시:
     * ```java
     * // TC01: 앱 시작 테스트
     * boolean appStarted = StartAppFlow.run(driver);
     * recordResult(1, "AppStart", appStarted);
     * // → 결과: screenshots/TC01_AppStart_Pass.png 저장
     * // → 구글 시트 TC01 행에 "Pass" 기록
     *
     * // TC02: 로고 검증 테스트
     * boolean logoFound = Evidence.waitForImage(driver, "logo.png", 30);
     * recordResult(2, "LogoVerify", logoFound);
     * // → 실패 시: screenshots/TC02_LogoVerify_Fail.png 저장
     * // → 구글 시트 TC02 행에 "Fail" 기록
     * ```
     *
     * 📸 증거 파일 명명 규칙:
     * - 형식: TC{번호2자리}_{테스트명}_{결과}.png
     * - 예시: TC01_AppStart_Pass.png
     * - 예시: TC03_MainMenu_Fail.png
     * - 목적: 파일명만 봐도 어떤 테스트의 어떤 결과인지 즉시 파악
     *
     * 📊 구글 시트 기록 방식:
     * - tcNo로 해당 테스트 케이스의 행 자동 탐색
     * - 결과 컬럼에 "Pass" 또는 "Fail" 기록
     * - 실시간 업데이트로 테스트 진행 상황 즉시 확인 가능
     *
     * ⚠️ 주의사항:
     * - tcName은 파일명에 사용되므로 특수문자 사용 금지
     * - 영문, 숫자, 언더스코어(_)만 권장
     * - 한글 사용 시 일부 시스템에서 파일명 깨질 수 있음
     *
     * @param tcNo 테스트 케이스 번호 (1, 2, 3, ...)
     * @param tcName 테스트 케이스 이름 (파일명에 사용, 특수문자 금지)
     * @param isPass 테스트 통과 여부 (true = Pass, false = Fail)
     * @throws Exception 화면 캡처 실패, 구글 시트 연결 오류 등
     */
    protected void recordResult(int tcNo, String tcName, boolean isPass) throws Exception {
        // [STEP 1] 결과를 문자열로 변환 (Pass 또는 Fail)
        String result = isPass ? "Pass" : "Fail";

        // [STEP 2] 현재 화면 캡처 및 증거 파일 저장
        // String.format("%02d", tcNo) = 1 → "01", 2 → "02" (항상 2자리 유지)
        String evidencePath = Evidence.saveScreenshot(
                driver,
                "TC" + String.format("%02d", tcNo) + "_" + tcName + "_" + result
        );
        System.out.println("→ 증거 저장: " + evidencePath);

        // [STEP 3] 구글 시트에 결과 기록
        // ChecklistReporter가 자동으로:
        // 1. tcNo에 해당하는 행 찾기
        // 2. 결과 컬럼에 Pass/Fail 기록
        // 3. 타임스탬프 자동 추가 (옵션)
        reporter.writeTCResult(tcNo, result);
        System.out.println("→ 결과 기록 완료: " + result);
    }

    /**
     * 테스트 케이스 Block 상태 기록 (전제조건 미충족)
     *
     * "Block"은 테스트 실패도 아니고 성공도 아닌 "실행 불가" 상태입니다.
     *
     * 🚫 Block이 발생하는 상황:
     *
     * **예시 1: 이전 테스트 실패로 인한 연쇄 Block**
     * ```
     * TC01: 앱 시작 → Fail (앱이 안 켜짐)
     * TC02: 로그인 → Block (앱이 안 켜져서 로그인 자체가 불가능)
     * TC03: 메인 메뉴 → Block (로그인이 안 돼서 메인 메뉴 진입 불가)
     * ```
     *
     * **예시 2: 환경 문제로 인한 Block**
     * ```
     * TC05: 인앱 결제 테스트 → Block (테스트 계정에 결제 권한 없음)
     * TC06: 프리미엄 기능 → Block (결제가 안 돼서 프리미엄 기능 접근 불가)
     * ```
     *
     * 💡 Fail vs Block 구분:
     * - **Fail**: 테스트를 실행했는데 기대와 다른 결과 발생
     *   → "로그인 버튼을 눌렀는데 에러 메시지가 나왔다"
     *
     * - **Block**: 테스트를 실행할 수 있는 상태 자체가 안 됨
     *   → "앱이 안 켜져서 로그인 버튼을 누를 수가 없다"
     *
     * 📊 리포팅 차이:
     * - Fail: 개발팀이 버그 수정 필요
     * - Block: QA팀이 테스트 환경 또는 전제조건 해결 필요
     *
     * 🎯 실전 활용 예시:
     * ```java
     * // TC02: 메인 화면 로고 검증
     * boolean appStarted = StartAppFlow.run(driver);
     * if (!appStarted) {
     *     // 앱이 안 켜졌으므로 로고 검증 자체가 불가능
     *     recordBlock(2, "LogoVerify", "앱 실행 실패 (TC01 전제조건 미충족)");
     *     return; // 더 이상 진행하지 않고 종료
     * }
     *
     * // 앱이 정상적으로 켜진 경우에만 로고 검증 진행
     * boolean logoFound = Evidence.waitForImage(driver, "logo.png", 30);
     * recordResult(2, "LogoVerify", logoFound);
     * ```
     *
     * 📸 증거 파일 명명:
     * - 형식: TC{번호}_테스트명_BLOCK.png
     * - 예시: TC02_LogoVerify_BLOCK.png
     * - Block 상태의 화면을 캡처하여 원인 파악에 활용
     *
     * ⚠️ 주의사항:
     * - Block 발생 시 reason(사유)을 명확히 기록할 것
     * - 다음 테스트도 Block될 가능성이 높으므로 조기 종료 고려
     * - Block이 많으면 테스트 설계나 환경 설정 재검토 필요
     *
     * @param tcNo 테스트 케이스 번호
     * @param tcName 테스트 케이스 이름
     * @param reason Block 발생 사유 (예: "앱 실행 실패", "로그인 전제조건 미충족")
     * @throws Exception 화면 캡처 실패, 구글 시트 연결 오류 등
     */
    protected void recordBlock(int tcNo, String tcName, String reason) throws Exception {
        // [LOG] Block 발생 사실 및 사유 출력
        System.out.println("→ TC" + String.format("%02d", tcNo) + " Block: " + reason);

        // [STEP 1] Block 상태의 화면 캡처 (문제 원인 파악용)
        Evidence.saveScreenshot(driver, "TC" + String.format("%02d", tcNo) + "_" + tcName + "_BLOCK");

        // [STEP 2] 구글 시트에 "Block" 상태 기록
        // Pass/Fail과 구분되는 별도 상태로 표시
        reporter.writeTCResult(tcNo, "Block");
    }
}
