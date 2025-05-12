package com.example.OAuth2_9oormthonUNIV.global.security;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;


import java.util.List;
import java.util.Map;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {

        List<String> protocols = request.getHeaders().get("Sec-WebSocket-Protocol");
        if (protocols != null && !protocols.isEmpty()) {
            String protocolHeader = protocols.get(0); // ex: access_token,JWT
            if (protocolHeader.startsWith("access_token,")) {
                String token = protocolHeader.split(",")[1];
                attributes.put("token", token);

                // response에 SubProtocol 지정 (핸드셰이크 성공 보장)
                response.getHeaders().set("Sec-WebSocket-Protocol", "access_token");
            }
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 생략 가능
    }
}
