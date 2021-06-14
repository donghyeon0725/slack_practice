package com.slack.slack.socket.handlerManager;

import com.slack.slack.socket.handlers.anotation.ChannelHandler;

/**
 * ChannelHandler 의 내부의 필드인 pattern 값을 추출하는 메소드
 * */
public final class ChannelHandlers {

  public static String getPattern(ChannelHandler channelHandler) {
    if (!"".equals(channelHandler.pattern())) {
      return channelHandler.pattern();
    }
    return channelHandler.value();
  }
}
