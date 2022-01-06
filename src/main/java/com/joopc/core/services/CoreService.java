package com.joopc.core.services;

import com.joopc.core.proto.AuthReq;
import com.joopc.core.proto.AuthRes;
import com.joopc.core.proto.CoreGrpc.CoreImplBase;
import com.joopc.core.proto.Packet;
import io.grpc.stub.StreamObserver;
import java.sql.Timestamp;
import java.time.Instant;

public class CoreService extends CoreImplBase {

  @Override
  public void auth(AuthReq req, StreamObserver<AuthRes> observer) {
    // TODO: check username and password, if valid return JWT
    AuthRes res = AuthRes.newBuilder()
            .setToken("JWT-Token-Value")
            .setTimestamp(Timestamp.from(Instant.now()).toString())
            .build();

    observer.onNext(res);
    observer.onCompleted();
  }

  @Override
  public void ping(Packet req, StreamObserver<Packet> observer) {

    Packet res = Packet.newBuilder()
        .setPayload("pong")
        .setTimestamp(Timestamp.from(Instant.now()).toString())
        .build();

    observer.onNext(res);
    observer.onCompleted();
  }
}
