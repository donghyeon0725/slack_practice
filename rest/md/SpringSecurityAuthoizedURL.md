๐ URL ์์ ์ธ๊ฐ ํ๋ก์ธ์ค DB ์ฐ๋
-
![default](./img/6b21725493d94aa7a96abb0c856d8133.png)

![default](./img/9545203e22834898b6353034418c8f20.png)
* FilterSecurityInterceptor ์์ ์ธ๊ฐ ์ฒ๋ฆฌ๊ฐ ์ผ์ด๋๋๋ฐ FilterSecurityInterceptor ๋ AccessDecisionManager ๋ฅผ ํธ์ถํด์ ์ธ๊ฐ ์ฒ๋ฆฌ๋ฅผ ํ๋ค. ์ด๋ ๋ค์๊ณผ ๊ฐ์ ์ ๋ณด๋ฅผ ๋๊ธด๋ค.
    * ์์ฒญ ์ ๋ณด (url)
    * ๊ถํ ์ ๋ณด (url ์ ํ ๋น ๋ role)
    * ์ธ์ฆ ์ ๋ณด (๊ธฐ๋ณธ์ ์ผ๋ก ์ธ์ฆ๋ ์ฌ์ฉ์๋ง ์ ๊ทผ ๊ฐ๋ฅ)

![default](./img/165b7e6154d94d5b9de0f8180b19a804.png)

![default](./img/7e4bfd7f55b44c3b9947365439f30481.png)
* getAttribute ๋ ๊ถํ ์ ๋ณด๋ฅผ ๊ฐ์ ธ์ค๋ ํต์ฌ ๋ฉ์๋๋ก ์ด๋ฅผ ์ค๋ฒ๋ผ์ด๋ ํ์ฌ, ๊ถํ ์ ๋ณด๋ฅผ DB ์ ์ฐ๋ํ  ์ ์๋ค.
* DefaultFilterInvocationSecurityMetadataSource (์๋ ExpressionBasedFilterInvocationSecurityMetadataSource ํด๋์ค๋ ์ด ํด๋์ค๋ฅผ ์์ํ ํด๋์ค)ํด๋์ค๋ url ์ ํค๋ก ๊ถํ ์ ๋ณด๋ฅผ ์ถ์ถํ๊ธฐ ์ํ ํด๋์ค์ด๋ค.
* MethodSecurityMetadataSource ๋ ๋ฉ์๋๋ฅผ ํค๋ก ๊ถํ ์ ๋ณด๋ฅผ ์ถ์ถํ๊ธฐ ์ํ ํด๋์ค์ด๋ค. ๊ทธ๋ฆฌ๊ณ , ์ฐ์ธก 4๊ฐ์ ์ธํฐํ์ด์ค๋ ์ด๋ธํ์ด์ ๋ฐฉ์์ผ๋ก Method ๊ถํ ์ ๋ณด๋ฅผ ์ค ๋ ์ฌ์ฉํ๋ ์ธํฐํ์ด์ค ๋ค์ด๋ค. 
* ์ปค์คํ ํ  ๋๋ ์ค์  MapBasedMethodSecurityMetadataSource ํด๋์ค๋ฅผ ์์ ๋ฐ์ ๊ตฌํํ  ๊ฒ์ด๋ค.

![default](./img/73964d45b7ba4791b4418aba9dc84fdc.png)
* ๊ธฐ๋ณธ์ ์ผ๋ก (Filter)SecurityInterceptor ํด๋์ค๊ฐ ๊ถํ ์ ๋ณด๋ฅผ ์ถ์ถํด์ฃผ๋๋ฐ ์ด ํด๋์ค๋ฅผ ๋ณด๋ฉด ๋ค์๊ณผ ๊ฐ์ ์ค์ด ์์
![default](./img/a17868c657c44a6a832cdaea2e8442b1.png)

์ฆ, Metadata ํด๋์ค๋ง ์์ํด์ ๊ตฌํํ๋ฉด ๊ถํ ์ ๋ณด๋ฅผ DB์์ ๊ฐ์ ธ์ค๋๋ก ์ฐ๋ํ  ์ ์์


