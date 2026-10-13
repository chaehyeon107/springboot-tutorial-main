# CS Fantasy Item Shop Backend

## 1. 프로젝트 개요

본 프로젝트는 **전북대학교 「웹서비스 설계」 과목의 Backend Framework Practice Assignment**를 위해 진행한 **Spring Boot 백엔드 실습 프로젝트**이다.

CS Fantasy Item Shop(컴공 판타지 아이템 쇼핑몰)은 컴퓨터공학(Computer Science)의 개념과 개발자 문화를 **판타지 RPG 아이템 상점** 콘셉트로 재해석한 서비스이다.

개발자를 게임 속 플레이어로 설정하고, 컴퓨터공학과 관련된 개념을 아이템으로 표현하였다.

예를 들어 다음과 같은 아이템을 사용한다.

- 마법 스택
- 알고리즘 스킬북
- 메모리 갑옷
- 디버깅 포션
- NullPointer 방패
- 무한루프의 반지

컴퓨터공학 개념과 개발자 밈(meme)을 게임적인 메타포로 표현하여 단순한 CRUD 실습에 프로젝트만의 콘셉트를 더하였다.

본 프로젝트에서는 Spring Boot를 이용하여 HTTP Method별 REST API를 구현하고, 다양한 HTTP Response Code 처리, 공통 응답 포맷 적용, Middleware 역할의 Interceptor 구현 등을 실습하는 것을 목표로 한다.

---

## 2. 과제 구현 내용

본 프로젝트에서는 과제 요구사항에 따라 다음 기능을 구현하였다.

- GET API 2개
- POST API 2개
- PUT API 2개
- DELETE API 2개
- 총 8개의 REST API 구현
- `LoggingInterceptor`를 이용한 요청 로그 출력
- 다양한 2xx, 4xx, 5xx HTTP Response Code 처리
- `ItemResponse<T>`를 이용한 공통 응답 형식 적용
- Postman을 이용한 API 실행 및 테스트

---

## 3. 기술 스택

| 구분 | 사용 기술 |
| --- | --- |
| Language | Java |
| Framework | Spring Boot |
| Web | Spring MVC |
| Data Storage | Java `HashMap` 기반 In-Memory Store |
| Middleware | Spring `HandlerInterceptor` |
| API Test | Postman |
| Build | Spring Boot Project |

본 실습에서는 별도의 데이터베이스를 연결하지 않고 `HashMap`을 임시 저장소로 사용하였다.

따라서 **서버를 종료하거나 재시작하면 저장된 아이템 데이터가 초기화된다.**

---

## 4. 프로젝트 구조

```text
src/main/java/com/example/ksl/wsd2025
├── api
│   ├── controller
│   │   └── ItemController.java
│   ├── dto
│   │   ├── ItemDto.java
│   │   └── ItemResponse.java
│   ├── enums
│   │   └── Rarity.java
│   └── request
│       ├── ItemCreateRequest.java
│       └── ItemUpdateRequest.java
│
└── config
    ├── LoggingInterceptor.java
    └── WebConfig.java
```

---

## 5. 실행 방법

### 5-1. 프로젝트 실행

Spring Boot Application을 실행한다.

IDE에서 Application의 `main()` 메서드를 실행하거나 다음 명령어를 사용할 수 있다.

```bash
./gradlew bootRun
```

Windows 환경에서는 다음과 같이 실행할 수 있다.

```bash
gradlew.bat bootRun
```

서버가 정상적으로 실행되면 기본적으로 다음 주소를 사용한다.

```text
http://localhost:8080
```

Item API의 Base URL은 다음과 같다.

```text
http://localhost:8080/api/items
```

---

## 6. API 엔드포인트

과제 요구사항에 따라 POST, GET, PUT, DELETE를 각각 2개씩 구현하였다.

| Method | Endpoint | 설명 |
| --- | --- | --- |
| GET | `/api/items` | 전체 아이템 조회 |
| GET | `/api/items/{id}` | 특정 아이템 단건 조회 |
| POST | `/api/items` | 새로운 아이템 생성 |
| POST | `/api/items/with-header` | Header 정보를 포함한 아이템 생성 요청 |
| PUT | `/api/items/{id}` | 특정 아이템 정보 수정 |
| PUT | `/api/items/{id}/stock` | 특정 아이템 재고 수량 변경 |
| DELETE | `/api/items/{id}` | 특정 아이템 삭제 |
| DELETE | `/api/items/out-of-stock` | 재고가 없는 품절 아이템 일괄 삭제 |

총 **8개의 API**를 구현하였다.

---

## 7. Item 데이터 구조

