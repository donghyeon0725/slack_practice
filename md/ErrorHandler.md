에러 핸들러를 작성하기 이전에
-

📌 효율적으로 에러 코드를 관리하려면
-
1. 모든 개발자가, 에러 상황에 대해서 동일하게 에러를 핸들링 할 수 있도록 에러 코드를 정의한 enum 클래스 생성
2. enum 클래스로 정의한 에러코드에 부가적인 정보를 가진 응답객체(Response) 생성 
3. 핸들러에 원하는 에러 코드로 응답 객체를 생성 후 리턴



<br/>


📌 1. 에러 코드
-
```java
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 에러 코드를 모아둔 곳
 *
 * 에러코드의 정보를 담을 필드, 생성자를 정의함으로써
 * 미리 정해둔 에러 코드 값을 이 클래스 외부에서 꺼내어 사용 할 수 있음
 * */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(400, "C001", " Invalid Input Value"),
    METHOD_NOT_ALLOWED(405, "C002", " Invalid Input Value"),
    ENTITY_NOT_FOUND(400, "C003", " Entity Not Found"),
    INTERNAL_SERVER_ERROR(500, "C004", "Server Error"),
    INVALID_TYPE_VALUE(400, "C005", " Invalid Type Value"),
    HANDLE_ACCESS_DENIED(403, "C006", "Access is Denied"),


    // Member
    EMAIL_DUPLICATION(400, "M001", "Email is Duplication"),
    LOGIN_INPUT_INVALID(400, "M002", "Login input is invalid"),

    // Coupon
    COUPON_ALREADY_USE(400, "CO001", "Coupon was already used"),
    COUPON_EXPIRE(400, "CO002", "Coupon was already expired");

    private final String code;
    private final String message;
    private int status;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public String getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }
}
```
* enum 클래스가 가져야할 필드 정의 + 생성자 정의
* 상황별로 사용할 수 있는 에러코드를 미리 정의 (위 작업이 선행 되어야만 가능)



<br/>

📌 2. 응답 객체
-
```java
import com.slack.slack.error.exception.ErrorCode;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="log_api")
public class ErrorResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = true)
    private Integer Id;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private int status;

    @Transient
    private List<FieldError> errors;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private Date date;


    /**
     * 생성자로 접근하는 것을 막음으로써
     * 다른 개발자가 직접 에러코드 생성하는 것을 막을 수 있음
     *
     * 오직 of로만 접근이 가능하고, ErrorCode 라는 enum 클래스만 받아서 만들 수 있기 때문에
     * 코드의 일관성이 증가함
     * */
    private ErrorResponse(final ErrorCode code, final List<FieldError> errors) {
        this.message = code.getMessage();
        this.status = code.getStatus();
        this.errors = errors;
        this.code = code.getCode();
        this.date = new Date();
    }

    private ErrorResponse(final ErrorCode code) {
        this.message = code.getMessage();
        this.status = code.getStatus();
        this.code = code.getCode();
        this.errors = new ArrayList<>();
        this.date = new Date();
    }


    public static ErrorResponse of(final ErrorCode code, final BindingResult bindingResult) {
        return new ErrorResponse(code, FieldError.of(bindingResult));
    }

    public static ErrorResponse of(final ErrorCode code) {
        return new ErrorResponse(code);
    }

    public static ErrorResponse of(final ErrorCode code, final List<FieldError> errors) {
        return new ErrorResponse(code, errors);
    }

    public static ErrorResponse of(MethodArgumentTypeMismatchException e) {
        final String value = e.getValue() == null ? "" : e.getValue().toString();
        final List<ErrorResponse.FieldError> errors = ErrorResponse.FieldError.of(e.getName(), value, e.getErrorCode());
        return new ErrorResponse(ErrorCode.INVALID_TYPE_VALUE, errors);
    }


    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FieldError {
        private String field;
        private String value;
        private String reason;

        private FieldError(final String field, final String value, final String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static List<FieldError> of(final String field, final String value, final String reason) {
            List<FieldError> fieldErrors = new ArrayList<>();
            fieldErrors.add(new FieldError(field, value, reason));
            return fieldErrors;
        }

        private static List<FieldError> of(final BindingResult bindingResult) {
            final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }
    }

}
```
* DB에 데이터를 저장할 수 있도록 JPA 어노테이션을 사용했다. 
* 핵심은 기본 생성자를 private 으로 선언한 뒤, of 라는 메소드를 통해서 "에러를 생성할 수 있는 방법"을 제한 하고 있다는 점이다.
* 오직 of로만 접근이 가능하고, ErrorCode 라는 enum 클래스만 받아서 만들 수 있기 때문에(개발자가 직접 ErrorResponse 생성하는 것이 불가능하기 때문에) 코드의 일관성이 증가한다.



<br/>


📌 3. 핸들러 생성
```java
// AOP 사용할 수 있는 어노테이션으로써, 모든 컴트롤러가 실행 될 때, 사전에 실행 되는 컨트롤러가 됨
@ControllerAdvice
@RestController
@AllArgsConstructor
public class CustomExceptionController {

    private ErrorRepository errorRepository;

    // 이 메소드가 Exception Handler로 사용될 수 있음을 알리는 핸들러 클래스
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorResponse> handlerAllException(Exception ex, WebRequest request) {
        final ErrorResponse exceptionResponse =
                // getDescription 은 에러 내용에 관한 것 같다.
                ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE);

        // 에러 로그 저장
        errorRepository.save(exceptionResponse);

        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

}

```


<br/>

📌 테스트
-
1. 2번 커밋 (2. git add Error Handler)으로 되돌린다.
2. 포스트맨(https://www.postman.com/)을 켠다. 또는 브라우저를 켠다.
3. http://localhost:8080/test 를 입력한다. (GET 메소드로 호출)
4. 로그확인. 아래와 같은 로그가 남으면 정상
```html
Hibernate: insert into log_api (id, code, date, message, status) values (null, ?, ?, ?, ?)
```
5. 또는 http://localhost:8080/h2-console 에 들어 갔을 때 log_api를 조회해서 1건 이상의 데이터가 있으면 정상
