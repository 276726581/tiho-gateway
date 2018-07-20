package com.tiho.http.gateway.autoconfig;

import com.tiho.http.gateway.client.ProxyPool;
import com.tiho.http.gateway.client.SingleProxyPool;
import com.tiho.http.gateway.client.feign.OkHttpFeignClient;
import com.tiho.http.gateway.client.okhttp.OkHttpInterceptor;
import feign.Request;
import feign.Retryer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayClientConfig {

    @ConditionalOnMissingBean
    @Bean
    public ProxyPool proxyPool(@Value("${gateway.host}") String host,
                               @Value("${gateway.port}") int port) {
        ProxyPool proxyPool = new SingleProxyPool(host, port);
        return proxyPool;
    }

    @ConditionalOnMissingBean
    @Bean
    public Retryer retryer() {
        Retryer retryer = Retryer.NEVER_RETRY;
        return retryer;
    }

    @ConditionalOnMissingBean
    @Bean
    public Request.Options options(@Value("${gateway.connectTimeout:#{10 * 1000}}") int connectTimeoutMillis,
                                   @Value("${gateway.readTimeout:#{60 * 1000}}") int readTimeoutMillis) {
        Request.Options options = new Request.Options(connectTimeoutMillis, readTimeoutMillis);
        return options;
    }

    @ConditionalOnMissingBean
    @Bean
    public OkHttpInterceptor okHttpInterceptor(ProxyPool proxyPool) {
        OkHttpInterceptor okHttpInterceptor = new OkHttpInterceptor(proxyPool);
        return okHttpInterceptor;
    }

    @ConditionalOnMissingBean
    @Bean
    public OkHttpFeignClient okHttpFeignClient(ProxyPool proxyPool) {
        OkHttpFeignClient okHttpFeignClient = new OkHttpFeignClient(proxyPool);
        return okHttpFeignClient;
    }
}
