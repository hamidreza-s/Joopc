package com.joopc.core.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.joopc.core.logics.UserLogic;
import com.joopc.core.proto.AuthReq;
import com.joopc.core.proto.AuthRes;
import com.joopc.core.proto.CoreGrpc.CoreImplBase;
import com.joopc.core.proto.Packet;
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
                            try {
                                // TODO: put JWT-SECRET in config file
                                var algorithm = Algorithm.HMAC256("JWT-SECRET");
                                var token = JWT.create().withIssuer(user.getName()).sign(algorithm);
                                AuthRes res = AuthRes.newBuilder()
                                        .setToken(token)
                                        .setTimestamp(Timestamp.from(Instant.now()).toString())
                                        .build();

                                observer.onNext(res);
                                observer.onCompleted();
                            } catch (JWTCreationException ex) {
                                observer.onError(Status.INTERNAL.asException());
                            }
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