<br/>

๐ ์น ์ธ๊ฐ DB ์ฐ๋์ ์ํ ๊ธฐ๋ณธ ์ค์  ์ค๋น (๊ธฐ๋ณธ ํ๊ฒฝ ์ปค์คํ)
-

![default](./img/dedbfb3402764dd2aab45296766c1aab.png)
* FilterInvocationSecurityMetadataSource  ์ธํฐํ์ด์ค๋ฅผ ๊ตฌํํ์ฌ UrlFilterInvocationSecurityMetadataSource ๋ฅผ ๋ง๋ค๊ณ , ์ด๋ฅผ ํตํด์ ์ธ๊ฐ ์ฒ๋ฆฌ๋ฅผ ํ  ๊ฒ์ด๋ค.

![default](./img/82f0e508aab6435ca4035bdf47113f75.png)
* ์์ฒญ์ FilterSecurityInterceptor๊ฐ FilterInvocationSecurityMetaSource ์ ์์ ๊ถํ ์ ๋ณด ์์ฒญ
* FilterInvocationSecurityMetaSource ์ด ๋ด๋ถ์ RequestMap ๋ผ๋ ํ๋์ ์ ๊ทผํด์ ๊ถํ ์ ๋ณด๋ฅผ ๊ฐ์ ธ์ด
    * ์ด ๋ ํค๋ก ์ฌ์ฉํ๋ ๊ฐ์ "/admin" ์ด๋ url ์ ๋ณด์ด๋ค.


<br/>


๐ ๊ตฌํ
-
> FilterInvocationSecurityMetadataSource ์ปค์คํ
* ๊ตฌํ ํ  ๋ ๊ธฐ์กด์ ๋ง๋ค์ด์ง DefaultFilterInvocationSecurityMetadataSource ํด๋์ค๋ฅผ ์ฐธ๊ณ ํด์ ๋ง๋ค๋ฉด ๋๋ค.
* [UrlFilterInvocationSecurityMetadataSource.java](../src/main/java/com/slack/slack/appConfig/security/jwt/metadata/UrlFilterInvocationSecurityMetadataSource.java)



> ๋ง๋  MetadataSource ๋ฑ๋ก
* ์ด ๋ FilterSecurityInterceptor Bean ๊ณผ ๊ฐ์ด ๋ฑ๋กํด์ฃผ์ด์ผ ํ๋ค.
* ์ด ๋ AccessDecisionManager ์ AuthenticationManager ๋ฅผ ํจ๊ป ์ฃผ์ด์ผ ํจ
    ```java
    @Bean
    public FilterSecurityInterceptor filterSecurityInterceptor() throws Exception {
      FilterSecurityInterceptor interceptor = new FilterSecurityInterceptor();
    
      interceptor.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource());
      interceptor.setAccessDecisionManager(affirmativeBased());
      interceptor.setAuthenticationManager(authenticationManagerBean());
    
      return interceptor;
    }
    
    @Bean
    public UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource() {
      return new UrlFilterInvocationSecurityMetadataSource();
    }
    
    /**
    * ํ๋๋ผ๋ ์ ๊ทผ ๊ฑฐ๋ถ๊ฐ ๋จ๋ฉด
    * ํ๊ฐ ๊ฑฐ๋ถ
    * */
    @Bean
    public AffirmativeBased affirmativeBased() {
      return new AffirmativeBased(getAccessDecisionVoters());
    }
    
    /**
    * ๋ณดํฐ ๋ฆฌ์คํธ
    * */
    private List<AccessDecisionVoter<?>> getAccessDecisionVoters() {
      return Arrays.asList(new RoleVoter());
    }
    ```
    
* ๊ทธ๋ฆฌ๊ณ  ๋ง๋  FilterSecurityInterceptor ์ ๊ธฐ์กด FilterSecurityInterceptor๊ณผ ๋์ฒด ํด์ค๋ค.
    ```java
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .addFilterBefore(filterSecurityInterceptor(), FilterSecurityInterceptor.class);
    }
    ```
    * [JwtSecurityConfig.java](../src/main/java/com/slack/slack/appConfig/security/jwt/config/JwtSecurityConfig.java)

