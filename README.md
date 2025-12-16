## 🍞 𝗕𝗨𝗡𝗢𝗨𝗧: 행동 활성화를 통한 번아웃 증후군 개선 프로젝트

### ⏳ 서비스 개요

| 구분 | 내용 |
| :--- | :--- |
| **서비스명** | BUNOUT |
| **기간** | 2024.03 ~ 2025.08 |
| **목표** | 행동 활성화를 통한 번아웃 증후군 개선 프로젝트 |
| **팀명** | Egoist (기획: 4명, 프론트엔드: 1명, 백엔드: 2명, 디자이너: 1명) |
| **성과** | 울산광역시 정신건강 인식개선 아이디어 공모전 장려상 |

### 🔍 기획 배경

- 최근 번아웃 증후군을 겪는 사람들이 증가하면서 이를 개선할 수 있는 디지털 솔루션에 대한 관심이 높아지고 있습니다.
- 행동 활성화 기법을 활용하여 사용자가 일상에서 실천할 수 있는 활동을 제안하고 관리할 수 있는 애플리케이션을 개발했습니다.

### ✨ 주요 기능

- 유형 진단: 번아웃 테스트를 통해 개인의 상태를 분석
- 맞춤 퀘스트 추천: 진단 결과에 따라 고정 및 일일 퀘스트 자동 제공
- 레벨업 시스템: 퀘스트 수행 및 출석 체크를 통해 경험치 적립
- 대시보드: 퀘스트 수행 기록을 캘린더 형태로 확인 가능

### ⚙️ 백엔드 기술 스택

| 구분                       | 기술                                      |
| ------------------------ | --------------------------------------- |
| **Language / Framework** | Java 17, Spring Boot 3, Spring Data JPA |
| **Authentication**       | OAuth 2.0, JWT                          |
| **Database**             | AWS RDS (MySQL)                         |
| **Infra / DevOps**       | Jenkins, Docker, Nginx, AWS EC2, AWS S3 |
| **Testing**              | JUnit                                   |
| **API 문서화**              | SpringDoc (OpenAPI 3.0), Swagger UI     |
| **협업 도구**                | GitHub, Slack, Notion                   |

### 📁 폴더 구조

```md
📦 src
 ┣ 📂 main
 ┃ ┣ 📂 java
 ┃ ┃ ┗ 📂 dough
 ┃ ┃   ┣ 📂 burnout                # 번아웃 유형 관리
 ┃ ┃   ┣ 📂 dashboard              # 사용자 활동 통계 및 대시보드
 ┃ ┃   ┣ 📂 feedback               # 사용자 피드백 수집 및 처리
 ┃ ┃   ┣ 📂 global                 # 공통 설정, 예외, 유틸 등 전역 모듈
 ┃ ┃   ┣ 📂 keyword                # 키워드 정보 관리
 ┃ ┃   ┣ 📂 level                  # 레벨 및 경험치 시스템
 ┃ ┃   ┣ 📂 login                  # 로그인 및 인증 (OAuth, JWT)
 ┃ ┃   ┣ 📂 member                 # 사용자 관련 기능
 ┃ ┃   ┣ 📂 notification           # 일반 알림 전송 기능
 ┃ ┃   ┣ 📂 pushNotification       # 푸시 알림 (FCM) 처리
 ┃ ┃   ┣ 📂 quest                  # 퀘스트 제공 및 관리
 ┃ ┃   ┗ 📄 DoughApplication.java
 ┃ ┗ 📂 resources
 ┃     ┗ 📄 application.yml
 ┗ 📂 test
     ┗ 📂 java
         ┗ 📂 dough
```

### 📐 시스템 아키텍처 설계

