package com.wenower.core;

import com.wenower.core.proto.HelloRequest;
import com.wenower.core.proto.HelloResponse;
import com.wenower.core.proto.HelloServiceGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;

public class App {

  public static class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {

    @Override
    public void hello(
        HelloRequest request, StreamObserver<HelloResponse> responseObserver) {

      var greeting = new StringBuilder()
          .append("Hello, ")
          .append(request.getFirstName())
          .append(" ")
          .append(request.getLastName())
          .toString();

      HelloResponse response = HelloResponse.newBuilder()
          .setGreeting(greeting)
          .build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    Server server = ServerBuilder
        .forPort(8080)
        .addService(new HelloServiceImpl()).build();

    System.out.println("GRPC server started on port 8080 ...");
    server.start();
    server.awaitTermination();
  }
}