* ์ฐ๋ฆฌ๊ฐ ๋ง๋  interceptor๊ฐ ๋จผ์  ์๋์ ํ๋ฉด, ๊ธฐ์กด์ interceptor๊ฐ ํ๋ ์ธ๊ฐ ์์์ ํ์ง ์๋๋ค. ์ด๋ฏธ ํ๊ฐ ์ฌ์ฌ๋ฅผ ๋ง์ณค๊ธฐ ๋๋ฌธ์ ์๋ํ์ง ์๋ ๊ฒ



<br/>

๐ ํ๋ฆ ์ ๋ฆฌ
-
![default](./img/99cd4d6caf9b48dfa9bd08cacd19d7c8.png)
* /users ์์ฒญ์ด ๋ค์ด์จ๋ค.
* FilterInvocationSecurityMetadataSource ์์ getAttributes๋ฅผ ํธ์ถํด์ url ์ ํด๋นํ๋ ๊ถํ์ ๋ณด๋ฅผ ์์ฒญ
* url ์ ํค๋ก RequestMap ์ ๋ด๊ธด ๊ถํ ์ ๋ณด get
    * null ์ผ ๊ฒฝ์ฐ ์ธ๊ฐ ์ฒ๋ฆฌ X
    * ์ฐพ์ ๊ฒฝ์ฐ FilterInvocationSecurityMetadataSource  ์ ๊ถํ ์ ๋ณด ๋ฐํ
* getAttributes ๋ฅผ ํธ์ถํด์ ๋ฐ์ ๊ถํ ์ ๋ณด๋ฅผ FilterSecurityInterceptor ๊ฐ ๋ฐ์


<br/>


๐ URL ์ธ๊ฐ ์ฒ๋ฆฌ, DB ์ฐ๋
-
![default](./img/07670da69cb64c36a29200a1f40900ac.png)
* FilterInvocationSecurityMetadataSource ๊ตฌํ ํด๋์ค์ requestMap ์ data๋ง set ํด์ฃผ๋ฉด ๋จ 
* ์ด๋ฅผ ์ํด์ LinkedHashMap<RequestMatcher, List<ConfigAttribute>> ํ์์ ์์์ ๋ฆฌํดํ  UrlResourcesMapFactoryBean ์์ฑ

> UrlResourcesMapFactoryBean ์์ฑ
* Bean ํํ๋ก ์ธํ
* [UrlResourcesMapFactoryBean.java](../src/main/java/com/slack/slack/appConfig/security/jwt/metadata/UrlResourcesMapFactoryBean.java)


> ์์ ๊ด๋ฆฌ๋ฅผ ํ  Service ์์ฑ
* DB์ ์ง์  ๋ฐ์ดํฐ ํต์ ์ ํด์ data ๋ฅผ setting
* [SecurityResourceService.java](../src/main/java/com/slack/slack/appConfig/security/form/service/SecurityResourceService.java)



> ResourceRepository ์์ฑ
* [ResourcesRepository.java](../src/main/java/com/slack/slack/appConfig/security/form/repository/ResourcesRepository.java)
    * ๋ด๋ถ ์ฟผ๋ฆฌ์ ๋ณด๋ฉด ์์๋ฅผ ์ง์ ํด์ ๊ฐ์ ธ์ค๋ ๋ถ๋ถ์ด ์๋๋ฐ
    ```java
    @Query("select r from Resources r join fetch r.resourcesRoles s join fetch s.role where r.resourceType = 'URL' order by r.orderNum desc")
    List<Resources> findAllResources();
    ```
    * ์ค์ ์ ์ธ๊ฐ ์ฒ๋ฆฌ๊ฐ ์ ์ฉ๋  ๊ฒฝ์ฐ ๋จผ์  ์ค๋ ์ค์  ๋ด์ฉ์ด ๋ ์ฐ์ ์๊ฐ ๋๊ธฐ ๋๋ฌธ์ ๊ผญ ์ด๋ ๊ฒ ์ฒ๋ฆฌ๋ฅผ ํด์ฃผ์ด์ผ ํ๋ค.
    

