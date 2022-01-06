package com.joopc.core.services;

import static io.grpc.stub.MetadataUtils.newAttachHeadersInterceptor;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.joopc.core.services.interceptors.Authenticator;
import com.joopc.core.proto.CoreGrpc;
import com.joopc.core.proto.Packet;
import io.grpc.Metadata;
import io.grpc.ServerInterceptors;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import java.sql.Timestamp;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CoreServiceTest {

  public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  @Test
  @DisplayName("Test ping service")
  void pingTest() throws Exception {
    var serverName = InProcessServerBuilder.generateName();
    grpcCleanup.register(
        InProcessServerBuilder
            .forName(serverName)
            .directExecutor()
            .addService(ServerInterceptors.intercept(new CoreService(), new Authenticator()))
            .build().start()
    );

    var header = new Metadata();
    header.put(Metadata.Key.of("JWT-Token", Metadata.ASCII_STRING_MARSHALLER), "todo-token-content");

    var blockingStub = CoreGrpc.newBlockingStub(
        grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));

    var packet = Packet
        .newBuilder()
        .setPayload("ping")
        .setTimestamp(Timestamp.from(Instant.now()).toString())
        .build();

    var reply = blockingStub.withInterceptors(newAttachHeadersInterceptor(header)).ping(packet);

    assertEquals("pong", reply.getPayload());
  }

}
