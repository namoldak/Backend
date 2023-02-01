package com.example.namoldak.config;

import com.example.namoldak.util.webSocket.SignalHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        log.info(">>>>>>>[ws] 웹소켓 연결 : {}", registry);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub");  // 구독한 것에 대한 경로
        config.setApplicationDestinationPrefixes("/pub");   //
        log.info(">>>>>>>[ws] 메시지 브로커 : {}", config);
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








//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, WebSocketConfigurer {
//
//    private final StompHandler stompHandler;
//
//    // 기본으로 내장되어있는 심플 메세지 브로커를 사용
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.enableSimpleBroker("/sub");  // 구독한 것에 대한 경로
//        config.setApplicationDestinationPrefixes("/pub");   //
//    }
//
//    // 프론트에서 웹소켓 사용시 Stomp 엔드포인트로 연결 (첫 연결)
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws-stomp")
//                .setAllowedOrigins("http://localhost:3000")
//                .setAllowedOrigins("https://namoldak.com")
//                .setAllowedOriginPatterns("*")
//                .withSockJS()
//                .setHeartbeatTime(1000);
//    }
//
//    // 소켓 연결 하기 전 유효성 검사하기 위해 만들어 놓은 stompHandler 등록
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(stompHandler);
//    }
//
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(signalHandler(), "/signal")
//                .setAllowedOrigins("*")
//                .setAllowedOrigins("http://localhost:3000", "https://namoldak.com")
//                .withSockJS(); // allow all origins
//    }
//
//    @Bean
//    public WebSocketHandler signalHandler() {
//        return new SignalHandler();
//    }
//
//    @Override
//    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
//        registration.setMessageSizeLimit(160 * 64 * 1024); // default : 64 * 1024
//        registration.setSendTimeLimit(100 * 10000); // default : 10 * 10000
//        registration.setSendBufferSizeLimit(3* 512 * 1024); // default : 512 * 1024
//    }
//
//
//}