> UrlFilterInvocationSecurityMetadataSource ๋ค์๊ณผ ๊ฐ์ด ๋ณ๊ฒฝ
* ์์ฑ์๋ก UrlResourcesMapFactoryBean ์ ๋ฐ๊ณ , ํด๋น Bean ์์ ๊ถํ ๋ฐ์ดํฐ๋ฅผ ๋ก๋ํ๋ ๋ถ๋ถ
```java
public class UrlFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private LinkedHashMap<RequestMatcher, List<ConfigAttribute>> requestMap;

    private SecurityResourceService securityResourceService;

    public UrlFilterInvocationSecurityMetadataSource(LinkedHashMap<RequestMatcher, List<ConfigAttribute>> requestMap, SecurityResourceService securityResourceService) {
        this.requestMap = requestMap;
        this.securityResourceService = securityResourceService;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {

        HttpServletRequest request = ((FilterInvocation) o).getRequest();

        // requestMap ์ DB์์ ์ถ์ถํ ๊ถํ ์ ๋ณด๋ฅผ ๋ฃ์ด๋์ผ ํ๋ค.
        if (requestMap != null)
            for ( Map.Entry<RequestMatcher, List<ConfigAttribute>> entry : requestMap.entrySet()) {
                RequestMatcher matcher = entry.getKey();

                if (matcher.matches(request))
                    return entry.getValue();

            }

        return null;
    }


    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<ConfigAttribute> allAttributes = new HashSet();
        Iterator var2 = this.requestMap.entrySet().iterator();

        while(var2.hasNext()) {
            Map.Entry<RequestMatcher, List<ConfigAttribute>> entry = (Map.Entry)var2.next();
            allAttributes.addAll((Collection)entry.getValue());
        }

        return allAttributes;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }


		// ์ด ๋ฉ์๋ ํธ์ถ
    public void reload() {
        requestMap = securityResourceService.getResourceList();
    }
}
```


> config ์ UrlResourcesMapFactoryBean ์์ฑ ํ UrlFilterInvocationSecurityMetadataSource ์ ๋ฑ๋ก
```java
@Bean
public UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource() {
    return new UrlFilterInvocationSecurityMetadataSource(urlResourcesMapFactoryBean().getObject(), securityResourceService);
}

private UrlResourcesMapFactoryBean urlResourcesMapFactoryBean() {
    UrlResourcesMapFactoryBean urlResourcesMapFactoryBean = new UrlResourcesMapFactoryBean();
    urlResourcesMapFactoryBean.setSecurityResourceService(securityResourceService);

    return urlResourcesMapFactoryBean;
}
```
* ์ฌ๊ธฐ ๊น์ง ๋ง์ณค๋ค๋ฉด DB์ ์ฐ๋ ๋์ด์ ์ธ๊ฐ ์ฒ๋ฆฌ๋ฅผ ํ๋ ๊ฒ์ ๋ง์ผ๋ ์ด๋ ์ ํ๋ฆฌ์ผ์ด์ ๋ก๋ฉ ์์ ์ ๋ฑ 1๋ฒ DB์ ์ฐ๋ํ๋ ๊ฒ์ด๋ค.
* ๋ฐ๋ผ์ ๊ถํ ๊ด๋ฆฌ ์ฒ๋ฆฌ๋ฅผ ํ๋ ๋ถ๋ถ์ ๊ผญ UrlFilterInvocationSecurityMetadataSource ๋ฉ์๋์ reload ๋ฅผ ํธ์ถํ๋๋ก ํ๋ค.

> ์ค์๊ฐ ์ธ๊ฐ์ฒ๋ฆฌ๋ฅผ ์ํด์ ์์์ insert ํ๋ ๋ถ๋ถ์๋ ๋ฆฌ๋ก๋ ํธ์ถ

![default](./img/8dffed3bb2dc4463aca8f74f0745b303.png)



<br/>

