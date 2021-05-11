📌 다국어 처리
-

1. 다국어 처리를 위해 properties 파일 대신, yml 파일 사용하기 위해 디팬던시 추가
```java
<!-- yml 사용을 위한 라이브러리 -->
<dependency>
    <groupId>net.rakugakibox.util</groupId>
    <artifactId>yaml-resource-bundle</artifactId>
    <version>1.1</version>
</dependency>
```

2. messages.yml 파일을 사용하겠다고 설정 파일에 명시
```html
spring:
  messages:
    basename: messages
    encoding: UTF-8
```

3. messages.yml 파일 준비 (언어 파일)
    * 한글의 경우 _ko 
    * 영문의 경우 _en

4. 자바 설정 파일 추가하기
```java
@Configuration
public class MessageConfig {
    @Bean("messageSource")
    public MessageSource messageSource(
            @Value("${spring.messages.basename}") String basename,
            @Value("${spring.messages.encoding}") String encoding
    ) {

        YamlMessageSource ms = new YamlMessageSource();
        ms.setBasename(basename);
        ms.setDefaultEncoding(encoding);
        ms.setAlwaysUseMessageFormat(true);
        ms.setUseCodeAsDefaultMessage(true);
        ms.setFallbackToSystemLocale(true);
        return ms;
    }

    @Bean
    public LocaleResolver localResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        // 언어&국가정보가 없는 경우 미국으로 인식하도록 설정
        localeResolver.setDefaultLocale(Locale.US);
        return localeResolver;
    }

    class YamlMessageSource extends ResourceBundleMessageSource {
        @Override
        protected ResourceBundle doGetBundle(String basename, Locale locale) throws MissingResourceException {
            return ResourceBundle.getBundle(basename, locale, YamlResourceBundle.Control.INSTANCE);
        }
    }
}

```

5. 의존성 추가 후 사용하기

```java
@Autowired
private MessageSource messageSource;

messageSource.getMessage("greeting.message", null, locale) => 불러오는 로직
```

6. 요청할 때에
Accept-Language 를 같이 보내줘야 함


 

