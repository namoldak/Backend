package com.example.namoldak.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    @Override
    //메세지 브로커 설정
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub"); // 구독한 것에 대한 경로. 이쪽으로 메세지를 보내면 전체적으로 브로드캐스팅
        config.setApplicationDestinationPrefixes("/pub");    // 이 접두어로 접근하는 메시지만 핸들러로 라우팅
    }

    // 1. 프론트에서 연결되는 작업을 해줘야함 /ws-stomp 경로를 실행해야함
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp")           // 클라이언트에서 websocket에 접속하기위한 endpoint를 등록
                .setAllowedOriginPatterns("*")              // CORS 설정
                .withSockJS();                              // 브라우져에서 websocket을 지원하지 않을 경우 fallback 옵션을 활성화 하는데 사용
    }
    // 소켓 연결 하기 전 유효성 검사하기 위해 만들어 놓은 stompHandler 등록
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {   // 클라이언트에서 들어오는 메시지에 사용되는 채널을 구성
        registration.interceptors(stompHandler);                                    // 메시지 채널에 대해 지정된 인터셉터를 구성하여 채널의 인터셉터 목록에 추가 들어오는 메세지를 가로채 유효성 검사
    }
}
