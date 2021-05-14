📌 스프링 시큐리티 간단 시작하기
-

* 디팬던시 추가하기
```java
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

<br/>

* 로깅 디버그 모드로 변경
```java
logging:
  level:
    org.springframework: DEBUG
```

<br/>

* 콘솔을 통해서 받은 비밀번호를 해더에 담아서 보내야 함

```js
Using generated security password: 00f89527-db8c-4a6d-8370-bb1e50b403a6
```
* 이 때 기본 username은 "user"가 되는데 API 개발 툴인 Postman에 Authorization 항목에 Basic Auth 값으로 user와 위 비밀번호를 추가해서 입력하면 Authorization 헤더로 값이 추가가 됩니다.


<br/>

* 아래는 위 비밀번호와 유저이름을 사용해서 만들어진 헤더입니다.
```java
Authorization : Basic dXNlcjowMGY4OTUyNy1kYjhjLTRhNmQtODM3MC1iYjFlNTBiNDAzYTY=
```
<br/>


* 이 때 Authorization 헤더의 문법은 아래와 같습니다.

```java
Authorization : <type> <value>
```
* 위에서 사용한 Basic type의 경우 username과 password를 콜론(:)으로 이어 붙여 base64로 인코딩 한 값입니다.
* 그 증거로 위에서 얻은 헤더 값을 아래와 같이 디코딩하면 아래와 같은 결과를 얻을 수 있습니다.

<br/>


* 디코딩하기
```java
String t = "dXNlcjowMGY4OTUyNy1kYjhjLTRhNmQtODM3MC1iYjFlNTBiNDAzYTY=";
String result = new String(Base64.getDecoder().decode(t));
System.out.println(result);

// 출력
user:00f89527-db8c-4a6d-8370-bb1e50b403a6 
```
* 출력된 내용을 보면 user에 얻은 비밀번호 값이 들어있음을 알 수 있다. 저 방법 대신 설정파일에 username과 비밀번호를 직접 작성할 수 있다.


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

* 허나 위 같이 고정된 비밀번호 값을 이용 하는 것은 보안에 문제가 될 수 있으므로 별도의 방법이 필요함


<br/>

📌 jwt 로 스프링 시큐리티 관리하기
-
* [이 파일을 참고해주세요.](./SpringSecurityWithJWT.md) 






