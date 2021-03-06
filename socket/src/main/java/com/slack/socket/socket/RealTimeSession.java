package com.slack.socket.socket;

import com.slack.socket.socket.model.UserId;
import com.slack.slack.common.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

/**
 * 웹 소켓 통신을 위해서 스프링이 지원하는 WebSocketSession 을
 * 한번 감싸서 편하게 사용할 수 있게 만든 wrapper 클래스
 *
 * 사용자 별로 세션을 생성할 수 있도록 했습니다.
 *
 * 토큰을 추출하거나, 각각의 세션에 메세지를 보내는 역할을 수행합니다.
 * A wrapper over {@link WebSocketSession} to add convenient methods
 */
public class RealTimeSession {

  private static final Logger log = LoggerFactory.getLogger(RealTimeSession.class);
  private static final String KEY_USER_ID = "KEY_USER_ID";

  private WebSocketSession session;

  public RealTimeSession(WebSocketSession session) {
    this.session = session;
  }

  public String id() {
    return session.getId();
  }

  public WebSocketSession wrapped() {
    return session;
  }

  public void setUserId(UserId userId) {
    addAttribute(KEY_USER_ID, userId);
  }

  public UserId getUserId() {
    return getAttribute(KEY_USER_ID);
  }

  void addAttribute(String key, Object value) {
    session.getAttributes().put(key, value);
  }

  @SuppressWarnings("unchecked")
  public <T> T getAttribute(String key) {
    Object value = session.getAttributes().get(key);
    if (value == null) {
      return null;
    }
    return (T) value;
  }

  public String getToken() {
    URI uri = session.getUri();
    UriComponents uriComponents = UriComponentsBuilder.fromUri(uri).build();
    return uriComponents.getQueryParams().getFirst("token");
  }

  public void error(String error) {
    sendMessage(WebSocketMessages.error(error));
  }

  public void fail(String failure) {
    sendMessage(WebSocketMessages.failure(failure));
  }

  public void reply(String reply) {
    sendMessage(WebSocketMessages.reply(reply));
  }

  private void sendMessage(Object message) {
    try {
      String textMessage = JsonUtils.toJson(message);
      session.sendMessage(new TextMessage(textMessage));
    } catch (IOException e) {
      log.error("Failed to send message through web socket session", e);
    }
  }
}
