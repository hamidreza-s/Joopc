package com.joopc.core.services;

import com.joopc.core.Database;
import com.joopc.core.db.tables.Users;
import com.joopc.core.logics.UserLogic;
import com.joopc.core.proto.AuthReq;
import com.joopc.core.proto.CoreGrpc;
import com.joopc.core.proto.Packet;
import com.joopc.core.services.interceptors.Authenticator;
import io.grpc.Metadata;
import io.grpc.ServerInterceptors;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

import static io.grpc.stub.MetadataUtils.newAttachHeadersInterceptor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class CoreServiceTest {

    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    @BeforeAll
    static void cleanDatabase() throws SQLException {
        Database.getContext().truncate(Users.USERS).execute();
    }

    @Test
    @DisplayName("Test auth service with happy path")
    void authHappyTest() throws Exception {
        var serverName = InProcessServerBuilder.generateName();
        grpcCleanup.register(
                InProcessServerBuilder
                        .forName(serverName)
                        .directExecutor()
                        .addService(ServerInterceptors.intercept(new CoreService(), new Authenticator()))
                        .build().start()
        );

        var blockingStub = CoreGrpc.newBlockingStub(
                grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));

        UserLogic.create("00981111111111").ifPresent(user -> {
            var authReq = AuthReq
                    .newBuilder()
                    .setUsername(user.getUsername())
                    .setPassword(user.getPassword())
                    .build();

            var reply = blockingStub.auth(authReq);

            assertFalse(reply.getToken().isEmpty());
        });
    }

    @Test
    @DisplayName("Test auth service with unhappy path")
    void authUnhappyTest() throws Exception {
        var serverName = InProcessServerBuilder.generateName();
        grpcCleanup.register(
                InProcessServerBuilder
                        .forName(serverName)
                        .directExecutor()
                        .addService(ServerInterceptors.intercept(new CoreService(), new Authenticator()))
                        .build().start()
        );

        var blockingStub = CoreGrpc.newBlockingStub(
                grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));


        var authReq = AuthReq
                .newBuilder()
                .setUsername("NOT-EXISTED-USERNAME")
                .setPassword("NOT-EXISTED-PASSWORD")
                .build();

        try {
            blockingStub.auth(authReq);
        } catch (StatusRuntimeException e) {
            assertEquals(e.getStatus(), Status.NOT_FOUND);
        }
    }

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
        header.put(Metadata.Key.of("JWT-Token", Metadata.ASCII_STRING_MARSHALLER), "JWT-Token-Value");

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
