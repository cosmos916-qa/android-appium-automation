package com.example.appium_android_automation.reporting;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.List;

public class ChecklistReporter {

    private final Sheets sheets;
    private final String spreadsheetId;
    private final String sheetName;

    public ChecklistReporter(Sheets sheets, String spreadsheetId, String sheetName) {
        this.sheets = sheets;
        this.spreadsheetId = spreadsheetId;
        this.sheetName = sheetName;
    }
    /**
     * 간단 버전:
     * - No=1 이 2행(헤더 다음)이라고 가정
     * - Result 컬럼이 E열이라고 가정
     */
    public void writeResultNo1(String result) throws Exception {
        String range = sheetName + "!F4"; // Result 셀 위치(필요시 수정)
        ValueRange body = new ValueRange().setValues(List.of(List.of(result)));

        sheets.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();
    }
}