π λ¦¬ν΄νμμΌλ‘ xml μ§μνκΈ°
-

* λ¦¬ν΄ λν¬λμ μΆκ°
```xml
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
    <version>2.10.2</version>
</dependency>
```

* μμ²­ 
```xml
Accept : application/xml
```

* 406 μλ¬κ° λλ€λ©΄, xml μ»¨λ²ν°κ° μΆκ° λμ§ μμ κ²μΌλ‘ μλμ κ°μ μ€μ μ μΆκ°ν΄μΌνλ€.
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


* μμ κ°μ μ€μ μΌλ‘ κΈ°λ³Έ ν¬λ©§μ΄ λ°λ κ²½μ°. μλμ κ°μ ν΄λμ€ μ€μ  νμΌμ μΆκ°
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







λ!
