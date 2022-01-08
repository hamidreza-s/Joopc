package com.joopc.core.services.interceptors;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

import com.joopc.core.db.tables.records.UsersRecord;
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


public class Authenticator implements ServerInterceptor {

    private static final Logger logger = Logger.getLogger(Authenticator.class.getName());
    private static final List<String> publicCalls = List.of("com.joopc.core.proto.Core/auth");
    private static final List<String> privateCalls = List.of("com.joopc.core.proto.Core/ping");
    private static final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    public static final Metadata.Key<String> tokenKey = Metadata.Key.of("JWT-Token", Metadata.ASCII_STRING_MARSHALLER);

    public static String createToken(UsersRecord user) {
        var now = new Date();
        var tomorrow = Date.from(now.toInstant().plusSeconds(24 * 60 * 60));
        return Jwts
                .builder()
                .setSubject(user.getUsername())
                .setId(user.getId())
                .setIssuedAt(now)
                .setExpiration(tomorrow)
                .signWith(secretKey)
                .compact();
    }

    public static Optional<Jws<Claims>> parseToken(String token) {
        try {
            return Optional.of(Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token));
        } catch (JwtException e) {
            logger.info("wrong client token: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            final Metadata requestHeaders,
            ServerCallHandler<ReqT, RespT> next) {
        Optional.ofNullable(requestHeaders.get(tokenKey)).ifPresent(t -> {
            logger.info("client token: " + t);
            logger.info("parse token: " + parseToken(t));
        });
        return next.startCall(new SimpleForwardingServerCall<>(call) {
        }, requestHeaders);
    }
}