![bunout](https://github.com/user-attachments/assets/42a36102-1d00-45ee-8637-af0c99404801) 

### 🗄️ 데이터베이스 설계

<img width="1519" height="722" alt="image" src="https://github.com/user-attachments/assets/842928c7-7070-4af9-aacd-de231e66f91e" />

## 팀 소개

### 👥 팀 구성

| 이름      | 역할                 | 깃허브                                                                                                                                                                            |
| ------- | ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **임주현** | Lead               | -                                                                                                                                                                              |
| **신재민** | Business Developer | -                                                                                                                                                                              |
| **이영선** | R&D                | -                                                                                                                                                                              |
| **노시현** | Business Developer | -                                                                                                                                                                              |
| **이가현** | Designer           | -                                                                                                                                                                              |
| **이재림** | Frontend Developer | <a href="https://github.com/jllee000" target="_blank"><img src="https://img.shields.io/badge/GitHub-jllee000-181717?style=flat-square&logo=github&logoColor=white"/></a>       |
| **하고은** | Backend Developer  | <a href="https://github.com/hagoeun0119" target="_blank"><img src="https://img.shields.io/badge/GitHub-hagoeun0119-181717?style=flat-square&logo=github&logoColor=white"/></a> |
| **김준희** | Backend Developer  | <a href="https://github.com/karl21-02" target="_blank"><img src="https://img.shields.io/badge/GitHub-karl21--02-181717?style=flat-square&logo=github&logoColor=white"/></a>    |

### 🔑 주요 업무

| 프로필                                                                                                        | 담당 기능                 | 세부 내용                                                                                             |
| ---------------------------------------------------------------------------------------------------------- | --------------------- | ------------------------------------------------------------------------------------------------- |
| <p align="center"><img src="https://github.com/hagoeun0119.png" width="120"/><br><sub><b>하고은</b></sub></p> | 퀘스트 시스템 및 무중단 배포 구축   | - 퀘스트 제공 및 관리 시스템 개발<br>- 경험치 및 레벨 관리 시스템 구축<br>- 통계 및 대시보드 시스템 개발 |
| <p align="center"><img src="https://github.com/karl21-02.png" width="120"/><br><sub><b>김준희</b></sub></p>   | 사용자 인증 및 푸시 알림 시스템 구축 | - 카카오·애플 OAuth 2.0 및 JWT 로그인 구현<br>- Firebase 기반 푸시 알림 기능 구현                                      |

## 주요 기능 및 UI

### 🧩 주요 기능

1. 회원가입 및 튜토리얼
2. 번아웃 유형 및 고정 퀘스트 설정
3. 퀘스트 수행 및 인증
4. 출석체크, 퀘스트 수행을 통한 경험치 획득
5. 대시보드를 통한 주별, 월별 미션 완료 현황 확인

### 🔍 상세 기능

#### 1. 회원가입 & 튜토리얼

* 카카오 소셜 로그인 기반 간편 회원가입
* 튜토리얼을 통해 서비스 핵심 기능 안내

#### 2. 번아웃 유형 & 퀘스트 설정

* 번아웃 유형 선택

  * ‘소보로’, ‘호빵’, ‘공갈빵’, ‘크림빵’ 중 선택
  * 한 달마다 변경 가능, 익일부터 적용
* 고정 퀘스트 설정

  * 유형별 고정 퀘스트 4개 중 1개 선택
  * 일주일마다 재설정 가능

#### 3. 메인 화면

* 데일리 퀘스트 제공
* 월/수/토 스페셜 퀘스트 추가 제공
* 총 268개 퀘스트 제공

#### 4. 레벨 관리 시스템

* 출석체크, 퀘스트 수행을 통한 경험치 획득
* 최대 40레벨까지 레벨 업 가능

#### 5. 대시보드

* 특정 날짜 기준 ±3일 범위의 주간 완료 기록 제공
* 달력 UI 기반의 월별 퀘스트 완료 내역 시각화
* 월간 통계 데이터 제공(평균 달성률, 최다 완료 요일, 완료된 퀘스트 수 포함)

### 📲 𝗨𝗜

| 회원가입 | 튜토리얼 |
|------------------|----------------|
| ![BUNOUT_인트로](https://github.com/user-attachments/assets/804dff22-86e5-4042-9a8b-bd813cf15117) | ![BUNOUT_튜토리얼](https://github.com/user-attachments/assets/4af5d7c1-f00c-489d-bf7d-4b9fa7a470c5) |
| 카카오 소셜 로그인을 통한 간편 회원가입이 가능합니다. | 튜토리얼을 통해 서비스 핵심 기능을 안내합니다. |

| 번아웃 유형 | 퀘스트 설정 |
|-----------|--------------|
| ![BUNOUT_유형재설정](https://github.com/user-attachments/assets/edae3cf8-aa28-446a-ad22-0250cb942508) | ![BUNOUT_고정퀘스트](https://github.com/user-attachments/assets/eed868a3-c94d-4a1b-81db-9d61273fb245) |
| 번아웃 유형 선택이 가능합니다. | 고정 퀘스트 선택이 가능합니다. |

| 메인 화면 | 출석 체크 |
|-----------|----------|
| ![BUNOUT_메인](https://github.com/user-attachments/assets/f27fe3f8-ff78-4937-a239-cde71352d0c2) | ![BHNOUT_출석](https://github.com/user-attachments/assets/0d036a02-1ec4-4055-9b31-87f9e45d23a9) |
| 오늘 제공된 퀘스트를 확인할 수 있습니다. | 매일 출석 체크를 진행할 수 있습니다. |

| 인증 | 대시보드 |
|--------------|----------------|
| ![BUNOUT_인증](https://github.com/user-attachments/assets/5a0b080e-b283-4a5d-80bf-61a31a58f884) | ![BUNOUT_대시보드](https://github.com/user-attachments/assets/ce4e1c66-3071-4ee6-9c9b-082d5ac3174b) |
| 퀘스트 수행 후 인증 사진 업로드를 통해 경험치 획득이 가능합니다. | 날짜별 완료 퀘스트 목록 및 주간 달성률 확인이 가능합니다. |






    

