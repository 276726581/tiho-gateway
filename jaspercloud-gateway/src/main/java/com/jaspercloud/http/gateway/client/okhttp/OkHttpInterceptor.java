package com.jaspercloud.http.gateway.client.okhttp;

import com.jaspercloud.http.gateway.client.ProxyPool;
import com.jaspercloud.http.gateway.client.SingleProxyPool;
import com.jaspercloud.http.gateway.util.ZuulConstants;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.InetSocketAddress;

public class OkHttpInterceptor implements Interceptor {

    private ProxyPool proxyPool;

    public OkHttpInterceptor(String host, int port) {
        this.proxyPool = new SingleProxyPool(host, port);
    }

    public OkHttpInterceptor(ProxyPool proxyPool) {
        this.proxyPool = proxyPool;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        HttpUrl url = request.url();
        String scheme = url.scheme();
        String host = url.host();
        int port = url.port();

        InetSocketAddress address = proxyPool.getInetSocketAddress();
        HttpUrl.Builder UrlBuilder = request.url().newBuilder();
        UrlBuilder.scheme("http");
        UrlBuilder.host(address.getHostString());
        UrlBuilder.port(address.getPort());
        HttpUrl httpUrl = UrlBuilder.build();

        Request.Builder builder = request.newBuilder();
        builder.url(httpUrl);
        builder.header(ZuulConstants.GatewayHost, String.format("%s://%s:%d", scheme, host, port));
        Request replaceRequest = builder.build();

        Response response = chain.proceed(replaceRequest);
        return response;
    }
}
