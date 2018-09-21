package com.jaspercloud.http.gateway.autoconfig;

import com.jaspercloud.http.gateway.client.feign.OkHttpFeignClient;
import com.jaspercloud.http.gateway.client.okhttp.OkHttpInterceptor;
import feign.Request;
import feign.Retryer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDiscoveryClient
public class GatewayClientConfig {

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
    public OkHttpInterceptor okHttpInterceptor(@Value("${gateway.appName}") String gatewayAppName,
                                               LoadBalancerClient loadBalancerClient) {
        OkHttpInterceptor okHttpInterceptor = new OkHttpInterceptor(gatewayAppName, loadBalancerClient);
        return okHttpInterceptor;
    }

    @ConditionalOnMissingBean
    @Bean
    public OkHttpFeignClient okHttpFeignClient(@Value("${gateway.appName}") String gatewayAppName,
                                               LoadBalancerClient loadBalancerClient) {
        OkHttpFeignClient okHttpFeignClient = new OkHttpFeignClient(gatewayAppName, loadBalancerClient);
        return okHttpFeignClient;
    }
}
