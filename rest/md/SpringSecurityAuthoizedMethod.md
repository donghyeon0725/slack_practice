๐ ์ธ๊ฐ ํ๋ก์ธ์ค DB ์ฐ๋ (Method)
-
![default](./img/90da2f8c538b482fa3f35eb011eeb607.png)


> Method ๋ฐฉ์ VS Url ๋ฐฉ์ 
* Method ๋ AOP ๊ธฐ๋ฐ์ผ๋ก ์์์ ์คํํ๋ ์ฝ๋๋ฅผ ํ๋ก์๋ก ๊ฐ์ธ๊ณ  ํ๋ก์ ํธ์ถ ์ด์  ์ดํ ๊ฒ์ฌ๋ฅผ ํด์, ๋ฑ๋ก๋ ์ด๋๋ฐ์ด์ค๋ฅผ ํธ์ถํ๋ ๋ฐฉ์
* Url ๋ฐฉ์์ ์์ url ์ ํด๋นํ๋ ์ปจํธ๋กค๋ฌ ์  ํ, Filter ๋ฅผ ์คํํ๋ ๋ฐฉ์

![default](./img/0b8d6d801b45415ca3214d29dbf15976.png)

* ์ด๊ธฐํ ์
    * ๋น์ ๊ฒ์ฌํ๋ฉด์ ๋ณด์์ด ์ค์ ๋ (์ด๋ธํ์ด์์ด ๋ถ์) ๋ฉ์๋๋ฅผ ํ์
    * ํด๋น ๊ฐ์ฒด๋ฅผ ํ๋ก์๋ก ์์ฑ
    * ๋ณด์ ๋ฉ์๋์ ์ธ๊ฐ ์ฒ๋ฆฌ (๊ถํ ์ฌ์ฌ)๋ฅผ ํ๋ Advice ๋ฅผ ๋ฑ๋กํ๋ค๋ฉด ํด๋น Advice ๋ฅผ ํธ์ถํด์ ๊ถํ ์ฌ์ฌ
    * ๋น ์ฐธ์กฐ์์, ์ค์  ๋น์ด ์๋๋ผ, ํ๋ก์ ๋น์ ์ฐธ์กฐ
        * ๋น์ฐํ, ์คํ๋ง ์ปจํ์ด๋์ ๋ฑ๋ก๋์ด์ผ ์ด๋๋ฐ์ด์ค ํธ์ถ์ด ๊ฐ๋ฅํ๊ธฐ ๋๋ฌธ์ new๋ก ์ง์  ์์ฑํด์๋ ์๋๋ค.

* ์งํ ๊ณผ์ 
    * ๋ฉ์๋๋ฅผ ํธ์ถํ๋ฉด, ํ๋ก์ ๊ฐ์ฒด๋ฅผ ํธ์ถํ๋ค.
    * Advice ๊ฐ ๋ฑ๋ก๋์ด ์๋ค๋ฉด Advice๋ฅผ ํธ์ถ
    * ๊ถํ ์ฌ์ฌ๋ฅผ ํต๊ณผํ๋ฉด ์ค์  ๋น์ ๋ฉ์๋ ํธ์ถ

![default](./img/fdac609ea6a94003889029667397fbbb.png)

* ์ธ๊ฐ ์ฒ๋ฆฌ๋ฅผ ํ  ์ ์๋ Advice ๊ฐ ๋ฑ๋ก๋ ํ๋ก์ ๊ฐ์ฒด๊ฐ ์์ฑ ๋ ์ํ
    * Order ๋ฉ์๋๋ฅผ ํธ์ถํ๋ค.
    * OrderServiceProxy ๊ฐ ๋์  ํธ์ถ ๋๋ค.
    * Advice ์๋ ์ธ๊ฐ ์ฒ๋ฆฌ๋ฅผ ์ํ ๋ก์ง์ ๋ด๊ณ  ์๋๋ฐ, ์ธ๊ฐ ์ฒ๋ฆฌ๊ฐ ์ฑ๊ณตํ๋ฉด OrderService์ Order ๋ฉ์๋๋ฅผ ํธ์ถํ๋๋ฐ, ์น์ธ์ด ๋์ง ์์ผ๋ฉด AccessDeniedException ๋ฉ์๋๋ฅผ ํธ์ถํ๋ค.



<br/>

๐ Method ๋ฐฉ์ ๊ถํ ์ค์ 
-

> ์ด๋ธํ์ด์ ์ด์ฉ

![default](./img/f057505245354d6886e118324d576dcf.png)
* @PreAuthorize || @PostAuthorize
    * PrePostAnnotationSecurityMetadataSource ๊ฐ ๋ด๋นํด์ ์ฒ๋ฆฌ
* @Secured || @RolesAllowed
    * SecuredAnnotationMetadataSource ์ Jsr250MethodSecurityMetadataSource ๊ฐ ๋ด๋น

> ์ด๋ธํ์ด์ ๊ธฐ๋ฐ Method ๋ณด์์ ์ํ ์ค์ 
* ์ค์  ํ์ผ์ ๋ค์ ๋ด์ฉ์ ํ์ฑํ
```java
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
```
* PreAuthorize ๋ฅผ ์ฌ์ฉํ๊ณ ์ ํ๋ ๊ฒฝ์ฐ์ => prePostEnabled = true
* Secured ๋ฅผ ์ฌ์ฉํ๊ณ ์ ํ๋ ๊ฒฝ์ฐ์ => securedEnabled = true

