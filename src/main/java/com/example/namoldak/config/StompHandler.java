package com.example.namoldak.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    @Override
    // preSend 메시지가 실제로 패널로 전송되기 전에 호출, 이 메서드가 null을 반환하면 전송 호출이 발생하지 않는다.
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        //메시지의 페이로드 및 헤더에서 인스턴스를 만듬
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        System.out.println("accessor = " + accessor);

        // 연결할때
        if (StompCommand.CONNECT == accessor.getCommand()) {

            // stomp의 헤더에서 accessToken과 refreshToken 을 뽑음
            // requireNonNull : Objects 클래스에서 제공하는 널(Null) 체크를 위한 메소드

            String accessToken = Objects.requireNonNull(accessor.getFirstNativeHeader("Authorization")).substring(7); // 토큰 가져오는데 앞에 Bearer과 공백을 제거하고

            System.out.println("accessToken = " + accessToken);
        }
        return message; // 유효성 검증된 메세지가 반환
    }
}
