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
 * Google Sheets API 클라이언트 생성 유틸리티
 *
 * <p>Service Account 인증 방식으로 Google Sheets 자동 연동을 제공합니다.
 * 테스트 결과를 팀원들과 실시간 공유할 수 있는 핵심 차별화 기능입니다.</p>
 *
 * <h3>사전 준비사항</h3>
 * <ol>
 *   <li>Google Cloud Console에서 Service Account 생성 및 JSON 키 다운로드</li>
 *   <li>Sheets API 활성화</li>
 *   <li>대상 스프레드시트에 Service Account 이메일 편집 권한 부여</li>
 *   <li>app/src/test/resources/credentials/ 경로에 JSON 파일 배치</li>
 * </ol>
 *
 * @author [Your Name]
 * @since 2025-01-24
 */
public class GoogleSheetsClient {

    private static final String APPLICATION_NAME = "appium-automation";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /**
     * ⚠️ 보안 주의: 실제 배포 시 환경변수 또는 별도 설정 파일로 관리 권장
     * 현재 경로: app/src/test/resources/credentials/google-service-account.json
     */
    private static final String SERVICE_ACCOUNT_RESOURCE = "credentials/google-service-account.json";

    /**
     * 인증된 Google Sheets 서비스 객체를 생성합니다.
     *
     * @return 테스트 결과 기록이 가능한 Sheets API 클라이언트
     * @throws Exception 인증 파일 누락 또는 네트워크 오류 시
     */
    public static Sheets createSheetsService() throws Exception {
        // 신뢰할 수 있는 HTTP 전송 계층 생성
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        // Service Account 인증 키 파일 로드
        InputStream in = GoogleSheetsClient.class.getClassLoader().getResourceAsStream(SERVICE_ACCOUNT_RESOURCE);
        if (in == null) {
            throw new IllegalStateException(
                    "Service account json not found: " + SERVICE_ACCOUNT_RESOURCE +
                            "\n확인 필요: app/src/test/resources/credentials/google-service-account.json"
            );
        }

        // SPREADSHEETS 스코프로 읽기/쓰기 권한 설정
        GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                .createScoped(List.of(SheetsScopes.SPREADSHEETS));

        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        // Sheets API 클라이언트 빌드 및 반환
        return new Sheets.Builder(httpTransport, JSON_FACTORY, requestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}