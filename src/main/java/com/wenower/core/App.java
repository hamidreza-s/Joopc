package com.wenower.core;

import com.wenower.core.services.CoreService;
import io.grpc.ServerBuilder;
import java.io.IOException;

public class App {

  public static void main(String[] args) throws IOException, InterruptedException {
    var server = ServerBuilder
        .forPort(8080)
        .addService(new CoreService()).build();

    System.out.println("GRPC server started on port 8080 ...");
    server.start();
    server.awaitTermination();
  }
}
