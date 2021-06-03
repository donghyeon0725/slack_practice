package com.slack.slack.error;

import com.slack.slack.error.exception.*;
import com.slack.slack.error.repository.ErrorRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

// AOP 사용할 수 있는 어노테이션으로써, 모든 컴트롤러가 실행 될 때, 사전에 실행 되는 컨트롤러가 됨
@ControllerAdvice
@RestController
@AllArgsConstructor
public class CustomExceptionController extends ResponseEntityExceptionHandler {

    private ErrorRepository errorRepository;

    // 이 메소드가 Exception Handler로 사용될 수 있음을 알리는 핸들러 클래스
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorResponse> handlerAllException(Exception ex, WebRequest request) {
        final ErrorResponse exceptionResponse =
                // getDescription 은 에러 내용에 관한 것 같다.
                ErrorResponse.of(ErrorCode.UNEXPECTED_SERVER_ACTION, ex);

        // 에러 로그 저장
        errorRepository.save(exceptionResponse);

        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @Override
    public final ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                            HttpHeaders headers,
                                                                            HttpStatus status,
                                                                            WebRequest request) {

        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE_ARGUMENT, ex.getBindingResult());

        return new ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInputException.class)
    public final ResponseEntity<ErrorResponse> handlerInvalidInputException(InvalidInputException e, WebRequest request) {
        final ErrorResponse exceptionResponse =
                ErrorResponse.of(e.getErrorCode(), e);

        errorRepository.save(exceptionResponse);

        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public final ResponseEntity<ErrorResponse> handlerInvalidTokenException(InvalidTokenException e, WebRequest request) {
        final ErrorResponse exceptionResponse =
                ErrorResponse.of(e.getErrorCode(), e);

        errorRepository.save(exceptionResponse);

        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MailLoadFailException.class)
    public final ResponseEntity<ErrorResponse> handlerMailLoadFailException(MailLoadFailException e, WebRequest request) {
        final ErrorResponse exceptionResponse =
                ErrorResponse.of(e.getErrorCode(), e);

        errorRepository.save(exceptionResponse);

        return new ResponseEntity(exceptionResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ErrorResponse> handlerResourceNotFoundException(ResourceNotFoundException e, WebRequest request) {
        final ErrorResponse exceptionResponse =
                ErrorResponse.of(e.getErrorCode(), e);

        errorRepository.save(exceptionResponse);

        return new ResponseEntity(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<ErrorResponse> handlerUserNotFoundException(UserNotFoundException e, WebRequest request) {
        final ErrorResponse exceptionResponse =
                ErrorResponse.of(e.getErrorCode(), e);

        errorRepository.save(exceptionResponse);

        return new ResponseEntity(exceptionResponse, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(ResourceConflict.class)
    public final ResponseEntity<ErrorResponse> handlerResourceConflict(ResourceConflict e, WebRequest request) {
        final ErrorResponse exceptionResponse =
                ErrorResponse.of(e.getErrorCode(), e);

        errorRepository.save(exceptionResponse);

        return new ResponseEntity(exceptionResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public final ResponseEntity<ErrorResponse> handlerUnauthorizedException(UnauthorizedException e, WebRequest request) {
        final ErrorResponse exceptionResponse =
                ErrorResponse.of(e.getErrorCode(), e);

        errorRepository.save(exceptionResponse);

        return new ResponseEntity(exceptionResponse, HttpStatus.NOT_FOUND);
    }





}
