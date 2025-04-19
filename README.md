# 🧑‍💻백엔드 개발 과제(Java) - 김창현

## 📑목차
- [🔧1. 기술 스택](#1-기술-스택)
- [🖥️2. 실행 방법]()
- [📋3. 요구사항](#3-요구사항)
- [⚙️4. 기능구현](#4-기능구현)
- [🔗5. GitHubRepository 링크](#5-github-링크)
- [🔍6. Swagger UI 주소](#6-swagger-ui--주소)
- [🧾7. API 명세서](#7-api-명세서)

## 🔧1. 기술 스택
- Java 17
- Spring Boot
- Spring Security
- JWT
- JUnit 5
- Swagger
- AWS EC2
- Docker, Docker Compose

## 🖥️2. 실행 방법
1. 회원가입을 진행합니다.
2. 로그인을 진행합니다.
3. 유저 - 모든 유저 정보 조회를 누릅니다.(토큰 검증 실패)
4. Authorize 버튼을 통해 토큰을 입력합니다.
5. 유저 - 모든 유저 정보 조회를 누릅니다.(성공)
6. 본인에게(5번에서 조회한 id 값으로) 관리자 권한을 부여합니다.(권한 부족)
7. 테스트용 관리자 생성 API를 실행합니다.
8. admin/admin으로 로그인합니다. (Authorize 토큰 입력까지)
9. 1번에서 가입한 계정에 관리자 권한을 부여합니다.(성공)
10. 모든 유저 정보를 조회합니다.(권한 확인)

## 📋3. 요구사항
**과제 : Spring Boot 기반 JWT 인증/인가 및 AWS 배포**

1️⃣ **Spring Boot**를 이용하여 JWT 인증/인가 로직과 API를 구현한다.

2️⃣ **Junit** 기반의 테스트 코드를 작성한다.

3️⃣ **Swagger** 로 API를 문서화 한다.

4️⃣ 애플리케이션을 **AWS EC2**에 배포하고, 실제 환경에서 실행되도록 구성한다.

### 🎯목표
- **사용자 인증 시스템**을 구축합니다. (회원가입, 로그인)
- **JWT(Json Web Token) 기반 인증 메커니즘**을 구현하여 보안성을 강화합니다.
- **역할(Role) 기반 접근 제어**를 적용하여 관리자(Admin) 권한이 필요한 API를 보호합니다.

### ✅체크리스트
- [x]  유저(User) 및 관리자(Admin) 회원가입, 로그인 API를 개발합니다.
- [x]  JWT를 이용하여 Access Token을 발급하고 검증하는 로직을 적용합니다.
- [x]  일반 사용자(User)와 관리자(Admin) 역할(Role)을 구분하여 특정 API 접근을 제한합니다.
    - 예) 관리자 권한 부여 API
- [x] 각 API 엔드포인트 별 올바른 입력과 잘못된 입력에 대해 테스트 케이스를 작성합니다.
- [x]  Swagger (또는 OpenAPI) 스펙을 기반으로 API 문서화 도구를 프로젝트에 추가합니다.
- [x]  각 API에 대한 설명, 파라미터, 요청/응답 예시 등을 Swagger UI에 등록하여 브라우저에서 쉽게 확인할 수 있도록 합니다.
- [x]  AWS EC2 인스턴스를 생성하고 기본 환경을 설정합니다. (보안 그룹, JDK 설치 등)
- [x]  다음 두 가지 방법 중 하나를 선택하여 애플리케이션을 배포합니다.
    - 사용 방법: 빌드된 JAR 파일 직접 업로드하기
        - 로컬에서 빌드한 JAR 파일을 EC2 인스턴스로 전송합니다.
- [x]  애플리케이션을 실행하고 외부에서 접근 가능하도록 구성합니다. (java -jar 명령어 사용)
- [ ] (선택 사항) Nginx를 리버스 프록시로 설정하여 요청을 애플리케이션으로 전달합니다.


## ⚙️4. 기능구현

### 🗂️프로젝트 구조
- domain: 도메인 로직(Entity, Service, Repository)
- common: 환경 설정, 예외처리 등 공통 처리
- adaptor: 입/출력 담당(dto, controller)

해당 구조를 통해 business 로직과 presentation 을 분리하고자 하였습니다.

### 🚫프로젝트 구현 제외 상황
- 많은 기능적 요구가 없었기에 현재 시점에서 필요하지 않은 것들은 뺀 것들이 많이 있습니다.
    - ex) 로그인 시 refresh 토큰은 포함하지 않고 access 토큰만 전달(refresh 토큰은 생성됨)
- 테스트용 Admin 계정 생성과 모든 유저 목록을 조회하는 API를 추가하였습니다.
  - 해당 API는 테스트나 swagger 명세에 자세하게 기재하지 않았습니다.

### 🔐사용자 인증 시스템
#### 회원가입
1. 회원가입 시도
2. 비밀번호는 암호화 하여 회원 정보 등록

#### 로그인
1. 로그인 시도
2. Security에서 사용자 정보 검증
3. Access 토큰 반환

### 🛡️관리자 권한 부여
1. 관리자 계정 로그인
2. 관리자로 등록하고자 하는 회원의 id를 입력하여 관리자 권한 부여 API 호출
3. Security에서 권한 확인
4. 관리자 권한 부여 성공

- 예시에 List 형식으로 Role이 들어가 있기에 USER와 Admin 역할이 모두 있어야 하고, Admin이 존재한다면 관리자 권한을
실행할 수 있다고 판단하여 Member클래스에 List형식으로 Role 필드를 구성했습니다.

### 🔄공통 케이스
- 토큰 검증 후 Security ContextHolder에 사용자 정보 등록(UserDetail을 implements한 MemberDetail과 UserDetailsService를
implements한 MemberDetailService를 통해 유저 정보를 등록해서 사용)하여 사용했습니다.
- 입력 검증 로직은 생략하였습니다(valid).

### 📘Swagger 문서화
- 테스트용 API에는 간략한 설명 정도만 작성해 두었습니다. 따로 파라미터가 필요 없고, Execute만 누르면 됩니다.

### 🧪테스트 코드
- Spring Security의 권한 검증 로직을 테스트하기 위해 AdminMemberService를 상속한 테스트용 서브 클래스를 작성했습니다.
  - grantAdminToMember 메서드를 오버라이딩하여, @PreAuthorize 대신 SecurityContextHolder를 활용한 권한 검증 로직을 직접 테스트할 수 있도록 구성했습니다.

### 🚀배포
- GitActions의 자동배포를 이용하여 배포를 하고자 하였으나, 시간적 여유가 없어 생략했습니다.
- EC2에 docker-compose를 활용하여 spring Application을 실행시켰습니다. (Nginx 사용 x)
- 탄력적 IP를 부여하여 고정된 IP를 사용하도록 설정하였습니다.

## 🔗5. GitHub 링크
🌐 https://github.com/chk223/task

## 🔍6. Swagger UI  주소
🌐 http://52.79.68.62:8080/swagger-ui/index.html

## 🧾7. Api 명세서
### 관리자
- /admin/users
  - 관리자 계정을 생성합니다.(테스트용)
  - 1번만 계정을 생성할 수 있습니다. (예외처리 해놔서 여러번 작동 안합니다)
  - username: admin / password: admin 으로 로그인 가능합니다.
- /admin/users/{userId}/roles
  - 관리자 권한을 부여합니다.
### 유저
- /users
  -모든 유저의 정보를 조회합니다.(테스트용)
### 회원인증
- /auth/signup
  - 회원가입을 진행합니다.
- /auth/login
  - 로그인을 진행합니다.