๐ permit All Filter
-
![default](./img/c56183f1eb6b4ab29c298f85b6e1c54d.png)
* ์ ๊ตฌ์กฐ๋ฅผ ์ฝ๊ฒ ํ์ด ํํํ๋ฉด FilterSecurityInterceptor ๋ฅผ ์์ ๋ฐ์ ๊ตฌํํ๊ณ  ์ด๋ฅผ ์์คํ์ด ์ฌ์ฉํ๋๋ก ํ๋ฉด ์์ฒญ์ด FilterSecurityInterceptor ์ผ๋ก ๋๊ฒจ์ง๊ธฐ ์ ์ ๋ด๊ฐ ๋ง๋  ํด๋์ค๊ฐ ๋ฐ๋๋ก ํ  ์ ์๋๋ฐ ์ฌ๊ธฐ์ "ํ์ํ ๊ถํ์ด ์์" ์ํ๋ก ๋ง๋ค์ด ์ค ์ ์๋ค๋ ๊ฒ์ด๋ค.

    * ์์ ๊ฐ์ด ๊ถํ ์ฌ์ฌ๋ฅผ ํ๋ฉด ์๋๋ (ํ  ํ์๊ฐ ์๋) ์์๋ค์ ์ธ์ ๋  permit ํ  ์ ์๋๋ก ์ค์ ํ๋ filter ์ด๋ค.
    * ์ฐ๋ฆฌ๊ฐ PermitAllFilter ๋ผ๋ ๊ฒ์ ๋ง๋ค์ด์, FilterSecurityInterceptor ์ชฝ์ ๋์  ์ถ๊ฐํด์ฃผ๊ณ , ๊ถํ ์ฌ์ฌ ์์ด ํต๊ณผํ  ์ ์๋๋ก ํ์ํ ๊ถํ์ null ๋ก ์ฒ๋ฆฌํ  ๊ฒ์ด๋ค.
    * ๊ธฐ์กด์ FilterSecurityInterceptor ๋ ์ธ๊ฐ ์ฒ๋ฆฌ๋ฅผ ์ํ ์ฌ์ฌ๋ฅผ ์งํํ๋ค๊ณ  ํ๋ค.
    * **๋ฐ๋ผ์ ์ด ํํฐ๋ฅผ ์์ ๋ฐ์ ํ์ํ ๋ถ๋ถ (๊ถํ ์ ๋ณด๋ฅผ ๋ฐ์์ค๋ ๋ฉ์๋ ๋ถ๋ถ๋ง) ์ฌ์ ์ ํ  ๊ฒ์ด๋ค.**
    * **PermitAllFilter ์์ฑ (FilterSecurityInterceptor ์์)**

> ํํฐ ์์ฑ

* [UrlFilterSecurityInterceptor.java](../src/main/java/com/slack/slack/appConfig/security/jwt/interceptor/UrlFilterSecurityInterceptor.java)
* ์ด ํด๋์ค๋ FilterSecurityInterceptor ์ ์ฐธ๊ณ ํด์ ๋ง๋ค๋ฉด ๋๋๋ฐ ๋ค์๊ณผ ๊ฐ์ ์ฃผ์ ์ฌํญ์ด ์๋ค.
    * invoke ๋ฉ์๋๊ฐ ๊ถํ ์ ๋ณด๋ฅผ ์ฌ์ฌ ํ๋ ๋ถ๋ถ์ผ๋ก beforeInvocation์ด ํธ์ถ ๋๋ ๋ฉ์๋๋ฅผ ๋ณ๊ฒฝํด์ฃผ์ด์ผ ํ๋ค.
    * ์ด ๋ beforeInvocation ๋ super ๊ฐ ์๋๋ผ, ์ด ํด๋์ค ๋ด๋ถ์์ ์ฌ์ ์ ํ  ๋ฉ์๋๋ฅผ ํธ์ถํ๊ธฐ ์ํด this๋ก ๋ณ๊ฒฝํด์ค๋ค.
    * ์์ฑ์ ํธ์ถ์ ์ ์ฅํด๋ permitAllRequestMatcher ์ผ๋ก ๋ถํฐ ๊ฒ์ฌํด์, ๋งค์นญ ๋๋ ์์ฒญ์ด ์ค๋ฉด null ๋ฆฌํด ํด์ผํ๋ค.
    * beforeInvocation ์ฌ์ ์ ํด์ผํ๋ค.

