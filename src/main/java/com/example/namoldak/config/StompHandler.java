package com.example.namoldak.config;

import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import com.google.common.util.concurrent.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import java.util.Objects;

// 기능 : Stomp로 메세지 보내기 전 들어오게 되는 핸들러
@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {
    private RateLimiter rateLimiter = RateLimiter.create(2.0);

//    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        //메시지의 페이로드 및 헤더에서 인스턴스를 만듬
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//        System.out.println("accessor = " + accessor);

//        // 연결할때
//        if (StompCommand.CONNECT == accessor.getCommand()) {
//
//            // stomp의 헤더에서 accessToken과 refreshToken 을 뽑음
//            String accessToken = Objects.requireNonNull(accessor.getFirstNativeHeader("Authorization")).substring(7);
//
//            System.out.println("accessToken = " + accessToken);
//        }
        if (rateLimiter.tryAcquire()) {
            log.info("============ 정상 출력됨");
            return message;
        } else {
            return null;
        }
    }
}
