package com.example.appium_android_automation.marker;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.OutputType;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 테스트 증거 수집 전문가 (법정 증거 사진사 역할)
 *
 * 이 클래스는 테스트 실행 중 발생한 모든 화면을 "증거 사진"으로 남기는 전문 사진사입니다.
 * 마치 법정에서 사건 현장 사진이 중요한 증거가 되듯이,
 * 테스트 결과의 정확성을 입증하기 위한 화면 캡처를 담당합니다.
 *
 * 📸 증거 수집의 중요성:
 *
 * **시나리오 1: 버그 리포팅**
 * - QA: "로그인 버튼을 눌렀는데 에러가 났어요"
 * - 개발자: "저는 재현이 안 되는데요?"
 * - 증거 사진: 에러 메시지가 명확히 보이는 화면 캡처 제공
 * → 개발자가 즉시 문제 파악 및 수정 가능
 *
 * **시나리오 2: 자동화 테스트 신뢰성 확보**
 * - 자동화 테스트가 Pass라고 해도 실제로 올바른 화면인지 의심스러울 때
 * - 증거 사진으로 "정말 올바른 화면에서 Pass 판정했다"는 것을 확인
 * → 자동화 테스트 결과에 대한 신뢰도 향상
 *
 * 🎯 주요 기능:
 * - 현재 스마트폰 화면을 PNG 이미지로 정확히 캡처
 * - 파일명에 테스트 정보와 시간 자동 포함 (중복 방지 + 추적 용이)
 * - 체계적인 폴더 구조로 증거 자동 정리
 * - 테스트 실패 시 실패 원인 파악을 위한 핵심 자료 제공
 *
 * 💡 Unity 앱 특성 반영:
 * - Unity SurfaceView는 일반 앱과 달리 화면 요소를 직접 읽을 수 없음
 * - 따라서 화면 캡처가 유일하게 신뢰할 수 있는 검증 방법
 * - 이미지 매칭 실패 시 "어떤 화면이었는지" 확인하는 유일한 수단
 *
 */
public class Evidence {

    /**
     * 현재 스마트폰 화면을 캡처하여 증거 파일로 저장
     *
     * 이 메서드는 "지금 이 순간"의 스마트폰 화면을 정확히 사진으로 남깁니다.
     *
     * 🎬 동작 과정:
     * 1. AndroidDriver에게 "지금 화면을 캡처해줘" 명령
     * 2. 임시 파일로 화면 이미지 받기
     * 3. 의미있는 파일명 자동 생성 (테스트명_날짜_시간.png)
     * 4. 체계적인 폴더 구조에 저장 (build/reports/evidence/)
     * 5. 저장된 파일 경로 반환 (로그 출력 및 추적용)
     *
     * 📁 저장 폴더 구조:
     * ```
     * 프로젝트 루트/
     * └── build/
     *     └── reports/
     *         └── evidence/
     *             ├── TC01_AppStart_Pass_20250126_143052.png
     *             ├── TC02_LogoVerify_Pass_20250126_143055.png
     *             ├── TC03_MainMenu_Fail_20250126_143100.png
     *             └── TC03_MainMenu_BLOCK_20250126_143105.png
     * ```
     *
     * 📝 파일명 구조 상세 분석:
     *
     * **namePrefix 부분** (호출 시 지정):
     * - "TC01_AppStart_Pass" → TC 번호, 테스트명, 결과
     * - "TC03_MainMenu_Fail" → 실패한 테스트의 화면
     * - "TC05_Payment_BLOCK" → 실행 불가 상태의 화면
     *
     * **타임스탬프 부분** (자동 생성):
     * - "20250126_143052" → 2025년 1월 26일 14시 30분 52초
     * - 형식: yyyyMMdd_HHmmss
     * - 같은 테스트를 여러 번 실행해도 파일명 중복 없음
     *
     * 💡 QA 테스터를 위한 활용 가이드:
     *
     * **1. 테스트 실패 원인 분석**
     * - TC03이 실패했다면 → TC03_xxx_Fail_날짜.png 파일 확인
     * - 화면을 보고 "왜 실패했는지" 즉시 파악
     * - 버튼이 없는지, 에러 메시지가 떴는지, 로딩 중인지 등
     *
     * **2. 버그 리포트 첨부 자료**
     * - 증거 파일을 JIRA, 노션 등에 첨부
     * - "이런 화면에서 이런 문제가 발생했습니다" 명확히 전달
     * - 개발자가 재현 없이도 문제 상황 정확히 이해 가능
     *
     * **3. 자동화 테스트 검증**
     * - Pass라고 나왔지만 의심스러울 때 증거 사진 확인
     * - "정말 올바른 화면에서 Pass 판정했나?" 검증
     * - 이미지 매칭이 잘못된 부분을 매칭했는지 확인
     *
     * ⚠️ 주의사항:
     *
     * **파일명 규칙 준수**
     * - namePrefix에 특수문자 사용 금지 (/, \, :, *, ?, ", <, >, |)
     * - 영문, 숫자, 언더스코어(_)만 사용 권장
     *
     * **저장 공간 관리**
     * - 화면 캡처는 파일 크기가 큼 (보통 500KB~2MB)
     * - 오래된 증거 파일은 주기적으로 정리 필요
     *
     * @param driver 테스트용 스마트폰 제어 도구
     * @param namePrefix 파일명 앞부분 (예: "TC01_AppStart_Pass")
     * @return 저장된 파일의 전체 경로
     * @throws Exception 화면 캡처 실패, 파일 저장 실패, 디렉토리 생성 실패 등
     */
    public static String saveScreenshot(AndroidDriver driver, String namePrefix) throws Exception {
        // [STEP 1] 현재 스마트폰 화면을 임시 파일로 캡처
        // getScreenshotAs()는 Selenium/Appium의 표준 화면 캡처 메서드
        File src = driver.getScreenshotAs(OutputType.FILE);

        // [STEP 2] 현재 시간을 파일명 형식으로 변환
        // "yyyyMMdd_HHmmss" = 20250126_143052 (년월일_시분초)
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        // [STEP 3] 증거 파일을 저장할 폴더 경로 설정
        // build/reports/evidence = Gradle/Maven 표준 리포트 폴더
        Path outDir = Path.of("build", "reports", "evidence");

        // [STEP 4] 폴더가 없으면 자동 생성
        // createDirectories() = 중간 경로 폴더도 모두 생성
        Files.createDirectories(outDir);

        // [STEP 5] 최종 파일명 조합 및 복사
        Path out = outDir.resolve(namePrefix + "_" + ts + ".png");
        Files.copy(src.toPath(), out);

        // [RETURN] 저장된 파일의 전체 경로 문자열 반환
        return out.toString();
    }
}
