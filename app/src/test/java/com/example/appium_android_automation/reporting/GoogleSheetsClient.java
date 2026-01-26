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
 * Google Sheets API 클라이언트 생성
 * - Service Account 인증 기반 Sheets 서비스 생성
 */
public class GoogleSheetsClient {

    private static final String APPLICATION_NAME = "appium-automation";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /**
     * ⚠️ 보안 주의: 실제 배포 시 환경변수 또는 별도 설정 파일로 관리 권장
     * 현재 경로: app/src/test/resources/credentials/google-service-account.json
     */
    private static final String SERVICE_ACCOUNT_RESOURCE = "credentials/google-service-account.json";

    // Service Account 인증으로 Sheets 서비스 생성
    // 성공 시: 인증된 Sheets 객체, 실패 시: Exception
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