๐ DB ์ธ์ฆ
-
* ๋น๋ฐ๋ฒํธ ์ํธํ 
* [ํจ์ค์๋ ์ธ์ฝ๋](../src/main/java/com/slack/slack/appConfig/security/common/BeanFactory.java)
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
}
```
* encode์ matches ๋๊ฐ์ง ๋ฉ์๋๋ฅผ ์ง์
* ์ํธํ ํฌ๋ฉง : "{ํฌ๋ฉง}encodedPassword"



<br/>


๐ WebIgnore
-
* static ์์์ ๊ฒฝ์ฐ์๋ ๊ธฐ๋ณธ์ ์ผ๋ก ์คํ๋ง ์ํ๋ฆฌํฐ๊ฐ ์ธ์ฆ์ ์๊ตฌํจ. ๋ฐ๋ผ์ ์ธ์ฆ์ ๋ณ๋๋ก ๊ด๋ฆฌํ  ํ์๊ฐ ์์
```java
@Override
public void configure(WebSecurity web) throws Exception {
    web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
}
```
* PathRequest ์ atCommonLocations ๋ฉ์๋ ๋ด๋ถ์ StaticResourceLocation ๋ฅผ ์ด์ด๋ณด๋ฉด ๋ค์๊ณผ ๊ฐ์ ๊ฒฝ๋ก๋ฅผ ์ ์ํ๊ณ  ์์
```java
CSS(new String[]{"/css/**"}),
JAVA_SCRIPT(new String[]{"/js/**"}),
IMAGES(new String[]{"/images/**"}),
WEB_JARS(new String[]{"/webjars/**"}),
FAVICON(new String[]{"/**/favicon.ico"});
```
* ๋ฐ๋ผ์ ์ด๋ฏธ์ง ํ์ผ์ ๊ฒฝ์ฐ images ํด๋ ์๋์ ์ ์ํด์ผ WebIgnore ์ค์ ์ ์ ์๋ ๋๋ก Filter ๋ฅผ ํต๊ณผ ํ  ์ ์์
* permitAll ๊ณผ๋ ๋ค๋ฅด๊ฒ, FilterSecurityInterceptor (์ธ๊ฐ) ์์ฒด๋ฅผ ๊ฑฐ์น์ง ์๋๋ค๋ ์ ์ด ๋ค๋ฅด๋ค.


<br/>


๐ ํ๋ ์นดํผ๋ฅผ ์ํ ์ค๋น
-
* ModelMapper ๋ผ์ด๋ธ๋ฌ๋ฆฌ๊ฐ ์๋ค.
```xml
<dependency>
    <groupId>org.modelmapper</groupId>
    <artifactId>modelmapper</artifactId>
    <version>2.3.0</version>
</dependency>
```
* ๋ค์๊ณผ ๊ฐ์ด ์ฌ์ฉ
```java
ModelMapper modelMapper = new ModelMapper();
Account account = modelMapper.map(accountDTO, Account.class);
```
* accountDTO ์ ํ๋ ์ค ์ด๋ฆ์ด ๊ฐ์ ํ๋ ๊ฐ์ ์๋ก์ด Account ํด๋์ค๋ก ์ฎ๊ฒจ ๋ฐํ



<br/>

๐ ์คํ๋ง ์ํ๋ฆฌํฐ DB ์ฐ๋ ์ธ์ฆ ์ฒ๋ฆฌ
-
* UserDetailsService ๋ฅผ ์ปค์คํ ํ์ฌ, DB ์ ๊ณ์ ์ ์ฐ๋ํ๋๋ก ์ค์ ํ  ์ ์์
    * ์ด ๋ UserDetails ๊ฐ์ฒด๋ฅผ ๋ฐํํด์ผํจ
![default](./img/995ca80b1be44403b5d1a8483550c451.png)
* UserDetailsService ๋ฅผ ๊ตฌํํ๊ณ  UserDetails(์ด๋ฏธ ๊ตฌํํ User ๋ฅผ ์์๋ฐ๋๊ฒ ํธํ๋ค) ๋ฅผ ๊ตฌํํ ํด๋์ค๋ฅผ ์ค์  ํ์ผ์ ๋ฑ๋กํด์ ์ฐ๋ ๊ฐ๋ฅ

> UserDetailsService

* [AccountDetailsServiceImpl.java](../src/main/java/com/slack/slack/appConfig/security/service/AccountDetailsServiceImpl.java)

> UserDetails

* [AccountContext.java](../src/main/java/com/slack/slack/appConfig/security/service/AccountContext.java)

> configure ๋ฑ๋ก
* ์ปค์คํํ service ๋ฅผ ์์คํ์ด ์ฌ์ฉํ๋๋ก ์ค์  ๋ฑ๋ก
```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService);
}
```

> remember me ์ต์์ ์ฌ์ฉํ๋ค๋ฉด ์ต์์๋ service๋ฅผ ์ถ๊ฐํด์ค๋ค.

```java
http
    .rememberMe()
    .rememberMeParameter(remember)
    .tokenValiditySeconds(3600 * 24 * 14)
    .userDetailsService(accountDetailsService);
