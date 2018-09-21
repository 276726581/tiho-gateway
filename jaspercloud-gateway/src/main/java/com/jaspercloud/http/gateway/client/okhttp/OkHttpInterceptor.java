package com.jaspercloud.http.gateway.client.okhttp;

import com.jaspercloud.http.gateway.util.ZuulConstants;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;

public class OkHttpInterceptor implements Interceptor {

    private String gatewayAppName;
    private LoadBalancerClient loadBalancerClient;

    public OkHttpInterceptor(String gatewayAppName, LoadBalancerClient loadBalancerClient) {
        this.gatewayAppName = gatewayAppName;
        this.loadBalancerClient = loadBalancerClient;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl httpUrl = request.url();
        String scheme = httpUrl.scheme();
        String host = httpUrl.host();
        int port = httpUrl.port();
        String url = httpUrl.toString();

        ServiceInstance instance = loadBalancerClient.choose(gatewayAppName);
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);
        String uriString = uriComponentsBuilder.scheme(instance.getScheme())
                .host(instance.getHost())
                .port(instance.getPort())
                .build()
                .toUriString();
        Request.Builder builder = request.newBuilder();
        builder.url(uriString);
        String encodeValue = URLEncoder.encode(String.format("%s://%s:%d", scheme, host, port), "utf-8");
        builder.addHeader(ZuulConstants.GatewayHost, encodeValue);
        Request replace = builder.build();

        Response response = chain.proceed(replace);
        return response;
    }
}
