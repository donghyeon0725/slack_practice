
๐ ์ฃผ์ ์ํคํ์ณ
-
![default](./img/d0565e5c56af4f54aff63834a8ad81a0.png)
* ์คํ๋ง ์ํ๋ฆฌํฐ๋ url ์์์ ๋ณด์ํ  ๋, ํํฐ ๊ธฐ๋ฐ์ผ๋ก ๋์
* ํํฐ(์คํ๋ง ์ปจํ์ด๋์ ์์ฒญ์ด ์ค๊ธฐ ์  ์ฒ๋ฆฌ)๋ ์๋ธ๋ฆฟ ์ปจํ์ด๋์ ๊ธฐ์ , ๊ทธ๋ฌ๋ ์คํ๋ง ์ํ๋ฆฌํฐ๋ ์คํ๋ง ์ปจํ์ด๋(๋น)์ ๊ธฐ์ ์ด ํ์ (์ธ์ ๋ฑ๋ฑ)
* ๋ฐ๋ผ์ Servlet ์ปจํ์ด๋์ DelegatingFilterProxy๊ฐ springSecurityFilterChain ์ด๋ผ๋ ์ด๋ฆ์ ๊ฐ์ง Proxy(๋๋ฆฌ์ธ) ๋น์ ApplicaionContext ์์ ์ฐพ์ ์์ฒญ์ ์์ํจ
    * ์ด Proxy ๋ FilterChainProxy ์ด๋ค.

![default](./img/f203d1b8fac9472a9637008c964ed1ec.png)
* ํํฐ ์์๋ฅผ ์ ์ ์ ํ๋ฉด ์ ์ฌ์ด์ Filter๋ฅผ ๋ผ์ ๋ฃ๋ ๊ฒ์ด ๊ฐ๋ฅํ๋ค.

![default](./img/9d4bb1800e7f47039f515b261046e10b.png)
* ํด๋ผ์ด์ธํธ ์์ฒญ ์ Servlet ์ springSecurityFilterChain ๋น (DelegatingFilterProxy) ์ ์ฐพ์ ๋ณด์ ์ฒ๋ฆฌ ์์
* DelegatingFilterProxy ์ Application ์์ springSecurityFilterChain ์ด๋ฆ์ ๋น(FilterChainProxy)์ ์ฐพ์ ๋ณด์ ์ฒ๋ฆฌ ์์
* FilterChainProxy์ ๋ณด์ ์ฒ๋ฆฌ ํ (ํํฐ๋ฅผ ๊ฑฐ์ณ) dispatcher servlet ์ผ๋ก ์์ฒญ์ ๋๊น
* ์ด ๋ DelegatingFilterProxy๋ฅผ ํธ์ถํ๋ ํด๋์ค๋ SecurityFilterAutoConfiguration ์๋๋ค.

![default](./img/88cb7955c3044dfa8893fe7211238997.png)
* WebSecurityConfigurerAdapter ๋ HttpSecurity(http) ํด๋์ค๋ฅผ ์ฝ์ด ๋ค์ฌ์ SecurityFilterChain ์ ์์ฑํ๊ณ  ์ด๋ฅผ FilterChainProxy๋ก ๋๊ธด๋ค.
* ์์ฒญ์ด ๋ค์ด์ค๋ฉด FilterChainProxy ๋ ์ ์ ํ FilterChain ์ ์ฐพ์์ ์ฒ๋ฆฌ๋ฅผ ํ๋ค.


![default](./img/6497f17f975b4830bbea92aae2d29260.png)
* ApplicationContext ๋ WebSecurityConfiguration ํธ์ถ
* WebSecurityConfiguration ๋ WebSecurity ์์ฑ
* WebSecurity ๋ FilterChainProxy ์์ฑ
* WebSecurityConfigurerAdapter ๊ฐ HttpSecurity(http) ๋ฅผ ์ฝ์ด๋ค์ฌ SecurityFilterChain ์ ๋ง๋ค๊ณ  WebSecurity ์ ๋ฐํํ๋ฉด WebSecurity๋ ์ด๋ฅผ FilterChainProxy์ ๋ฑ๋ก



<br/>

๐ ์ธ์ฆ ํ๋ก์ธ์ค
-

> Authentication 

* ๋น์ ์ด ๋๊ตฌ์ธ์ง ์ฆ๋ช
* ์ธ์ฆ ํ ์ ์ญ์ ์ผ๋ก ์ฐธ์กฐ ๊ฐ๋ฅ
```java
Authentication authenticaion = SecurityContextHolder.getContext().getAuthentication();
```

