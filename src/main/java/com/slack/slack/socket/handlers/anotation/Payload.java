package com.slack.slack.socket.handlers.anotation;

import java.lang.annotation.*;

/**
 * 채널 핸들링에 필요한 데이터
 * Mark a parameter as the payload in an action method of channel handler.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Payload {
}
