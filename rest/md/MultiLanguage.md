๐ ๋ค๊ตญ์ด ์ฒ๋ฆฌ
-

1. ๋ค๊ตญ์ด ์ฒ๋ฆฌ๋ฅผ ์ํด properties ํ์ผ ๋์ , yml ํ์ผ ์ฌ์ฉํ๊ธฐ ์ํด ๋ํฌ๋์ ์ถ๊ฐ
```java
<!-- yml ์ฌ์ฉ์ ์ํ ๋ผ์ด๋ธ๋ฌ๋ฆฌ -->
<dependency>
    <groupId>net.rakugakibox.util</groupId>
    <artifactId>yaml-resource-bundle</artifactId>
    <version>1.1</version>
</dependency>
```

2. messages.yml ํ์ผ์ ์ฌ์ฉํ๊ฒ ๋ค๊ณ  ์ค์  ํ์ผ์ ๋ช์
```html
spring:
  messages:
    basename: messages
    encoding: UTF-8
```

3. messages.yml ํ์ผ ์ค๋น (์ธ์ด ํ์ผ)
    * ํ๊ธ์ ๊ฒฝ์ฐ _ko 
    * ์๋ฌธ์ ๊ฒฝ์ฐ _en

4. ์๋ฐ ์ค์  ํ์ผ ์ถ๊ฐํ๊ธฐ
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
        // ์ธ์ด&๊ตญ๊ฐ์ ๋ณด๊ฐ ์๋ ๊ฒฝ์ฐ ๋ฏธ๊ตญ์ผ๋ก ์ธ์ํ๋๋ก ์ค์ 
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

5. ์์กด์ฑ ์ถ๊ฐ ํ ์ฌ์ฉํ๊ธฐ

```java
@Autowired
private MessageSource messageSource;

messageSource.getMessage("greeting.message", null, locale) => ๋ถ๋ฌ์ค๋ ๋ก์ง
```

6. ์์ฒญํ  ๋์
Accept-Language ๋ฅผ ๊ฐ์ด ๋ณด๋ด์ค์ผ ํจ


 

