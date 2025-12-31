package com.sukhoi.gateway.filter;

import com.sukhoi.gateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class JwtFilter implements GlobalFilter, Ordered {

    private  final JwtUtil jwtUtil;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {


        String path = exchange.getRequest().getURI().getPath();

        if(path.contains("/auth/")){
            return chain.filter(exchange);
        }

        HttpCookie cookie = exchange.getRequest().getCookies().getFirst("access_token");

        if(cookie != null && !cookie.getValue().isEmpty()){
            String accessToken = cookie.getValue();
            int id = jwtUtil.validateToken(accessToken);

            if(id == 0){
                return chain.filter(exchange);
            }

            ServerHttpRequest request = exchange.getRequest().mutate().header("X-USER-ID", String.valueOf(id)).build();

            return chain.filter(exchange.mutate().request(request).build());
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
