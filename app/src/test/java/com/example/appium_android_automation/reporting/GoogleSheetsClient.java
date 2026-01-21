package com.example.appium_android_automation.reporting;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.io.InputStream;
import java.util.List;

public class GoogleSheetsClient {

    private static final String APPLICATION_NAME = "appium-automation";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    // test resources 경로 기준
    private static final String SERVICE_ACCOUNT_RESOURCE = "credentials/google-service-account.json";

    public static Sheets createSheetsService() throws Exception {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        InputStream in = GoogleSheetsClient.class.getClassLoader().getResourceAsStream(SERVICE_ACCOUNT_RESOURCE);
        if (in == null) {
            throw new IllegalStateException("Service account json not found in test resources: " + SERVICE_ACCOUNT_RESOURCE);
        }

        GoogleCredentials credentials =
                GoogleCredentials.fromStream(in)
                        .createScoped(List.of(SheetsScopes.SPREADSHEETS));

        return new Sheets.Builder(httpTransport, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
