package com.example.appium_android_automation.reporting;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.util.List;

/**
 * Google Sheets 테스트 결과 기록
 * - TC 번호 기반 동적 셀 주소 계산 및 결과 기록
 */
public class ChecklistReporter {

    private final Sheets sheets;
    private final String spreadsheetId;
    private final String sheetName;

    // 스프레드시트 구조 상수
    private static final String RESULT_COLUMN = "G";  // Result 컬럼
    private static final int HEADER_ROWS = 3;         // 제목 + 헤더 행 수

    public ChecklistReporter(Sheets sheets, String spreadsheetId, String sheetName) {
        this.sheets = sheets;
        this.spreadsheetId = spreadsheetId;
        this.sheetName = sheetName;
    }

    // TC 번호 기반 결과 기록 (TC01 → G3, TC02 → G4)
    public void writeTCResult(int tcNo, String result) throws Exception {
        int rowNumber = tcNo + HEADER_ROWS;
        String cellAddress = RESULT_COLUMN + rowNumber;
        String range = sheetName + "!" + cellAddress;

        ValueRange body = new ValueRange().setValues(List.of(List.of(result)));

        sheets.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();

        System.out.println("[Report] TC" + String.format("%02d", tcNo) +
                " → " + cellAddress + " 기록 완료: " + result);
    }

    // 여러 TC 결과 일괄 기록
    public void writeBatchResults(java.util.Map<Integer, String> results) throws Exception {
        for (var entry : results.entrySet()) {
            writeTCResult(entry.getKey(), entry.getValue());
        }
    }

    // 편의 메서드 (하위 호환성, 선택적 사용)
    public void writeTC01Result(String result) throws Exception {
        writeTCResult(1, result);
    }

    public void writeTC02Result(String result) throws Exception {
        writeTCResult(2, result);
    }
}