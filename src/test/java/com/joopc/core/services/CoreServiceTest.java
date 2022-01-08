package com.joopc.core.services;

import com.joopc.core.Database;
import com.joopc.core.db.tables.Users;
import com.joopc.core.db.tables.records.UsersRecord;
import com.joopc.core.logics.UserLogic;
import com.joopc.core.proto.AuthReq;
import com.joopc.core.proto.CoreGrpc;
import com.joopc.core.proto.CoreGrpc.CoreBlockingStub;
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

import java.sql.Timestamp;
import java.time.Instant;

import static io.grpc.stub.MetadataUtils.newAttachHeadersInterceptor;
import static org.junit.jupiter.api.Assertions.*;

public class CoreServiceTest {

    public static final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
    public static final String serverName = InProcessServerBuilder.generateName();
    public static final CoreBlockingStub blockingStub = CoreGrpc.newBlockingStub(
            grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));

    public static UsersRecord user;

    @BeforeAll
    static void cleanDatabase() throws Exception {
        Database.getContext().truncate(Users.USERS).execute();
        user = UserLogic.create("00981111111111");

        grpcCleanup.register(
                InProcessServerBuilder
                        .forName(serverName)
                        .directExecutor()
                        .addService(ServerInterceptors.intercept(new CoreService(), new Authenticator()))
                        .build().start()
        );
    }

    @Test
    @DisplayName("Test auth service with happy path")
    void authHappyTest() {
        var authReq = AuthReq
                .newBuilder()
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
                .build();

        var reply = blockingStub.auth(authReq);
        assertFalse(reply.getToken().isEmpty());
    }

    @Test
    @DisplayName("Test auth service with unhappy path")
    void authUnhappyTest() {
        var authReq = AuthReq
                .newBuilder()
                .setUsername("NOT-EXISTED-USERNAME")
                .setPassword("NOT-EXISTED-PASSWORD")
                .build();

        var exception = assertThrows(StatusRuntimeException.class, () -> blockingStub.auth(authReq));
        assertEquals(exception.getStatus(), Status.NOT_FOUND);
    }

    @Test
    @DisplayName("Test ping service")
    void pingTest() {
        var header = new Metadata();
        var token = Authenticator.createToken(user);
        header.put(Authenticator.tokenKey, token);

        var packet = Packet
                .newBuilder()
                .setPayload("ping")
                .setTimestamp(Timestamp.from(Instant.now()).toString())
                .build();

        var reply = blockingStub.withInterceptors(newAttachHeadersInterceptor(header)).ping(packet);
        assertEquals("pong", reply.getPayload());
    }

}
