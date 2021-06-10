스프링 부트 연습을 위한 애플리케이션
-

환경
-
* 스프링 부트 2.4.5
* h2 database (테스트 용도)
* spring data jpa



<br/>

플러그인
-
* 롬복


<br/>


실행
-
1. 개발 모드로 변경
```text
Active Profiles 를 "dev" 모드로 실행하셔야 합니다.
```

2. 이메일 연동을 위해, 자신의 구글 이메일 계정 정보 입력
```text
application-dev.yml 파일의 "# 메일 관련 설정" 부분에 자신의 계정 & 비밀번호 (메일 연동 서비스)를 입력
```

3. 메이븐 의존성 다운로드
```text
mvn clean
mvn install
```

4. 어플리케이션 실행
```text
spring-boot:run
```



<br/>

기술 설명
-
1. [필터](md/Filter.md)
2. [에러 핸들러](md/ErrorHandler.md)
3. [Entity](md/Entity.md)
4. [메일 시스템](md/Mailing.md)
5. [메일 엔진으로서 머스타치](md/Mustache.md)
6. [Validation Check](md/validationCheck.md)
7. [다국어 처리](md/MultiLanguage.md)
8. [리턴 포멧 xml 지원](md/ReturnFormat.md)
9. [필드값 필터링 처리](md/FieldFiltering.md)
10. [JPA 연동하기](md/JPA.md)
11. [스프링 시큐리티](md/SpringSecurity.md)
12. [스프링 시큐리티 jwt 토큰으로 관리하기](md/SpringSecurityWithJWT.md)
13. [swagger 연동하기](md/Swagger.md)
14. [모니터링 시스템 구축하기](md/Monitoring.md)
15. [이벤트 기반 프로그래밍을 위한 이벤트 핸들러와 이벤트](md/EventHandler.md)

📌 추가할 내용 
-
1. [X] 필터
2. [X] 에러 핸들러
3. [X] Entity
4. [X] 메일 시스템
5. [X] 메일 엔진으로서 머스타치
6. [X] Validation Check
7. [X] 다국어 처리하기 - yml 파일로 처리하기
    * User 관련 Business 로직 작성하기
8. [X] 리턴 포멧 xml 지원하기
9. [X] 리턴값 filtering 하기 
10. [X] JPA 연동하기
11. [X] 스프링 시큐리티
12. [X] 스프링 시큐리티 jwt 토큰으로 관리하기
13. [X] swagger 연동하기
14. [X] 모니터링 시스템 구축하기 (actuator + Hal Browser)
15. [X] 이벤트 기반 프로그래밍


<br/>

* DTO와 Entity를 분리해야하는 이유 : <https://velog.io/@gillog/Entity-DTO-VO-%EB%B0%94%EB%A1%9C-%EC%95%8C%EA%B8%B0>



<br/>


📌 Version
-
* 1.0.0 Version [2021-06-10]
