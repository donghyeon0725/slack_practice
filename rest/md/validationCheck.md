๐ ์ ํจ์ฑ ๊ฒ์ฌ
-

์คํ๋ง ๋ถํธ์์๋ Entity์ ์ด๋ธํ์ด์์ ์ถ๊ฐํ๋ ๋ฐฉ๋ฒ์ผ๋ก ์ ํจ์ฑ ๊ฒ์ฌ๋ฅผ ํ๋ค.
์ด๋ฅผ ์ํด์๋ ์๋์ ๊ฐ์ ์ผ๊ด์ ์ธ ์์์ ๋ง์น๋ค.


<br/>


1. ์์กด์ฑ ์ถ๊ฐ

```html
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

2. ์ด๋ธํ์ด์์ Entity์ ์ถ๊ฐ

* @NotNull : null์ ์๋จ
* @NotBlank : ๋น๋ฌธ์์ด์ ์๋จ
* @Past : ๋ ์ง๋ ํ์ฌ๋ณด๋ค ๊ณผ๊ฑฐ์ฌ์ผํจ(Future ๋ฑ๋ฑ ๋ฏธ๋๋ ์ค์  ๊ฐ๋ฅ)
* @Size(min=8, message = "๋น๋ฐ๋ฒํธ๋ ์ต์ 8์๋ฆฌ ์ด์์ด์ด์ผ ํฉ๋๋ค") : ํฌ๊ธฐ, ๋ฉ์ธ์ง ๋ฑ ์์ธ ์ค์  ๊ฐ๋ฅ

3. validation check ํ๊ณ  ์ถ์ dto์ @Valid ์ด๋ธํ์ด์ ์ถ๊ฐํ๊ธฐ


<br/>


๐ ์ ํจ์ฑ ์ฒดํฌ์ ๋ํด, error handling ํ๊ธฐ
-

1. ์๋ฌ ํธ๋ค๋ง ํ๋ ํด๋์ค์  ResponseEntityExceptionHandler ์์ ์ถ๊ฐํ๊ธฐ
2. ์๋ ๋ฉ์๋ override ํ๊ธฐ
```java
@Override
public final ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                        HttpHeaders headers,
                                                                        HttpStatus status,
                                                                        WebRequest request) {

    final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, ex.getBindingResult());

    return new ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST);
}
```




