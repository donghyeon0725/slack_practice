📌 유효성 검사
-

스프링 부트에서는 Entity에 어노테이션을 추가하는 방법으로 유효성 검사를 한다.
이를 위해서는 아래와 같은 일괄적인 작업을 마친다.


<br/>


1. 의존성 추가

```html
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

2. 어노테이션을 Entity에 추가

* @NotNull : null은 안됨
* @NotBlank : 빈문자열은 안됨
* @Past : 날짜는 현재보다 과거여야함(Future 등등 미래도 설정 가능)
* @Size(min=8, message = "비밀번호는 최소 8자리 이상이어야 합니다") : 크기, 메세지 등 상세 설정 가능

3. validation check 하고 싶은 dto에 @Valid 어노테이션 추가하기


<br/>


📌 유효성 체크에 대해, error handling 하기
-

1. 에러 핸들링 하는 클래스에  ResponseEntityExceptionHandler 상속 추가하기
2. 아래 메소드 override 하기
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




