package com.joopc.core;

import com.joopc.core.services.CoreService;
import com.joopc.core.services.interceptors.Authenticator;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;

import java.io.IOException;
import java.util.logging.Logger;

public class App {

    private static final Logger logger = Logger.getLogger(App.class.getName());
    private static final int PORT = 9090;

    public static void main(String[] args) throws IOException, InterruptedException {
        var server = ServerBuilder
                .forPort(PORT)
                .addService(ServerInterceptors.intercept(new CoreService(), new Authenticator()))
                .build();

        logger.info("Server started, listening on " + PORT);
        server.start();
        server.awaitTermination();
    }
}
