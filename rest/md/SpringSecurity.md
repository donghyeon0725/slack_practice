๐ ์ฌํ
-
* [์คํ๋ง ์ํ๋ฆฌํฐ ๊ธฐ๋ณธ ๊ตฌ์กฐ & ํ๋ฆ](./SpringSecurityFirst.md) ๋ฅผ ์ฐธ๊ณ ํด์ฃผ์ธ์.
* [์คํ๋ง ์ํ๋ฆฌํฐ ์ฃผ์ ์ํคํ์ณ](./SpringSecuritySecond.md) ๋ฅผ ์ฐธ๊ณ ํด์ฃผ์ธ์.
* [Form ์ธ์ฆ & Ajax ์ธ์ฆ DB ์ฐ๋](./SpringSecurityAuthentication.md) ๋ฅผ ์ฐธ๊ณ ํด์ฃผ์ธ์.
* [์ธ๊ฐ ํ๋ก์ธ์ค DB ์ฐ๋ (Url)](./SpringSecurityAuthoizedURL.md) ๋ฅผ ์ฐธ๊ณ ํด์ฃผ์ธ์.
* [์ธ๊ฐ ํ๋ก์ธ์ค DB ์ฐ๋ (Method)](./SpringSecurityAuthoizedMethod.md) ๋ฅผ ์ฐธ๊ณ ํด์ฃผ์ธ์.

<br/>


๐ ์คํ๋ง ์ํ๋ฆฌํฐ ๊ฐ๋จ ์์ํ๊ธฐ
-

* ๋ํฌ๋์ ์ถ๊ฐํ๊ธฐ
```java
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

<br/>

* ๋ก๊น ๋๋ฒ๊ทธ ๋ชจ๋๋ก ๋ณ๊ฒฝ
```java
logging:
  level:
    org.springframework: DEBUG
```

<br/>

* ์ฝ์์ ํตํด์ ๋ฐ์ ๋น๋ฐ๋ฒํธ๋ฅผ ํด๋์ ๋ด์์ ๋ณด๋ด์ผ ํจ

```js
Using generated security password: 00f89527-db8c-4a6d-8370-bb1e50b403a6
```
* ์ด ๋ ๊ธฐ๋ณธ username์ "user"๊ฐ ๋๋๋ฐ API ๊ฐ๋ฐ ํด์ธ Postman์ Authorization ํญ๋ชฉ์ Basic Auth ๊ฐ์ผ๋ก user์ ์ ๋น๋ฐ๋ฒํธ๋ฅผ ์ถ๊ฐํด์ ์๋ ฅํ๋ฉด Authorization ํค๋๋ก ๊ฐ์ด ์ถ๊ฐ๊ฐ ๋ฉ๋๋ค.


<br/>

* ์๋๋ ์ ๋น๋ฐ๋ฒํธ์ ์ ์ ์ด๋ฆ์ ์ฌ์ฉํด์ ๋ง๋ค์ด์ง ํค๋์๋๋ค.
```java
Authorization : Basic dXNlcjowMGY4OTUyNy1kYjhjLTRhNmQtODM3MC1iYjFlNTBiNDAzYTY=
```
<br/>


* ์ด ๋ Authorization ํค๋์ ๋ฌธ๋ฒ์ ์๋์ ๊ฐ์ต๋๋ค.

```java
Authorization : <type> <value>
```
* ์์์ ์ฌ์ฉํ Basic type์ ๊ฒฝ์ฐ username๊ณผ password๋ฅผ ์ฝ๋ก (:)์ผ๋ก ์ด์ด ๋ถ์ฌ base64๋ก ์ธ์ฝ๋ฉ ํ ๊ฐ์๋๋ค.
* ๊ทธ ์ฆ๊ฑฐ๋ก ์์์ ์ป์ ํค๋ ๊ฐ์ ์๋์ ๊ฐ์ด ๋์ฝ๋ฉํ๋ฉด ์๋์ ๊ฐ์ ๊ฒฐ๊ณผ๋ฅผ ์ป์ ์ ์์ต๋๋ค.

<br/>


* ๋์ฝ๋ฉํ๊ธฐ
```java
String t = "dXNlcjowMGY4OTUyNy1kYjhjLTRhNmQtODM3MC1iYjFlNTBiNDAzYTY=";
String result = new String(Base64.getDecoder().decode(t));
System.out.println(result);

// ์ถ๋ ฅ
user:00f89527-db8c-4a6d-8370-bb1e50b403a6 
```
* ์ถ๋ ฅ๋ ๋ด์ฉ์ ๋ณด๋ฉด user์ ์ป์ ๋น๋ฐ๋ฒํธ ๊ฐ์ด ๋ค์ด์์์ ์ ์ ์๋ค. ์  ๋ฐฉ๋ฒ ๋์  ์ค์ ํ์ผ์ username๊ณผ ๋น๋ฐ๋ฒํธ๋ฅผ ์ง์  ์์ฑํ  ์ ์๋ค.


<br/>

* application.yml
```java
spring:
  security:
    user:
      name: username
      password: passw0rd
```



<br/>

* ํ๋ ์ ๊ฐ์ด ๊ณ ์ ๋ ๋น๋ฐ๋ฒํธ ๊ฐ์ ์ด์ฉ ํ๋ ๊ฒ์ ๋ณด์์ ๋ฌธ์ ๊ฐ ๋  ์ ์์ผ๋ฏ๋ก ๋ณ๋์ ๋ฐฉ๋ฒ์ด ํ์ํจ


<br/>

๐ jwt ๋ก ์คํ๋ง ์ํ๋ฆฌํฐ ๊ด๋ฆฌํ๊ธฐ
-
* [์ด ํ์ผ์ ์ฐธ๊ณ ํด์ฃผ์ธ์.](./SpringSecurityWithJWT.md) 








