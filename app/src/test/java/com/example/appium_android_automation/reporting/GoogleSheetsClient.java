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

public class GoogleSheetsClient {

    private static final String APPLICATION_NAME = "appium-automation";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    // ✅ test resources 아래 경로
    private static final String SERVICE_ACCOUNT_RESOURCE = "credentials/google-service-account.json";

    public static Sheets createSheetsService() throws Exception {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        InputStream in = GoogleSheetsClient.class.getClassLoader().getResourceAsStream(SERVICE_ACCOUNT_RESOURCE);
        if (in == null) {
            throw new IllegalStateException("Service account json not found in test resources: " + SERVICE_ACCOUNT_RESOURCE);
        }

        // ✅ 포인트: createScoped() 결과 타입은 GoogleCredentials 로 받는 게 안전
        GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                .createScoped(List.of(SheetsScopes.SPREADSHEETS));

        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        return new Sheets.Builder(httpTransport, JSON_FACTORY, requestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
