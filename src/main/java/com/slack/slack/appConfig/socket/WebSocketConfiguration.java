package com.slack.slack.appConfig.socket;

import com.slack.slack.socket.WebSocketRequestDispatcher;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * 이 설정이 있어야, 소켓의 요청을 받을 수 있다.
 * */
@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private WebSocketRequestDispatcher requestDispatcher;

    private String socketServerUrl = "/rt";

    private String origin = "http://localhost:8080";

    public WebSocketConfiguration(WebSocketRequestDispatcher requestDispatcher) {
        this.requestDispatcher = requestDispatcher;
    }

    /**
     * /rt로 온 요청을, origin 허용을 하고
     * Socket 통신으로 만들고, 해당 요청을 requestDispatcher 클래스에서 처리합니다.
     * */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(requestDispatcher, socketServerUrl).setAllowedOrigins(origin).withSockJS();
    }
}