> ์ค์ ํ์ผ ์ถ๊ฐ(๋ณ๊ฒฝ)
```java
private final RequestMatcher[] permitAllResources = {
        new AntPathRequestMatcher("/**", HttpMethod.OPTIONS.name())
        , new AntPathRequestMatcher("/h2-console*")
        , new AntPathRequestMatcher("/getImage*")
        , new AntPathRequestMatcher("/users/login*")

        , new AntPathRequestMatcher("/users/join*")
        , new AntPathRequestMatcher("/users*")
        , new AntPathRequestMatcher("/socket*")
        , new AntPathRequestMatcher("/rt*")
        , new AntPathRequestMatcher("/teams/join*")
        , new AntPathRequestMatcher("/rt*")
};

@Bean
public FilterSecurityInterceptor filterSecurityInterceptor() throws Exception {
    UrlFilterSecurityInterceptor interceptor = new UrlFilterSecurityInterceptor(permitAllResources);

    interceptor.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource());
    interceptor.setAccessDecisionManager(affirmativeBased());
    interceptor.setAuthenticationManager(authenticationManagerBean());

    return interceptor;
}
```


<br/>

๐ ๊ถํ ๊ณ์ธต ์ ์ฉํ๊ธฐ
-![default](./img/d47579f081a843ab80fcb5e51f19786a.png)

* ๊ถํ ๊ด๊ณ๋ฅผ ์ค์ ํ  ์ ์๋๋ก ๋ณ๋์ ํ์ด๋ธ์ด ํ์ํจ
* ํด๋น ํ์ด๋ธ์ ํตํด์ ๊ทธ๋ฆผ๊ณผ ๊ฐ์ ๋ฐฉ๋ฒ์ผ๋ก ํฌ๋ฉงํ ๋ ๋ฌธ์์ด์ select ํ๊ณ , ๊ทธ๊ฒ์ voter ๊ฐ ์ฝ์ ์ ์๋๋ก ํด์ฃผ๋ฉด ๋๋ค.
* ์ด ๋ RoleHierarchy ๋ Spring Security ๊ฐ ๊ธฐ๋ณธ ์ ๊ณตํ๋ ๊ฒ์ผ๋ก Bean ์ผ๋ก ๋ง๋ค๊ณ  ์ด๊ฒ์ ๋ฑ๋กํ Voter ๋ฅผ ๋ง๋ค์ด ๋ผ์ ์ค ๊ฒ์ด๋ค.
* ์ด ๋ ํ์ด๋ธ์ ์ฌ์ฉํ referencedColumnName = "child" ์, ์ด ์ด๋ฆ์ผ๋ก join ์ ํ๊ธฐ ์ํด์ ์ค์ ํด์ค ๋ด์ฉ์ด๋ค.

> Entity
* [RoleHierarchy.java](../src/main/java/com/slack/slack/appConfig/security/domain/entity/RoleHierarchy.java)
* ์ฌ๊ธฐ์ ์ด๋ฆ์ผ๋ก join ํ  ์ ์๋๋ก referencedColumnName = "child" ์ ๋ช์ํด์ฃผ์๋ค.
    * ๊ทธ๋ฆฌ๊ณ  @ManyToOne ์๋ @Column ์ ์ฌ์ฉํ  ์ ์๊ธฐ ๋๋ฌธ์ @JoinColumn(name = "parent") ์ ๋ช์ํด์ฃผ์๋ค.
* ์ฌ๊ธฐ์ ์ํฐํฐ๋ Serializable ๋ฅผ ๊ตฌํํ์ฌ์ผ ํ๋ค.

> RoleHierarchyService
* [RoleHierarchyService.java](../src/main/java/com/slack/slack/appConfig/security/domain/service/RoleHierarchyService.java)
  
