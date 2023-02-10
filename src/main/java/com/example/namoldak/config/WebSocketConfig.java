package com.example.namoldak.config;

import com.example.namoldak.util.webSocket.SignalHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;

// 기능 : 웹소켓 사용에 필요한 설정
@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocket
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, WebSocketConfigurer {

    // 웹 소켓 연결을 위한 엔드포인트 설정 및 stomp sub/pub 엔드포인트 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // stomp 접속 주소 url => /ws/chat
        registry.addEndpoint("/ws-stomp")      // 연결될 Endpoint
                .setAllowedOriginPatterns("*")  // CORS 설정
                .withSockJS()                   // SockJS 설정
                .setHeartbeatTime(1000);        // 연결상태 확인 주기
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub");  // 구독한 것에 대한 경로
        config.setApplicationDestinationPrefixes("/pub");   //
    }

    // 웹 소켓 버퍼 사이즈 증축
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(160 * 64 * 1024); // default : 64 * 1024
        registration.setSendTimeLimit(100 * 10000); // default : 10 * 10000
        registration.setSendBufferSizeLimit(3* 512 * 1024); // default : 512 * 1024
    }

    @Bean
    public WebSocketHandler signalHandler() {
        return new SignalHandler();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(signalHandler(), "/signal")
                .setAllowedOrigins("*")
                .setAllowedOrigins("http://localhost:3000", "https://namoldak.com")
                .withSockJS(); // allow all origins
    }
}