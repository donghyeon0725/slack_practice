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




<br/>


📌 필터 개선
-
* 로그 필터의 경우, 언제든 다른 버전의 로그 필터를 사용하고 싶을 수 있으므로 개선을 조금 해봤다.
* 인터페이스를 통해서 로그 필터를 다양하게 받을 수 있도록 설정했다.

```java
// @AllArgsConstructor 어노테이션을 사용 중이다.

// 의존성 주입으로 받고 있다
private LogFilters logFilters;

@Bean
public FilterRegistrationBean requestLogFilterFilterRegistrationBean() {
    FilterRegistrationBean registration = new FilterRegistrationBean<>();
    
    // OncePerRequestFilter 필터로 변환
    registration.setFilter((OncePerRequestFilter)logFilters);
    registration.setOrder(Integer.MAX_VALUE);
    registration.setUrlPatterns(Arrays.asList(Path.ALL.get()));
    return registration;
}
```


<br/>


📌 인코딩 필터 추가
-

```java
@Bean
public FilterRegistrationBean EncodingFilterFilterRegistrationBean() {
    FilterRegistrationBean registration = new FilterRegistrationBean<>();

    // 인코딩 필터
    CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
    characterEncodingFilter.setForceEncoding(true);
    characterEncodingFilter.setEncoding(Encoding.UTF8.get());

    registration.setFilter(characterEncodingFilter);
    return registration;
}
```


<br/>

📌 테스트
-
1. 커밋 (필터 개선, encoding 필터, XSS 필터(컨버터) 추가)으로 되돌리기
2. http://localhost:8080/filter_test/한글은지원한데 요청
3. 로그 확인
```html
한글은지원한데
```



<br/>

📌 XSS 컨버터 추가
-
1. 디팬던시 org.apache.commons 의 commons-text 추가
2. 컨버터 추가
3. 설정파일 추가


<br/>

1. 디팬던시 org.apache.commons 의 commons-text 추가
```html
<!-- XSS Converter를 위해서 사용 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-text</artifactId>
    <version>1.9</version>
</dependency>
```

2. 컨버터 추가

```java
package com.slack.slack.config.converter;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;
import org.apache.commons.text.StringEscapeUtils;

/**
* XSS 공격에 대비한 변환기
* org.apache.commons 의 commons-text 디팬던시가 필요하다.
* */
public class HtmlCharacterEscapes extends CharacterEscapes {

    private final int[] asciiEscapes;

    public HtmlCharacterEscapes() {
        // 1. XSS 방지 처리할 특수 문자 지정
        asciiEscapes = CharacterEscapes.standardAsciiEscapesForJSON();
        asciiEscapes['<'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['>'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['\"'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['('] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes[')'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['#'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['\''] = CharacterEscapes.ESCAPE_CUSTOM;
    }

    @Override
    public int[] getEscapeCodesForAscii() {
        return asciiEscapes;
    }

    @Override
    public SerializableString getEscapeSequence(int ch) {
        return new SerializedString(StringEscapeUtils.escapeHtml4(Character.toString((char) ch)));
    }
}

```


<br/>

```java
package com.slack.slack.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.slack.config.converter.HtmlCharacterEscapes;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * XSS 관련 설정
 * */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(htmlEscapingConverter());
    }

    private HttpMessageConverter<?> htmlEscapingConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getFactory().setCharacterEscapes(new HtmlCharacterEscapes());

        return new MappingJackson2HttpMessageConverter(objectMapper);
    }
}

```


<br/>

📌 테스트
-
1. 커밋 (필터 개선, encoding 필터, XSS 필터(컨버터) 추가)으로 되돌리기
2. request body 아래와 같이 세팅
```html
{
  "title": "(title)",
  "content": "<scirpt></script>"
}
```
3. http://localhost:8080/xss_test 요청 후 결과 확인
```html
// 로그는 아래와 같이 남지만,
<scirpt></script>
(title)

// 사용자에게 반환하는 값은 아래와 같습니다.
{
    "title": "(title)",
    "content": "&lt;scirpt&gt;&lt;/script&gt;"
}
```




<br/>



📌 Filter, interceptor, AOP의 차이점
-

Filter, interceptor, AOP의 차이점은 아래 링크를 참고하자.   
한 줄로 요약하자면 필터와 interceptor는 url을 통해서 적용할지 말지를 구분해야하지만, AOP는 주소, 파라미터, 어노테이션 등 다양한 방법으로 상황을 구분 해낼 수 있다는 점이다.

[Filter, interceptor, AOP의 차이점](https://goddaehee.tistory.com/154)



<br/>
