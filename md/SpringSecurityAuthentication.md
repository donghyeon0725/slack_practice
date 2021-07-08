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

> remember me 옵션을 사용한다면 옵션에도 service를 추가해준다.

```java
http
    .rememberMe()
    .rememberMeParameter(remember)
    .tokenValiditySeconds(3600 * 24 * 14)
    .userDetailsService(accountDetailsService);
```





<br/>

📌 AuthenticationProvider 커스텀 (details set)
-
![default](./img/736b774754a748fcbd3627c174075f8e.png)
* AuthenticationProvider 구현체를 만들고, 설정 파일에 Bean 으로 등록 후, provider 등록

> AuthenticationProvider 를 커스텀하면
* details 에 setting 할 내용을 커스텀할 수 있게 됨
* 인증 시 추가적인 작업을 진행할 수 있게 됨


> AuthenticationProvider 생성
* [FormAuthenticationProvider.java](../src/main/java/com/slack/slack/appConfig/security/provider/FormAuthenticationProvider.java)
* ProviderManager 를 디버깅하면 support 를 호출한다는 사실을 할 수 있음

> Bean 으로 등록하기

```java
@Bean
public AuthenticationProvider authenticationProvider() {
    return new FormAuthenticationProvider(accountDetailsService, passwordEncoder);
}
```

> 만든 AuthenticationProvider 를 시스템에 등록

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(authenticationProvider());
}
```


> WebAuthenticationDetails
* 스프링 시큐리티는 인증에 필요한 파라미터인 username, password 이외에 정보들을 가지고 있을 수 있도록 Authentication 클래스에 details 라는 필드가 있는데, 여기에 WebAuthenticationDetails을 담을 수 있다.
![default](./img/7c12d2aa66d94130b8ef648db3f88efe.png)
* Authentication 객체 내부의 WebAuthenticationDetails 에 기본적으로 remoteAddress, sessionId 를 가지고 있다.
* AuthenticationDetailsSource 객체가 WebAuthenticationDetails를 생성해준다.

> WebAuthenticationDetails 사용법
* WebAuthenticationDetails 상속
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
    
* FormWebAuthenticationDetailsSource 상속 클래스를 만들어 Component로 등록 (또는 Config 에 함수로 생성)
    ```java
    // config 함수로 등록했다.
    private AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource() {
        return request -> new FormWebAuthenticationDetails(request);
    }
    ```
    
* config 에 details 등록
    ```java
    // 이 config 의 인증 정책
    http
            .formLogin()
            // detail
            .authenticationDetailsSource(authenticationDetailsSource())
            ...
    ```
    
* 사용할 때
    ```java
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    FormWebAuthenticationDetails details = (FormWebAuthenticationDetails) authentication.getDetails();
    ```
    

<br/>

📌 로그인 커스텀 & 로그아웃 커스텀
-
![default](./img/f600f74d63cc415a8e53aeca5703d89e.png)

* 이 때 ".loginProcessingUrl("/login_proc")" 으로 명시한 경로와 form 의 action 경로가 일치햐야 한다.


![default](./img/00e6ae96f626482499ad863de5f0e3b8.png)

<br/>

📌 인증 핸들러 커스텀
-
![default](./img/9a6831a762754eaeb0cd3c5fb7d0de94.png)
* 성공 핸들러는 페이지 리다이렉트 등등을 위해 사용

![default](./img/06f12ec45ca24e8ca31c5669d9ce4dee.png)
* 실패 핸들러는 페이지 에러를 알리기 위해서 사용


> 핸들러 생성


* [FormAuthenticationSuccessHandler.java](../src/main/java/com/slack/slack/appConfig/security/handler/FormAuthenticationSuccessHandler.java)
* [FormAuthenticationFailureHandler.java](../src/main/java/com/slack/slack/appConfig/security/handler/FormAuthenticationFailureHandler.java)
    * FormAuthenticationFailureHandler 의 경우 redirect 로 페이지를 이동하는 것이 아니기에, permitAll 해주어야 한다.

> 핸들러 등록
```java
// 이 config 의 인증 정책
http
    .formLogin()
    ...
    // 자격 증명에 성공 했을 때
    .successHandler(authenticationSuccessHandler)
    // 실패 핸들러
    .failureHandler(authenticationFailureHandler)
    ...
