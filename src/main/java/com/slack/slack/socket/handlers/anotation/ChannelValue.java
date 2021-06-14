package com.slack.slack.socket.handlers.anotation;

import java.lang.annotation.*;

/**
 * Mark a parameter as the channel's value
 * 채널에 들어갈 값
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ChannelValue {
}
