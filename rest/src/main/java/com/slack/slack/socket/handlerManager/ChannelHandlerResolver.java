package com.slack.slack.socket.handlerManager;

import com.slack.slack.socket.handlers.anotation.ChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 채널 별로 소켓을 관리하기 위해
 * 스프링 빈에서 핸들러를 찾아 별도로 저장해두고
 * 필요할 때 찾아주는 리졸버
 * */
@Component
public class ChannelHandlerResolver {

  private static final Logger log = LoggerFactory.getLogger(ChannelHandlerResolver.class);

  private static final AntPathMatcher antPathMatcher = new AntPathMatcher();
  // The key is the channel ant-like path pattern, value is the corresponding invoker
  private final Map<String, ChannelHandlerInvoker> invokers = new HashMap<>();

  private ApplicationContext applicationContext;

  /**
   * 특정 스프링 컨테이너에 들어있는 bean을 호출하기 위해서 ApplicationContext을 spring 으로 부터 주입 받습니다.
   * 그리고 bootstrap 메소드를 호출함으로써 이전에 발행하지 않은 채널이라면, 새롭게 발행합니다.
   * */
  public ChannelHandlerResolver(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
    this.bootstrap();
  }

  /**
   * 적절한 핸들러를 찾아 줍니다.
   * */
  public ChannelHandlerInvoker findInvoker(IncomingMessage incomingMessage) {
    ChannelHandlerInvoker invoker = null;
    Set<String> pathPatterns = invokers.keySet();
    for (String pathPattern : pathPatterns) {
      if (antPathMatcher.match(pathPattern, incomingMessage.getChannel())) {
        invoker = invokers.get(pathPattern);
      }
    }
    if (invoker == null) {
      return null;
    }
    return invoker.supports(incomingMessage.getAction()) ? invoker : null;
  }

  /**
   * 생성할 때, 모든 패턴의 ChannelHandler 를 저장소에 넣어 둡니다.
   * */
  private void bootstrap() {
    log.info("Bootstrapping channel handler resolver");

    // ChannelHandler 라는 어노테이션이 붙은 bean(@component)를 찾습니다.
    Map<String, Object> handlers = applicationContext.getBeansWithAnnotation(ChannelHandler.class);
    // 어노테이션이 붙은 모든 component를 찾아서 사용자가 발행한 채널과 동일한 패턴의 핸들러를 호출 합니다.
    for (String handlerName : handlers.keySet()) {
      Object handler = handlers.get(handlerName);
      Class<?> handlerClass = handler.getClass();

      ChannelHandler handlerAnnotation = handlerClass.getAnnotation(ChannelHandler.class);
      String channelPattern = ChannelHandlers.getPattern(handlerAnnotation);

      // 만약 핸들러 저장소에 이미 생성한 핸들러라면 오류를 내고, 생성하지 않습니다. => 중복된 핸들러 키값은 사용할 수 없습니다.
      if (invokers.containsKey(channelPattern)) {
        throw new IllegalStateException("Duplicated handlers found for chanel pattern `" + channelPattern + "`.");
      }
      invokers.put(channelPattern, new ChannelHandlerInvoker(handler));
      log.debug("Mapped channel `{}` to channel handler `{}`", channelPattern, handlerClass.getName());
    }
  }
}