> ์ค์  ํ์ผ ๋ฑ๋ก
```java
/**
 * ๋ณดํฐ ๋ฆฌ์คํธ
 * */
private List<AccessDecisionVoter<?>> getAccessDecisionVoters() {
    List<AccessDecisionVoter<?>> voters = new ArrayList<>();
    voters.add(roleVoter());
    return voters;
}

/**
 * ๊ถํ ๊ณ์ธต์ ์ ๋ณด๋ฅผ setting ํ voter
 * */
@Bean
public AccessDecisionVoter<?> roleVoter() {
    return new RoleHierarchyVoter(roleHierarchy());
}

/**
 * ๊ถํ ๊ณ์ธต
 * */
@Bean
public RoleHierarchyImpl roleHierarchy() {
    return new RoleHierarchyImpl();
}
```
* [JwtSecurityConfig.java](../src/main/java/com/slack/slack/appConfig/security/jwt/config/JwtSecurityConfig.java)
  

> ์ ํ๋ฆฌ์ผ์ด์ ๋ก๋ฉ ์์ ์ ๊ถํ ๊ณ์ธต set
```java
@Component
@RequiredArgsConstructor
public class SecurityInitializer implements ApplicationRunner {
    private final RoleHierarchyService roleHierarchyService;

    private final RoleHierarchyImpl roleHierarchy;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        String allHierarchy = roleHierarchyService.findAllHierarchy();
        roleHierarchy.setHierarchy(allHierarchy);
    }
}
```


๐ IP ์ ํํ๊ธฐ
-
![default](./img/40db57115a6a49678ca2ce14bd1a87b1.png)
* Ip ๋ณดํฐ๋ฅผ ํ๋ ์ถ๊ฐํ๊ณ , ํด๋น ๋ณดํฐ๊ฐ ํ๊ฐ ๊ฑฐ๋ถ๋ฅผ return ํ๋๋ก ์ค์ 
* ์ด ๋ ์ค์ํ์  IP ๋ณดํฐ๋ ์ค์ง ์ด IP ๊ฐ ์์คํ์ ์ ๊ทผ ๊ถํ์ด ์๋์ง์ ๋ํ ๊ถํ๋ง ์ค์ ํด์ผ ํ๊ณ  ๋ค๋ฅธ ์ฌ์ฌ๋ ์ฌ์ ํ ๋ฐ์์ผ ํ๋ค. ๋ฐ๋ผ์, ํต๊ณผํ์ ๋ ACCESS_GRANTED(ํ์ฉ, ๋ค๋ฅธ ์ฌ์ ์์ด ํต๊ณผํ  ์ ์๋ ์ํ) ๊ฐ ์๋๋ผ, ACCESS_ABSTAIN(๋ณด๋ฅ) ์ ๋ฆฌํดํ์ฌ์ผ ํฉ๋๋ค.
    * ๊ทธ๋ฆฌ๊ณ  ์น์ธ์ด ์๋ ๋์๋ ACCESS_DENIED ๋ฅผ ๋ฆฌํดํด์ ๋ค์ ์ฌ์ฌ๋ฅผ ๋ฐ๋๋ก ํ  ๊ฒ์ด ์๋๋ผ, ๋ฐ๋ก ์์ธ๋ฅผ ๋์ ธ์ ์ธ๊ฐ ํ์ฉ์ ๋ชปํ๋๋ก ํด์ผํฉ๋๋ค.

> ์์ดํผ ๋ณดํฐ ์์ฑ
* [IpAddressVoter.java](../src/main/java/com/slack/slack/appConfig/security/jwt/voter/IpAddressVoter.java)


> ๋ณดํฐ ๋ฆฌ์คํธ์ ์์ดํผ ๋ณดํฐ add
```java
/**
 * ๋ณดํฐ ๋ฆฌ์คํธ
 * */
private List<AccessDecisionVoter<?>> getAccessDecisionVoters() {
    List<AccessDecisionVoter<?>> voters = new ArrayList<>();
    voters.add(new IpAddressVoter(securityResourceService));
    voters.add(roleVoter());
    return voters;
}
```
