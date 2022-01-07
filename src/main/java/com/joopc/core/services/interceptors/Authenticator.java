package com.joopc.core.services.interceptors;

import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

import java.util.List;
import java.util.logging.Logger;


public class Authenticator implements ServerInterceptor {

    private static final Logger logger = Logger.getLogger(Authenticator.class.getName());
    private static final List<String> publicCalls = List.of("com.joopc.core.proto.Core/auth");
    private static final List<String> privateCalls = List.of("com.joopc.core.proto.Core/ping");

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            final Metadata requestHeaders,
            ServerCallHandler<ReqT, RespT> next) {
        // TODO: check JWT token for privateCalls
        logger.info("header received from client:" + requestHeaders);
        return next.startCall(new SimpleForwardingServerCall<>(call) {
        }, requestHeaders);
    }
}