* ๊ตฌ์กฐ
    * principal : User ์์ด๋ or User ๊ฐ์ฒด
    * credential : ๋น๋ฐ๋ฒํธ
    * authorities : ์ธ์ฆ๋ ์ฌ์ฉ์์ ๊ถํ ๋ชฉ๋ก
    * details : ์ธ์ฆ ์ ๋ณด ์ด์ธ, ๋ถ๊ฐ์ ๋ณด
    * Authenticated : ์ธ์ฆ ์ฌ๋ถ



<br/>


> SecurityContextHolder, SecurityContext

* SecurityContext
    * Authentication ๊ฐ์ฒด๊ฐ ์ ์ฅ๋๋ ๊ณณ 
    * SecurityContextHolder ์ ThreadLocal ์ ์ ์ฅ์ด ๋์ด ์๋ฌด๊ณณ์์๋ ์ฐธ์กฐ๊ฐ ๊ฐ๋ฅํ๋๋ก ์ค๊ณ๋จ
    * ์ธ์ฆ์ด ์๋ฃ๋๋ฉด HttpSessionSecurityContextRepository๊ฐ SecurityContext ์ Authentication ๊ฐ์ฒด๋ฅผ Session ์ ์ ์ฅํจ
* SecurityContextHolder
    * SecurityContext ๋ฅผ ์ ์ฅํ๋ ์ ์ฅ์๋ก ๋ด๋ถ์ ThreadLocal ํ๋๊ฐ ์กด์ฌ
    * SecurityContext ๋ฅผ ์ ์ฅํ๋ ๋ฐฉ์์ด ๋ค์๊ณผ ๊ฐ๋ค.
        ```java
    // ์์ ์ค๋ ๋์์๋ context๋ฅผ ์ฐธ์กฐํ  ์ ์๋๋ก ํ๋ค.
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        ```
        
        * MODE_THREADLOCAL : ์ค๋ ๋๋น SecurityContext ๊ฐ์ฒด๋ฅผ ํ ๋น. ๊ธฐ๋ณธ ๊ฐ
        * MODE_INHERITABLETHREADLOCAL : ๋ฉ์ธ ์ค๋ ๋์ ์์ ์ค๋ ๋์ ๊ดํด ๋์ผํ SecurityContext ๋ฅผ ์ ์ง(์ค๋ ๋๋ฅผ ์๋ก ์์ฑํด๋ ๊ฐ์ ThreadLocal ์ ๋ฐ๋ผ๋ณด๋๋ก)
            ```java
            @GetMapping("/thread")
            public String thread() {
                new Thread(() -> {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    // default ๊ฐ main์๋ง ์ ์ฅ์ ํ  ์ ์๋ ๋ชจ๋์ด๊ธฐ ๋๋ฌธ์ ์์ ์ค๋ ๋์์๋ SecurityContextHolder.getContext().getAuthentication();์์ ์ฐธ์กฐํ  ์ ์๋ค.
                }).start();
            
                return "thread";
            }
            ```
            * ๊ธฐ๋ณธ์ ์ผ๋ก new Thread ๋ด๋ถ์์๋ ์ค๋ ๋๋ฅผ ์ฐธ์กฐ ํ  ์ ์์ง๋ง (Thread Local ์ ์ ์ฅ๋์ด ์๊ธฐ ๋๋ฌธ์) MODE_INHERITABLETHREADLOCAL ๋ฅผ ์ฌ์ฉํ๋ฉด ์ฐธ์กฐ๊ฐ ๊ฐ๋ฅํด์ง๋ค.
        * MODE_GLOBAL : ์์ฉ ํ๋ก๊ทธ๋จ ๋ด์์ ๋จ ํ๋์ SecurityContext ์ ์ฅ
        
    * SecurityContextHolder.clear() ๋ก SecurityContextHolder ์ ์ ์ฅ๋ SecurityContext ๋ฅผ ์ ๊ฑฐ

* Session ์์ Authentication ์ฐธ์กฐ
    ```java
    Authentication authFromSession =
        ((SecurityContext) request
                                .getSession()
                                .getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)
        ).getAuthentication();
    ```
  * HttpSessionSecurityContextRepository ์์ "SPRING_SECURITY_CONTEXT" ์ผ๋ก ์ ์ฅ์ ํด๋๋ ๋ถ๋ถ์ด ์์

    


<br/>
    
    
> SecurityContextPersistenceFilter

