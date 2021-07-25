package com.webflux.thiago.webfluxdevdojo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;

@SpringBootApplication
public class WebfluxDevdojoApplication {

    static{
        BlockHound.install(builder -> builder
                .allowBlockingCallsInside("java.util.UUID", "randomUUID")
        .allowBlockingCallsInside("java.io.InputStream", "readNBytes")
        .allowBlockingCallsInside("java.io.FilterInputStream", "read")
        .allowBlockingCallsInside("java.io.InputStreamReader", "read"));
    }
    public static void main(String[] args) {
        SpringApplication.run(WebfluxDevdojoApplication.class, args);
    }

}
