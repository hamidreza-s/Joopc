package com.joopc.core.services.interceptors;

import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import java.util.logging.Logger;


public class Authenticator implements ServerInterceptor {

  private static final Logger logger = Logger.getLogger(Authenticator.class.getName());

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
      ServerCall<ReqT, RespT> call,
      final Metadata requestHeaders,
      ServerCallHandler<ReqT, RespT> next) {
    logger.info("header received from client:" + requestHeaders);
    return next.startCall(new SimpleForwardingServerCall<>(call) {}, requestHeaders);
  }
}
