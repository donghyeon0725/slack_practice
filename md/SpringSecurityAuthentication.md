📌 DB 인증
-
* 비밀번호 암호화 
* [패스워드 인코더](../src/main/java/com/slack/slack/appConfig/security/common/BeanFactory.java)
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
}
```
* encode와 matches 두가지 메소드를 지원
* 암호화 포멧 : "{포멧}encodedPassword"



<br/>


📌 WebIgnore
-
* static 자원의 경우에도 기본적으로 스프링 시큐리티가 인증을 요구함. 따라서 인증을 별도로 관리할 필요가 있음
```java
@Override
public void configure(WebSecurity web) throws Exception {
    web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
}
```
* PathRequest 의 atCommonLocations 메소드 내부의 StaticResourceLocation 를 열어보면 다음과 같은 경로를 정의하고 있음
```java
CSS(new String[]{"/css/**"}),
JAVA_SCRIPT(new String[]{"/js/**"}),
IMAGES(new String[]{"/images/**"}),
WEB_JARS(new String[]{"/webjars/**"}),
FAVICON(new String[]{"/**/favicon.ico"});
```
* 따라서 이미지 파일의 경우 images 폴더 아래에 정의해야 WebIgnore 설정에 정의된 대로 Filter 를 통과 할 수 있음
* permitAll 과는 다르게, FilterSecurityInterceptor (인가) 자체를 거치지 않는다는 점이 다르다.


<br/>


📌 필드 카피를 위한 준비
-
* ModelMapper 라이브러리가 있다.
```xml
<dependency>
    <groupId>org.modelmapper</groupId>
    <artifactId>modelmapper</artifactId>
    <version>2.3.0</version>
</dependency>
```
* 다음과 같이 사용
```java
ModelMapper modelMapper = new ModelMapper();
Account account = modelMapper.map(accountDTO, Account.class);
```
* accountDTO 의 필드 중 이름이 같은 필드 값을 새로운 Account 클래스로 옮겨 반환



<br/>

📌 스프링 시큐리티 DB 연동 인증 처리
-
* UserDetailsService 를 커스텀 하여, DB 와 계정을 연동하도록 설정할 수 있음
    * 이 때 UserDetails 객체를 반환해야함
![default](./img/995ca80b1be44403b5d1a8483550c451.png)
* UserDetailsService 를 구현하고 UserDetails(이미 구현한 User 를 상속받는게 편하다) 를 구현한 클래스를 설정 파일에 등록해서 연동 가능

> UserDetailsService

* [AccountDetailsServiceImpl.java](../src/main/java/com/slack/slack/appConfig/security/service/AccountDetailsServiceImpl.java)

> UserDetails

* [AccountContext.java](../src/main/java/com/slack/slack/appConfig/security/service/AccountContext.java)

> configure 등록
* 커스텀한 service 를 시스템이 사용하도록 설정 등록
```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService);
}
```