```





<br/>

๐ AuthenticationProvider ์ปค์คํ (details set)
-
![default](./img/736b774754a748fcbd3627c174075f8e.png)
* AuthenticationProvider ๊ตฌํ์ฒด๋ฅผ ๋ง๋ค๊ณ , ์ค์  ํ์ผ์ Bean ์ผ๋ก ๋ฑ๋ก ํ, provider ๋ฑ๋ก

> AuthenticationProvider ๋ฅผ ์ปค์คํํ๋ฉด
* details ์ setting ํ  ๋ด์ฉ์ ์ปค์คํํ  ์ ์๊ฒ ๋จ
* ์ธ์ฆ ์ ์ถ๊ฐ์ ์ธ ์์์ ์งํํ  ์ ์๊ฒ ๋จ


> AuthenticationProvider ์์ฑ
* [FormAuthenticationProvider.java](../src/main/java/com/slack/slack/appConfig/security/provider/FormAuthenticationProvider.java)
* ProviderManager ๋ฅผ ๋๋ฒ๊นํ๋ฉด support ๋ฅผ ํธ์ถํ๋ค๋ ์ฌ์ค์ ํ  ์ ์์

> Bean ์ผ๋ก ๋ฑ๋กํ๊ธฐ

```java
@Bean
public AuthenticationProvider authenticationProvider() {
    return new FormAuthenticationProvider(accountDetailsService, passwordEncoder);
}
```

> ๋ง๋  AuthenticationProvider ๋ฅผ ์์คํ์ ๋ฑ๋ก

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(authenticationProvider());
}
```


> WebAuthenticationDetails
* ์คํ๋ง ์ํ๋ฆฌํฐ๋ ์ธ์ฆ์ ํ์ํ ํ๋ผ๋ฏธํฐ์ธ username, password ์ด์ธ์ ์ ๋ณด๋ค์ ๊ฐ์ง๊ณ  ์์ ์ ์๋๋ก Authentication ํด๋์ค์ details ๋ผ๋ ํ๋๊ฐ ์๋๋ฐ, ์ฌ๊ธฐ์ WebAuthenticationDetails์ ๋ด์ ์ ์๋ค.
![default](./img/7c12d2aa66d94130b8ef648db3f88efe.png)
* Authentication ๊ฐ์ฒด ๋ด๋ถ์ WebAuthenticationDetails ์ ๊ธฐ๋ณธ์ ์ผ๋ก remoteAddress, sessionId ๋ฅผ ๊ฐ์ง๊ณ  ์๋ค.
* AuthenticationDetailsSource ๊ฐ์ฒด๊ฐ WebAuthenticationDetails๋ฅผ ์์ฑํด์ค๋ค.

> WebAuthenticationDetails ์ฌ์ฉ๋ฒ
* WebAuthenticationDetails ์์
    ```java
    public class FormWebAuthenticationDetails extends WebAuthenticationDetails {
    
        private String secretKey;
    
        public FormWebAuthenticationDetails(HttpServletRequest request) {
            super(request);
            secretKey = request.getParameter("secret_key");
        }
    
        public String getSecretKey() {
            return secretKey;
        }
    }
    ```
    
* FormWebAuthenticationDetailsSource ์์ ํด๋์ค๋ฅผ ๋ง๋ค์ด Component๋ก ๋ฑ๋ก (๋๋ Config ์ ํจ์๋ก ์์ฑ)
    ```java
    // config ํจ์๋ก ๋ฑ๋กํ๋ค.
    private AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource() {
        return request -> new FormWebAuthenticationDetails(request);
    }
    ```
    
* config ์ details ๋ฑ๋ก
    ```java
    // ์ด config ์ ์ธ์ฆ ์ ์ฑ
    http
            .formLogin()
            // detail
            .authenticationDetailsSource(authenticationDetailsSource())
            ...
    ```
    
* ์ฌ์ฉํ  ๋
    ```java
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    FormWebAuthenticationDetails details = (FormWebAuthenticationDetails) authentication.getDetails();
    ```
    

<br/>

๐ ๋ก๊ทธ์ธ ์ปค์คํ & ๋ก๊ทธ์์ ์ปค์คํ
-
![default](./img/f600f74d63cc415a8e53aeca5703d89e.png)

