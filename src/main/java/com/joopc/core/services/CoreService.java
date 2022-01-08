package com.joopc.core.services;

import com.joopc.core.logics.UserLogic;
import com.joopc.core.proto.AuthReq;
import com.joopc.core.proto.AuthRes;
import com.joopc.core.proto.CoreGrpc.CoreImplBase;
import com.joopc.core.proto.Packet;
import com.joopc.core.services.interceptors.Authenticator;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.sql.Timestamp;
import java.time.Instant;

public class CoreService extends CoreImplBase {

    @Override
    public void auth(AuthReq req, StreamObserver<AuthRes> observer) {
        UserLogic
                .fetch(req.getUsername(), req.getPassword())
                .ifPresentOrElse(user -> {
                            var token = Authenticator.createToken(user);
                            var res = AuthRes.newBuilder()
                                    .setToken(token)
                                    .setTimestamp(Timestamp.from(Instant.now()).toString())
                                    .build();

                            observer.onNext(res);
                            observer.onCompleted();

                        },
                        () -> observer.onError(Status.NOT_FOUND.asException()));
    }

    @Override
    public void ping(Packet req, StreamObserver<Packet> observer) {

        var res = Packet.newBuilder()
                .setPayload("pong")
                .setTimestamp(Timestamp.from(Instant.now()).toString())
                .build();

        observer.onNext(res);
        observer.onCompleted();
    }
}