```
* 이 때 Bean 으로 등록하는게 바람직 한데, 왜냐하면 defualt url을 외부로 부터 주입받고 싶기 때문이다.


<br/>

📌 인가 핸들러 커스텀
-
![default](./img/53cf6adb5f1f40b18860bffdbc0dfccb.png)
* AccessDeniedHandler 구현
    * [FormAccessDeniedHandler.java](../src/main/java/com/slack/slack/appConfig/security/handler/FormAccessDeniedHandler.java)

* 설정 파일에 핸들러 등록 (Bean 으로 등록하는 이유는 url 을 외부로 부터 주입받기 위함)
    ```java
    private AccessDeniedHandler accessDeniedHandler() {
        return new FormAccessDeniedHandler().setErrorPage(denied);
    };

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
      // 인증 예외 처리
      http
          .exceptionHandling()
          // AccessDeniedHandler 를 구현한 구현체
          .accessDeniedHandler(accessDeniedHandler());
    }
    ```
    
* denied 를 처리할 컨트롤러 구성
    ```java
    // 관리자 인가 거부
    @GetMapping("/denied")
    public String denied(HttpServletRequest request, Model model) {
        Optional opt_exception = Optional.ofNullable(request).map(req -> req.getParameter("exception"));
        model.addAttribute("exception", opt_exception.orElse(null));
        return "admin/denied/denied";
    }
    ```


<br/>
    
---
---
---

📌 Ajax 인증 시큐리티 환경 구성하기
-
![default](./img/ffc49005a42649b7aad0a5b0808b3964.png)
* ajax 요청에 대해 보안을 하기 위해서, ajax 요청이 들어왔을 때 별도의 인증을 위한 필터에서 token 을 생성해 Manager 에 보내고 Provider 까지 가는 일련의 과정을 구현
* 그리고 Entry Point와 Handler 까지 구현


<br/>

📌 Ajax 요청 처리를 위한 필터 생성
-
![default](./img/b04e3e44e411480c99dbb48d217111c5.png)
* AbstractAuthenticationProcessingFilter 를 상속 받아야 함
* 이 필터의 동작 조건은 초기화 단계에서 넘겨 준 AntPathRequestMatcher 의 url 요청이 들어올 때
* AuthenticationManager 는 기존 시스템에서 제공하는 것을 사용하고 Token 은 커스텀하여 사용합니다.

> tip
* UsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationToken 참고해서 만들면 된다.

> AbstractAuthenticationToken 구현
* 인증 객체인 Authentication 의 구현체인 AbstractAuthenticationToken 을 상속 받는다.
```java
public class AjaxAuthenticationToken extends AbstractAuthenticationToken {
    private Object principal;
    private Object credentials;

    /**
     * 인증 전 생성
     * */
    public AjaxAuthenticationToken(Object principal, Object credentials) {
        super((Collection)null);
        this.principal = principal;
        this.credentials = credentials;
        this.setAuthenticated(false);
    }

