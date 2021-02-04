package com.plee.auth.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "database")
@Getter
@Setter
public class DatabaseProperties {

    @Getter
    @Setter
    public static class Server {

        private String ip;
        private int port;

    }

    private String username;
    private String password;
    private Server server;

}