> ์ด๊ธฐํ ๋๋ฒ๊น 
* GlobalMethodSecurityConfiguration ํด๋์ค์ methodSecurityMetadataSource ๋ถ๋ถ
```java
boolean hasCustom = customMethodSecurityMetadataSource != null;
boolean isPrePostEnabled = this.prePostEnabled();
boolean isSecuredEnabled = this.securedEnabled();
boolean isJsr250Enabled = this.jsr250Enabled();
```
* ์์์ ์ค์  ๋ด์ฉ์ ํตํด ๋ถ๊ธฐ ์ฒ๋ฆฌ๊ฐ ๋จ์ ์ ์ ์์

> ์ด๋๋ฐ์ด์ค ๋๋ฒ๊น
* ์ด๋๋ฐ์ด์ค ์ญํ ์ MethodSecurityInterceptor ๊ฐ ๋ด๋นํ๋ค. ๋ฐ๋ผ์ ์ด ํด๋์ค์ ๋ถ๋ชจํด๋์ค์ธ AbstractSecurityInterceptor ๋๋ฒ๊น
* filter ๊ธฐ๋ฐ์ด๊ธฐ ๋๋ฌธ์ FilterChainProxy ๋ ๋๋ฒ๊น ํ์ง ์๋๋ค.

![default](./img/ae8b30663a5d45ae90f09b88eaafcdea.png)
* DefaultAdvisorAutoProxyCreator ์ด๋ผ๋ ๋น ํ ์ฒ๋ฆฌ๊ธฐ๊ฐ MethodSecurityMetadataSourceAdvisor ๋ฅผ ํตํด ๋น์ ๊ฒ์ฌ. (MethodSecurityMetadataSourceAdvisor ์ ํ๋ก์ ๊ฐ์ฒด๋ก ๋ง๋ฌ)
* MethodSecurityMetadataSourceAdvisor ๋ MethodSecurityMetadataSourcePointcut ๊ณผ MethodInterceptor ๋ฅผ ๊ฐ์ง๊ณ  ์์
* MethodSecurityMetadataSourcePointcut ๋ ๋ฉ์๋ ์ ๋ณด๋ฅผ MethodSecurityMetadataSource ์ผ๋ก ๋๊ธฐ๊ณ , MethodSecurityMetadataSource๋ ๋ฐ์ดํฐ๋ฅผ ํ์ฑํด์ ๊ฐ์ง๊ณ  ์๋ค๊ฐ ํ๋ก์๊ฐ ํธ์ถ๋  ๋, ํ์ฑ๋ ๋ฐ์ดํฐ์์ ๊ถํ ์ ๋ณด๋ฅผ ๋ฆฌํดํ๋ค.


![default](./img/c9cd431be9fa49caa4208906220e18ec.png)

![default](./img/4e81665f581d41098afc74413e28008e.png)


> Filter vs AOP ๊ธฐ๋ฐ Method ๋ฐฉ์ ์ฐจ์ด

>![default](./img/cd024a6119564846a3edae512458dd27.png)

๋์ผํ ๊ตฌ์กฐ๋ก ๋์์ ํ  ์ ์๋๋ก ๋์ด ์์

- Filter ๊ธฐ๋ฐ : FilterSecurityInterceptor ๊ฐ ๋์
- Method ๊ธฐ๋ฐ : MethodSecurityInterceptor ๊ฐ ๋์

MetaDataSource ๋ ์์์ ํด๋นํ๋ ๊ถํ ์ ๋ณด๋ฅผ ์ธํฐ์ํฐ์ ๋ฐํํ๋ฉด ์ธํฐ์ํฐ๋ ์ธ์ฆ, ์์ฒญ, ๊ถํ ์ ๋ณด๋ฅผ AccessDecisionManager ์ ๋๊ฒจ์ ๋์์ํด
    * ๋ฐ๋ผ์ DB ์ฐ๋์ด ํ๊ณ  ์ถ์ผ๋ฉด MetaDataSource ํด๋์ค๋ฅผ ๊ฑด๋๋ ค์ผ ํ๋ค.

> Method ๋ณด์ DB ์ฐ๋

![default](./img/52e30d4a452d40caadfd604bfd0c5b74.png)

* MapBasedMethodSecurityMetadataSource (MethodSecurityMetadataSource ์์ ํด๋์ค)
* Map ๊ธฐ๋ฐ์ด๊ณ , ์ด๋ธํ์ด์ ๊ธฐ๋ฐ์ด ์๋๋ค. 

![default](./img/b2b2140ee6944ef1bc91e668c4005bdd.png)
* MethodSecurityInterceptor ์ด๋๋ฐ์ด์ค๋ฅผ ํธ์ถ
* ์ด ๋ ๋ด๋ถ์ ์ผ๋ก ์์์ ๋ํ ๊ถํ ์ ๋ณด๋ฅผ MapBasedMethodSecurityMetadataSource ์ ์์ฒญ
* ๊ถํ ์ ๋ณด๊ฐ ์กด์ฌํ๋ฉด AccessDecisionManager ์ ์ ๋ฌ

> ์ฌ์ฉ๋ฒ
* <https://www.notion.so/6-DB-Method-9426920b8873444793fa3b600aee8fcd>
