package com.wenower.core.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.wenower.core.proto.CoreGrpc;
import com.wenower.core.proto.Packet;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import java.sql.Timestamp;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.grpc.testing.GrpcCleanupRule;

public class CoreServiceTest {

  public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  @Test
  @DisplayName("Test ping service")
  void pingTest() throws Exception {
    var serverName = InProcessServerBuilder.generateName();
    grpcCleanup.register(
        InProcessServerBuilder.forName(serverName).directExecutor().addService(new CoreService())
            .build().start());

    var blockingStub = CoreGrpc.newBlockingStub(
        grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));

    var packet = Packet
        .newBuilder()
        .setPayload("ping")
        .setTimestamp(Timestamp.from(Instant.now()).toString())
        .build();

    var reply = blockingStub.ping(packet);

    assertEquals("pong", reply.getPayload());
  }

}