* SecurityContext ๊ฐ์ฒด๋ฅผ ์์ฑ, ์ ์ฅ, ์กฐํํ๋ ์ญํ 
* SecurityContext ๋ฅผ ๊บผ๋ด์ด Session ์ ๋ฃ์ด์ฃผ๋ ๊ฒ๋ ์ด ๊ฐ์ฒด
* ๋ค์๊ณผ ๊ฐ์ ์ฒ๋ฆฌ
    * ์ต๋ช ์ฌ์ฉ์ ์ ์ ์ 
        * ์๋ก์ด SecurityContext ๊ฐ์ฒด ์์ฑ ํ SecurityContextHolder ์ ์ ์ฅ
        * ์ด ํ, AnonymousAuthenticationFilter ์์ AnonymousAuthenticationToken ์ ์์ฑํด SecurityContext ์ ์ ์ฅ
    * ์ธ์ฆ ์ 
        * ์๋ก์ด SecurityContext ๊ฐ์ฒด ์์ฑ ํ SecurityContextHolder ์ ์ ์ฅ
        * Authentication filter ๊ฐ ์ธ์ฆ์ ๋ง์น๋ฉด, SecurityContext ์ Authentication ๊ฐ์ฒด ์ ์ฅ
    * ์ธ์ฆ ํ ์์ฒญ์ด ๋ค์ด์ฌ ๋๋ง๋ค
        * Session ์์ SecurityContext ๋ฅผ ๊บผ๋ด์ด SecurityContextHolder ์ ์ ์ฅ
            * SecurityContext ๋ด๋ถ์ Authentication ๊ฐ์ฒด๋ฅผ ๋ฃ์ด์ ์ธ์ฆ ์ํ๋ฅผ ์ ์งํ๋ค.
    * ์ต์ข ์๋ต์ 
        * SecurityContextHolder.clearContext()

> ์ ์ฒด ํ๋ฆ

![default](./img/ee35553942e24c6e80665337321c22fe.png)
* SecurityContextPersistenceFilter ๋ HttpSessionSecurityContextRepository ๋ฅผ ํธ์ถํด์ Session ์ SecurityContext ๊ฐ์ฒด๊ฐ ์๋์ง ํ์ธ
    * ์๋ค๋ฉด SecurityContextHolder ์์ฑ ํ, ์ธ์ฆ์ ์ธ์ฆ ํํฐ์ ์ธ์ฆ์ ๋ง๊น
    * ์๋ค๋ฉด SecurityContextHolder ์์ฑ ํ Session ์ ์๋ SecurityContext (HttpSessionSecurityContextRepository ์์ get)์ ๊ฐ์ ธ์ setting (์ด ๋์๋ ์ค์ ํด๋, SecurityContextHolderStrategy ํธ์ถ)

![default](./img/3bbd7b3a88594c0d9d9fb9f4c87cb300.png)


 
<br/>


๐ ์ธ์ฆ ํ๋ฆ
-
> ์ ์ฒด

![default](./img/8446f9ea839a424c9214078ae86eeb5e.png)

> AuthenticationManager

![default](./img/7aff0f45eac74a3bb6f18740f81b7176.png)

![default](./img/a99daf63466040d78e0445f8d1f464a2.png)
* ์ด ๋ AuthenticationManager ๋ ์ธ์ฆ ์ฒ๋ฆฌ๋ฅผ ํ  ์ ์ ํ Provider ๋ฅผ ์ฐพ๋๋ฐ ๋ง์ฝ ์ฐพ์ง ๋ชปํ๋ฉด AuthenticationManager ๋ถ๋ชจ์ ๋ํ ์ฐธ์กฐ๋ฅผ ํ์ธํ๋ฉฐ ์ ์ ํ Provider ๋ฅผ ๊ฐ์ง AuthenticationManager ๋ฅผ ์ฐพ๋๋ค.

> AuthenticationProvider

![default](./img/9d454f57414c4275a2d9657e1f3aded6.png)
* support ๋ฉ์๋๋ฅผ ํตํด์, ์ ๋ฌ ๋ฐ์ Authentication ๊ฐ์ฒด์ ๋ํ ์ฒ๋ฆฌ๋ฅผ ์ง์ํ๋์ง ์ฌ๋ถ๋ฅผ ๋ฆฌํดํ๋ค.




<br/>

๐ ์ธ๊ฐ ํ๋ก์ธ์ค
-

> Authorization
* ์ด ์์์ด ํ๊ฐ ๋์๋๊ฐ ํ์ธํ๋ ์ ์ฐจ

![default](./img/73bf2c0cdb134377b6728bef03d1d062.png)

