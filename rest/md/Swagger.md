๐ ์ค์จ๊ฑฐ ์ฐ๋ํ๊ธฐ
-


* ์ค์จ๊ฑฐ ๋ํฌ๋์ ์ถ๊ฐ
```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency>
```

* ์ค์จ๊ฑฐ ์ค์ ํ์ผ ์ถ๊ฐ
```java
@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2);
    }
}
```

* json ์์ฒญ
```java
http://localhost:8088/v2/api-docs
```
* ui ์์ฒญ
```java
http://localhost:8088/swagger-ui.html
```

* 3์ ๋๋ถํฐ ui ์ฃผ์ ๋ณ๊ฒฝ๋จ
```java
http://localhost:8080/swagger-ui/index.html
```


<br/>

๐ ์๋ฌ๊ฐ ๋๋ ๊ฒฝ์ฐ
-

* hateoas + swagger๋ฅผ ๋๋ค ์ฌ์ฉํ๋ ๊ฒฝ์ฐ (์ค์จ๊ฑฐ 2.X.X ๋ฒ์ ๋)
```java
@Bean
public LinkDiscoverers discoverers() {
    List<LinkDiscoverer> plugins = new ArrayList<>();
    plugins.add(new CollectionJsonLinkDiscoverer());
    return new LinkDiscoverers(SimplePluginRegistry.create(plugins));
}
```
> swagger ์ hateoas ๋์, ๋ค๋ฅธ ๋ผ์ด๋ธ๋ฌ๋ฆฌ์ ๊ฐ์ ์ด๋ฆ์ ๋ฉ์๋๋ฅผ ์ฌ์ฉํ๊ณ  ์์ด์ ์ถฉ๋์ด ๋๋ ๊ฒ ๊ฐ๋ค.
๋ฐ๋ผ์ ์์ ๊ฐ์ ์ค์ ์ ์ถ๊ฐํด์ ์ถฉ๋์ ๋ง๋๋ค.
swagger ์ค์  ํ์ผ์ชฝ์ ์ถ๊ฐํด์ฃผ๋ฉด ๋ ๊ฒ ๊ฐ๋ค.

* 2.9.X ๋ฒ์ ์ ๋ฌธ์๋ฅผ ๋ชป์ฐพ๋ ์๋ฌ๊ฐ ์๋๋ฐ, ์ด๋ด ๋ ๋ณ๋์ ์ค์  ํ์ผ์ด ํ์ํฉ๋๋ค. (์ฐธ๊ณ ๋งํฌ : <https://www.inflearn.com/questions/72169>)
```java
@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

    // ์ค์จ๊ฑฐ ui ๋ฌธ์๋ฅผ ์ฐพ์ง ๋ชปํ  ๊ฒฝ์ฐ, ์๋์ ๊ฐ์ ์ค์ ์ด ์ถ๊ฐ์ ์ผ๋ก ํ์ํฉ๋๋ค.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui.html**").addResourceLocations("classpath:/META-INF/resources/swagger-ui.html");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
```



<br/>


๐ swagger ์ปค์คํฐ ๋ง์ด์ง
-

```java
private static final String BASEPACKAGE = "com.slack.slack";
private static final Contact DEFAULT_CONTACT = new Contact("", "", "ehdgus5015@gmail.com");
private static final String TITLE = "Hello REST API";
private static final String DESCRIPTION = "Some custom description of API.";
private static final String VERSION = "API 1.0";
private static final String TERMOFSERVICEURL = "Terms of service";
private static final String LICENSE = "License of API";
private static final String LICENSEURL = "API license URL";
private static final String APPLICATION_XML = "application/json"; 
private static final String APPLICATION_JSON = "application/xml";
// ์ฌ์ฉ์๊ฐ ์์ฒญํ  ์ ์๋ ๋ฐ์ดํฐ ํ์
private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES = new HashSet<>(Arrays.asList(APPLICATION_XML, APPLICATION_JSON));

// ์ค์จ๊ฑฐ ๊ธฐ๋ณธ ์ ๋ณด
private static final ApiInfo DEFAULT_API_INFO = new ApiInfo(
                                            TITLE,
                                            DESCRIPTION,
                                            VERSION,
                                            TERMOFSERVICEURL,
                                            DEFAULT_CONTACT,
                                            LICENSE,
                                            LICENSEURL,
                                            Collections.emptyList());

/**
 * api ๋ฌธ์ ํ๋ฉด์ ๋ณด์ฌ์ง, ๊ธ๊ณผ ์ปจํธ๋กค๋ฌ๋ฅผ ์ ์ํฉ๋๋ค.
 * */
@Bean
public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage(BASEPACKAGE))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(DEFAULT_API_INFO)
            .produces(DEFAULT_PRODUCES_AND_CONSUMES)
            .consumes(DEFAULT_PRODUCES_AND_CONSUMES)
            ;
}
```

* ์ค์จ๊ฑฐ์ ์ฃผ์ ๋ฌ๊ธฐ
```java
// description
@ApiModel(description = "์ฌ์ฉ์ ์์ธ ์ ๋ณด")
public class User {
    // description
    @ApiModelProperty(notes = "๋น๋ฐ๋ฒํธ๋ฅผ ์๋ ฅํด ์ฃผ์ธ์.")
    private String password;
}
```






