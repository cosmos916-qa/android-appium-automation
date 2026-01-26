package com.example.appium_android_automation.flow;

import io.appium.java_client.android.AndroidDriver;

/*
 * 테스트 시작 단계: 앱 실행하기
 * - 모든 테스트를 시작하기 전에 가장 먼저 실행되는 단계입니다.
 * - 마치 사람이 스마트폰에서 게임 아이콘을 터치해서 실행하는 것과 동일한 역할을 합니다.

 *이 단계에서 하는 일:
 * - 게임이 꺼져있으면 → 새로 실행
 * - 게임이 뒤로 숨어있으면 → 화면 앞으로 가져오기
 * - 게임이 제대로 켜졌는지 → 간단히 확인

 * 테스트 시나리오에서의 역할:
 * 1. StartAppFlow.run() 호출 → 게임 실행 시도
 * 2. true가 나오면 → 다음 테스트 진행 (로그인, 메인화면 등)
 * 3. false가 나오면 → 게임 실행 자체에 문제가 있으므로 테스트 중단
 *
 * 주의: 이 단계는 "게임이 켜졌는가?"만 확인합니다.
 * 로그인 성공이나 특정 화면 진입은 다른 테스트에서 확인합니다.
*/
public class StartAppFlow {
/*
 * 앱을 실행하고 정상 작동 여부를 확인합니다.
 *
 * 동작 순서
 * 1. 앱 실행 명령 전송 (앱 아이콘 터치와 동일)
 * 2. 현재 화면에서 보이는 앱 이름 확인
 * 3. 성공/실패 결과 전달
 *
 * 성공 시나리오 : 로딩 화면이나 메인 화면이 보임 > True
 * 실패 시나리오 : 앱 크래시, 다른 앱 화면, 에러 발생 > False
 *
 * @param driver 테스트용 스마트폰 제어 도구 (자동화 테스트의 리모컨 역할)
 * @return 성공 시나리오 = true, 실패 시나리오 = false
*/
    public static boolean run(AndroidDriver driver) {
        try {
            /*
             * [ACTION]앱 실행 요청
             * "com.epidgames.trickcalrevive"는 앱의 고유 식별 번호입니다
             * 이미 실행 중이면 화면 앞으로, 꺼져있으면 새로 실행합니다.
            */
            driver.activateApp("com.epidgames.trickcalrevive");

            /*
             * [ASSERT] 현재 화면에 떠있는 앱이 무엇인지 확인
             * 현재 패키지명으로 실행 상태 검증
             * 스마트폰에게 "지금 화면에 뭐가 보이니?" 라고 물어보는 과정입니다
            */
            String currentPkg = driver.getCurrentPackage();

            /*
             * 앱 이름이 정상적으로 확인되면 게임이 실행된 것으로 판단
             * null(아무것도 없음) 이거나 빈 문자열("")이면 실행 실패로 처리
            */
            return currentPkg != null && !currentPkg.isEmpty();

        } catch (Exception e) {
            /*
             * [ERROR] 예상치 못한 문제 발생 시 처리
             * 앱 크래시, 권한 문제, 네트워크 오류 등이 발생하면 여기서 잡힙니다.
            */
            System.err.println("[StartAppFlow] 앱 실행 실패: " + e.getMessage());
            return false;
        }
    }
}