* ์ด ๋ ".loginProcessingUrl("/login_proc")" ์ผ๋ก ๋ช์ํ ๊ฒฝ๋ก์ form ์ action ๊ฒฝ๋ก๊ฐ ์ผ์นํ์ผ ํ๋ค.


![default](./img/00e6ae96f626482499ad863de5f0e3b8.png)

<br/>

๐ ์ธ์ฆ ํธ๋ค๋ฌ ์ปค์คํ
-
![default](./img/9a6831a762754eaeb0cd3c5fb7d0de94.png)
* ์ฑ๊ณต ํธ๋ค๋ฌ๋ ํ์ด์ง ๋ฆฌ๋ค์ด๋ ํธ ๋ฑ๋ฑ์ ์ํด ์ฌ์ฉ

![default](./img/06f12ec45ca24e8ca31c5669d9ce4dee.png)
* ์คํจ ํธ๋ค๋ฌ๋ ํ์ด์ง ์๋ฌ๋ฅผ ์๋ฆฌ๊ธฐ ์ํด์ ์ฌ์ฉ


> ํธ๋ค๋ฌ ์์ฑ


* [FormAuthenticationSuccessHandler.java](../src/main/java/com/slack/slack/appConfig/security/handler/FormAuthenticationSuccessHandler.java)
* [FormAuthenticationFailureHandler.java](../src/main/java/com/slack/slack/appConfig/security/handler/FormAuthenticationFailureHandler.java)
    * FormAuthenticationFailureHandler ์ ๊ฒฝ์ฐ redirect ๋ก ํ์ด์ง๋ฅผ ์ด๋ํ๋ ๊ฒ์ด ์๋๊ธฐ์, permitAll ํด์ฃผ์ด์ผ ํ๋ค.

> ํธ๋ค๋ฌ ๋ฑ๋ก
```java
// ์ด config ์ ์ธ์ฆ ์ ์ฑ
http
    .formLogin()
    ...
    // ์๊ฒฉ ์ฆ๋ช์ ์ฑ๊ณต ํ์ ๋
    .successHandler(authenticationSuccessHandler)
    // ์คํจ ํธ๋ค๋ฌ
    .failureHandler(authenticationFailureHandler)
    ...
```
* ์ด ๋ Bean ์ผ๋ก ๋ฑ๋กํ๋๊ฒ ๋ฐ๋์ง ํ๋ฐ, ์๋ํ๋ฉด defualt url์ ์ธ๋ถ๋ก ๋ถํฐ ์ฃผ์๋ฐ๊ณ  ์ถ๊ธฐ ๋๋ฌธ์ด๋ค.


<br/>

๐ ์ธ๊ฐ ํธ๋ค๋ฌ ์ปค์คํ
-
![default](./img/53cf6adb5f1f40b18860bffdbc0dfccb.png)
* AccessDeniedHandler ๊ตฌํ
    * [FormAccessDeniedHandler.java](../src/main/java/com/slack/slack/appConfig/security/handler/FormAccessDeniedHandler.java)

* ์ค์  ํ์ผ์ ํธ๋ค๋ฌ ๋ฑ๋ก (Bean ์ผ๋ก ๋ฑ๋กํ๋ ์ด์ ๋ url ์ ์ธ๋ถ๋ก ๋ถํฐ ์ฃผ์๋ฐ๊ธฐ ์ํจ)
    ```java
    private AccessDeniedHandler accessDeniedHandler() {
        return new FormAccessDeniedHandler().setErrorPage(denied);
    };

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
      // ์ธ์ฆ ์์ธ ์ฒ๋ฆฌ
      http
          .exceptionHandling()
          // AccessDeniedHandler ๋ฅผ ๊ตฌํํ ๊ตฌํ์ฒด
          .accessDeniedHandler(accessDeniedHandler());
    }
    ```
    
* denied ๋ฅผ ์ฒ๋ฆฌํ  ์ปจํธ๋กค๋ฌ ๊ตฌ์ฑ
    ```java
    // ๊ด๋ฆฌ์ ์ธ๊ฐ ๊ฑฐ๋ถ
    @GetMapping("/denied")
    public String denied(HttpServletRequest request, Model model) {
        Optional opt_exception = Optional.ofNullable(request).map(req -> req.getParameter("exception"));
        model.addAttribute("exception", opt_exception.orElse(null));
        return "denied/denied";
    }
    ```


<br/>
    
---
---
---

๐ Ajax ์ธ์ฆ ์ํ๋ฆฌํฐ ํ๊ฒฝ ๊ตฌ์ฑํ๊ธฐ
* <https://www.notion.so/4-Ajax-ea10647f1b4040f2ad3f8d0d6c8974bd>
