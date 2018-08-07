package com.jaspercloud.http.gateway.autoconfig;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(GatewayClientConfig.class)
public @interface EnableGatewayClient {
}
