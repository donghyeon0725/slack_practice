package com.slack.slack.socket;

import com.slack.slack.socket.model.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * RealTimeSession 에서 사용자 정보를 찾아
 * 해당 사용자가 어떤 채널을 구독했는지에 대한 정보를 관리합니다.
 * */
public final class SubscriptionHub {

  private static final Logger log = LoggerFactory.getLogger(SubscriptionHub.class);
  // 키값은, 구독한 채널의 value 입니다. => 채널에 어떤 세션이 할당되었는가 대한 정보
  private static final Map<String, Set<WebSocketSession>> subscriptions = new HashMap<>();
  // 클라이언트가 구독한 채널을 유지하기 위함
  // 키는 세션 아이디이며, 값은 구독한 채널의 값 입니다.
  // 사용자가 어떤 채널 값을 구독하였는가 확인하기 위함
  private static final Map<String, Set<String>> subscribedChannels = new HashMap<>();

  /**
   * 구독하기
   * */
  public static void subscribe(RealTimeSession session, String channel) {
    // 채널이 비었는지 확인
    Assert.hasText(channel, "Parameter `channel` must not be null");

    // subscriptions 에서 channel 에 대한 값이 존재하면 가져오고, 없으면 2번째 인자로 전달한 값을 반환 합니다. => 누군가 채널을 발행한 적이 없다면 새 채널을 발행합니다.
    Set<WebSocketSession> subscribers = subscriptions.computeIfAbsent(channel, k -> new HashSet<>());
    // 세션 값을 가져옵니다.
    subscribers.add(session.wrapped());

    UserId userId = session.getUserId();
    log.debug("RealTimeSession[{}] Subscribed user[id={}] to channel `{}`", session.id(), userId, channel);

    // 사용자가 구독한 채널이 없다면 새로운 HashSet을 발행하고 있다면 채널에 추가를 합니다.
    Set<String> channels = subscribedChannels.computeIfAbsent(session.id(), k -> new HashSet<>());
    channels.add(channel);
  }

  /**
   * 구독 해제하기
   * */
  public static void unsubscribe(RealTimeSession session, String channel) {
    Assert.hasText(channel, "Parameter `channel` must not be empty");
    Assert.notNull(session, "Parameter `session` must not be null");

    //소켓 세션 앞으로 저장된 채널 값을 제거 합니다.
    Set<WebSocketSession> subscribers = subscriptions.get(channel);
    if (subscribers != null) {
      // 제거
      subscribers.remove(session.wrapped());
      UserId userId = session.getUserId();
      log.debug("RealTimeSession[{}] Unsubscribed user[id={}] from channel `{}`", session.id(), userId, channel);
    }

    // 채널 앞으로 할당된 세션 아이디를 모두 제거합니다.
    Set<String> channels = subscribedChannels.get(session.id());
    if (channels != null) {
      channels.remove(channel);
    }
  }

  /**
   * 사용자의
   * 모든 채널 구독 취소
   * */
  public static void unsubscribeAll(RealTimeSession session) {
    Set<String> channels = subscribedChannels.get(session.id());
    if (channels == null) {
      log.debug("RealTimeSession[{}] No channels to unsubscribe.", session.id());
      return;
    }

    for (String channel: channels) {
      unsubscribe(session, channel);
    }

    // Remove the subscribed channels
    subscribedChannels.remove(session.id());
  }

  /**
   * 채널에 저장된 소켓에 메세지를 보냅니다.
   * */
  public static void send(String channel, String update) {
    Assert.hasText(channel, "Parameter `channel` must not be empty");
    Assert.hasText(update, "Parameter `update` must not be null");

    Set<WebSocketSession> subscribers = subscriptions.get(channel);
    if (subscribers == null || subscriptions.isEmpty()) {
      log.debug("No subscribers of channel `{}` found", channel);
      return;
    }

    for (WebSocketSession subscriber: subscribers) {
      sendTo(subscriber, channel, update);
    }
  }

  /**
   * 특정 사용자에게만 메세지를 보냅니다.
   * */
  private static void sendTo(WebSocketSession subscriber, String channel, String update) {
    try {
      subscriber.sendMessage(WebSocketMessages.channelMessage(channel, update));
      log.debug("RealTimeSession[{}] Send message `{}` to subscriber at channel `{}`",
        subscriber.getId(), update, channel);
    } catch (IOException e) {
      log.error("Failed to send message to subscriber `" + subscriber.getId() +
        "` of channel `" + channel + "`. Message: " + update, e);
    }
  }
}