    /**
     * 인증 후 생성
     * */
    public AjaxAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        } else {
            super.setAuthenticated(false);
        }
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
```
* 원래는 위와 같이 구현해야하지만 JWT 사용으로 조금 다르게 구현하였음
* [JwtAuthenticationToken.java](../src/main/java/com/slack/slack/appConfig/security/jwt/token/JwtAuthenticationToken.java)
    * 토큰 인증이기 때문에 유효성 검사와 비밀키를 가지고 있다.

> AbstractAuthenticationProcessingFilter 구현
* 인증 객체를 만들고, Manager 를 불러 인증을 시도하는 역할을 한다.
```java
public class AjaxLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    protected AjaxLoginProcessingFilter() {
        // 작동하는 url pattern 조건을 줄 수 있음
        super(new AntPathRequestMatcher("/api/login"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        if (!isAjax(request))
            throw new IllegalStateException("Authentication is not supported");

        AccountDTO accountDTO = new ObjectMapper().readValue(request.getReader(), AccountDTO.class);
        if (StringUtils.isEmpty(accountDTO.getUsername()) || StringUtils.isEmpty(accountDTO.getPassword()))
            throw new IllegalArgumentException("IllegalArgumentException");

        // 인증 객체 set, 이 때 detail 도 setting 해주어야 함
        AjaxAuthenticationToken authRequest
                = new AjaxAuthenticationToken(accountDTO.getEmail(), accountDTO.getPassword());
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * request 로부터 헤더를 가져와 ajax 요청인지 확인을 할 수 있음
     * */
    private boolean isAjax(HttpServletRequest request) {
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With")))
            return true;
        return false;
    }
}
```

* 원래는 위와 같이 구현해야하지만 JWT 사용으로 조금 다르게 구현하였음
* [JwtAuthenticationFilter.java](../src/main/java/com/slack/slack/appConfig/security/jwt/filter/JwtAuthenticationFilter.java)
* 토큰 인증이기 때문에, 인증 전에는 토큰 문자열을 가지고 있고 인증 후에는 토큰에서 추출한 email 을 기반으로 생성한 Userdetails를 가진다.

> JwtTokenResolver
* request 로 부터 토큰을 추출하기 위해서, 별도로 리졸버 생성
* JWT 를 사용하는 경우에만 생성
* [JwtTokenResolver.java](../src/main/java/com/slack/slack/appConfig/security/jwt/common/JwtTokenResolver.java)

> 필터를 원하는 위치에 추가
```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    // 기본 설정. 사용자 인증에 HTTP Basic Authentication 을 사용합니다.
    http
            .httpBasic().disable();

    // 이 config 이 잡고 있는 url
    http
            .antMatcher(all)
            .authorizeRequests()
            .antMatchers(all).hasRole("USER")
            .anyRequest().authenticated();

    // 이 config 의 기본 인증 정책
    http
            // rest api 만을 고려하여 기본 설정은 해제 => Authorization에 basic 항목은 사용하지 않음
            .httpBasic().disable()
            // csrf 보안 토큰 disable처리.
            .cors().disable()
            .csrf().disable();
    
    // 세션 정책
    http
            // 토큰 기반 인증이므로 세션 역시 사용하지 않습니다.
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    
    http
            .addFilterBefore(abstractAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);


    http
            .headers().frameOptions().disable();


    // SecurityContext 저장 전략 - other thread 에서 참조 가능
    SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
}

// 매니저 추가
@Override
public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
}

@Bean
public AbstractAuthenticationProcessingFilter abstractAuthenticationProcessingFilter() throws Exception {
    JwtAuthenticationFilter filter = new JwtAuthenticationFilter(url);
    filter.setAuthenticationManager(authenticationManagerBean());
    return filter;
}
```


<br/>

📌 Provider 커스텀
-
* 위에서 만든 Token 은 커스텀 하였기 때문에 이를 처리할 Provider 가 아직 없고 Provider 가 없으면 요청이 와도 이 필터가 생성한 토큰을 처리할 수 없다.
![default](./img/50bbc55a57c349bdb74b029d3deeb3ee.png)

> 인증을 시도할 Provider 생성
* [JwtAuthenticationProvider.java](../src/main/java/com/slack/slack/appConfig/security/jwt/provider/JwtAuthenticationProvider.java)

> JwtUserDetailsService 필요
* [JwtUserDetailsService.java](../src/main/java/com/slack/slack/appConfig/security/jwt/service/JwtUserDetailsService.java)

> User Entity 와 DetailsService 를 호환하기 위해서 UserContext 생성
* [UserContext.java](../src/main/java/com/slack/slack/appConfig/security/jwt/domain/UserContext.java)

> Provider 등록
```java
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    private AuthenticationProvider authenticationProvider() {
        return new JwtAuthenticationProvider(userDetailsService);
    }
```


<br/>

