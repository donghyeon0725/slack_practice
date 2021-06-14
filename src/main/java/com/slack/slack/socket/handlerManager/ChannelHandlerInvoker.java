package com.slack.slack.socket.handlerManager;


import com.slack.slack.socket.RealTimeSession;
import com.slack.slack.socket.handlers.anotation.Action;
import com.slack.slack.socket.handlers.anotation.ChannelHandler;
import com.slack.slack.socket.handlers.anotation.ChannelValue;
import com.slack.slack.socket.handlers.anotation.Payload;
import com.slack.slack.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 사용자로부터 받은 채널의 정보와
 * 핸들러에 정의된 메소드를 적절하게 호출해서
 * 채널을 관리할 수 있게 해주는 Invoker 입니다.
 *
 * 핸들러를 한번 감싸서 보다 편리하게 사용할 수 있도록 합니다.
 * */
public class ChannelHandlerInvoker {

  private static final Logger log = LoggerFactory.getLogger(ChannelHandlerInvoker.class);

  private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

  private String channelPattern;
  private Object handler;
  // Key is the action, value is the method to handle that action
  private final Map<String, Method> actionMethods = new HashMap<>();

  /**
   * 핸들러를 받고, 유효성 검사를 진행합니다.
   *
   * 핸들러에 Action 어노테이션이 붙은 메소드를 모두 찾아서
   * 핸들러와 함께 저장합니다.
   * */
  public ChannelHandlerInvoker(Object handler) {
    Assert.notNull(handler, "Parameter `handler` must not be null");

    Class<?> handlerClass = handler.getClass();
    ChannelHandler handlerAnnotation = handlerClass.getAnnotation(ChannelHandler.class);
    Assert.notNull(handlerAnnotation, "Parameter `handler` must have annotation @ChannelHandler");

    // handlerClass 로 부터 메소드를 추출 합니다.
    Method[] methods = handlerClass.getMethods();
    for (Method method : methods) {
      Action actionAnnotation = method.getAnnotation(Action.class);
      // Action 어노테이션이 없는 메소드는 지나 칩니다.
      if (actionAnnotation == null) {
        continue;
      }

      // action 어노테이션에 붙은 이름 앞으로, 메소드를 할당 해둡니다. => 이름으로 쉽게 핸들링 할 수 있도록
      String action = actionAnnotation.value();
      actionMethods.put(action, method);
      log.debug("Mapped action `{}` in channel handler `{}#{}`", action, handlerClass.getName(), method);
    }

    // 패턴과 핸들러를 함께 저장 해 둡니다.
    this.channelPattern = ChannelHandlers.getPattern(handlerAnnotation);
    this.handler = handler;
  }

  /**
   * 핸들러가 지원하는 메소드 인지 여부를 판단합니다.
   * */
  public boolean supports(String action) {
    return actionMethods.containsKey(action);
  }

  /**
   * 클라이언트가 보낸 정보를 통해서 핸들러가 핸들링 할 수 있는 메소드를 호출합니다.
   * 이때 클라이언트는 아래와 같은 세가지 정보를 보냅니다.
   * 1. 채널
   * 2. 액션 (동작)
   * 3. 페이로드 (동작에 필요한 데이터)
   * 이를 IncomingMessage 에 담아서 핸들러를 호출 합니다.
   * */
  public void handle(IncomingMessage incomingMessage, RealTimeSession session) {
    // 채널의 유효성 검사를 진행합니다. => 리졸버가 찾은 핸들러의 패턴이 사용자의 패턴과 일치하지 않으면 에러를 내뱉습니다.
    Assert.isTrue(antPathMatcher.match(channelPattern, incomingMessage.getChannel()), "Channel of the handler must match");

    // 클라이언트가 전달한 actionMethod를 추출해서 적절한 action 메소드를 찾고 찾은 메소드의 null 여부를 체크 합니다.
    Method actionMethod = actionMethods.get(incomingMessage.getAction());
    Assert.notNull(actionMethod, "Action method for `" + incomingMessage.getAction() + "` must exist");

    // 필요한 파라미터의 타입을 모두 찾습니다.
    Class<?>[] parameterTypes = actionMethod.getParameterTypes();
    // 각 매개변수의 어노테이션을 찾습니다.
    Annotation[][] allParameterAnnotations = actionMethod.getParameterAnnotations();
    // The arguments that will be passed to the action method
    Object[] args = new Object[parameterTypes.length];

    try {
      // 각 파라미터를 계산합니다.
      for (int i = 0; i < parameterTypes.length; i++) {
        Class<?> parameterType = parameterTypes[i];
        // 해당 파라미너의 어노테이션
        Annotation[] parameterAnnotations = allParameterAnnotations[i];

        // 어노테이션이 없는 경우
        if (parameterAnnotations.length == 0) {
          if (parameterType.isInstance(session)) {
            // 해당 어노테이션이 세션의 인스턴스이면 할당합니다.
            args[i] = session;
          } else {
            args[i] = null;
          }
          continue;
        }

        // 파라미터에 적용된 어노테이션 중, 오직 첫번째 요소만 사용하기 위해 처리합니다.
        Annotation parameterAnnotation = parameterAnnotations[0];
        if (parameterAnnotation instanceof Payload) {
          // 페이로드 데이터를 파라미터로 준 경우, 사용자가 보낸 페이로드를 핸들러에 전달합니다.
          Object arg = JsonUtils.toObject(incomingMessage.getPayload(), parameterType);
          // null check
          if (arg == null) {
            throw new IllegalArgumentException("Unable to instantiate parameter of type `" +
              parameterType.getName() + "`.");
          }
          args[i] = arg;

        } else if (parameterAnnotation instanceof ChannelValue) {
          // 체널 데이터인 경우 해당 파라미터에 채널 데이터를 할당
          args[i] = incomingMessage.getChannel();
        }
      }

      // 메소드 호출
      actionMethod.invoke(handler, args);
    } catch (Exception e) {
      String error = "Failed to invoker action method `" + incomingMessage.getAction() +
        "` at channel `" + incomingMessage.getChannel() + "` ";
      log.error(error, e);
      session.error(error);
    }
  }
}
