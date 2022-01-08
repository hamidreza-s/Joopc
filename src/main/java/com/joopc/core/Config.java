package com.joopc.core;

import java.util.List;

public class Config {
    public static List<String> getPrivateCalls() {
        return List.of("com.joopc.core.proto.Core/ping");
    }
}
