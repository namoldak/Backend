package com.example.namoldak.config;

import com.example.namoldak.util.webSocket.SignalHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;

// 기능 : 웹소켓 사용에 필요한 설정
@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocket
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, WebSocketConfigurer {

    private final StompHandler stompHandler;

    // 기본으로 내장되어있는 심플 메세지 브로커를 사용
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub");  // 구독한 것에 대한 경로
        config.setApplicationDestinationPrefixes("/pub");   //
    }

    // 프론트에서 웹소켓 사용시 Stomp 엔드포인트로 연결 (첫 연결)
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    // 소켓 연결 하기 전 유효성 검사하기 위해 만들어 놓은 stompHandler 등록
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(signalHandler(), "/signal")
                .setAllowedOrigins("http://localhost:3000")
                .setAllowedOrigins("https://d34w3p8z4etsgt.cloudfront.net")
                .withSockJS(); // allow all origins
    }

    @Bean
    public WebSocketHandler signalHandler() {
        return new SignalHandler();
    }
}