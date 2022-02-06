package com.slack.slack.common.messageBroker;

/**
 * 메세지 리스너
 *
 * 1. 사용할 모듈에서 리스너를 빈으로 등록해주어야 합니다.
 * */
public interface MessageListener {
    void startListening();
}