아이템은 다음과 같은 정보를 가진다.

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `id` | Long | 아이템 식별자 |
| `name` | String | 아이템 이름 |
| `price` | Integer | 아이템 가격 |
| `description` | String | 아이템 설명 |
| `itemRarity` | Rarity | 아이템 등급 |
| `stock_quantity` | Integer | 재고 수량 |

예시 데이터:

```json
{
  "name": "마법 스택",
  "price": 1500,
  "description": "함수 호출과 지역 변수를 차곡차곡 쌓아두는 개발자의 마법 도구",
  "itemRarity": "COMMON",
  "stock_quantity": 20
}
```

---

## 8. 공통 응답 포맷

모든 API 응답은 과제 요구사항에 따라 `ItemResponse<T>`를 이용해 동일한 형태로 반환하도록 구현하였다.

### 성공 응답

```json
{
  "status": "success",
  "data": {
    "id": 1,
    "name": "마법 스택",
    "price": 1500,
    "description": "함수 호출과 지역 변수를 차곡차곡 쌓아두는 개발자의 마법 도구",
    "itemRarity": "COMMON",
    "stock_quantity": 20
  }
}
```

### 실패 응답

```json
{
  "status": "error",
  "data": {
    "message": "해당 아이템을 찾을 수 없습니다."
  }
}
```

이를 통해 API별로 서로 다른 형태의 응답을 반환하지 않고 다음 형식을 공통으로 유지한다.

```json
{
  "status": "success 또는 error",
  "data": {
  }
}
```

---

## 9. HTTP Response Code 처리

다양한 요청 상황을 확인하기 위해 2xx, 4xx, 5xx 범주의 HTTP Response Code를 처리하였다.

### 2xx Success

| 코드 | 의미 | 사용 예 |
| --- | --- | --- |
| `200 OK` | 요청 정상 처리 | 조회, 수정, 삭제 성공 |
| `201 Created` | 리소스 생성 성공 | 아이템 생성 |
| `202 Accepted` | 요청 접수 | 비동기 처리 상황 실습 |
| `203 Non-Authoritative Information` | 가공 또는 캐시 데이터 응답 | 단건 조회 실습 |
| `206 Partial Content` | 일부 데이터 반환 | 조회 개수 제한 |

### 4xx Client Error

| 코드 | 의미 | 사용 예 |
| --- | --- | --- |
| `400 Bad Request` | 잘못된 요청 | 잘못된 ID, 음수 가격 및 재고 |
| `401 Unauthorized` | 인증 정보 없음 | Authorization Header 누락 |
| `403 Forbidden` | 권한 부족 | 관리자 Header 누락 |
| `404 Not Found` | 리소스 없음 | 존재하지 않는 Item ID |
| `409 Conflict` | 데이터 충돌 | 동일한 아이템 이름 중복 등록 |
| `422 Unprocessable Entity` | 요청값 처리 불가 | 허용 범위를 초과한 조회 제한값 |

### 5xx Server Error

| 코드 | 의미 | 사용 예 |
| --- | --- | --- |
| `500 Internal Server Error` | 서버 내부에서 예상하지 못한 오류 발생 | 예외 처리 |
| `503 Service Unavailable` | 서비스 이용 불가 | 서비스 장애 상황 처리 |

`500 Internal Server Error`는 Controller 내부에서 예상하지 못한 Exception이 발생할 경우 `catch`를 통해 처리하도록 구현하였다.

`503 Service Unavailable`은 실습 환경에서 서비스 장애 상황을 확인할 수 있도록 `serviceDown` 요청 파라미터를 이용해 테스트할 수 있도록 구성하였다.

예:

```text
GET /api/items?serviceDown=true
```

---

## 10. Middleware 구현

Spring MVC의 `HandlerInterceptor`를 이용하여 `LoggingInterceptor`를 구현하였다.

Interceptor는 `/api/**` 경로로 들어오는 요청에 대해 Controller 실행 전후에 로그를 출력한다.

### 요청 시작 로그

다음 정보를 출력한다.

- HTTP Method
- Request URI
- Query String

예:

```text
[LoggingInterceptor] 요청 시작: GET /api/items/1
```

### 요청 완료 로그

Controller의 처리가 완료되면 HTTP Response Status를 출력한다.

```text
[LoggingInterceptor] 요청 완료, status=200
```

404 응답의 경우:

```text
[LoggingInterceptor] 요청 시작: GET /api/items/999999
[LoggingInterceptor] 요청 완료, status=404
```

이를 통해 공통 요청 로깅 기능을 Controller마다 반복해서 작성하지 않고 Middleware 역할의 Interceptor에서 처리하였다.

---

## 11. Postman API 테스트

API 실행 및 테스트에는 **Postman**을 사용하였다.

프로젝트에 작성한 Postman Collection을 Import하여 다음 항목을 테스트할 수 있다.

### 주요 테스트 항목

