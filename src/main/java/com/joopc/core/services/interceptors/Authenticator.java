package com.joopc.core.services.interceptors;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.joopc.core.Config;
import com.joopc.core.db.tables.records.UsersRecord;
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;


public class Authenticator implements ServerInterceptor {

    private static final Logger logger = Logger.getLogger(Authenticator.class.getName());
    private static final List<String> publicCalls = List.of("com.joopc.core.proto.Core/auth");
    private static final List<String> privateCalls = List.of("com.joopc.core.proto.Core/ping");
    private static final Algorithm algorithm = Algorithm.HMAC256(Config.getJwtSecret());
    public static final Metadata.Key<String> key = Metadata.Key.of("JWT-Token", Metadata.ASCII_STRING_MARSHALLER);

    public static String createToken(UsersRecord user) {
        // TODO: define the right payload
        // TODO: user JJWT instead
        try {
            var payload = Map.of(
                    "foo", "bar",
                    "bat", "ban"
            );
            return JWT.create().withPayload(payload).sign(algorithm);
        } catch (JWTCreationException ex) {
            return ex.getMessage();
        }
    }

    public static Optional<DecodedJWT> verifyToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            return Optional.of(verifier.verify(token));
        } catch (JWTVerificationException ex) {
            return Optional.empty();
        }
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            final Metadata requestHeaders,
            ServerCallHandler<ReqT, RespT> next) {
        Optional.ofNullable(requestHeaders.get(key)).ifPresent(t -> {
            logger.info("client token: " + t);
            verifyToken(t).ifPresent(decodedJWT -> {
                logger.info("client payload: " + decodedJWT.getPayload());
            });
        });
        return next.startCall(new SimpleForwardingServerCall<>(call) {
        }, requestHeaders);
    }
}
