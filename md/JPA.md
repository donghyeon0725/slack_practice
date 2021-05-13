📌 JPA란?
-

Java Persistence API의 약자
우리는 JPA를 구현한 구현체를 가지고 개발을 한다.   

* JPA => 자바 ORM 기술에 대한 API 표준 명세, EntityManager를 통해서 CRUD처리   
* Hibernate => JPA의 구현체, 인터페이스를 직접 구현한 라이브러리   
* Spring Data JPA => spring module, JPA를 추상화한 Repository 인터페이스 사용   
* JDBC 는 DB와 자바가 통신을 하기 위한 드라이버 클래스라고 하면, 그 위에 Hibernate가 작성 되어 있고, 한단계 위에서 JPA 라는 표준을 정의했고 해당 부분을 구현한 Spring Data JPA 를 사용할 수 있다.
* 작업하다보면 공통적으로 사용하게 되는 부분이 CRUD 인데 그런 부분을 쉽게 사용할 수 있도록 이미 구현 되어 있는 것이다.



<br/>

📌 시작하기
-

* 디팬던시 추가
```xml
<!-- spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- h2 -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```
Jpa는 DB와 자바 Entity 관계를 맺어주는 프레임 워크이다. 
h2는 DB 대신 임시용도로 사용할 메모리 DB이다.


* 로그 출력을 위한 설정 추가
```yml
spring:
  jpa:
    show-sql: true
  h2:
    console:
      enabled: true

  datasource:
    url: jdbc:h2:mem:testdb
```
jdbc:h2:mem:testdb 의 mem은 Memory DB에 저장됨을 의미하고, 어플리케이션 동작 중에만 저장되는 DB이다.


<br/>

📌 스프링 시큐리티를 사용하는 경우
-
스프링 시큐리티를 사용하는 경우, h2 database와의 연결이 원활 하지 않을 수 있다.
```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    // /h2-console/** 으로 오는 모든 요청 허용
    http.authorizeRequests().antMatchers("/h2-console/**").permitAll();
    // cross site scripting disable 하는 기능
    http.csrf().disable();
    http.headers().frameOptions().disable();
}
```
위와 같은 설정으로 시큐리티 설정을 일부 허용해주면 됨


📌 엔티티 클래스 생성하기
-
* [엔티티 클래스 생성해서 관리하는 방법](Entity.md)