- 정상적인 Item 생성
- 전체 Item 조회
- Item 단건 조회
- Item 정보 수정
- Item 재고 수정
- Item 삭제
- 품절 Item 일괄 삭제
- Header 전달 테스트
- 잘못된 입력값에 대한 4xx 응답
- 존재하지 않는 Item에 대한 404 응답
- 중복 Item에 대한 409 응답
- 서비스 장애 상황에 대한 503 응답
- 공통 `status`, `data` 응답 포맷 확인

Postman 테스트에서는 응답 Status Code뿐만 아니라 공통 응답 형식도 함께 확인한다.

예:

```javascript
pm.test("공통 응답 포맷 status/data 확인", function () {
    const json = pm.response.json();

    pm.expect(json).to.have.property("status");
    pm.expect(json).to.have.property("data");
});
```

---

## 12. Postman 테스트용 아이템

API 테스트에는 프로젝트 콘셉트에 맞는 다음 아이템을 사용하였다.

| 아이템 | 설명 |
| --- | --- |
| 마법 스택 | 함수 호출과 지역 변수를 쌓아두는 마법 도구 |
| 알고리즘 스킬북 | 정렬, 탐색, 그래프 알고리즘을 배우는 비전서 |
| 메모리 갑옷 | 메모리 누수와 포인터 공격을 막아주는 갑옷 |
| 디버깅 포션 | 버그를 찾을 때 집중력을 회복하는 포션 |
| NullPointer 방패 | NullPointerException 공격을 막아주는 방패 |
| 무한루프의 반지 | 끝없이 반복되는 제어문의 힘을 가진 반지 |
| Deprecated API 두루마리 | 더 이상 사용되지 않는 오래된 API가 기록된 두루마리 |

`무한루프의 반지`는 `stock_quantity = 0`으로 생성하여 품절 상품 일괄 삭제 API 테스트에 활용하였다.

---

## 13. 테스트 시 주의사항

현재 프로젝트는 실제 Database가 아닌 Java `HashMap`을 임시 저장소로 사용한다.

따라서 Spring Boot 서버를 다시 실행하면 다음 값들이 모두 초기화된다.

- Item 데이터
- Item ID Sequence

Postman 테스트를 처음부터 다시 실행하려면 서버를 재시작한 후 테스트 데이터를 다시 생성한다.

또한 `Rarity` 값은 프로젝트의 `Rarity.java` Enum에 정의된 값을 사용해야 한다.

---

## 14. 구현 목적

본 실습을 통해 다음 내용을 학습하는 것을 목표로 하였다.

- Spring Boot REST Controller 구현
- HTTP Method별 API 설계
- `@RequestBody` 사용
- `@PathVariable` 사용
- `@RequestParam` 사용
- `@RequestHeader` 사용
- `ResponseEntity`를 이용한 HTTP Response 처리
- 다양한 HTTP Status Code 활용
- DTO를 이용한 데이터 전달
- 공통 API Response 형식 설계
- Spring MVC Interceptor 구현
- Postman을 이용한 REST API 테스트

---

## 15. 프로젝트 한계

본 프로젝트는 Backend Framework 실습을 목적으로 한 간단한 프로젝트이므로 다음과 같은 한계가 있다.

- 실제 Database를 사용하지 않고 In-Memory `HashMap`을 사용한다.
- 서버 재시작 시 데이터가 초기화된다.
- 실제 회원 인증 시스템은 구현하지 않았다.
- 실제 비동기 처리 시스템은 구현하지 않았다.
- `503 Service Unavailable` 등의 일부 상황은 HTTP Response Code 실습을 위해 단순화하여 구현하였다.
- 실제 서비스 수준의 Service / Repository 계층 구조는 적용하지 않았다.

향후 실제 서비스로 확장할 경우 Database 연동, Service 및 Repository 계층 분리, Spring Validation, 전역 Exception Handler 등의 구조를 추가할 수 있다.

---

## 16. 과제 요구사항 확인

| 과제 요구사항 | 구현 여부 |
| --- | :---: |
| GET API 2개 | ✅ |
| POST API 2개 | ✅ |
| PUT API 2개 | ✅ |
| DELETE API 2개 | ✅ |
| 총 8개 API | ✅ |
| Middleware 구현 | ✅ |
| 2xx Response Code 다양성 | ✅ |
| 4xx Response Code 다양성 | ✅ |
| 5xx Response Code 처리 | ✅ |
| 공통 Response Format | ✅ |
| Postman API 테스트 | ✅ |

---

## 17. 제출 자료

본 과제 제출 자료는 다음과 같이 구성한다.

- GitHub Repository
    - Spring Boot 구현 코드
    - README
- Postman 실행 및 API 테스트 화면 캡처
