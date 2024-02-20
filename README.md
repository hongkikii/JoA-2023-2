# **💌 JoA**

**고백에 필요한 것은 용기가 아니라 “JoA”다**

Spring Boot + SwiftUI를 사용한 대학교 네트워킹 어플리케이션
<br>
<br>

# **💁🏻‍♀️ 프로젝트 소개**

강의실에서 도서관에서…

말 한 번 걸어보고 싶고 친해지고 싶었지만

다가가지 못해 아쉬웠던 기억, 혹시 있으신가요?

이제 JoA가 대신 용기내 드리려 해요!

앱에서 내 주변에 있는 친구들을 확인하고,

하트와 투표를 보내 마음을 전해보세요💘
<br>
<br>

# **⚙️ 개발 환경**

- `Java 17`, `Spring Boot 3.1.1`
- `SwiftUI`, `Swift 5.9`, `UIKit`, `Combine`
- MySQL, PostgreSQL
- AWS - Lightsail, RDS, S3, Route 53, Cloudfront
- [GitHub](https://github.com/hongkikii/JoA-2023-2), [Notion](https://www.notion.so/JoA-71b845898d3846d4a5a44578ded0d62e?pvs=21), Discord, Slack
<br>

# **👩‍👧‍👦 멤버와 역할**

### 홍향미
  - 프로젝트 리더, 백엔드
  - [회원](#회원), [위치](#위치), [하트](#하트), [투표](#투표)
  - Lightsail, S3, DNS
### 최종현
  - 백엔드
  - 채팅
  - RDS
### 최가의
  - 프론트엔드
  - UI/UX 디자인 및 구현
<br>

# **⏳ 진행 일정**

### 2023.06.26 - 2023.07.02

기능 정리, ERD&UI 프로토타입 생성

### 2023.07.03 - 2023.12.03

개발, 테스트, 앱스토어 심사

### 2023.12.04

앱스토어 등록 완료🥳

### 2023.12.04 - 2023.12.15

베타 테스트

### 2024.01.03 - 2024.03.01

리팩토링, 테스트, 앱스토어 재심사(예정)

### 2024.03.04

정식 출시🥳(예정)
<br>
<br>

# **🚀 기능**

<details>
<summary>회원</summary>
<div markdown="1">

## 회원

### 계정 생성

1. [서버에서 학교 웹메일로 전송한 인증번호를 통해 해당 학교의 학생인지를 검증한다.](https://github.com/hongkikii/JoA-2023-2/blob/main/src/main/java/com/mjuAppSW/joA/domain/member/controller/CertifyApiController.java)
2. [아이디 중복 검증, 아이디/비밀번호 유효성 검증을 거쳐 회원 가입을 완료한다.](https://github.com/hongkikii/JoA-2023-2/blob/main/src/main/java/com/mjuAppSW/joA/domain/member/controller/JoinApiController.java)

### [계정 관리](https://github.com/hongkikii/JoA-2023-2/blob/main/src/main/java/com/mjuAppSW/joA/domain/member/controller/AccountApiController.java)

1. 사용자는 아이디, 비밀번호를 통해 로그인 할 수 있다.
2. 아이디를 잊어버렸을 시, 학교 웹메일로 아이디를 전송받을 수 있다.
3. 비밀번호를 잊어버렸을 시, 학교 웹메일로 임시 비밀번호를 전송받아 사용할 수 있다.
4. 사용자는 로그아웃을 할 수 있다.
5. 사용자는 탈퇴를 할 수 있다.

### [회원 정보](https://github.com/hongkikii/JoA-2023-2/blob/main/src/main/java/com/mjuAppSW/joA/domain/member/controller/InfoApiController.java)

1. 사용자는 프로필 사진을 변경하거나 삭제할 수 있다.
2. 사용자는 한 줄 소개를 변경하거나 삭제할 수 있다.
3. 사용자는 계정 정보와 획득한 투표, 하트에 관한 정보를 확인할 수 있다.

### [회원 정지](https://github.com/hongkikii/JoA-2023-2/blob/main/src/main/java/com/mjuAppSW/joA/domain/member/service/StatusService.java)

1. 사용자는 5회 신고될 시 1일 계정 정지에 처해진다.
2. 사용자는 10회 신고될 시 7일 계정 정지에 처해진다.
3. 사용자는 15회 신고될 시 계정 영구 정지에 처해진다.
4. 영구 정지된 계정은 재가입이 불가능하다.

</div>
</details>

<details>
<summary>위치</summary>
<div markdown="1">

## 위치

### [위치 업데이트](https://github.com/hongkikii/JoA-2023-2/blob/main/src/main/java/com/mjuAppSW/joA/geography/location/controller/LocationApiController.java)

1. 사용자는 10m 이동할 때마다 자동으로 위치를 서버로 전송하고, 사용자의 위치는 업데이트 된다.
2. 사용자는 자신이 원하는 때 위치 업데이트를 할 수 있다.

### 주변 친구 목록

1. [사용자는 학교 내에 위치할 시 주변 친구 목록을 불러올 수 있다.](https://github.com/hongkikii/JoA-2023-2/blob/main/src/main/java/com/mjuAppSW/joA/geography/location/controller/LocationApiController.java)
2. [사용자는 주변 친구 목록에서 보고 싶지 않은 사용자를 차단할 수 있다.](https://github.com/hongkikii/JoA-2023-2/blob/main/src/main/java/com/mjuAppSW/joA/geography/block/controller/BlockApiController.java)

</div>
</details>

<details>
<summary>하트</summary>
<div markdown="1">

## 하트

### [하트 전송](https://github.com/hongkikii/JoA-2023-2/blob/main/src/main/java/com/mjuAppSW/joA/domain/heart/controller/HeartApiController.java)

1. 사용자는 익명 혹은 실명으로 다른 사용자에게 매일 한 번 하트를 보낼 수 있다.
</div>
</details>

<details>
<summary>투표</summary>
<div markdown="1">

## 투표

### 투표 전송

1. [사용자는 다른 사용자에게 카테고리별로 매일 한 번 투표할 수 있다.](https://github.com/hongkikii/JoA-2023-2/blob/main/src/main/java/com/mjuAppSW/joA/domain/vote/controller/VoteApiController.java)
2. 투표 전송 시 코멘트를 첨부할 수 있다.
3. [투표 수신자는 투표를 신고할 수 있다.](https://github.com/hongkikii/JoA-2023-2/blob/main/src/main/java/com/mjuAppSW/joA/domain/voteReport/controller/VoteReportApiController.java)
</div>
</details>

<details>
<summary>채팅</summary>
<div markdown="1">

...

</div>
</details>

<br>

# **✚ 추가 예정 기능**

<details>
<summary>알림</summary>
<div markdown="1">

### **기획 의도**

- 하트, 투표, 채팅을 보냈을 때 알림 기능이 있을 시 사용자들 간의 더욱 빠른 소통이 가능할 것으로 예측
- 실시간 소통은 더욱 활발한 기능 사용으로 이어질 것
- 뿐만 아니라 사용자들의 어플리케이션 사용 만족도를 높일 것이라 예상
- 위와 같은 이유로 알림 기능이 필요하다 판단!

### **사용 기술**

- FCM, APNs(Apple Push Notification Service)

### **담당자**

- 최종현, 최가의

### **진행 시기**

- 3월 내 구현 후 즉시 도입 예정

</div>
</details>

<br>

# 📄 문서

- 코드 컨벤션
- [커밋 컨벤션](https://velog.io/@rladpwl0512/Git-commit-%EB%A9%94%EC%8B%9C%EC%A7%80-%EC%BB%A8%EB%B2%A4%EC%85%98)
- [코드 리뷰 규칙](https://github.com/hongkikii/document/blob/main/guideline/%EC%BD%94%EB%93%9C_%EB%A6%AC%EB%B7%B0_%EA%B0%80%EC%9D%B4%EB%93%9C%EB%9D%BC%EC%9D%B8.md)

<br>

# ⚠️ 주의사항
해당 리포지토리는 프론트엔드 코드를 제공하지 않습니다.  
App Store에 ‘JoA’를 검색하여 어플리케이션을 직접 만나보실 수 있습니다!
