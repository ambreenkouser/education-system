package com.edumanage.apigateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final ReactiveStringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(ctx -> {
                    if (ctx.getAuthentication() == null
                            || !(ctx.getAuthentication().getPrincipal() instanceof Jwt jwt)) {
                        return chain.filter(exchange);
                    }

                    String jti    = jwt.getId();
                    String userId = jwt.getSubject();
                    String role   = jwt.getClaimAsString("role");  // Critical fix: was broken syntax

                    // Reject tokens missing required claims
                    if (jti == null || userId == null || role == null) {
                        log.warn("JWT missing required claims: jti={} sub={} role={}", jti, userId, role);
                        return unauthorized(exchange);
                    }

                    // Check Redis revocation list — set by auth-service on logout
                    return redisTemplate.hasKey("revoked:" + jti)
                            .flatMap(revoked -> {
                                if (Boolean.TRUE.equals(revoked)) {
                                    log.warn("Rejected revoked token jti={}", jti);
                                    return unauthorized(exchange);
                                }

                                ServerHttpRequest mutated = exchange.getRequest().mutate()
                                        .header("X-User-Id",   userId)
                                        .header("X-User-Role", role)
                                        .build();
                                return chain.filter(exchange.mutate().request(mutated).build());
                            });
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
