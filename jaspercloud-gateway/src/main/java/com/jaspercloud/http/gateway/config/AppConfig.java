package com.jaspercloud.http.gateway.config;

import com.google.gson.Gson;
import com.jaspercloud.http.gateway.support.JasperCloudRouteLocator;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class AppConfig {

    @Value("${gateway.connectTimeout}")
    private long connectTimeout;

    @Value("${gateway.writeTimeout}")
    private long writeTimeout;

    @Value("${gateway.readTimeout}")
    private long readTimeout;

    @Bean
    public Gson gson() {
        Gson gson = new Gson();
        return gson;
    }

    @Bean
    public OkHttpClient okHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        builder.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
        builder.readTimeout(readTimeout, TimeUnit.MILLISECONDS);
        OkHttpClient okHttpClient = builder.build();
        return okHttpClient;
    }

    @Bean
    public JasperCloudRouteLocator jasperCloudRouteLocator(ServerProperties server,
                                                    ZuulProperties zuulProperties) {
        return new JasperCloudRouteLocator(server.getServlet().getServletPrefix(), zuulProperties);
    }
}
