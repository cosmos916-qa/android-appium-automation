# 🤖 Unity 모바일 게임 QA 자동화 프레임워크

> Appium 3.0 + Java 기반 레이어드 아키텍처 자동화 프레임워크  
> Unity SurfaceView 이미지 매칭 + Google Sheets 실시간 리포팅

[![Java](https://img.shields.io/badge/Java-23_(Target_17)-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Appium](https://img.shields.io/badge/Appium-3.0.0--rc.1-6E4C9A?logo=appium&logoColor=white)](https://appium.io/)

---

## 💡 핵심 특징

- **Unity 특화**: SurfaceView 이미지 매칭 기반 UI 자동화
- **범용성**: 레이어드 아키텍처로 일반 앱에도 즉시 적용 (70~80% 재사용)
- **협업**: Google Sheets 실시간 Pass/Fail 리포팅
- **확장성**: Test / Flow / Verification / Infra 계층 분리

---

## 🛠️ 기술 스택

- **Framework**: Appium 3.0 + UiAutomator2 + Images Plugin  
- **Language**: Java 23 (Target 17) + JUnit 4  
- **Reporting**: Google Sheets API v4  


---

## 📁 프로젝트 구조

```text
android-appium-automation
├─ flow                    # Business Layer
│  ├─ FirstLaunchFlow.java   # 최초 실행 6단계 통합 플로우
│  ├─ LoginFlow.java         # 구글 로그인 플로우
│  ├─ LogoutFlow.java        # 로그아웃 9단계 네비게이션
│  └─ StartAppFlow.java      # 앱 실행 및 연결 확인
│
├─ infra                   # Infrastructure Layer
│  ├─ AppiumConfig.java      # 설정값 중앙 관리
│  ├─ DriverFactory.java     # AndroidDriver 세션 관리
│  ├─ ScreenHelper.java      # 화면 해상도 계산
│  └─ TouchActionHelper.java # W3C Actions 터치/드래그 제어
│
├─ main                    # Core Layer
│  └─ BaseTestCase.java      # 공통 설정, 결과 기록
│
├─ marker                  # Verification Layer
│  ├─ ImageAssert.java       # OpenCV 이미지 매칭
│  └─ Evidence.java          # 스크린샷 자동 저장
│
├─ reporting               # Reporting Layer
│  ├─ ChecklistReporter.java # 동적 셀 계산
│  └─ GoogleSheetsClient.java# Google Sheets API 통신
│
└─ testcase                # Test Layer
   └─ SmokeTestSuite.java    # TC01~TC07 순차 실행

```


## 📖 상세 가이드
더 자세한 내용은 Notion 포트폴리오를 참고하세요.

🔗 [![Notion Portfolio](https://img.shields.io/badge/Portfolio-Notion-black?logo=notion&logoColor=white)](https://melon-crowd-c24.notion.site/Unity-QA-2ee788f1fc92808aab71ee7b660cd85a?pvs=74)

---

## 🤝 기여
학습 목적 프로젝트이며, 피드백은 언제나 환영합니다.

🌟 **Star this repository if you find it useful!**


