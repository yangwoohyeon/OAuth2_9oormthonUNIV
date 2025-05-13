package com.example.OAuth2_9oormthonUNIV.global.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServletServerHttpRequest;
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
        // ① 쿼리 파라미터에서 token 가져오기 (ws://localhost:8080/ws/conn?token=xxx)
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String token = servletRequest.getServletRequest().getParameter("token");
            if (token != null && !token.isEmpty()) {
                attributes.put("token", token);
                return true;
            }
        }

        // ② subprotocol에서 token 꺼내기 (access_token,xxx 형식)
        List<String> protocols = request.getHeaders().get("Sec-WebSocket-Protocol");
        if (protocols != null && !protocols.isEmpty()) {
            String protocolHeader = protocols.get(0); // ex: "access_token,eyJ..."
            if (protocolHeader.startsWith("access_token,")) {
                String token = protocolHeader.split(",")[1];
                attributes.put("token", token);
                // 응답에 subprotocol 설정
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
