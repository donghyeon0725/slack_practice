📌 리턴타입으로 xml 지원하기
-

* 리턴 디팬던시 추가
```xml
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
    <version>2.10.2</version>
</dependency>
```

* 요청 
```xml
Accept : application/xml
```

* 406 에러가 난다면, xml 컨버터가 추가 되지 않은 것으로 아래와 같은 설정을 추가해야한다.
```java
@Configuration
@EnableWebMvc
public class AppConfig implements WebMvcConfigurer {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2XmlHttpMessageConverter());
    }
}
```


* 위와 같은 설정으로 기본 포멧이 바뀐 경우. 아래와 같은 클래스 설정 파일을 추가
```java
@Configuration
@EnableWebMvc
public class AppConfig implements WebMvcConfigurer {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter());
        converters.add(new MappingJackson2XmlHttpMessageConverter());
    }
}
```







끝!
