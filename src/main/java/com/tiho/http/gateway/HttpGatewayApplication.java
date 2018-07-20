package com.tiho.http.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulServer;

@EnableZuulServer
@EnableDiscoveryClient
@SpringBootApplication
public class HttpGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(HttpGatewayApplication.class, args);
    }

}
