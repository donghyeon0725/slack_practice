package com.slack.slack.error;

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

    // Entity에서 제외하고 싶은 필드는 아래와 같은 어노테이션으로 처리한다.
    @Transient
    private List<FieldError> errors;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private Date date;


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
