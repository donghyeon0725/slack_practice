package com.slack.slack.socket.handlers.anotation;

import com.slack.slack.socket.WebSocketRequestDispatcher;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * ChannelHandler 어노테이션에는 자동으로 @Component 어노테이션이 붙기 때문에,
 * 별도로 어노테이션을 추가할 필요가 없게 됩니다.
 *
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ChannelHandler {

  /**
   * Channel patter, alias of value()
   */
  String pattern() default "";

  /**
   * The channel pattern that the handler will be mapped to by {@link WebSocketRequestDispatcher}
   * using Spring's {@link org.springframework.util.AntPathMatcher}
   */
  String value() default "";

}
