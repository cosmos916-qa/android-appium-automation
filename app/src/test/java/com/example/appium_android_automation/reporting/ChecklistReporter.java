package com.example.appium_android_automation.reporting;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.util.List;

/**
 * Google Sheets 체크리스트 리포터 (동적 셀 주소 계산)
 *
 * <p>TC 번호를 기반으로 자동으로 셀 주소를 계산하여 결과를 기록합니다.</p>
 *
 * <h3>셀 주소 계산 규칙</h3>
 * <ul>
 *   <li>1행: 제목, 2행: 헤더</li>
 *   <li>3행부터: 실제 데이터 (TC01 → F3, TC02 → F4)</li>
 *   <li>공식: F{tcNo + 2}</li>
 * </ul>
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

    /**
     * TC 번호를 기반으로 동적으로 결과를 기록합니다.
     *
     * @param tcNo TC 번호 (1부터 시작: TC01=1, TC02=2, ...)
     * @param result 테스트 결과 ("Pass", "Fail", "Block")
     * @throws Exception Google Sheets API 호출 실패 시
     */
    public void writeTCResult(int tcNo, String result) throws Exception {
        // 셀 주소 동적 계산: F{tcNo + 2}
        // TC01(1) → F3, TC02(2) → F4, TC99(99) → F101
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

    /**
     * 여러 TC 결과를 일괄 기록합니다.
     *
     * @param results TC 번호와 결과 맵 (예: {1: "Pass", 2: "Fail"})
     */
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