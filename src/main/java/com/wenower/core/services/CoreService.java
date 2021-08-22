package com.wenower.core.services;

import com.wenower.core.proto.CoreGrpc.CoreImplBase;
import com.wenower.core.proto.Packet;
import io.grpc.stub.StreamObserver;
import java.sql.Timestamp;
import java.time.Instant;

public class CoreService extends CoreImplBase {

  @Override
  public void ping(
      Packet request, StreamObserver<Packet> responseObserver) {

    Packet response = Packet.newBuilder()
        .setPayload("pong")
        .setTimestamp(Timestamp.from(Instant.now()).toString())
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
