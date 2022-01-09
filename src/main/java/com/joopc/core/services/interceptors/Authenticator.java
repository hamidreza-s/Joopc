package com.joopc.core.services.interceptors;

import com.joopc.core.Config;
import com.joopc.core.db.tables.records.UsersRecord;
import io.grpc.*;
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Logger;


public class Authenticator implements ServerInterceptor {

    private static final Logger logger = Logger.getLogger(Authenticator.class.getName());
    private static final Key jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    public static final Context.Key<Object> contextTokenKey = Context.key("CONTEXT-TOKEN-KEY");
    public static final Metadata.Key<String> headerTokenKey = Metadata.Key.of("HEADER-TOKEN-KEY", Metadata.ASCII_STRING_MARSHALLER);

    public static String createToken(UsersRecord user) {
        var now = new Date();
        var tomorrow = Date.from(now.toInstant().plusSeconds(24 * 60 * 60));
        return Jwts
                .builder()
                .setSubject(user.getUsername())
                .setId(user.getId())
                .setIssuedAt(now)
                .setExpiration(tomorrow)
                .signWith(jwtSecretKey)
                .compact();
    }

    public static Optional<Jws<Claims>> parseToken(String token) {
        try {
            return Optional.of(Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(token));
        } catch (JwtException e) {
            logger.info("wrong client token: " + e.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<Claims> getClaims() {
        var claims = (Claims) contextTokenKey.get();
        return Optional.ofNullable(claims);
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            final Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        if (Config.getPrivateCalls().contains(call.getMethodDescriptor().getFullMethodName())) {
            var encryptedToken = Optional.ofNullable(headers.get(headerTokenKey));
            var parsedToken = encryptedToken.flatMap(Authenticator::parseToken);
            if (parsedToken.isEmpty()) {
                call.close(Status.UNAUTHENTICATED, headers);
                return new ServerCall.Listener<>() {
                };
            } else {
                var context = Context
                        .current()
                        .withValue(contextTokenKey, parsedToken.get().getBody());
                return Contexts.interceptCall(context, call, headers, next);
            }
        }

        return next.startCall(new SimpleForwardingServerCall<>(call) {
        }, headers);
    }
}
