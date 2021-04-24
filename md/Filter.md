📌 필터 종류와 방법
-

<br/>

기본적인 필터의 적용법
-

* 필터는 자바 EE 의 또 다른 좋은 기술
* 이 필터는 디자인 패턴인 **책임 연쇄 패턴(Chain of Responsibility)을 구현한 것.
* 서블릿에 도달하기 전에 작동한다.
* 필터를 만드려면 javax.serlvet.Filter 인터페이스를 구현하여야 한다.

스프링 부트에서 필터 기술을 적용하는 방법
-
1. 필터 생성
2. 필터 등록


<br/>

1. 필터 생성

```java
public class AuditingFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) 
            throws IOException, ServletException {
        long start = new Date().getTime();
        chain.doFilter(req, res);
        long elapsed = new Date().getTime() - start;
        
        HttpServletRequest request = (HttpServletRequest) req;
        logger.debug("Request[url=" + request.getRequestURI() + 
                ", method=" + ((HttpServletRequest) req).getMethod() + 
                " completed in " + elapsed + " ms");
    }
}
```

* GenericFilterBean 을 상속받아야 함
* chain.doFilter(req, res)
    - 체인에 필터가 존재하면 추가 필터를 호출 할 수 있도록 함. 이 후 서블릿을 호출한다.
    - doFilter 를 호출하지 않으면 클라이언트에 응답을 보낼 수 없다. 호출했지만 필터를 등록하지 않았다면 마찬가지로 응답을 보낼 수 없다.
    - 또한 doFilter 를 호출한 이후 추가 작업을 진행할 수 있다.
    


<br/>

2. 필터 등록 (FilterRegistrationBean을 만들어 설정파일인 AppConfig 에 등록하는 방법 (톰캣을 임베디드로 실행할 때 적합))

```java
@Configuration
public class AppConfig {
    @Bean
    public FilterRegistrationBean<AuditingFilter> auditingFilterFilterRegistrationBean() {
        FilterRegistrationBean<AuditingFilter> registration = new FilterRegistrationBean<>();

        AuditingFilter filter = new AuditingFilter();
        registration.setFilter(filter);
        registration.setOrder(Integer.MAX_VALUE);
        registration.setUrlPatterns(Arrays.asList("/messages/*"));
        return registration;
    }

}
```
* AuditingFilter 타입을 제네릭으로 하는 FilterRegistrationBean을 생성한다.
* 만든 필터를 생성 후 필터 등록 객체에 등록한다.



<br/>


특정 url 패턴을 제외할 수 있는 필터 만들기
-
```java
package com.slack.slack.filter;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 특정, url 패턴을 제외하는 방법
 * https://github.com/spring-projects/spring-boot/issues/7426
 * GenericFilterBean 대신 OncePerRequestFilter를 사용해야 한다.
 * */
public class RequestLogFilter extends OncePerRequestFilter { // GenericFilterBean
    /**
     * 매칭 되는 url이 있는지 확인
     * */
    private AntPathMatcher pathMatcher = new AntPathMatcher();

    private List<String> excludeUrlPatterns = Arrays.asList("/h2-console/**");

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain)
            throws IOException, ServletException {

        long start = new Date().getTime();
        chain.doFilter(req, res);
        long elapsed = new Date().getTime() - start;


        String url = ((HttpServletRequest) req).getRequestURI();
        String method = ((HttpServletRequest) req).getMethod();
        String message = "completed in " + elapsed + " ms";

        logger.debug("Request[url=" + url + ", method=" + method + " " + message);
    }

    /**
     * 제외하고 싶은 패턴이 있을 경우 
     * excludeUrlPatterns 에 패턴을 등록하고
     * 아래와 같은 메소드를 추가한다.
     * */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException
    {
        return excludeUrlPatterns.stream()
                .anyMatch(p -> pathMatcher.match(p, request.getServletPath()));
        // Populate excludeUrlPatterns on which one to exclude here
    }

}

```



<br/>

📌 테스트
-
1. 1번 커밋 (1. request 로그를 남길 필터 적용(h2-console 로그 제외), 설명 추가, 테스트를 위한 컨트롤러 추가) 으로 되돌린다.
2. 포스트맨(https://www.postman.com/)을 켠다. 또는 브라우저를 켠다.
3. http://localhost:8080/test 를 입력한다. (GET 메소드로 호출)
4. 로그확인 (h2-console 로그가 남지 않는 것 또한 확인 하면 좋음). 아래와 같은 형식이면 정상
```html
Request[url=/test, method=GET completed in 143 ms]
```




