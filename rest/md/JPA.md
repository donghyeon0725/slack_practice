๐ JPA๋?
-

Java Persistence API์ ์ฝ์
์ฐ๋ฆฌ๋ JPA๋ฅผ ๊ตฌํํ ๊ตฌํ์ฒด๋ฅผ ๊ฐ์ง๊ณ  ๊ฐ๋ฐ์ ํ๋ค.   

* JPA => ์๋ฐ ORM ๊ธฐ์ ์ ๋ํ API ํ์ค ๋ช์ธ, EntityManager๋ฅผ ํตํด์ CRUD์ฒ๋ฆฌ   
* Hibernate => JPA์ ๊ตฌํ์ฒด, ์ธํฐํ์ด์ค๋ฅผ ์ง์  ๊ตฌํํ ๋ผ์ด๋ธ๋ฌ๋ฆฌ   
* Spring Data JPA => spring module, JPA๋ฅผ ์ถ์ํํ Repository ์ธํฐํ์ด์ค ์ฌ์ฉ   
* JDBC ๋ DB์ ์๋ฐ๊ฐ ํต์ ์ ํ๊ธฐ ์ํ ๋๋ผ์ด๋ฒ ํด๋์ค๋ผ๊ณ  ํ๋ฉด, ๊ทธ ์์ Hibernate๊ฐ ์์ฑ ๋์ด ์๊ณ , ํ๋จ๊ณ ์์์ JPA ๋ผ๋ ํ์ค์ ์ ์ํ๊ณ  ํด๋น ๋ถ๋ถ์ ๊ตฌํํ Spring Data JPA ๋ฅผ ์ฌ์ฉํ  ์ ์๋ค.
* ์์ํ๋ค๋ณด๋ฉด ๊ณตํต์ ์ผ๋ก ์ฌ์ฉํ๊ฒ ๋๋ ๋ถ๋ถ์ด CRUD ์ธ๋ฐ ๊ทธ๋ฐ ๋ถ๋ถ์ ์ฝ๊ฒ ์ฌ์ฉํ  ์ ์๋๋ก ์ด๋ฏธ ๊ตฌํ ๋์ด ์๋ ๊ฒ์ด๋ค.



<br/>

๐ ์์ํ๊ธฐ
-

* ๋ํฌ๋์ ์ถ๊ฐ
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
Jpa๋ DB์ ์๋ฐ Entity ๊ด๊ณ๋ฅผ ๋งบ์ด์ฃผ๋ ํ๋ ์ ์ํฌ์ด๋ค. 
h2๋ DB ๋์  ์์์ฉ๋๋ก ์ฌ์ฉํ  ๋ฉ๋ชจ๋ฆฌ DB์ด๋ค.


* ๋ก๊ทธ ์ถ๋ ฅ์ ์ํ ์ค์  ์ถ๊ฐ
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
jdbc:h2:mem:testdb ์ mem์ Memory DB์ ์ ์ฅ๋จ์ ์๋ฏธํ๊ณ , ์ดํ๋ฆฌ์ผ์ด์ ๋์ ์ค์๋ง ์ ์ฅ๋๋ DB์ด๋ค.


<br/>

๐ ์คํ๋ง ์ํ๋ฆฌํฐ๋ฅผ ์ฌ์ฉํ๋ ๊ฒฝ์ฐ
-
์คํ๋ง ์ํ๋ฆฌํฐ๋ฅผ ์ฌ์ฉํ๋ ๊ฒฝ์ฐ, h2 database์์ ์ฐ๊ฒฐ์ด ์ํ ํ์ง ์์ ์ ์๋ค.
```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    // /h2-console/** ์ผ๋ก ์ค๋ ๋ชจ๋  ์์ฒญ ํ์ฉ
    http.authorizeRequests().antMatchers("/h2-console/**").permitAll();
    // cross site scripting disable ํ๋ ๊ธฐ๋ฅ
    http.csrf().disable();
    http.headers().frameOptions().disable();
}
```
์์ ๊ฐ์ ์ค์ ์ผ๋ก ์ํ๋ฆฌํฐ ์ค์ ์ ์ผ๋ถ ํ์ฉํด์ฃผ๋ฉด ๋จ


๐ ์ํฐํฐ ํด๋์ค ์์ฑํ๊ธฐ
-
* [์ํฐํฐ ํด๋์ค ์์ฑํด์ ๊ด๋ฆฌํ๋ ๋ฐฉ๋ฒ](Entity.md)

