package com.example.appium_android_automation.infra;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.URL;
import java.time.Duration;

/**
 * 테스트용 스마트폰 제어 도구 생성 공장
 *
 * 이 클래스는 자동화 테스트를 위한 "무선 조종기"를 만드는 공장입니다.
 * 마치 RC카를 조종하기 위해 리모컨과 자동차를 페어링하는 것처럼,
 * 컴퓨터와 스마트폰을 연결해서 게임을 자동으로 조작할 수 있게 해줍니다.
 *
 * 🎮 무선 조종 시스템 비유:
 * - 컴퓨터 = 조종사 (테스트 명령을 내리는 곳)
 * - 스마트폰 = RC카 (실제 게임이 실행되는 곳)
 * - AndroidDriver = 무선 조종기 (명령을 전달하는 리모컨)
 * - 이 클래스 = 조종기 설정 및 페어링 담당자
 *
 * 🔧 주요 역할:
 * - 테스트 시작 시 스마트폰과 컴퓨터 간 연결 수립
 * - 어떤 게임을 조종할지, 어떤 방식으로 제어할지 규칙 설정
 * - 연결이 끊어지지 않도록 안정성 확보
 *
 * ⚠️ Unity 게임 특성 반영:
 * - Unity SurfaceView로 인해 일반적인 앱 테스트보다 복잡한 설정 필요
 * - 게임 로딩 시간이 길어 타임아웃 설정이 중요
 * - NoReset 옵션으로 매번 튜토리얼 반복 방지
 */
public class DriverFactory {

    /**
     * 게임용 AndroidDriver 생성 및 연결
     *
     * 무선 조종기를 설정하고 스마트폰과 페어링하는 전 과정을 담당합니다.
     * 이 메서드가 성공해야 비로소 "화면 터치", "스와이프", "이미지 검증" 등의
     * 모든 자동화 명령을 실행할 수 있습니다.
     *
     * ⚙️ 핵심 설정값 해설:
     *
     * **UiAutomator2**: 안드로이드 공식 자동화 엔진
     * - 구글에서 직접 개발한 가장 안정적인 제어 방식
     * - Unity 게임과 호환성이 뛰어남
     * - 다른 엔진 대비 속도와 정확성에서 우수
     *
     * **NoReset(true)**: 앱 데이터 보존 모드
     * - 매 테스트마다 앱을 새로 설치하지 않음
     * - 로그인 상태, 게임 진행도, 설정값 모두 유지
     * - 테스트 시간 대폭 단축 (튜토리얼 스킵 가능)
     * - 주의: 계정 상태가 테스트에 영향을 줄 수 있음
     *
     * **NewCommandTimeout(30분)**: 연결 유지 시간
     * - Unity 게임의 긴 로딩 시간 대응
     * - 테스트 중 수동 확인이 필요한 상황 대비
     * - 디버깅 중 중단점에서 멈춰있어도 연결 유지
     *
     * 💡 QA 테스터를 위한 문제 해결 가이드:
     * - "앱이 자꾸 초기화돼요" → NoReset(true) 설정 확인
     * - "연결이 자주 끊겨요" → CommandTimeout 시간 증가 검토
     * - "다른 게임을 테스트하고 싶어요" → AppiumConfig의 패키지명 변경
     * - "에뮬레이터에서 안 돼요" → 에뮬레이터 설정과 Appium 버전 호환성 확인
     *
     * @return 즉시 사용 가능한 스마트폰 제어 도구 (AndroidDriver)
     * @throws Exception Appium 서버 미실행, USB 연결 실패, 앱 미설치 등의 오류 발생 시
     */
    public static AndroidDriver createAndroidDriver() throws Exception {
        // [SETUP] 무선 조종기 설정값 구성
        UiAutomator2Options options = new UiAutomator2Options()
                // 연결 대상 기기 식별명 (여러 기기 연결 시 구분용)
                .setDeviceName("Android")

                // 자동화 엔진 선택 (안드로이드 공식 UiAutomator2 사용)
                .setAutomationName("UiAutomator2")

                // 제어 대상 게임의 고유 식별 번호 (AppiumConfig에서 중앙 관리)
                .setAppPackage(AppiumConfig.APP_PACKAGE)

                // 게임 시작 시 첫 화면 지정 (Firebase 기반 Unity 게임의 시작점)
                .setAppActivity(AppiumConfig.APP_ACTIVITY)

                // 🔑 중요: 게임 데이터 보존 설정
                // true = 기존 로그인, 진행도, 설정 모두 유지
                // false = 매번 새로 설치하는 것처럼 완전 초기화
                .setNoReset(true)

                // 명령 없이도 연결을 유지할 최대 시간 (1800초 = 30분)
                // Unity 게임의 특성상 로딩이 길고 예측하기 어려워서 여유있게 설정
                .setNewCommandTimeout(Duration.ofSeconds(1800));

        // [CONNECTION] Appium 서버에 접속하여 실제 제어 도구 생성
        // 이 시점에서 실제로:
        // 1. Appium 서버가 연결된 스마트폰/에뮬레이터를 찾음
        // 2. 앱을 실행함 (이미 실행 중이면 포그라운드로)
        // 3. 제어 준비가 완료된 AndroidDriver 객체 반환
        return new AndroidDriver(new URL(AppiumConfig.SERVER_URL), options);
    }
}
