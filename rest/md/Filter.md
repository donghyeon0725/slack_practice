π νν° μ’λ₯μ λ°©λ²
-

<br/>

κΈ°λ³Έμ μΈ νν°μ μ μ©λ²
-

* νν°λ μλ° EE μ λ λ€λ₯Έ μ’μ κΈ°μ 
* μ΄ νν°λ λμμΈ ν¨ν΄μΈ **μ±μ μ°μ ν¨ν΄(Chain of Responsibility)μ κ΅¬νν κ².
* μλΈλ¦Ώμ λλ¬νκΈ° μ μ μλνλ€.
* νν°λ₯Ό λ§λλ €λ©΄ javax.serlvet.Filter μΈν°νμ΄μ€λ₯Ό κ΅¬ννμ¬μΌ νλ€.

μ€νλ§ λΆνΈμμ νν° κΈ°μ μ μ μ©νλ λ°©λ²
-
1. νν° μμ±
2. νν° λ±λ‘


<br/>

1. νν° μμ±

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

* GenericFilterBean μ μμλ°μμΌ ν¨
* chain.doFilter(req, res)
    - μ²΄μΈμ νν°κ° μ‘΄μ¬νλ©΄ μΆκ° νν°λ₯Ό νΈμΆ ν  μ μλλ‘ ν¨. μ΄ ν μλΈλ¦Ώμ νΈμΆνλ€.
    - doFilter λ₯Ό νΈμΆνμ§ μμΌλ©΄ ν΄λΌμ΄μΈνΈμ μλ΅μ λ³΄λΌ μ μλ€. νΈμΆνμ§λ§ νν°λ₯Ό λ±λ‘νμ§ μμλ€λ©΄ λ§μ°¬κ°μ§λ‘ μλ΅μ λ³΄λΌ μ μλ€.
    - λν doFilter λ₯Ό νΈμΆν μ΄ν μΆκ° μμμ μ§νν  μ μλ€.
    


<br/>

2. νν° λ±λ‘ (FilterRegistrationBeanμ λ§λ€μ΄ μ€μ νμΌμΈ AppConfig μ λ±λ‘νλ λ°©λ² (ν°μΊ£μ μλ² λλλ‘ μ€νν  λ μ ν©))

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
* AuditingFilter νμμ μ λ€λ¦­μΌλ‘ νλ FilterRegistrationBeanμ μμ±νλ€.
* λ§λ  νν°λ₯Ό μμ± ν νν° λ±λ‘ κ°μ²΄μ λ±λ‘νλ€.



<br/>


νΉμ  url ν¨ν΄μ μ μΈν  μ μλ νν° λ§λ€κΈ°
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
 * νΉμ , url ν¨ν΄μ μ μΈνλ λ°©λ²
 * https://github.com/spring-projects/spring-boot/issues/7426
 * GenericFilterBean λμ  OncePerRequestFilterλ₯Ό μ¬μ©ν΄μΌ νλ€.
 * */
