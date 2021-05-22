📌 스웨거 연동하기
-


* 스웨거 디팬던시 추가
```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency>
```

* 스웨거 설정파일 추가
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

* json 요청
```java
http://localhost:8088/v2/api-docs
```
* ui 요청
```java
http://localhost:8088/swagger-ui.html
```

* 3점대부터 ui 주소 변경됨
```java
http://localhost:8080/swagger-ui/index.html
```


<br/>

📌 에러가 나는 경우
-

* hateoas + swagger를 둘다 사용하는 경우 (스웨거 2.X.X 버전대)
```java
@Bean
public LinkDiscoverers discoverers() {
    List<LinkDiscoverer> plugins = new ArrayList<>();
    plugins.add(new CollectionJsonLinkDiscoverer());
    return new LinkDiscoverers(SimplePluginRegistry.create(plugins));
}
```
> swagger 와 hateoas 둘은, 다른 라이브러리의 같은 이름의 메소드를 사용하고 있어서 충돌이 나는 것 같다.
따라서 위와 같은 설정을 추가해서 충돌을 막는다.
swagger 설정 파일쪽에 추가해주면 될것 같다.

* 2.9.X 버전엔 문서를 못찾는 에러가 있는데, 이럴 땐 별도의 설정 파일이 필요합니다. (참고링크 : <https://www.inflearn.com/questions/72169>)
```java
@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

    // 스웨거 ui 문서를 찾지 못할 경우, 아래와 같은 설정이 추가적으로 필요합니다.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui.html**").addResourceLocations("classpath:/META-INF/resources/swagger-ui.html");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
```



<br/>


📌 swagger 커스터 마이징
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
// 사용자가 요청할 수 있는 데이터 타입
private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES = new HashSet<>(Arrays.asList(APPLICATION_XML, APPLICATION_JSON));

// 스웨거 기본 정보
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
 * api 문서 화면에 보여질, 글과 컨트롤러를 정의합니다.
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

* 스웨거에 주석 달기
```java
// description
@ApiModel(description = "사용자 상세 정보")
public class User {
    // description
    @ApiModelProperty(notes = "비밀번호를 입력해 주세요.")
    private String password;
}
```






