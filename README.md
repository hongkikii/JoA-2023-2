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
  - 회원, 위치, 하트, 투표
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
<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/d1a3947e-fc64-492d-bd36-8d1cee69b827" width="200" height="400"/>

1. [서버에서 학교 웹메일로 전송한 인증번호를 통해 해당 학교의 학생인지를 검증한다.](https://github.com/hongkikii/JoA-2023-2/blob/main/src/main/java/com/mjuAppSW/joA/domain/member/controller/CertifyApiController.java)

<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/273b9948-1361-432c-9de7-657f1488fffd" width="200" height="400"/>

2. [아이디 중복 검증, 아이디/비밀번호 유효성 검증을 거쳐 회원 가입을 완료한다.](https://github.com/hongkikii/JoA-2023-2/blob/main/src/main/java/com/mjuAppSW/joA/domain/member/controller/JoinApiController.java)

### [계정 관리](https://github.com/hongkikii/JoA-2023-2/blob/main/src/main/java/com/mjuAppSW/joA/domain/member/controller/AccountApiController.java)

<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/12b2f8e2-ccdf-4bc4-aff4-64765f298fbe" width="200" height="400"/>

1. 사용자는 아이디, 비밀번호를 통해 로그인 할 수 있다.

<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/2dfd982a-d934-4de3-8a67-442fc327226b" width="200" height="400"/>

2. 사용자는 아이디를 잊어버렸을 시, 학교 웹메일로 아이디를 전송받을 수 있다.

<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/1f67a684-8589-43d9-a77e-06102019d75f" width="200" height="400"/>

3. 사용자는 비밀번호를 잊어버렸을 시, 학교 웹메일로 임시 비밀번호를 전송받아 사용할 수 있다.

<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/fa1a8c96-38ed-42d9-b18f-cd332bf04d50" width="200" height="400"/>
<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/3eb2e88e-8555-4c11-843a-c1e1ba8ecfc9" width="200" height="400"/>

4. 사용자는 비밀번호를 변경할 수 있다.

<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/a3b9f37a-170f-45c6-ad66-2be2e3fb5457" width="200" height="400"/>

5. 사용자는 로그아웃을 할 수 있다.

<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/c2e8237c-cc5b-4b78-9018-6654262e1a81" width="200" height="400"/>

6. 사용자는 탈퇴를 할 수 있다.

### [회원 정보](https://github.com/hongkikii/JoA-2023-2/blob/main/src/main/java/com/mjuAppSW/joA/domain/member/controller/InfoApiController.java)

<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/819b6ecf-8072-4d4a-9ab1-5a6c38cf3b02" width="200" height="400"/>
<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/a17ffd9f-3348-4f58-90ad-c173e4faaab1" width="200" height="400"/>

1. 사용자는 프로필 사진을 변경하거나 삭제할 수 있다.

<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/d6c70a7b-01fe-4695-b4f5-c427ef36fbd2" width="200" height="400"/>
<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/8f59836f-2432-4291-b089-f7dbd91ffdc6" width="200" height="400"/>
<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/46d2cf4b-7bd6-4c59-90d7-93ba66a7404b" width="200" height="400"/>


2. 사용자는 한 줄 소개를 변경하거나 삭제할 수 있다.

<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/d89b3663-6be8-4213-b808-f3ebec35b712" width="200" height="400"/>


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

<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/b64d8f39-2c9c-49ab-8cbf-0b4e831fd47d" width="200" height="400"/>

1. 사용자는 자신이 원하는 때 위치 업데이트를 할 수 있다.

### 주변 친구 목록

<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/b64d8f39-2c9c-49ab-8cbf-0b4e831fd47d" width="200" height="400"/>

1. [사용자는 학교 내에 위치할 시 주변 친구 목록을 불러올 수 있다.](https://github.com/hongkikii/JoA-2023-2/blob/main/src/main/java/com/mjuAppSW/joA/geography/location/controller/LocationApiController.java)

<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/1253db21-2c42-47f6-9bf5-bf91eb1c21d8" width="200" height="400"/>

2. [사용자는 주변 친구 목록에서 보고 싶지 않은 사용자를 차단할 수 있다.](https://github.com/hongkikii/JoA-2023-2/blob/main/src/main/java/com/mjuAppSW/joA/geography/block/controller/BlockApiController.java)

</div>
</details>

<details>
<summary>하트</summary>
<div markdown="1">

## 하트

### [하트 전송](https://github.com/hongkikii/JoA-2023-2/blob/main/src/main/java/com/mjuAppSW/joA/domain/heart/controller/HeartApiController.java)

<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/be8dc431-7fb2-41da-92d7-b549ace5fef6" width="200" height="400"/>
<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/2c178d0a-e9c3-499d-b891-a6f9741ba8cf" width="200" height="400"/>


1. 사용자는 익명 혹은 실명으로 다른 사용자에게 매일 한 번 하트를 보낼 수 있다.
</div>
</details>

<details>
<summary>투표</summary>
<div markdown="1">

## 투표

### 투표 전송

<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/5eae2970-1ad1-4de4-9e5f-ee3b6399afa5" width="200" height="400"/>
<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/058635aa-8f3f-4b01-9437-2bc1c5e3d703" width="200" height="400"/>
<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/fbd8a92e-71ed-4bea-a66d-599d2f37ae09" width="200" height="400"/>

1. [사용자는 다른 사용자에게 카테고리별로 매일 한 번 투표할 수 있다.](https://github.com/hongkikii/JoA-2023-2/blob/main/src/main/java/com/mjuAppSW/joA/domain/vote/controller/VoteApiController.java)

<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/1529eb97-6f4a-411c-a296-cf46946194be" width="200" height="400"/>

2. 투표 전송 시 코멘트를 첨부할 수 있다.

<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/a627c191-6779-410b-8f48-0daf1a15a3b1" width="200" height="400"/>


3. [사용자는 자신이 받은 투표 목록을 확인할 수 있다.](https://github.com/hongkikii/JoA-2023-2/blob/main/src/main/java/com/mjuAppSW/joA/domain/vote/controller/VoteApiController.java)

<img src="https://github.com/hongkikii/JoA-2023-2/assets/110226866/4fc1eaa0-ca77-4461-ab85-4c959c0bc589" width="200" height="400"/>

4. [투표 수신자는 코멘트를 확인하고 투표를 신고할 수 있다.](https://github.com/hongkikii/JoA-2023-2/blob/main/src/main/java/com/mjuAppSW/joA/domain/voteReport/controller/VoteReportApiController.java)
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
- 위와 같은 이유로 알림 기능이 필요하다 판단

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
