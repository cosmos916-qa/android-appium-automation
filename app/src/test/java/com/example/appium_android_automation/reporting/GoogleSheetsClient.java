package com.example.appium_android_automation.reporting;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.auth.http.HttpCredentialsAdapter;

import java.io.InputStream;
import java.util.List;

/**
 * 구글 시트 API 연결 전문가 (구글 클라우드 인증 담당자 역할)
 *
 * 이 클래스는 자동화 테스트 프로그램이 구글 스프레드시트에 접근할 수 있도록
 * "신분증"을 발급하고 "출입증"을 제공하는 보안 담당자입니다.
 *
 * 🔐 Service Account 인증 방식:
 * - 사람이 아닌 "프로그램 전용 계정"을 사용
 * - 로그인 창 없이 JSON 키 파일로 자동 인증
 * - 보안성이 높고 자동화 시스템에 최적화
 *
 * 📋 사전 준비 체크리스트 (6단계)
 *
 * **1단계: 구글 클라우드 프로젝트 생성**
 * - https://console.cloud.google.com 접속
 * - "새 프로젝트" 생성 (예: "appium-test-automation")
 *
 * **2단계: Google Sheets API 활성화**
 * - 프로젝트 대시보드 → "API 및 서비스" → "라이브러리"
 * - "Google Sheets API" 검색 → "사용 설정" 클릭
 *
 * **3단계: Service Account 생성**
 * - "IAM 및 관리자" → "서비스 계정"
 * - "서비스 계정 만들기" 클릭
 * - 이름: automation-reporter (자유롭게 설정)
 * - 역할: 없음 (시트별로 개별 권한 부여할 예정)
 *
 * **4단계: JSON 키 파일 다운로드**
 * - 생성된 서비스 계정 클릭
 * - "키" 탭 → "키 추가" → "새 키 만들기"
 * - JSON 형식 선택 → 다운로드
 *
 * **5단계: 키 파일 프로젝트에 배치**
 * ```
 * 프로젝트 루트/
 * └── app/
 *     └── src/
 *         └── test/
 *             └── resources/
 *                 └── credentials/
 *                     └── google-service-account.json ← 여기에 복사!
 * ```
 *
 * **6단계: 스프레드시트에 권한 부여**
 * - 테스트 결과를 기록할 구글 시트 열기
 * - 우측 상단 "공유" 버튼 클릭
 * - Service Account 이메일 주소 입력 (JSON 파일에서 확인)
 * - 권한: "편집자" 선택
 * - "전송" 클릭
 *
 * ⚠️ 보안 주의사항:
 *
 * **JSON 키 파일 관리:**
 * - ❌ 절대 Git에 커밋하지 말 것!
 * - ✅ .gitignore에 반드시 추가
 *
 *
 * **키 파일 유출 시 대응:**
 * 1. 즉시 구글 클라우드 콘솔에서 해당 키 삭제
 * 2. 새 키 생성 및 재배포
 * 3. 유출된 키로 접근 가능했던 모든 시트 권한 재검토
 *
 */
public class GoogleSheetsClient {

    /**
     * 애플리케이션 이름 (구글 API 요청 시 식별용)
     */
    private static final String APPLICATION_NAME = "appium-automation";

    /**
     * JSON 직렬화/역직렬화 팩토리
     */
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /**
     * Service Account 키 파일 경로 (리소스 폴더 기준)
     *
     * ⚠️ 보안 주의:
     * 실제 배포 환경에서는 환경변수로 관리 권장
     * 예: System.getenv("GOOGLE_SERVICE_ACCOUNT_PATH")
     */
    private static final String SERVICE_ACCOUNT_RESOURCE = "credentials/google-service-account.json";

    /**
     * 인증된 구글 시트 API 서비스 객체 생성 (핵심 메서드)
     *
     * 🎬 인증 과정 (5단계):
     * 1. 신뢰할 수 있는 HTTP 전송 계층 생성
     * 2. Service Account 키 파일 로드
     * 3. 인증 정보(Credentials) 생성 및 스코프 설정
     * 4. HTTP 요청 초기화 객체 생성
     * 5. Sheets API 클라이언트 빌드
     *
     * ⚠️ 에러 발생 시 체크포인트:
     *
     * **"Service account json not found"**
     * - 파일 경로 확인: app/src/test/resources/credentials/
     * - 파일명 확인: google-service-account.json
     *
     * **"403 Forbidden"**
     * - Service Account에 시트 편집 권한 부여했는지 확인
     * - 시트 "공유" 설정에서 Service Account 이메일 확인
     *
     * **"401 Unauthorized"**
     * - JSON 키 파일이 유효한지 확인
     * - 구글 클라우드 콘솔에서 Service Account 활성 상태 확인
     *
     * **"API has not been enabled"**
     * - 구글 클라우드 콘솔에서 Google Sheets API 활성화 확인
     *
     * @return 테스트 결과 기록이 가능한 Sheets API 클라이언트
     * @throws Exception 인증 파일 누락, 네트워크 오류, 권한 부족 등
     */
    public static Sheets createSheetsService() throws Exception {
        // [STEP 1] 신뢰할 수 있는 HTTP 전송 계층 생성
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        // [STEP 2] Service Account 인증 키 파일 로드
        InputStream in = GoogleSheetsClient.class.getClassLoader().getResourceAsStream(SERVICE_ACCOUNT_RESOURCE);

        // [VALIDATION] 파일 존재 여부 확인
        if (in == null) {
            throw new IllegalStateException(
                    "Service account json not found: " + SERVICE_ACCOUNT_RESOURCE +
                            "\n확인 필요: app/src/test/resources/credentials/google-service-account.json"
            );
        }

        // [STEP 3] 인증 정보 생성 및 스코프 설정
        GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                .createScoped(List.of(SheetsScopes.SPREADSHEETS));

        // [STEP 4] HTTP 요청 초기화 객체 생성
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        // [STEP 5] Sheets API 클라이언트 빌드 및 반환
        return new Sheets.Builder(httpTransport, JSON_FACTORY, requestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