> ์ธ๊ฐ ์ฒ๋ฆฌ์ ๊ณ์ธต

![default](./img/84a28b7587424d5ea05c6be1c42e9812.png)
* ์คํ๋ง ์ํ๋ฆฌํฐ๋ 3๊ฐ์ง ๊ณ์ธต์ ๋ํด ์ธ๊ฐ ์ฒ๋ฆฌํ  ์ ์๋๋ก ์ ๊ณตํ๊ณ  ์์
    1. ์น ๊ณ์ธต : URL ๋จ์
    2. ์๋น์ค ๊ณ์ธต : Method ๋จ์
    3. ๋๋ฉ์ธ ๊ณ์ธต : ๊ฐ ๊ฐ์ฒด ๋จ์ 


> FilterSecurityInterceptor
* ๋ง์ง๋ง์ ์์นํ ํํฐ๋ก์ ์ธ๊ฐ ์ฒ๋ฆฌ๋ฅผ ํจ (์ต์ข ์น์ธ ์ฌ๋ถ๋ฅผ ๊ฒฐ์ ํจ)
* ์ธ์ฆ ๊ฐ์ฒด ์์ด ๋ณดํธ ์์์ ์ ๊ทผ์ ์๋ํ  ๊ฒฝ์ฐ AuthenticationException ๋ฐ์
* ์ธ์ฆ ํ ์์์ ๋ํ ๊ถํ์ด ์์ ๊ฒฝ์ฐ AccessDeniedException ๋ฐ์
* ๊ถํ ์ ์ด ๋ฐฉ์ ์ค HTTP ์์์ ๋ณด์์ ์ฒ๋ฆฌํ๋ ํํฐ (URL ์์ฒญ)
* ์ง์ ์ ์ธ ๊ถํ ์ฒ๋ฆฌ๋ AccessDeniedManager ์๊ฒ ์์

![default](./img/359ba7c8b34a45f68166eefb99f58ec0.png)
* ์์ฒญ ์ ๋ณด, ์ฌ์ฉ์ ์ธ์ฆ์ ๋ณด, SecurityMetadataSource์์ ๋ฝ์ ๊ถํ ์ ๋ณด๋ฅผ AccessDeniedManager ์๊ฒ ๋๊ฒจ ์ฌ์ฌ ์์ฒญ
* AccessDecisionManager ๋ ์ ์ ํ AccessDecisionVoter ๋ฅผ ํธ์ถํด์ ์ฌ์ฌ๋ฅผ ๋ง๊ธฐ๊ณ , ์ด๋ค์ ๊ฒฐ๊ณผ๋ฅผ ์ขํฉํ์ฌ ์น์ธ ์ฌ๋ถ๋ฅผ AccessDecisionManager ์๊ฒ ๋๊น
 
![default](./img/00e14587f9dc460088a14b609e6675e9.png)


> AccessDecisionManager

![default](./img/84700b76da0b4310a4a73acf9de93df2.png)
* AffirmativeBased : ํ๋๋ผ๋ ํ๊ฐ์ ํ๊ฐ
* ConsensusBased : ๊ณผ๋ฐ์. ๋์์ผ ๋์๋ allowIfEqualGrantedDeniedDecisions ์ต์์ด true ์ผ ๋ ํ๊ฐ
* UnanimousBased : ๋ง์ฅ์ผ์น์ ํ๊ฐ


> AccessDecisionVoter
* FilterSecurityInterceptor => AccessDecisionManager => AccessDecisionVoter ์ผ๋ก ์ฌ์ฌ์ ํ์ํ ์ ๋ณด๊ฐ ๋์ด์ด 
![default](./img/308c3a36d849429aa11712e7c6c4b495.png)

> AccessDecisionManager & AccessDecisionVoter ํ๋ฆ ์ ๋ฆฌ

![default](./img/7fc351ecff9c4319ae8a5d7e2699b2c9.png)

![default](./img/5c870ecccf5e4cb68dba2e8be08e0dfd.png)
* ์ด๋ voter ๋ฅผ ์ปค์คํ ํ  ๋, AccessDecisionManager ์๋ํ๋ ๋ฐฉ์์ ๋ฐ๋ผ์ AccessDecisionVoter ๊ฐ์ ์์ ๋ฑ๋ฑ์ ๊ณ ๋ คํด์ผ ํจ


> ์ฃผ์ ์ํคํ์ณ ์ด ์ ๋ฆฌ

![default](./img/2df35e9df962472381f5406d66829b12.png)