public class RequestLogFilter extends OncePerRequestFilter { // GenericFilterBean
    /**
     * λ§€μΉ­ λλ urlμ΄ μλμ§ νμΈ
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
     * μ μΈνκ³  μΆμ ν¨ν΄μ΄ μμ κ²½μ° 
     * excludeUrlPatterns μ ν¨ν΄μ λ±λ‘νκ³ 
     * μλμ κ°μ λ©μλλ₯Ό μΆκ°νλ€.
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

π νμ€νΈ
-
1. 1λ² μ»€λ° (1. request λ‘κ·Έλ₯Ό λ¨κΈΈ νν° μ μ©(h2-console λ‘κ·Έ μ μΈ), μ€λͺ μΆκ°, νμ€νΈλ₯Ό μν μ»¨νΈλ‘€λ¬ μΆκ°) μΌλ‘ λλλ¦°λ€.
2. ν¬μ€νΈλ§¨(https://www.postman.com/)μ μΌ λ€. λλ λΈλΌμ°μ λ₯Ό μΌ λ€.
3. http://localhost:8080/test λ₯Ό μλ ₯νλ€. (GET λ©μλλ‘ νΈμΆ)
4. λ‘κ·ΈνμΈ (h2-console λ‘κ·Έκ° λ¨μ§ μλ κ² λν νμΈ νλ©΄ μ’μ). μλμ κ°μ νμμ΄λ©΄ μ μ
```html
Request[url=/test, method=GET completed in 143 ms]
```




<br/>


π νν° κ°μ 
-
* λ‘κ·Έ νν°μ κ²½μ°, μΈμ λ  λ€λ₯Έ λ²μ μ λ‘κ·Έ νν°λ₯Ό μ¬μ©νκ³  μΆμ μ μμΌλ―λ‘ κ°μ μ μ‘°κΈ ν΄λ΄€λ€.
* μΈν°νμ΄μ€λ₯Ό ν΅ν΄μ λ‘κ·Έ νν°λ₯Ό λ€μνκ² λ°μ μ μλλ‘ μ€μ νλ€.

```java
// @AllArgsConstructor μ΄λΈνμ΄μμ μ¬μ© μ€μ΄λ€.

// μμ‘΄μ± μ£ΌμμΌλ‘ λ°κ³  μλ€
private LogFilters logFilters;

@Bean
public FilterRegistrationBean requestLogFilterFilterRegistrationBean() {
    FilterRegistrationBean registration = new FilterRegistrationBean<>();
    
    // OncePerRequestFilter νν°λ‘ λ³ν
    registration.setFilter((OncePerRequestFilter)logFilters);
    registration.setOrder(Integer.MAX_VALUE);
    registration.setUrlPatterns(Arrays.asList(Path.ALL.get()));
    return registration;
}
```


<br/>


π μΈμ½λ© νν° μΆκ°
-

```java
@Bean
public FilterRegistrationBean EncodingFilterFilterRegistrationBean() {
    FilterRegistrationBean registration = new FilterRegistrationBean<>();

    // μΈμ½λ© νν°
    CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
    characterEncodingFilter.setForceEncoding(true);
    characterEncodingFilter.setEncoding(Encoding.UTF8.get());

    registration.setFilter(characterEncodingFilter);
    return registration;
}
```


<br/>

π νμ€νΈ
-
1. μ»€λ° (νν° κ°μ , encoding νν°, XSS νν°(μ»¨λ²ν°) μΆκ°)μΌλ‘ λλλ¦¬κΈ°
2. http://localhost:8080/filter_test/νκΈμμ§μνλ° μμ²­
3. λ‘κ·Έ νμΈ
```html
νκΈμμ§μνλ°
```



<br/>

π XSS μ»¨λ²ν° μΆκ°
-
1. λν¬λμ org.apache.commons μ commons-text μΆκ°
2. μ»¨λ²ν° μΆκ°
3. μ€μ νμΌ μΆκ°


<br/>

1. λν¬λμ org.apache.commons μ commons-text μΆκ°
```html
<!-- XSS Converterλ₯Ό μν΄μ μ¬μ© -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-text</artifactId>
    <version>1.9</version>
</dependency>
```

2. μ»¨λ²ν° μΆκ°

```java
package com.slack.slack.config.converter;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;
import org.apache.commons.text.StringEscapeUtils;

/**
* XSS κ³΅κ²©μ λλΉν λ³νκΈ°
* org.apache.commons μ commons-text λν¬λμκ° νμνλ€.
* */
public class HtmlCharacterEscapes extends CharacterEscapes {

    private final int[] asciiEscapes;

    public HtmlCharacterEscapes() {
        // 1. XSS λ°©μ§ μ²λ¦¬ν  νΉμ λ¬Έμ μ§μ 
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
 * XSS κ΄λ ¨ μ€μ 
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

π νμ€νΈ
-
1. μ»€λ° (νν° κ°μ , encoding νν°, XSS νν°(μ»¨λ²ν°) μΆκ°)μΌλ‘ λλλ¦¬κΈ°
2. request body μλμ κ°μ΄ μΈν
```html
{
  "title": "(title)",
  "content": "<scirpt></script>"
}
```
3. http://localhost:8080/xss_test μμ²­ ν κ²°κ³Ό νμΈ
```html
// λ‘κ·Έλ μλμ κ°μ΄ λ¨μ§λ§,
<scirpt></script>
(title)

// μ¬μ©μμκ² λ°ννλ κ°μ μλμ κ°μ΅λλ€.
{
    "title": "(title)",
    "content": "&lt;scirpt&gt;&lt;/script&gt;"
}
```




<br/>



π Filter, interceptor, AOPμ μ°¨μ΄μ 
-

Filter, interceptor, AOPμ μ°¨μ΄μ μ μλ λ§ν¬λ₯Ό μ°Έκ³ νμ.   
ν μ€λ‘ μμ½νμλ©΄ νν°μ interceptorλ urlμ ν΅ν΄μ μ μ©ν μ§ λ§μ§λ₯Ό κ΅¬λΆν΄μΌνμ§λ§, AOPλ μ£Όμ, νλΌλ―Έν°, μ΄λΈνμ΄μ λ± λ€μν λ°©λ²μΌλ‘ μν©μ κ΅¬λΆ ν΄λΌ μ μλ€λ μ μ΄λ€.

[Filter, interceptor, AOPμ μ°¨μ΄μ ](https://goddaehee.tistory.com/154)



<br/>
